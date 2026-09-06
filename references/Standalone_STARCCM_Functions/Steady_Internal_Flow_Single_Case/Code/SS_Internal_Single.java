
/**
 * Simple steady state internal flow simulation
 * with streamlines, contours, and total pressure monitors
 *
 * @author Andrew Gunderson
 *
 * 2017, v11.06
 */
import java.io.*;
import java.util.*;
import macroutils.*;
import star.base.report.*;
import star.common.*;
import star.vis.*;
import org.apache.commons.math3.stat.descriptive.*;
import org.apache.poi.ss.usermodel.*;
import com.opencsv.CSVReader;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import star.base.neo.DoubleVector;

public class SS_Internal_Single extends StarMacro {

    String version = "v10";
    String flowRate = "23Lpm";
    String[] headers = {"Revision", "A", "B", "C", "D", "E", "F"};
    Double mfr = 0.389;

    int resx = 1200;
    int resy = 300;

    public void execute() {

        // get pre-created plane sections
        sections = getSimulation().getPartManager().getObjects();

        try {

            initMacro(version, flowRate);

            if (!mu.check.has.volumeMesh()) {
                physics();
                mesh(version);
            }

            if (!mu.check.has.solution()) {
                monitors();
                solve();
            }

            if (!mu.getSimulation().isParallel()) {
            post();
            output();
            }

            mu.saveSim();
            mu.remove.all();

        } catch (Exception ex) {
            mu.getSimulation().println(ex);
        }

    }

    void initMacro(String version, String flowRate) {
        mu = new MacroUtils(getSimulation());
        ud = mu.userDeclarations;
        ud.simTitle = version + "_" + flowRate;
        as = mu.getSimulation().getSimulationIterator().getAutoSave();
    }

    void physics() {
        ud.physCont = mu.add.physicsContinua.generic(
                StaticDeclarations.Space.THREE_DIMENSIONAL,
                StaticDeclarations.Time.STEADY,
                StaticDeclarations.Material.LIQUID,
                StaticDeclarations.Solver.SEGREGATED,
                StaticDeclarations.Density.CONSTANT,
                StaticDeclarations.Energy.ISOTHERMAL,
                StaticDeclarations.Viscous.RKE_2LAYER);
        mu.enable.cellQualityRemediation(ud.physCont, vo);
        ud.viscWater = 0.000854;
        ud.denWater = 1015;
        mu.set.physics.materialProperty(ud.physCont, "water",
                StaticDeclarations.Vars.VISC, ud.viscWater, ud.unit_Pa_s);
        mu.set.physics.materialProperty(ud.physCont, "water",
                StaticDeclarations.Vars.DEN, ud.denWater, ud.unit_kgpm3);
    }

    void mesh(String version) {
        // add geometry which is placed in working directory
        mu.add.geometry.importPart(version + ".x_b");
        ud.region = mu.add.region.fromAll(true);
        ud.geometryParts = mu.get.geometries.all(true);
        mu.add.scene.geometry();
        // mesh sizing (default units are mm)
        ud.mshBaseSize = 2;
        ud.prismsLayers = 4;
        ud.prismsRelSizeHeight = 33;
        ud.prismsStretching = 1.5;
        ud.autoMshOp = mu.add.meshOperation.automatedMesh(ud.geometryParts,
                StaticDeclarations.Meshers.SURFACE_REMESHER,
                StaticDeclarations.Meshers.POLY_MESHER,
                StaticDeclarations.Meshers.PRISM_LAYER_MESHER);
        mu.set.boundary.asMassFlowInlet(
                mu.get.boundaries.byREGEX(".*" + ud.bcInlet, vo),
                mfr, 20.0, 0.05, 10.0);
        mu.set.boundary.asPressureOutlet(
                mu.get.boundaries.byREGEX(".*" + ud.bcOutlet, vo),
                0.0, 21.0, 0.05, 10.0);
        as.setAutoSaveMesh(false);
        mu.update.volumeMesh();
        ud.scene = mu.add.scene.mesh();
        mu.saveSim();
    }

    void monitors() {
        // choose region and create report for each plane section
        for (Part ps : sections) {
            ps.getInputParts().setObjects(mu.get.regions.byREGEX(".*", vo));
            ud.rep = mu.add.report.massFlowAverage(ps,
                    ps.getPresentationName(),
                    mu.get.objects.fieldFunction(
                            StaticDeclarations.Vars.TOTAL_P),
                    ud.unit_Pa, vo);
        }
    }

    void solve() {
        //mu.set.solver.aggressiveSettings();
        as.setAutoSaveBatch(false);
        mu.step(2000);
        mu.saveSim();
    }

