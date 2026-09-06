package myStarJavaMacros;

import star.common.PhysicsContinuum;
import star.common.Simulation;
import star.common.StarMacro;
import star.common.SteadyModel;
import star.common.XYPlot;
import star.flow.ConstantDensityModel;
import star.keturb.KEpsilonTurbulence;
import star.keturb.KeTwoLayerAllYplusWallTreatment;
import star.keturb.RkeTwoLayerTurbModel;
import star.material.SingleComponentGasModel;
import star.metrics.ThreeDimensionalModel;
import star.segregatedflow.SegregatedFlowModel;
import star.turbulence.RansTurbulenceModel;
import star.turbulence.TurbulentModel;
import star.vis.Scene;
import starClasses.ContiuumBuilder;
import starClasses.FieldFunctions;
import starClasses.GeometryBuilder;
import starClasses.PolyhedralMesher;
import starClasses.RegionBuilder;
import starClasses.ReportsMonitorsPlots;
import starClasses.Scenes;
import starClasses.SolversNode;
import starClasses.StoppingCriteria;

public class MAE7440_GLS17 extends StarMacro
{
	public void execute() 
	{
		Simulation activeSim = getActiveSimulation();
		double angleOfAttack = 20;
		int numIter = 5000;
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			GEOMETRY NODE */
		// Creating the bulk fluid part where the plate will be cut from
		double[] x_fluid = {-2.25, 2.25};
		double[] y_fluid = {-2, 2};
		double extrude = 0.1;
		String[] fluidBlockSurfaceNames = {"Inlet", "Top", "Outlet", "Bottom", "Front", "Back"};
		GeometryBuilder fluidBlock = new GeometryBuilder(activeSim, "Fluid_Block", "XY");
		fluidBlock.boxBuilder(x_fluid, y_fluid, extrude);
		fluidBlock.splitSurface(89, fluidBlockSurfaceNames, true);
		
		// Creating the plate part which will be used to cut a slot in the bulk fluid part
		double[] x_plate = {-1, 1};
		double[] y_plate = {-0.0125, 0.0125};
		String[] plateSurfaceNames = {"LeadingEdge", "Top", "TrailingEdge", "Bottom"};
		GeometryBuilder plate = new GeometryBuilder(activeSim, "Plate", "XY");
		plate.boxBuilder(x_plate, y_plate, extrude);
		plate.partRotate(-angleOfAttack*(Math.PI/180), new double[] {0, 0, 1});
		plate.splitSurface(89, plateSurfaceNames, true);
		
		// Creating the actual fluid part by cutting the plate part from the bulk fluid part
		GeometryBuilder fluid = new GeometryBuilder(activeSim, "Fluid", "XY");
		fluid.partSubtract("Fluid_Block", "Plate", "Fluid");
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			REGIONS NODE */
		// Creating the region from the "Fluid" part
		RegionBuilder fluidRegion = new RegionBuilder(activeSim, "Fluid");
		fluidRegion.part2Region("Fluid", true);
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			CONTINUA NODE */
		// Creating the physics continua
		ContiuumBuilder physics = new ContiuumBuilder(activeSim);
		PhysicsContinuum air = physics.createPhysicsContinua("Air");
		
		// Enabling the physics models for the simulation
		air.enable(ThreeDimensionalModel.class);
		air.enable(SteadyModel.class);
		air.enable(SingleComponentGasModel.class);
		air.enable(SegregatedFlowModel.class);
		air.enable(ConstantDensityModel.class);
		air.enable(TurbulentModel.class);
		air.enable(RansTurbulenceModel.class);
		air.enable(KEpsilonTurbulence.class);
		air.enable(RkeTwoLayerTurbModel.class);
		air.enable(KeTwoLayerAllYplusWallTreatment.class);
		
		// Setting the initial velocity conditions in the model
		physics.setInitialConditionsVel(air, new double[] {15, 0, 0});
		
		// Setting the boundary conditions in the fluid domain
		fluidRegion.setBoundaryCondition("Fluid_Block.Inlet", "Velocity Inlet", 15);
		fluidRegion.setBoundaryCondition("Fluid_Block.Outlet", "Pressure Outlet", 0);
		fluidRegion.setBoundaryCondition("Fluid_Block.Front", "Symmetry", 0);
		fluidRegion.setBoundaryCondition("Fluid_Block.Back", "Symmetry", 0);
		
		// Creating the polyhedral mesh in the fluid region "Fluid"
		PolyhedralMesher fluidMesh =  new PolyhedralMesher(activeSim, "Fluid");
		fluidMesh.setMesherSettings(0.1, 75, 75);
		fluidMesh.setPrismLayerSettings(0.0075, 0.0075, 1);
		fluidMesh.setReferenceValuesSurfaceSize(0.075, 0.075);
		fluidMesh.setSurfaceGrowthRate(1.05);
		for(int i = 0; i < plateSurfaceNames.length; i++)
		{
			fluidMesh.setCustomBoundarySurfaceSize("Fluid", "Plate." + plateSurfaceNames[i], 5, 5);
		}
		fluidMesh.generateMesh();
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			SOLVERS NODE */
		SolversNode solvers = new SolversNode(activeSim);
		solvers.setKepsilonRelax(0.6);
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			STOPPING CRITERIA NODE */
		StoppingCriteria stoppingCriteria = new StoppingCriteria(activeSim);
		stoppingCriteria.maxSteps(1500, "OR", false);
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			DERIVED PARTS NODE */
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
		 	PLOTS NODE */
		// Turning off the "Auto" normalization option for all the residual monitors
		ReportsMonitorsPlots reportsMonitorsPlots = new ReportsMonitorsPlots(activeSim);
		reportsMonitorsPlots.residualNormalization();
		
		FieldFunctions fieldFunction = new FieldFunctions(activeSim);
		// Creating an XY plot of the pressure profiles throughout the model
		ReportsMonitorsPlots pressureProfilePlot = new ReportsMonitorsPlots(activeSim);
		XYPlot pressureProfile_XYPlot = pressureProfilePlot.createXYPlot(new double[] {0, 1, 0}, "PressureProfiles", "Static Pressure (Pa)");
		fieldFunction.setXYPlotFieldFunction(pressureProfile_XYPlot, "StaticPressure", "0");
		pressureProfilePlot.addObjects2XYPlot(pressureProfile_XYPlot, "Fluid", 
				new String[] {"Plate.Top", "Plate.Bottom", "Plate.LeadingEdge", "Plate.TrailingEdge"});
		
		// Turning off the "Auto" normalization option for all the residual monitors
		ReportsMonitorsPlots reportsMonitorsPlots = new ReportsMonitorsPlots(activeSim);
		reportsMonitorsPlots.residualNormalization();
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			SCENES NODE */
		// Creating a scene of pressure
		Scenes pressureScene = new Scenes(activeSim, "Pressure");
		Scene pressure_Scene = pressureScene.createScalarScene();
		fieldFunction.setSceneFieldFunction(pressure_Scene, "StaticPressure", "Magnitude");
		pressureScene.addObject2Scene(pressure_Scene, "Fluid", 
				new String[] {"Fluid_Block.Top", "Fluid_Block.Bottom", "Fluid_Block.Inlet", "Fluid_Block.Outlet", 
					"Plate.LeadingEdge", "Plate.Top", "Plate.TrailingEdge", "Plate.Bottom"});
		
		// Creating a scene of velocity on the "CenterPlane"
		Scenes velocityScene = new Scenes(activeSim, "Velocity");
		Scene velocity_Scene = velocityScene.createScalarScene();
		fieldFunction.setSceneFieldFunction(velocity_Scene, "Velocity", "0");
		velocityScene.addObject2Scene(velocity_Scene, "Fluid", new String[] {"Fluid_Block.Top", "Fluid_Block.Bottom", "Fluid_Block.Inlet", "Fluid_Block.Outlet", 
				"Plate.LeadingEdge", "Plate.Top", "Plate.TrailingEdge", "Plate.Bottom"});
		
		// Creating a scene of wall y+ values on the region "Fluid"
		Scenes wallYScene = new Scenes(activeSim, "WallY+");
		Scene wallY_Scene = wallYScene.createScalarScene();
		fieldFunction.setSceneFieldFunction(wallY_Scene, "WallYplus", "0");
		wallYScene.addObject2Scene(wallY_Scene, "Fluid", 
				new String[] {"Plate.Top", "Plate.Bottom", "Plate.LeadingEdge", "Plate.TrailingEdge"});
		
		physics.convertMeshTo2D("Fluid", 1e-6);
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			REPORTS NODE */
		ReportsMonitorsPlots cl = new ReportsMonitorsPlots(activeSim);
		cl.createForceCoefficientReport("Fluid 2D", new String[] {"Plate.LeadingEdge" , "Plate.Top", "Plate.TrailingEdge", "Plate.Bottom"},
				"Pressure and Shear", 101325.0, new double[] {0, 1.0, 0});
		
		activeSim.getSimulationIterator().run(numIter);
	}
}
