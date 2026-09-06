// Simcenter STAR-CCM+ macro: ExportShearSurfaceData.java
// Written by Simcenter STAR-CCM+ 15.06.008
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;

public class ExportShear extends StarMacro {

  public void execute() {
    exportShearSurfaceToCsv();
  }

  private void exportShearSurfaceToCsv() {

    Simulation simulation = getActiveSimulation();
    XyzInternalTable xyzInternalTable = simulation.getTableManager().createTable(XyzInternalTable.class);
    PrimitiveFieldFunction primitiveFieldFunction = ((PrimitiveFieldFunction) simulation.getFieldFunctionManager()
        .getFunction("MeanShearStressMonitor"));

    xyzInternalTable.setFieldFunctions(new NeoObjectVector(new Object[] { primitiveFieldFunction }));
    xyzInternalTable.setPresentationName("MeanShearMonitor");
    xyzInternalTable.getParts().setQuery(null);

    Region region = simulation.getRegionManager().getRegion("Fluid");
    Boundary boundary = region.getBoundaryManager().getBoundary("Stator");

    xyzInternalTable.getParts().setObjects(boundary);
    xyzInternalTable.extract();

    String SessionDirectory = simulation.getSessionDir();
    String fullPath = SessionDirectory + "/" + "MeanShearMonitorData.csv";
    xyzInternalTable.export(fullPath, ",");
  }
}