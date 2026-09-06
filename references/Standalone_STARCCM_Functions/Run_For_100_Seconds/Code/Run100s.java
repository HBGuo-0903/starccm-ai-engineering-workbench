// STAR-CCM+ macro: CreateTurbKineticEnergyFunctions.java
// Written by STAR-CCM+ 15.02.007
package macro;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import star.common.*;
import star.base.neo.*;
import star.base.report.*;

public class Run100s extends StarMacro {

  String uid;
  String SimName;
  String SessionDirectory;

  public void execute() {
    run(100.0);
  }

  
  private void RunSimulation() {

    Simulation simulation = getActiveSimulation();
    simulation.getSimulationIterator().run();
  }

  private void run(double time) {
    Simulation simulation = getActiveSimulation();

    PhysicalTimeStoppingCriterion physicalTimeStoppingCriterion = ((PhysicalTimeStoppingCriterion) simulation
        .getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Physical Time"));

    double curTime = simulation.getSolution().getPhysicalTime();
    physicalTimeStoppingCriterion.getMaximumTime().setDefinition(curTime + time);

    RunSimulation();
  }

}
