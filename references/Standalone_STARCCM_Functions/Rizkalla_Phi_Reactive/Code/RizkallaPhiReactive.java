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
public class RizkallaPhiReactive extends StarMacro {

        // Physical Time to run
        static final double simulationTime = __SimulationTime__;

        // Misc
        static final String PHYSICSNAME = "Physics 1";
        static final String REGIONNAME = "Fluid";
        static final String WALLNAME = "Fluid.Container.Wall";

        // Fluid Properties
        static final double density = 997.561; // kg/m3
        static final double viscosity = 8.8871E-4; // Pa s

        // Concentration
        static final double concentrationNaSO4 = __concentrationNa2SO4__; // mol/kgw
        static final double concentrationBaCl2 = __concentrationBaCl2__; // mol/kgw
        static final String[] scalars = { "mSO4_2-", "mNa_1+", "mBa_2+", "mCl_1-", "BariteScale" };
        static final double D_SO4 = 2.0 * 1.06e-5 * 1e-4;
        static final double D_Na = 1.0 * 1.334e-5 * 1e-4;
        static final double D_Ba = 2.0 * 0.847e-5 * 1e-4;
        static final double D_Cl = 1.0 * 2.032e-5 * 1e-4;
        static final double molarMassH2O = 18.01528e-3; // kg / mole
        static final double molarMassBarite = 233.39e-3; // kg / mole
        static final double DensityBarite = 4480; // kg / mole
        static final double k = __RateConstant__;

        // Turbulent Schmidt Number
        static final double TurbulentSchmidtNumber = __TurbulentSchmidtNumber__;

        // Temperature
        static final double Temperature = __Temperature__; // C

        // Time-Stepping
        boolean IsSteady = true;
        double targetCourant = 0.8;
        double TimeStep = targetCourant * 1e-3; // s

        // TurbulenceModelling
        String TurbulenceModel = "RANS-RST"; // "RANS-kOmega" / "LES" / "Laminar" / "RANS-RST" / "RANS-kEpsilon"

        // FileName
        String SimName = "";
        String SessionDirectory = "";

        static final int iterationUpdateFrequency = 5;
        static final int innerIterations = 1;

        boolean isReactive = true;

        public void execute() {
                Simulation simulation = getActiveSimulation();
                SessionDirectory = simulation.getSessionDir();
                StartRstReactiveSimulation();

        }

