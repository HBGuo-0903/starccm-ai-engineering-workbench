// STAR-CCM+ macro: CreateTurbKineticEnergyFunctions.java
// Written by STAR-CCM+ 15.02.007
package macro;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import star.common.*;
import star.base.neo.*;
import star.base.report.*;

public class RecordReynoldsStresses extends StarMacro {

  String uid;
  String SimName;
  String SessionDirectory;

  public void execute() {

    Dimensions dimensionless = Dimensions.Builder().build();
    Dimensions velocity = Dimensions.Builder().length(1).time(-1).build();
    Dimensions velocitySquared = Dimensions.Builder().length(2).time(-2).build();
    Dimensions diffusivity = Dimensions.Builder().length(2).time(-1).build();

    // Create Velocity^2 field functions
    createVectorFieldFunction("Velocity ii", "vel_ii",
        "[$${Velocity}[0] * $${Velocity}[0], $${Velocity}[1] * $${Velocity}[1], $${Velocity}[2] * $${Velocity}[2]]",
        velocitySquared);
    createVectorFieldFunction("Velocity ij", "vel_ij",
        "[$${Velocity}[0] * $${Velocity}[1], $${Velocity}[0] * $${Velocity}[2], $${Velocity}[1] * $${Velocity}[2]]",
        velocitySquared);

    // Create Mean(Velocity^2) and Mean(Velocity) monitors
    for (int i = 0; i < 3; i++) {
      createMeanFlowMonitor("Velocity", i);
      createMeanFlowMonitor("vel_ii", i);
      createMeanFlowMonitor("vel_ij", i);

    }

    // ${MeanNaNaMonitor}-${MeanmNa_1+Monitor}*${MeanmNa_1+Monitor}
    // Create Reynolds Stress field functions
    createVectorFieldFunction("Reynolds Stress ii", "rs_ii",
        "[${Meanvel_ii_0Monitor}-${MeanVelocity_0Monitor}*${MeanVelocity_0Monitor},${Meanvel_ii_1Monitor}-${MeanVelocity_1Monitor}*${MeanVelocity_1Monitor},${Meanvel_ii_2Monitor}-${MeanVelocity_2Monitor}*${MeanVelocity_2Monitor}]",
        velocitySquared);
    createVectorFieldFunction("Reynolds Stress ij", "rs_ij",
        "[${Meanvel_ij_0Monitor}-${MeanVelocity_0Monitor}*${MeanVelocity_1Monitor},${Meanvel_ij_1Monitor}-${MeanVelocity_0Monitor}*${MeanVelocity_2Monitor},${Meanvel_ij_2Monitor}-${MeanVelocity_1Monitor}*${MeanVelocity_2Monitor}]",
        velocitySquared);

    createTensorFieldFunction("Velocity Gradient Tensor", "GradU",
        "[ grad(${MeanVelocity_0Monitor})[0],grad(${MeanVelocity_0Monitor})[1],grad(${MeanVelocity_0Monitor})[2];  grad(${MeanVelocity_1Monitor})[1],grad(${MeanVelocity_1Monitor})[2]; grad(${MeanVelocity_2Monitor})[2] ]",
        Dimensions.Builder().time(-1).build());

    createTensorFieldFunction("Reynolds Stress Tensor", "R_t",
        "[ $${rs_ii}[0],$${rs_ij}[1],$${rs_ij}[2]; $${rs_ii}[1],$${rs_ij}[2]; $${rs_ii}[2] ]", velocitySquared);

    createScalarFieldFunction("Fittet Viscosity", "c_u_i",
        "norm($$${R_t}, \"frobenius\")/norm($$${GradU}, \"frobenius\")", diffusivity);

    // runOneRev();

    // Create Reynolds Stress monitors
    for (int i = 0; i < 3; i++) {
      createMeanFlowMonitor("rs_ii", i);
      createMeanFlowMonitor("rs_ij", i);
    }

    // SaveFile();
  }

  private void SaveFile() {
    Simulation simulation = getActiveSimulation();
    uid = UUID.randomUUID().toString().substring(0, 5);
    SimName = "Sim_" + "TurbStats" + "_" + uid + ".sim";
    SessionDirectory = simulation.getSessionDir();
    Save();

  }

