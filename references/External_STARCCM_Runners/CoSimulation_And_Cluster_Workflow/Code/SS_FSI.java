package myStarJavaMacros;

import star.common.ImplicitUnsteadyModel;
import star.common.PhysicsContinuum;
import star.common.Simulation;
import star.common.StarMacro;
import star.coupledflow.CoupledFlowModel;
import star.flow.ConstantDensityModel;
import star.keturb.KEpsilonTurbulence;
import star.keturb.KeTwoLayerAllYplusWallTreatment;
import star.keturb.RkeTwoLayerTurbModel;
import star.material.SingleComponentLiquidModel;
import star.metrics.ThreeDimensionalModel;
import star.turbulence.RansTurbulenceModel;
import star.turbulence.TurbulentModel;
import star.vis.LinePart;
import star.vis.Scene;
import starClasses.ContiuumBuilder;
import starClasses.DataReader;
import starClasses.DerivedParts;
import starClasses.FieldFunctions;
import starClasses.GeoData;
import starClasses.GeometryBuilder;
import starClasses.ImportCAE;
import starClasses.MeshElementData;
import starClasses.MeshMorpher;
import starClasses.MeshSpacingData;
import starClasses.RegionBuilder;
import starClasses.Scenes;
import starClasses.SolversNode;
import starClasses.TrimmerMesher;