        public String getFileName() {
                return String.format("Rizkalla_%s_ReactivePhiTimeSeriesLes.sim", getRpmString());
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

        private void StartRstReactiveSimulation() {

                // Reaction Functions
                Dimensions dimensionless = Dimensions.Builder().build();
                Dimensions reactivity = Dimensions.Builder().length(-3).time(-1).build();

                SwapToLes();

                setTimeStep(TimeStep);
                EnableAdaptiveTimeStepping(targetCourant, targetCourant * 5);
                enableSecondOrderTimestep();
                setInnerIterationCount(innerIterations);

                // Enable passive scalars and static temp
                EnableSpecies();
                addIsoThermal(Temperature);
                SetScalarSchmidtNumber("mNa_1+", viscosity / (D_Na * density));
                SetScalarSchmidtNumber("mSO4_2-", viscosity / (D_SO4 * density));
                SetScalarSchmidtNumber("mBa_2+", viscosity / (D_Ba * density));
                SetScalarSchmidtNumber("mCl_1-", viscosity / (D_Cl * density));
                for (int i = 0; i < scalars.length - 1; i++) { // -1 becuse excluding the solid barite
                        SetScalarTurbulentSchmidtNumber(scalars[i], TurbulentSchmidtNumber);
                }

                createScalarFieldFunction("InitialConcentationSO4", "InitialConcentationSO4",
                                "($${Centroid}[0] > 0 ? 1 : 0)*1*" + concentrationNaSO4, dimensionless);
                createScalarFieldFunction("InitialConcentationNa", "InitialConcentationNa",
                                "($${Centroid}[0] > 0 ? 1 : 0)*2*" + concentrationNaSO4, dimensionless);
                createScalarFieldFunction("InitialConcentationBa", "InitialConcentationBa",
                                "($${Centroid}[0] < 0 ? 1 : 0)*1*" + concentrationBaCl2, dimensionless);
                createScalarFieldFunction("InitialConcentationCl", "InitialConcentationCl",
                                "($${Centroid}[0] < 0 ? 1 : 0)*2*" + concentrationBaCl2, dimensionless);

                SetScalarInitialConcentrationToFieldFunction(0, "InitialConcentationSO4");
                SetScalarInitialConcentrationToFieldFunction(1, "InitialConcentationNa");
                SetScalarInitialConcentrationToFieldFunction(2, "InitialConcentationBa");
                SetScalarInitialConcentrationToFieldFunction(3, "InitialConcentationCl");
                InitializeSolution();

                // Reactive part
                CreateSpeciesFieldFunctions();
                AddUserLib(SessionDirectory + "/libuser.so");

                createScalarFieldFunction("Saturation Rate", "SR", "pow(10,${UserPitzerSaturationIndex})",
                                dimensionless);
                setReactionFieldFunctions(isReactive);

                createScalarFieldFunction("Reaction Parameter", "ReactionParameter", "${mBa_2+} * ${mSO4_2-}",
                                reactivity);
                createScalarFieldFunction("Reaction Parameter 2", "ReactionParameter2",
                                "${mBa_2+} * ${mSO4_2-} * (1 - exp(${DeltaG_bulk} / (${R} * ${Temperature})))",
                                reactivity);

                createVolumeAverageReport("Average Reaction Parameter", "ReactionParameter", REGIONNAME);
                createVolumeAverageReport("Average Reaction Parameter 2", "ReactionParameter2", REGIONNAME);
                createVolumeAverageReport("Average mSO4_2-", "mSO4_2-", REGIONNAME);
                createVolumeAverageReport("Average mBa_2+", "mBa_2+", REGIONNAME);

                recordAndPlotReport("Average Reaction Parameter");
                recordAndPlotReport("Average Reaction Parameter 2");
                recordAndPlotReport("Average mSO4_2-");
                recordAndPlotReport("Average mBa_2+");

                // Save and prepare
                SetAutoSave();
                SimName = getFileName();
                saveAs(SimName);

                RunTime(simulationTime);

        }

        public void setReactionFieldFunctions(boolean enableReaction) {

                Dimensions dimensionless = Dimensions.Builder().build();
                CreateGlobalDimensionlessParameter("k", "" + k);
                CreateGlobalDimensionlessParameter("R", "8.314");

                createScalarFieldFunction("Bulk Reaction Rate Constant", "K_bulk", "${k}", dimensionless);
                createScalarFieldFunction("Bulk DeltaG", "DeltaG_bulk", "-${R} * ${Temperature} * log(max(1, ${SR}))",
                                dimensionless);

                createScalarFieldFunction("Bulk Reaction Rate", "R_Bulk",
                                "${K_bulk} * ${mBa_2+} * ${mSO4_2-} * (1 - exp(${DeltaG_bulk} / (${R} * ${Temperature})))",
                                dimensionless);
                createScalarFieldFunction("Bulk R_Ba", "R_Ba_Bulk", "-${R_Bulk}", dimensionless);
                createScalarFieldFunction("Bulk R_SO4", "R_SO4_Bulk", "-${R_Bulk}", dimensionless);

                createScalarFieldFunction("Bulk dRdmBa", "dRdmBa_Bulk",
                                "${K_bulk} * ${mSO4_2-} * (1 - exp(${DeltaG_bulk} / (${R} * ${Temperature})))",
                                dimensionless);
                createScalarFieldFunction("Bulk dRdmSO4", "dRdmSO4_Bulk",
                                "${K_bulk} * ${mBa_2+} * (1 - exp(${DeltaG_bulk} / (${R} * ${Temperature})))",
                                dimensionless);
                createScalarFieldFunction("Bulk dRdmBaSO4", "dRdmBaSO4_Bulk", "-(${dRdmSO4_Bulk} + ${dRdmBa_Bulk})",
                                dimensionless);

                if (enableReaction) {
                        SetPassiveScalarMassSource(0, "R_SO4_Bulk", "dRdmSO4_Bulk");
                        SetPassiveScalarMassSource(2, "R_Ba_Bulk", "dRdmBa_Bulk");
                        SetPassiveScalarMassSource(4, "R_Bulk", "dRdmBaSO4_Bulk");
                }
        }

        private void recordAndPlotReport(String reportName) {

                Simulation simulation = getActiveSimulation();
                ReportManager reportManager = simulation.getReportManager();
                Report report = reportManager.getReport​(reportName);

                simulation.getMonitorManager().createMonitorAndPlot(new NeoObjectVector(new Object[] { report }), true,
                                "%1$s Plot");

                ReportMonitor reportMonitor = ((ReportMonitor) simulation.getMonitorManager()
                                .getMonitor(reportName + " Monitor"));

                MonitorPlot monitorPlot = simulation.getPlotManager().createMonitorPlot(
                                new NeoObjectVector(new Object[] { reportMonitor }), reportName + " Time Series");
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

        private void createVolumeAverageReport(String reportName, String targetFieldFunction, String regionName) {
                Simulation simulation = getActiveSimulation();
                VolumeAverageReport volumeAverageReport = simulation.getReportManager()
                                .createReport(VolumeAverageReport.class);
                volumeAverageReport.setPresentationName(reportName);
                FieldFunction fieldFunctionHandle = simulation.getFieldFunctionManager()
                                .getFunction(targetFieldFunction);
                volumeAverageReport.setFieldFunction(fieldFunctionHandle);
                Region region = simulation.getRegionManager().getRegion(regionName);
                volumeAverageReport.getParts().setObjects(region);
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

        private void EnableAdaptiveTimeStepping(double getTargetMeanCfl, double getTargetMaxCfl) {

                Simulation simulation = getActiveSimulation();
                PhysicsContinuum physicsContinuum = ((PhysicsContinuum) simulation.getContinuumManager()
                                .getContinuum(PHYSICSNAME));
                physicsContinuum.enable(AdaptiveTimeStepModel.class);

                AdaptiveTimeStepModel adaptiveTimeStepModel = physicsContinuum.getModelManager()
                                .getModel(AdaptiveTimeStepModel.class);
                ConvectiveCflTimeStepProvider convectiveCflTimeStepProvider = adaptiveTimeStepModel
                                .getTimeStepProviderManager().createObject(ConvectiveCflTimeStepProvider.class);

                convectiveCflTimeStepProvider.getTargetMeanCfl().setValue(getTargetMeanCfl);
                convectiveCflTimeStepProvider.getTargetMaxCfl().setValue(getTargetMaxCfl);
        }

        private void SetScalarInitialConcentration(int no, double concentration) {
                Simulation simulation = getActiveSimulation();
                PhysicsContinuum physicsContinuum = ((PhysicsContinuum) simulation.getContinuumManager()
                                .getContinuum(PHYSICSNAME));
                PassiveScalarProfile passiveScalarProfile = physicsContinuum.getInitialConditions()
                                .get(PassiveScalarProfile.class);
                passiveScalarProfile.setMethod(CompositeArrayProfileMethod.class);
                ScalarProfile scalarProfile = passiveScalarProfile.getMethod(CompositeArrayProfileMethod.class)
                                .getProfile(no);
                scalarProfile.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(concentration);
        }

        private void SetScalarInitialConcentrationToFieldFunction(int no, String fieldFiunctionName) {
                Simulation simulation = getActiveSimulation();
                PhysicsContinuum physicsContinuum = ((PhysicsContinuum) simulation.getContinuumManager()
                                .getContinuum(PHYSICSNAME));
                PassiveScalarProfile passiveScalarProfile = physicsContinuum.getInitialConditions()
                                .get(PassiveScalarProfile.class);
                passiveScalarProfile.setMethod(CompositeArrayProfileMethod.class);
                ScalarProfile scalarProfile = passiveScalarProfile.getMethod(CompositeArrayProfileMethod.class)
                                .getProfile(no);
                FieldFunction fieldFunction = ((FieldFunction) simulation.getFieldFunctionManager()
                                .getFunction(fieldFiunctionName));
                scalarProfile.getMethod(FunctionScalarProfileMethod.class).setFieldFunction(fieldFunction);
        }

        private void SetScalarSchmidtNumber(String scalar, double Schmidt) {

                Simulation simulation = getActiveSimulation();

                PhysicsContinuum physicsContinuum = ((PhysicsContinuum) simulation.getContinuumManager()
                                .getContinuum(PHYSICSNAME));

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
                                .getContinuum(PHYSICSNAME));

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

        private void SwapToLes() {

                Simulation simulation = getActiveSimulation();
                PhysicsContinuum physicsContinuum = ((PhysicsContinuum) simulation.getContinuumManager()
                                .getContinuum(PHYSICSNAME));

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
                        physicsContinuum.disableModel(
                                        physicsContinuum.getModelManager().getModel(TurbulentModel.class));
                        physicsContinuum.disableModel(
                                        physicsContinuum.getModelManager().getModel(RansTurbulenceModel.class));
                        physicsContinuum.disableModel(
                                        physicsContinuum.getModelManager().getModel(KOmegaTurbulence.class));
                        physicsContinuum.disableModel(
                                        physicsContinuum.getModelManager().getModel(SstKwTurbModel.class));
                        physicsContinuum.disableModel(
                                        physicsContinuum.getModelManager().getModel(KwAllYplusWallTreatment.class));
                        physicsContinuum.disableModel(
                                        physicsContinuum.getModelManager().getModel(GammaTransitionModel.class));

                } else if (TurbulenceModel == "RANS-RST") {
                        physicsContinuum.disableModel(
                                        physicsContinuum.getModelManager().getModel(RansTurbulenceModel.class));
                        physicsContinuum.disableModel(
                                        physicsContinuum.getModelManager().getModel(ReynoldsStressTurbulence.class));
                        physicsContinuum.disableModel(physicsContinuum.getModelManager().getModel(EbRsTurbModel.class));
                        physicsContinuum.disableModel(
                                        physicsContinuum.getModelManager().getModel(EbRsAllYplusWallTreatment.class));

                } else if (TurbulenceModel == "RANS-kEpsilon") {
                        physicsContinuum.disableModel(
                                        physicsContinuum.getModelManager().getModel(RansTurbulenceModel.class));
                        physicsContinuum.disableModel(
                                        physicsContinuum.getModelManager().getModel(KEpsilonTurbulence.class));
                        physicsContinuum.disableModel(
                                        physicsContinuum.getModelManager().getModel(RkeTwoLayerTurbModel.class));
                        physicsContinuum.disableModel(physicsContinuum.getModelManager()
                                        .getModel(KeTwoLayerAllYplusWallTreatment.class));
                } else if (TurbulenceModel == "LES") {

                } else {
                        physicsContinuum.disableModel(physicsContinuum.getModelManager().getModel(LaminarModel.class));
                }

                TurbulenceModel = "LES";
                physicsContinuum.enable(TurbulentModel.class);
                physicsContinuum.enable(LesTurbulenceModel.class);
                physicsContinuum.enable(WaleSgsModel.class);
                physicsContinuum.enable(LesAllYplusWallTreatment.class);

                setLesCw(0.325);
        }

        private void setLesCw(double Cw) {
                Simulation simulation = getActiveSimulation();
                PhysicsContinuum physicsContinuum = ((PhysicsContinuum) simulation.getContinuumManager()
                                .getContinuum(PHYSICSNAME));
                WaleSgsModel waleSgsModel = physicsContinuum.getModelManager().getModel(WaleSgsModel.class);
                waleSgsModel.setCw(Cw);
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

                createDimensionlessFieldFuncs("BariteVolumeFraction",
                                "${BariteScale} * ${Density} * ${MolarMassBarite} / ${DensityBarite}");

        }

        private void EnableSpecies() {
                boolean[] convectionOnly = { false, false, false, false, true };
                AddPassiveScalar(scalars, convectionOnly);
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

        private void addIsoThermal(double TempC) {

                Simulation simulation = getActiveSimulation();

                PhysicsContinuum physicsContinuum = ((PhysicsContinuum) simulation.getContinuumManager()
                                .getContinuum(PHYSICSNAME));

                physicsContinuum.enable(SegregatedFluidIsothermalModel.class);

                SegregatedFluidIsothermalModel segregatedFluidIsothermalModel_0 = physicsContinuum.getModelManager()
                                .getModel(SegregatedFluidIsothermalModel.class);

                Units units_0 = ((Units) simulation.getUnitsManager().getObject("C"));

                segregatedFluidIsothermalModel_0.getContinuumTemperature().setUnits(units_0);

                segregatedFluidIsothermalModel_0.getContinuumTemperature().setValue(TempC);
        }

        private void AddPassiveScalar(String[] scalars, boolean[] convectionOnly) {

                Simulation simulation = getActiveSimulation();
                PhysicsContinuum physicsContinuum = ((PhysicsContinuum) simulation.getContinuumManager()
                                .getContinuum(PHYSICSNAME));
                physicsContinuum.enable(PassiveScalarModel.class);

                for (int i = 0; i < scalars.length; i++) {
                        String scalar = scalars[i];
                        PassiveScalarModel passiveScalarModel = physicsContinuum.getModelManager()
                                        .getModel(PassiveScalarModel.class);

                        PassiveScalarMaterial passiveScalarMaterial = passiveScalarModel.getPassiveScalarManager()
                                        .createPassiveScalarMaterial(PassiveScalarMaterial.class);

                        if (convectionOnly[i]) {
                                passiveScalarMaterial.getTransportOption()
                                                .setSelected(PassiveScalarTransportOption.Type.CONVECTION_ONLY);
                        }
                        passiveScalarMaterial.getClipMode().setSelected(PassiveScalarClipMode.Type.CLIP_BOTH);

                        passiveScalarMaterial.setMaxAllowable(1.0);

                        passiveScalarMaterial.setPresentationName(scalar);

                        if (TurbulenceModel == "RANS-RST" && !convectionOnly[i]) {
                                passiveScalarMaterial.setUseGGDH(true);
                        }
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

        private void SetNormalStressOption(boolean activated) {

                if (TurbulenceModel == "RANS-kOmega") {
                        Simulation simulation = getActiveSimulation();

                        PhysicsContinuum physicsContinuum = ((PhysicsContinuum) simulation.getContinuumManager()
                                        .getContinuum(PHYSICSNAME));

                        SstKwTurbModel sstKwTurbModel = physicsContinuum.getModelManager()
                                        .getModel(SstKwTurbModel.class);

                        sstKwTurbModel.setNormalStressOption(activated);
                }
        }

        private void swapToUnsteady() {

                if (IsSteady) {
                        IsSteady = false;
                        Simulation simulation = getActiveSimulation();

                        PhysicsContinuum physicsContinuum = ((PhysicsContinuum) simulation.getContinuumManager()
                                        .getContinuum(PHYSICSNAME));
                        SteadyModel steadyModel = physicsContinuum.getModelManager().getModel(SteadyModel.class);

                        physicsContinuum.disableModel(steadyModel);
                        physicsContinuum.enable(ImplicitUnsteadyModel.class);
                }
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

        private void SetPassiveScalarMassSource(int scalarInd, String sourceFieldFunc,
                        String sourceDerivativeFieldFunc) {

                Simulation simulation = getActiveSimulation();

                Region region = simulation.getRegionManager().getRegion(REGIONNAME);

                region.getConditions().get(PassiveScalarUserSourceOption.class)
                                .setSelected(PassiveScalarUserSourceOption.Type.EXPLICIT_DENSITY);

                PassiveScalarUserSource passiveScalarUserSource = region.getValues().get(PassiveScalarUserSource.class);

                passiveScalarUserSource.setMethod(CompositeArrayProfileMethod.class);

                ScalarProfile scalarProfile = passiveScalarUserSource.getMethod(CompositeArrayProfileMethod.class)
                                .getProfile(scalarInd);
                scalarProfile.setMethod(FunctionScalarProfileMethod.class);
                UserFieldFunction userFieldFunction = ((UserFieldFunction) simulation.getFieldFunctionManager()
                                .getFunction(sourceFieldFunc));
                scalarProfile.getMethod(FunctionScalarProfileMethod.class).setFieldFunction(userFieldFunction);
                PassiveScalarUserSourceDerivative passiveScalarUserSourceDerivative = region.getValues()
                                .get(PassiveScalarUserSourceDerivative.class);

                passiveScalarUserSourceDerivative.setMethod(CompositeArrayProfileMethod.class);

                ScalarProfile scalarProfile_1 = passiveScalarUserSourceDerivative
                                .getMethod(CompositeArrayProfileMethod.class).getProfile(scalarInd);
                scalarProfile_1.setMethod(FunctionScalarProfileMethod.class);
                UserFieldFunction userFieldFunction_1 = ((UserFieldFunction) simulation.getFieldFunctionManager()
                                .getFunction(sourceDerivativeFieldFunc));
                scalarProfile_1.getMethod(FunctionScalarProfileMethod.class).setFieldFunction(userFieldFunction_1);
        }
}
