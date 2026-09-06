// STAR-CCM+ macro: AddPassiveScalars.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.passivescalar.*;

public class AddPassiveScalars extends StarMacro {

  public void execute() {
      String[] scalars = {"yBa_2+","ySO4_2-", "yNa_1+", "yCl_1-"};
    AddPassiveScalar(scalars);
  }

  private void AddPassiveScalar(String[] scalars) {

    Simulation simulation =  getActiveSimulation();
    PhysicsContinuum physicsContinuum = 
      ((PhysicsContinuum) simulation.getContinuumManager().getContinuum("Physics 1"));
    
    for(String scalar : scalars) {
        PassiveScalarModel passiveScalarModel = 
          physicsContinuum.getModelManager().getModel(PassiveScalarModel.class);

        PassiveScalarMaterial passiveScalarMaterial = 
          passiveScalarModel.getPassiveScalarManager().createPassiveScalarMaterial(PassiveScalarMaterial.class);
          
        passiveScalarMaterial.getTransportOption().setSelected(PassiveScalarTransportOption.Type.CONVECTION_ONLY);

        passiveScalarMaterial.getClipMode().setSelected(PassiveScalarClipMode.Type.CLIP_BOTH);

        passiveScalarMaterial.setMaxAllowable(1.0);

        passiveScalarMaterial.setPresentationName(scalar);
    }
  }
}
