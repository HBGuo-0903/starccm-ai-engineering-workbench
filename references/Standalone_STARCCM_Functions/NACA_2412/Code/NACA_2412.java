package myStarJavaMacros;

import star.cadmodeler.PointSketchPrimitive;
import star.cadmodeler.Sketch;
import star.common.BrickVolumeShape;
import star.common.PhysicsContinuum;
import star.common.Simulation;
import star.common.StarMacro;
import star.common.SteadyModel;
import star.common.XYPlot;
import star.coupledflow.CoupledFlowModel;
import star.flow.ConstantDensityModel;
import star.material.SingleComponentGasModel;
import star.meshing.VolumeSource;
import star.metrics.ThreeDimensionalModel;
import star.saturb.SaLowYplusWallTreatment;
import star.saturb.SaTurbModel;
import star.saturb.SpalartAllmarasTurbulence;
import star.turbulence.RansTurbulenceModel;
import star.turbulence.TurbulentModel;
import star.vis.Scene;
import starClasses.ContiuumBuilder;
import starClasses.FieldFunctions;
import starClasses.GeometryBuilder;
import starClasses.RegionBuilder;
import starClasses.ReportsMonitorsPlots;
import starClasses.Scenes;
import starClasses.StoppingCriteria;
import starClasses.TrimmerMesher;

