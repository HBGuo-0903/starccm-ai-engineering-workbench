
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

public class Props_TR2017_0404_008 extends StarMacro {

    //--------------------------------------------------------------------------
    // -- USER INPUTS --
    //--------------------------------------------------------------------------
    // NOTE: be sure to go through this script and check that all the strings
    //       match the object names inside your simulation file (i.e. c-sys,
    //       boundary names, mesh operation names, etc.)
    boolean linux = true;
    int version = 7;
    String versionFileHeader = "BIII_28P";
    // submerged are ratios {front, front, front, rear, rear, rear}
    double[][] subAreaRatios = {
        {1., .92, .73, 1., .92, .66},
        {1., .92, .73, 1., .92, .66},
        {1., .92, .73, 1., .92, .66},
        {1., .92, .73, 1., .92, .66},
        {1., .92, .73, 1., .92, .66},
        {1., .92, .73, 1., .92, .66},
        {1., .92, .73, 1., .92, .66},
    };
    // prop diameter {front, rear} inches
    double[][] dProps = {
        {16.63, 15.46, 16.},
        {16.69, 15.52, 16.},
        {16.60, 15.52, 16.},
        {16.67, 15.62, 16.},
        {17.61, 16.34, 16.99},
        {18.62, 17.23, 17.94},
        {16.61, 15.44, 16.04}
    };
    // distance from prop center to GC center {front, rear} inches
    double[][] xProps = {
        {18.62, 25.47},
        {18.62, 25.47},
        {18.62, 25.47},
        {18.70, 25.52},
        {18.77, 25.55},
        {18.79, 25.63},
        {18.69, 25.43}
    };
    //--------------------------------------------------------------------------
    // -- END USER INPUTS --
    //--------------------------------------------------------------------------

    String[] propHeaders = {"Revision",
        "Speed (mph)",
        "Trim (deg)",
        "Height (in.)",
        "RPM",
        "Prop Lift (lbf)",
        "Prop Sideforce (lbf)",
        "Prop Thrust Net (lbf)",
        "Prop Normal (lbf)",
        "Prop Pitch Moment (lbf-ft)",
        "Prop Yaw Moment (lbf-ft)",
        "Prop Thrust",
        "Mean Blade Thrust (lbf)",
        "Max Blade Thrust (lbf)",
        "Min Blade Thrust (lbf)",
        "Prop Torque (lbf-ft)",
        "Mean Blade Torque (lbf-ft)",
        "Max Blade Torque",
        "Min Blade Torque",
        "SHP",
        "J",
        "KT_norm",
        "KQ_norm",
        "eta",};

    String[] combinedPropHeaders = {"Revision",
        "Speed (mph)",
        "Trim (deg)",
        "Height (in.)",
        "RPM",
        "Prop Thrust Net (lbf)",
        "Prop Torque (lbf-ft)",
        "SHP",
        "J",
        "KT",
        "KQ",
        "eta",};

    String[] gcHeaders = {"Revision",
        "Speed (mph)",
        "Trim (deg)",
        "Height (in.)",
        "RPM",
        "Gearcase Drag (lbf",
        "Gearcase Lift (lbf)",
        "Gearcase Sideforce (lbf)",
        "Gearcase Pitch Moment (lbf-ft)",
        "Gearcase Roll Moment (lbf-ft)",
        "Gearcase Yaw Moment (lbf-ft)"
    };

// Simulation parameters
    double[] speeds = {60.}; // mph
    double[] trims = {-7., 3.5, 8.5}; // deg, positive is trim out
    double[] heights = {8.}; // // level trim propshaft depth below water (in.)
    double[] rpms = {2000., 2400., 2800.};
    double mfr = .6; // exhaust mass flow rate (kg/s)
    double stepSize = 1.; // degrees per timestep 
    double revs_init = 4; // number of prop revolutions for initial rpm setting
    double revs = 2; // number of prop revolutions for subsequent rpms
    double trimPoint_z = 44.37; // z distance from trim point to GC center (in)
    double trimPoint_x = 8.07; // x distance from trim point to GC center (in)
    int numPropReports = 10; // number of reports being exported to csv file
    int numTitleCol = 5; // number of columns containing run condition info (speed, trim, etc)
    int numPropCol = numPropReports + numTitleCol + 4;
    int numGcReports = 6; // number of gc reports being exported to csv

