
/**
 * Propeller parametric simulation
 *
 * @author Andrew Gunderson
 *
 * 2017, v11.06
 */
import com.opencsv.CSVReader;
import java.io.*;
import star.common.*;
import macroutils.*;
import java.util.*;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import star.base.neo.DoubleVector;
import star.flow.*;
import star.meshing.*;
import star.vof.*;
import star.motion.*;
import star.vis.Displayer;

public class Gearcase extends StarMacro {

    //--------------------------------------------------------------------------
    // -- USER INPUTS --
    //--------------------------------------------------------------------------
    // NOTE: be sure to go through this script and check that all the strings
    //       match the object names inside your simulation file (i.e. c-sys,
    //       boundary names, mesh operation names, etc.)
    boolean linux = true;
    int version = 0;
    String versionFileHeader = "Stingray_GC_v" + version;

    String[] headers = {"Revision",
        "Speed (mph)",
        "Trim (deg)",
        "Height (in.)",
        "Lift (lbf)",
        "Gearcase Drag (lbf",
        "Gearcase Lift (lbf)",
        "Gearcase Sideforce (lbf)",
        "Gearcase Pitch Moment (lbf-ft)",
        "Gearcase Roll Moment (lbf-ft)",
        "Gearcase Yaw Moment (lbf-ft)"
    };

    // Simulation parameters
    TODO
    : fill out run matrix from TR wiki
    double runMatrix[][] = {
        {40, 3.5, 8, 0},
        {50, 3.5, 8, 0}
    };
    double meshSize = 0.1; // smallest mesh size on gearcase surface
    double runPoints = 18; // # total rows in run matrix
    double mfr = .6; // exhaust mass flow rate (kg/s)
    TODO
    : check trim point with john
    double trimPoint_z = 36.37; // z distance from trim point to GC center (in)
    double trimPoint_x = 8.07; // x distance from trim point to GC center (in)
    double xProp = 19;
    int numGcReports = 6; // number of gc reports being exported to csv

    //--------------------------------------------------------------------------
    // -- END USER INPUTS --
    //--------------------------------------------------------------------------
    public void execute() {
        try {
            initMacro();
            for (int i = 0; i < runPoints; i++) {
                setSpeed(runMatrix[i][0]);
                setHeight(runMatrix[i][2]);
                setTrim(runMatrix[i][1]);
                setCsys(runMatrix[i][2], runMatrix[i][1]);
                TODO: update vars with runMatrix like above
                ud.simTitle = versionFileHeader + "_"
                        + speed + "mph_"
                        + trim + "deg_"
                        + height + "in_"
                        + rpm + "rpm";
                fileName = ud.simPath + slash + ud.simTitle;
                run(speed, height, trim, rpm);
                exportScene();
                CreateResultSS(speed, height, trim, rpm);

            }
        } catch (Exception ex) {
            mu.getSimulation().println(ex);
        }
    }

    void initMacro() {
        mu = new MacroUtils(getActiveSimulation(), intrusive);
        ud = mu.userDeclarations;
        ud.defColormap = mu.get.objects.colormap(
                StaticDeclarations.Colormaps.BLUE_RED);
        if (linux) {
            slash = "/";
        } else {
            slash = "\\";
        }
    }

    void setSpeed(double speed) {
        // set wave speed
        ud.physCont = mu.get.objects.physicsContinua(".*", vo);
        vwm
                = ud.physCont.getModelManager().getModel(VofWaveModel.class
                );
        fvw = (FlatVofWave) vwm.getVofWaveManager().getObject("FlatVofWave 1");
        fvw.getCurrent().setComponents(speed, 0, 0);
        fvw.getWind().setComponents(speed, 0, 0);

        // set pressure coeff ref velocity
        ud.ff = mu.get.objects.fieldFunction(
                StaticDeclarations.Vars.PC.getVar(), vo);
        pcf = (PressureCoefficientFunction) ud.ff;
        pcf.getReferenceVelocity().setValue(speed);

        // set time step
        tStep = meshSize / speed / 17.6; // may be too conservative
        mu.set.solver.timestep(tStep);

        // set exhaust flow
        ud.bdry = mu.get.boundaries.byREGEX("Inlet_Exhaust", true);
        mu.set.boundary.values(ud.bdry,
                StaticDeclarations.Vars.MFR, mfr, ud.unit_kgps);

    }

    void setHeight(double height) {
        // set heave value in parts translate operation
        tpo = (TransformPartsOperation) mu.getSimulation()
                .get(MeshOperationManager.class
                ).getObject("Translate");
        tc = (TranslationControl) tpo.getTransforms().getObject("Heave");
        tc.getTranslationVector().setCoordinate(
                ud.unit_in, ud.unit_in, ud.unit_in,
                new DoubleVector(new double[]{0.0, 0.0, -height}));

    }

    void setTrim(double trim) {
        // set trim angle in parts rotate operation
        tpo = (TransformPartsOperation) mu.getSimulation()
                .get(MeshOperationManager.class
                ).getObject("Rotate");
        rc = (RotationControl) tpo.getTransforms().getObject("Pitch");
        rc.getAngle().setValue(trim);

        // Move outer refinement zone to follow motion due to trim
        tpo
                = (TransformPartsOperation) mu.getSimulation()
                        .get(MeshOperationManager.class
                        )
                        .getObject("Translate_Refine_Outer");
        tc = (TranslationControl) tpo.getTransforms().getObject("Translate");
        tc.getTranslationVector().setCoordinate(ud.unit_in, ud.unit_in, ud.unit_in,
                new DoubleVector(new double[]{
            trimPoint_z * Math.sin(trim * Math.PI / 180), 0.0,
            (trimPoint_x + xProp) * Math.sin(trim * Math.PI / 180)}));

        // Clear solution history and fields
        mu.clear.solution();
        // Execute all mesh operations
        mu.update.volumeMesh();
    }

