
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

public class Props_WriteScenes extends StarMacro {

    //--------------------------------------------------------------------------
    // -- USER INPUTS --
    //--------------------------------------------------------------------------
    // NOTE: be sure to go through this script and check that all the strings
    //       match the object names inside your simulation file (i.e. c-sys,
    //       boundary names, mesh operation names, etc.)
    boolean linux = true;
    int version = 0;
    int hubVersion = 1;
    String versionFileHeader = "6036_hub_v" + hubVersion;
    double[][] subAreaRatios = {
        {0.8856, 0.7886, 0.6715},
        {0.8740, 0.7787, 0.6650},
        {0.8978, 0.7992, 0.6785},
        {0.8856, 0.7886, 0.6716},
        {0.8856, 0.7886, 0.6715},
        {0.8856, 0.7885, 0.6714},
        {0.8856, 0.7887, 0.6716},
        {0.8856, 0.7886, 0.6715},
        {0.8856, 0.7886, 0.6715},
        {0.8853, 0.7880, 0.6707},
        {0.8859, 0.7892, 0.6724},
        {0.8855, 0.7885, 0.6713},
        {0.8857, 0.7887, 0.6717},
        {0.8856, 0.7886, 0.6715},
        {0.8856, 0.7886, 0.6715}
    };
// prop diameter (in)
    double[] dProps = {
        14.502722,
        15.002926,
        14.002553,
        14.502607,
        14.502823,
        14.502741,
        14.502668,
        14.502708,
        14.502781,
        14.502813,
        14.502614,
        14.502743,
        14.502677,
        14.502832,
        14.502594
    };
    // distance from prop center to GC center (in)
    double[] xProps = {
        13.1907390,
        13.2960820,
        13.0882700,
        13.1878010,
        13.1942020,
        13.1964430,
        13.1851520,
        13.1904440,
        13.1912000,
        13.2439180,
        13.1389370,
        13.2028830,
        13.1789650,
        13.1900920,
        13.1913690
    };
    //--------------------------------------------------------------------------
    // -- END USER INPUTS --
    //--------------------------------------------------------------------------

    String[] headers = {"Revision",
        "Speed (mph)",
        "Trim (deg)",
        "Height (in.)",
        "RPM",
        "Prop Lift (lbf)",
        "Prop Sideforce (lbf)",
        "Prop Thrust Net (lbf)",
        "Prop Thrust Normal (lbf)",
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
        "eta",
        "Gearcase Drag (lbf",
        "Gearcase Lift (lbf)",
        "Gearcase Sideforce (lbf)",
        "Gearcase Pitch Moment (lbf-ft)",
        "Gearcase Roll Moment (lbf-ft)",
        "Gearcase Yaw Moment (lbf-ft)"
    };

    // Simulation parameters
    double[] speeds = {62.7, 58.6}; // mph
    double[] trims = {5, 7.5, 10}; // deg, positive is trim out
    double[] heights = {7.19}; // // level trim propshaft depth below water (in.)
    double[] rpms = {3135, 3265.5, 3396, 3526.5, 3657};
    double mfr = .4; // exhaust mass flow rate (kg/s)
    double stepSize = 1.; // degrees per timestep 
    double revs_init = 4; // number of prop revolutions for initial rpm setting
    double revs = 2; // number of prop revolutions for subsequent rpms
    double trimPoint_z = 43.19; // z distance from trim point to GC center (in)
    double trimPoint_x = 11.1; // x distance from trim point to GC center (in)
    int numPropReports = 10; // number of reports being exported to csv file
    int numTitleCol = 5; // number of columns containing run condition info (speed, trim, etc)
    int numPropCol = numPropReports + numTitleCol + 4;
    int numGcReports = 6; // number of gc reports being exported to csv

    public void execute() {
        try {
            
            if (linux) {
                slash = "/";
            } else {
                slash = "\\";
            }
            sim = getSimulation();
            simPath = sim.getSessionDir();
            
            for (double speed : speeds) {

                for (double height : heights) {

                    for (double trim : trims) {

                        for (double rpm : rpms) {
                            simTitle = versionFileHeader + "_"
                                    + speed + "mph_"
                                    + trim + "deg_"
                                    + height + "in_"
                                    + rpm + "rpm";
                            fileName = simPath + slash + simTitle;
                            initMacro();
                            exportScene();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            mu.getSimulation().println(ex);
        }
    }

    void initMacro() {
        sim.kill();
        sim = new Simulation(fileName + ".sim");
        mu = new MacroUtils(sim, intrusive);
        ud = mu.userDeclarations;        
    }

    void exportScene() {
        // export pressure coeff 3d scene
        ud.scene = mu.get.scenes.byREGEX("Scalar Scene Side", vo);
        ud.scene.export3DSceneFileAndWait(
                fileName + "_exh_cav_waterline.sce", ud.simTitle,
                "P-coeff, exhaust, cavitation, waterline", false, false);
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
    double xProp;
    double dProp;
    double[] subAreaRatio;
    Simulation sim;
    String simPath;
    String simTitle;

}
