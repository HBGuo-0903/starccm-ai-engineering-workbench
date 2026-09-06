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
import starClasses.DirectedMesher8_02_008;
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
import starClasses.StoppingCriteria;
import starClasses.TrimmerMesher;

public class FSI_SS_StarCCM extends StarMacro
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
			String[] fluidSurfaceNames = {"Outlet", "Front", "Inlet", "Back", "Right", "Left", "PlateBottom", "PlateBack", "PlateTop", "PlateFront"};
			//String[] fluidSurfaceNames = {"Outlet", "Front", "Inlet", "Back", "Right", "Left"};
			
		double[] yInletLarge = {plateLength, (plateLength + inletLength)};
		double[] zInletLarge = {plateHeight, (largeChannelHeight + plateHeight)};
		
		double[] yInletPlate = {plateLength, (plateLength + inletLength)};
		double[] zInletPlate = {0, plateHeight};
		
		double[] yInletSmall = {plateLength, (plateLength + inletLength)};
		double[] zInletSmall = {-smallChannelHeight, 0};

		double[] ySmall = {0, plateLength};
		double[] zSmall = {-smallChannelHeight, 0};
		
		double[] yLarge = {0, plateLength};
		double[] zLarge = {plateHeight, (largeChannelHeight + plateHeight)};
		
		double[] yOutletLarge = {-outletLength, 0};
		double[] zOutletLarge = {plateHeight, (largeChannelHeight + plateHeight)};
		
		double[] yOutletPlate = {-outletLength, 0};
		double[] zOutletPlate = {0, plateHeight};
		
		double[] yOutletSmall = {-outletLength, 0};
		double[] zOutletSmall = {-smallChannelHeight, 0};
		
		// Specifying the mesh element parameters for each part
		reader.readMeshElementData(folder + "Mesh_Element_Input.txt");
		MeshElementData meshElementData = reader.getMeshElementDetails();
		
		int extrudeElements = meshElementData.getExtrudeCell();
		
		int inletXelements = meshElementData.getInletX();
		int inletLargeYelements = meshElementData.getLargeInletY();
		int inletPlateYelements = meshElementData.getPlateInletY();
		int inletSmallYelements = meshElementData.getSmallInletY();
		
		int outletXelements = meshElementData.getOutletX();
		int outletLargeYelements = meshElementData.getLargeOutletY();
		int outletPlateYelements = meshElementData.getPlateOutletY();
		int outletSmallYelements = meshElementData.getSmallOutletY();
		
		int smallChannelXelements = meshElementData.getSmChannelX();
		int smallChannelYelements = meshElementData.getSmChannelY();
		int largeChannelXelements = meshElementData.getLgChannelX();
		int largeChannelYelements = meshElementData.getLgChannelY();
		
		// Specifying the mesh spacing parameters for each part
		reader.readMeshSpacingData(folder + "Mesh_Spacing_Input.txt");
		MeshSpacingData meshSpacingData = reader.getMeshSpacingDetails();
		
		double inletSpacingX = meshSpacingData.getInletSpacingX();
		double inletSpacingY = meshSpacingData.getInletSpacingY();
		
		double smallChannelSpacingX = meshSpacingData.getSmChannelSpacingX();
		double smallChannelSpacingY = meshSpacingData.getSmChannelSpacingY();
		
		double largeChannelSpacingX = meshSpacingData.getLgChannelSpacingX();
		double largeChannelSpacingY = meshSpacingData.getLgChannelSpacingY();
				
		double outletSpacingX = meshSpacingData.getOutletSpacingX();
		double outletSpacingY = meshSpacingData.getOutletSpacingY();
		
		// Names of the parts
		String inletLargeName = "InletPlenumLarge";
		String inletPlateName = "InletPlenumPlate";
		String inletSmallName = "InletPlenumSmall";
		
		String smallChannelName = "SmallChannel";
		String largeChannelName = "LargeChannel";
		
		String outletLargeName = "OutletPlenumLarge";
		String outletPlateName = "OutletPlenumPlate";
		String outletSmallName = "OutletPlenumSmall";
			
		// Names of the surfaces of each part
		String[] inletLargeSurfaceNames = {"LargeChannel-Interface", "Front", "Inlet", 
				"InletPlenumPlate-Interface", "Right", "Left"};
		String[] inletPlateSurfaceNames = {"FSI", "InletPlenumLarge-Interface", "Inlet", 
				"InletPlenumSmall-Interface", "Right", "Left"};
		String[] inletSmallSurfaceNames = {"SmallChannel-Interface", "InletPlenumPlate-Interface", 
				"Inlet", "Back", "Right", "Left"};
		
		String[] smallChannelSurfaceNames = {"OutletPlenumSmall-Interface", "FSI", "InletPlenumSmall-Interface", 
				"Back", "Right", "Left"};
		
		String[] largeChannelSurfaceNames = {"OutletPlenumLarge-Interface", "Front", "InletPlenumLarge-Interface", 
				"FSI", "Right", "Left"};
		
		String[] outletLargeSurfaceNames = {"Outlet", "Front", 
				"LargeChannel-Interface", "OutletPlenumPlate-Interface", "Right", "Left"};
		String[] outletPlateSurfaceNames = {"Outlet", "OutletPlenumLarge-Interface", 
				"FSI", "OutletPlenumSmall-Interface", "Right","Left"};
		String[] outletSmallSurfaceNames = {"Outlet", "OutletPlenumPlate-Interface", 
				"SmallChannel-Interface", "Back", "Right", "Left"};

		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			GEOMETRY NODE */
		// Building the parts
		InnerClass.partBuilder(activeSim, extrude, yInletLarge, zInletLarge, 0, sketchPlane, inletLargeName, inletLargeSurfaceNames);
		InnerClass.partBuilder(activeSim, extrude, yInletPlate, zInletPlate, 0, sketchPlane, inletPlateName, inletPlateSurfaceNames);
		InnerClass.partBuilder(activeSim, extrude, yInletSmall, zInletSmall, 0, sketchPlane, inletSmallName, inletSmallSurfaceNames);
		
		InnerClass.partBuilder(activeSim, extrude, yLarge, zLarge, 0, sketchPlane, largeChannelName, largeChannelSurfaceNames);
		InnerClass.partBuilder(activeSim, extrude, ySmall, zSmall, 0, sketchPlane, smallChannelName, smallChannelSurfaceNames);
		
		InnerClass.partBuilder(activeSim, extrude, yOutletLarge, zOutletLarge, 0, sketchPlane, outletLargeName, outletLargeSurfaceNames);
		InnerClass.partBuilder(activeSim, extrude, yOutletPlate, zOutletPlate, 0, sketchPlane, outletPlateName, outletPlateSurfaceNames);
		InnerClass.partBuilder(activeSim, extrude, yOutletSmall, zOutletSmall, 0, sketchPlane, outletSmallName, outletSmallSurfaceNames);
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			REGION NODE */
		// Creating the regions for the simulation
		// Creating the region for the Fluid
		RegionBuilder fluid = new RegionBuilder(activeSim, "Fluid");
		fluid.parts2Region(new String[] {inletLargeName, inletSmallName, inletPlateName,
				outletLargeName, outletSmallName, outletPlateName, smallChannelName, largeChannelName});
		String[] inletPlenumBoundaryNames = new String[18];
		for (int i = 0; i < 6; i++)
		{
			inletPlenumBoundaryNames[i] = inletLargeName + "." + inletLargeSurfaceNames[i];
			inletPlenumBoundaryNames[i + 6] = inletPlateName + "." + inletPlateSurfaceNames[i];
			inletPlenumBoundaryNames[i + 12] = inletSmallName + "." + inletSmallSurfaceNames[i];
		}
		fluid.createBoundaryGroup("InletPlenumLarge", new String[] {inletPlenumBoundaryNames[0], inletPlenumBoundaryNames[1], inletPlenumBoundaryNames[2],
				inletPlenumBoundaryNames[3],inletPlenumBoundaryNames[4],inletPlenumBoundaryNames[5]});
		fluid.createBoundaryGroup("InletPlenumPlate", new String[] {inletPlenumBoundaryNames[6], inletPlenumBoundaryNames[7], inletPlenumBoundaryNames[8],
				inletPlenumBoundaryNames[9],inletPlenumBoundaryNames[10],inletPlenumBoundaryNames[11]});
		fluid.createBoundaryGroup("InletPlenumSmall", new String[] {inletPlenumBoundaryNames[12], inletPlenumBoundaryNames[13], inletPlenumBoundaryNames[14],
				inletPlenumBoundaryNames[15],inletPlenumBoundaryNames[16],inletPlenumBoundaryNames[17]});
		
		String[] plateChannelsBoundaryNames = new String[12];
		for (int i = 0; i < 6; i++)
		{
			plateChannelsBoundaryNames[i] = smallChannelName + "." + smallChannelSurfaceNames[i];
			plateChannelsBoundaryNames[i + 6] = largeChannelName + "." + largeChannelSurfaceNames[i];
		}
		fluid.createBoundaryGroup("SmallChannel", new String[] {plateChannelsBoundaryNames[0], plateChannelsBoundaryNames[1], plateChannelsBoundaryNames[2],
				plateChannelsBoundaryNames[3],plateChannelsBoundaryNames[4],plateChannelsBoundaryNames[5]});
		fluid.createBoundaryGroup("LargeChannel", new String[] {plateChannelsBoundaryNames[6], plateChannelsBoundaryNames[7], plateChannelsBoundaryNames[8],
				plateChannelsBoundaryNames[9],plateChannelsBoundaryNames[10],plateChannelsBoundaryNames[11]});
		
		String[] outletPlenumBoundaryNames = new String[18];
		for (int i = 0; i < 6; i++)
		{
			outletPlenumBoundaryNames[i] = outletLargeName + "." + outletLargeSurfaceNames[i];
			outletPlenumBoundaryNames[i + 6] = outletPlateName + "." + outletPlateSurfaceNames[i];
			outletPlenumBoundaryNames[i + 12] = outletSmallName + "." + outletSmallSurfaceNames[i];
		}
		fluid.createBoundaryGroup("OutletPlenumLarge", new String[] {outletPlenumBoundaryNames[0], outletPlenumBoundaryNames[1], outletPlenumBoundaryNames[2],
				outletPlenumBoundaryNames[3],outletPlenumBoundaryNames[4],outletPlenumBoundaryNames[5]});
		fluid.createBoundaryGroup("OutletPlenumPlate", new String[] {outletPlenumBoundaryNames[6], outletPlenumBoundaryNames[7], outletPlenumBoundaryNames[8],
				outletPlenumBoundaryNames[9],outletPlenumBoundaryNames[10],outletPlenumBoundaryNames[11]});
		fluid.createBoundaryGroup("OutletPlenumSmall", new String[] {outletPlenumBoundaryNames[12], outletPlenumBoundaryNames[13], outletPlenumBoundaryNames[14],
				outletPlenumBoundaryNames[15],outletPlenumBoundaryNames[16],outletPlenumBoundaryNames[17]});
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			INTERFACES NODE */
		// Interfaces for the Inlet Plenum and Plate Channels
		String[] regionNames = {"Fluid", "Fluid"};
		String[] iteractionNames = {"InletPlenum.InletPlenumPlate-Interaction", "InletPlenum.InletPlenumSmall-Interaction", "InletPlenum.LargeChannel-Interaction",
				"InletPlenum.SmallChannel-Interaction", "OutletPlenum.OutletPlenumLarge_OutletPlenumPlate-Interaction", "OutletPlenum.OutletPlenumSmall-Interaction",
				"OutletPlenum.LargeChannel-Interaction", "OutletPlenum.SmallChannel-Interaction"};
		
		String[] boundaryNames_0 = {"InletPlenumLarge.InletPlenumPlate-Interface", "InletPlenumPlate.InletPlenumLarge-Interface"};
		String interactionName_0 = "InletPlenum.InletPlenumPlate-Interaction";
		fluid.createInterface(regionNames, boundaryNames_0, interactionName_0);
		
		String[] boundaryNames_1 = {"InletPlenumPlate.InletPlenumSmall-Interface", "InletPlenumSmall.InletPlenumPlate-Interface"};
		String interactionName_1 = "InletPlenum.InletPlenumSmall-Interaction";
		fluid.createInterface(regionNames, boundaryNames_1, interactionName_1);
		
		String[] boundaryNames_2 = {"InletPlenumLarge.LargeChannel-Interface", "LargeChannel.InletPlenumLarge-Interface"};
		String interactionName_2 = "InletPlenum.LargeChannel-Interaction";
		fluid.createInterface(regionNames, boundaryNames_2, interactionName_2);
		
		String[] boundaryNames_3 = {"InletPlenumSmall.SmallChannel-Interface", "SmallChannel.InletPlenumSmall-Interface"};
		String interactionName_3 = "InletPlenum.SmallChannel-Interaction";
		fluid.createInterface(regionNames, boundaryNames_3, interactionName_3);
		
		String[] boundaryNames_4 = {"OutletPlenumLarge.OutletPlenumPlate-Interface", "OutletPlenumPlate.OutletPlenumLarge-Interface"};
		String interactionName_4 = "OutletPlenum.OutletPlenumLarge_OutletPlenumPlate-Interaction";
		fluid.createInterface(regionNames, boundaryNames_4, interactionName_4);
		
		String[] boundaryNames_5 = {"OutletPlenumPlate.OutletPlenumSmall-Interface", "OutletPlenumSmall.OutletPlenumPlate-Interface"};
		String interactionName_5 = "OutletPlenum.OutletPlenumSmall-Interaction";
		fluid.createInterface(regionNames, boundaryNames_5, interactionName_5);
		
		String[] boundaryNames_6 = {"OutletPlenumLarge.LargeChannel-Interface", "LargeChannel.OutletPlenumLarge-Interface"};
		String interactionName_6 = "OutletPlenum.LargeChannel-Interaction";
		fluid.createInterface(regionNames, boundaryNames_6, interactionName_6);
		
		String[] boundaryNames_7 = {"OutletPlenumSmall.SmallChannel-Interface", "SmallChannel.OutletPlenumSmall-Interface"};
		String interactionName_7 = "OutletPlenum.SmallChannel-Interaction";
		fluid.createInterface(regionNames, boundaryNames_7, interactionName_7);
		
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
		String[] inletBoundaries = {"InletPlenumPlate.Inlet", "InletPlenumSmall.Inlet", "InletPlenumLarge.Inlet"};
		for (int i = 0; i < inletBoundaries.length; i++)
		{
			fluid.setBoundaryCondition(inletBoundaries[i], "Velocity Inlet", inletVel);
		}
		
		// Setting the outlet surfaces/boundaries to pressure outlets
		String[] outletBoundaries = {"OutletPlenumPlate.Outlet","OutletPlenumSmall.Outlet", "OutletPlenumLarge.Outlet"};
		for (int i = 0; i < outletBoundaries.length; i++)
		{
			fluid.setBoundaryCondition(outletBoundaries[i], "Pressure Outlet", 0);
		}
		
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
		// Meshing the Inlet Plenum
		InnerClass.partMesher(activeSim, inletLargeName, inletLargeSurfaceNames, inletXelements, inletLargeYelements, extrudeElements, 
				inletSpacingX, inletSpacingY, "Two", "Two", true, false);
		InnerClass.partMesher(activeSim, inletPlateName, inletPlateSurfaceNames,inletXelements, inletPlateYelements, extrudeElements, 
				inletSpacingX, inletSpacingY, "Two", "Constant", false, false);
		InnerClass.partMesher(activeSim, inletSmallName, inletSmallSurfaceNames,inletXelements, inletSmallYelements, extrudeElements, 
				inletSpacingX, inletSpacingY, "Two", "Two", true, true);
		
		// Meshing the Large Channel
		InnerClass.partMesher(activeSim, largeChannelName, largeChannelSurfaceNames,largeChannelXelements, largeChannelYelements, extrudeElements, 
				largeChannelSpacingX, largeChannelSpacingY, "Two", "Two", true, false);
		InnerClass.partMesher(activeSim, smallChannelName, smallChannelSurfaceNames,smallChannelXelements, smallChannelYelements, extrudeElements, 
				smallChannelSpacingX, smallChannelSpacingY, "Two", "Two", true, false);
		
		// Meshing the Small Channel
		InnerClass.partMesher(activeSim, outletLargeName, outletLargeSurfaceNames,outletXelements, outletLargeYelements, extrudeElements, 
				outletSpacingX, outletSpacingY, "One", "Two", true, false);
		InnerClass.partMesher(activeSim, outletPlateName, outletPlateSurfaceNames,outletXelements, outletPlateYelements, extrudeElements, 
				outletSpacingX, outletSpacingY, "One", "Constant", true, false);
		InnerClass.partMesher(activeSim, outletSmallName, outletSmallSurfaceNames,outletXelements, outletSmallYelements, extrudeElements, 
				outletSpacingX, outletSpacingY, "One", "Two", true, true);
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			IMPORTED PARTS NODE */
		// Importing Abaqus plate part and its initial deflection solution
		String fileLocation = "Q:\\Computational Results\\SS_Abaqus\\";
		String abqFileName = "Job-1";
		String[] regionDeflectionMapping = {"Fluid", "Fluid", "Fluid", "Fluid"};
		String[] boundaryDeflectionMapping = {"SmallChannel.FSI", "LargeChannel.FSI", "InletPlenumPlate.FSI", "OutletPlenumPlate.FSI"};
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
		for(int i = 0; i < 6; i++)
		{
			morpher.addRegionBoundary("Fluid", "InletPlenumSmall." + inletSmallSurfaceNames[i]);
			morpher.addRegionBoundary("Fluid", "InletPlenumLarge." + inletLargeSurfaceNames[i]);
			morpher.addRegionBoundary("Fluid", "InletPlenumPlate." + inletPlateSurfaceNames[i]);
			
			morpher.addRegionBoundary("Fluid", "SmallChannel." + smallChannelSurfaceNames[i]);
			morpher.addRegionBoundary("Fluid", "LargeChannel." + largeChannelSurfaceNames[i]);
			
			morpher.addRegionBoundary("Fluid", "OutletPlenumSmall." + outletSmallSurfaceNames[i]);
			morpher.addRegionBoundary("Fluid", "OutletPlenumLarge." + outletLargeSurfaceNames[i]);
			morpher.addRegionBoundary("Fluid", "OutletPlenumPlate." + outletPlateSurfaceNames[i]);
		}

		//for(int i = 0; i < 8; i++)
		//{
		//	morpher.addRegionBoundary("Fluid", iteractionNames[i]);
		//}
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			SOLVERS NODE */
		StoppingCriteria maxInnerIter = new StoppingCriteria(activeSim);
		maxInnerIter.innerIterationStoppingCriteriaController(1, "OR", true);
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
 			SCENES NODE */
		FieldFunctions fieldFunction = new FieldFunctions(activeSim);
		// Creating a scene of pressure
		Scenes pressureScene = new Scenes(activeSim, "Deflection");
		Scene pressure_Scene = pressureScene.createScalarScene();
		fieldFunction.setSceneFieldFunction(pressure_Scene, "StaticPressure", "0");
		pressureScene.addObject2Scene(pressure_Scene, "Fluid", new String[] {"InletPlenumPlate.FSI", "OutletPlenumPlate.FSI", "SmallChannel.FSI", "LargeChannel.FSI"});
		pressureScene.addDerivedPart2Scene(pressure_Scene, new String[] {"CenterPlane"});
	}
	
	private static class InnerClass
	{
		/** This method builds a part and meshes it using the directed mesher */
		private static void partBuilder(Simulation sim, double extrude, double[] y, double[] z, double xTranslate, String sketchPlane, 
				String partName, String[] surfaceNames)
		{
			GeometryBuilder part = new GeometryBuilder(sim, partName);
			part.boxBuilder(sketchPlane, y, z, extrude);
			part.splitSurface(89, surfaceNames);
			part.partTranslate(new double[] {xTranslate, 0, 0});
		}// end nested method partBuilder
		
		/** This method builds the mesh around a part */
		private static void partMesher(Simulation sim, String partName, String[] surfaceNames,int meshNodeX, int meshNodeY, int meshNodeZ, 
				double meshSpacingX, double meshSpacingY, String flagX, String flagY, boolean isDirectionReversedX, boolean isDirectionReversedY)
		{
			DirectedMesher8_02_008 mesh = new DirectedMesher8_02_008(sim, partName);
			mesh.setSourceTargetSurfaces(surfaceNames[4], surfaceNames[5]);
			mesh.definePatchCurveParameters(meshNodeY, meshNodeX, meshSpacingY, meshSpacingX, flagY, flagX, isDirectionReversedY, isDirectionReversedX);
			mesh.createDirectedVolumeMesh(meshNodeZ);
		}
	}
}
