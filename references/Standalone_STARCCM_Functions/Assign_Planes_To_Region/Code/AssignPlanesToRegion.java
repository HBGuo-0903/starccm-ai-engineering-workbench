// STAR-CCM+ macro: AssignPlanesToRegion.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class AssignPlanesToRegion extends StarMacro {

  public void execute() {
    execute0();
  }

  private void AssignPlanesToRegion() {

    Simulation simulation = 
      getActiveSimulation();

    PlaneSection planeSection_0 = 
      ((PlaneSection) simulation.getPartManager().getObject("XY Plane"));

    planeSection_0.getInputParts().setQuery(null);

    Region region_0 = 
      simulation.getRegionManager().getRegion("Fluid");

    Boundary boundary_1 = 
      region_0.getBoundaryManager().getBoundary("InletA");

    Boundary boundary_2 = 
      region_0.getBoundaryManager().getBoundary("InletB");

    Boundary boundary_3 = 
      region_0.getBoundaryManager().getBoundary("OutletA");

    Boundary boundary_4 = 
      region_0.getBoundaryManager().getBoundary("OutletB");

    Boundary boundary_0 = 
      region_0.getBoundaryManager().getBoundary("Rotor");

    Boundary boundary_5 = 
      region_0.getBoundaryManager().getBoundary("Stator");

    planeSection_0.getInputParts().setObjects(region_0, boundary_1, boundary_2, boundary_3, boundary_4, boundary_0, boundary_5);

    PlaneSection planeSection_1 = 
      ((PlaneSection) simulation.getPartManager().getObject("XZ Plane"));

    planeSection_1.getInputParts().setQuery(null);

    planeSection_1.getInputParts().setObjects(region_0, boundary_1, boundary_2, boundary_3, boundary_4, boundary_0, boundary_5);

    PlaneSection planeSection_2 = 
      ((PlaneSection) simulation.getPartManager().getObject("YZ Plane"));

    planeSection_2.getInputParts().setQuery(null);

    planeSection_2.getInputParts().setObjects(region_0, boundary_1, boundary_2, boundary_3, boundary_4, boundary_0, boundary_5);
  }
}
