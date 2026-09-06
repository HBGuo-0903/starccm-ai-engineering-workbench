// STAR-CCM+ macro: setAutoSave.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;

public class setAutoSave extends StarMacro {

  public void execute() {
    execute0();
  }

  private void SetAutoSave() {

    Simulation simulation = getActiveSimulation();

    AutoSave autoSave = simulation.getSimulationIterator().getAutoSave();

    autoSave.setSeparator("_At_");
    autoSave.setFormatWidth(6);

    StarUpdate starUpdate = autoSave.getStarUpdate();

    starUpdate.setEnabled(true);
    if(IsSteady){
        starUpdate.getUpdateModeOption().setSelected(StarUpdateModeOption.Type.ITERATION);
        IterationUpdateFrequency iterationUpdateFrequency = starUpdate.getIterationUpdateFrequency();
        iterationUpdateFrequency.setIterations(5000);
    } else {
        starUpdate.getUpdateModeOption().setSelected(StarUpdateModeOption.Type.TIMESTEP);
        TimeStepUpdateFrequency timeStepUpdateFrequency = starUpdate.getTimeStepUpdateFrequency();
        timeStepUpdateFrequency.setTimeSteps(1000);
    }
    autoSave.setMaxAutosavedFiles(2);
  }
}
