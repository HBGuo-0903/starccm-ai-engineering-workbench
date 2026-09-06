// STAR-CCM+ macro: CreateTurbKineticEnergyFunctions.java
// Written by STAR-CCM+ 15.02.007
package macro;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import star.common.*;
import star.base.neo.*;
import star.base.report.*;


public class RecordTurbulenceStats extends StarMacro {


  String uid;
  String SimName;
  String SessionDirectory;

  public void execute() {



    // Create Velocity^2 field functions
    createVel2FieldFunction("Velocity ii", "vel_ii", "[$${Velocity}[0] * $${Velocity}[0], $${Velocity}[1] * $${Velocity}[1], $${Velocity}[2] * $${Velocity}[2]]");
    createVel2FieldFunction("Velocity ij", "vel_ij", "[$${Velocity}[0] * $${Velocity}[1], $${Velocity}[0] * $${Velocity}[2], $${Velocity}[1] * $${Velocity}[2]]");

    // Create Velocity^2 monitors
    for(int i = 0; i < 3; i++){  
        createMeanFlowMonitor("Velocity", i);
        createMeanFlowMonitor("vel_ii", i);
        createMeanFlowMonitor("vel_ij", i);
      }

    // Create Reynolds Stress field functions
    createVel2FieldFunction("Reynolds Stress ii", "rs_ii", "[${Meanvel_ii_0Monitor}-${MeanVelocity_0Monitor}*${MeanVelocity_0Monitor},${Meanvel_ii_1Monitor}-${MeanVelocity_1Monitor}*${MeanVelocity_1Monitor},${Meanvel_ii_2Monitor}-${MeanVelocity_2Monitor}*${MeanVelocity_2Monitor}]");
    createVel2FieldFunction("Reynolds Stress ij", "rs_ij", "[${Meanvel_ij_0Monitor}-${MeanVelocity_0Monitor}*${MeanVelocity_1Monitor},${Meanvel_ij_1Monitor}-${MeanVelocity_0Monitor}*${MeanVelocity_2Monitor},${Meanvel_ij_2Monitor}-${MeanVelocity_1Monitor}*${MeanVelocity_2Monitor}]");

    // Create Reynolds Stress monitors
    for(int i = 0; i < 3; i++){  
        createMeanFlowMonitor("rs_ii", i);
        createMeanFlowMonitor("rs_ij", i);
      }
    

      runOneRev();

      SaveFile();
  }

private void SaveFile(){
    Simulation simulation =  getActiveSimulation();
    uid = UUID.randomUUID().toString().substring(0, 5);
    SimName = "Sim_" + "TurbStats" + "_" + uid + ".sim";
    SessionDirectory = simulation.getSessionDir();
    Save();

}

private void Save() {
  Simulation simulation = getActiveSimulation();
  String fullPath = SessionDirectory + "/TurbStats/" + SimName;

  try {
    Files.createDirectories(Paths.get(SessionDirectory + "/TurbStats"));
    simulation.saveState(fullPath);
  } catch (Exception ex) {
    simulation.println(ex);
  }
}

private void RunSimulation() {

  Simulation simulation = getActiveSimulation();
  simulation.getSimulationIterator().run();
}

  private void createVel2FieldFunction(String name, String identifier, String definition) {

    Simulation simulation_0 = 
      getActiveSimulation();

    UserFieldFunction userFieldFunction_0 = 
      simulation_0.getFieldFunctionManager().createFieldFunction();

    userFieldFunction_0.getTypeOption().setSelected(FieldFunctionTypeOption.Type.VECTOR);

    userFieldFunction_0.setPresentationName(name);

    userFieldFunction_0.setFunctionName(identifier);

    userFieldFunction_0.setDimensions(Dimensions.Builder().length(2).time(-2).build());

    userFieldFunction_0.setDefinition(definition);
  }

  private void createMeanFlowMonitor(String vel, int component) {

    Simulation simulation_0 = 
      getActiveSimulation();

    FieldMeanMonitor fieldMeanMonitor_1 = 
      simulation_0.getMonitorManager().createMonitor(FieldMeanMonitor.class);

    fieldMeanMonitor_1.getParts().setQuery(null);

    Region region_0 = 
      simulation_0.getRegionManager().getRegion("Fluid");

    fieldMeanMonitor_1.getParts().setObjects(region_0);

    
    FieldFunction targetFieldFunction = 
      ((FieldFunction) simulation_0.getFieldFunctionManager().getFunction(vel));

    VectorComponentFieldFunction vectorComponentFieldFunction_0 = 
      ((VectorComponentFieldFunction) targetFieldFunction.getComponentFunction(component));

    fieldMeanMonitor_1.setFieldFunction(vectorComponentFieldFunction_0);

    fieldMeanMonitor_1.setPresentationName("Mean " + vel + "_" + component);
  }

  private void createMeanScalarMonitor(String scalar) {

    Simulation simulation_0 = 
      getActiveSimulation();

    FieldMeanMonitor fieldMeanMonitor_1 = 
      simulation_0.getMonitorManager().createMonitor(FieldMeanMonitor.class);

    fieldMeanMonitor_1.getParts().setQuery(null);

    Region region_0 = 
      simulation_0.getRegionManager().getRegion("Fluid");

    fieldMeanMonitor_1.getParts().setObjects(region_0);

    
    FieldFunction targetFieldFunction = 
      ((FieldFunction) simulation_0.getFieldFunctionManager().getFunction(vel));

    fieldMeanMonitor_1.setFieldFunction(targetFieldFunction);

    fieldMeanMonitor_1.setPresentationName("Mean " + vel + "_" + component);
  }

  private void runOneRev() {

    Simulation simulation_0 = 
      getActiveSimulation();

    Units units_2 = 
      simulation_0.getUnitsManager().getInternalUnits(new IntVector(new int[] {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

    PhysicalTimeStoppingCriterion physicalTimeStoppingCriterion_0 = 
      ((PhysicalTimeStoppingCriterion) simulation_0.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Physical Time"));


    double curTime = simulation_0.getSolution().getPhysicalTime();
    physicalTimeStoppingCriterion_0.getMaximumTime().setDefinition( Double.toString(curTime) + " + 60/$RPM");
    
    RunSimulation();
  }

}
