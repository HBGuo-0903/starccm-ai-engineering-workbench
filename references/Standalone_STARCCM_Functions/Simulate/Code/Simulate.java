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

import star.passivescalar.*;
import star.segregatedenergy.*;
import star.segregatedflow.*;
import star.material.*;
import star.turbulence.*;
import star.flow.*;
import star.metrics.*;
import star.vis.*;

public class Simulate extends StarMacro {

  // Length units in [m]
  double RotorDiameter = 75e-3;
  double RotorHeight = 138.4e-3;
  double StatorDiameter = 97e-3;
  double StatorHeight = 140e-3;
  double RotorStartZ = 0.5e-3;
  double InletHoleDiameter = 5e-3;
  double InletHoleRadialPosition = 41e-3;
  double TubeLength = 10e-3;
  double ShaftDiameter = 10e-3;

  // RPM Units [1/min]
  double RPM = 1000;

  // Flow rate
  double flowRate = 1.1666666666666666E-6; // m3/s

  // Concentration
  boolean enableSpecies = true;
  boolean enableSaturationEq = true;
  boolean enableReaction = false;
  double concentrationNaSO4 = 5.2e-4; // mol/kgw
  double concentrationBaCl2 = 5.2e-4; // mol/kgw

  // Temperature
  double Temperature = 25; // C

  // Mesh
  double MeshBaseSize = 0.001; // m
  int NoPrismLayers = 10; // -
  int NoThinLayers = 10; // -
  double PrimLayerTotalThickness = 5e-4; // m
  double PrismLayerStretching = 1.5; // -

  // Time-Stepping
  boolean IsSteady = true;
  double TimeStep = 1e-5; // s

  // TurbulenceModelling
  String TurbulenceModel = "RANS-kOmega"; // "RANS-kOmega" / "LES" / "Laminar"
  String kOmegaConsituitive = "Cubic"; // "Cubic" / "Linear" / "QCR"

  // Simulation Settings
  double PhysialTimeRun1 = 720.0; // s

  // New FileName
  String SimName = "Sim_" + TurbulenceModel + "_" + UUID.randomUUID().toString() + ".sim";

  // Const;
  double molarMassH2O = 18.01528e-3; // kg / mole
  double densityOfWater = 997.561; // kg / m3
  String UserLibPath = "/home/mek/jroben/couetteCell/couette-cell/sim/libuser.so";
  int InitialRansSteps = 30000;

  public void execute() {

    SetAutoSave();
    Save();

    // Run RANS First
    // RunSteps(InitialRansSteps);

    swapToUnsteady();
    setTimeStep(TimeStep);
    
    RunSteps(10); // Needs to run a few URANS Steps before enabling Passive Scalar Model

    if (enableSpecies) {
      EnableSpecies();

      if (enableSaturationEq) {
        String[] yEtcs1 = { "yEtc_1-", "${yCl_1-} + ${yNa_1+}" };
        String[] yEtcs2 = { "yEtc_2-", "${ySO4_2-} + ${yBa_2+}" };
        createEtcFieldFuncs(yEtcs1);
        createEtcFieldFuncs(yEtcs2);
        addIsoThermal(Temperature);
        AddUserLib(UserLibPath);
        AddSaturationScene();
      }

    }

    RunSimulation();

  new StarScript(getActiveRootObject(), new File(resolvePath("Empty.java"))).play();


  }

  private void Save() {
    Simulation simulation = getActiveSimulation();

    String sessionDir = simulation.getSessionDir();
    String fullPath = sessionDir + "/Results/" + SimName;

    try {
      Files.createDirectories(Paths.get(sessionDir + "/Results"));
      simulation.saveState(fullPath);
    } catch (Exception ex) {
      simulation.println(ex);
    }
  }

