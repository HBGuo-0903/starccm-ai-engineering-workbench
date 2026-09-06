package myStarJavaMacros;

import star.base.report.AreaAverageReport;
import star.base.report.MaxReport;
import star.base.report.MinReport;
import star.common.FieldFunction;
import star.common.ImplicitUnsteadyModel;
import star.common.PhysicsContinuum;
import star.common.PrimitiveFieldFunction;
import star.common.Simulation;
import star.common.StarMacro;
import star.common.XYPlot;
import star.coupledflow.CoupledFlowModel;
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
import starClasses.ContiuumBuilder;
import starClasses.DataReader;
import starClasses.DerivedParts;
import starClasses.DirectedMesher8_02_008;
import starClasses.FieldFunctions;
import starClasses.GeoData;
import starClasses.GeometryBuilder;
import starClasses.MeshElementData;
import starClasses.MeshSpacingData;
import starClasses.RegionBuilder;
import starClasses.ReportsMonitorsPlots;
import starClasses.Scenes;
import starClasses.SolversNode;
import starClasses.TrimmerMesher;

public class FlatPlateWithBoxes extends StarMacro 
{
	public void execute()
	{
		String folder = "D:/users/cj8q5/desktop/Data/";
		String sketchPlane = "YZ";
		String sketchPlanePipe = "ZX";
		Simulation activeSim = getActiveSimulation();
				
		// Reading in the geometry parameters from the external file
		DataReader reader = new DataReader();
		reader.readGeometryData(folder + "Plate_Geometry_Input.txt");
		GeoData geoData = reader.getGeoDetails();
		
		// Grabbing the geometry parameters from the GeoData object and converting to metric
		double plateLength = geoData.getPlateLength()*0.0254;
		double plateHeight = geoData.getPlateHeight()*0.0254;
		double testSectionWidth = geoData.getPlateWidth()*0.0254;
		double smallChannelHeight = geoData.getSmallChannelHeight()*0.0254;
		double largeChannelHeight = geoData.getLargeChannelHeight()*0.0254;
		double inletLength = geoData.getInletLength()*0.0254;
		double outletLength = geoData.getOutletLength()*0.0254;
		
		// Geometry parameters for the inlet and outlet boxes
		double boxWidth = 8.692*0.0254;
		double boxHeight = 2.75*0.0254;
		
		// Geometry parameters for the inlet and outlet pipes
		double pipeRadius = 1*0.0254;
		double inletPipeLength = 24*0.0254;
		double outletPipeLength = 15*0.0254;
		
		// Creating variables for the velocity conditions
		double[] initialVel = {0.0, -9.0, 0.0};
		double inletVel = Math.abs((smallChannelHeight + largeChannelHeight)/(Math.PI*0.254*0.0254)*initialVel[1]);
		
		// Specifying the geometry parameters for each part
		double extrude = testSectionWidth; // Value for the length of all extrudes
		
		double[] pipeCoords = {plateHeight*0.5, 0.5*0.0254 + testSectionWidth*0.5};
		double[] inletPipeTranslate = {0, plateLength + inletLength + boxHeight, 0};
		double[] outletPipeTranslate = {0, -(outletLength + boxHeight + outletPipeLength), 0};
		
		double[] yInletBox = {(plateLength + inletLength), (plateLength + inletLength + boxHeight)};
		double[] zInletTopBox = {(plateHeight + largeChannelHeight), (boxWidth*0.5 + plateHeight*0.5)};
		double[] zInletLargeBox = {plateHeight, (largeChannelHeight + plateHeight)};
		double[] zInletPlateBox = {0, plateHeight};
		double[] zInletSmallBox = {-smallChannelHeight, 0};
		double[] zInletBottomBox = {(-boxWidth*0.5 + plateHeight*0.5), (-smallChannelHeight)};
		
		double[] yOutletBox = {(-outletLength + -boxHeight), -outletLength};
		double[] zOutletTopBox = {(plateHeight + largeChannelHeight), (boxWidth*0.5 + plateHeight*0.5)};
		double[] zOutletLargeBox = {plateHeight, (largeChannelHeight + plateHeight)};
		double[] zOutletPlateBox = {0, plateHeight};
		double[] zOutletSmallBox = {-smallChannelHeight, 0};
		double[] zOutletBottomBox = {(-boxWidth*0.5 + plateHeight*0.5), (-smallChannelHeight)};
		
		
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
		int extrudeBoxes =  86;
		
		int inletBoxXelements = 28;
		int inletBoxFrontYelements = 85;
		int inletBoxLargeYelements = 10;
		int inletBoxPlateYelements = 4;
		int inletBoxSmallYelements = 8;
		int inletBoxBackYelements = 85;
		
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
		
		int outletBoxXelements = 28;
		int outletBoxFrontYelements = 85;
		int outletBoxLargeYelements = 10;
		int outletBoxPlateYelements = 4;
		int outletBoxSmallYelements = 8;
		int outletBoxBackYelements = 85;
		
		// Specifying the mesh spacing parameters for each part
		reader.readMeshSpacingData(folder + "Mesh_Spacing_Input.txt");
		MeshSpacingData meshSpacingData = reader.getMeshSpacingDetails();
		
		double inletBoxSpacingX = 0.001;
		double inletBoxSpacingY = 0.00015;
		
		double inletSpacingX = meshSpacingData.getInletSpacingX();
		double inletSpacingY = meshSpacingData.getInletSpacingY();
		
		double smallChannelSpacingX = meshSpacingData.getSmChannelSpacingX();
		double smallChannelSpacingY = meshSpacingData.getSmChannelSpacingY();
		
		double largeChannelSpacingX = meshSpacingData.getLgChannelSpacingX();
		double largeChannelSpacingY = meshSpacingData.getLgChannelSpacingY();
				
		double outletSpacingX = meshSpacingData.getOutletSpacingX();
		double outletSpacingY = meshSpacingData.getOutletSpacingY();
		
		double outletBoxSpacingX = 0.001;
		double outletBoxSpacingY = 0.00015;
		
		// Names of the parts
		String inletPipeName = "InletPipe";
		
		String inletFrontBoxName = "InletBoxFront";
		String inletLargeBoxName = "InletBoxLarge";
		String inletPlateBoxName = "InletBoxPlate";
		String inletSmallBoxName = "InletBoxSmall";
		String inletBackBoxName = "InletBoxBack";
		
		String inletLargeName = "InletPlenumLarge";
		String inletPlateName = "InletPlenumPlate";
		String inletSmallName = "InletPlenumSmall";
		
		String smallChannelName = "SmallChannel";
		String largeChannelName = "LargeChannel";
		
		String outletLargeName = "OutletPlenumLarge";
		String outletPlateName = "OutletPlenumPlate";
		String outletSmallName = "OutletPlenumSmall";
		
		String outletFrontBoxName = "OutletBoxFront";
		String outletLargeBoxName = "OutletBoxLarge";
		String outletPlateBoxName = "OutletBoxPlate";
		String outletSmallBoxName = "OutletBoxSmall";
		String outletBackBoxName = "OutletBoxBack";
		
		String outletPipeName = "OutletPipe";
			
		// Names of the surfaces of each part
		String[] inletPipeSurfaceNames = {"InletPipe_Inlet", "InletPipe_Wall", "InletPipe_InletBox-Interface"};
		
		String[] inletFrontBoxSurfaceNames = {"InletBoxFront_Bottom", "InletBoxFront_Front", "InletBoxFront_InletPipe-Interface", 
				"InletBoxFront_InletBoxLarge-Interface", "InletBoxFront_Right", "InletBoxFront_Left"};
		String[] inletLargeBoxSurfaceNames = {"InletBoxLarge_InletPlenumLarge-Interface", "InletBoxLarge_InletBoxFront-Interface", 
				"InletBoxLarge_InletPipe-Interface", "InletBoxLarge_InletBoxPlate-Interface", "InletBoxLarge_Right", "InletBoxLarge_Left"};
		String[] inletPlateBoxSurfaceNames = {"InletBoxPlate_InletPlenumPlate-Interface", "InletBoxPlate_InletBoxLarge-Interface", 
				"InletBoxPlate_InletPipe-Interface", "InletBoxPlate_InletBoxSmall-Interface", "InleBoxPlate_Right", "InletBoxPlate_Left"};
		String[] inletSmallBoxSurfaceNames = {"InletBoxSmall_InletPlenumSmall-Interface", "InletBoxSmall_InletBoxPlate-Interface", 
				"InletBoxSmall_InletPipe-Interface", "InletBoxSmall_InletBoxBack-Interface", "InletBoxSmall_Right", "InletBoxSmall_Left"};
		String[] inletBackBoxSurfaceNames = {"InletBoxBack_Bottom", "InletBoxBack_InletBoxSmall-Interface", 
				"InletBoxBack_InletPipe-Interface", "InletBoxBack_Back", "InletBoxBack_Right", "InletBoxBack_Left"};
		
		String[] inletLargeSurfaceNames = {"InletPlenumLarge_LargeChannel-Interface", "InletPlenumLarge_Front", "InletPlenumLarge_InletBoxLarge-Interface", 
				"InletPlenumLarge_InletPlenumPlate-Interface", "InletPlenumLarge_Right", "InletPlenumLarge_Left"};
		String[] inletPlateSurfaceNames = {"InletPlenumPlate_FSI", "InletPlenumPlate_InletPlenumLarge-Interface", "InletPlenumPlate_InletBoxPlate-Interface", 
				"InletPlenumPlate_InletPlenumSmall-Interface", "InletPlenumPlate_Right", "InletPlenumPlate_Left"};
		String[] inletSmallSurfaceNames = {"InletPlenumSmall_SmallChannel-Interface", "InletPlenumSmall_InletPlenumPlate-Interface", 
				"InletPlenumSmall_InletBoxSmall-Interface", "InletPlenumSmall_Back", "InletPlenumSmall_Right", "InletPlenumSmall_Left"};
		
		String[] smallChannelSurfaceNames = {"SmallChannel_OutletPlenumSmall-Interface", "SmallChannel_FSI", "SmallChannel_InletPlenumSmall-Interface", 
				"SmallChannel_Back", "SmallChannel_Right", "SmallChannel_Left"};
		
		String[] largeChannelSurfaceNames = {"LargeChannel_OutletPlenumLarge-Interface", "LargeChannel_Front", "LargeChannel_InletPlenumLarge-Interface", 
				"LargeChannel_FSI", "LargeChannel_Right", "LargeChannel_Left"};
		
		String[] outletLargeSurfaceNames = {"OutletPlenumLarge_OutletBoxLarge-Interface", "OutletPlenumLarge_Front", 
				"OutletPlenumLarge_LargeChannel-Interface", "OutletPlenumLarge_OutletPlenumPlate-Interface", "OutletPlenumLarge_Right", "OutletPlenumLarge_Left"};
		String[] outletPlateSurfaceNames = {"OutletPlenumPlate_OutletBoxPlate-Interface", "OutletPlenumPlate_OutletPlenumLarge-Interface", 
				"OutletPlenumPlate_FSI", "OutletPlenumPlate_OutletPlenumSmall-Interface", "OutletPlenumPlate_Right","OutletPlenumPlate_Left"};
		String[] outletSmallSurfaceNames = {"OutletPlenumSmall_OutletBoxSmall-Interface", "OutletPlenumSmall_OutletPlenumPlate-Interface", 
				"OutletPlenumSmall_SmallChannel-Interface", "OutletSmall_Back", "OutletSmall_Right", "OutletSmall_Left"};
		
		String[] outletFrontBoxSurfaceNames = {"OutletBoxFront_OutletPipe-Interface", "OutletBoxFront_Front", "OutletBoxFront_Top", 
				"OutletBoxFront_OutletBoxLarge-Interface", "OutletBoxFront_Right", "OutletBoxFront_Left"};
		String[] outletLargeBoxSurfaceNames = {"OutletBoxLarge_OutletPipe-Interface", "OutletBoxLarge_OutletBoxFront-Interface", 
				"OutletBoxLarge_OutletPlenumLarge-Interface", "OutletBoxLarge_OutletBoxPlate-Interface", "OutletBoxLarge_Right", "OutletBoxLarge_Left"};
		String[] outletPlateBoxSurfaceNames = {"OutletBoxPlate_OutletPipe-Interface", "OutletBoxPlate_OutletBoxLarge-Interface", 
				"OutletBoxPlate_OutletPlenumPlate-Interface", "OutletBoxPlate_OutletBoxSmall-Interface", "OutletBoxPlate_Right", "OutletBoxPlate_Left"};
		String[] outletSmallBoxSurfaceNames = {"OutletBoxSmall_OutletPipe-Interface", "OutletBoxSmall_OutletBoxPlate-Interface", 
				"OutletBoxSmall_OutletPlenumSmall-Interface", "OutletBoxSmall_OutletBoxBack-Interface", "OutletBoxSmall_Right", "OutletBoxSmall_Left"};
		String[] outletBackBoxSurfaceNames = {"OutletBoxBack_OutletPipe-Interface", "OutletBoxBack_OutletBoxSmall-Interface", 
				"OutletBoxBack_Top", "OutletBoxBack_Back", "OutletBoxBack_Right", "OutletBoxBack_Left"};
		
		String[] outletPipeSurfaceNames = {"OutletPipe_OutletBox-Interface", "OutletPipe_Wall", "OutletPipe_Outlet"};
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			GEOMETRY NODE */
		// Building the parts
		InnerClass.cylinderBuilder(activeSim, inletPipeLength, pipeRadius, pipeCoords, inletPipeTranslate, sketchPlanePipe, inletPipeName, inletPipeSurfaceNames);
		InnerClass.cylinderBuilder(activeSim, outletPipeLength, pipeRadius, pipeCoords, outletPipeTranslate, sketchPlanePipe, outletPipeName, outletPipeSurfaceNames);
		
		InnerClass.partBuilder(activeSim, extrude, yInletLarge, zInletLarge, 0.0127, sketchPlane, inletLargeName, inletLargeSurfaceNames);
		InnerClass.partBuilder(activeSim, extrude, yInletPlate, zInletPlate, 0.0127, sketchPlane, inletPlateName, inletPlateSurfaceNames);
		InnerClass.partBuilder(activeSim, extrude, yInletSmall, zInletSmall, 0.0127, sketchPlane, inletSmallName, inletSmallSurfaceNames);
		
		InnerClass.partBuilder(activeSim, extrude, yLarge, zLarge, 0.0127, sketchPlane, largeChannelName, largeChannelSurfaceNames);
		InnerClass.partBuilder(activeSim, extrude, ySmall, zSmall, 0.0127, sketchPlane, smallChannelName, smallChannelSurfaceNames);
		
		InnerClass.partBuilder(activeSim, extrude, yOutletLarge, zOutletLarge, 0.0127, sketchPlane, outletLargeName, outletLargeSurfaceNames);
		InnerClass.partBuilder(activeSim, extrude, yOutletPlate, zOutletPlate, 0.0127, sketchPlane, outletPlateName, outletPlateSurfaceNames);
		InnerClass.partBuilder(activeSim, extrude, yOutletSmall, zOutletSmall, 0.0127, sketchPlane, outletSmallName, outletSmallSurfaceNames);
		
		InnerClass.partBuilder(activeSim, boxWidth, yInletBox, zInletTopBox, (extrude - boxWidth)*0.5 + 0.0127, sketchPlane, inletFrontBoxName, inletFrontBoxSurfaceNames);
		InnerClass.partBuilder(activeSim, boxWidth, yInletBox, zInletLargeBox, (extrude - boxWidth)*0.5 + 0.0127, sketchPlane, inletLargeBoxName, inletLargeBoxSurfaceNames);
		InnerClass.partBuilder(activeSim, boxWidth, yInletBox, zInletPlateBox, (extrude - boxWidth)*0.5 + 0.0127, sketchPlane, inletPlateBoxName, inletPlateBoxSurfaceNames);
		InnerClass.partBuilder(activeSim, boxWidth, yInletBox, zInletSmallBox, (extrude - boxWidth)*0.5 + 0.0127, sketchPlane, inletSmallBoxName, inletSmallBoxSurfaceNames);
		InnerClass.partBuilder(activeSim, boxWidth, yInletBox, zInletBottomBox, (extrude - boxWidth)*0.5 + 0.0127, sketchPlane, inletBackBoxName, inletBackBoxSurfaceNames);
		
		InnerClass.partBuilder(activeSim, boxWidth, yOutletBox, zOutletTopBox, (extrude - boxWidth)*0.5 + 0.0127, sketchPlane, outletFrontBoxName, outletFrontBoxSurfaceNames);
		InnerClass.partBuilder(activeSim, boxWidth, yOutletBox, zOutletLargeBox, (extrude - boxWidth)*0.5 + 0.0127, sketchPlane, outletLargeBoxName, outletLargeBoxSurfaceNames);
		InnerClass.partBuilder(activeSim, boxWidth, yOutletBox, zOutletPlateBox, (extrude - boxWidth)*0.5 + 0.0127, sketchPlane, outletPlateBoxName, outletPlateBoxSurfaceNames);
		InnerClass.partBuilder(activeSim, boxWidth, yOutletBox, zOutletSmallBox, (extrude - boxWidth)*0.5 + 0.0127, sketchPlane, outletSmallBoxName, outletSmallBoxSurfaceNames);
		InnerClass.partBuilder(activeSim, boxWidth, yOutletBox, zOutletBottomBox, (extrude - boxWidth)*0.5 + 0.0127, sketchPlane, outletBackBoxName, outletBackBoxSurfaceNames);
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			REGION NODE */
		// Creating the regions for the simulation
		RegionBuilder inletPipe = new RegionBuilder(activeSim, "InletPipe");
		inletPipe.part2Region(inletPipeName, false);
		String[] inletPipeBoundaryNames = inletPipeSurfaceNames;
		
		// Creating the region for the Inlet Box
		RegionBuilder inletBox = new RegionBuilder(activeSim, "InletBox");
		inletBox.parts2Region(new String[]{inletFrontBoxName, inletLargeBoxName, inletPlateBoxName, inletSmallBoxName, inletBackBoxName});
		String[] inletBoxBoundaryNames = new String[30];
		for (int i = 0; i < 6; i++)
		{
			inletBoxBoundaryNames[i] = inletFrontBoxName + "." + inletFrontBoxSurfaceNames[i];
			inletBoxBoundaryNames[i + 6] = inletLargeBoxName + "." + inletLargeBoxSurfaceNames[i];
			inletBoxBoundaryNames[i + 12] = inletPlateBoxName + "." + inletPlateBoxSurfaceNames[i];
			inletBoxBoundaryNames[i + 18] = inletSmallBoxName + "." + inletSmallBoxSurfaceNames[i];
			inletBoxBoundaryNames[i + 24] = inletBackBoxName + "." + inletBackBoxSurfaceNames[i];
		}
		inletBox.createBoundaryGroup("InletBoxFront", new String[] {inletBoxBoundaryNames[0], inletBoxBoundaryNames[1], inletBoxBoundaryNames[2],
				inletBoxBoundaryNames[3],inletBoxBoundaryNames[4],inletBoxBoundaryNames[5]});
		inletBox.createBoundaryGroup("InletBoxLarge", new String[] {inletBoxBoundaryNames[6], inletBoxBoundaryNames[7], inletBoxBoundaryNames[8],
				inletBoxBoundaryNames[9],inletBoxBoundaryNames[10],inletBoxBoundaryNames[11]});
		inletBox.createBoundaryGroup("InletBoxPlate", new String[] {inletBoxBoundaryNames[12], inletBoxBoundaryNames[13], inletBoxBoundaryNames[14],
				inletBoxBoundaryNames[15],inletBoxBoundaryNames[16],inletBoxBoundaryNames[17]});
		inletBox.createBoundaryGroup("InletBoxSmall", new String[] {inletBoxBoundaryNames[18], inletBoxBoundaryNames[19], inletBoxBoundaryNames[20],
				inletBoxBoundaryNames[21],inletBoxBoundaryNames[22],inletBoxBoundaryNames[23]});
		inletBox.createBoundaryGroup("InletBoxBack", new String[] {inletBoxBoundaryNames[24], inletBoxBoundaryNames[25], inletBoxBoundaryNames[26],
				inletBoxBoundaryNames[27],inletBoxBoundaryNames[28],inletBoxBoundaryNames[29]});
		
		// Creating the region for the Inlet Plenum
		RegionBuilder inletPlenum = new RegionBuilder(activeSim, "InletPlenum");
		inletPlenum.parts2Region(new String[] {inletLargeName, inletSmallName, inletPlateName});
		String[] inletPlenumBoundaryNames = new String[18];
		for (int i = 0; i < 6; i++)
		{
			inletPlenumBoundaryNames[i] = inletLargeName + "." + inletLargeSurfaceNames[i];
			inletPlenumBoundaryNames[i + 6] = inletPlateName + "." + inletPlateSurfaceNames[i];
			inletPlenumBoundaryNames[i + 12] = inletSmallName + "." + inletSmallSurfaceNames[i];
		}
		inletPlenum.createBoundaryGroup("InletPlenumLarge", new String[] {inletPlenumBoundaryNames[0], inletPlenumBoundaryNames[1], inletPlenumBoundaryNames[2],
				inletPlenumBoundaryNames[3],inletPlenumBoundaryNames[4],inletPlenumBoundaryNames[5]});
		inletPlenum.createBoundaryGroup("InletPlenumPlate", new String[] {inletPlenumBoundaryNames[6], inletPlenumBoundaryNames[7], inletPlenumBoundaryNames[8],
				inletPlenumBoundaryNames[9],inletPlenumBoundaryNames[10],inletPlenumBoundaryNames[11]});
		inletPlenum.createBoundaryGroup("InletPlenumSmall", new String[] {inletPlenumBoundaryNames[12], inletPlenumBoundaryNames[13], inletPlenumBoundaryNames[14],
				inletPlenumBoundaryNames[15],inletPlenumBoundaryNames[16],inletPlenumBoundaryNames[17]});
		
		// Creating the region for the Plate Channels
		RegionBuilder channels = new RegionBuilder(activeSim, "PlateChannels");
		channels.parts2Region(new String[] {smallChannelName, largeChannelName});
		String[] plateChannelsBoundaryNames = new String[12];
		for (int i = 0; i < 6; i++)
		{
			plateChannelsBoundaryNames[i] = smallChannelName + "." + smallChannelSurfaceNames[i];
			plateChannelsBoundaryNames[i + 6] = largeChannelName + "." + largeChannelSurfaceNames[i];
		}
		channels.createBoundaryGroup("SmallChannel", new String[] {plateChannelsBoundaryNames[0], plateChannelsBoundaryNames[1], plateChannelsBoundaryNames[2],
				plateChannelsBoundaryNames[3],plateChannelsBoundaryNames[4],plateChannelsBoundaryNames[5]});
		channels.createBoundaryGroup("LargeChannel", new String[] {plateChannelsBoundaryNames[6], plateChannelsBoundaryNames[7], plateChannelsBoundaryNames[8],
				plateChannelsBoundaryNames[9],plateChannelsBoundaryNames[10],plateChannelsBoundaryNames[11]});
		
		// Creating the region for the Outlet Plenum
		RegionBuilder outletPlenum = new RegionBuilder(activeSim, "OutletPlenum");
		outletPlenum.parts2Region(new String[] {outletLargeName, outletSmallName, outletPlateName});
		String[] outletPlenumBoundaryNames = new String[18];
		for (int i = 0; i < 6; i++)
		{
			outletPlenumBoundaryNames[i] = outletLargeName + "." + outletLargeSurfaceNames[i];
			outletPlenumBoundaryNames[i + 6] = outletPlateName + "." + outletPlateSurfaceNames[i];
			outletPlenumBoundaryNames[i + 12] = outletSmallName + "." + outletSmallSurfaceNames[i];
		}
		outletPlenum.createBoundaryGroup("OutletPlenumLarge", new String[] {outletPlenumBoundaryNames[0], outletPlenumBoundaryNames[1], outletPlenumBoundaryNames[2],
				outletPlenumBoundaryNames[3],outletPlenumBoundaryNames[4],outletPlenumBoundaryNames[5]});
		outletPlenum.createBoundaryGroup("OutletPlenumPlate", new String[] {outletPlenumBoundaryNames[6], outletPlenumBoundaryNames[7], outletPlenumBoundaryNames[8],
				outletPlenumBoundaryNames[9],outletPlenumBoundaryNames[10],outletPlenumBoundaryNames[11]});
		outletPlenum.createBoundaryGroup("OutletPlenumSmall", new String[] {outletPlenumBoundaryNames[12], outletPlenumBoundaryNames[13], outletPlenumBoundaryNames[14],
				outletPlenumBoundaryNames[15],outletPlenumBoundaryNames[16],outletPlenumBoundaryNames[17]});
		
		// Creating the region for the Outlet Box
		RegionBuilder outletBox =  new RegionBuilder(activeSim, "OutletBox");
		outletBox.parts2Region(new String[] {outletFrontBoxName, outletLargeBoxName, outletPlateBoxName, outletSmallBoxName, outletBackBoxName});
		String[] outletBoxBoundaryNames = new String[30];
		for (int i = 0; i < 6; i++)
		{
			outletBoxBoundaryNames[i] = outletFrontBoxName + "." + outletFrontBoxSurfaceNames[i];
			outletBoxBoundaryNames[i + 6] = outletLargeBoxName + "." + outletLargeBoxSurfaceNames[i];
			outletBoxBoundaryNames[i + 12] = outletPlateBoxName + "." + outletPlateBoxSurfaceNames[i];
			outletBoxBoundaryNames[i + 18] = outletSmallBoxName + "." + outletSmallBoxSurfaceNames[i];
			outletBoxBoundaryNames[i + 24] = outletBackBoxName + "." + outletBackBoxSurfaceNames[i];
		}
		outletBox.createBoundaryGroup("OutletBoxFront", new String[] {outletBoxBoundaryNames[0], outletBoxBoundaryNames[1], outletBoxBoundaryNames[2],
				outletBoxBoundaryNames[3],outletBoxBoundaryNames[4],outletBoxBoundaryNames[5]});
		outletBox.createBoundaryGroup("OutletBoxLarge", new String[] {outletBoxBoundaryNames[6], outletBoxBoundaryNames[7], outletBoxBoundaryNames[8],
				outletBoxBoundaryNames[9],outletBoxBoundaryNames[10],outletBoxBoundaryNames[11]});
		outletBox.createBoundaryGroup("OutletBoxPlate", new String[] {outletBoxBoundaryNames[12], outletBoxBoundaryNames[13], outletBoxBoundaryNames[14],
				outletBoxBoundaryNames[15],outletBoxBoundaryNames[16],outletBoxBoundaryNames[17]});
		outletBox.createBoundaryGroup("OutletBoxSmall", new String[] {outletBoxBoundaryNames[18], outletBoxBoundaryNames[19], outletBoxBoundaryNames[20],
				outletBoxBoundaryNames[21],outletBoxBoundaryNames[22],outletBoxBoundaryNames[23]});
		outletBox.createBoundaryGroup("OutletBoxBack", new String[] {outletBoxBoundaryNames[24], outletBoxBoundaryNames[25], outletBoxBoundaryNames[26],
				outletBoxBoundaryNames[27],outletBoxBoundaryNames[28],outletBoxBoundaryNames[29]});
		
		RegionBuilder outletPipe = new RegionBuilder(activeSim, "OutletPipe");
		outletPipe.part2Region(outletPipeName, false);
		String[] outletPipeBoundaryNames = outletPipeSurfaceNames;
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			INTERFACES NODE */
		// Interfaces for the Inlet Plenum and Plate Channels
		String[] regionNames_0 = {"InletPlenum", "InletPlenum"};
		String[] boundaryNames_0 = {"InletPlenumLarge.InletPlenumLarge_InletPlenumPlate-Interface", "InletPlenumPlate.InletPlenumPlate_InletPlenumLarge-Interface"};
		String interactionName_0 = "InletPlenum.InletPlenumLarge_InletPlenumPlate-Interaction";
		inletPlenum.createInterface(regionNames_0, boundaryNames_0, interactionName_0);
		
		String[] regionNames_1 = {"InletPlenum", "InletPlenum"};
		String[] boundaryNames_1 = {"InletPlenumPlate.InletPlenumPlate_InletPlenumSmall-Interface", "InletPlenumSmall.InletPlenumSmall_InletPlenumPlate-Interface"};
		String interactionName_1 = "InletPlenum.InletPlenumPlate_InletPlenumSmall-Interaction";
		inletPlenum.createInterface(regionNames_1, boundaryNames_1, interactionName_1);
		
		String[] regionNames_2 = {"InletPlenum", "PlateChannels"};
		String[] boundaryNames_2 = {"InletPlenumLarge.InletPlenumLarge_LargeChannel-Interface", "LargeChannel.LargeChannel_InletPlenumLarge-Interface"};
		String interactionName_2 = "InletPlenum.InletPlenumLarge_LargeChannel-Interaction";
		inletPlenum.createInterface(regionNames_2, boundaryNames_2, interactionName_2);
		
		String[] regionNames_3 = {"InletPlenum", "PlateChannels"};
		String[] boundaryNames_3 = {"InletPlenumSmall.InletPlenumSmall_SmallChannel-Interface", "SmallChannel.SmallChannel_InletPlenumSmall-Interface"};
		String interactionName_3 = "InletPlenum.InletPlenumSmall_SmallChannel-Interaction";
		inletPlenum.createInterface(regionNames_3, boundaryNames_3, interactionName_3);
		
		// Interfaces for the Outlet Plenum and the Plate Channels
		String[] regionNames_4 = {"OutletPlenum", "OutletPlenum"};
		String[] boundaryNames_4 = {"OutletPlenumLarge.OutletPlenumLarge_OutletPlenumPlate-Interface", "OutletPlenumPlate.OutletPlenumPlate_OutletPlenumLarge-Interface"};
		String interactionName_4 = "OutletPlenum.OutletPlenumLarge_OutletPlenumPlate-Interaction";
		outletPlenum.createInterface(regionNames_4, boundaryNames_4, interactionName_4);
		
		String[] regionNames_5 = {"OutletPlenum", "OutletPlenum"};
		String[] boundaryNames_5 = {"OutletPlenumPlate.OutletPlenumPlate_OutletPlenumSmall-Interface", "OutletPlenumSmall.OutletPlenumSmall_OutletPlenumPlate-Interface"};
		String interactionName_5 = "OutletPlenum.OutletPlenumPlate_OutletPlenumSmall-Interaction";
		outletPlenum.createInterface(regionNames_5, boundaryNames_5, interactionName_5);
		
		String[] regionNames_6 = {"OutletPlenum", "PlateChannels"};
		String[] boundaryNames_6 = {"OutletPlenumLarge.OutletPlenumLarge_LargeChannel-Interface", "LargeChannel.LargeChannel_OutletPlenumLarge-Interface"};
		String interactionName_6 = "OutletPlenum.OutletPlenumLarge_LargeChannel-Interaction";
		outletPlenum.createInterface(regionNames_6, boundaryNames_6, interactionName_6);
		
		String[] regionNames_7 = {"OutletPlenum", "PlateChannels"};
		String[] boundaryNames_7 = {"OutletPlenumSmall.OutletPlenumSmall_SmallChannel-Interface", "SmallChannel.SmallChannel_OutletPlenumSmall-Interface"};
		String interactionName_7 = "OutletPlenum.OutletPlenumSmall_SmallChannel-Interaction";
		outletPlenum.createInterface(regionNames_7, boundaryNames_7, interactionName_7);
		
		// Interfaces for the Inlet Box, Inlet Pipe, and Inlet Plenum
		String[] regionNames_8 = {"InletBox", "InletBox"};
		String[] boundaryNames_8 = {"InletBoxBack.InletBoxBack_InletBoxSmall-Interface", "InletBoxSmall.InletBoxSmall_InletBoxBack-Interface"};
		String interactionName_8 = "InletBox.InletBoxBack_InletBoxSmall-Interaction";
		outletPlenum.createInterface(regionNames_8, boundaryNames_8, interactionName_8);

		String[] regionNames_9 = {"InletBox", "InletBox"};
		String[] boundaryNames_9 = {"InletBoxFront.InletBoxFront_InletBoxLarge-Interface", "InletBoxLarge.InletBoxLarge_InletBoxFront-Interface"};
		String interactionName_9 = "InletBox.InletBoxFront_InletBoxLarge-Interaction";
		outletPlenum.createInterface(regionNames_9, boundaryNames_9, interactionName_9);
		
		String[] regionNames_10 = {"InletBox", "InletBox"};
		String[] boundaryNames_10 = {"InletBoxLarge.InletBoxLarge_InletBoxPlate-Interface", "InletBoxPlate.InletBoxPlate_InletBoxLarge-Interface"};
		String interactionName_10 = "InletBox.InletBoxLarge_InletBoxPlate-Interaction";
		outletPlenum.createInterface(regionNames_10, boundaryNames_10, interactionName_10);
		
		String[] regionNames_11 = {"InletBox", "InletBox"};
		String[] boundaryNames_11 = {"InletBoxSmall.InletBoxSmall_InletBoxPlate-Interface", "InletBoxPlate.InletBoxPlate_InletBoxSmall-Interface"};
		String interactionName_11 = "InletBox.InletBoxSmall_InletBoxPlate-Interaction";
		outletPlenum.createInterface(regionNames_11, boundaryNames_11, interactionName_11);
		
		String[] regionNames_12 = {"InletBox", "InletPlenum"};
		String[] boundaryNames_12 = {"InletBoxSmall.InletBoxSmall_InletPlenumSmall-Interface", "InletPlenumSmall.InletPlenumSmall_InletBoxSmall-Interface"};
		String interactionName_12 = "InletBox.InletBoxSmall_InletPlenumSmall-Interaction";
		outletPlenum.createInterface(regionNames_12, boundaryNames_12, interactionName_12);

		String[] regionNames_13 = {"InletBox", "InletPlenum"};
		String[] boundaryNames_13 = {"InletBoxLarge.InletBoxLarge_InletPlenumLarge-Interface", "InletPlenumLarge.InletPlenumLarge_InletBoxLarge-Interface"};
		String interactionName_13 = "InletBox.InletBoxLarge_InletPlenumLarge-Interaction";
		outletPlenum.createInterface(regionNames_13, boundaryNames_13, interactionName_13);

		String[] regionNames_14 = {"InletBox", "InletPlenum"};
		String[] boundaryNames_14 = {"InletBoxPlate.InletBoxPlate_InletPlenumPlate-Interface", "InletPlenumPlate.InletPlenumPlate_InletBoxPlate-Interface"};
		String interactionName_14 = "InletBox.InletBoxPlate_InletPlenumPlate-Interaction";
		outletPlenum.createInterface(regionNames_14, boundaryNames_14, interactionName_14);
		
		// Interfaces for the Outlet Box, Outlet Pipe, and Outlet Plenum
		String[] regionNames_15 = {"OutletBox", "OutletBox"};
		String[] boundaryNames_15 = {"OutletBoxBack.OutletBoxBack_OutletBoxSmall-Interface", "OutletBoxSmall.OutletBoxSmall_OutletBoxBack-Interface"};
		String interactionName_15 = "OutletBox.OutletBoxBack_OutletBoxSmall-Interaction";
		outletPlenum.createInterface(regionNames_15, boundaryNames_15, interactionName_15);

		String[] regionNames_16 = {"OutletBox", "OutletBox"};
		String[] boundaryNames_16 = {"OutletBoxFront.OutletBoxFront_OutletBoxLarge-Interface", "OutletBoxLarge.OutletBoxLarge_OutletBoxFront-Interface"};
		String interactionName_16 = "OutletBox.OutletBoxFront_OutletBoxLarge-Interaction";
		outletPlenum.createInterface(regionNames_16, boundaryNames_16, interactionName_16);
		
		String[] regionNames_17 = {"OutletBox", "OutletBox"};
		String[] boundaryNames_17 = {"OutletBoxLarge.OutletBoxLarge_OutletBoxPlate-Interface", "OutletBoxPlate.OutletBoxPlate_OutletBoxLarge-Interface"};
		String interactionName_17 = "OutletBox.OutletBoxLarge_OutletBoxPlate-Interaction";
		outletPlenum.createInterface(regionNames_17, boundaryNames_17, interactionName_17);
		
		String[] regionNames_18 = {"OutletBox", "OutletBox"};
		String[] boundaryNames_18 = {"OutletBoxSmall.OutletBoxSmall_OutletBoxPlate-Interface", "OutletBoxPlate.OutletBoxPlate_OutletBoxSmall-Interface"};
		String interactionName_18 = "OutletBox.OutletBoxSmall_OutletBoxPlate-Interaction";
		outletPlenum.createInterface(regionNames_18, boundaryNames_18, interactionName_18);
		
		String[] regionNames_19 = {"OutletBox", "OutletPlenum"};
		String[] boundaryNames_19 = {"OutletBoxSmall.OutletBoxSmall_OutletPlenumSmall-Interface", "OutletPlenumSmall.OutletPlenumSmall_OutletBoxSmall-Interface"};
		String interactionName_19 = "OutletBox.OutletBoxSmall_OutletPlenumSmall-Interaction";
		outletPlenum.createInterface(regionNames_19, boundaryNames_19, interactionName_19);

		String[] regionNames_20 = {"OutletBox", "OutletPlenum"};
		String[] boundaryNames_20 = {"OutletBoxLarge.OutletBoxLarge_OutletPlenumLarge-Interface", "OutletPlenumLarge.OutletPlenumLarge_OutletBoxLarge-Interface"};
		String interactionName_20 = "OutletBox.OutletBoxLarge_OutletPlenumLarge-Interaction";
		outletPlenum.createInterface(regionNames_20, boundaryNames_20, interactionName_20);

		String[] regionNames_21 = {"OutletBox", "OutletPlenum"};
		String[] boundaryNames_21 = {"OutletBoxPlate.OutletBoxPlate_OutletPlenumPlate-Interface", "OutletPlenumPlate.OutletPlenumPlate_OutletBoxPlate-Interface"};
		String interactionName_21 = "OutletBox.OutletBoxPlate_OutletPlenumPlate-Interaction";
		outletPlenum.createInterface(regionNames_21, boundaryNames_21, interactionName_21);

		// Interfaces between the Inlet Box and the Inlet Pipe
		String[] regionNames_22 = {"InletPipe", "InletBox"};
		String[] boundaryNames_22 = {"InletPipe_InletBox-Interface", "InletBoxFront.InletBoxFront_InletPipe-Interface"};
		String interactionName_22 = "InletPipe.InletPipe_InletBoxFront-Interaction";
		outletPlenum.createInterface(regionNames_22, boundaryNames_22, interactionName_22);
		
		String[] regionNames_23 = {"InletPipe", "InletBox"};
		String[] boundaryNames_23 = {"InletPipe_InletBox-Interface", "InletBoxLarge.InletBoxLarge_InletPipe-Interface"};
		String interactionName_23 = "InletPipe.InletPipe_InletBoxLarge-Interaction";
		outletPlenum.createInterface(regionNames_23, boundaryNames_23, interactionName_23);
		
		String[] regionNames_24 = {"InletPipe", "InletBox"};
		String[] boundaryNames_24 = {"InletPipe_InletBox-Interface", "InletBoxPlate.InletBoxPlate_InletPipe-Interface"};
		String interactionName_24 = "InletPipe.InletPipe_InletBoxPlate-Interaction";
		outletPlenum.createInterface(regionNames_24, boundaryNames_24, interactionName_24);
		
		String[] regionNames_25 = {"InletPipe", "InletBox"};
		String[] boundaryNames_25 = {"InletPipe_InletBox-Interface", "InletBoxSmall.InletBoxSmall_InletPipe-Interface"};
		String interactionName_25 = "InletPipe.InletPipe_InletBoxSmall-Interaction";
		outletPlenum.createInterface(regionNames_25, boundaryNames_25, interactionName_25);
		
		String[] regionNames_26 = {"InletPipe", "InletBox"};
		String[] boundaryNames_26 = {"InletPipe_InletBox-Interface", "InletBoxBack.InletBoxBack_InletPipe-Interface"};
		String interactionName_26 = "InletPipe.InletPipe_InletBoxBack-Interaction";
		outletPlenum.createInterface(regionNames_26, boundaryNames_26, interactionName_26);
		
		// Interfaces between the Outlet Box and Outlet Pipe
		String[] regionNames_27 = {"OutletPipe", "OutletBox"};
		String[] boundaryNames_27 = {"OutletPipe_OutletBox-Interface", "OutletBoxFront.OutletBoxFront_OutletPipe-Interface"};
		String interactionName_27 = "OutletPipe.OutletPipe_OutletBoxFront-Interaction";
		outletPlenum.createInterface(regionNames_27, boundaryNames_27, interactionName_27);

		String[] regionNames_28 = {"OutletPipe", "OutletBox"};
		String[] boundaryNames_28 = {"OutletPipe_OutletBox-Interface", "OutletBoxLarge.OutletBoxLarge_OutletPipe-Interface"};
		String interactionName_28 = "OutletPipe.OutletPipe_OutletBoxLarge-Interaction";
		outletPlenum.createInterface(regionNames_28, boundaryNames_28, interactionName_28);

		String[] regionNames_29 = {"OutletPipe", "OutletBox"};
		String[] boundaryNames_29 = {"OutletPipe_OutletBox-Interface", "OutletBoxPlate.OutletBoxPlate_OutletPipe-Interface"};
		String interactionName_29 = "OutletPipe.OutletPipe_OutletBoxPlate-Interaction";
		outletPlenum.createInterface(regionNames_29, boundaryNames_29, interactionName_29);
		
		String[] regionNames_30 = {"OutletPipe", "OutletBox"};
		String[] boundaryNames_30 = {"OutletPipe_OutletBox-Interface", "OutletBoxSmall.OutletBoxSmall_OutletPipe-Interface"};
		String interactionName_30 = "OutletPipe.OutletPipe_OutletBoxSmall-Interaction";
		outletPlenum.createInterface(regionNames_30, boundaryNames_30, interactionName_30);

		String[] regionNames_31 = {"OutletPipe", "OutletBox"};
		String[] boundaryNames_31 = {"OutletPipe_OutletBox-Interface", "OutletBoxBack.OutletBoxBack_OutletPipe-Interface"};
		String interactionName_31 = "OutletPipe.OutletPipe_OutletBoxBack-Interaction";
		outletPlenum.createInterface(regionNames_31, boundaryNames_31, interactionName_31);
		
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
		inletPipe.setBoundaryCondition("InletPipe_Inlet", "Velocity Inlet", inletVel);
		
		// Setting the outlet surfaces/boundaries to pressure outlets
		outletPipe.setBoundaryCondition("OutletPipe_Outlet", "Pressure Outlet", 0);
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			DERIVED PARTS NODE */
		// Creating line probes throughout the model for plotting the pressure profile through the entire model
		String[] lineProbeRegions = {"InletPipe", "InletBox", "InletPlenum", "PlateChannels", "OutletPlenum", "OutletBox", "OutletPipe"};
		double[] smChLineProbeCoord_0 = {testSectionWidth*0.5 + (0.5*0.0254), -(boxHeight + outletPipeLength), -(smallChannelHeight*0.5)};
		double[] smChLineProbeCoord_1 = {testSectionWidth*0.5 + (0.5*0.0254), plateLength + inletLength + boxHeight + inletPipeLength, -(smallChannelHeight*0.5)};
		DerivedParts smChLineProbe = new DerivedParts(activeSim, lineProbeRegions);
		LinePart smChLinePart = smChLineProbe.createLineProbe(smChLineProbeCoord_0, smChLineProbeCoord_1, 1000, "SmallChannelLineProbe");
		
		double[] lgChLineProbeCoord_0 = {testSectionWidth*0.5 + (0.5*0.0254), -(boxHeight + outletPipeLength), largeChannelHeight*0.5 + plateHeight};
		double[] lgChLineProbeCoord_1 = {testSectionWidth*0.5 + (0.5*0.0254), plateLength + inletLength + boxHeight + inletPipeLength, largeChannelHeight*0.5 + plateHeight};
		DerivedParts lgChLineProbe = new DerivedParts(activeSim, lineProbeRegions);
		LinePart lgChLinePart = lgChLineProbe.createLineProbe(lgChLineProbeCoord_0, lgChLineProbeCoord_1, 1000, "LargeChannelLineProbe");
		
		DerivedParts centerPlane = new DerivedParts(activeSim, lineProbeRegions);
		centerPlane.createSectionPlane(new double[] {1, 0, 0}, new double[] {testSectionWidth*0.5 + (0.5*0.0254), 0, 0}, "CenterPlane");
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			SOLVERS NODE */
		SolversNode solvers = new SolversNode(activeSim);
		solvers.setKepsilonRelax(0.6);
		solvers.setUnsteadyTimeStep(5.0, 1);
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			PART MESHING */
		///**
		// Meshing the Inlet Box
		InnerClass.partMesher(activeSim, inletFrontBoxName, inletFrontBoxSurfaceNames, inletBoxXelements, inletBoxFrontYelements,
				extrudeBoxes, inletBoxSpacingX, inletBoxSpacingY, "Two", "Two", true, true);
		InnerClass.partMesher(activeSim, inletLargeBoxName, inletLargeBoxSurfaceNames, inletBoxXelements, inletLargeYelements,
				extrudeBoxes, inletBoxSpacingX, inletBoxSpacingY, "Two", "Two", true, true);
		InnerClass.partMesher(activeSim, inletPlateBoxName, inletPlateBoxSurfaceNames, inletBoxXelements, inletPlateYelements,
				extrudeBoxes, inletBoxSpacingX, inletBoxSpacingY, "Two", "Constant", false, false);
		InnerClass.partMesher(activeSim, inletSmallBoxName, inletSmallBoxSurfaceNames, inletBoxXelements, inletSmallYelements,
				extrudeBoxes, inletBoxSpacingX, inletBoxSpacingY, "Two", "Two", true, true);
		InnerClass.partMesher(activeSim, inletBackBoxName, inletBackBoxSurfaceNames, inletBoxXelements, inletBoxBackYelements,
				extrudeBoxes, inletBoxSpacingX, inletBoxSpacingY, "Two", "Two", true, true);
		
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
		
		// Meshing the Outlet Box
		InnerClass.partMesher(activeSim, outletFrontBoxName, outletFrontBoxSurfaceNames, outletBoxXelements, outletBoxFrontYelements,
				extrudeBoxes, inletBoxSpacingX, inletBoxSpacingY, "Two", "Two", true, true);
		InnerClass.partMesher(activeSim, outletLargeBoxName, outletLargeBoxSurfaceNames, outletBoxXelements, outletLargeYelements,
				extrudeBoxes, inletBoxSpacingX, inletBoxSpacingY, "Two", "Two", true, true);
		InnerClass.partMesher(activeSim, outletPlateBoxName, outletPlateBoxSurfaceNames, outletBoxXelements, outletPlateYelements,
				extrudeBoxes, inletBoxSpacingX, inletBoxSpacingY, "Two", "Constant", false, false);
		InnerClass.partMesher(activeSim, outletSmallBoxName, outletSmallBoxSurfaceNames, outletBoxXelements, outletSmallYelements,
				extrudeBoxes, inletBoxSpacingX, inletBoxSpacingY, "Two", "Two", true, true);
		InnerClass.partMesher(activeSim, outletBackBoxName, outletBackBoxSurfaceNames, outletBoxXelements, outletBoxBackYelements,
				extrudeBoxes, inletBoxSpacingX, inletBoxSpacingY, "Two", "Two", true, true);
		
		// Meshing the Inlet and Outlet Pipes
		TrimmerMesher inletPipeMesh = new TrimmerMesher(activeSim, "InletPipe");
		inletPipeMesh.setBaseSize(0.0015);
		inletPipeMesh.setPrismLayerSettings(0.001, 1.5e-4, 5);
		inletPipe.setBoundaryPrismOption(inletPipeSurfaceNames[0], false);
		inletPipe.setBoundaryPrismOption(inletPipeSurfaceNames[2], false);
		
		TrimmerMesher outletPipeMesh = new TrimmerMesher(activeSim, "OutletPipe");
		outletPipeMesh.setBaseSize(0.0015);
		outletPipeMesh.setPrismLayerSettings(0.001, 1.5e-4, 5);
		outletPipe.setBoundaryPrismOption(outletPipeSurfaceNames[0], false);
		outletPipe.setBoundaryPrismOption(outletPipeSurfaceNames[2], false);
		
		// Generating the pipe meshes
		inletPipeMesh.generateMesh();
		outletPipeMesh.generateMesh();
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
		 	PLOTS NODE */
		FieldFunctions fieldFunction = new FieldFunctions(activeSim);
		// Creating an XY plot of the pressure profiles throughout the model
		ReportsMonitorsPlots pressureProfilePlot = new ReportsMonitorsPlots(activeSim);
		XYPlot pressureProfile_XYPlot = pressureProfilePlot.createXYPlot(new double[] {0, 1, 0}, "PressureProfiles", "Static Pressure (Pa)");
		fieldFunction.setXYPlotFieldFunction(pressureProfile_XYPlot, "StaticPressure", "0");
		pressureProfilePlot.addLineProbe2XYPlot(pressureProfile_XYPlot, smChLinePart);
		pressureProfilePlot.addLineProbe2XYPlot(pressureProfile_XYPlot, lgChLinePart);
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
	 		SCENES NODE */
		// Creating a scene of pressure
		Scenes pressureScene = new Scenes(activeSim, "Pressure");
		Scene pressure_Scene = pressureScene.createScalarScene();
		fieldFunction.setSceneFieldFunction(pressure_Scene, "StaticPressure", "0");
		pressureScene.addObject2Scene(pressure_Scene, "InletPipe", inletPipeBoundaryNames);
		pressureScene.addObject2Scene(pressure_Scene, "InletBox", inletBoxBoundaryNames);
		pressureScene.addObject2Scene(pressure_Scene, "InletPlenum", inletPlenumBoundaryNames);
		pressureScene.addObject2Scene(pressure_Scene, "PlateChannels", plateChannelsBoundaryNames);
		pressureScene.addObject2Scene(pressure_Scene, "OutletPlenum", outletPlenumBoundaryNames);
		pressureScene.addObject2Scene(pressure_Scene, "OutletBox", outletBoxBoundaryNames);
		pressureScene.addObject2Scene(pressure_Scene, "OutletPipe", outletPipeBoundaryNames);
		
		Scenes wallYScene = new Scenes(activeSim, "WallY+");
		Scene wallY_Scene = wallYScene.createScalarScene();
		fieldFunction.setSceneFieldFunction(wallY_Scene, "WallYplus", "0");
		wallYScene.addObject2Scene(wallY_Scene, "InletPipe", inletPipeBoundaryNames);
		wallYScene.addObject2Scene(wallY_Scene, "InletBox", inletBoxBoundaryNames);
		wallYScene.addObject2Scene(wallY_Scene, "InletPlenum", inletPlenumBoundaryNames);
		wallYScene.addObject2Scene(wallY_Scene, "PlateChannels", plateChannelsBoundaryNames);
		wallYScene.addObject2Scene(wallY_Scene, "OutletPlenum", outletPlenumBoundaryNames);
		wallYScene.addObject2Scene(wallY_Scene, "OutletBox", outletBoxBoundaryNames);
		wallYScene.addObject2Scene(wallY_Scene, "OutletPipe", outletPipeBoundaryNames);
		
		// Creating a scene of velocity
		Scenes velocityScene = new Scenes(activeSim, "Velocity");
		Scene velocity_Scene = velocityScene.createScalarScene();
		fieldFunction.setSceneFieldFunction(velocity_Scene, "Velocity", "1");
		velocityScene.addDerivedPart2Scene(velocity_Scene, new String[] {"CenterPlane"});
		
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
		
		/** This method builds a cylinder part */
		private static void cylinderBuilder(Simulation sim, double pipeLength, double pipeRadius, double[] pipeCenterCoords, double[] translateCoords, 
				String sketchPlane, String partName, String[] surfaceNames)
		{
			GeometryBuilder pipe = new GeometryBuilder(sim, partName);
			pipe.cylinderBuilder(sketchPlane, pipeCenterCoords, pipeRadius, pipeLength);
			pipe.splitSurface(89, surfaceNames);
			pipe.partTranslate(translateCoords);
		}// end nested method cylinderBuilder
		
		/** This method builds the mesh around a part */
		private static void partMesher(Simulation sim, String partName, String[] surfaceNames,int meshNodeX, int meshNodeY, int meshNodeZ, 
				double meshSpacingX, double meshSpacingY, String flagX, String flagY, boolean isDirectionReversedX, boolean isDirectionReversedY)
		{
			DirectedMesher8_02_008 mesh = new DirectedMesher8_02_008(sim, partName);
			mesh.setSourceTargetSurfaces(surfaceNames[4], surfaceNames[5]);
			mesh.definePatchCurveParameters(meshNodeY, meshNodeX, meshSpacingY, meshSpacingX, flagY, flagX, isDirectionReversedY, isDirectionReversedX);
			mesh.createDirectedVolumeMesh(meshNodeZ);
		}
		
		/** This method creates reports, monitors, and a plot of a specfied field function */
		/**private static void reportMonitorPlot(Simulation sim, String[] objectPath, String[] reportNames, 
				FieldFunction fieldFunction, String plotTitles, String[] axesTitles)
		{
			ReportsMonitorsPlots reportsMonitorsPlots = new ReportsMonitorsPlots(sim);
			MinReport minPressureReport_0 = reportsMonitorsPlots.createMinReport(objectPath, reportNames[0]);
			minPressureReport_0.setScalar(fieldFunction);
			MaxReport maxPressureReport_0 = reportsMonitorsPlots.createMaxReport(objectPath, reportNames[1]);
			maxPressureReport_0.setScalar(fieldFunction);
			AreaAverageReport avgPressureReport_0 = reportsMonitorsPlots.createAverageReport(objectPath, reportNames[2]);
			avgPressureReport_0.setScalar(fieldFunction);
			reportsMonitorsPlots.createMonitorPlot2(reportNames, plotTitles, axesTitles, true);
		}*/

	}// end inner class InnerClass
}