    void setCsys(double height, double trim) {

        // -- Trim Center --
        trimCenter = (CartesianCoordinateSystem) ud.lab0
                .getLocalCoordinateSystemManager().getObject("Trim_Center");
        trimCenter.getOrigin().setCoordinate(ud.unit_in, ud.unit_in, ud.unit_in,
                new DoubleVector(new double[]{
            -trimPoint_x, 0.0, trimPoint_z + -height}));
        // set orientation
        trimCenter.setBasis0(new DoubleVector(new double[]{
            Math.cos(trim * Math.PI / 180), 0.0,
            Math.sin(trim * Math.PI / 180)}));

        // -- GC Center --
        gcCenter = (CartesianCoordinateSystem) trimCenter
                .getLocalCoordinateSystemManager().getObject("GC_Center");
        gcCenter.getOrigin().setCoordinate(ud.unit_in, ud.unit_in, ud.unit_in,
                new DoubleVector(new double[]{
            trimPoint_x, 0.0, -trimPoint_z}));

    }

    void run(double speed, double height, double trim, double rpm) {
        // set volume mesh repr for all displayers
        for (Displayer d : mu.get.scenes.allDisplayers(vo)) {
            d.setRepresentation(mu.get.mesh.fvr());
        }

        // disable autosave
        mu.getSimulation().getSimulationIterator()
                .getAutoSave().getStarUpdate().setEnabled(false);

        // run
        mu.step(numSteps);

        // output csv data
        MonitorPlot gcPlot = (MonitorPlot) mu.get.plots.byREGEX("Gearcase", vo);
        gcPlot.export(fileName + "_gc.csv", ",");

        mu.saveSim();
    }

    void exportScene() {
        // export pressure coeff 3d scene
        ud.scene = mu.get.scenes.byREGEX("Scalar Scene", vo);
        ud.scene.export3DSceneFileAndWait(
                fileName + ".sce", ud.simTitle,
                "Pressure Coefficient", false, false);

        // write prop plot as picture (doesn't work with software rendering)
        mu.io.write.picture(mu.get.plots.byREGEX("Prop", vo),
                ud.simTitle, ud.picResX, ud.picResY, vo);

        // clear solution history
        mu.clear.solutionHistory();
    }
    
    void CreateGcSS(double speed, double height, double trim, double rpm)
            throws Exception {

        // create results spreadsheet if not already created
        ssTitle = ud.simPath + slash + versionFileHeader + "_Gearcase.xls";
        if (!new File(ssTitle).exists()) {
            initSpreadsheet();
        }
        // open existing results spreadsheet and create new row
        wb = WorkbookFactory.create(new File(ssTitle));
        sheet = wb.getSheet("Data");
        int currentRow = sheet.getLastRowNum() + 1;
        row = sheet.createRow(currentRow);
        row.createCell(0).setCellValue(versionFileHeader);
        row.createCell(1).setCellValue(speed);
        row.createCell(2).setCellValue(trim);
        row.createCell(3).setCellValue(height);
        row.createCell(4).setCellValue(rpm);

        // read in gearcase data
        reader = new CSVReader(new FileReader(fileName + "_gc.csv"));
        data = reader.readAll();

        // Compute mean and standard deviation of gc data
        int reportIterator = 1;
        stats = new SummaryStatistics();
        for (columnIterator = 5;
                columnIterator < 5 + numGcReports; columnIterator++) {
            for (rowIterator = data.size() - 1;
                    rowIterator >= data.size() - ud.numToAve; rowIterator--) {
                String[] array = data.get(rowIterator);
                stats.addValue(Double.parseDouble(array[reportIterator]));
            }
            row.createCell(columnIterator).setCellValue(stats.getMean());
            stats = new SummaryStatistics();
            reportIterator++;
        }

        // save spreadsheet
        mu.io.say.action("Updating Gearcase Results SS", vo);
        fileOut = new FileOutputStream(ssTitle);
        wb.write(fileOut);
        fileOut.close();
        mu.io.say.ok(vo);

    }

    void initSpreadsheet() throws Exception {
        // Create prop excel workbook with headers
        wb = new HSSFWorkbook();
        row = wb.createSheet("Data").createRow(0);
        for (int i = 0; i < headers.length; i++) {
            row.createCell(i).setCellValue(headers[i]);
        }
        fileOut = new FileOutputStream(ssTitle);
        wb.write(fileOut);
        fileOut.close();
    }

    MacroUtils mu;
    UserDeclarations ud;
    boolean vo = true;
    boolean intrusive = true;

    int numSteps;
    int columnIterator;
    int rowIterator;
    int meshCount;

    FileOutputStream fileOut;
    Workbook wb;
    Sheet sheet;
    Row row;
    CSVReader reader;
    List<String[]> data;
    SummaryStatistics stats;
    VofWaveModel vwm;
    FlatVofWave fvw;
    TransformPartsOperation tpo;
    PressureCoefficientFunction pcf;
    RotationControl rc;
    TranslationControl tc;
    CartesianCoordinateSystem trimCenter;
    CartesianCoordinateSystem gcCenter;
    CartesianCoordinateSystem propCenter;
    RotatingMotion rm;
    String fileName;
    String slash;
    String ssTitle;
    double tStep;
    double dProp;
    double[] subAreaRatio;

}