  private void EnableSpecies() {
    String[] scalars = { "yBa_2+", "ySO4_2-", "yNa_1+", "yCl_1-" };


    // Inlet B
    double yNa2SO4_inletA = concentrationNaSO4 / (1.0 / molarMassH2O + concentrationNaSO4);
    double nu_Na = 2;
    double nu_SO4 = 1;
    double nu_Na2SO4 = 1;
    double yNa_inletA = (nu_Na / nu_Na2SO4) * yNa2SO4_inletA;
    double ySO4_inletA = (nu_SO4 / nu_Na2SO4) * yNa2SO4_inletA;

    // Inlet B
    double yBaCl2_inletB = concentrationBaCl2 / (1.0 / molarMassH2O + concentrationNaSO4);
    double nu_Cl = 2;
    double nu_Ba = 1;
    double nu_Ba2Cl = 1;
    double yBa_inletB = (nu_Ba / nu_Ba2Cl) * yBaCl2_inletB;
    double yCl_inletB = (nu_Cl / nu_Ba2Cl) * yBaCl2_inletB;

    double[] inletAConcentrations = { 0.0, ySO4_inletA, yNa_inletA, 0.0 };
    double[] inletBConcentrations = { yBa_inletB, 0.0, 0.0, yCl_inletB };

    AddPassiveScalar(scalars);
    SetPassiveScalarInlet("InletA", inletAConcentrations);
    SetPassiveScalarInlet("InletB", inletBConcentrations);
  }

  private void RunSteps(int steps) {
    Simulation simulation = getActiveSimulation();

    Solution solution = simulation.getSolution();

    simulation.getSimulationIterator().run(steps);
  }