public class SS_FSI extends StarMacro
{
	public void execute()
	{
		String folder = "D:/users/cj8q5/desktop/Data/";
		String sketchPlane = "YZ";
		Simulation activeSim = getActiveSimulation();
		
		// Reading in the geometry parameters from the external file
		DataReader reader = new DataReader();
		reader.readGeometryData(folder + "Plate_Geometry_Input.txt");
		GeoData geoData = reader.getGeoDetails();
		
		// Grabbing the geometry parameters from the GeoData object and converting to metric
		double plateLength = geoData.getPlateLength()*0.0254;
		double plateHeight = geoData.getPlateHeight()*0.0254;
		double wettedPlateWidth = geoData.getPlateWidth()*0.0254;
		double smallChannelHeight = geoData.getSmallChannelHeight()*0.0254;
		double largeChannelHeight = geoData.getLargeChannelHeight()*0.0254;
		double inletLength = geoData.getInletLength()*0.0254;
		double outletLength = geoData.getOutletLength()*0.0254;
		
		double[] initialVel = {0.0, -9.0, 0.0};
		double inletVel = Math.abs((smallChannelHeight + largeChannelHeight)/(smallChannelHeight + largeChannelHeight + plateHeight)*initialVel[1]);
				
		// Specifying the geometry parameters for each part
		double extrude = wettedPlateWidth; // Value for the length of all extrudes
		String[] fluidSurfaceNames = {"Outlet", "Front", "Inlet", "Back", "Right", "Left", "PlateBottom.FSI", "PlateBack.FSI", "PlateTop.FSI", "PlateFront.FSI"};

		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			GEOMETRY NODE */
		// Building the parts
		double[] X = {-outletLength, plateLength + inletLength, 0, plateLength};
		double[] Y = {-largeChannelHeight, plateHeight + smallChannelHeight, 0, plateHeight};
		GeometryBuilder fluid = new GeometryBuilder(activeSim, "Fluid");	
		fluid.boxWithVoidBuilder(sketchPlane, X, Y, extrude);
		fluid.splitSurface(89, fluidSurfaceNames);
	
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
		fluidPhysics.enable(CoupledFlowModel.class);
		fluidPhysics.enable(ConstantDensityModel.class);
		fluidPhysics.enable(TurbulentModel.class);
		fluidPhysics.enable(RansTurbulenceModel.class);
		fluidPhysics.enable(KEpsilonTurbulence.class);
		fluidPhysics.enable(RkeTwoLayerTurbModel.class);
		fluidPhysics.enable(KeTwoLayerAllYplusWallTreatment.class);
		
		// Setting the initial velocity in all cells
		physics.setInitialConditionsVel(fluidPhysics, initialVel);
		
		// Setting the inlet surfaces/boundaries to velocity inlets and setting the inlet velocity
		fluidRegion.setBoundaryCondition("Inlet", "Velocity Inlet", inletVel);
		
		// Setting the outlet surfaces/boundaries to pressure outlets
		fluidRegion.setBoundaryCondition("Outlet", "Pressure Outlet", 0);
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			DERIVED PARTS NODE */
		// Creating line probes throughout the model for plotting the pressure profile through the entire model
		String[] lineProbeRegions = {"Fluid"};
		double[] smChLineProbeCoord_0 = {wettedPlateWidth*0.5, -outletLength, -(smallChannelHeight*0.5)};
		double[] smChLineProbeCoord_1 = {wettedPlateWidth*0.5, plateLength + inletLength, -(smallChannelHeight*0.5)};
		DerivedParts smChLineProbe = new DerivedParts(activeSim, lineProbeRegions);
		LinePart smChLinePart = smChLineProbe.createLineProbe(smChLineProbeCoord_0, smChLineProbeCoord_1, 1000, "SmallChannelLineProbe");
		
		double[] lgChLineProbeCoord_0 = {wettedPlateWidth*0.5 + (0.5*0.0254), -outletLength, largeChannelHeight*0.5 + plateHeight};
		double[] lgChLineProbeCoord_1 = {wettedPlateWidth*0.5 + (0.5*0.0254), plateLength + inletLength, largeChannelHeight*0.5 + plateHeight};
		DerivedParts lgChLineProbe = new DerivedParts(activeSim, lineProbeRegions);
		LinePart lgChLinePart = lgChLineProbe.createLineProbe(lgChLineProbeCoord_0, lgChLineProbeCoord_1, 1000, "LargeChannelLineProbe");
		
		DerivedParts centerPlane = new DerivedParts(activeSim, lineProbeRegions);
		centerPlane.createSectionPlane(new double[] {1, 0, 0}, new double[] {wettedPlateWidth*0.5, 0, 0}, "CenterPlane");
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			PART MESHING */
		// Meshing the Fluid Region
		TrimmerMesher fluidMesh = new TrimmerMesher(activeSim, "Fluid");
		fluidMesh.setMesherSettings(0.0254*0.1, 50, 25, 100);
		fluidMesh.setPrismLayerSettings(0.0254*0.1, 0.0254*0.025, 3);
		fluidMesh.generateMesh();
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			IMPORTED PARTS NODE */
		// Importing Abaqus plate part and its initial deflection solution
		String fileLocation = "Q:\\Computational Results\\SS_Abaqus\\";
		String abqFileName = "Job-1";
		String[] regionDeflectionMapping = {"Fluid", "Fluid", "Fluid", "Fluid"};
		String[] boundaryDeflectionMapping = {"PlateBottom.FSI", "PlateTop.FSI", "PlateFront.FSI", "PlateBack.FSI"};
		ImportCAE cae = new ImportCAE(activeSim, fileLocation);
		cae.importAbaqusInputFile(abqFileName, false);
		cae.importAbaqusOdbFile(abqFileName, "InitialPressure");
		cae.deformImportedAbaqusModel(abqFileName);
		cae.mapAbaqusDeflectionData(regionDeflectionMapping, boundaryDeflectionMapping, "PLATE.FSI_INTERFACE");
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			SOLVERS NODE */
		SolversNode solvers = new SolversNode(activeSim);
		solvers.setKepsilonRelax(0.6);
		solvers.setUnsteadyTimeStep(5.0, 1);
		
		// Setting up the mesh morpher solver
		MeshMorpher morpher =  new MeshMorpher(activeSim);
		morpher.addRegionBoundary("Fluid", "PlateBottom.FSI");
		morpher.addRegionBoundary("Fluid", "PlateTop.FSI");
		morpher.addRegionBoundary("Fluid", "PlateFront.FSI");
		morpher.addRegionBoundary("Fluid", "PlateBack.FSI");
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
 			SCENES NODE */
		FieldFunctions fieldFunction = new FieldFunctions(activeSim);
		// Creating a scene of pressure
		Scenes pressureScene = new Scenes(activeSim, "Deflection");
		Scene pressure_Scene = pressureScene.createScalarScene();
		fieldFunction.setSceneFieldFunction(pressure_Scene, "StaticPressure", "0");
		pressureScene.addObject2Scene(pressure_Scene, "Fluid", new String[] {"PlateTop.FSI", "PlateBottom.FSI", "PlateFront.FSI", "PlateBack.FSI"});
	}
}
