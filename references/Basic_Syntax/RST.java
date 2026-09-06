// STAR-CCM+ macro: RST.java
// Written by STAR-CCM+ 15.02.007
package macro;

import java.util.*;

import star.common.*;
import star.segregatedflow.*;
import star.material.*;
import star.turbulence.*;
import star.rsturb.*;
import star.flow.*;
import star.metrics.*;

public class RST extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    PhysicsContinuum physicsContinuum = 
      simulation_0.getContinuumManager().createContinuum(PhysicsContinuum.class);

    physicsContinuum.enable(ThreeDimensionalModel.class);

    physicsContinuum.enable(ImplicitUnsteadyModel.class);

    physicsContinuum.enable(SingleComponentLiquidModel.class);

    physicsContinuum.enable(SegregatedFlowModel.class);

    physicsContinuum.enable(ConstantDensityModel.class);

    physicsContinuum.enable(TurbulentModel.class);

    physicsContinuum.enable(RansTurbulenceModel.class);

    physicsContinuum.enable(ReynoldsStressTurbulence.class);

    physicsContinuum.enable(EbRsTurbModel.class);

    physicsContinuum.enable(EbRsAllYplusWallTreatment.class);
  }
}
