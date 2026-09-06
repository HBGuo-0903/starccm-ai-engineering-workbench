
/**
 * DFBI simulation for planing boat hulls
 *
 * @author Andrew Gunderson
 *
 * 2017, v11.06
 */
import java.io.*;
import java.util.*;
import macroutils.*;
import star.common.*;
import org.apache.commons.math3.stat.descriptive.*;
import org.apache.poi.ss.usermodel.*;
import com.opencsv.CSVReader;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import star.meshing.*;
import star.vis.*;
import star.vof.*;

public class Jpo_Boat_Para_freeSurf extends StarMacro {

    /* STATIC PITCH AND HEAVE */
    double pitch0 = .8; // deg
    double waterline = 25.5; // in

    /* RUN MATRIX */
    // vary heave 24.5, 25.5, 26.5
    double[] speedsForward = {1, 2, 3}; // fps
    double[] speedsAft = {.5, 1, 2}; // fps
    double[] speedsSway = {.5, 1, 2}; // fps
    double[] speedsOblique = {.5, 1, 2}; // fps
    double[] staticRolls = {3, 6, 9, 12}; // deg
    double[] dynRolls = {-6, 6}; // deg
    double[] pitches = {-2, -1, 1, 2}; // deg

    double runTime = 100;
    int resx = 1200;
    int resy = 700;

    public void execute() {

        initMacro();
//        staticRollStability();
//        staticPitchStability();
        rollResistance();
//        forwardMotion();
//        aftMotion();
//        swayMotion();
//        obliqueMotion();

    }

    void staticRollStability() {
        title = "staticRollStability";
        for (double roll : staticRolls) {
            run(roll, 0, 0, 0);
        }
    }

    void staticPitchStability() {
        title = "staticPitchStability";
        for (double pitch : pitches) {
            run(0, pitch, 0, 0);
        }
    }

    void rollResistance() {
        title = "rollResistance";
        yaw = 90.;
        for (double roll : dynRolls) {
            run(roll, 0, yaw, 1);
        }
    }

    void forwardMotion() {
        title = "forwardMotion";
        for (double speed : speedsForward) {
            run(0, 0, 0, speed);
        }
    }

    void aftMotion() {
        title = "aftMotion";
        for (double speed : speedsAft) {
            run(0, 0, 180, speed);
        }
    }

    void swayMotion() {
        title = "swayMotion";
        for (double speed : speedsSway) {
            run(0, 0, 90, speed);
        }
    }

    void obliqueMotion() {
        title = "obliqueMotion";
        for (double speed : speedsOblique) {
            run(0, 0, 45, speed);
        }

    }

    void run(double roll, double pitch, double yaw, double speed) {
        ud.simTitle = title
                + "_roll" + roll
                + "_pitch" + pitch
                + "_yaw" + yaw
                + "_speed" + speed;
        pre(roll, pitch, yaw, speed);
        solve();
        try {
            post();
        } catch (Exception ex) {
            mu.getSimulation().println(ex);
        }
    }

    void initMacro() {
        mu = new MacroUtils(getActiveSimulation());
        ud = mu.userDeclarations;
        ud.defUnitLength = ud.unit_in;
    }

