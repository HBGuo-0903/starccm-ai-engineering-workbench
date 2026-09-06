// STAR-CCM+ macro: SetTimeStep.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;

public class SetTimeStep extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    PhysicsContinuum physicsContinuum_0 = 
      ((PhysicsContinuum) simulation_0.getContinuumManager().getContinuum("Physics 2"));

    Region region_0 = 
      simulation_0.getRegionManager().getRegion("Fluid");

    physicsContinuum_0.add(region_0);

    ImplicitUnsteadySolver implicitUnsteadySolver_0 = 
      ((ImplicitUnsteadySolver) simulation_0.getSolverManager().getSolver(ImplicitUnsteadySolver.class));

    implicitUnsteadySolver_0.getTimeStep().setValue(1.0E-4);
  }
}
