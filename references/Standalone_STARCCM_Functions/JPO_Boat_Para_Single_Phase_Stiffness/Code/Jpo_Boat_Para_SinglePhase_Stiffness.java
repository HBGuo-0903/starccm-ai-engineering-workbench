
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
import star.base.neo.DoubleVector;
import star.flow.VelocityMagnitudeProfile;
import star.meshing.*;
import star.vis.*;
import star.vof.*;

public class Jpo_Boat_Para_SinglePhase_Stiffness extends StarMacro {

    /* RUN MATRIX */
    double[] sinks = {24.5, 25.5, 26.5}; // in
    double[] staticRolls = {0, 3, 6, 9, 12, 20}; // deg
    double[] dynRolls = {-12, -6, 6, 12}; // deg
    double[] staticPitches = {-2, -1, 0, 1, 2}; // deg
    double[] yaws = {0, 45, 90, 135, 180}; // deg

    int iterations = 1000;
    int resx = 1200;
    int resy = 700;

    public void execute() {

        initMacro();
        for (double sink : sinks) {
            staticRollStability(sink);
            staticPitchStability(sink);
            rollResistance(sink);
        }

    }

    void staticRollStability(double sink) {
        title = "staticRollStability";
        for (double roll : staticRolls) {
            run(roll, 0, 0, 0, sink);
        }
    }

    void staticPitchStability(double sink) {
        title = "staticPitchStability";
        for (double pitch : staticPitches) {
            run(0, pitch, 0, 0, sink);
        }
    }

    void rollResistance(double sink) {
        title = "rollResistance";
        for (double roll : dynRolls) {
            for (double yaw : yaws) {
                run(roll, 0, yaw, 1, sink);
                run(roll, 0, yaw, 2, sink);
            }
        }
    }

    void run(double roll, double pitch, double yaw, double speed, double sink) {
        ud.simTitle = title
                + "_sink" + sink
                + "_roll" + roll
                + "_pitch" + pitch
                + "_yaw" + yaw
                + "_speed" + speed;
        pre(sink, roll, pitch, yaw, speed);
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
        ud.defColormap = mu.get.objects.colormap(
                StaticDeclarations.Colormaps.BLUE_RED);
    }

    void pre(double sink, double roll, double pitch, double yaw, double speed) {
        if (mu.check.has.volumeMesh()) {
            return;
        }

        // set inlet speed
        mu.get.boundaries.byREGEX("inlet", vo).getValues()
                .get(VelocityMagnitudeProfile.class).getMethod(
                ConstantScalarProfileMethod.class).getQuantity()
                .setValue(speed);

        // set boat orientation
        tpo = (TransformPartsOperation) mu.getSimulation()
                .get(MeshOperationManager.class).getObject("Transform");
        rcRoll = (RotationControl) tpo.getTransforms().getObject("roll");
        rcRoll.getAngle().setValue(roll);
        rcPitch = (RotationControl) tpo.getTransforms().getObject("pitch");
        rcPitch.getAngle().setValue(pitch);
        rcYaw = (RotationControl) tpo.getTransforms().getObject("yaw");
        rcYaw.getAngle().setValue(yaw);
        tcSink = (TranslationControl) tpo.getTransforms().getObject("sink");
        tcSink.getTranslationVector().setCoordinate(ud.unit_in, ud.unit_in, ud.unit_in, new DoubleVector(new double[]{0., 0., sink}));

        // set c-sys orientation
        sinkCsys = (CartesianCoordinateSystem) ud.lab0
                .getLocalCoordinateSystemManager().getObject("sink");
        // sink
        sinkCsys.getOrigin().setCoordinate(ud.unit_in, ud.unit_in, ud.unit_in,
                new DoubleVector(new double[]{
            0.0, 0.0, sink}));
        // yaw
        yawCsys = (CartesianCoordinateSystem) sinkCsys
                .getLocalCoordinateSystemManager().getObject("yaw");
        yawCsys.setBasis0(new DoubleVector(new double[]{
            Math.cos(yaw * Math.PI / 180),
            Math.sin(yaw * Math.PI / 180),
            0.0
        }));
        // roll
        rollTrimCsys = (CartesianCoordinateSystem) yawCsys
                .getLocalCoordinateSystemManager().getObject("roll_trim");
        rollTrimCsys.setBasis1(new DoubleVector(new double[]{
            0.0,
            Math.cos(roll * Math.PI / 180),
            Math.sin(roll * Math.PI / 180)
        }));
        // trim
        rollTrimCsys.setBasis0(new DoubleVector(new double[]{
            Math.cos(pitch * Math.PI / 180),
            0.0,
            Math.sin(-pitch * Math.PI / 180)
        }));

        mu.update.volumeMesh();
    }

    void solve() {
        if (mu.check.has.solution()) {
            return;
        }
        mu.step(iterations);
        for (Displayer d : mu.get.scenes.allDisplayers(vo)) {
            d.setRepresentation(mu.get.mesh.fvr());
        }
        mu.saveSim();
    }

    void post() throws Exception {
        // export plots
        //ud.picPath = ud.simPath + "/" + ud.simTitle;
        //mu.io.write.plots();

        // export waterline 2d scene
        ud.scene = mu.get.scenes.byREGEX("Velocity Scene", vo);
        mu.io.write.picture(ud.scene, ud.simTitle, resx, resy, vo);

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
            ud.rep = mu.get.reports.byREGEX(rep, vo);
            row.createCell(resultsCol).setCellValue(
                    ud.rep.getReportMonitorValue());
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
    String[] reports = {"Fx", "Fy", "Fz", "Mx", "My", "Mz", "Lift", "Drag"};

    VofWaveModel vwm;
    FlatVofWave fvw;
    TransformPartsOperation tpo;
    RotationControl rcRoll;
    RotationControl rcPitch;
    RotationControl rcYaw;
    TranslationControl tcSink;
    CartesianCoordinateSystem sinkCsys;
    CartesianCoordinateSystem yawCsys;
    CartesianCoordinateSystem rollTrimCsys;

}
