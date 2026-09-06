package myStarJavaMacros;

import star.common.ImplicitUnsteadyModel;
import star.common.PhysicsContinuum;
import star.common.PrimitiveFieldFunction;
import star.common.Simulation;
import star.common.StarMacro;
import star.common.XYPlot;
import star.cosimulation.abaqus.AbaqusCoSimulationModel;
import star.cosimulation.common.CoSimulationModel;
import star.flow.ConstantDensityModel;
import star.keturb.KEpsilonTurbulence;
import star.keturb.KeTwoLayerAllYplusWallTreatment;
import star.keturb.RkeTwoLayerTurbModel;
import star.material.SingleComponentLiquidModel;
import star.metrics.ThreeDimensionalModel;
import star.segregatedflow.SegregatedFlowModel;
import star.turbulence.RansTurbulenceModel;
import star.turbulence.TurbulentModel;
import star.vis.LinePart;
import star.vis.Scene;
import starClasses.CoSimulationAbaqus;
import starClasses.ContiuumBuilder;
import starClasses.DataReader;
import starClasses.DerivedParts;
import starClasses.DirectedMesher8_02_008;
import starClasses.FieldFunctions;
import starClasses.GeoData;
import starClasses.GeometryBuilder;
import starClasses.MeshMorpher;
import starClasses.RegionBuilder;
import starClasses.ReportsMonitorsPlots;
import starClasses.Scenes;
import starClasses.SolversNode;
import starClasses.StoppingCriteria;

public class Implicit_FSI extends StarMacro 
{

