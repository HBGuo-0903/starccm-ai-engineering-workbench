// Simcenter STAR-CCM+ macro: a4monitors.java
// Written by Simcenter STAR-CCM+ 19.02.009


// this macro just creates monitors
// no prerequisits 


package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.base.report.*;

public class a4monitors extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    FieldMeanMonitor fieldMeanMonitor_0 = 
      simulation_0.getMonitorManager().create("star.base.report.FieldMeanMonitor");

    fieldMeanMonitor_0.setPresentationName("Mean WSS");

    fieldMeanMonitor_0.getParts().setQuery(null);

    Region region_0 = 
      simulation_0.getRegionManager().getRegion("Body 1");

    Boundary boundary_3 = 
      region_0.getBoundaryManager().getBoundary("wall");

    fieldMeanMonitor_0.getParts().setObjects(boundary_3);

    PrimitiveFieldFunction primitiveFieldFunction_1 = 
      ((PrimitiveFieldFunction) simulation_0.getFieldFunctionManager().getFunction("WallShearStress"));

    VectorMagnitudeFieldFunction vectorMagnitudeFieldFunction_1 = 
      ((VectorMagnitudeFieldFunction) primitiveFieldFunction_1.getMagnitudeFunction());

    fieldMeanMonitor_0.setFieldFunction(vectorMagnitudeFieldFunction_1);

    FieldSumMonitor fieldSumMonitor_0 = 
      simulation_0.getMonitorManager().create("star.base.report.FieldSumMonitor");

    fieldSumMonitor_0.setPresentationName("Sum WSS");

    fieldSumMonitor_0.getParts().setQuery(null);

    fieldSumMonitor_0.getParts().setObjects(boundary_3);

    fieldSumMonitor_0.setFieldFunction(vectorMagnitudeFieldFunction_1);

    FieldSumMonitor fieldSumMonitor_1 = 
      simulation_0.getMonitorManager().create("star.base.report.FieldSumMonitor");

    fieldSumMonitor_1.setPresentationName("Sum WSS i");

    fieldSumMonitor_1.getParts().setQuery(null);

    fieldSumMonitor_1.getParts().setObjects(boundary_3);

    VectorComponentFieldFunction vectorComponentFieldFunction_0 = 
      ((VectorComponentFieldFunction) primitiveFieldFunction_1.getComponentFunction(0));

    fieldSumMonitor_1.setFieldFunction(vectorComponentFieldFunction_0);

    FieldSumMonitor fieldSumMonitor_2 = 
      simulation_0.getMonitorManager().create("star.base.report.FieldSumMonitor");

    fieldSumMonitor_2.setPresentationName("Sum WSS j");

    fieldSumMonitor_2.getParts().setQuery(null);

    fieldSumMonitor_2.getParts().setObjects(boundary_3);

    VectorComponentFieldFunction vectorComponentFieldFunction_1 = 
      ((VectorComponentFieldFunction) primitiveFieldFunction_1.getComponentFunction(1));

    fieldSumMonitor_2.setFieldFunction(vectorComponentFieldFunction_1);

    FieldSumMonitor fieldSumMonitor_3 = 
      simulation_0.getMonitorManager().create("star.base.report.FieldSumMonitor");

    fieldSumMonitor_3.setPresentationName("Sum WSS k");

    fieldSumMonitor_3.getParts().setQuery(null);

    fieldSumMonitor_3.getParts().setObjects(boundary_3);

    VectorComponentFieldFunction vectorComponentFieldFunction_2 = 
      ((VectorComponentFieldFunction) primitiveFieldFunction_1.getComponentFunction(2));

    fieldSumMonitor_3.setFieldFunction(vectorComponentFieldFunction_2);
  }
}
