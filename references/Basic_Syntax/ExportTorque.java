// Simcenter STAR-CCM+ macro: ExportShearSurfaceData.java
// Written by Simcenter STAR-CCM+ 15.06.008
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.base.report.*;

public class ExportTorque extends StarMacro {

  public void execute() {
    Simulation simulation = getActiveSimulation();
    String SessionDirectory = simulation.getSessionDir();

    exportNamedMonitorToCsv("RotorOnlyMoment Monitor", SessionDirectory + "/RotorOnlyTorque.csv");
    exportNamedMonitorToCsv("Rotor Moment Monitor", SessionDirectory + "/RotorTorque.csv");
    exportNamedMonitorToCsv("Stator Moment Monitor", SessionDirectory + "/StatorTorque.csv");
    exportNamedMonitorToCsv("Physical Time", SessionDirectory + "/PhysicalTime.csv");
  }

  private void exportNamedMonitorToCsv(String monitorName, String path) {
    Simulation simulation = getActiveSimulation();

    simulation.getMonitorManager().export(path, ",",
        new NeoObjectVector(new Object[] { simulation.getMonitorManager().getMonitor(monitorName) }));
  }

}