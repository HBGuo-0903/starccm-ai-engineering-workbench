// STAR-CCM+ macro: setCubicLinear.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.kwturb.*;

public class setCubicLinear extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    PhysicsContinuum physicsContinuum_0 = 
      ((PhysicsContinuum) simulation_0.getContinuumManager().getContinuum("Physics 1"));

    SstKwTurbModel sstKwTurbModel_0 = 
      physicsContinuum_0.getModelManager().getModel(SstKwTurbModel.class);

    sstKwTurbModel_0.getKwTurbConstitutiveOption().setSelected(KwTurbConstitutiveOption.Type.CUBIC);

    sstKwTurbModel_0.getKwTurbConstitutiveOption().setSelected(KwTurbConstitutiveOption.Type.QCR);

    sstKwTurbModel_0.getKwTurbConstitutiveOption().setSelected(KwTurbConstitutiveOption.Type.LINEAR);
  }
}
