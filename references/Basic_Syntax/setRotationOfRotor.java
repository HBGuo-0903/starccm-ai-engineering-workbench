// STAR-CCM+ macro: setRotationOfRotor.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.flow.*;

public class setRotationOfRotor extends StarMacro {

  public void execute() {
    execute0();
  }

  private void SetRotationOfRotor() {

    Simulation simulation = 
      getActiveSimulation();

    Region fluidRegion = 
      simulation.getRegionManager().getRegion("Fluid");

    Boundary rotorBoundary = 
      fluidRegion.getBoundaryManager().getBoundary("Rotor");

    rotorBoundary.getConditions().get(WallSlidingOption.class).setSelected(WallSlidingOption.Type.LOCAL_ROTATION_RATE);

    Units units_1 = 
      simulation.getUnitsManager().getInternalUnits(new IntVector(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0}));

    WallRelativeRotationProfile wallRelativeRotationProfile_0 = 
      rotorBoundary.getValues().get(WallRelativeRotationProfile.class);

    wallRelativeRotationProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setDefinition("$RPM");
  }
}
