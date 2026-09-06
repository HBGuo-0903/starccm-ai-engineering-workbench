// STAR-CCM+ macro: CreateTurbKineticEnergyFunctions.java
// Written by STAR-CCM+ 15.02.007
package macro;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import star.common.*;
import star.base.neo.*;
import star.base.report.*;

public class RunOneRev extends StarMacro {

  String uid;
  String SimName;
  String SessionDirectory;

  public void execute() {

    runOneRev();

    // SaveFile();
  }

  private void SaveFile() {
    Simulation simulation = getActiveSimulation();
    uid = UUID.randomUUID().toString().substring(0, 5);
    SimName = "Sim_" + "RunOneRev" + "_" + uid + ".sim";
    SessionDirectory = simulation.getSessionDir();
    Save();

  }

  private void Save() {
    Simulation simulation = getActiveSimulation();
    String fullPath = SessionDirectory + "/" + SimName;

    try {
      simulation.saveState(fullPath);
    } catch (Exception ex) {
      simulation.println(ex);
    }
  }

  private void RunSimulation() {

    Simulation simulation = getActiveSimulation();
    simulation.getSimulationIterator().run(120);
  }

  private void runOneRev() {

    Simulation simulation = getActiveSimulation();

    PhysicalTimeStoppingCriterion physicalTimeStoppingCriterion = ((PhysicalTimeStoppingCriterion) simulation
        .getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Physical Time"));

    double curTime = simulation.getSolution().getPhysicalTime();
    physicalTimeStoppingCriterion.getMaximumTime().setDefinition(Double.toString(curTime) + " + 60/${RPM}");

    RunSimulation();
  }

}