public class NACA_2412 extends StarMacro 
{
	public void execute() 
	{
		
		Simulation activeSim = getActiveSimulation();
		double v_inf = 50.0;
		double alpha = -2.0749;
		
		// Computing the components of the inlet velocity vector
		double dir_x = Math.cos(alpha*Math.PI/180.0);
		double dir_y = Math.sin(alpha*Math.PI/180.0);
		
		double bulkFluidHeight = 25;
		double bulkFluidLength = 30;
		
		double[] xyPoints = {0, 0,	0.0964977584237475,	0.0554465543500332, 0.197134807759545, 0.0723038448091064,
				0.298500037005369, 0.0787485197852375, 0.4, 0.0780301084764790, 0.550353735809986, 0.0682730133980573,
				0.700523361843123, 0.0516353290186108, 0.850439733913068, 0.0292709159431620, 1.0, 0.0, 0.849560266086932, 
				-0.0117709159431620, 0.699476638156877,	-0.0216353290186108, 0.549646264190014,	-0.0307730133980573, 0.4,	
				-0.0380301084764790, 0.301499962994631,	-0.0412485197852375, 0.202865192240455, -0.0423038448091064, 
				0.103502241576253, -0.0379465543500332, 0, 0};
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			GEOMETRY NODE */
		GeometryBuilder airfoil = new GeometryBuilder(activeSim, "NACA 2412", "XY");
		Sketch sketch = airfoil.createSketch();
		
		// Creating all of the upper and lower points on the NACA 2412 airfoil
		for(int i = 0; i < xyPoints.length - 1; i = i + 2)
		{
			airfoil.createSketchPoint(sketch, 
					new double[] {xyPoints[i], xyPoints[i + 1]});
		}

		// Creating the spline around the NACA 2412 airfoil upper and lower XY points
		PointSketchPrimitive firstLastSplinePoint = 
				airfoil.createSketchPoint(sketch, new double[] {0, 0});
		airfoil.createSpline(sketch, xyPoints, firstLastSplinePoint, firstLastSplinePoint);
		
		// Creating the extruded airfoil
		String[] airFoilSurfaces = 
			{"Front", "LowerSurface", "UpperSurface", "TrailingEdge_0", "TrailingEdge_1", "Back"};
		airfoil.createPart(sketch, 1);
		airfoil.splitSurface(7.50, airFoilSurfaces, true);
		//airfoil.partTranslate(new double[] {bulkFluidLength/2 - 1.5, bulkFluidHeight/2, 0});
		
		// Creating the bulk fluid part
		String[] fluidSurfaceNames = 
			{"Inlet", "Outlet", "Front-Symmetry", "Back-Symmetry"};
		GeometryBuilder fluid = new GeometryBuilder(activeSim, "Fluid", "XY");
		fluid.airfoilBulkFluidBuilder(bulkFluidHeight, bulkFluidLength, 0.1);
		fluid.splitSurface(10, fluidSurfaceNames, true);
		
		// Creating the fluid with the airfoil geometry cut from it
		GeometryBuilder airfoilFluid = new GeometryBuilder(activeSim, "Airfoil Fluid", "XY");
		airfoilFluid.partSubtract("Fluid", "NACA 2412", "Airfoil Fluid");
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			REGIONS NODE */
		RegionBuilder fluidRegion = new RegionBuilder(activeSim, "Airfoil Fluid");
		fluidRegion.part2Region("Airfoil Fluid", true);
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			CONTINUA NODE */
		ContiuumBuilder airPhysics = new ContiuumBuilder(activeSim);
		PhysicsContinuum air = airPhysics.createPhysicsContinua("Air");
		
		// Enabling the physics models for the simulation
		air.enable(ThreeDimensionalModel.class);
		air.enable(SteadyModel.class);
		air.enable(SingleComponentGasModel.class);
		air.enable(CoupledFlowModel.class);
		air.enable(ConstantDensityModel.class);
		air.enable(TurbulentModel.class);
		air.enable(RansTurbulenceModel.class);
		//air.enable(KEpsilonTurbulence.class);
		//air.enable(RkeTwoLayerTurbModel.class);
		//air.enable(KeTwoLayerAllYplusWallTreatment.class);
		air.enable(SpalartAllmarasTurbulence.class);
		air.enable(SaTurbModel.class);
		air.enable(SaLowYplusWallTreatment.class);
		
		// Setting the Courant number
		airPhysics.setCourantNumber(25);
		
		//fluidRegion.deleteBoundary("NACA 2412.TrailingEdge_1");
		fluidRegion.setBoundaryCondition("Fluid.Back-Symmetry", "Symmetry", 
				new double[] {0, 0, 0}, 0);
		fluidRegion.setBoundaryCondition("Fluid.Front-Symmetry", "Symmetry", 
				new double[] {0, 0, 0}, 0);
		fluidRegion.setBoundaryCondition("Fluid.Inlet", "Velocity Inlet", 
				new double[] {dir_x, dir_y, 0}, 50);
		fluidRegion.setBoundaryCondition("Fluid.Outlet", "Pressure Outlet", 
				new double[] {0, 0, 0}, 0);
		
		// Setting the initial velocity conditions in the simulation
		airPhysics.setInitialConditionsVel(air, 
				new double[] {dir_x*v_inf, dir_y*v_inf, 0});
		
		double meshBaseSize = 0.5;
		double airfoilSurfaceMeshSize = 0.25;
		double surfaceGrowthRate = 1.05;
		
		// Setting up the trimmer mesh
		TrimmerMesher mesh = new TrimmerMesher(activeSim, "Airfoil Fluid");
		mesh.meshInParallel(true);
		mesh.setMesherSettings(meshBaseSize, 200, 100);
		mesh.setPrismLayerSettings(0.025, 1e-6, 50);
		mesh.setSurfaceGrowthRate(surfaceGrowthRate);
		mesh.setCustomBoundarySurfaceSize("NACA 2412.LowerSurface", 
				airfoilSurfaceMeshSize, airfoilSurfaceMeshSize);
		mesh.setCustomBoundarySurfaceSize("NACA 2412.TrailingEdge_0", 
				airfoilSurfaceMeshSize, airfoilSurfaceMeshSize);
		mesh.setCustomBoundarySurfaceSize("NACA 2412.UpperSurface", 
				airfoilSurfaceMeshSize, airfoilSurfaceMeshSize);
		mesh.setCustomBoundarySurfaceSize("NACA 2412.TrailingEdge_1", 
				airfoilSurfaceMeshSize, airfoilSurfaceMeshSize);
		
		// Creating volumetric control around the airfoil
		BrickVolumeShape volumeShapeAirfoil = 
				airfoilFluid.createVolumeShapeBlock(new double[] {-2, -2, 0}, 
						new double[] {3, 3, 0.1});
		VolumeSource volumeSourceAirfoil = 
				mesh.createBlockVolumetricControl(volumeShapeAirfoil);
		mesh.setTrimmerVolumetricControl(volumeSourceAirfoil, 3.0);
		mesh.setTrimmerVolumetricAnisotropicSize(volumeSourceAirfoil, 
				false, 1, 
				false, 1, 
				true, 200);
		
		// Creating volumetric control for the airfoil's wake
		/**
		BrickVolumeShape volumeShapeWake = 
				airfoilFluid.createVolumeShapeBlock(new double[] {2, -0.25, 0}, 
						new double[] {30, 0.25, 0.1});
		VolumeSource volumeSourceWake = 
				mesh.createBlockVolumetricControl(volumeShapeWake);
		mesh.setTrimmerVolumetricControl(volumeSourceWake, 3.0);
		mesh.setTrimmerVolumetricAnisotropicSize(volumeSourceWake, 
				false, 1, 
				false, 1, 
				true, 200);
		*/
		
		// Creating volumetric control for the airfoil's outer area
		/**
		BrickVolumeShape volumeShapeOuter = 
				airfoilFluid.createVolumeShapeBlock(new double[] {-2, -4, 0}, 
						new double[] {2.5, 4, 0.1});
		VolumeSource volumeSourceOuter = 
				mesh.createBlockVolumetricControl(volumeShapeOuter);
		mesh.setTrimmerVolumetricControl(volumeSourceOuter, 12);
		mesh.setTrimmerVolumetricAnisotropicSize(volumeSourceOuter, 
				false, 1, 
				false, 1, 
				true, 200);
		*/
		//mesh.generateMesh();
		
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			REPORTS AND MONITORS NODE */
		// Creating the lift and drag coefficients
		ReportsMonitorsPlots reportsMonitorsPlots = new ReportsMonitorsPlots(activeSim);
		reportsMonitorsPlots.createForceCoefficientReportMonitorPlot("Airfoil Fluid", 
				new String[] {"NACA 2412.LowerSurface", "NACA 2412.UpperSurface"},
				"Pressure and Shear", 0, v_inf, 1, 1.18514, new double[] {-dir_y, dir_x, 0}, 
				"Lift Coefficient Report");
		reportsMonitorsPlots.createForceCoefficientReportMonitorPlot("Airfoil Fluid", 
				new String[] {"NACA 2412.LowerSurface", "NACA 2412.UpperSurface"},
				"Pressure and Shear", 0, v_inf, 1, 1.18514, new double[] {dir_x, dir_y, 0}, 
				"Drag Coefficient Report");
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			STOPPING CRITERIA NODE */
		new StoppingCriteria(activeSim).maxSteps(200000, "OR", true);
		
		StoppingCriteria stoppingCriteria = new StoppingCriteria(activeSim);
		stoppingCriteria.createAsymStoppingCriteria("Lift Coefficient Report Monitor", "Report", 1e-4, 25, "AND", true);
		stoppingCriteria.createAsymStoppingCriteria("Drag Coefficient Report Monitor", "Report", 1e-6, 25, "AND", true);		
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			PLOTS AND SCENES NODE */
		// Turning off the "Auto" normalization option for all the residual monitors
		reportsMonitorsPlots.residualNormalization(
				new String[] {"Continuity", "Sa_nut", "X-momentum", "Y-momentum", "Z-momentum"});
		
		FieldFunctions fieldFunction = new FieldFunctions(activeSim);
		
		// Creating a velocity scene
		Scenes velocityScene = new Scenes(activeSim, "Velocity");
		Scene velocity = velocityScene.createScalarScene();
		velocityScene.addObject2Scene(velocity, "Airfoil Fluid", 
				new String[] {"Fluid.Front-Symmetry"});
		fieldFunction.setSceneFieldFunction(velocity, "Velocity", "0");
		
		// Creating a pressure scene
		Scenes pressureScene = new Scenes(activeSim, "Pressure");
		Scene pressure = pressureScene.createScalarScene();
		pressureScene.addObject2Scene(pressure, "Airfoil Fluid", 
				new String[] {"Fluid.Front-Symmetry"});
		fieldFunction.setSceneFieldFunction(pressure, "StaticPressure", "0");
		
		// Converting the mesh to 2D
		//airPhysics.convertMeshTo2D("Airfoil Fluid", 1e-6);
		
		// Creating an XY plot of the airfoil pressure
		ReportsMonitorsPlots airfoilPressure = new ReportsMonitorsPlots(activeSim);
		XYPlot afPressure = airfoilPressure.createXYPlot(new double[] {1, 0, 0}, 
				"Airfoil Pressure", "Pressure");
		airfoilPressure.addObjects2XYPlot(afPressure, "Airfoil Fluid 2D", 
				new String[] {"NACA 2412.LowerSurface", "NACA 2412.UpperSurface"});
		fieldFunction.setXYPlotFieldFunction(afPressure, "StaticPressure", "0");
		
		// Creating an XY plot of the airfoil's wall y+ values
		ReportsMonitorsPlots airfoilWallY = new ReportsMonitorsPlots(activeSim);
		XYPlot afWallY = airfoilWallY.createXYPlot(new double[] {1, 0, 0}, 
				"Airfoil Wall y+", "Wall y+");
		airfoilWallY.addObjects2XYPlot(afWallY, "Airfoil Fluid 2D", 
				new String[] {"NACA 2412.LowerSurface", "NACA 2412.UpperSurface"});
		fieldFunction.setXYPlotFieldFunction(afWallY, "WallYplus", "0");
		
		// Starting the simulation
		//activeSim.getSimulationIterator().run(25000);
		
	}
}