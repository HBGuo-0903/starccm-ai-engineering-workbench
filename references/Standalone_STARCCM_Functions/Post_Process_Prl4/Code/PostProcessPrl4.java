// Simcenter STAR-CCM+ macro: PostProcessPrl4.java
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

public class PostProcessPrl4 extends StarMacro {

    public void execute() {

        double k1 = 0;
        double delta_t = 0.001;
        double timeToRun = 0.1;

        Simulation simulation = getActiveSimulation();
        Region region = simulation.getRegionManager().getRegion("Fluid");
        Boundary boundary = region.getBoundaryManager().getBoundary("Stator");

        relinkUserlib("libuser.so", "libuser.so");

        AddBariteDiffusivity();

        createDimensionlessFieldFuncs("BariteDepositionRate", "${BariteScale} * 1.0");

        SetBariteFlux(simulation, "Fluid", "Stator", "BariteDepositionRate", 1.0);

        createDimensionlessFieldFuncs("TotalDeposition", "0 - ${BariteScaleBoundaryFlux} + ${mBa_2+BoundaryFlux}");

        MonitorBoundaryFlux(simulation, "Fluid", "Stator", "TotalDeposition", "DepositionRate");

        createDepositionScene("DepositionRateMonitor");
        setParameter("k1", k1);

        setTimeStep(delta_t);
        RunTime(timeToRun);
    }

    public void setTimeStep(double deltat) {
        Simulation simulation = getActiveSimulation();

        PhysicsContinuum physicsContinuum = simulation.getContinuumManager()
                        .createContinuum(PhysicsContinuum.class);

        physicsContinuum.enable(ThreeDimensionalModel.class);

        ImplicitUnsteadySolver implicitUnsteadySolver = ((ImplicitUnsteadySolver) simulation.getSolverManager()
                        .getSolver(ImplicitUnsteadySolver.class));

        implicitUnsteadySolver.getTimeStep().setValue(deltat);

    }

    private void setParameter(String parameterName, double value) {

        Simulation simulation_0 = 
          getActiveSimulation();
    
        ScalarGlobalParameter scalarGlobalParameter_0 = 
          ((ScalarGlobalParameter) simulation_0.get(GlobalParameterManager.class).getObject(parameterName));
    
        scalarGlobalParameter_0.getQuantity().setValue(value);
    
        Units units_0 = 
          ((Units) simulation_0.getUnitsManager().getObject(""));
    
        scalarGlobalParameter_0.getQuantity().setUnits(units_0);
      }

    public void relinkUserlib(String libraryName, String path) {
        Simulation simulation = getActiveSimulation();
        UserLibrary userLibrary = simulation.getUserFunctionManager().getLibrary(libraryName);
        userLibrary.setLibraryName(path);
    }

    public void createDimensionlessFieldFuncs(String name, String definition) {

        Simulation simulation = getActiveSimulation();

        UserFieldFunction userFieldFunction = simulation.getFieldFunctionManager().createFieldFunction();

        userFieldFunction.getTypeOption().setSelected(FieldFunctionTypeOption.Type.SCALAR);

        userFieldFunction.setPresentationName(name);
        userFieldFunction.setFunctionName(name);
        userFieldFunction.setDefinition(definition);

    }