    void post() {
        // set scene properties
        ud.defColormap = mu.get.objects.colormap(StaticDeclarations.Colormaps.BLUE_RED);
        ud.legVis = false;
        // create resampled volume scene
        ud.rvp = (ResampledVolumePart) mu.getSimulation().
                getPartManager().createResampledVolumePart();
        ud.rvp.getInputParts().setObjects(mu.get.regions.byREGEX(".*", vo));
        // -- params set by part size
        ud.rvp.getOriginCoordinate().setValue(new DoubleVector(
                new double[]{.199, .359, .05}));
        ud.rvp.getSizeCoordinate().setValue(new DoubleVector(
                new double[]{.400, .218, .219}));
        // --
        ud.namedObjects.clear();
        ud.namedObjects.add(ud.rvp);
        ud.ff = mu.get.objects.fieldFunction(
                StaticDeclarations.Vars.VEL.getVar(), vo);
        ud.scene1 = mu.add.scene.scalar(
                ud.namedObjects, ud.ff, ud.unit_mps, vo);
        ud.scene1.setPresentationName("Velocity Contours");
        ud.disp = mu.get.scenes.displayerByREGEX(ud.scene1, ".*", vo);
        ud.sdq = mu.get.scenes.scalarDisplayQuantity(ud.disp, vo);
        ud.sdq.setRange(new double[]{0, 2});

        // create streamline scene
        ud.scene = mu.add.scene.geometry();
        ud.disp = mu.get.scenes.displayerByREGEX(ud.scene, ".*", vo);
        ud.disp.setFrontFaceCulling(true);
        ud.disp.setColorMode(PartColorMode.CONSTANT);
        ud.scene.setPresentationName("Streamline");
        ud.namedObjects.clear();
        ud.namedObjects.add(mu.get.geometries.byREGEX(".*", vo));
        ud.namedObjects.add(mu.get.boundaries.byREGEX(ud.bcInlet, vo));
        ud.postStreamlinesTubesWidth = 0.001;
        ud.postStreamlineResolution = 10;
        ud.std = mu.add.scene.displayer_Streamline(
                ud.scene, ud.namedObjects, true);
        ud.sdq = mu.get.scenes.scalarDisplayQuantity(ud.std, vo);
        ud.sdq.setRange(new double[]{0, 2});

        // apply recommended visual settings
        mu.templates.prettify.all();
    }

    void output() throws Exception {
        // read in camera views
        mu.io.read.cameraViews("myCameras.txt");
        // output velo scene
        for (VisView vv : mu.get.cameras.allByREGEX(".*(3|4)", vo)) {
            mu.set.scene.cameraView(ud.scene, vv, vo);
            mu.io.sleep(1000);
            mu.io.write.picture(
                    ud.scene,
                    ud.simTitle + " " + vv.getPresentationName(),
                    resx, resy, vo);
        }
        // output streamline scene
        for (VisView vv : mu.get.cameras.allByREGEX(".*(1|2)", vo)) {
            mu.set.scene.cameraView(ud.scene1, vv, vo);
            mu.io.sleep(1000);
            mu.io.write.picture(
                    ud.scene1,
                    ud.simTitle + " " + vv.getPresentationName(),
                    resx, resy, vo);
        }
         
        // create or update pressure drop results spreadsheet
        String ssTitle = ud.simPath + "\\results.xls";
        if (!new File(ssTitle).exists()) {
            wb = new HSSFWorkbook();
            sheet = wb.createSheet("data");
            row = sheet.createRow(0);
            for (i = 0; i < headers.length; i++) {
                row.createCell(i).setCellValue(headers[i]);
                out = new FileOutputStream(ssTitle);
                wb.write(out);
                out.close();
            }
        }
        wb = WorkbookFactory.create(new File(ssTitle));
        sheet = wb.getSheet("data");
        int currentRow = sheet.getLastRowNum() + 1;
        row = sheet.createRow(currentRow);
        j = 0;
        for (Report rep : mu.get.reports.all(vo)) {
            ud.mon = mu.get.monitors.fromReport(rep, vo);
            String fileName = ud.simPath + "\\temp.csv";
            ud.mon.export(fileName);
            reader = new CSVReader(new FileReader(fileName));
            data = reader.readAll();
            stats = new SummaryStatistics();
            for (i = data.size() - 1; i >= data.size() - ud.numToAve; i--) {
                String[] rowData = data.get(i);
                stats.addValue(Double.parseDouble(rowData[1]));
            }
            if (j != 0) {
                row.createCell(j).setCellValue(mean - stats.getMean());
            } else {
                row.createCell(0).setCellValue(ud.simTitle);
            }
            mean = stats.getMean();
            j++;
        }
        out = new FileOutputStream(ssTitle);
        wb.write(out);
        out.close();

    }

    private MacroUtils mu;
    private UserDeclarations ud;
    boolean vo = true;

    Collection<Part> sections;
    List<String[]> data;
    Workbook wb;
    Sheet sheet;
    Row row;
    CSVReader reader;
    SummaryStatistics stats;
    FileOutputStream out;
    AutoSave as;

    double mean;
    int i;
    int j;

}
