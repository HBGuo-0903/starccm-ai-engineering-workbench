// STAR-CCM+ macro: saveSolution.java
// Written by STAR-CCM+ 8.06.005
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;

public class saveSolution extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    simulation_0.saveState(resolvePath("C:\\Users\\renova\\Documents\\Pablo\\A320_M10H2_FLAP@DOE_edit_flap_curvo.sim"));
  }
}
