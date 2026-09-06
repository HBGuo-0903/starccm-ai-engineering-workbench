// STAR-CCM+ macro: CreateTurbKineticEnergyFunctions.java
// Written by STAR-CCM+ 15.02.007
package macro;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import star.common.*;
import star.base.neo.*;
import star.base.report.*;

import star.vis.*;
import star.post.*;

public class RecordVelocityAtXz extends StarMacro {

    String uid;
    String SimName;
    String SessionDirectory;

    public void execute() {

        Dimensions dimensionless = Dimensions.Builder().build();
        Dimensions velocity = Dimensions.Builder().length(1).time(-1).build();
        Dimensions velocitySquared = Dimensions.Builder().length(2).time(-2).build();
        Dimensions diffusivity = Dimensions.Builder().length(2).time(-1).build();

        createScalarFieldFunction("u", "u", "$${Velocity}[0]", velocity);
        createScalarFieldFunction("v", "v", "$${Velocity}[1]", velocity);
        createScalarFieldFunction("w", "w", "$${Velocity}[2]", velocity);

        createSolutionHistory();
        // runOneRev();

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

    private void createSolutionHistory() {
        Simulation simulation = getActiveSimulation();
        FieldFunction targetFieldFunction1 = ((FieldFunction) simulation.getFieldFunctionManager().getFunction("u"));
        FieldFunction targetFieldFunction2 = ((FieldFunction) simulation.getFieldFunctionManager().getFunction("v"));
        FieldFunction targetFieldFunction3 = ((FieldFunction) simulation.getFieldFunctionManager().getFunction("w"));

        PlaneSection planeSection = ((PlaneSection) simulation.getPartManager().getObject("XZ Plane"));

        SolutionHistory solutionHistory = simulation.get(SolutionHistoryManager.class).createForFile(
                resolvePath(simulation.getSessionDir() + "/RecordedVelocities.simh"), false, false);
        solutionHistory.setFunctions(
                new NeoObjectVector(new Object[] { targetFieldFunction1, targetFieldFunction2, targetFieldFunction3 }));
        solutionHistory.getInputs().setQuery(null);
        solutionHistory.getInputs().setObjects(planeSection);
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

        Units units_2 = simulation.getUnitsManager().getInternalUnits(
                new IntVector(new int[] { 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }));

        PhysicalTimeStoppingCriterion physicalTimeStoppingCriterion = ((PhysicalTimeStoppingCriterion) simulation
                .getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Physical Time"));

        double curTime = simulation.getSolution().getPhysicalTime();
        physicalTimeStoppingCriterion.getMaximumTime().setDefinition(Double.toString(curTime) + " + 60/${RPM}");

        RunSimulation();
    }

}
