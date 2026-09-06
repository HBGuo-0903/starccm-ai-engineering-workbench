// STAR-CCM+ macro: setStoppingCriteria.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;

public class setStoppingCriteria extends StarMacro {

  public void execute() {
    execute0();
  }

  private void SetStoppingCriteria() {

    Simulation simulation = 
      getActiveSimulation();

    PhysicalTimeStoppingCriterion physicalTimeStoppingCriterion = 
      ((PhysicalTimeStoppingCriterion) simulation.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Physical Time"));

    physicalTimeStoppingCriterion.getMaximumTime().setValue(10.0);

    StepStoppingCriterion stepStoppingCriterion = 
      ((StepStoppingCriterion) simulation.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps"));

    stepStoppingCriterion.setIsUsed(false);
  }
}
