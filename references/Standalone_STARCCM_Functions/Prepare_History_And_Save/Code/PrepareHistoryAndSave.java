// STAR-CCM+ macro: CreateTurbKineticEnergyFunctions.java
// Written by STAR-CCM+ 15.02.007
package macro;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.io.File;

import star.common.*;
import star.base.neo.*;

public class PrepareHistoryAndSave extends StarMacro {

  String uid;
  String SimName;
  String SessionDirectory;

  public void execute() {

    setInnerIterationCount(1);
    enableSecondOrderTimestep();
    new StarScript(getActiveRootObject(), new File(resolvePath("CreateLineProbe.java"))).play();
    new StarScript(getActiveRootObject(), new File(resolvePath("RecordReynoldsStresses.java"))).play();
    new StarScript(getActiveRootObject(), new File(resolvePath("CreateExportTable.java"))).play();
    Save();
    new StarScript(getActiveRootObject(), new File(resolvePath("RunOneRev.java"))).play();
    Save();

  }

  private void Save() {
    Simulation simulation = getActiveSimulation();
    File f = simulation.getSessionDirFile();
    String fname = f.getName()+".sim";
    simulation.saveState(fname);
  }

  private void setInnerIterationCount(int count) {
    Simulation simulation = getActiveSimulation();
    InnerIterationStoppingCriterion innerIterationStoppingCriterion = 
      ((InnerIterationStoppingCriterion) simulation.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Inner Iterations"));
      innerIterationStoppingCriterion.setMaximumNumberInnerIterations(count);
  }

  private void enableSecondOrderTimestep() {
    Simulation simulation = getActiveSimulation();
    ImplicitUnsteadySolver implicitUnsteadySolver = 
      ((ImplicitUnsteadySolver) simulation.getSolverManager().getSolver(ImplicitUnsteadySolver.class));
    implicitUnsteadySolver.getTimeDiscretizationOption().setSelected(TimeDiscretizationOption.Type.SECOND_ORDER);
  }

}
