// STAR-CCM+ macro: SetVelocityMagInlet.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.flow.*;

public class SetVelocityMagInlet extends StarMacro {

  public void execute() {
    execute0();
  }

  private void SetInletVelocity() {

    Simulation simulation = 
      getActiveSimulation();

    Units units_3 = 
      simulation.getUnitsManager().getInternalUnits(new IntVector(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

    Region fluidRegion = 
      simulation.getRegionManager().getRegion("Fluid");

    Boundary inletBoundaryA = 
      fluidRegion.getBoundaryManager().getBoundary("InletA");

    VelocityMagnitudeProfile velocityMagnitudeProfile_0 = 
      inletBoundaryA.getValues().get(VelocityMagnitudeProfile.class);

    velocityMagnitudeProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setDefinition("$InletVelocity");

    Boundary inletBoundaryB = 
      fluidRegion.getBoundaryManager().getBoundary("InletB");

    VelocityMagnitudeProfile velocityMagnitudeProfile_1 = 
      inletBoundaryB.getValues().get(VelocityMagnitudeProfile.class);

    velocityMagnitudeProfile_1.getMethod(ConstantScalarProfileMethod.class).getQuantity().setDefinition("$InletVelocity");
  }
}