    void pre(double roll, double pitch, double yaw, double speed) {
        if (mu.check.has.volumeMesh()) {
            return;
        }
        // set inlet speed
        ud.physCont = mu.get.objects.physicsContinua(".*", vo);
        vwm = ud.physCont.getModelManager().getModel(VofWaveModel.class);
        fvw = (FlatVofWave) vwm.getVofWaveManager().getObject("FlatVofWave 1");
        fvw.getCurrent().setComponents(-speed, 0, 0);
        fvw.getWind().setComponents(-speed, 0, 0);

        // set timestep
        if (speed == 0.0) {
            tStep = .5 / (.5 * 12) * 2;
        } else {
            tStep = .5 / (speed * 12) * 2;
        }
        mu.set.solver.timestep(tStep);
        ud.numToAve = (int) (runTime / 10 / tStep);

        // set boat orientation
        tpo = (TransformPartsOperation) mu.getSimulation()
                .get(MeshOperationManager.class).getObject("Transform");
        rcRoll = (RotationControl) tpo.getTransforms().getObject("roll");
        rcRoll.getAngle().setValue(roll);
        rcPitch = (RotationControl) tpo.getTransforms().getObject("pitch");
        rcPitch.getAngle().setValue(pitch0 + pitch);
        rcYaw = (RotationControl) tpo.getTransforms().getObject("yaw");
        rcYaw.getAngle().setValue(yaw);

        mu.update.volumeMesh();
    }

    void solve() {
        if (mu.check.has.solution()) {
            return;
        }
        mu.get.solver.stoppingCriteria_MaxTime().setMaximumTime(runTime);
        mu.run();
        mu.saveSim();
    }

    void post() throws Exception {

        for (Displayer d : mu.get.scenes.allDisplayers(vo)) {
            d.setRepresentation(mu.get.mesh.fvr());
        }
        // export waterline 3d scene
        ud.scene = mu.get.scenes.byREGEX("waterline", vo);
        ud.scene.export3DSceneFileAndWait(
                ud.simPath + "/" + ud.simTitle + ".sce", "waterline",
                "", true, false);

        // export plots
        ud.picPath = ud.simPath + "/" + ud.simTitle;
        mu.io.write.plots();

        // export waterline 2d scene
        mu.io.write.picture(ud.scene, "waterline", resx, resy, vo);

        // update excel with numerical results
        String ssTitle = ud.simPath + "/results.xls";
        if (!new File(ssTitle).exists()) {
            wb = new HSSFWorkbook();
            sheet = wb.createSheet("data");
            row = sheet.createRow(0);
            row.createCell(0).setCellValue("Run");
            for (i = 0; i < reports.length; i++) {
                row.createCell(i + 1).setCellValue(reports[i]);
                out = new FileOutputStream(ssTitle);
                wb.write(out);
                out.close();
            }
        }

        wb = WorkbookFactory.create(new File(ssTitle));
        sheet = wb.getSheet("data");
        int currentRow = sheet.getLastRowNum() + 1;
        row = sheet.createRow(currentRow);
        row.createCell(0).setCellValue(ud.simTitle);
        resultsCol = 1;
        for (String rep : reports) {
            ud.mon = mu.get.monitors.byREGEX(rep, vo);
            String fileName = ud.simPath + "/" + ud.simTitle + ".csv";
            ud.mon.export(fileName);
            reader = new CSVReader(new FileReader(fileName));
            data = reader.readAll();
            stats = new SummaryStatistics();
            for (i = data.size() - 1; i >= data.size() - ud.numToAve; i--) {
                String[] rowData = data.get(i);
                stats.addValue(Double.parseDouble(rowData[1]));
            }
            row.createCell(resultsCol).setCellValue(stats.getMean());
            resultsCol++;
        }
        out = new FileOutputStream(ssTitle);
        wb.write(out);
        out.close();

        mu.clear.solution();
        mu.clear.meshes();
    }

    private MacroUtils mu;
    private UserDeclarations ud;
    boolean vo = true;

    String title;
    Double yaw;
    List<String[]> data;
    Workbook wb;
    Sheet sheet;
    Row row;
    CSVReader reader;
    SummaryStatistics stats;
    FileOutputStream out;
    AutoSave as;
    int i;
    int resultsCol;
    double tStep;
    String[] reports = {"Fx", "Fy", "Fz", "Mx", "My", "Mz"};

    VofWaveModel vwm;
    FlatVofWave fvw;
    TransformPartsOperation tpo;
    RotationControl rcRoll;
    RotationControl rcPitch;
    RotationControl rcYaw;
}
