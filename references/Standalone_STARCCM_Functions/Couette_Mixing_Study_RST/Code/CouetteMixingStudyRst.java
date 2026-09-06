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
// Mixing validation Run7 - Non-reactive species 500RPM
public class CouetteMixingStudyRst extends StarMacro {

    // Length units in [m]
    double RotorDiameter = 80e-3; // Rotor1 = 80mm, Rotor2 = 50mm
    double RotorHeight = 138e-3;
    double StatorDiameter = 98e-3;
    double StatorHeight = 140e-3;
    double RotorStartZ = 1e-3;
    double InletHoleDiameter = 5e-3;
    double InletHoleRadialPosition = 41e-3;
    double TubeLength = 20e-3;
    double ShaftDiameter = 10e-3;

    // RPM Units [1/min]
    double RPM = 500;

    // Flow rate
    double flowRate = 8.3 / 2.0 / 60.0 * 1e-6; // m3/s
    double massFlowRate = flowRate * 997.561;

    // Concentration
    double concentrationNaSO4 = 1.19e-2; // mol/kgw
    double concentrationBaCl2 = 0; // mol/kgw
    String[] scalars = { "mSO4_2-", "mNa_1+" };

    // Temperature
    double Temperature = 25; // C

    // Mesh
    double RelMeshBaseSize = 10; // - [dimensionless in terms of R_stator - R_rotor]
    double refinementRelSize = 0.01; // -
    boolean thinMesher = false;
    boolean parallelMesher = false;
    boolean curvatureControl = true;
    int NoPrismLayers = 10; // -
    int NoThinLayers = 10; // -
    double PrimLayerTotalThickness = 5e-4; // m
    double PrismLayerStretching = 1.5; // -

    // Time-Stepping
    boolean IsSteady = true;
    double targetCourant = 20; // adjusting timestep by RPM, RotorDiameter and MeshSize
    double TimeStep = targetCourant * 1e-3 * (500.0 / RPM) * (RelMeshBaseSize / 20.0) * (80.0e-3 / RotorDiameter); // s

    //targetCourant * 1e-6 * (80*100*Pi*2*500.0/(60*20))* (delta)  / (Omega * RotorDiameter)

    // TurbulenceModelling
    String TurbulenceModel = "RANS-RST"; // "RANS-kOmega" / "LES" / "Laminar" / "RANS-RST" / "RANS-kEpsilon"
    String kOmegaConsituitive = "Linear"; // "Cubic" / "Linear" / "QCR"
    boolean addCurvatureCorrection = true;
    boolean addNormalStressTerm = false;

    // New FileName
    String uid = UUID.randomUUID().toString().substring(0, 5);
    String SimName = "Sim_" + TurbulenceModel + "_" + uid + ".sim";
    String SessionDirectory = "";

    double molarMassH2O = 18.01528e-3; // kg / mole
    double molarMassBarite = 233.39e-3; // kg / mole
    double DensityBarite = 4480; // kg / mole

    int InitialRansSteps = 20000;
    double PureWaterPhysicalTime = 2.5;
    double TotalPhysialTime = 5002.5; // s
    int iterationUpdateFrequency = 5;
    boolean continuityInitialization = true;

    // calclated
    double MeshBaseSize, R1, R2, d; // m

    public void execute() {
        Simulation simulation = getActiveSimulation();
        SessionDirectory = simulation.getSessionDir();
        StartMixingSimulation();
        
        new StarScript(getActiveRootObject(), new File(resolvePath("RecordMixing.java"))).play();
        new StarScript(getActiveRootObject(), new File(resolvePath("SetAdaptiveTimeStep.java"))).play();
        new StarScript(getActiveRootObject(), new File(resolvePath("SetMassFlowInlet.java"))).play();
        new StarScript(getActiveRootObject(), new File(resolvePath("SetOutletBackflowConcentration.java"))).play();

        RunTime(0.12);
        freezeFlow();
        RunTime(1500);
        
        
        
    }

    private void StartFlowSimulation() {
        SetAutoSave();
        InitializeSolution();

        ResetScenesAndTablesOnRemesh();
        setContinuityInitialization(continuityInitialization);
        RunSteps(InitialRansSteps);
    }

    private void StartMixingSimulation() {

        swapToUnsteady();
        SimName = "Sim_" + TurbulenceModel + "_MIXING_RST_" + uid + ".sim";

        setTimeStep(TimeStep);
        SetTransientStoppingCriteria(TotalPhysialTime);

        SetAutoSave();

        EnableSpecies();

        Save();

    }

    private void freezeFlow() {

        Simulation simulation = 
          getActiveSimulation();
    
        SegregatedFlowSolver segregatedFlowSolver = 
          ((SegregatedFlowSolver) simulation.getSolverManager().getSolver(SegregatedFlowSolver.class));
    
        segregatedFlowSolver.setFreezeFlow(true);
      }

    private void WriteConfig() {
        try {
            PrintWriter writer = new PrintWriter("Sim_" + TurbulenceModel + "_TORQUE_" + uid, "UTF-8");
            writer.println("Mesh Base Size :" + RelMeshBaseSize);
            writer.println("Turbulence Model: " + TurbulenceModel);
            if (TurbulenceModel == "RANS-kOmega") {
                writer.println("kOmegaConsituitive: " + kOmegaConsituitive);
                writer.println("addCurvatureCorrection: " + addCurvatureCorrection);
                writer.println("addNormalStressTerm: " + addNormalStressTerm);

            }
            writer.close();
        } catch (Exception ex) {
            Simulation simulation = getActiveSimulation();
            simulation.println(ex);
        } finally {

        }
    }

    private void setContinuityInitialization(boolean active) {

        Simulation simulation = getActiveSimulation();

        SegregatedFlowSolver segregatedFlowSolver = ((SegregatedFlowSolver) simulation.getSolverManager()
                .getSolver(SegregatedFlowSolver.class));

        segregatedFlowSolver.setContinuityInitialization(true);
    }

    private void StartTorqueSimulation() {

        SimName = "Sim_" + TurbulenceModel + "_TORQUE_" + uid + ".sim";

        swapToUnsteady();
        setTimeStep(TimeStep);
        SetTransientStoppingCriteria(TotalPhysialTime);
        CreateTimingPlots();

        SetAutoSave();

        Save();
        RunSimulation();
    }

    private void StartTorqueLesSimulation() {

        SimName = "Sim_" + TurbulenceModel + "_TORQUE_" + uid + ".sim";

        SwapToLes();
        setTimeStep(TimeStep);
        SetTransientStoppingCriteria(TotalPhysialTime);
        CreateTimingPlots();

        SetAutoSave();
        // AddSurfaceSideViewScene("Wall Shear Stress", "WallShearStress", 0, 150);

        Save();
        RunSimulation();
    }

