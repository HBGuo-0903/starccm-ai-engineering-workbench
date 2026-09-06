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
import star.lesturb.LesLowYplusWallTreatment;
import star.lesturb.WaleSgsModel;
import star.material.SingleComponentLiquidModel;
import star.metrics.ThreeDimensionalModel;
import star.segregatedflow.SegregatedFlowModel;
import star.turbulence.LesTurbulenceModel;
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

public class FlatPlate_40mil_LES extends StarMacro 
{
	public void execute()
	{
		String folder = "D:/users/cj8q5/workspace/starJavaCode/src/GeometryMeshingData/";
		//D:\Users\cj8q5\workspace\starJavaCode\src\GeometryMeshingData
		String sketchPlane = "YZ";
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
		
		// Inlet Velocity and initial velocity conditions for the fluid
		double[] initialVel = {0.0, -9.0, 0.0};
		double inletVel = Math.abs((smallChannelHeight + largeChannelHeight)/(smallChannelHeight + largeChannelHeight + plateHeight)*initialVel[1]);
				
		String[] fluidBoundaryNames = {"", "", ""};
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			GEOMETRY NODE */
		
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			REGION NODE */
		RegionBuilder fluidRegion = new RegionBuilder(activeSim, "Fluid");
				
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			INTERFACES NODE */
		
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			PART MESHING */
		
		
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
		fluidPhysics.enable(LesTurbulenceModel.class);
		fluidPhysics.enable(WaleSgsModel.class);
		fluidPhysics.enable(LesLowYplusWallTreatment.class);
		
		// Setting the initial velocity in all cells
		physics.setInitialConditionsVel(fluidPhysics, initialVel);
		
		// Setting the inlet surfaces/boundaries to velocity inlets and setting the inlet velocity
		fluidRegion.setBoundaryCondition("Inlet", "Velocity Inlet", inletVel);
		
		// Setting the outlet surfaces/boundaries to pressure outlets
		fluidRegion.setBoundaryCondition("Outlet", "Pressure Outlet", 0);
		
		// Setting the symmetry boundary conditions
		fluidRegion.setBoundaryCondition("Symmetry_Left", "Symmetry", 0);
		fluidRegion.setBoundaryCondition("Symmetry_Right", "Symmetry", 0);
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			DERIVED PARTS NODE */
		// Creating line probes throughout the model for plotting the pressure profile through the entire model
		String[] lineProbeRegions = {"InletPipe", "InletBox", "InletPlenum", "PlateChannels", "OutletPlenum", "OutletBox", "OutletPipe"};
		double[] smChLineProbeCoord_0 = {testSectionWidth*0.5 + (0.5*0.0254), -outletLength, -(smallChannelHeight*0.5)};
		double[] smChLineProbeCoord_1 = {testSectionWidth*0.5 + (0.5*0.0254), plateLength + inletLength, -(smallChannelHeight*0.5)};
		DerivedParts smChLineProbe = new DerivedParts(activeSim, lineProbeRegions);
		LinePart smChLinePart = smChLineProbe.createLineProbe(smChLineProbeCoord_0, smChLineProbeCoord_1, 1000, "SmallChannelLineProbe");
		
		double[] lgChLineProbeCoord_0 = {testSectionWidth*0.5 + (0.5*0.0254), -outletLength, largeChannelHeight*0.5 + plateHeight};
		double[] lgChLineProbeCoord_1 = {testSectionWidth*0.5 + (0.5*0.0254), plateLength + inletLength, largeChannelHeight*0.5 + plateHeight};
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
		pressureScene.addObject2Scene(pressure_Scene, "InletPlenum", fluidBoundaryNames);
		
		// Creating a scene of the wall y+ values
		Scenes wallYScene = new Scenes(activeSim, "WallY+");
		Scene wallY_Scene = wallYScene.createScalarScene();
		fieldFunction.setSceneFieldFunction(wallY_Scene, "WallYplus", "0");
		wallYScene.addObject2Scene(wallY_Scene, "InletPlenum", fluidBoundaryNames);
		
		// Creating a scene of velocity
		Scenes velocityScene = new Scenes(activeSim, "Velocity");
		Scene velocity_Scene = velocityScene.createScalarScene();
		fieldFunction.setSceneFieldFunction(velocity_Scene, "Velocity", "1");
		velocityScene.addDerivedPart2Scene(velocity_Scene, new String[] {"CenterPlane"});
	
	}
	
	private static class InnerClass
	{
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