  private void RunSimulation() {

    Simulation simulation = getActiveSimulation();

    Solution solution = simulation.getSolution();

    simulation.getSimulationIterator().run();
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
    
    StepStoppingCriterion stepStoppingCriterion = 
      ((StepStoppingCriterion) simulation.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps"));

    stepStoppingCriterion.setIsUsed(false);
  }

  private void SetPrismLayers(int noLayers, int noThinLayers, double prismLayerStretch,
      double prismLayerTotalThickness) {

    Simulation simulation = getActiveSimulation();

    AutoMeshOperation autoMeshOperation = ((AutoMeshOperation) simulation.get(MeshOperationManager.class)
        .getObject("Automated Mesh"));

    ThinNumLayers thinNumLayers = autoMeshOperation.getDefaultValues().get(ThinNumLayers.class);

    thinNumLayers.setLayers(noThinLayers);

    NumPrismLayers numPrismLayers = autoMeshOperation.getDefaultValues().get(NumPrismLayers.class);

    IntegerValue integerValuePrsimLayers = numPrismLayers.getNumLayersValue();

    integerValuePrsimLayers.getQuantity().setValue(noLayers);

    PrismLayerStretching prismLayerStretching = autoMeshOperation.getDefaultValues().get(PrismLayerStretching.class);

    prismLayerStretching.getStretchingQuantity().setValue(prismLayerStretch);

    PrismThickness prismThickness = autoMeshOperation.getDefaultValues().get(PrismThickness.class);

    prismThickness.getRelativeOrAbsoluteOption().setSelected(RelativeOrAbsoluteOption.Type.ABSOLUTE);

    ((ScalarPhysicalQuantity) prismThickness.getAbsoluteSizeValue()).setValue(prismLayerTotalThickness);
  }

  private void CreateAutomatedMeshOperation(double meshBaseSize) {

    Simulation simulation = getActiveSimulation();

    AutoMeshOperation autoMeshOperation = simulation.get(MeshOperationManager.class).createAutoMeshOperation(
        new StringVector(new String[] { "star.resurfacer.ResurfacerAutoMesher",
            "star.resurfacer.AutomaticSurfaceRepairAutoMesher", "star.dualmesher.DualAutoMesher",
            "star.prismmesher.PrismAutoMesher", "star.solidmesher.ThinAutoMesher" }),
        new NeoObjectVector(new Object[] {}));

    autoMeshOperation.setLinkOutputPartName(false);

    autoMeshOperation.getDefaultValues().get(BaseSize.class).setValue(meshBaseSize);

    autoMeshOperation.getInputGeometryObjects().setQuery(null);

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

    Boundary OutletA = CreateOutlet("OutletA", fluidRegion, simulation);
    Boundary OutletB = CreateOutlet("OutletB", fluidRegion, simulation);
    Boundary InletA = CreateInlet("InletA", fluidRegion, simulation);
    Boundary InletB = CreateInlet("InletB", fluidRegion, simulation);

  }

  private static Boundary CreateOutlet(String name, Region region, Simulation simulation) {
    Boundary boundary = region.getBoundaryManager().createEmptyBoundary();

    boundary.setPresentationName(name);

    OutletBoundary outletBoundary = ((OutletBoundary) simulation.get(ConditionTypeManager.class)
        .get(OutletBoundary.class));

    boundary.setBoundaryType(outletBoundary);

    return boundary;
  }

  private static Boundary CreateInlet(String name, Region region, Simulation simulation) {
    Boundary boundary = region.getBoundaryManager().createEmptyBoundary();

    boundary.setPresentationName(name);

    InletBoundary inletBoundary = ((InletBoundary) simulation.get(ConditionTypeManager.class).get(InletBoundary.class));

    boundary.setBoundaryType(inletBoundary);

    return boundary;
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
        .createUnitePartsOperation(new NeoObjectVector(new Object[] { simpleCylinderPart_1, simpleCylinderPart_2 }));

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
        .createUnitePartsOperation(new NeoObjectVector(new Object[] { simpleCylinderPart_1, simpleCylinderPart_2,
            simpleCylinderPart_3, simpleCylinderPart_4, simpleCylinderPart_5 }));

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

    SubtractPartsOperation subtractPartsOperation = (SubtractPartsOperation) simulation.get(MeshOperationManager.class)
        .createSubtractPartsOperation(new NeoObjectVector(new Object[] { meshOperationPart_4, meshOperationPart_5 }));

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

    Units units_1 = simulation.getUnitsManager().getInternalUnits(
        new IntVector(new int[] { 0, 3, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }));

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

    VelocityMagnitudeProfile velocityMagnitudeProfile_0 = boundary_1.getValues().get(VelocityMagnitudeProfile.class);

    velocityMagnitudeProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity()
        .setDefinition("$InletVelocity");

    Boundary boundary_2 = region_0.getBoundaryManager().getBoundary("InletB");

    VelocityMagnitudeProfile velocityMagnitudeProfile_1 = boundary_2.getValues().get(VelocityMagnitudeProfile.class);

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

    rotorBoundary.getConditions().get(WallSlidingOption.class).setSelected(WallSlidingOption.Type.LOCAL_ROTATION_RATE);

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

    VolumeControlSize volumeControlSize_0 = volumeCustomMeshControl_1.getCustomValues().get(VolumeControlSize.class);

    volumeControlSize_0.getRelativeSizeScalar().setValue(RelativeSize * 100);
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

      iterationUpdateFrequency_0.setIterations(50);

      StarUpdate starUpdate_1 = reportMonitor_6.getStarUpdate();

      IterationUpdateFrequency iterationUpdateFrequency_1 = starUpdate_1.getIterationUpdateFrequency();

      iterationUpdateFrequency_1.setIterations(50);

      StarUpdate starUpdate_2 = reportMonitor_5.getStarUpdate();

      IterationUpdateFrequency iterationUpdateFrequency_2 = starUpdate_2.getIterationUpdateFrequency();

      iterationUpdateFrequency_2.setIterations(50);

      StarUpdate starUpdate_3 = reportMonitor_4.getStarUpdate();

      IterationUpdateFrequency iterationUpdateFrequency_3 = starUpdate_3.getIterationUpdateFrequency();

      iterationUpdateFrequency_3.setIterations(50);

      StarUpdate starUpdate_4 = reportMonitor_3.getStarUpdate();

      IterationUpdateFrequency iterationUpdateFrequency_4 = starUpdate_4.getIterationUpdateFrequency();

      iterationUpdateFrequency_4.setIterations(50);
    } else {
      // Set some update freq for timestep..?
    }

    MonitorPlot monitorPlot_1 = simulation.getPlotManager()
        .createMonitorPlot(new NeoObjectVector(new Object[] { reportMonitor_3 }), "Angular Momentum Z Monitor Plot");

    MonitorPlot monitorPlot_2 = simulation.getPlotManager()
        .createMonitorPlot(new NeoObjectVector(new Object[] { reportMonitor_4 }), "Kinetic Engery Monitor Plot");

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

    lineStyle_1
        .setColor(new DoubleVector(new double[] { 0.8899999856948853, 0.07000000029802322, 0.1899999976158142 }));

    monitorPlot_3.setTitle("Moments Monitor Plot");
  }

