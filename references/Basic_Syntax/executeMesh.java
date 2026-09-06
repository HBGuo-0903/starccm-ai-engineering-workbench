// STAR-CCM+ macro: executeMesh.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.meshing.*;

public class executeMesh extends StarMacro {

  public void execute() {
    execute0();
  }

  private void ExecuteMesh() {

    Simulation simulation = 
      getActiveSimulation();

    AutoMeshOperation autoMeshOperation = 
      ((AutoMeshOperation) simulation.get(MeshOperationManager.class).getObject("Automated Mesh"));

    autoMeshOperation.execute();
  }
}
