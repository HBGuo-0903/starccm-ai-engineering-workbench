// Simcenter STAR-CCM+ macro: RecordMeanShearStresses.java
// Written by Simcenter STAR-CCM+ 15.06.008
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
import star.resurfacer.*;
import star.passivescalar.*;

import star.kwturb.*;
import star.lesturb.*;
import star.rsturb.*;
import star.keturb.*;

import star.passivescalar.*;
import star.segregatedenergy.*;
import star.segregatedflow.*;
import star.material.*;
import star.turbulence.*;
import star.flow.*;
import star.metrics.*;
import star.vis.*;

public class RecordMeanShearStress extends StarMacro {

    public double samplingDurationSeconds = 1.0;

    public void execute() {
        recordMeanShearStress();
        String fileName = String.format("Rotor%s_%s_Shear.sim", getRotorDiameterString(), getRpmString());
        saveAs(fileName);
        runTime(samplingDurationSeconds);
    }

    private void saveAs(String simName) {
        Simulation simulation = getActiveSimulation();
        String SessionDirectory = simulation.getSessionDir();
        String fullPath = SessionDirectory + "/" + simName;

        try {
            simulation.saveState(fullPath);
        } catch (Exception ex) {
            simulation.println(ex);
        }
    }

    private String getRpmString() {
        Simulation simulation = getActiveSimulation();
        ScalarGlobalParameter sParameter = (ScalarGlobalParameter) simulation.get(GlobalParameterManager.class)
                .getObject("RPM");
        return sParameter.getQuantity().getDefinition().replace(" ", "").replace(".0", "") + "RPM";
    }

    private String getRotorDiameterString() {
        Simulation simulation = getActiveSimulation();
        ScalarGlobalParameter sParameter = (ScalarGlobalParameter) simulation.get(GlobalParameterManager.class)
                .getObject("RotorDiameter");
        double diameterMeters = sParameter.getQuantity().getSIValue();
        return String.format("%1.0fmm", diameterMeters * 1000);
    }

    private void runTime(double time) {
        Simulation simulation = getActiveSimulation();

        PhysicalTimeStoppingCriterion physicalTimeStoppingCriterion = ((PhysicalTimeStoppingCriterion) simulation
                .getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Physical Time"));

        double curTime = simulation.getSolution().getPhysicalTime();
        physicalTimeStoppingCriterion.getMaximumTime().setValue(curTime + time);

        simulation.getSimulationIterator().run();
    }

    private void recordMeanShearStress() {

        Simulation simulation_0 = getActiveSimulation();

        FieldMeanMonitor fieldMeanMonitor_0 = simulation_0.getMonitorManager().createMonitor(FieldMeanMonitor.class);

        fieldMeanMonitor_0.setPresentationName("Mean Shear Stress");

        PrimitiveFieldFunction primitiveFieldFunction_0 = ((PrimitiveFieldFunction) simulation_0
                .getFieldFunctionManager().getFunction("WallShearStress"));

        VectorMagnitudeFieldFunction vectorMagnitudeFieldFunction_0 = ((VectorMagnitudeFieldFunction) primitiveFieldFunction_0
                .getMagnitudeFunction());

        fieldMeanMonitor_0.setFieldFunction(vectorMagnitudeFieldFunction_0);

        fieldMeanMonitor_0.getParts().setQuery(null);

        Region region_0 = simulation_0.getRegionManager().getRegion("Fluid");

        Boundary boundary_0 = region_0.getBoundaryManager().getBoundary("Stator");

        fieldMeanMonitor_0.getParts().setObjects(boundary_0);
    }
}