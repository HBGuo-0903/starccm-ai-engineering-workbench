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

// Simulation file note:
// Reactive Flow Demo
public class MeshConvergence extends StarMacro {


        // Sim
        double ReynoldsNumber = __Reynolds__;
        double targetCourant = __targetCourant__;
        double K_adjustment = __reactivityAdjustment__;

        double R_fit_pA = 1.03E-4 * K_adjustment;
        double R_fit_pB = 0.00822 * K_adjustment;

        String TargetPhysicsContinuumName = "LES";

        // Fluid Properties
        double density = 997.561; // kg/m3
        double viscosity = 8.8871E-4; // Pa s

        
        // Mesh
        double RelMeshBaseSize = __MeshSize__; // - [dimensionless in terms of R_stator - R_rotor]


        // New FileName
        String uid = UUID.randomUUID().toString().substring(0, 5);
        String SimName = "Sim_MeshConvergence" + uid + ".sim";
        String SessionDirectory = "";

        int iterationUpdateFrequency = 5;
        int innerIterations = 1;
        double TimeToRun = __TimeToRun__;
        boolean IsSteady = false;

        public void execute() {
                SessionDirectory = getActiveSimulation().getSessionDir();
                StartMeshConvergence();
        }

        private void StartMeshConvergence() {

                SimName = "MeshConvergence_Re_" + ReynoldsNumber + "basesize_"+ RelMeshBaseSize + "_" + uid + ".sim";

                relinkUserlib("libuser.so", "libuser.so");
                setParameter("Re", ReynoldsNumber);
                setParameter("BaseSize", RelMeshBaseSize * 0.001);
                setParameter("R_fit_pA", R_fit_pA);
                setParameter("R_fit_pB", R_fit_pB);

                SetAdaptiveTimeStepping(targetCourant, targetCourant * 5);
                enableSecondOrderTimestep();
                setInnerIterationCount(innerIterations);

                // Save, prepare and run
                ExecuteMesh("Mesh");
                SetAutoSave();
                Save();
                RunTime(TimeToRun);
                ExportAllMonitors();

        }

        private void ExecuteMesh(String meshName) {

                Simulation simulation = getActiveSimulation();
            
                AutoMeshOperation autoMeshOperation = ((AutoMeshOperation) simulation.get(MeshOperationManager.class)
                    .getObject(meshName));
            
                autoMeshOperation.execute();
              }

        private void setParameter(String name, double value) {
                Simulation simulation = getActiveSimulation();
                ScalarGlobalParameter spar = ((ScalarGlobalParameter) simulation.get(GlobalParameterManager.class).getObject(name));
                spar.getQuantity().setValue(value);
              }

        private void Save() {
                Simulation simulation = getActiveSimulation();

                String fullPath = SessionDirectory + "/Results/" + SimName;

                try {
                        Files.createDirectories(Paths.get(SessionDirectory + "/Results"));
                        simulation.saveState(fullPath);
                } catch (Exception ex) {
                        simulation.println(ex);
                }
        }

        private void RecordOutletConcentrations(String Scalar) {

                Simulation simulation = getActiveSimulation();

                MassFlowAverageReport massFlowAverageReport = simulation.getReportManager()
                                .createReport(MassFlowAverageReport.class);

                FieldFunction primitiveFieldFunction =  simulation.getFieldFunctionManager().getFunction(Scalar);

                massFlowAverageReport.setFieldFunction(primitiveFieldFunction);

                massFlowAverageReport.setPresentationName("Mass Flow Averaged Outlet Concentration of " + Scalar);

                Region region = simulation.getRegionManager().getRegion("Fluid");
                Boundary boundary = region.getBoundaryManager().getBoundary("OutletA");
                massFlowAverageReport.getParts().setObjects(boundary);

                simulation.getMonitorManager().createMonitorAndPlot(
                                new NeoObjectVector(new Object[] { massFlowAverageReport }), true, "%1$s Plot");

                ReportMonitor reportMonitor = ((ReportMonitor) simulation.getMonitorManager()
                                .getMonitor("Mass Flow Averaged Outlet Concentration of " + Scalar + " Monitor"));

                MonitorPlot monitorPlot_1 = simulation.getPlotManager().createMonitorPlot(
                                new NeoObjectVector(new Object[] { reportMonitor }),
                                "Mass Flow Averaged Outlet Concentration of " + Scalar + " Monitor Plot");
        }

