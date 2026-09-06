// Simcenter STAR-CCM+ macro: a2inletvel.java
// Written by Simcenter STAR-CCM+ 19.02.009


//Part needs to be named "Body 1"
// right cliclk body 1 and click "assign part to region" - both options have to be the 1st underdrop down
// RUN


package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.flow.*;

public class a2inletvel extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    FileTable fileTable_1 = 
      (FileTable) simulation_0.getTableManager().createFromFile(resolvePath("P:\\Macros\\unsteadyinlet.csv"), null);

    Region region_0 = 
      simulation_0.getRegionManager().getRegion("Body 1");

    Boundary boundary_0 = 
      region_0.getBoundaryManager().getBoundary("inlet");

    InletBoundary inletBoundary_0 = 
      ((InletBoundary) simulation_0.get(ConditionTypeManager.class).get(InletBoundary.class));

    boundary_0.setBoundaryType(inletBoundary_0);

    VelocityMagnitudeProfile velocityMagnitudeProfile_0 = 
      boundary_0.getValues().get(VelocityMagnitudeProfile.class);

    velocityMagnitudeProfile_0.getMethod(TimeTabularScalarProfileMethod.class).setTable(fileTable_1);

    velocityMagnitudeProfile_0.getMethod(TimeTabularScalarProfileMethod.class).setData("velocity");

    velocityMagnitudeProfile_0.getMethod(TimeTabularScalarProfileMethod.class).getInterpolationOption().setSelected(InterpolationOption.Type.CUBIC_AKIMA);
  }
}
