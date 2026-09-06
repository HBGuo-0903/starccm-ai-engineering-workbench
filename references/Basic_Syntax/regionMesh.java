// Note: "Region mesh" is not recommended by Siemens (no updates from 11.06)

package macro;

import java.util.*;
import star.common.*;
import star.meshing.*;

public class regionMesh extends StarMacro {

  public void execute() {
    meshing();
  }

  private void meshing() {

	/*get active simulation */
	Simulation simulation_0 = getActiveSimulation();
    
	/* Path and file name */
	String name = getActiveSimulation().getPresentationName();		
	String fullPath = resolvePath(name);				
	String filePath = fullPath.substring(0,fullPath.length()-name.length());
	String fileName = name;

    /* mesh and save simulation */    	
    MeshPipelineController meshPipelineController_0 = simulation_0.get(MeshPipelineController.class);
    meshPipelineController_0.generateSurfaceMesh();
    meshPipelineController_0.generateVolumeMesh();
	
	/* save and close simulation */
	simulation_0.saveState(resolvePath(name)+"_mesh.sim");

    /* close simulation */	
	simulation_0.kill();
  }
}
