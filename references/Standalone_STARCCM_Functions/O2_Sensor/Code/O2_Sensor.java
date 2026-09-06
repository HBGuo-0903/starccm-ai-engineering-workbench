
/**
 * Creates reports/monitors/plots for potential O2 sensor positions and 
 * multiple passive scalars (1 per cylinder). Runs specified number of cycles,
 * and exports the sensor data to .csv files.
 *
 * @author Andrew Gunderson
 * TR2017-0210-003
 * 2017, starccm+ v12.02
 */
import star.common.*;
import macroutils.*;
import java.util.*;

public class O2_Sensor extends StarMacro {

    int cycles = 1;

    public void execute() {
        initMacro();
        
        // UNCOMENT FOLLOWING SECTION IF REPORTS HAVEN'T YET BEEN CREATED
/*
        // create port sensor report/monitor/plots
        for (int i = 1; i <= 5; i++) {
            ud.geometryParts.clear();
            ud.geometryParts.add(mu.get.geometries.byREGEX("p-" + i, vo));
            for (int j = 1; j <= 12; j++) {
                mu.add.report.volumeAverage(
                        ud.geometryParts,
                        "p" + i + "-cyl" + j,
                        mu.get.objects.fieldFunction("Passive Scalar " + j, vo),
                        ud.unit_Dimensionless, vo);
            }
        }

        // create starboard sensor report/monitor/plots
        for (int i = 1; i <= 5; i++) {
            ud.geometryParts.clear();
            ud.geometryParts.add(mu.get.geometries.byREGEX("s-" + i, vo));
            for (int j = 1; j <= 12; j++) {
                mu.add.report.volumeAverage(
                        ud.geometryParts,
                        "s" + i + "-cyl" + j,
                        mu.get.objects.fieldFunction("Passive Scalar " + j, vo),
                        ud.unit_Dimensionless, vo);
            }
        }
*/
        // solve
        mu.clear.solutionHistory();
        mu.step(cycles * 720);
        mu.saveSim("star-rev3");

        // export time history of port sensor data
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 12; j++) {
                mp = (MonitorPlot) mu.get.plots.byREGEX("p" + i + "-cyl" + j, vo);
                mp.export(ud.simPath + "/p" + i + "-cyl" + j + ".csv", ",");
            }
        }
        
        // export time history of starboard sensor data
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 12; j++) {
                mp = (MonitorPlot) mu.get.plots.byREGEX("s" + i + "-cyl" + j, vo);
                mp.export(ud.simPath + "/s" + i + "-cyl" + j + ".csv", ",");
            }
        }        
        
    }

    void initMacro() {
        mu = new MacroUtils(getActiveSimulation());
        ud = mu.userDeclarations;
    }

    MacroUtils mu;
    UserDeclarations ud;
    boolean vo = true;
    MonitorPlot mp;
}