  private void AddPassiveScalar(String[] scalars) {

    Simulation simulation = getActiveSimulation();
    PhysicsContinuum physicsContinuum = ((PhysicsContinuum) simulation.getContinuumManager().getContinuum("Physics 1"));
    physicsContinuum.enable(PassiveScalarModel.class);

    for (String scalar : scalars) {
      PassiveScalarModel passiveScalarModel = physicsContinuum.getModelManager().getModel(PassiveScalarModel.class);

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
      ScalarProfile scalarProfile_0 = passiveScalarProfile.getMethod(CompositeArrayProfileMethod.class).getProfile(i);
      scalarProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(inletConcentrations[i]);

    }

  }

  private void createEtcFieldFuncs(String[] yEtc) {

    Simulation simulation = getActiveSimulation();

    UserFieldFunction userFieldFunction = simulation.getFieldFunctionManager().createFieldFunction();

    userFieldFunction.getTypeOption().setSelected(FieldFunctionTypeOption.Type.SCALAR);

    userFieldFunction.setPresentationName(yEtc[0]);
    userFieldFunction.setFunctionName(yEtc[0]);
    userFieldFunction.setDefinition(yEtc[1]);

  }

  private void addIsoThermal(double TempC) {

    Simulation simulation = getActiveSimulation();

    PhysicsContinuum physicsContinuum = ((PhysicsContinuum) simulation.getContinuumManager().getContinuum("Physics 1"));

    physicsContinuum.enable(SegregatedFluidIsothermalModel.class);

    SegregatedFluidIsothermalModel segregatedFluidIsothermalModel_0 = physicsContinuum.getModelManager()
        .getModel(SegregatedFluidIsothermalModel.class);

    Units units_0 = ((Units) simulation.getUnitsManager().getObject("C"));

    segregatedFluidIsothermalModel_0.getContinuumTemperature().setUnits(units_0);

    segregatedFluidIsothermalModel_0.getContinuumTemperature().setValue(TempC);
  }

  private void SetFreezeFlow(boolean freeze) {

    Simulation simulation = getActiveSimulation();

    SegregatedFlowSolver segregatedFlowSolver = ((SegregatedFlowSolver) simulation.getSolverManager()
        .getSolver(SegregatedFlowSolver.class));

    segregatedFlowSolver.setFreezeFlow(freeze);
  }

  private void AddUserLib(String libPath) {

    Simulation simulation = getActiveSimulation();

    UserLibrary userLibrary_1 = simulation.getUserFunctionManager().createUserLibrary(resolvePath(libPath));

  }

  private void AddSaturationScene() {

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

    scene_0.setPresentationName("Saturation Rate");

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

    deltaTimeUpdateFrequency_0.setDeltaTime("0.02", units_0);

    sceneUpdate_0.setAnimationFilenameBase("SaturationRate");

    sceneUpdate_0.setAnimationFilePath("SaturationRate");

    sceneUpdate_0.setSaveAnimation(true);

    scalarDisplayer_0.getInputParts().setQuery(null);

    PlaneSection planeSection_0 = ((PlaneSection) simulation.getPartManager().getObject("XY Plane"));

    PlaneSection planeSection_1 = ((PlaneSection) simulation.getPartManager().getObject("XZ Plane"));

    PlaneSection planeSection_2 = ((PlaneSection) simulation.getPartManager().getObject("YZ Plane"));

    scalarDisplayer_0.getInputParts().setObjects(planeSection_0, planeSection_1, planeSection_2);

    PrimitiveFieldFunction primitiveFieldFunction_3 = ((PrimitiveFieldFunction) simulation.getFieldFunctionManager()
        .getFunction("UserDebyeHuckelSaturationIndex"));

    scalarDisplayer_0.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_3);

  }

  private void swapToUnsteady() {

    Simulation simulation = getActiveSimulation();

    PhysicsContinuum physicsContinuum = ((PhysicsContinuum) simulation.getContinuumManager().getContinuum("Physics 1"));

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

}
