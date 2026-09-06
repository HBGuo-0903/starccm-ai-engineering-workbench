// STAR-CCM+ macro: solutionInitialization.java
// Written by STAR-CCM+ 8.06.005
package macro;

import java.util.*;

import star.common.*;

public class solutionInitialization extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    Solution solution_0 = 
      simulation_0.getSolution();

    solution_0.clearSolution();

    solution_0.initializeSolution();
  }
}