	public void execute() 
	{
		String folder = "D:/users/cj8q5/desktop/Data/";
		
		// Co-Simulation settings and variables
		String abaqusFilePath = "D:\\users\\cj8q5\\Simulations\\June 2013\\FSI_214_32mil_Explicit_Coupling\\Explicit_FreePlate.inp";
		double couplingTimeStep = 0.5;
		int numAbaqusCPUs = 8;
		String abqExecutableFileName = "abq6122.bat";
		int numExchanges = 100;
		int iterationsPerExchange = 100;
		double deflectionUnderRelax = 0.1;
		
		// Star-CCM+ settings and variables
		String sketchPlane = "YZ";
		Simulation activeSim = getActiveSimulation();
		
		// Reading in the geometry parameters from the external file
		DataReader reader = new DataReader();
		reader.readGeometryData(folder + "Plate_Geometry_Input.txt");
		GeoData geoData = reader.getGeoDetails();
		
		// Grabbing the geometry parameters from the GeoData object
		double plateLength = geoData.getPlateLength();
		double plateHeight = geoData.getPlateHeight();
		double wettedPlateWidth = geoData.getPlateWidth();
		double smallChannelHeight = geoData.getSmallChannelHeight();
		double largeChannelHeight = geoData.getLargeChannelHeight();
		double inletLength = geoData.getInletLength();
		double outletLength = geoData.getOutletLength();
		
		double[] initialVel = {0.0, -2.14, 0.0};
		double inletVel = Math.abs((smallChannelHeight + largeChannelHeight)/(smallChannelHeight + largeChannelHeight + plateHeight)*initialVel[1]);
				
		// Specifying the geometry parameters for each part
		double extrude = wettedPlateWidth; // Value for the length of all extrudes
		String[] fluidSurfaceNames = {"Outlet", "Front", "Inlet", "Back", "Right", "Left", "PlateBottom.FSI", "PlateBack.FSI", "PlateTop.FSI", "PlateFront.FSI"};

		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			GEOMETRY NODE */
		// Building the parts
		double[] X = {-outletLength, plateLength + inletLength, 0, plateLength};
		double[] Y = {-largeChannelHeight, plateHeight + smallChannelHeight, 0, plateHeight};
		GeometryBuilder fluid = new GeometryBuilder(activeSim, "Fluid", sketchPlane);	
		fluid.boxWithVoidBuilder(X, Y, extrude);
		fluid.splitSurface(89, fluidSurfaceNames, true);
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			REGION NODE */
		// Building the fluid regions
		RegionBuilder fluidRegion = new RegionBuilder(activeSim, "Fluid");
		fluidRegion.part2Region("Fluid", false);
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			PHYSICS NODE */
		// Creating the physics continuum
		ContiuumBuilder physics = new ContiuumBuilder(activeSim);
		PhysicsContinuum fluidPhysics = physics.createPhysicsContinua("Water");
		
		fluidPhysics.enable(ThreeDimensionalModel.class);
		fluidPhysics.enable(ImplicitUnsteadyModel.class);
		fluidPhysics.enable(SingleComponentLiquidModel.class);
		fluidPhysics.enable(SegregatedFlowModel.class);
		fluidPhysics.enable(ConstantDensityModel.class);
		fluidPhysics.enable(TurbulentModel.class);
		fluidPhysics.enable(RansTurbulenceModel.class);
		fluidPhysics.enable(KEpsilonTurbulence.class);
		fluidPhysics.enable(RkeTwoLayerTurbModel.class);
		fluidPhysics.enable(KeTwoLayerAllYplusWallTreatment.class);
		fluidPhysics.enable(CoSimulationModel.class);
		fluidPhysics.enable(AbaqusCoSimulationModel.class);
		
		// Setting the initial velocity in all cells
		physics.setInitialConditionsVel(fluidPhysics, initialVel);
		
		// Setting the inlet surfaces/boundaries to velocity inlets and setting the inlet velocity
		fluidRegion.setBoundaryCondition("Inlet", "Velocity Inlet", new double[] {0, -1, 0}, inletVel);
		
		// Setting the outlet surfaces/boundaries to pressure outlets
		fluidRegion.setBoundaryCondition("Outlet", "Pressure Outlet", new double[] {0, 1, 0}, 0);
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			DIRECTED MESHER NODE */	
		DirectedMesher8_02_008 mesh = new DirectedMesher8_02_008(activeSim, "Fluid");
		mesh.setSourceTargetSurfaces("Left", "Right");
		mesh.createPatchMesh();
		
		// Creating patch curves in the inlet plenum
		mesh.splitPatchCurve(3, new double[] {0, plateLength + inletLength, plateHeight});
		mesh.createPatchCurve(7, 8);
		
		mesh.splitPatchCurve(8, new double[] {0, plateLength + inletLength, 0});
		mesh.createPatchCurve(4, 9);
		
		// Creating patch curves in the plate channels
		mesh.splitPatchCurve(2, new double[] {0, 0, -largeChannelHeight});
		mesh.createPatchCurve(5, 10);
		
		mesh.splitPatchCurve(12, new double[] {0, plateLength, -largeChannelHeight});
		mesh.createPatchCurve(4, 11);
		
		mesh.splitPatchCurve(1, new double[] {0, 0, plateHeight + smallChannelHeight});
		mesh.createPatchCurve(6, 12);
		
		mesh.splitPatchCurve(16, new double[] {0, plateLength, plateHeight + smallChannelHeight});
		mesh.createPatchCurve(7, 13);
		
		// Creating patch curves in the outlet plenum
		mesh.splitPatchCurve(0, new double[] {0, -outletLength, plateHeight});
		mesh.createPatchCurve(6, 14);
		
		mesh.splitPatchCurve(20, new double[] {0, -outletLength, 0});
		mesh.createPatchCurve(5, 15);
		
		// Cells along the small channel walls (y direction)
		mesh.setPatchCurveParameters(16, 255, 1e-3, 1e-3, false, "Two Sided Hyperbolic");
		// Cells along the small channel walls (z direction)
		mesh.setPatchCurveParameters(15, 16, 1.5e-4, 1.5e-4, false, "Two Sided Hyperbolic");
		
		// Cells along the large channel walls (y direction)
		mesh.setPatchCurveParameters(11, 255, 1e-3, 1e-3, false, "Two Sided Hyperbolic");
		// Cells along the large channel walls (z direction)
		mesh.setPatchCurveParameters(22, 20, 1.5e-4, 1.5e-4, false, "Two Sided Hyperbolic");
		
		// Cells along the inlet plenum (y direction)
		mesh.setPatchCurveParameters(14, 30, 1e-3, 1e-3, false, "Two Sided Hyperbolic");
		
		// Cells along the outlet plenum (y direction)
		mesh.setPatchCurveParameters(5, 75, 1e-3, 1e-3, false, "Two Sided Hyperbolic");
		
		// Cells along the plate thickness in the inlet plenum (z direction) 
		mesh.setPatchCurveParameters(6, 6, 2.71e-4, 2.71e-4, false, "Constant");
		// Cells along the plate thickness in the outlet plenum (z direction) 
		mesh.setPatchCurveParameters(21, 6, 2.71e-4, 2.71e-4, false, "Constant");
		
		mesh.createDirectedVolumeMesh(65);
	
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			CO-SIMULATIONS NODE */
		// Creating the Abaqus Co-Simulation
		CoSimulationAbaqus abaqus = new CoSimulationAbaqus(activeSim, "Fluid");
		abaqus.setCouplingBoundaries(new String[] {"PlateBack.FSI", "PlateBottom.FSI", "PlateFront.FSI", "PlateTop.FSI"});
		abaqus.setAbaqusExecutionSettings("Implicit_FSI_" + Math.ceil(inletVel), abaqusFilePath, abqExecutableFileName, numAbaqusCPUs);
		abaqus.abaqusCouplingAlgorithm("Explicit", "Star Leads", couplingTimeStep);
		abaqus.setFieldExchangeControls(numExchanges, iterationsPerExchange, deflectionUnderRelax);
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			SOLVERS NODE */
		SolversNode solvers = new SolversNode(activeSim);
		solvers.setKepsilonRelax(0.6);
		solvers.setUnsteadyTimeStep(couplingTimeStep, 1);
		
		// Setting up the mesh morpher solver
		MeshMorpher morpher =  new MeshMorpher(activeSim);
		morpher.addRegionBoundary("Fluid", "PlateBottom.FSI", "FSI");
		morpher.addRegionBoundary("Fluid", "PlateTop.FSI", "FSI");
		morpher.addRegionBoundary("Fluid", "PlateFront.FSI", "FSI");
		morpher.addRegionBoundary("Fluid", "PlateBack.FSI", "FSI");
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			STOPPING CRITERIA NODE */
		StoppingCriteria stoppingCriteria = new StoppingCriteria(activeSim);
		stoppingCriteria.innerIterationStoppingCriteriaController(1000, "OR", true);
		stoppingCriteria.maxPhysicalTime(1.0, "OR", false);
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			DERIVED PARTS NODE */
		DerivedParts centerPlane = new DerivedParts(activeSim, new String[] {"Fluid"});
		centerPlane.createSectionPlane(new double[] {1, 0, 0}, new double[] {wettedPlateWidth * 0.5, 0, 0}, "CenterPlane");
		
		// Creating line probes throughout the model for plotting the pressure profile through the entire model
		String[] lineProbeRegions = {"Fluid"};
		double[] lgChLineProbeCoord_0 = {wettedPlateWidth*0.5, -outletLength, -largeChannelHeight*0.5};
		double[] lgChLineProbeCoord_1 = {wettedPlateWidth*0.5, plateLength + inletLength, -(largeChannelHeight*0.5)};
		DerivedParts smChLineProbe = new DerivedParts(activeSim, lineProbeRegions);
		LinePart smChLinePart = smChLineProbe.createLineProbe(lgChLineProbeCoord_0, lgChLineProbeCoord_1, 1000, "LargeChannelLineProbe");
		
		double[] smChLineProbeCoord_0 = {wettedPlateWidth*0.5, -outletLength, smallChannelHeight*0.5 + plateHeight};
		double[] smChLineProbeCoord_1 = {wettedPlateWidth*0.5, plateLength + inletLength, smallChannelHeight*0.5 + plateHeight};
		DerivedParts lgChLineProbe = new DerivedParts(activeSim, lineProbeRegions);
		LinePart lgChLinePart = lgChLineProbe.createLineProbe(smChLineProbeCoord_0, smChLineProbeCoord_1, 1000, "SmallChannelLineProbe");
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
		 	PLOTS NODE */
		FieldFunctions fieldFunction = new FieldFunctions(activeSim);
		// Creating an XY plot of the pressure profiles throughout the model
		ReportsMonitorsPlots pressureProfilePlot = new ReportsMonitorsPlots(activeSim);
		XYPlot pressureProfile_XYPlot = pressureProfilePlot.createXYPlot(new double[] {0, 1, 0}, "PressureProfiles", "Static Pressure (Pa)");
		fieldFunction.setXYPlotFieldFunction(pressureProfile_XYPlot, "StaticPressure", "0");
		pressureProfilePlot.addLineProbe2XYPlot(pressureProfile_XYPlot, smChLinePart);
		pressureProfilePlot.addLineProbe2XYPlot(pressureProfile_XYPlot, lgChLinePart);
		
		// Turning off the "Auto" normalization option for all the residual monitors
		ReportsMonitorsPlots reportsMonitorsPlots = new ReportsMonitorsPlots(activeSim);
		reportsMonitorsPlots.residualNormalization(new String[] {"Continuity", "Tdr", "Tke", "X-momentum", "Y-momentum", "Z-momentum"});
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
 			SCENES NODE */
		// Creating a scene of pressure
		Scenes deflectionScene = new Scenes(activeSim, "Deflection");
		Scene deflection_Scene = deflectionScene.createScalarScene();
		//fieldFunction.setSceneFieldFunction(pressure_Scene, "Morpher Displacement", "Magnitude");
		PrimitiveFieldFunction nodalDisplacement = fieldFunction.getNodalDisplacement();
		deflectionScene.setSceneFieldFunction(nodalDisplacement, 4);
		deflectionScene.addObject2Scene(deflection_Scene, "Fluid", new String[] {"PlateTop.FSI", "PlateBottom.FSI", "PlateFront.FSI", "PlateBack.FSI"});
		deflectionScene.addDerivedPart2Scene(deflection_Scene, new String[] {"CenterPlane"});
		
		// Creating a scene of velocity on the "CenterPlane"
		Scenes velocityScene = new Scenes(activeSim, "Velocity");
		Scene velocity_Scene = velocityScene.createScalarScene();
		fieldFunction.setSceneFieldFunction(velocity_Scene, "Velocity", "1");
		velocityScene.addDerivedPart2Scene(velocity_Scene, new String[] {"CenterPlane"});
		
		// Creating a scene of wall y+ values on the region "Fluid"
		Scenes wallYScene = new Scenes(activeSim, "WallY+");
		Scene wallY_Scene = wallYScene.createScalarScene();
		fieldFunction.setSceneFieldFunction(wallY_Scene, "WallYplus", "0");
		wallYScene.addObject2Scene(wallY_Scene, "Fluid", fluidSurfaceNames);
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			RUNNING THE SIMULATION */
		//activeSim.getSimulationIterator().run(1);
	}
}
