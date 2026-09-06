// STAR-CCM+ macro: AddSteadyClass.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;

public class AddSteadyClass extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    PhysicsContinuum physicsContinuum_0 = 
      ((PhysicsContinuum) simulation_0.getContinuumManager().getContinuum("Physics 1"));

    physicsContinuum_0.enable(SteadyModel.class);
  }
}
