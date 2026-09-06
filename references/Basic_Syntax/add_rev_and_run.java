// Simcenter STAR-CCM+ macro: add_rev_and_run.java
// Written by Simcenter STAR-CCM+ 20.02.007
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;

public class add_rev_and_run extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();
    
    // set revs
    ScalarGlobalParameter scalarGlobalParameter_0 = 
      ((ScalarGlobalParameter) simulation_0.get(GlobalParameterManager.class).getObject("revs"));

    Units units_0 = 
      ((Units) simulation_0.getUnitsManager().getObject(""));

    scalarGlobalParameter_0.getQuantity().setValueAndUnits(6.0, units_0);

    // run
    simulation_0.getSimulationIterator().run();

    // save to current path
    simulation_0.saveState(simulation_0.getSessionPath());
  }
}
