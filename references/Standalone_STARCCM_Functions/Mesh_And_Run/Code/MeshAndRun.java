// STAR-CCM+ macro: CouetteCell.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;
import java.io.*;
import java.nio.file.*;


import star.common.*;
import star.base.neo.*;
import star.base.report.*;

import star.meshing.*;
import star.prismmesher.*;
import star.solidmesher.*;
import star.dualmesher.*;

import star.kwturb.*;
import star.lesturb.*;

import star.segregatedflow.*;
import star.material.*;
import star.turbulence.*;
import star.flow.*;
import star.metrics.*;
import star.vis.*;

public class MeshAndRun extends StarMacro {
   
   public void execute() {
     
    ExecuteMesh();
    
    RunSimulation();
  }
 
  
  
  private void RunSimulation() {

    Simulation simulation = getActiveSimulation();

    Solution solution = simulation.getSolution();

    solution.initializeSolution();
    solution.initializeSolution();

    simulation.getSimulationIterator().run();
  }
  
  private void ExecuteMesh() {

    Simulation simulation = getActiveSimulation();

    AutoMeshOperation autoMeshOperation = 
      ((AutoMeshOperation) simulation.get(MeshOperationManager.class).getObject("Automated Mesh"));

    autoMeshOperation.execute();
  }
    
}