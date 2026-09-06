// Simcenter STAR-CCM+ macro: a9outlet.java
// Written by Simcenter STAR-CCM+ 19.02.009
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.flow.*;

public class a9outlet extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    Region region_0 = 
      simulation_0.getRegionManager().getRegion("Body 1");

    Boundary boundary_0 = 
      region_0.getBoundaryManager().getBoundary("outlet");

    OutletBoundary outletBoundary_0 = 
      ((OutletBoundary) simulation_0.get(ConditionTypeManager.class).get(OutletBoundary.class));

    boundary_0.setBoundaryType(outletBoundary_0);

    OutletSplitFactor outletSplitFactor_0 = 
      boundary_0.getValues().get(OutletSplitFactor.class);

    Units units_1 = 
      ((Units) simulation_0.getUnitsManager().getObject(""));

    outletSplitFactor_0.getSplitFactor().setValueAndUnits(0.5, units_1);

    Boundary boundary_1 = 
      region_0.getBoundaryManager().getBoundary("outlet 2");

    boundary_1.setBoundaryType(outletBoundary_0);

    OutletSplitFactor outletSplitFactor_1 = 
      boundary_1.getValues().get(OutletSplitFactor.class);

    outletSplitFactor_1.getSplitFactor().setValueAndUnits(0.5, units_1);
  }
}
