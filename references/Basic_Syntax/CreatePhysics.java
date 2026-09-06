// STAR-CCM+ macro: CreatePhysics.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.segregatedflow.*;
import star.material.*;
import star.turbulence.*;
import star.flow.*;
import star.metrics.*;

public class CreatePhysics extends StarMacro {

  public void execute() {
    execute0();
  }

  private void CreatePhysics() {

    Simulation simulation = 
      getActiveSimulation();

    PhysicsContinuum physicsContinuum = 
      simulation.getContinuumManager().createContinuum(PhysicsContinuum.class);

    physicsContinuum.enable(ThreeDimensionalModel.class);

    physicsContinuum.enable(ImplicitUnsteadyModel.class);

    physicsContinuum.enable(SingleComponentLiquidModel.class);

    physicsContinuum.enable(SegregatedFlowModel.class);

    physicsContinuum.enable(ConstantDensityModel.class);

    physicsContinuum.enable(TurbulentModel.class);

    physicsContinuum.enable(RansTurbulenceModel.class);
  }
}
