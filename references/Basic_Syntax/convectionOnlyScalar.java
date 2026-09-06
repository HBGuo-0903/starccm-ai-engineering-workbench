// STAR-CCM+ macro: convectionOnlyScalar.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.passivescalar.*;

public class convectionOnlyScalar extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    PhysicsContinuum physicsContinuum_0 = 
      ((PhysicsContinuum) simulation_0.getContinuumManager().getContinuum("Physics 1"));

    PassiveScalarModel passiveScalarModel_0 = 
      physicsContinuum_0.getModelManager().getModel(PassiveScalarModel.class);

    PassiveScalarMaterial passiveScalarMaterial_5 = 
      passiveScalarModel_0.getPassiveScalarManager().createPassiveScalarMaterial(PassiveScalarMaterial.class);

    passiveScalarMaterial_5.getTransportOption().setSelected(PassiveScalarTransportOption.Type.CONVECTION_ONLY);

    passiveScalarMaterial_5.getClipMode().setSelected(PassiveScalarClipMode.Type.CLIP_BOTH);

    passiveScalarMaterial_5.setMaxAllowable(1.0);
  }
}