        private void freezeFlow() {

                Simulation simulation = getActiveSimulation();

                SegregatedFlowSolver segregatedFlowSolver = ((SegregatedFlowSolver) simulation.getSolverManager()
                                .getSolver(SegregatedFlowSolver.class));

                segregatedFlowSolver.setFreezeFlow(true);
        }

        private void InitializeSolution() {
                Simulation simulation = getActiveSimulation();
            
                Solution solution = simulation.getSolution();
            
                solution.initializeSolution();
                solution.initializeSolution();
              }

        private void freezeRst() {

                Simulation simulation = getActiveSimulation();

                EbRsTurbSolver ebRsTurbSolver = ((EbRsTurbSolver) simulation.getSolverManager()
                                .getSolver(EbRsTurbSolver.class));

                ebRsTurbSolver.setFrozen(true);
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

        private void SetAdaptiveTimeStepping(double getTargetMeanCfl, double getTargetMaxCfl) {

                Simulation simulation = getActiveSimulation();
                PhysicsContinuum physicsContinuum = ((PhysicsContinuum) simulation.getContinuumManager()
                                .getContinuum(TargetPhysicsContinuumName));
                //physicsContinuum.enable(AdaptiveTimeStepModel.class);

                AdaptiveTimeStepModel adaptiveTimeStepModel = physicsContinuum.getModelManager()
                                .getModel(AdaptiveTimeStepModel.class);


                ConvectiveCflTimeStepProvider convectiveCflTimeStepProvider = 
                ((ConvectiveCflTimeStepProvider) adaptiveTimeStepModel.getTimeStepProviderManager().getObject("Convective CFL Condition"));

                convectiveCflTimeStepProvider.getTargetMeanCfl().setValue(getTargetMeanCfl);
                convectiveCflTimeStepProvider.getTargetMaxCfl().setValue(getTargetMaxCfl);
        }

        private void setOutletBackflowConcentration(String[] scalars) {

                Simulation simulation = getActiveSimulation();
                Region region = simulation.getRegionManager().getRegion("Fluid");
                Boundary boundary = region.getBoundaryManager().getBoundary("OutletA");

                PassiveScalarProfile passiveScalarProfile = boundary.getValues().get(PassiveScalarProfile.class);
                passiveScalarProfile.setMethod(CompositeArrayProfileMethod.class);

                for (int i = 0; i < scalars.length; i++) {
                        ScalarProfile scalarProfile_i = passiveScalarProfile
                                        .getMethod(CompositeArrayProfileMethod.class).getProfile(i);
                        scalarProfile_i.getMethod(ConstantScalarProfileMethod.class).getQuantity().setDefinition(
                                        "${MassFlowAveragedOutletConcentrationof" + scalars[i] + "Report}");
                }

        }

        private void SetScalarInitialConcentration(int no, double concentration) {
                Simulation simulation = getActiveSimulation();
                PhysicsContinuum physicsContinuum = ((PhysicsContinuum) simulation.getContinuumManager()
                                .getContinuum(TargetPhysicsContinuumName));
                PassiveScalarProfile passiveScalarProfile = physicsContinuum.getInitialConditions()
                                .get(PassiveScalarProfile.class);
                passiveScalarProfile.setMethod(CompositeArrayProfileMethod.class);
                ScalarProfile scalarProfile = passiveScalarProfile.getMethod(CompositeArrayProfileMethod.class)
                                .getProfile(no);
                scalarProfile.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(concentration);
        }

        private void SetWallConcentration(int no, String FieldFunction) {

                Simulation simulation = getActiveSimulation();

                Region region = simulation.getRegionManager().getRegion("Fluid");

                Boundary boundary = region.getBoundaryManager().getBoundary("Stator");

                boundary.getConditions().get(WallPassiveScalarOption.class)
                                .setSelected(WallPassiveScalarOption.Type.SPECIFIED_SCALAR);

                PassiveScalarProfile passiveScalarProfile = boundary.getValues().get(PassiveScalarProfile.class);

                passiveScalarProfile.setMethod(CompositeArrayProfileMethod.class);

                ScalarProfile scalarProfile = passiveScalarProfile.getMethod(CompositeArrayProfileMethod.class)
                                .getProfile(0);

                scalarProfile.setMethod(FunctionScalarProfileMethod.class);

                PrimitiveFieldFunction primitiveFieldFunction = ((PrimitiveFieldFunction) simulation
                                .getFieldFunctionManager().getFunction("UserPitzerDiffModelWallmBa_2+"));

                scalarProfile.getMethod(FunctionScalarProfileMethod.class).setFieldFunction(primitiveFieldFunction);
        }

        private void SetScalarSchmidtNumber(String scalar, double Schmidt) {

                Simulation simulation = getActiveSimulation();

                PhysicsContinuum physicsContinuum = ((PhysicsContinuum) simulation.getContinuumManager()
                                .getContinuum(TargetPhysicsContinuumName));

                PassiveScalarModel passiveScalarModel = physicsContinuum.getModelManager()
                                .getModel(PassiveScalarModel.class);

                PassiveScalarMaterial passiveScalarMaterial = ((PassiveScalarMaterial) passiveScalarModel
                                .getPassiveScalarManager().getPassiveScalarMaterial(scalar));

                SchmidtNumberDiffusivityMethod schmidtNumberDiffusivityMethod = ((SchmidtNumberDiffusivityMethod) passiveScalarMaterial
                                .getMaterialProperties().getMaterialProperty(PassiveScalarDiffusivityProperty.class)
                                .getMethod());

                schmidtNumberDiffusivityMethod.setSchmidtNumber(Schmidt);
        }

        private void SetScalarTurbulentSchmidtNumber(String scalar, double Schmidt) {

                Simulation simulation = getActiveSimulation();

                PhysicsContinuum physicsContinuum = ((PhysicsContinuum) simulation.getContinuumManager()
                                .getContinuum(TargetPhysicsContinuumName));

                PassiveScalarModel passiveScalarModel = physicsContinuum.getModelManager()
                                .getModel(PassiveScalarModel.class);

                PassiveScalarMaterial passiveScalarMaterial = ((PassiveScalarMaterial) passiveScalarModel
                                .getPassiveScalarManager().getPassiveScalarMaterial(scalar));

                ConstantMaterialPropertyMethod constantMaterialPropertyMethod = ((ConstantMaterialPropertyMethod) passiveScalarMaterial
                                .getMaterialProperties()
                                .getMaterialProperty(PassiveScalarTurbulentSchmidtNumberProperty.class).getMethod());

                constantMaterialPropertyMethod.getQuantity().setValue(Schmidt);
        }

        private void ExportAllMonitors() {
                Simulation simulation = getActiveSimulation();

                try {
                        String SessionDirectory = simulation.getSessionDir();
                        Files.createDirectories(Paths.get(SessionDirectory + "/monitors"));

                        Collection<Monitor> monitors = simulation.getMonitorManager().getMonitors();

                        for (Monitor monitor : monitors) {
                                monitor.export(SessionDirectory + "/monitors/"
                                                + monitor.getPresentationName().replace(" ", "") + ".csv");
                        }
                } catch (Exception ex) {
                        simulation.println(ex);
                }

        }

        

        private void setLesCw(double Cw) {
                Simulation simulation = getActiveSimulation();
                PhysicsContinuum physicsContinuum = ((PhysicsContinuum) simulation.getContinuumManager()
                                .getContinuum(TargetPhysicsContinuumName));
                WaleSgsModel waleSgsModel = physicsContinuum.getModelManager().getModel(WaleSgsModel.class);
                waleSgsModel.setCw(Cw);
        }

        

        
        private void RunSimulation() {

                Simulation simulation = getActiveSimulation();
                simulation.getSimulationIterator().run();
        }

        private void RunTime(double time) {
                Simulation simulation = getActiveSimulation();

                PhysicalTimeStoppingCriterion physicalTimeStoppingCriterion = ((PhysicalTimeStoppingCriterion) simulation
                                .getSolverStoppingCriterionManager()
                                .getSolverStoppingCriterion("Maximum Physical Time"));

                double curTime = simulation.getSolution().getPhysicalTime();
                physicalTimeStoppingCriterion.getMaximumTime().setValue(curTime + time);

                RunSimulation();
        }

        private void setInnerIterationCount(int count) {
                Simulation simulation = getActiveSimulation();
                InnerIterationStoppingCriterion innerIterationStoppingCriterion = ((InnerIterationStoppingCriterion) simulation
                                .getSolverStoppingCriterionManager()
                                .getSolverStoppingCriterion("Maximum Inner Iterations"));
                innerIterationStoppingCriterion.setMaximumNumberInnerIterations(count);
        }

        private void enableSecondOrderTimestep() {
                Simulation simulation = getActiveSimulation();
                ImplicitUnsteadySolver implicitUnsteadySolver = ((ImplicitUnsteadySolver) simulation.getSolverManager()
                                .getSolver(ImplicitUnsteadySolver.class));
                implicitUnsteadySolver.getTimeDiscretizationOption()
                                .setSelected(TimeDiscretizationOption.Type.SECOND_ORDER);
        }

        private void AddUserLib(String libPath) {
                Simulation simulation = getActiveSimulation();
                UserLibrary userLibrary = simulation.getUserFunctionManager().createUserLibrary(resolvePath(libPath));
        }

        public void relinkUserlib(String libraryName, String path) {
                Simulation simulation = getActiveSimulation();
                UserLibrary userLibrary = simulation.getUserFunctionManager().getLibrary(libraryName);
                userLibrary.setLibraryName(path);
            }
        

        private void addIsoThermal(double TempC) {

                Simulation simulation = getActiveSimulation();

                PhysicsContinuum physicsContinuum = ((PhysicsContinuum) simulation.getContinuumManager()
                                .getContinuum(TargetPhysicsContinuumName));

                physicsContinuum.enable(SegregatedFluidIsothermalModel.class);

                SegregatedFluidIsothermalModel segregatedFluidIsothermalModel_0 = physicsContinuum.getModelManager()
                                .getModel(SegregatedFluidIsothermalModel.class);

                Units units_0 = ((Units) simulation.getUnitsManager().getObject("C"));

                segregatedFluidIsothermalModel_0.getContinuumTemperature().setUnits(units_0);

                segregatedFluidIsothermalModel_0.getContinuumTemperature().setValue(TempC);
        }

       

        private void SetPassiveScalarInlet(String inletName, double[] inletConcentrations) {

                Simulation simulation = getActiveSimulation();

                Region region = simulation.getRegionManager().getRegion("Fluid");

                Boundary boundary = region.getBoundaryManager().getBoundary(inletName);

                PassiveScalarProfile passiveScalarProfile = boundary.getValues().get(PassiveScalarProfile.class);

                passiveScalarProfile.setMethod(CompositeArrayProfileMethod.class);

                for (int i = 0; i < inletConcentrations.length; i++) {
                        ScalarProfile scalarProfile_0 = passiveScalarProfile
                                        .getMethod(CompositeArrayProfileMethod.class).getProfile(i);
                        scalarProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity()
                                        .setValue(inletConcentrations[i]);

                }

        }

        private void createDimensionlessFieldFuncs(String name, String definition) {

                Simulation simulation = getActiveSimulation();

                UserFieldFunction userFieldFunction = simulation.getFieldFunctionManager().createFieldFunction();

                userFieldFunction.getTypeOption().setSelected(FieldFunctionTypeOption.Type.SCALAR);

                userFieldFunction.setPresentationName(name);
                userFieldFunction.setFunctionName(name);
                userFieldFunction.setDefinition(definition);

        }

        
        private void AddSurfaceSideViewScene(String name, String fieldFunction, double Min, double Max) {

                Simulation simulation = getActiveSimulation();

                simulation.getSceneManager().createScalarScene("Scalar Scene", "Outline", "Scalar");
                Scene scene_0 = simulation.getSceneManager().getScene("Scalar Scene 1");
                scene_0.initializeAndWait();

                PartDisplayer partDisplayer_0 = ((PartDisplayer) scene_0.getDisplayerManager()
                                .getDisplayer("Outline 1"));
                partDisplayer_0.initialize();

                ScalarDisplayer scalarDisplayer_0 = ((ScalarDisplayer) scene_0.getDisplayerManager()
                                .getDisplayer("Scalar 1"));
                scalarDisplayer_0.initialize();

                Legend legend_0 = scalarDisplayer_0.getLegend();
                BlueRedLookupTable blueRedLookupTable_0 = ((BlueRedLookupTable) simulation.get(LookupTableManager.class)
                                .getObject("blue-red"));
                legend_0.setLookupTable(blueRedLookupTable_0);

                SceneUpdate sceneUpdate_0 = scene_0.getSceneUpdate();
                HardcopyProperties hardcopyProperties_0 = sceneUpdate_0.getHardcopyProperties();
                hardcopyProperties_0.setCurrentResolutionWidth(25);
                hardcopyProperties_0.setCurrentResolutionHeight(25);
                hardcopyProperties_0.setCurrentResolutionWidth(1024);
                hardcopyProperties_0.setCurrentResolutionHeight(494);

                scene_0.resetCamera();
                CurrentView currentView_0 = scene_0.getCurrentView();
                currentView_0.setInput(new DoubleVector(
                                new double[] { 1.552230566304047E-14, -8.213661445433118E-9, 0.07000000000000002 }),
                                new DoubleVector(new double[] { 1.552230566304047E-14, -8.213661445433118E-9,
                                                0.47714309349357603 }),
                                new DoubleVector(new double[] { 0.0, 1.0, 0.0 }), 0.10628567536481787, 1, 30.0);

                scene_0.setViewOrientation(new DoubleVector(new double[] { 0.0, 1.0, 0.0 }),
                                new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));
                scene_0.close();

                scene_0.setPresentationName(name);
                scene_0.setBackgroundColorMode(BackgroundColorMode.SOLID);
                LogoAnnotation logoAnnotation_0 = ((LogoAnnotation) simulation.getAnnotationManager()
                                .getObject("Logo"));
                scene_0.getAnnotationPropManager().removePropsForAnnotations(logoAnnotation_0);
                PhysicalTimeAnnotation physicalTimeAnnotation_0 = ((PhysicalTimeAnnotation) simulation
                                .getAnnotationManager().getObject("Solution Time"));
                PhysicalTimeAnnotationProp physicalTimeAnnotationProp_0 = (PhysicalTimeAnnotationProp) scene_0
                                .getAnnotationPropManager().createPropForAnnotation(physicalTimeAnnotation_0);

                sceneUpdate_0.getUpdateModeOption().setSelected(StarUpdateModeOption.Type.DELTATIME);
                DeltaTimeUpdateFrequency deltaTimeUpdateFrequency_0 = sceneUpdate_0.getDeltaTimeUpdateFrequency();
                Units units_0 = ((Units) simulation.getUnitsManager().getObject("s"));
                deltaTimeUpdateFrequency_0.setDeltaTime("0.02", units_0); // 50 FPS
                sceneUpdate_0.setAnimationFilenameBase(name.replace(' ', '_'));
                sceneUpdate_0.setAnimationFilePath(uid + "_" + name.replace(' ', '_'));
                sceneUpdate_0.setSaveAnimation(true);

                scalarDisplayer_0.getInputParts().setQuery(null);
                Region region_0 = simulation.getRegionManager().getRegion("Fluid");
                Boundary boundary_0 = region_0.getBoundaryManager().getBoundary("InletA");
                Boundary boundary_1 = region_0.getBoundaryManager().getBoundary("InletB");
                Boundary boundary_2 = region_0.getBoundaryManager().getBoundary("OutletA");
                Boundary boundary_3 = region_0.getBoundaryManager().getBoundary("OutletB");
                Boundary boundary_4 = region_0.getBoundaryManager().getBoundary("Rotor");
                Boundary boundary_5 = region_0.getBoundaryManager().getBoundary("Stator");
                scalarDisplayer_0.getInputParts().setObjects(boundary_0, boundary_1, boundary_2, boundary_3, boundary_4,
                                boundary_5);

                FieldFunction primitiveFieldFunction_3 = ((FieldFunction) simulation.getFieldFunctionManager()
                                .getFunction(fieldFunction));
                scalarDisplayer_0.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_3);
                scalarDisplayer_0.getScalarDisplayQuantity().setAutoRange(AutoRangeMode.NONE);
                scalarDisplayer_0.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { Min, Max }));
                scalarDisplayer_0.getScalarDisplayQuantity().setClip(ClipMode.NONE);
        }

        private void AddSideViewScene(String name, String fieldFunction, double Min, double Max) {

                Simulation simulation = getActiveSimulation();

                simulation.getSceneManager().createScalarScene("Scalar Scene", "Outline", "Scalar");

                Scene scene_0 = simulation.getSceneManager().getScene("Scalar Scene 1");

                scene_0.initializeAndWait();

                PartDisplayer partDisplayer_0 = ((PartDisplayer) scene_0.getDisplayerManager()
                                .getDisplayer("Outline 1"));

                partDisplayer_0.initialize();

                ScalarDisplayer scalarDisplayer_0 = ((ScalarDisplayer) scene_0.getDisplayerManager()
                                .getDisplayer("Scalar 1"));

                scalarDisplayer_0.initialize();

                Legend legend_0 = scalarDisplayer_0.getLegend();

                BlueRedLookupTable blueRedLookupTable_0 = ((BlueRedLookupTable) simulation.get(LookupTableManager.class)
                                .getObject("blue-red"));

                legend_0.setLookupTable(blueRedLookupTable_0);

                SceneUpdate sceneUpdate_0 = scene_0.getSceneUpdate();

                HardcopyProperties hardcopyProperties_0 = sceneUpdate_0.getHardcopyProperties();

                hardcopyProperties_0.setCurrentResolutionWidth(25);

                hardcopyProperties_0.setCurrentResolutionHeight(25);

                hardcopyProperties_0.setCurrentResolutionWidth(1024);

                hardcopyProperties_0.setCurrentResolutionHeight(494);

                scene_0.resetCamera();

                CurrentView currentView_0 = scene_0.getCurrentView();

                currentView_0.setInput(new DoubleVector(
                                new double[] { 1.552230566304047E-14, -8.213661445433118E-9, 0.07000000000000002 }),
                                new DoubleVector(new double[] { 1.552230566304047E-14, -8.213661445433118E-9,
                                                0.47714309349357603 }),
                                new DoubleVector(new double[] { 0.0, 1.0, 0.0 }), 0.10628567536481787, 1, 30.0);

                scene_0.setViewOrientation(new DoubleVector(new double[] { 0.0, 1.0, 0.0 }),
                                new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));

                scene_0.close();

                scene_0.setPresentationName(name);

                scene_0.setBackgroundColorMode(BackgroundColorMode.SOLID);

                LogoAnnotation logoAnnotation_0 = ((LogoAnnotation) simulation.getAnnotationManager()
                                .getObject("Logo"));

                scene_0.getAnnotationPropManager().removePropsForAnnotations(logoAnnotation_0);

                PhysicalTimeAnnotation physicalTimeAnnotation_0 = ((PhysicalTimeAnnotation) simulation
                                .getAnnotationManager().getObject("Solution Time"));

                PhysicalTimeAnnotationProp physicalTimeAnnotationProp_0 = (PhysicalTimeAnnotationProp) scene_0
                                .getAnnotationPropManager().createPropForAnnotation(physicalTimeAnnotation_0);

                sceneUpdate_0.getUpdateModeOption().setSelected(StarUpdateModeOption.Type.DELTATIME);

                DeltaTimeUpdateFrequency deltaTimeUpdateFrequency_0 = sceneUpdate_0.getDeltaTimeUpdateFrequency();
                Units units_0 = ((Units) simulation.getUnitsManager().getObject("s"));
                deltaTimeUpdateFrequency_0.setDeltaTime("0.02", units_0); // 50 FPS

                sceneUpdate_0.setAnimationFilenameBase(name.replace(' ', '_'));
                sceneUpdate_0.setAnimationFilePath(uid + "_" + name.replace(' ', '_'));
                sceneUpdate_0.setSaveAnimation(true);

                scalarDisplayer_0.getInputParts().setQuery(null);
                PlaneSection planeSection_0 = ((PlaneSection) simulation.getPartManager().getObject("XY Plane"));
                PlaneSection planeSection_1 = ((PlaneSection) simulation.getPartManager().getObject("XZ Plane"));
                PlaneSection planeSection_2 = ((PlaneSection) simulation.getPartManager().getObject("YZ Plane"));
                scalarDisplayer_0.getInputParts().setObjects(planeSection_0, planeSection_1, planeSection_2);

                FieldFunction primitiveFieldFunction_3 = ((FieldFunction) simulation.getFieldFunctionManager()
                                .getFunction(fieldFunction));
                scalarDisplayer_0.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_3);
                scalarDisplayer_0.getScalarDisplayQuantity().setAutoRange(AutoRangeMode.NONE);
                scalarDisplayer_0.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { Min, Max }));
                scalarDisplayer_0.getScalarDisplayQuantity().setClip(ClipMode.NONE);

        }

        

        private void setTimeStep(double deltat) {
                Simulation simulation = getActiveSimulation();

                PhysicsContinuum physicsContinuum = simulation.getContinuumManager()
                                .createContinuum(PhysicsContinuum.class);

                physicsContinuum.enable(ThreeDimensionalModel.class);

                ImplicitUnsteadySolver implicitUnsteadySolver = ((ImplicitUnsteadySolver) simulation.getSolverManager()
                                .getSolver(ImplicitUnsteadySolver.class));

                implicitUnsteadySolver.getTimeStep().setValue(deltat);

        }

        private void SetAutoSave() {

                Simulation simulation = getActiveSimulation();

                AutoSave autoSave = simulation.getSimulationIterator().getAutoSave();

                autoSave.setSeparator("_At_");
                autoSave.setFormatWidth(6);

                StarUpdate starUpdate = autoSave.getStarUpdate();

                starUpdate.setEnabled(true);
                if (IsSteady) {
                        starUpdate.getUpdateModeOption().setSelected(StarUpdateModeOption.Type.ITERATION);
                        IterationUpdateFrequency iterationUpdateFrequency = starUpdate.getIterationUpdateFrequency();
                        iterationUpdateFrequency.setIterations(5000);
                } else {
                        starUpdate.getUpdateModeOption().setSelected(StarUpdateModeOption.Type.TIMESTEP);
                        TimeStepUpdateFrequency timeStepUpdateFrequency = starUpdate.getTimeStepUpdateFrequency();
                        timeStepUpdateFrequency.setTimeSteps(1000);
                }
                autoSave.setMaxAutosavedFiles(2);
        }

        private void SetTransientStoppingCriteria(double physicalTime) {

                Simulation simulation = getActiveSimulation();

                PhysicalTimeStoppingCriterion physicalTimeStoppingCriterion = ((PhysicalTimeStoppingCriterion) simulation
                                .getSolverStoppingCriterionManager()
                                .getSolverStoppingCriterion("Maximum Physical Time"));

                physicalTimeStoppingCriterion.getMaximumTime().setValue(physicalTime);

                StepStoppingCriterion stepStoppingCriterion = ((StepStoppingCriterion) simulation
                                .getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps"));

                stepStoppingCriterion.setIsUsed(false);
        }

        private void CreateGlobalDimensionlessParameter(String name, String definition) {
                Simulation simulation = getActiveSimulation();
                ScalarGlobalParameter sParameter = (ScalarGlobalParameter) simulation.get(GlobalParameterManager.class)
                                .createGlobalParameter(ScalarGlobalParameter.class, name);

                sParameter.getQuantity().setDefinition(definition);
        }

        private void SetMassFlowInletFlowRate(String BoundaryName, String RegionName, double massFlowSi) {

                Simulation simulation = getActiveSimulation();

                Region region = simulation.getRegionManager().getRegion(RegionName);

                Boundary boundary = region.getBoundaryManager().getBoundary(BoundaryName);

                MassFlowRateProfile massFlowRateProfile = boundary.getValues().get(MassFlowRateProfile.class);

                massFlowRateProfile.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(massFlowSi);
        }


        private void SetPassiveScalarWallFlux(int scalarInd, String fluxFieldFunc, String FluxDerivativeFieldFunc) {

                Simulation simulation = getActiveSimulation();
                Region region = simulation.getRegionManager().getRegion("Fluid");
                Boundary boundary = region.getBoundaryManager().getBoundary("Stator");
            
                boundary.getConditions().get(WallPassiveScalarOption.class).setSelected(WallPassiveScalarOption.Type.SPECIFIED_FLUX);
                boundary.getConditions().get(WallPassiveScalarFluxDerivativeOption.class).setSelected(WallPassiveScalarFluxDerivativeOption.Type.SPECIFIED);
            
                PassiveScalarFluxProfile passiveScalarFluxProfile = boundary.getValues().get(PassiveScalarFluxProfile.class);
                passiveScalarFluxProfile.setMethod(CompositeArrayProfileMethod.class);
               
                PassiveScalarFluxDerivativeProfile passiveScalarFluxDerivativeProfile = boundary.getValues().get(PassiveScalarFluxDerivativeProfile.class);
                passiveScalarFluxDerivativeProfile.setMethod(CompositeArrayProfileMethod.class);

                ScalarProfile scalarProfile = passiveScalarFluxProfile.getMethod(CompositeArrayProfileMethod.class).getProfile(scalarInd);
                scalarProfile.setMethod(FunctionScalarProfileMethod.class);
                UserFieldFunction fluxFieldFunction = ((UserFieldFunction) simulation.getFieldFunctionManager().getFunction(fluxFieldFunc));
                scalarProfile.getMethod(FunctionScalarProfileMethod.class).setFieldFunction(fluxFieldFunction);
            
                ScalarProfile scalarProfile_1 = passiveScalarFluxDerivativeProfile.getMethod(CompositeArrayProfileMethod.class).getProfile(scalarInd);
                scalarProfile_1.setMethod(FunctionScalarProfileMethod.class);
                UserFieldFunction FluxDerivativeFieldFunction = ((UserFieldFunction) simulation.getFieldFunctionManager().getFunction(FluxDerivativeFieldFunc));   
                scalarProfile_1.getMethod(FunctionScalarProfileMethod.class).setFieldFunction(FluxDerivativeFieldFunction);
              }


        private void SetPassiveScalarWallMassSource(int scalarInd, String sourceFieldFunc, String sourceDerivativeFieldFunc) {

                Simulation simulation = getActiveSimulation();
            
                Region region = simulation.getRegionManager().getRegion("Fluid");
            
                region.getConditions().get(PassiveScalarUserSourceOption.class).setSelected(PassiveScalarUserSourceOption.Type.EXPLICIT_DENSITY);
            
                PassiveScalarUserSource passiveScalarUserSource = region.getValues().get(PassiveScalarUserSource.class);
            
                passiveScalarUserSource.setMethod(CompositeArrayProfileMethod.class);
            
                ScalarProfile scalarProfile = passiveScalarUserSource.getMethod(CompositeArrayProfileMethod.class).getProfile(scalarInd);
                scalarProfile.setMethod(FunctionScalarProfileMethod.class);
                UserFieldFunction userFieldFunction = ((UserFieldFunction) simulation.getFieldFunctionManager().getFunction(sourceFieldFunc));
                scalarProfile.getMethod(FunctionScalarProfileMethod.class).setFieldFunction(userFieldFunction);
                PassiveScalarUserSourceDerivative passiveScalarUserSourceDerivative = region.getValues().get(PassiveScalarUserSourceDerivative.class);

                passiveScalarUserSourceDerivative.setMethod(CompositeArrayProfileMethod.class);

                ScalarProfile scalarProfile_1 = passiveScalarUserSourceDerivative.getMethod(CompositeArrayProfileMethod.class).getProfile(scalarInd);
                scalarProfile_1.setMethod(FunctionScalarProfileMethod.class);
                UserFieldFunction userFieldFunction_1 = ((UserFieldFunction) simulation.getFieldFunctionManager().getFunction(sourceDerivativeFieldFunc));
                scalarProfile_1.getMethod(FunctionScalarProfileMethod.class).setFieldFunction(userFieldFunction_1);
              }
}
