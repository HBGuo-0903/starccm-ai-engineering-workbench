

package macro;

import java.util.*;

// Additional package might be required based on the sim file you want to solve.
import star.common.*;
import star.base.neo.*;
import star.meshing.*;
import star.segregatedenergy.*;

public class run extends StarMacro {

  public void execute() {
    run();
  }

  private void execute0() {

	/*get active simulation */
	Simulation simulation_0 = getActiveSimulation();

	/* Path and file name */
	String name = getActiveSimulation().getPresentationName();		
	String fullPath = resolvePath(name);				
	String filePath = fullPath.substring(0,fullPath.length()-name.length());
	String fileName = name;

        /* run Simulation */
        simulation_0.getSimulationIterator().run();
	
	/* close simulation */	
	simulation_0.kill();

  }
}
