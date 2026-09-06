// STAR-CCM+ macro: SimpleSave.java
// Written by STAR-CCM+ 15.02.007
package macro;

import java.io.File;
import java.util.*;

import star.common.*;
import star.base.neo.*;

public class SimpleSave extends StarMacro {

  public void execute() {
    Save();
  }

  private void Save() {
    Simulation simulation = getActiveSimulation();
    File f = simulation.getSessionDirFile();
    String fname = f.getName();
    simulation.saveState(fname);
  }


}
