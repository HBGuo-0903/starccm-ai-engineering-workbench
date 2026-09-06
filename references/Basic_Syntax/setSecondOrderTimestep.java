// STAR-CCM+ macro: setSecondOrderTimestep.java
// Written by STAR-CCM+ 15.02.007
package macro;

import java.util.*;

import star.common.*;

public class setSecondOrderTimestep extends StarMacro {

  public void execute() {
    enableSecondOrderTimestep();
  }

  private void enableSecondOrderTimestep() {
    Simulation simulation = getActiveSimulation();
    ImplicitUnsteadySolver implicitUnsteadySolver = 
      ((ImplicitUnsteadySolver) simulation.getSolverManager().getSolver(ImplicitUnsteadySolver.class));
    implicitUnsteadySolver.getTimeDiscretizationOption().setSelected(TimeDiscretizationOption.Type.SECOND_ORDER);
  }
}