    public void execute() {
        try {
            initMacro();

            // -- SET SPEED --
            for (double speed : speeds) {
                setSpeed(speed);
                meshCount = -1;

                // -- SET HEIGHT --
                for (double height : heights) {
                    setHeight(height);

                    // -- SET TRIM --
                    //for (double trim : trims) {
                    double trim = 0.;
                    if (speed == 15.) {
                        trim = -7.;
                    } else if (speed == 40.) {
                        trim = 3.5;
                    } else if (speed == 60.) {
                        trim = 8.5;
                    } else {
                        mu.getSimulation().println("*ERROR: CHECK TRIM INPUT");
                    }
                    setTrim(trim);
                    meshCount++; // increment number of meshes created

                    setCsys(height, trim);
                    // -- SET RPM --
                    if (speed == 15.) {
                        rpms = new double[]{1600., 2000., 2800.};
                    } else if (speed == 40.) {
                        rpms = new double[]{1300., 1500., 2000., 2200.};
                    } else if (speed == 60.) {
                        rpms = new double[]{2200., 2400., 2600., 2800., 3000., 3200.};
                    }
                    for (double rpm : rpms) {
                        setRpm(rpm);
                        ud.simTitle = versionFileHeader + "_"
                                + speed + "mph_"
                                + trim + "deg_"
                                + height + "in_"
                                + rpm + "rpm";
                        fileName = ud.simPath + slash + ud.simTitle;
                        run(speed, height, trim, rpm);
                        exportScene();
                        ud.numToAve = (int) (360 / stepSize);
                        CreateFrontPropSS(speed, height, trim, rpm);
                        CreateRearPropSS(speed, height, trim, rpm);
                        CreateCombinedPropSS(speed, height, trim, rpm);
                        CreateGcSS(speed, height, trim, rpm);
                    }
                    //}
                }
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
        // assign variables for particular version
        xProp = new double[2];
        for (int i = 0; i < xProps[i].length; i++) {
            xProp[i] = xProps[version - 1][i];
        }
        dProp = new double[3];
        for (int i = 0; i < dProps[i].length; i++) {
            dProp[i] = dProps[version - 1][i];
        }
        subAreaRatio = new double[6];
        for (int i = 0; i < subAreaRatios[i].length; i++) {
            subAreaRatio[i] = subAreaRatios[version - 1][i];
        }
        // print input values for user confirmation
        mu.io.say.value("Submerged Area Ratio",
                Arrays.toString(subAreaRatio), null, vo);
        mu.io.say.value("Prop Diameter", Arrays.toString(dProp), ud.unit_in, vo);
        mu.io.say.value("XPROP", Arrays.toString(xProp), ud.unit_in, vo);
    }

    void setSpeed(double speed) {
        // set wave speed
        ud.physCont = mu.get.objects.physicsContinua(".*", vo);
        vwm = ud.physCont.getModelManager().getModel(VofWaveModel.class);
        fvw = (FlatVofWave) vwm.getVofWaveManager().getObject("FlatVofWave 1");
        fvw.getCurrent().setComponents(speed, 0, 0);
        fvw.getWind().setComponents(speed, 0, 0);

        // set pressure coeff ref velocity
        ud.ff = mu.get.objects.fieldFunction(
                StaticDeclarations.Vars.PC.getVar(), vo);
        pcf = (PressureCoefficientFunction) ud.ff;
        pcf.getReferenceVelocity().setValue(speed);

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
        tpo = (TransformPartsOperation) mu.getSimulation()
                .get(MeshOperationManager.class)
                .getObject("Translate_Refine_Outer");
        tc = (TranslationControl) tpo.getTransforms().getObject("Translate");
        tc.getTranslationVector().setCoordinate(ud.unit_in, ud.unit_in, ud.unit_in,
                new DoubleVector(new double[]{
            trimPoint_z * Math.sin(trim * Math.PI / 180), 0.0,
            (trimPoint_x + xProp[0]) * Math.sin(trim * Math.PI / 180)}));

        // Clear solution history and fields
        mu.clear.solution();
        // Execute all mesh operations and update mesh count
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

        // -- Front Prop Center --
        propCenter = (CartesianCoordinateSystem) gcCenter
                .getLocalCoordinateSystemManager().getObject("f_Prop_Center");
        propCenter.getOrigin().setCoordinate(ud.unit_in, ud.unit_in, ud.unit_in,
                new DoubleVector(new double[]{xProp[0], 0.0, 0.0}));

        // -- Rear Prop Center --
        propCenter = (CartesianCoordinateSystem) gcCenter
                .getLocalCoordinateSystemManager().getObject("r_Prop_Center");
        propCenter.getOrigin().setCoordinate(ud.unit_in, ud.unit_in, ud.unit_in,
                new DoubleVector(new double[]{xProp[1], 0.0, 0.0}));
    }

    void setRpm(double rpm) {
        // set time step
        tStep = 1 / (rpm / 60 * 360 / stepSize);
        mu.set.solver.timestep(tStep);

        // set exhaust flow
        ud.bdry = mu.get.boundaries.byREGEX("r_exh_in", true);
        mu.set.boundary.values(ud.bdry,
                StaticDeclarations.Vars.MFR, mfr, ud.unit_kgps);

        // set front prop rotation speed
        rm = (RotatingMotion) mu.getSimulation().get(
                MotionManager.class).getObject("Front Rotation");
        rm.getRotationRate().setValue(rpm);

        // set rear prop rotation speed
        rm = (RotatingMotion) mu.getSimulation().get(
                MotionManager.class).getObject("Rear Rotation");
        rm.getRotationRate().setValue(rpm);

        // set number of timesteps
        if (rpm == rpms[0]) {
            numSteps = (int) Math.round(revs_init * 360 / stepSize);
        } else {
            numSteps = (int) Math.round(revs * 360 / stepSize);
        }
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
        mu.io.say.action("Exporting CSV Data", vo);

        ud.monPlot = (MonitorPlot) mu.get.plots.byREGEX("Front Prop", vo);
        ud.monPlot.export(fileName + "_front_prop.csv", ",");

        ud.monPlot = (MonitorPlot) mu.get.plots.byREGEX("Rear Prop", vo);
        ud.monPlot.export(fileName + "_rear_prop.csv", ",");

        ud.monPlot = (MonitorPlot) mu.get.plots.byREGEX("Combined Prop", vo);
        ud.monPlot.export(fileName + "_combined_prop.csv", ",");

        ud.monPlot = (MonitorPlot) mu.get.plots.byREGEX("Gearcase", vo);
        ud.monPlot.export(fileName + "_gc.csv", ",");

        mu.io.say.ok(vo);

        mu.saveSim();
    }

    void exportScene() {
        // export pressure coeff 3d scene
        ud.scene = mu.get.scenes.byREGEX("Scalar Scene", vo);
        ud.scene.export3DSceneFileAndWait(
                fileName + ".sce", ud.simTitle,
                "Pressure Coefficient", false, false);
        
        mu.io.write.picture(mu.get.scenes.byREGEX("animation", vo),
                ud.simTitle + "composite", 1650, 900, vo);
        
/*
        // write prop plots as picture (doesn't work with software rendering)
        mu.io.write.picture(mu.get.plots.byREGEX("Front Prop", vo),
                ud.simTitle + "_front_plot", ud.picResX, ud.picResY, vo);
        mu.io.write.picture(mu.get.plots.byREGEX("Rear Prop", vo),
                ud.simTitle + "_rear_plot", ud.picResX, ud.picResY, vo);

        // write composite 2d scenes
        mu.io.write.picture(mu.get.scenes.byREGEX("f_composite", vo),
                ud.simTitle + "_front_scene", 1000, 750, vo);
        mu.io.write.picture(mu.get.scenes.byREGEX("r_composite", vo),
                ud.simTitle + "_rear_scene", 1000, 750, vo);
        mu.io.write.picture(mu.get.scenes.byREGEX("Scalar Scene Side", vo),
                ud.simTitle + "_side_scene", 1200, 600, vo);
*/
        // clear solution history
        mu.clear.solutionHistory();
    }

    void CreateFrontPropSS(double speed, double height, double trim, double rpm)
            throws Exception {

        // create results spreadsheet if not already created
        ssTitle = ud.simPath + slash + versionFileHeader + "_Front_Prop.xls";
        if (!new File(ssTitle).exists()) {
            initSpreadsheet("prop");
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

        // read in prop data
        fileName = ud.simPath + slash + ud.simTitle;
        reader = new CSVReader(new FileReader(fileName + "_front_prop.csv"));
        data = reader.readAll();

        // compute mean and blade max/min of prop data
        stats = new SummaryStatistics();
        int reportIterator = 1;
        for (columnIterator = 5;
                columnIterator < numPropCol; columnIterator++) {
            for (rowIterator = data.size() - 1;
                    rowIterator >= data.size() - ud.numToAve; rowIterator--) {
                String[] array = data.get(rowIterator);
                stats.addValue(Double.parseDouble(array[reportIterator]));
            }
            // write data to row
            if (columnIterator == 12 || columnIterator == 16) {
                row.createCell(columnIterator).setCellValue(stats.getMean());
                row.createCell(columnIterator + 1).setCellValue(stats.getMax());
                row.createCell(columnIterator + 2).setCellValue(stats.getMin());
                columnIterator += 2;
            } else {
                row.createCell(columnIterator).setCellValue(stats.getMean());
            }
            stats = new SummaryStatistics();
            reportIterator++;
        }
        // Compute prop parameters of interest
        double torque = row.getCell(15).getNumericCellValue();
        double thrust = row.getCell(7).getNumericCellValue();
        double SHP = rpm * 2 * Math.PI / 60
                * row.getCell(15).getNumericCellValue() / 550;
        double J = speed * 1.467 / (rpm / 60 * dProp[0] / 12);
        double KT = row.getCell(7).getNumericCellValue()
                / (Math.pow(rpm / 60, 2) * Math.pow(dProp[0] / 12, 4) * 1.94);
        double KT_norm = KT / subAreaRatio[meshCount];
        double KQ = row.getCell(15).getNumericCellValue()
                / (Math.pow(rpm / 60, 2) * Math.pow(dProp[0] / 12, 5) * 1.94);
        double KQ_norm = KQ / subAreaRatio[meshCount];
        double eta = J / 2 / Math.PI * KT_norm / KQ_norm;

        // Write prop parameters to excel ss
        row.createCell(columnIterator).setCellValue(SHP); // colIt = 19
        row.createCell(columnIterator + 1).setCellValue(J);
        row.createCell(columnIterator + 2).setCellValue(KT_norm);
        row.createCell(columnIterator + 3).setCellValue(KQ_norm);
        row.createCell(columnIterator + 4).setCellValue(eta);

        // save spreadsheet
        mu.io.say.action("Updating Front Prop Results SS", vo);
        fileOut = new FileOutputStream(ssTitle);
        wb.write(fileOut);
        fileOut.close();
        mu.io.say.ok(vo);

    }

    void CreateRearPropSS(double speed, double height, double trim, double rpm)
            throws Exception {

        // create results spreadsheet if not already created
        ssTitle = ud.simPath + slash + versionFileHeader + "_Rear_Prop.xls";
        if (!new File(ssTitle).exists()) {
            initSpreadsheet("prop");
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

        // read in prop data
        fileName = ud.simPath + slash + ud.simTitle;
        reader = new CSVReader(new FileReader(fileName + "_rear_prop.csv"));
        data = reader.readAll();

        // compute mean and blade max/min of prop data
        stats = new SummaryStatistics();
        int reportIterator = 1;
        for (columnIterator = 5;
                columnIterator < numPropCol; columnIterator++) {
            for (rowIterator = data.size() - 1;
                    rowIterator >= data.size() - ud.numToAve; rowIterator--) {
                String[] array = data.get(rowIterator);
                stats.addValue(Double.parseDouble(array[reportIterator]));
            }
            // write data to row
            if (columnIterator == 12 || columnIterator == 16) {
                row.createCell(columnIterator).setCellValue(stats.getMean());
                row.createCell(columnIterator + 1).setCellValue(stats.getMax());
                row.createCell(columnIterator + 2).setCellValue(stats.getMin());
                columnIterator += 2;
            } else {
                row.createCell(columnIterator).setCellValue(stats.getMean());
            }
            stats = new SummaryStatistics();
            reportIterator++;
        }
        // Compute prop parameters of interest
        double torque = row.getCell(15).getNumericCellValue();
        double thrust = row.getCell(7).getNumericCellValue();
        double SHP = rpm * 2 * Math.PI / 60
                * row.getCell(15).getNumericCellValue() / 550;
        double J = speed * 1.467 / (rpm / 60 * dProp[1] / 12);
        double KT = row.getCell(7).getNumericCellValue()
                / (Math.pow(rpm / 60, 2) * Math.pow(dProp[1] / 12, 4) * 1.94);
        double KT_norm = KT / subAreaRatio[meshCount + 3];
        double KQ = row.getCell(15).getNumericCellValue()
                / (Math.pow(rpm / 60, 2) * Math.pow(dProp[1] / 12, 5) * 1.94);
        double KQ_norm = KQ / subAreaRatio[meshCount + 3];
        double eta = J / 2 / Math.PI * KT_norm / KQ_norm;

        // Write prop parameters to excel ss
        row.createCell(columnIterator).setCellValue(SHP); // colIt = 19
        row.createCell(columnIterator + 1).setCellValue(J);
        row.createCell(columnIterator + 2).setCellValue(KT_norm);
        row.createCell(columnIterator + 3).setCellValue(KQ_norm);
        row.createCell(columnIterator + 4).setCellValue(eta);

        // save spreadsheet
        mu.io.say.action("Updating Rear Prop Results SS", vo);
        fileOut = new FileOutputStream(ssTitle);
        wb.write(fileOut);
        fileOut.close();
        mu.io.say.ok(vo);

    }

    void CreateCombinedPropSS(double speed, double height, double trim, double rpm)
            throws Exception {

        // create results spreadsheet if not already created
        ssTitle = ud.simPath + slash + versionFileHeader + "_Combined_Prop.xls";
        if (!new File(ssTitle).exists()) {
            initSpreadsheet("combined");
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

        // read in prop data
        fileName = ud.simPath + slash + ud.simTitle;
        reader = new CSVReader(new FileReader(fileName + "_combined_prop.csv"));
        data = reader.readAll();

        // compute mean of 1 prop revolution
        stats = new SummaryStatistics();
        int reportIterator = 1;
        for (columnIterator = 5;
                columnIterator < numTitleCol + 2; columnIterator++) {
            for (rowIterator = data.size() - 1;
                    rowIterator >= data.size() - ud.numToAve; rowIterator--) {
                String[] array = data.get(rowIterator);
                stats.addValue(Double.parseDouble(array[reportIterator]));
            }
            // write data to row
            row.createCell(columnIterator).setCellValue(stats.getMean());
            stats = new SummaryStatistics();
            reportIterator++;
        }
        // Compute prop parameters of interest
        double SHP = rpm * 2 * Math.PI / 60
                * row.getCell(6).getNumericCellValue() / 550;
        double J = speed * 1.467 / (rpm / 60 * dProp[0] / 12);
        double KT = row.getCell(5).getNumericCellValue()
                / (Math.pow(rpm / 60, 2) * Math.pow(dProp[0] / 12, 4) * 1.94);
        double KQ = row.getCell(6).getNumericCellValue()
                / (Math.pow(rpm / 60, 2) * Math.pow(dProp[0] / 12, 5) * 1.94);
        double eta = J / 2 / Math.PI * KT / KQ;

        // Write prop parameters to excel ss
        row.createCell(columnIterator).setCellValue(SHP); // colIt = 19
        row.createCell(columnIterator + 1).setCellValue(J);
        row.createCell(columnIterator + 2).setCellValue(KT);
        row.createCell(columnIterator + 3).setCellValue(KQ);
        row.createCell(columnIterator + 4).setCellValue(eta);

        // save spreadsheet
        mu.io.say.action("Updating Combined Prop Results SS", vo);
        fileOut = new FileOutputStream(ssTitle);
        wb.write(fileOut);
        fileOut.close();
        mu.io.say.ok(vo);

    }

    void CreateGcSS(double speed, double height, double trim, double rpm)
            throws Exception {

        // create results spreadsheet if not already created
        ssTitle = ud.simPath + slash + versionFileHeader + "_Gearcase.xls";
        if (!new File(ssTitle).exists()) {
            initSpreadsheet("gc");
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

    void initSpreadsheet(String type) throws Exception {
        // Create prop excel workbook with headers
        wb = new HSSFWorkbook();
        row = wb.createSheet("Data").createRow(0);
        if ("prop".equals(type)) {
            for (int i = 0; i < propHeaders.length; i++) {
                row.createCell(i).setCellValue(propHeaders[i]);
            }
        } else if ("gc".equals(type)) {
            for (int i = 0; i < gcHeaders.length; i++) {
                row.createCell(i).setCellValue(gcHeaders[i]);
            }
        } else if ("combined".equals(type)) {
            for (int i = 0; i < combinedPropHeaders.length; i++) {
                row.createCell(i).setCellValue(combinedPropHeaders[i]);
            }
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
    double[] xProp;
    double[] dProp;
    double[] subAreaRatio;

}