    private void InitializeSolution() {
        Simulation simulation = getActiveSimulation();

        Solution solution = simulation.getSolution();

        solution.initializeSolution();
        solution.initializeSolution();
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

    private void ExportAllMonitors() {
        Simulation simulation = getActiveSimulation();

        try {
            String SessionDirectory = simulation.getSessionDir();
            Files.createDirectories(Paths.get(SessionDirectory + "/monitors"));

            Collection<Monitor> monitors = simulation.getMonitorManager().getMonitors();

            for (Monitor monitor : monitors) {
                monitor.export(
                        SessionDirectory + "/monitors/" + monitor.getPresentationName().replace(" ", "") + ".csv");
            }
        } catch (Exception ex) {
            simulation.println(ex);
        }

    }

    private void SwapToLes() {

        Simulation simulation = getActiveSimulation();
        PhysicsContinuum physicsContinuum = ((PhysicsContinuum) simulation.getContinuumManager()
                .getContinuum("Physics 1"));

        if (IsSteady) {
            SteadyModel steadyModel_0 = physicsContinuum.getModelManager().getModel(SteadyModel.class);
            physicsContinuum.disableModel(steadyModel_0);
            physicsContinuum.enable(ImplicitUnsteadyModel.class);
            IsSteady = false;
        }

        ImplicitUnsteadySolver implicitUnsteadySolver = ((ImplicitUnsteadySolver) simulation.getSolverManager()
                .getSolver(ImplicitUnsteadySolver.class));

        implicitUnsteadySolver.getTimeStep().setValue(TimeStep);

        if (TurbulenceModel == "RANS-kOmega") {
            physicsContinuum.disableModel(physicsContinuum.getModelManager().getModel(TurbulentModel.class));
            physicsContinuum.disableModel(physicsContinuum.getModelManager().getModel(RansTurbulenceModel.class));
            physicsContinuum.disableModel(physicsContinuum.getModelManager().getModel(KOmegaTurbulence.class));
            physicsContinuum.disableModel(physicsContinuum.getModelManager().getModel(SstKwTurbModel.class));
            physicsContinuum.disableModel(physicsContinuum.getModelManager().getModel(KwAllYplusWallTreatment.class));
            physicsContinuum.disableModel(physicsContinuum.getModelManager().getModel(GammaTransitionModel.class));

        } else if (TurbulenceModel == "RANS-RST") {
            physicsContinuum.disableModel(physicsContinuum.getModelManager().getModel(RansTurbulenceModel.class));
            physicsContinuum.disableModel(physicsContinuum.getModelManager().getModel(ReynoldsStressTurbulence.class));
            physicsContinuum.disableModel(physicsContinuum.getModelManager().getModel(EbRsTurbModel.class));
            physicsContinuum.disableModel(physicsContinuum.getModelManager().getModel(EbRsAllYplusWallTreatment.class));

        } else if (TurbulenceModel == "RANS-kEpsilon") {
            physicsContinuum.disableModel(physicsContinuum.getModelManager().getModel(RansTurbulenceModel.class));
            physicsContinuum.disableModel(physicsContinuum.getModelManager().getModel(KEpsilonTurbulence.class));
            physicsContinuum.disableModel(physicsContinuum.getModelManager().getModel(RkeTwoLayerTurbModel.class));
            physicsContinuum
                    .disableModel(physicsContinuum.getModelManager().getModel(KeTwoLayerAllYplusWallTreatment.class));
        } else if (TurbulenceModel == "LES") {

        } else {
            physicsContinuum.disableModel(physicsContinuum.getModelManager().getModel(LaminarModel.class));
        }

        TurbulenceModel = "LES";
        physicsContinuum.enable(TurbulentModel.class);
        physicsContinuum.enable(LesTurbulenceModel.class);
        physicsContinuum.enable(WaleSgsModel.class);
        physicsContinuum.enable(LesAllYplusWallTreatment.class);
    }

    private void CreateSpeciesFieldFunctions() {

        CreateGlobalDimensionlessParameter("MolarMassWater", "" + molarMassH2O);
        CreateGlobalDimensionlessParameter("MolarMassBarite", "" + molarMassBarite);
        CreateGlobalDimensionlessParameter("DensityBarite", "" + DensityBarite);

        createDimensionlessFieldFuncs("mEtc_1-", "${mCl_1-} + ${mNa_1+}");
        createDimensionlessFieldFuncs("mEtc_2-", "${mSO4_2-} + ${mBa_2+}");

        createDimensionlessFieldFuncs("mTot", "1.0 / ${MolarMassWater} + ${mEtc_1-} + ${mEtc_2-}");

        createDimensionlessFieldFuncs("yEtc_1-", "${mEtc_1-} / ${mTot}");
        createDimensionlessFieldFuncs("yEtc_2-", "${mEtc_2-} / ${mTot}");

        for (int i = 0; i < scalars.length; i++) {
            String mName = scalars[i];
            String yName = mName.replaceFirst("m", "y");
            String definition = "${" + mName + "} / ${mTot}";
            createDimensionlessFieldFuncs(yName, definition);
        }

        createDimensionlessFieldFuncs("dBaSO4dt", "${UserDebyeHuckelWallDeposition(Molality)} * ${Density}");
        createDimensionlessFieldFuncs("dydt", "-${UserDebyeHuckelWallDeposition(MoleFraction)} / ${Volume}");
        createDimensionlessFieldFuncs("dBadt", "-${dBaSO4dt}");
        createDimensionlessFieldFuncs("dSO4dt", "-${dBaSO4dt}");

        createDimensionlessFieldFuncs("BariteVolumeFraction",
                "${BariteScale} * ${Density} * ${MolarMassBarite} / ${DensityBarite}");

    }

    private void EnableSpecies() {
        // Totals:

        // Inlet B
        double mNa2SO4_inletA = concentrationNaSO4;
        double nu_Na = 2;
        double nu_SO4 = 1;
        double nu_Na2SO4 = 1;
        double mNa_inletA = (nu_Na / nu_Na2SO4) * mNa2SO4_inletA;
        double mSO4_inletA = (nu_SO4 / nu_Na2SO4) * mNa2SO4_inletA;

        double[] inletAConcentrations = { mSO4_inletA, mNa_inletA };
        double[] inletBConcentrations = { 0.0, 0.0 };

        AddPassiveScalar(scalars);
        SetPassiveScalarInlet("InletA", inletAConcentrations);
        SetPassiveScalarInlet("InletB", inletBConcentrations);
    }

    private void RunSimulation() {

        Simulation simulation = getActiveSimulation();
        simulation.getSimulationIterator().run();
    }

    private void RunSteps(int steps) {
        Simulation simulation = getActiveSimulation();

        Solution solution = simulation.getSolution();

        simulation.getSimulationIterator().run(steps);
    }

    private void RunTime(double time) {
        Simulation simulation = getActiveSimulation();

        PhysicalTimeStoppingCriterion physicalTimeStoppingCriterion = ((PhysicalTimeStoppingCriterion) simulation
                .getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Physical Time"));

        double curTime = simulation.getSolution().getPhysicalTime();
        physicalTimeStoppingCriterion.getMaximumTime().setDefinition(Double.toString(curTime + time));

        RunSimulation();
    }

    private void AddUserLib(String libPath) {

        Simulation simulation = getActiveSimulation();

        UserLibrary userLibrary_1 = simulation.getUserFunctionManager().createUserLibrary(resolvePath(libPath));

    }

    private void ResetScenesAndTablesOnRemesh() {

        Simulation simulation = getActiveSimulation();

        FvRepresentation fvRepresentation = ((FvRepresentation) simulation.getRepresentationManager()
                .getObject("Volume Mesh"));

        simulation.getTableManager().applyRepresentation(fvRepresentation);
        simulation.getSceneManager().applyRepresentation(fvRepresentation);
        simulation.getReportManager().applyRepresentation(fvRepresentation);

    }

    private void EnableReactionSourceTerms() {
        Simulation simulation = getActiveSimulation();
        Region region = simulation.getRegionManager().getRegion("Fluid");

        region.getConditions().get(PassiveScalarUserSourceOption.class)
                .setSelected(PassiveScalarUserSourceOption.Type.EXPLICIT_DENSITY);

        PassiveScalarUserSource passiveScalarUserSource = region.getValues().get(PassiveScalarUserSource.class);
        passiveScalarUserSource.setMethod(CompositeArrayProfileMethod.class);

        ScalarProfile productProfile = passiveScalarUserSource.getMethod(CompositeArrayProfileMethod.class)
                .getProfile(4);
        UserFieldFunction productFieldFunction = ((UserFieldFunction) simulation.getFieldFunctionManager()
                .getFunction("dBaSO4dt"));
        productProfile.getMethod(FunctionScalarProfileMethod.class).setFieldFunction(productFieldFunction);

        ScalarProfile reactantAProfile = passiveScalarUserSource.getMethod(CompositeArrayProfileMethod.class)
                .getProfile(0);
        UserFieldFunction reactantAFieldFunction = ((UserFieldFunction) simulation.getFieldFunctionManager()
                .getFunction("dBadt"));
        reactantAProfile.getMethod(FunctionScalarProfileMethod.class).setFieldFunction(reactantAFieldFunction);

        ScalarProfile reactantBProfile = passiveScalarUserSource.getMethod(CompositeArrayProfileMethod.class)
                .getProfile(1);
        UserFieldFunction reactantBFieldFunction = ((UserFieldFunction) simulation.getFieldFunctionManager()
                .getFunction("dSO4dt"));
        reactantBProfile.getMethod(FunctionScalarProfileMethod.class).setFieldFunction(reactantBFieldFunction);
    }

    private void AddPassiveScalar(String[] scalars) {

        Simulation simulation = getActiveSimulation();
        PhysicsContinuum physicsContinuum = ((PhysicsContinuum) simulation.getContinuumManager()
                .getContinuum("Physics 1"));
        physicsContinuum.enable(PassiveScalarModel.class);

        for (String scalar : scalars) {
            PassiveScalarModel passiveScalarModel = physicsContinuum.getModelManager()
                    .getModel(PassiveScalarModel.class);

            PassiveScalarMaterial passiveScalarMaterial = passiveScalarModel.getPassiveScalarManager()
                    .createPassiveScalarMaterial(PassiveScalarMaterial.class);

            passiveScalarMaterial.getTransportOption().setSelected(PassiveScalarTransportOption.Type.CONVECTION_ONLY);

            passiveScalarMaterial.getClipMode().setSelected(PassiveScalarClipMode.Type.CLIP_BOTH);

            passiveScalarMaterial.setMaxAllowable(1.0);

            passiveScalarMaterial.setPresentationName(scalar);
        }
    }

    private void SetPassiveScalarInlet(String inletName, double[] inletConcentrations) {

        Simulation simulation = getActiveSimulation();

        Region region = simulation.getRegionManager().getRegion("Fluid");

        Boundary boundary = region.getBoundaryManager().getBoundary(inletName);

        PassiveScalarProfile passiveScalarProfile = boundary.getValues().get(PassiveScalarProfile.class);

        passiveScalarProfile.setMethod(CompositeArrayProfileMethod.class);

        for (int i = 0; i < inletConcentrations.length; i++) {
            ScalarProfile scalarProfile_0 = passiveScalarProfile.getMethod(CompositeArrayProfileMethod.class)
                    .getProfile(i);
            scalarProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(inletConcentrations[i]);

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

    private void AddCurvatureCorrection() {

        Simulation simulation = getActiveSimulation();

        PhysicsContinuum physicsContinuum = ((PhysicsContinuum) simulation.getContinuumManager()
                .getContinuum("Physics 1"));

        SstKwTurbModel sstKwTurbModel = physicsContinuum.getModelManager().getModel(SstKwTurbModel.class);

        sstKwTurbModel.getKwTurbCurvatureCorrectionOption().setSelected(KwTurbCurvatureCorrectionOption.Type.DURBIN);
    }

    private void SetNormalStressOption(boolean activated) {

        if (TurbulenceModel == "RANS-kOmega") {
            Simulation simulation = getActiveSimulation();

            PhysicsContinuum physicsContinuum = ((PhysicsContinuum) simulation.getContinuumManager()
                    .getContinuum("Physics 1"));

            SstKwTurbModel sstKwTurbModel = physicsContinuum.getModelManager().getModel(SstKwTurbModel.class);

            sstKwTurbModel.setNormalStressOption(activated);
        }
    }

    private void addIsoThermal(double TempC) {

        Simulation simulation = getActiveSimulation();

        PhysicsContinuum physicsContinuum = ((PhysicsContinuum) simulation.getContinuumManager()
                .getContinuum("Physics 1"));

        physicsContinuum.enable(SegregatedFluidIsothermalModel.class);

        SegregatedFluidIsothermalModel segregatedFluidIsothermalModel_0 = physicsContinuum.getModelManager()
                .getModel(SegregatedFluidIsothermalModel.class);

        Units units_0 = ((Units) simulation.getUnitsManager().getObject("C"));

        segregatedFluidIsothermalModel_0.getContinuumTemperature().setUnits(units_0);

        segregatedFluidIsothermalModel_0.getContinuumTemperature().setValue(TempC);
    }

    private void AddSurfaceSideViewScene(String name, String fieldFunction, double Min, double Max) {

        Simulation simulation = getActiveSimulation();

        simulation.getSceneManager().createScalarScene("Scalar Scene", "Outline", "Scalar");
        Scene scene_0 = simulation.getSceneManager().getScene("Scalar Scene 1");
        scene_0.initializeAndWait();

        PartDisplayer partDisplayer_0 = ((PartDisplayer) scene_0.getDisplayerManager().getDisplayer("Outline 1"));
        partDisplayer_0.initialize();

        ScalarDisplayer scalarDisplayer_0 = ((ScalarDisplayer) scene_0.getDisplayerManager().getDisplayer("Scalar 1"));
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
        currentView_0.setInput(
                new DoubleVector(new double[] { 1.552230566304047E-14, -8.213661445433118E-9, 0.07000000000000002 }),
                new DoubleVector(new double[] { 1.552230566304047E-14, -8.213661445433118E-9, 0.47714309349357603 }),
                new DoubleVector(new double[] { 0.0, 1.0, 0.0 }), 0.10628567536481787, 1, 30.0);

        scene_0.setViewOrientation(new DoubleVector(new double[] { 0.0, 1.0, 0.0 }),
                new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));
        scene_0.close();

        scene_0.setPresentationName(name);
        scene_0.setBackgroundColorMode(BackgroundColorMode.SOLID);
        LogoAnnotation logoAnnotation_0 = ((LogoAnnotation) simulation.getAnnotationManager().getObject("Logo"));
        scene_0.getAnnotationPropManager().removePropsForAnnotations(logoAnnotation_0);
        PhysicalTimeAnnotation physicalTimeAnnotation_0 = ((PhysicalTimeAnnotation) simulation.getAnnotationManager()
                .getObject("Solution Time"));
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

        PartDisplayer partDisplayer_0 = ((PartDisplayer) scene_0.getDisplayerManager().getDisplayer("Outline 1"));

        partDisplayer_0.initialize();

        ScalarDisplayer scalarDisplayer_0 = ((ScalarDisplayer) scene_0.getDisplayerManager().getDisplayer("Scalar 1"));

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

        currentView_0.setInput(
                new DoubleVector(new double[] { 1.552230566304047E-14, -8.213661445433118E-9, 0.07000000000000002 }),
                new DoubleVector(new double[] { 1.552230566304047E-14, -8.213661445433118E-9, 0.47714309349357603 }),
                new DoubleVector(new double[] { 0.0, 1.0, 0.0 }), 0.10628567536481787, 1, 30.0);

        scene_0.setViewOrientation(new DoubleVector(new double[] { 0.0, 1.0, 0.0 }),
                new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));

        scene_0.close();

        scene_0.setPresentationName(name);

        scene_0.setBackgroundColorMode(BackgroundColorMode.SOLID);

        LogoAnnotation logoAnnotation_0 = ((LogoAnnotation) simulation.getAnnotationManager().getObject("Logo"));

        scene_0.getAnnotationPropManager().removePropsForAnnotations(logoAnnotation_0);

        PhysicalTimeAnnotation physicalTimeAnnotation_0 = ((PhysicalTimeAnnotation) simulation.getAnnotationManager()
                .getObject("Solution Time"));

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

    private void CreateTimingPlots() {

        Simulation simulation = getActiveSimulation();

        TimeStepCpuTimeReport timeStepCpuTimeReport_0 = simulation.getReportManager()
                .createReport(TimeStepCpuTimeReport.class);

        TimeStepElapsedTimeReport timeStepElapsedTimeReport_0 = simulation.getReportManager()
                .createReport(TimeStepElapsedTimeReport.class);

        simulation.getMonitorManager().createMonitorAndPlot(
                new NeoObjectVector(new Object[] { timeStepCpuTimeReport_0, timeStepElapsedTimeReport_0 }), true,
                "Reports Plot");

        SimulationIteratorTimeReportMonitor simulationIteratorTimeReportMonitor_0 = ((SimulationIteratorTimeReportMonitor) simulation
                .getMonitorManager().getMonitor("Solver CPU Time per Time Step Monitor"));

        SimulationIteratorTimeReportMonitor simulationIteratorTimeReportMonitor_1 = ((SimulationIteratorTimeReportMonitor) simulation
                .getMonitorManager().getMonitor("Solver Elapsed Time per Time Step Monitor"));

        MonitorPlot monitorPlot_0 = simulation.getPlotManager()
                .createMonitorPlot(new NeoObjectVector(
                        new Object[] { simulationIteratorTimeReportMonitor_0, simulationIteratorTimeReportMonitor_1 }),
                        "Reports Plot");
    }

    private void CreateTotalScalePlot() {

        Simulation simulation = getActiveSimulation();

        VolumeIntegralReport volumeIntegralReport_0 = simulation.getReportManager()
                .createReport(VolumeIntegralReport.class);

        volumeIntegralReport_0.setPresentationName("Volume Integral of Barite");
        volumeIntegralReport_0.getParts().setQuery(null);
        Region region_0 = simulation.getRegionManager().getRegion("Fluid");
        volumeIntegralReport_0.getParts().setObjects(region_0);

        UserFieldFunction userFieldFunction_0 = ((UserFieldFunction) simulation.getFieldFunctionManager()
                .getFunction("BariteVolumeFraction"));

        volumeIntegralReport_0.setFieldFunction(userFieldFunction_0);

        ReportMonitor reportMonitor_0 = volumeIntegralReport_0.createMonitor();
        StarUpdate starUpdate_0 = reportMonitor_0.getStarUpdate();
        IterationUpdateFrequency iterationUpdateFrequency_0 = starUpdate_0.getIterationUpdateFrequency();
        iterationUpdateFrequency_0.setIterations(10);

        MonitorPlot monitorPlot_1 = simulation.getPlotManager()
                .createMonitorPlot(new NeoObjectVector(new Object[] { reportMonitor_0 }), "Scale volume plot");

    }

    private void swapToUnsteady() {

        IsSteady = false;
        Simulation simulation = getActiveSimulation();

        PhysicsContinuum physicsContinuum = ((PhysicsContinuum) simulation.getContinuumManager()
                .getContinuum("Physics 1"));

        SteadyModel steadyModel_0 = physicsContinuum.getModelManager().getModel(SteadyModel.class);

        physicsContinuum.disableModel(steadyModel_0);

        physicsContinuum.enable(ImplicitUnsteadyModel.class);
    }

    private void setTimeStep(double deltat) {
        Simulation simulation = getActiveSimulation();

        PhysicsContinuum physicsContinuum = simulation.getContinuumManager().createContinuum(PhysicsContinuum.class);

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
                .getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Physical Time"));

        physicalTimeStoppingCriterion.getMaximumTime().setValue(physicalTime);

        StepStoppingCriterion stepStoppingCriterion = ((StepStoppingCriterion) simulation
                .getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps"));

        stepStoppingCriterion.setIsUsed(false);
    }

    private void CreatePhysics(boolean isSteady, String turbulenceModel) {

        Simulation simulation = getActiveSimulation();

        PhysicsContinuum physicsContinuum = simulation.getContinuumManager().createContinuum(PhysicsContinuum.class);

        physicsContinuum.enable(ThreeDimensionalModel.class);

        if (isSteady) {
            physicsContinuum.enable(SteadyModel.class);

        } else {
            physicsContinuum.enable(ImplicitUnsteadyModel.class);

            ImplicitUnsteadySolver implicitUnsteadySolver = ((ImplicitUnsteadySolver) simulation.getSolverManager()
                    .getSolver(ImplicitUnsteadySolver.class));

            implicitUnsteadySolver.getTimeStep().setValue(TimeStep);
        }

        StepStoppingCriterion stepStoppingCriterion = ((StepStoppingCriterion) simulation
                .getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps"));
        stepStoppingCriterion.setIsUsed(false);

        physicsContinuum.enable(SingleComponentLiquidModel.class);

        physicsContinuum.enable(SegregatedFlowModel.class);

        physicsContinuum.enable(ConstantDensityModel.class);

        if (turbulenceModel == "RANS-kOmega") {
            physicsContinuum.enable(TurbulentModel.class);

            physicsContinuum.enable(RansTurbulenceModel.class);

            physicsContinuum.enable(KOmegaTurbulence.class);

            physicsContinuum.enable(SstKwTurbModel.class);

            physicsContinuum.enable(KwAllYplusWallTreatment.class);

            physicsContinuum.enable(GammaTransitionModel.class);

            SstKwTurbModel sstKwTurbModel = physicsContinuum.getModelManager().getModel(SstKwTurbModel.class);

            if (kOmegaConsituitive == "Cubic") {
                sstKwTurbModel.getKwTurbConstitutiveOption().setSelected(KwTurbConstitutiveOption.Type.CUBIC);
            } else if (kOmegaConsituitive == "QCR") {
                sstKwTurbModel.getKwTurbConstitutiveOption().setSelected(KwTurbConstitutiveOption.Type.QCR);
            } else if (kOmegaConsituitive == "Linear") {
                sstKwTurbModel.getKwTurbConstitutiveOption().setSelected(KwTurbConstitutiveOption.Type.LINEAR);
            }

        } else if (turbulenceModel == "LES") {
            physicsContinuum.enable(TurbulentModel.class);

            physicsContinuum.enable(LesTurbulenceModel.class);

            physicsContinuum.enable(WaleSgsModel.class);

            physicsContinuum.enable(LesAllYplusWallTreatment.class);
        } else {
            physicsContinuum.enable(LaminarModel.class);

        }
    }

    private void SetPrismLayers(int noLayers, int noThinLayers, double prismLayerStretch,
            double prismLayerTotalThickness) {

        Simulation simulation = getActiveSimulation();

        AutoMeshOperation autoMeshOperation = ((AutoMeshOperation) simulation.get(MeshOperationManager.class)
                .getObject("Automated Mesh"));

        if (thinMesher) {
            ThinNumLayers thinNumLayers = autoMeshOperation.getDefaultValues().get(ThinNumLayers.class);
            thinNumLayers.setLayers(noThinLayers);
        }

        NumPrismLayers numPrismLayers = autoMeshOperation.getDefaultValues().get(NumPrismLayers.class);

        IntegerValue integerValuePrsimLayers = numPrismLayers.getNumLayersValue();

        integerValuePrsimLayers.getQuantity().setValue(noLayers);

        PrismLayerStretching prismLayerStretching = autoMeshOperation.getDefaultValues()
                .get(PrismLayerStretching.class);

        prismLayerStretching.getStretchingQuantity().setValue(prismLayerStretch);

        PrismThickness prismThickness = autoMeshOperation.getDefaultValues().get(PrismThickness.class);

        prismThickness.getRelativeOrAbsoluteOption().setSelected(RelativeOrAbsoluteOption.Type.ABSOLUTE);

        ((ScalarPhysicalQuantity) prismThickness.getAbsoluteSizeValue()).setValue(prismLayerTotalThickness);
    }

    private void CreateAutomatedMeshOperation(double meshBaseSize) {

        Simulation simulation = getActiveSimulation();

        String[] mesherArr = null;
        if (thinMesher) {
            mesherArr = new String[] { "star.resurfacer.ResurfacerAutoMesher",
                    "star.resurfacer.AutomaticSurfaceRepairAutoMesher", "star.dualmesher.DualAutoMesher",
                    "star.prismmesher.PrismAutoMesher", "star.solidmesher.ThinAutoMesher" };
        } else {
            mesherArr = new String[] { "star.resurfacer.ResurfacerAutoMesher",
                    "star.resurfacer.AutomaticSurfaceRepairAutoMesher", "star.dualmesher.DualAutoMesher",
                    "star.prismmesher.PrismAutoMesher" };
        }
        AutoMeshOperation autoMeshOperation = simulation.get(MeshOperationManager.class)
                .createAutoMeshOperation(new StringVector(mesherArr), new NeoObjectVector(new Object[] {}));

        autoMeshOperation.setLinkOutputPartName(false);

        autoMeshOperation.getDefaultValues().get(BaseSize.class).setValue(meshBaseSize);

        autoMeshOperation.getInputGeometryObjects().setQuery(null);

        if (!thinMesher && parallelMesher) {
            autoMeshOperation.getMesherParallelModeOption().setSelected(MesherParallelModeOption.Type.PARALLEL);
        }

        MeshOperationPart meshOperationPart = ((MeshOperationPart) simulation.get(SimulationPartManager.class)
                .getPart("FluidDomain"));

        autoMeshOperation.getInputGeometryObjects().setObjects(meshOperationPart);
    }

    private void ExecuteMesh() {

        Simulation simulation = getActiveSimulation();

        AutoMeshOperation autoMeshOperation = ((AutoMeshOperation) simulation.get(MeshOperationManager.class)
                .getObject("Automated Mesh"));

        autoMeshOperation.execute();
    }

    private void AssignPartsToRegion() {
        Simulation simulation = getActiveSimulation();

        Region fluidRegion = simulation.getRegionManager().getRegion("Fluid");

        fluidRegion.getPartGroup().setQuery(null);

        MeshOperationPart meshOperationPart_0 = ((MeshOperationPart) simulation.get(SimulationPartManager.class)
                .getPart("FluidDomain"));

        fluidRegion.getPartGroup().setObjects(meshOperationPart_0);

        Boundary statorBoundary = fluidRegion.getBoundaryManager().getBoundary("Stator");

        statorBoundary.getPartSurfaceGroup().setQuery(null);

        PartSurface partSurface_0 = ((PartSurface) meshOperationPart_0.getPartSurfaceManager()
                .getPartSurface("StatorUnit.InletA.Cylinder Surface"));

        PartSurface partSurface_1 = ((PartSurface) meshOperationPart_0.getPartSurfaceManager()
                .getPartSurface("StatorUnit.InletB.Cylinder Surface"));

        PartSurface partSurface_2 = ((PartSurface) meshOperationPart_0.getPartSurfaceManager()
                .getPartSurface("StatorUnit.OutletA.Cylinder Surface"));

        PartSurface partSurface_3 = ((PartSurface) meshOperationPart_0.getPartSurfaceManager()
                .getPartSurface("StatorUnit.OutletB.Cylinder Surface"));

        PartSurface partSurface_4 = ((PartSurface) meshOperationPart_0.getPartSurfaceManager()
                .getPartSurface("StatorUnit.Stator.Cylinder Surface"));

        statorBoundary.getPartSurfaceGroup().setObjects(partSurface_0, partSurface_1, partSurface_2, partSurface_3,
                partSurface_4);

        Boundary rotorBoundary = fluidRegion.getBoundaryManager().getBoundary("Rotor");

        rotorBoundary.getPartSurfaceGroup().setQuery(null);

        PartSurface partSurface_5 = ((PartSurface) meshOperationPart_0.getPartSurfaceManager()
                .getPartSurface("RotorUnit.Rotor.Cylinder Surface"));

        PartSurface partSurface_6 = ((PartSurface) meshOperationPart_0.getPartSurfaceManager()
                .getPartSurface("RotorUnit.Shaft.Cylinder Surface"));

        rotorBoundary.getPartSurfaceGroup().setObjects(partSurface_5, partSurface_6);

    }

    private void CreateGeometry() {
        CreateCylinder("Stator", "[0.0, 0.0, 0.0] m m m", "[0.0, 0.0, ($StatorHeight)]", "$StatorDiameter/2");
        CreateCylinder("Rotor", "[0.0, 0.0, $RotorStartZ]", "[0.0, 0.0, ($RotorStartZ + $RotorHeight)]",
                "$RotorDiameter/2");
        CreateCylinder("Shaft", "[0.0, 0.0, -0.001] m m m", "[0.0, 0.0, ($StatorHeight+0.001)]", "$ShaftDiameter/2");

        CreateCylinder("InletA", "[(-$InletHoleRadialPosition), 0.0, (-$TubeLength)]",
                "[(-$InletHoleRadialPosition), 0.0, 0.0]", "$InletHoleDiameter/2");
        CreateCylinder("InletB", "[($InletHoleRadialPosition), 0.0, (-$TubeLength)]",
                "[($InletHoleRadialPosition), 0.0, 0.0]", "$InletHoleDiameter/2");
        CreateCylinder("OutletA", "[(-$InletHoleRadialPosition), 0.0, ($StatorHeight)]",
                "[(-$InletHoleRadialPosition), 0.0, ($StatorHeight+$TubeLength)]", "$InletHoleDiameter/2");
        CreateCylinder("OutletB", "[($InletHoleRadialPosition), 0.0, ($StatorHeight)]",
                "[($InletHoleRadialPosition), 0.0, ($StatorHeight+$TubeLength)]", "$InletHoleDiameter/2");

        SetPartSurfaces();

        UniteRotor();
        UniteStator();
        SubtractParts();

    }

    private void CreateCylinder(String name, String start, String end, String Radius) {

        Simulation simulation = getActiveSimulation();

        MeshPartFactory meshPartFactory = simulation.get(MeshPartFactory.class);

        SimpleCylinderPart simpleCylinderPart = meshPartFactory
                .createNewCylinderPart(simulation.get(SimulationPartManager.class));

        simpleCylinderPart.setPresentationName(name);

        simpleCylinderPart.setDoNotRetessellate(true);

        LabCoordinateSystem labCoordinateSystem_0 = simulation.getCoordinateSystemManager().getLabCoordinateSystem();

        simpleCylinderPart.setCoordinateSystem(labCoordinateSystem_0);

        simpleCylinderPart.getRadius().setDefinition(Radius);

        simpleCylinderPart.getStartCoordinate().setDefinition(start);

        simpleCylinderPart.getEndCoordinate().setDefinition(end);

        simpleCylinderPart.getTessellationDensityOption().setSelected(TessellationDensityOption.Type.VERY_FINE);

        simpleCylinderPart.rebuildSimpleShapePart();

        simpleCylinderPart.setDoNotRetessellate(false);

    }

    private void CreateRegion() {
        Simulation simulation = getActiveSimulation();

        Region fluidRegion = simulation.getRegionManager().createEmptyRegion();
        fluidRegion.setPresentationName("Fluid");

        Boundary statorBoundary = fluidRegion.getBoundaryManager().getBoundary("Default");

        statorBoundary.setPresentationName("Stator");

        Boundary rotorBoundary = fluidRegion.getBoundaryManager().createEmptyBoundary();

        rotorBoundary.setPresentationName("Rotor");

        if (flowRate > 1e-9) {
            Boundary OutletA = CreatePressureOutlet("OutletA", fluidRegion, simulation);
            Boundary OutletB = CreateWall("OutletB", fluidRegion, simulation);
            Boundary InletA = CreateInlet("InletA", fluidRegion, simulation);
            Boundary InletB = CreateInlet("InletB", fluidRegion, simulation);
            // Boundary InletA = CreateMassFlowInlet("InletA", fluidRegion, simulation,
            // massFlowRate);
            // Boundary InletB = CreateMassFlowInlet("InletB", fluidRegion, simulation,
            // massFlowRate);
        } else {
            Boundary OutletA = CreateWall("OutletA", fluidRegion, simulation);
            Boundary OutletB = CreateWall("OutletB", fluidRegion, simulation);
            Boundary InletA = CreateWall("InletA", fluidRegion, simulation);
            Boundary InletB = CreateWall("InletB", fluidRegion, simulation);
        }

    }

    private Boundary CreateMassFlowInlet(String name, Region region, Simulation simulation, double massFlowSi) {

        Boundary boundary = region.getBoundaryManager().createEmptyBoundary();
        boundary.setPresentationName(name);

        MassFlowBoundary massFlowBoundary_0 = ((MassFlowBoundary) simulation.get(ConditionTypeManager.class)
                .get(MassFlowBoundary.class));

        boundary.setBoundaryType(massFlowBoundary_0);

        Units units_0 = simulation.getUnitsManager().getInternalUnits(new IntVector(
                new int[] { 1, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }));

        MassFlowRateProfile massFlowRateProfile_0 = boundary.getValues().get(MassFlowRateProfile.class);

        massFlowRateProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(massFlowSi);

        return boundary;
    }

    private static Boundary CreateWall(String name, Region region, Simulation simulation) {
        Boundary boundary = region.getBoundaryManager().createEmptyBoundary();

        boundary.setPresentationName(name);

        return boundary;
    }

    private static Boundary CreateOutlet(String name, Region region, Simulation simulation) {
        Boundary boundary = region.getBoundaryManager().createEmptyBoundary();

        boundary.setPresentationName(name);

        OutletBoundary outletBoundary = ((OutletBoundary) simulation.get(ConditionTypeManager.class)
                .get(OutletBoundary.class));

        boundary.setBoundaryType(outletBoundary);

        return boundary;
    }

    private static Boundary CreatePressureOutlet(String name, Region region, Simulation simulation) {
        Boundary boundary = region.getBoundaryManager().createEmptyBoundary();

        boundary.setPresentationName(name);

        PressureBoundary outletBoundary = ((PressureBoundary) simulation.get(ConditionTypeManager.class)
                .get(PressureBoundary.class));

        boundary.setBoundaryType(outletBoundary);

        return boundary;
    }

    private static Boundary CreateInlet(String name, Region region, Simulation simulation) {
        Boundary boundary = region.getBoundaryManager().createEmptyBoundary();

        boundary.setPresentationName(name);

        InletBoundary inletBoundary = ((InletBoundary) simulation.get(ConditionTypeManager.class)
                .get(InletBoundary.class));

        boundary.setBoundaryType(inletBoundary);

        return boundary;
    }

    private void CreateGlobalDimensionlessParameter(String name, String definition) {
        Simulation simulation = getActiveSimulation();
        ScalarGlobalParameter sParameter = (ScalarGlobalParameter) simulation.get(GlobalParameterManager.class)
                .createGlobalParameter(ScalarGlobalParameter.class, name);

        sParameter.getQuantity().setDefinition(definition);
    }

    private void CreateGlobalParameters() {
        Simulation simulation = getActiveSimulation();

        // ScalarGlobalParameter UnitMeterParameter =
        // (ScalarGlobalParameter)simulation.get(GlobalParameterManager.class).createGlobalParameter(ScalarGlobalParameter.class,
        // "UnitMeter");
        // UnitMeterParameter.setDimensions(Dimensions.Builder().length(1).build());
        // UnitMeterParameter.getQuantity().setValue(1.0);

        CreateGlobalLengthParameter("RotorDiameter", RotorDiameter);
        CreateGlobalLengthParameter("RotorHeight", RotorHeight);
        CreateGlobalLengthParameter("StatorDiameter", StatorDiameter);
        CreateGlobalLengthParameter("StatorHeight", StatorHeight);
        CreateGlobalLengthParameter("RotorStartZ", RotorStartZ);
        CreateGlobalLengthParameter("InletHoleDiameter", InletHoleDiameter);
        CreateGlobalLengthParameter("InletHoleRadialPosition", InletHoleRadialPosition);
        CreateGlobalLengthParameter("TubeLength", TubeLength);
        CreateGlobalLengthParameter("ShaftDiameter", ShaftDiameter);

        ScalarGlobalParameter rpmParameter = (ScalarGlobalParameter) simulation.get(GlobalParameterManager.class)
                .createGlobalParameter(ScalarGlobalParameter.class, "RPM");
        rpmParameter.setDimensions(Dimensions.Builder().angularVelocity(1).build());
        Units rpmUnit = ((Units) simulation.getUnitsManager().getObject("rpm"));
        rpmParameter.getQuantity().setUnits(rpmUnit);
        rpmParameter.getQuantity().setValue(RPM);

    }

    private void CreateGlobalLengthParameter(String name, double lengthInMeters) {
        Simulation simulation = getActiveSimulation();
        ScalarGlobalParameter sParameter = (ScalarGlobalParameter) simulation.get(GlobalParameterManager.class)
                .createGlobalParameter(ScalarGlobalParameter.class, name);
        sParameter.setDimensions(Dimensions.Builder().length(1).build());
        sParameter.getQuantity().setValue(lengthInMeters);
    }

    private void UniteRotor() {

        Simulation simulation = getActiveSimulation();

        SimpleCylinderPart simpleCylinderPart_1 = ((SimpleCylinderPart) simulation.get(SimulationPartManager.class)
                .getPart("Shaft"));

        SimpleCylinderPart simpleCylinderPart_2 = ((SimpleCylinderPart) simulation.get(SimulationPartManager.class)
                .getPart("Rotor"));

        UnitePartsOperation unitePartsOperation = (UnitePartsOperation) simulation.get(MeshOperationManager.class)
                .createUnitePartsOperation(
                        new NeoObjectVector(new Object[] { simpleCylinderPart_1, simpleCylinderPart_2 }));

        unitePartsOperation.setPerformCADBoolean(true);

        unitePartsOperation.setLinkOutputPartName(false);

        unitePartsOperation.setPresentationName("RotorUnit");

        unitePartsOperation.execute();

        MeshOperationPart meshOperationPart = ((MeshOperationPart) simulation.get(SimulationPartManager.class)
                .getPart("Unite"));

        meshOperationPart.setPresentationName("RotorUnit");

    }

    private void UniteStator() {

        Simulation simulation = getActiveSimulation();

        SimpleCylinderPart simpleCylinderPart_1 = ((SimpleCylinderPart) simulation.get(SimulationPartManager.class)
                .getPart("InletA"));

        SimpleCylinderPart simpleCylinderPart_2 = ((SimpleCylinderPart) simulation.get(SimulationPartManager.class)
                .getPart("InletB"));

        SimpleCylinderPart simpleCylinderPart_3 = ((SimpleCylinderPart) simulation.get(SimulationPartManager.class)
                .getPart("OutletA"));

        SimpleCylinderPart simpleCylinderPart_4 = ((SimpleCylinderPart) simulation.get(SimulationPartManager.class)
                .getPart("OutletB"));

        SimpleCylinderPart simpleCylinderPart_5 = ((SimpleCylinderPart) simulation.get(SimulationPartManager.class)
                .getPart("Stator"));

        UnitePartsOperation unitePartsOperation = (UnitePartsOperation) simulation.get(MeshOperationManager.class)
                .createUnitePartsOperation(new NeoObjectVector(new Object[] { simpleCylinderPart_1,
                        simpleCylinderPart_2, simpleCylinderPart_3, simpleCylinderPart_4, simpleCylinderPart_5 }));

        unitePartsOperation.setPerformCADBoolean(true);

        unitePartsOperation.setLinkOutputPartName(false);

        unitePartsOperation.setPresentationName("StatorUnit");

        unitePartsOperation.execute();

        MeshOperationPart meshOperationPart = ((MeshOperationPart) simulation.get(SimulationPartManager.class)
                .getPart("Unite"));

        meshOperationPart.setPresentationName("StatorUnit");

    }

    private void SubtractParts() {

        Simulation simulation = getActiveSimulation();

        MeshOperationPart meshOperationPart_4 = ((MeshOperationPart) simulation.get(SimulationPartManager.class)
                .getPart("RotorUnit"));

        MeshOperationPart meshOperationPart_5 = ((MeshOperationPart) simulation.get(SimulationPartManager.class)
                .getPart("StatorUnit"));

        SubtractPartsOperation subtractPartsOperation = (SubtractPartsOperation) simulation
                .get(MeshOperationManager.class).createSubtractPartsOperation(
                        new NeoObjectVector(new Object[] { meshOperationPart_4, meshOperationPart_5 }));

        subtractPartsOperation.getTargetPartManager().setQuery(null);

        subtractPartsOperation.getTargetPartManager().setObjects(meshOperationPart_5);

        subtractPartsOperation.setPerformCADBoolean(true);

        subtractPartsOperation.setPresentationName("FluidDomain");

        subtractPartsOperation.setLinkOutputPartName(true);

        subtractPartsOperation.execute();
    }

    private void SetPartSurfaces() {

        Simulation simulation = getActiveSimulation();

        SimpleCylinderPart simpleCylinderPart_48 = ((SimpleCylinderPart) simulation.get(SimulationPartManager.class)
                .getPart("InletA"));

        PartSurface partSurface_18 = ((PartSurface) simpleCylinderPart_48.getPartSurfaceManager()
                .getPartSurface("Cylinder Surface"));

        simpleCylinderPart_48.getPartSurfaceManager()
                .splitPartSurfacesByAngle(new NeoObjectVector(new Object[] { partSurface_18 }), 89.0);

        PartSurface partSurface_19 = ((PartSurface) simpleCylinderPart_48.getPartSurfaceManager()
                .getPartSurface("Cylinder Surface 2"));

        partSurface_19.setPresentationName("InletA");

        SimpleCylinderPart simpleCylinderPart_49 = ((SimpleCylinderPart) simulation.get(SimulationPartManager.class)
                .getPart("InletB"));

        PartSurface partSurface_20 = ((PartSurface) simpleCylinderPart_49.getPartSurfaceManager()
                .getPartSurface("Cylinder Surface"));

        simpleCylinderPart_49.getPartSurfaceManager()
                .splitPartSurfacesByAngle(new NeoObjectVector(new Object[] { partSurface_20 }), 89.0);

        PartSurface partSurface_21 = ((PartSurface) simpleCylinderPart_49.getPartSurfaceManager()
                .getPartSurface("Cylinder Surface 2"));

        partSurface_21.setPresentationName("InletB");

        SimpleCylinderPart simpleCylinderPart_50 = ((SimpleCylinderPart) simulation.get(SimulationPartManager.class)
                .getPart("OutletA"));

        PartSurface partSurface_22 = ((PartSurface) simpleCylinderPart_50.getPartSurfaceManager()
                .getPartSurface("Cylinder Surface"));

        simpleCylinderPart_50.getPartSurfaceManager()
                .splitPartSurfacesByAngle(new NeoObjectVector(new Object[] { partSurface_22 }), 89.0);

        PartSurface partSurface_23 = ((PartSurface) simpleCylinderPart_50.getPartSurfaceManager()
                .getPartSurface("Cylinder Surface 3"));

        partSurface_23.setPresentationName("OutletA");

        SimpleCylinderPart simpleCylinderPart_51 = ((SimpleCylinderPart) simulation.get(SimulationPartManager.class)
                .getPart("OutletB"));

        PartSurface partSurface_24 = ((PartSurface) simpleCylinderPart_51.getPartSurfaceManager()
                .getPartSurface("Cylinder Surface"));

        simpleCylinderPart_51.getPartSurfaceManager()
                .splitPartSurfacesByAngle(new NeoObjectVector(new Object[] { partSurface_24 }), 89.0);

        PartSurface partSurface_25 = ((PartSurface) simpleCylinderPart_51.getPartSurfaceManager()
                .getPartSurface("Cylinder Surface 3"));

        partSurface_25.setPresentationName("OutletB");
    }

    private void CreateInletVelocityParameter() {

        Simulation simulation = getActiveSimulation();

        simulation.get(GlobalParameterManager.class).createGlobalParameter(ScalarGlobalParameter.class, "Scalar");

        ScalarGlobalParameter scalarGlobalParameter_1 = ((ScalarGlobalParameter) simulation
                .get(GlobalParameterManager.class).getObject("Scalar"));

        scalarGlobalParameter_1.setPresentationName("InletHoleArea");

        scalarGlobalParameter_1.setDimensions(Dimensions.Builder().length(2).build());

        Units units_0 = simulation.getUnitsManager().getInternalUnits(
                new IntVector(new int[] { 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }));

        scalarGlobalParameter_1.getQuantity().setDefinition("$InletHoleDiameter*$InletHoleDiameter/4.0");

        simulation.get(GlobalParameterManager.class).createGlobalParameter(ScalarGlobalParameter.class, "Scalar");

        ScalarGlobalParameter scalarGlobalParameter_2 = ((ScalarGlobalParameter) simulation
                .get(GlobalParameterManager.class).getObject("Scalar"));

        scalarGlobalParameter_2.setPresentationName("FlowRate");

        scalarGlobalParameter_2.setDimensions(Dimensions.Builder().length(3).time(-1).build());

        Units units_1 = simulation.getUnitsManager().getInternalUnits(new IntVector(
                new int[] { 0, 3, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }));

        scalarGlobalParameter_2.getQuantity().setValue(flowRate);

        simulation.get(GlobalParameterManager.class).createGlobalParameter(ScalarGlobalParameter.class, "Scalar");

        ScalarGlobalParameter scalarGlobalParameter_3 = ((ScalarGlobalParameter) simulation
                .get(GlobalParameterManager.class).getObject("Scalar"));

        scalarGlobalParameter_3.setPresentationName("InletVelocity");

        scalarGlobalParameter_3.setDimensions(Dimensions.Builder().length(1).time(-1).build());

        Units units_2 = simulation.getUnitsManager().getInternalUnits(
                new IntVector(new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 }));

        scalarGlobalParameter_3.getQuantity().setDefinition("$FlowRate/(2*$InletHoleArea)");

        Region region_0 = simulation.getRegionManager().getRegion("Fluid");

        Boundary boundary_1 = region_0.getBoundaryManager().getBoundary("InletA");

        VelocityMagnitudeProfile velocityMagnitudeProfile_0 = boundary_1.getValues()
                .get(VelocityMagnitudeProfile.class);

        velocityMagnitudeProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity()
                .setDefinition("$InletVelocity");

        Boundary boundary_2 = region_0.getBoundaryManager().getBoundary("InletB");

        VelocityMagnitudeProfile velocityMagnitudeProfile_1 = boundary_2.getValues()
                .get(VelocityMagnitudeProfile.class);

        velocityMagnitudeProfile_1.getMethod(ConstantScalarProfileMethod.class).getQuantity()
                .setDefinition("$InletVelocity");
    }

    private void SetInletVelocity() {

        Simulation simulation = getActiveSimulation();

        Units units_3 = simulation.getUnitsManager().getInternalUnits(
                new IntVector(new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 }));

        Region fluidRegion = simulation.getRegionManager().getRegion("Fluid");

        Boundary inletBoundaryA = fluidRegion.getBoundaryManager().getBoundary("InletA");

        VelocityMagnitudeProfile velocityMagnitudeProfile_0 = inletBoundaryA.getValues()
                .get(VelocityMagnitudeProfile.class);

        velocityMagnitudeProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity()
                .setDefinition("$InletVelocity");

        Boundary inletBoundaryB = fluidRegion.getBoundaryManager().getBoundary("InletB");

        VelocityMagnitudeProfile velocityMagnitudeProfile_1 = inletBoundaryB.getValues()
                .get(VelocityMagnitudeProfile.class);

        velocityMagnitudeProfile_1.getMethod(ConstantScalarProfileMethod.class).getQuantity()
                .setDefinition("$InletVelocity");
    }

    private void SetRotationOfRotor() {

        Simulation simulation = getActiveSimulation();

        Region fluidRegion = simulation.getRegionManager().getRegion("Fluid");

        Boundary rotorBoundary = fluidRegion.getBoundaryManager().getBoundary("Rotor");

        rotorBoundary.getConditions().get(WallSlidingOption.class)
                .setSelected(WallSlidingOption.Type.LOCAL_ROTATION_RATE);

        Units units_1 = simulation.getUnitsManager().getInternalUnits(
                new IntVector(new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0 }));

        WallRelativeRotationProfile wallRelativeRotationProfile_0 = rotorBoundary.getValues()
                .get(WallRelativeRotationProfile.class);

        wallRelativeRotationProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setDefinition("$RPM");
    }

    private void CreateDerivedParts() {

        Simulation simulation = getActiveSimulation();

        PlaneSection planeSection_0 = (PlaneSection) simulation.getPartManager().createImplicitPart(
                new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] { 0.0, 0.0, 1.0 }),
                new DoubleVector(new double[] { 0.0, 0.0, 0.0 }), 0, 1, new DoubleVector(new double[] { 0.0 }));

        Units units_2 = simulation.getUnitsManager().getInternalUnits(
                new IntVector(new int[] { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }));

        planeSection_0.getOriginCoordinate().setDefinition("[0.0, 0.0, ($StatorHeight/2)] ");

        planeSection_0.setPresentationName("XY Plane");

        PlaneSection planeSection_1 = (PlaneSection) simulation.getPartManager().createImplicitPart(
                new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] { 0.0, 0.0, 1.0 }),
                new DoubleVector(new double[] { 0.0, 0.0, 0.0 }), 0, 1, new DoubleVector(new double[] { 0.0 }));

        planeSection_1.getOrientationCoordinate().setCoordinate(units_2, units_2, units_2,
                new DoubleVector(new double[] { 0.0, 1.0, 0.0 }));

        planeSection_1.setPresentationName("XZ Plane");

        PlaneSection planeSection_2 = (PlaneSection) simulation.getPartManager().createImplicitPart(
                new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] { 0.0, 0.0, 1.0 }),
                new DoubleVector(new double[] { 0.0, 0.0, 0.0 }), 0, 1, new DoubleVector(new double[] { 0.0 }));

        planeSection_2.getOrientationCoordinate().setCoordinate(units_2, units_2, units_2,
                new DoubleVector(new double[] { 1.0, 0.0, 0.0 }));

        planeSection_2.setPresentationName("YZ Plane");
    }

    private void AssignPlanesToRegion() {

        Simulation simulation = getActiveSimulation();

        PlaneSection planeSection_0 = ((PlaneSection) simulation.getPartManager().getObject("XY Plane"));

        planeSection_0.getInputParts().setQuery(null);

        Region region_0 = simulation.getRegionManager().getRegion("Fluid");

        Boundary boundary_1 = region_0.getBoundaryManager().getBoundary("InletA");

        Boundary boundary_2 = region_0.getBoundaryManager().getBoundary("InletB");

        Boundary boundary_3 = region_0.getBoundaryManager().getBoundary("OutletA");

        Boundary boundary_4 = region_0.getBoundaryManager().getBoundary("OutletB");

        Boundary boundary_0 = region_0.getBoundaryManager().getBoundary("Rotor");

        Boundary boundary_5 = region_0.getBoundaryManager().getBoundary("Stator");

        planeSection_0.getInputParts().setObjects(region_0, boundary_1, boundary_2, boundary_3, boundary_4, boundary_0,
                boundary_5);

        PlaneSection planeSection_1 = ((PlaneSection) simulation.getPartManager().getObject("XZ Plane"));

        planeSection_1.getInputParts().setQuery(null);

        planeSection_1.getInputParts().setObjects(region_0, boundary_1, boundary_2, boundary_3, boundary_4, boundary_0,
                boundary_5);

        PlaneSection planeSection_2 = ((PlaneSection) simulation.getPartManager().getObject("YZ Plane"));

        planeSection_2.getInputParts().setQuery(null);

        planeSection_2.getInputParts().setObjects(region_0, boundary_1, boundary_2, boundary_3, boundary_4, boundary_0,
                boundary_5);
    }

    private void CreateVerticalRefinementZone(double Start, double End, double RelativeSize) {

        Simulation simulation = getActiveSimulation();

        Units units_0 = simulation.getUnitsManager().getPreferredUnits(
                new IntVector(new int[] { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }));

        MeshPartFactory meshPartFactory = simulation.get(MeshPartFactory.class);

        SimpleBlockPart refinementBlockPart = meshPartFactory
                .createNewBlockPart(simulation.get(SimulationPartManager.class));

        refinementBlockPart.setDoNotRetessellate(true);

        LabCoordinateSystem labCoordinateSystem_0 = simulation.getCoordinateSystemManager().getLabCoordinateSystem();

        refinementBlockPart.setCoordinateSystem(labCoordinateSystem_0);

        refinementBlockPart.getCorner1().setCoordinateSystem(labCoordinateSystem_0);

        refinementBlockPart.getCorner1().setCoordinate(units_0, units_0, units_0,
                new DoubleVector(new double[] { 0.0, 0.0, 0.0 }));

        refinementBlockPart.getCorner2().setCoordinateSystem(labCoordinateSystem_0);

        refinementBlockPart.getCorner2().setCoordinate(units_0, units_0, units_0,
                new DoubleVector(new double[] { 1.0, 1.0, 1.0 }));

        refinementBlockPart.rebuildSimpleShapePart();

        refinementBlockPart.setDoNotRetessellate(false);

        refinementBlockPart.setPresentationName("RefinementZone");

        refinementBlockPart.getCorner1().setDefinition("[-$StatorDiameter, -$StatorDiameter, " + Start + "] ");

        refinementBlockPart.getCorner2().setDefinition("[$StatorDiameter, $StatorDiameter, " + End + "]");

        AutoMeshOperation autoMeshOperation_0 = ((AutoMeshOperation) simulation.get(MeshOperationManager.class)
                .getObject("Automated Mesh"));

        VolumeCustomMeshControl volumeCustomMeshControl_1 = autoMeshOperation_0.getCustomMeshControls()
                .createVolumeControl();

        volumeCustomMeshControl_1.getGeometryObjects().setQuery(null);

        volumeCustomMeshControl_1.getGeometryObjects().setObjects(refinementBlockPart);

        VolumeControlDualMesherSizeOption volumeControlDualMesherSizeOption_0 = volumeCustomMeshControl_1
                .getCustomConditions().get(VolumeControlDualMesherSizeOption.class);

        volumeControlDualMesherSizeOption_0.setVolumeControlBaseSizeOption(true);

        VolumeControlResurfacerSizeOption volumeControlResurfacerSizeOption_0 = volumeCustomMeshControl_1
                .getCustomConditions().get(VolumeControlResurfacerSizeOption.class);

        volumeControlResurfacerSizeOption_0.setVolumeControlBaseSizeOption(true);

        VolumeControlSize volumeControlSize_0 = volumeCustomMeshControl_1.getCustomValues()
                .get(VolumeControlSize.class);

        volumeControlSize_0.getRelativeSizeScalar().setValue(RelativeSize);
    }

    private void CreateFieldFunctions() {

        Simulation simulation = getActiveSimulation();

        UserFieldFunction KinEnergyFieldFunction = simulation.getFieldFunctionManager().createFieldFunction();

        KinEnergyFieldFunction.getTypeOption().setSelected(FieldFunctionTypeOption.Type.SCALAR);

        KinEnergyFieldFunction.setPresentationName("Kinetic Energy");

        KinEnergyFieldFunction.setFunctionName("KineticEnergy");

        KinEnergyFieldFunction.setDimensions(Dimensions.Builder().length(-3).energy(1).build());

        KinEnergyFieldFunction.setDefinition("${Density}*mag2($${Velocity})");

        UserFieldFunction AngMomentumFieldFunction = simulation.getFieldFunctionManager().createFieldFunction();

        AngMomentumFieldFunction.getTypeOption().setSelected(FieldFunctionTypeOption.Type.VECTOR);

        AngMomentumFieldFunction.setPresentationName("Angular Momentum");

        AngMomentumFieldFunction.setFunctionName("AngularMomentum");

        AngMomentumFieldFunction.setDimensions(Dimensions.Builder().length(-2).force(1).time(1).build());

        AngMomentumFieldFunction.setDefinition("${Density}*cross($${Centroid}, $${Velocity})");
    }

    private void CreateReportsAndPlots() {

        Simulation simulation = getActiveSimulation();

        MomentReport momentReport_3 = simulation.getReportManager().createReport(MomentReport.class);

        momentReport_3.setPresentationName("Total Moment");

        momentReport_3.getParts().setQuery(null);

        Region region_0 = simulation.getRegionManager().getRegion("Fluid");

        Boundary boundary_0 = region_0.getBoundaryManager().getBoundary("Rotor");

        Boundary boundary_1 = region_0.getBoundaryManager().getBoundary("Stator");

        momentReport_3.getParts().setObjects(boundary_0, boundary_1);

        MomentReport momentReport_4 = simulation.getReportManager().createReport(MomentReport.class);

        momentReport_4.setPresentationName("Stator Moment");

        momentReport_4.getParts().setQuery(null);

        momentReport_4.getParts().setObjects(boundary_1);

        MomentReport momentReport_5 = simulation.getReportManager().createReport(MomentReport.class);

        momentReport_5.setPresentationName("Rotor Moment");

        momentReport_5.getParts().setQuery(null);

        momentReport_5.getParts().setObjects(boundary_0);

        VolumeIntegralReport volumeIntegralReport_0 = simulation.getReportManager()
                .createReport(VolumeIntegralReport.class);

        volumeIntegralReport_0.setPresentationName("Kinetic Engery");

        UserFieldFunction userFieldFunction_0 = ((UserFieldFunction) simulation.getFieldFunctionManager()
                .getFunction("KineticEnergy"));

        volumeIntegralReport_0.setFieldFunction(userFieldFunction_0);

        volumeIntegralReport_0.getParts().setQuery(null);

        volumeIntegralReport_0.getParts().setObjects(region_0);

        VolumeIntegralReport volumeIntegralReport_1 = simulation.getReportManager()
                .createReport(VolumeIntegralReport.class);

        volumeIntegralReport_1.setPresentationName("Angular Momentum Z");

        UserFieldFunction userFieldFunction_1 = ((UserFieldFunction) simulation.getFieldFunctionManager()
                .getFunction("AngularMomentum"));

        VectorComponentFieldFunction vectorComponentFieldFunction_0 = ((VectorComponentFieldFunction) userFieldFunction_1
                .getComponentFunction(2));

        volumeIntegralReport_1.setFieldFunction(vectorComponentFieldFunction_0);

        volumeIntegralReport_1.getParts().setQuery(null);

        volumeIntegralReport_1.getParts().setObjects(region_0);

        ReportMonitor reportMonitor_3 = volumeIntegralReport_1.createMonitor();

        ReportMonitor reportMonitor_4 = volumeIntegralReport_0.createMonitor();

        ReportMonitor reportMonitor_5 = momentReport_5.createMonitor();

        ReportMonitor reportMonitor_6 = momentReport_4.createMonitor();

        ReportMonitor reportMonitor_7 = momentReport_3.createMonitor();

        StarUpdate starUpdate_0 = reportMonitor_7.getStarUpdate();

        if (IsSteady) {

            IterationUpdateFrequency iterationUpdateFrequency_0 = starUpdate_0.getIterationUpdateFrequency();

            iterationUpdateFrequency_0.setIterations(iterationUpdateFrequency);

            StarUpdate starUpdate_1 = reportMonitor_6.getStarUpdate();

            IterationUpdateFrequency iterationUpdateFrequency_1 = starUpdate_1.getIterationUpdateFrequency();

            iterationUpdateFrequency_1.setIterations(iterationUpdateFrequency);

            StarUpdate starUpdate_2 = reportMonitor_5.getStarUpdate();

            IterationUpdateFrequency iterationUpdateFrequency_2 = starUpdate_2.getIterationUpdateFrequency();

            iterationUpdateFrequency_2.setIterations(iterationUpdateFrequency);

            StarUpdate starUpdate_3 = reportMonitor_4.getStarUpdate();

            IterationUpdateFrequency iterationUpdateFrequency_3 = starUpdate_3.getIterationUpdateFrequency();

            iterationUpdateFrequency_3.setIterations(iterationUpdateFrequency);

            StarUpdate starUpdate_4 = reportMonitor_3.getStarUpdate();

            IterationUpdateFrequency iterationUpdateFrequency_4 = starUpdate_4.getIterationUpdateFrequency();

            iterationUpdateFrequency_4.setIterations(iterationUpdateFrequency);
        } else {
            // Set some update freq for timestep..?
        }

        MonitorPlot monitorPlot_1 = simulation.getPlotManager().createMonitorPlot(
                new NeoObjectVector(new Object[] { reportMonitor_3 }), "Angular Momentum Z Monitor Plot");

        MonitorPlot monitorPlot_2 = simulation.getPlotManager().createMonitorPlot(
                new NeoObjectVector(new Object[] { reportMonitor_4 }), "Kinetic Engery Monitor Plot");

        MonitorPlot monitorPlot_3 = simulation.getPlotManager()
                .createMonitorPlot(new NeoObjectVector(new Object[] { reportMonitor_7 }), "Total Moment Monitor Plot");

        monitorPlot_3.getDataSetManager()
                .addDataProviders(new NeoObjectVector(new Object[] { reportMonitor_5, reportMonitor_6 }));

        MonitorDataSet monitorDataSet_0 = ((MonitorDataSet) monitorPlot_3.getDataSetManager()
                .getDataSet("Total Moment Monitor"));

        LineStyle lineStyle_0 = monitorDataSet_0.getLineStyle();

        lineStyle_0.setColor(new DoubleVector(new double[] { 0.0, 0.0, 0.0 }));

        MonitorDataSet monitorDataSet_1 = ((MonitorDataSet) monitorPlot_3.getDataSetManager()
                .getDataSet("Rotor Moment Monitor"));

        LineStyle lineStyle_1 = monitorDataSet_1.getLineStyle();

        lineStyle_1.setColor(
                new DoubleVector(new double[] { 0.8899999856948853, 0.07000000029802322, 0.1899999976158142 }));

        monitorPlot_3.setTitle("Moments Monitor Plot");
    }

    private void setCalculatedProperties() {
        R2 = StatorDiameter * 0.5;
        R1 = RotorDiameter * 0.5;
        d = R2 - R1;
        MeshBaseSize = RelMeshBaseSize * d;
    }

    private void SetCurvatureDeviationMeshing(boolean enabled) {

        Simulation simulation_0 = getActiveSimulation();

        AutoMeshOperation autoMeshOperation_0 = ((AutoMeshOperation) simulation_0.get(MeshOperationManager.class)
                .getObject("Automated Mesh"));

        SurfaceCurvature surfaceCurvature_0 = autoMeshOperation_0.getDefaultValues().get(SurfaceCurvature.class);

        surfaceCurvature_0.setEnableCurvatureDeviationDist(enabled);

        surfaceCurvature_0.getCurvatureDeviationDistance().setValue(0.005);

        surfaceCurvature_0.setMaxNumPointsAroundCircle(8000.0);

        surfaceCurvature_0.setNumPointsAroundCircle(20.0);
    }

    private void CreateRotorThresholdMonitor() {

        Simulation simulation = getActiveSimulation();

        Units units = ((Units) simulation.getUnitsManager().getObject("m"));

        NullFieldFunction nullFieldFunction = ((NullFieldFunction) simulation.getFieldFunctionManager()
                .getFunction("NullFieldFunction"));

        ThresholdPart thresholdPart = simulation.getPartManager().createThresholdPart(
                new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] { 0.0, 1.0 }), units,
                nullFieldFunction, 0);

        thresholdPart.getInputParts().setQuery(null);

        Region region = simulation.getRegionManager().getRegion("Fluid");

        Boundary boundary = region.getBoundaryManager().getBoundary("Rotor");

        thresholdPart.getInputParts().setObjects(boundary);

        PrimitiveFieldFunction primitiveFieldFunction = ((PrimitiveFieldFunction) simulation.getFieldFunctionManager()
                .getFunction("Normal"));

        VectorComponentFieldFunction vectorComponentFieldFunction = ((VectorComponentFieldFunction) primitiveFieldFunction
                .getComponentFunction(2));

        thresholdPart.setFieldFunction(vectorComponentFieldFunction);

        thresholdPart.getRangeQuantities().setArray(new DoubleVector(new double[] { -0.1, 0.1 }));

        MomentReport momentReport = simulation.getReportManager().createReport(MomentReport.class);

        momentReport.setPresentationName("RotorOnlyMoment");

        momentReport.getParts().setQuery(null);

        momentReport.getParts().setObjects(thresholdPart);

        ReportMonitor reportMonitor = momentReport.createMonitor();
    }

    private void SetMassFlowInletFlowRate(String BoundaryName, String RegionName, double massFlowSi) {

        Simulation simulation_0 = getActiveSimulation();

        Region region_0 = simulation_0.getRegionManager().getRegion(RegionName);

        Boundary boundary_1 = region_0.getBoundaryManager().getBoundary(BoundaryName);

        MassFlowBoundary massFlowBoundary_0 = ((MassFlowBoundary) simulation_0.get(ConditionTypeManager.class)
                .get(MassFlowBoundary.class));

        boundary_1.setBoundaryType(massFlowBoundary_0);

        Units units_0 = simulation_0.getUnitsManager().getInternalUnits(new IntVector(
                new int[] { 1, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }));

        MassFlowRateProfile massFlowRateProfile_0 = boundary_1.getValues().get(MassFlowRateProfile.class);

        massFlowRateProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(massFlowSi);
    }

}
