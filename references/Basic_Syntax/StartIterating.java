// STAR-CCM+ macro: StartIterating.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;

public class StartIterating extends StarMacro {

  public void execute() {
    execute0();
  }

  private void RunSimulation() {

    Simulation simulation = getActiveSimulation();

    Solution solution = simulation.getSolution();

    solution.initializeSolution();
    solution.initializeSolution();

    simulation.getSimulationIterator().run();
  }
}
