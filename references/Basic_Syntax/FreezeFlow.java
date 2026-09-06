// STAR-CCM+ macro: FreezeFlow.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.segregatedflow.*;
import star.base.neo.*;

public class FreezeFlow extends StarMacro {

  public void execute() {
    freezeFlow();
  }

  private void freezeFlow() {

    Simulation simulation = 
      getActiveSimulation();

    SegregatedFlowSolver segregatedFlowSolver = 
      ((SegregatedFlowSolver) simulation.getSolverManager().getSolver(SegregatedFlowSolver.class));

    segregatedFlowSolver.setFreezeFlow(true);
  }
}
