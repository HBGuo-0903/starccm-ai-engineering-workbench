// Generates mesh and save the file - part mesh 

package macro;

import java.util.*;
import star.common.*;
import star.base.neo.*;
import star.meshing.*;

public class partMesh extends StarMacro {

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
	AutoMeshOperation autoMeshOperation_0 = ((AutoMeshOperation) simulation_0.get(MeshOperationManager.class).getObject("Automated Mesh"));	
    autoMeshOperation_0.executeSurfaceMeshers(); 
    autoMeshOperation_0.execute();

	/* save and close simulation */
	simulation_0.saveState(resolvePath(name)+"_mesh.sim");

	/* close simulation */	
	simulation_0.kill();

  }
}

