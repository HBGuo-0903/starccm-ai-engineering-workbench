// Simcenter STAR-CCM+ macro: ExportShearSurfaceData.java
// Written by Simcenter STAR-CCM+ 15.06.008
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;

public class ExportShearRst extends StarMacro {
  static final String FIELDFUNCTIONNAME = "WallShearStress";
  static final String REGIONNAME = "Fluid";
  static final String PARTNAME = "Stator";
  static final String OUTPUTNAME = "MeanShearMonitorData";

  public void execute() {
    exportShearSurfaceToCsv();
  }

  private void exportShearSurfaceToCsv() {

    Simulation simulation = getActiveSimulation();
    XyzInternalTable xyzInternalTable = simulation.getTableManager().createTable(XyzInternalTable.class);

    PrimitiveFieldFunction fieldFunction = ((PrimitiveFieldFunction) simulation.getFieldFunctionManager().getFunction(FIELDFUNCTIONNAME));

    VectorMagnitudeFieldFunction vectorMagnitudeFieldFunction = ((VectorMagnitudeFieldFunction) fieldFunction
        .getMagnitudeFunction());

    xyzInternalTable.setFieldFunctions(new NeoObjectVector(new Object[] { vectorMagnitudeFieldFunction }));
    xyzInternalTable.setPresentationName(OUTPUTNAME);
    xyzInternalTable.getParts().setQuery(null);

    Region region = simulation.getRegionManager().getRegion(REGIONNAME);
    Boundary boundary = region.getBoundaryManager().getBoundary(PARTNAME);

    xyzInternalTable.getParts().setObjects(boundary);
    xyzInternalTable.extract();

    String SessionDirectory = simulation.getSessionDir();
    String fullPath = SessionDirectory + "/" + OUTPUTNAME + ".csv";
    xyzInternalTable.export(fullPath, ",");
  }
}