    public void RunTime(double time) {
        Simulation simulation = getActiveSimulation();

        PhysicalTimeStoppingCriterion physicalTimeStoppingCriterion = ((PhysicalTimeStoppingCriterion) simulation
                .getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Physical Time"));

        double curTime = simulation.getSolution().getPhysicalTime();
        physicalTimeStoppingCriterion.getMaximumTime().setValue(curTime + time);

        simulation.getSimulationIterator().run();
    }

    public void MonitorBoundaryFlux(Simulation simulation, String regionName, String boundaryName,
            String fieldFunctionName, String monitorName) {
        Region region = simulation.getRegionManager().getRegion(regionName);
        Boundary boundary = region.getBoundaryManager().getBoundary(boundaryName);
        FieldMeanMonitor fieldMeanMonitor = simulation.getMonitorManager().createMonitor(FieldMeanMonitor.class);
        fieldMeanMonitor.getParts().setQuery(null);
        fieldMeanMonitor.getParts().setObjects(boundary);
        FieldFunction fieldFunction = simulation.getFieldFunctionManager().getFunction(fieldFunctionName);

        fieldMeanMonitor.setFieldFunction(fieldFunction);
        fieldMeanMonitor.setPresentationName(monitorName);
    }

    public void SetBariteFlux(Simulation simulation, String regionName, String boundaryName, String fieldFunctionName,
            double fluxDerivative) {

        Region region = simulation.getRegionManager().getRegion(regionName);
        Boundary boundary = region.getBoundaryManager().getBoundary(boundaryName);

        PassiveScalarFluxProfile passiveScalarFluxProfile = boundary.getValues().get(PassiveScalarFluxProfile.class);

        ScalarProfile scalarProfile = passiveScalarFluxProfile.getMethod(CompositeArrayProfileMethod.class)
                .getProfile(4);

        scalarProfile.setMethod(FunctionScalarProfileMethod.class);
        FieldFunction fieldFunction = simulation.getFieldFunctionManager().getFunction(fieldFunctionName);
        scalarProfile.getMethod(FunctionScalarProfileMethod.class).setFieldFunction(fieldFunction);

        PassiveScalarFluxDerivativeProfile passiveScalarFluxDerivativeProfile = boundary.getValues()
                .get(PassiveScalarFluxDerivativeProfile.class);
        ScalarProfile scalarProfile_1 = passiveScalarFluxDerivativeProfile.getMethod(CompositeArrayProfileMethod.class)
                .getProfile(4);
        scalarProfile_1.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(fluxDerivative);

        Units units = ((Units) simulation.getUnitsManager().getObject("/m^2-s"));

        scalarProfile_1.getMethod(ConstantScalarProfileMethod.class).getQuantity().setUnits(units);

    }

    public void AddBariteDiffusivity() {

        Simulation simulation_0 = getActiveSimulation();

        PhysicsContinuum physicsContinuum_0 = ((PhysicsContinuum) simulation_0.getContinuumManager()
                .getContinuum("Physics 1"));

        PassiveScalarModel passiveScalarModel_0 = physicsContinuum_0.getModelManager()
                .getModel(PassiveScalarModel.class);

        PassiveScalarMaterial passiveScalarMaterial_0 = ((PassiveScalarMaterial) passiveScalarModel_0
                .getPassiveScalarManager().getPassiveScalarMaterial("BariteScale"));

        passiveScalarMaterial_0.getTransportOption()
                .setSelected(PassiveScalarTransportOption.Type.CONVECTION_DIFFUSION);

        SchmidtNumberDiffusivityMethod schmidtNumberDiffusivityMethod_0 = ((SchmidtNumberDiffusivityMethod) passiveScalarMaterial_0
                .getMaterialProperties().getMaterialProperty(PassiveScalarDiffusivityProperty.class).getMethod());

        schmidtNumberDiffusivityMethod_0.setSchmidtNumber(100.0);
    }

    public void createDepositionScene(String fieldFunctionName) {

        Simulation simulation_0 = getActiveSimulation();

        Scene scene_1 = simulation_0.getSceneManager().createScene();

        scene_1.initializeAndWait();

        ScalarDisplayer scalarDisplayer_1 = ((ScalarDisplayer) scene_1.getDisplayerManager()
                .createScalarDisplayer​("DepositionScene"));

        scalarDisplayer_1.initialize();

        Legend legend_1 = scalarDisplayer_1.getLegend();

        BlueRedLookupTable blueRedLookupTable_0 = ((BlueRedLookupTable) simulation_0.get(LookupTableManager.class)
                .getObject("blue-red"));

        legend_1.setLookupTable(blueRedLookupTable_0);

        SceneUpdate sceneUpdate_1 = scene_1.getSceneUpdate();

        HardcopyProperties hardcopyProperties_1 = sceneUpdate_1.getHardcopyProperties();

        hardcopyProperties_1.setCurrentResolutionWidth(25);

        hardcopyProperties_1.setCurrentResolutionHeight(25);

        hardcopyProperties_1.setCurrentResolutionWidth(1405);

        hardcopyProperties_1.setCurrentResolutionHeight(363);

        scene_1.resetCamera();

        scene_1.setPresentationName("Deposition");

        scalarDisplayer_1.getInputParts().setQuery(null);

        Region region_0 = simulation_0.getRegionManager().getRegion("Fluid");

        Boundary boundary_0 = region_0.getBoundaryManager().getBoundary("Stator");

        scalarDisplayer_1.getInputParts().setObjects(boundary_0);

        scene_1.setBackgroundColorMode(BackgroundColorMode.SOLID);

        CurrentView currentView_0 = scene_1.getCurrentView();

        currentView_0.setInput(
                new DoubleVector(new double[] { 1.4849862783883339E-8, 3.3893392259454203E-9, 0.07000000000000003 }),
                new DoubleVector(new double[] { 1.4849862783883339E-8, 3.3893392259454203E-9, 0.5400712503594618 }),
                new DoubleVector(new double[] { 0.0, 1.0, 0.0 }), 0.12166339214818385, 0, 30.0);

        scene_1.setViewOrientation(new DoubleVector(new double[] { 0.0, -1.0, 0.0 }),
                new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));

        FieldFunction primitiveFieldFunction_2 = (simulation_0.getFieldFunctionManager()
                .getFunction(fieldFunctionName));

        scalarDisplayer_1.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_2);

        LogoAnnotation logoAnnotation_0 = ((LogoAnnotation) simulation_0.getAnnotationManager().getObject("Logo"));

        scene_1.getAnnotationPropManager().removePropsForAnnotations(logoAnnotation_0);

        PhysicalTimeAnnotation physicalTimeAnnotation_0 = ((PhysicalTimeAnnotation) simulation_0.getAnnotationManager()
                .getObject("Solution Time"));

        PhysicalTimeAnnotationProp physicalTimeAnnotationProp_0 = (PhysicalTimeAnnotationProp) scene_1
                .getAnnotationPropManager().createPropForAnnotation(physicalTimeAnnotation_0);

        scene_1.close();
    }

}
