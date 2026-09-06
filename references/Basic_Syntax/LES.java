// STAR-CCM+ macro: LES.java
// Written by STAR-CCM+ 14.02.010
package macro;

import java.util.*;

import star.common.*;
import star.segregatedflow.*;
import star.material.*;
import star.turbulence.*;
import star.lesturb.*;
import star.flow.*;
import star.metrics.*;

public class LES extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    PhysicsContinuum physicsContinuum_0 = 
      simulation_0.getContinuumManager().createContinuum(PhysicsContinuum.class);

    physicsContinuum_0.enable(ThreeDimensionalModel.class);

    physicsContinuum_0.enable(ImplicitUnsteadyModel.class);

    physicsContinuum_0.enable(SingleComponentLiquidModel.class);

    physicsContinuum_0.enable(SegregatedFlowModel.class);

    physicsContinuum_0.enable(ConstantDensityModel.class);

    physicsContinuum_0.enable(TurbulentModel.class);

    physicsContinuum_0.enable(LesTurbulenceModel.class);

    physicsContinuum_0.enable(WaleSgsModel.class);

    physicsContinuum_0.enable(LesAllYplusWallTreatment.class);
  }
}
