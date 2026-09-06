// STAR-CCM+ macro: SetInnerIterations.java
// Written by STAR-CCM+ 15.02.007
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;

public class SetInnerIterations extends StarMacro {

  public void execute() {
    setInnerIterationCount(1);
  }

  private void setInnerIterationCount(int count) {
    Simulation simulation = getActiveSimulation();
    InnerIterationStoppingCriterion innerIterationStoppingCriterion = 
      ((InnerIterationStoppingCriterion) simulation.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Inner Iterations"));
      innerIterationStoppingCriterion.setMaximumNumberInnerIterations(count);
  }
}
