// STAR-CCM+ macro: SetAutoSaveIteration.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;

public class SetAutoSaveIteration extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    AutoSave autoSave_0 = 
      simulation_0.getSimulationIterator().getAutoSave();

    StarUpdate starUpdate_0 = 
      autoSave_0.getStarUpdate();

    starUpdate_0.getUpdateModeOption().setSelected(StarUpdateModeOption.Type.ITERATION);

    IterationUpdateFrequency iterationUpdateFrequency_0 = 
      starUpdate_0.getIterationUpdateFrequency();

    iterationUpdateFrequency_0.setIterations(1000);
  }
}