  private void Save() {
    Simulation simulation = getActiveSimulation();
    String fullPath = SessionDirectory + "/" + SimName;

    try {
      // Files.createDirectories(Paths.get(SessionDirectory + "/TurbStats"));
      simulation.saveState(fullPath);
    } catch (Exception ex) {
      simulation.println(ex);
    }
  }

  private void RunSimulation() {

    Simulation simulation = getActiveSimulation();
    simulation.getSimulationIterator().run();
  }

  private void createScalarFieldFunction(String name, String identifier, String definition, Dimensions units) {
    Simulation simulation = getActiveSimulation();
    UserFieldFunction userFieldFunction = simulation.getFieldFunctionManager().createFieldFunction();
    userFieldFunction.getTypeOption().setSelected(FieldFunctionTypeOption.Type.SCALAR);
    userFieldFunction.setPresentationName(name);
    userFieldFunction.setFunctionName(identifier);
    userFieldFunction.setDimensions(units);
    userFieldFunction.setDefinition(definition);
  }

  private void createVectorFieldFunction(String name, String identifier, String definition, Dimensions units) {
    Simulation simulation = getActiveSimulation();
    UserFieldFunction userFieldFunction = simulation.getFieldFunctionManager().createFieldFunction();
    userFieldFunction.getTypeOption().setSelected(FieldFunctionTypeOption.Type.VECTOR);
    userFieldFunction.setPresentationName(name);
    userFieldFunction.setFunctionName(identifier);
    userFieldFunction.setDimensions(units);
    userFieldFunction.setDefinition(definition);
  }

  private void createTensorFieldFunction(String Name, String Identifier, String Definition, Dimensions Units) {
    Simulation simulation = getActiveSimulation();
    UserFieldFunction userFieldFunction = simulation.getFieldFunctionManager().createFieldFunction();
    userFieldFunction.getTypeOption().setSelected(FieldFunctionTypeOption.Type.SYMMETRIC_TENSOR);
    userFieldFunction.setPresentationName(Name);
    userFieldFunction.setFunctionName(Identifier);
    userFieldFunction.setDimensions(Units);
    userFieldFunction.setDefinition(Definition);
  }

  private void createMeanFlowMonitor(String vel, int component) {

    Simulation simulation = getActiveSimulation();

    FieldMeanMonitor fieldMeanMonitor = simulation.getMonitorManager().createMonitor(FieldMeanMonitor.class);
    fieldMeanMonitor.getParts().setQuery(null);
    Region region = simulation.getRegionManager().getRegion("Fluid");
    fieldMeanMonitor.getParts().setObjects(region);

    FieldFunction targetFieldFunction = ((FieldFunction) simulation.getFieldFunctionManager().getFunction(vel));
    VectorComponentFieldFunction vectorComponentFieldFunction = ((VectorComponentFieldFunction) targetFieldFunction
        .getComponentFunction(component));
    fieldMeanMonitor.setFieldFunction(vectorComponentFieldFunction);

    fieldMeanMonitor.setPresentationName("Mean " + vel + "_" + component);
  }

  private void createMeanScalarMonitor(String scalar) {

    Simulation simulation = getActiveSimulation();

    FieldMeanMonitor fieldMeanMonitor = simulation.getMonitorManager().createMonitor(FieldMeanMonitor.class);
    fieldMeanMonitor.getParts().setQuery(null);
    Region region = simulation.getRegionManager().getRegion("Fluid");
    fieldMeanMonitor.getParts().setObjects(region);

    FieldFunction targetFieldFunction = ((FieldFunction) simulation.getFieldFunctionManager().getFunction(scalar));
    fieldMeanMonitor.setFieldFunction(targetFieldFunction);

    fieldMeanMonitor.setPresentationName("Mean " + scalar);
  }

  private void runOneRev() {

    Simulation simulation = getActiveSimulation();

    PhysicalTimeStoppingCriterion physicalTimeStoppingCriterion = ((PhysicalTimeStoppingCriterion) simulation
        .getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Physical Time"));

    double curTime = simulation.getSolution().getPhysicalTime();
    physicalTimeStoppingCriterion.getMaximumTime().setDefinition(Double.toString(curTime) + " + 60/${RPM}");

    RunSimulation();
  }

}
