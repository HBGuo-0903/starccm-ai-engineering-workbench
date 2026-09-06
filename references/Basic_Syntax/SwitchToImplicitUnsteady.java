// STAR-CCM+ macro: SwitchToImplicitUnsteady.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;

public class SwitchToImplicitUnsteady extends StarMacro {

  public void execute() {
    swapToUnsteady();
  }

  private void swapToUnsteady() {

    Simulation simulation_0 = 
      getActiveSimulation();

    PhysicsContinuum physicsContinuum_0 = 
      ((PhysicsContinuum) simulation_0.getContinuumManager().getContinuum("Physics 1"));

    SteadyModel steadyModel_0 = 
      physicsContinuum_0.getModelManager().getModel(SteadyModel.class);

    physicsContinuum_0.disableModel(steadyModel_0);

    physicsContinuum_0.enable(ImplicitUnsteadyModel.class);
  }
}
