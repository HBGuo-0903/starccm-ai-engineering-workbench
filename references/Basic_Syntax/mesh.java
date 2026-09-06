// STAR-CCM+ macro: mesh.java
// Written by STAR-CCM+ 13.04.010
package macro;

import java.util.*;

import star.common.*;
import star.meshing.*;

public class mesh extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    AutoMeshOperation autoMeshOperation_0 = 
      ((AutoMeshOperation) simulation_0.get(MeshOperationManager.class).getObject("Automated-Mesh"));

    autoMeshOperation_0.execute();
  }
}
