// STAR-CCM+ macro: RecordHistory.java
// Written by STAR-CCM+ 15.02.007
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;
import star.post.*;

public class RecordHistory extends StarMacro {

  public void execute() {
    createSolutionHistory();
  }

  private void createSolutionHistory() {

    Simulation simulation = getActiveSimulation();
    FieldFunction targetFieldFunction1 = ((FieldFunction) simulation.getFieldFunctionManager().getFunction("u"));
    FieldFunction targetFieldFunction2 = ((FieldFunction) simulation.getFieldFunctionManager().getFunction("v"));
    FieldFunction targetFieldFunction3 = ((FieldFunction) simulation.getFieldFunctionManager().getFunction("w"));

    PlaneSection planeSection = ((PlaneSection) simulation.getPartManager().getObject("XZ Plane"));

    SolutionHistory solutionHistory = simulation.get(SolutionHistoryManager.class).createForFile(
            resolvePath(simulation.getSessionDir() + "/RecordedVelocities.simh"), false, false);
    solutionHistory.setFunctions(
            new NeoObjectVector(new Object[] { targetFieldFunction1, targetFieldFunction2, targetFieldFunction3 }));
    solutionHistory.getInputs().setQuery(null);
    solutionHistory.getInputs().setObjects(planeSection);
  }
}
