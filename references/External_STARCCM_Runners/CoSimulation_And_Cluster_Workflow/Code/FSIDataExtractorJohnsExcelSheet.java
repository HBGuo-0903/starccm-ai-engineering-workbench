package myStarJavaMacros;

import java.io.File;
import java.io.IOException;

import star.base.report.AreaAverageReport;
import star.base.report.MaxReport;
import star.base.report.MinReport;
import star.common.ImplicitUnsteadyModel;
import star.common.PhysicsContinuum;
import star.common.PrimitiveFieldFunction;
import star.common.Simulation;
import star.common.StarMacro;
import star.common.VectorMagnitudeFieldFunction;
import star.common.XYPlot;
import star.cosimulation.abaqus.AbaqusCoSimulation;
import star.cosimulation.abaqus.AbaqusCoSimulationModel;
import star.cosimulation.common.*;
import star.flow.ConstantDensityModel;
import star.keturb.KEpsilonTurbulence;
import star.keturb.KeTwoLayerAllYplusWallTreatment;
import star.keturb.RkeTwoLayerTurbModel;
import star.material.SingleComponentLiquidModel;
import star.metrics.ThreeDimensionalModel;
import star.segregatedflow.SegregatedFlowModel;
import star.coupledflow.CoupledFlowModel;
import star.turbulence.RansTurbulenceModel;
import star.turbulence.TurbulentModel;
import star.vis.ConstrainedPlaneSection;
import star.vis.FrontalAreaReport;
import star.vis.LinePart;
import star.vis.Scene;
import starClasses.CoSimulationAbaqus;
import starClasses.ContiuumBuilder;
import starClasses.DerivedParts;
import starClasses.FieldFunctions;
import starClasses.ImportCAE;
import starClasses.MeshMorpher;
import starClasses.NewDataReader;
import starClasses.RegionBuilder;
import starClasses.ReportsMonitorsPlots;
import starClasses.Scenes;
import starClasses.SolutionHistoryCreator;
import starClasses.SolversNode;
import starClasses.StoppingCriteria;
import starClasses.Tools;

public class FSIDataExtractorJohnsExcelSheet extends StarMacro 
{
	public void execute() 
	{
		String currentDirectory = System.getProperty("user.dir");
		String abqExecutableFileName = "abq6122.bat";
		
		// Star-CCM+ settings and variables
		Simulation activeSim = getActiveSimulation();
		
		// Reading in the geometry parameters from the external file
		NewDataReader reader = new NewDataReader();
		try 
		{
			reader.readGeometryData(currentDirectory + File.separator + "FSI_Input_File.txt");
		}
		catch (NumberFormatException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (IOException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Grabbing the Co-Simulation settings and variables
		String plateGeometry = reader.getStringData("plateGeometry");
		String couplingScheme = reader.getStringData("couplingScheme");
		double couplingTimeStep = reader.getDoubleData("timeStep");
		int numAbaqusCPUs = reader.getIntData("abaqusCPUs");
		int numExchanges = reader.getIntData("numImplicitExch");
		int iterationsPerExchange = reader.getIntData("iterPerExch");
		int iterationsPerTS = reader.getIntData("iterPerTS");
		double deflectionUnderRelax = reader.getDoubleData("plateUnderRelax");
		String morphAtInnIter = reader.getStringData("morphAtInnIter");
		double maxSimTime = reader.getDoubleData("maxSimTime");
		String abaqusInputFilePath = "D:/Users/cj8q5/Simulations/Current Simulation/Abaqus/" + 
			couplingScheme + "_FreePlate.inp";
		
		// Grabbing the geometry parameters
		double plateLength = reader.getDoubleData("plateLength");
		double plateHeight = reader.getDoubleData("plateThickness");
		double wettedPlateWidth = reader.getDoubleData("plateWidth");
		double smallChannelHeight = reader.getDoubleData("smChHeight");
		double largeChannelHeight = reader.getDoubleData("lgChHeight");
		double inletLength = reader.getDoubleData("inletPlLength");
		double outletLength = reader.getDoubleData("outletPlLength");
		double avgChVel = reader.getDoubleData("avgChVelocity");
		
		double r_in = wettedPlateWidth/(Math.PI/4);
		
		double[] initialVel = {0.0, -avgChVel, 0.0};
		double inletVel = Math.abs((smallChannelHeight + largeChannelHeight)/(smallChannelHeight + largeChannelHeight + plateHeight)*avgChVel);		
				
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			DERIVED PARTS NODE */
		// Creating constrained planes for the small and large channels at mid plate length
		double[] normalVector = {0.0, 1.0, 0.0};
		double[] originVector = {0.0, 0.0*0.5, 0.0};
		
		double[] contourCoordinates_100mil = {0.0, 				0.0*0.5, 0.0, 
											  0.0, 				0.0*0.5, -largeChannelHeight,
											  wettedPlateWidth, 0.0*0.5, -largeChannelHeight,
											  wettedPlateWidth, 0.0*0.5, 0.0,
											  0.0, 				0.0*0.5, 0.0};
		DerivedParts constrainedPlane_100mil = new DerivedParts(activeSim, new String[] {"Fluid"});
		ConstrainedPlaneSection constrainedPlane100mil = constrainedPlane_100mil.createConstrainedPlane("100 mil Constrained Plane", 
				normalVector, originVector, contourCoordinates_100mil);

		double[] contourCoordinates_80mil = {0.0, 				0.0*0.5, plateHeight, 
											 0.0, 				0.0*0.5, plateHeight + smallChannelHeight,
											 wettedPlateWidth,  0.0*0.5, plateHeight + largeChannelHeight,
											 wettedPlateWidth,  0.0*0.5, plateHeight,
											 0.0, 				0.0*0.5, plateHeight};
		DerivedParts constrainedPlane_80mil = new DerivedParts(activeSim, new String[] {"Fluid"});
		ConstrainedPlaneSection constrainedPlane80mil = constrainedPlane_80mil.createConstrainedPlane("80 mil Constrained Plane", 
				normalVector, originVector, contourCoordinates_80mil);
		
		// Creating reports to find the max and average plate deflection at the leading and trailing edges of the plate
		FieldFunctions fieldFunctions = new FieldFunctions(activeSim);
		PrimitiveFieldFunction plateDeflection = fieldFunctions.getNodalDisplacement();
		VectorMagnitudeFieldFunction plateDeflection_Mag = ((VectorMagnitudeFieldFunction) plateDeflection.getMagnitudeFunction());
		
		ReportsMonitorsPlots reports = new ReportsMonitorsPlots(activeSim);
		MaxReport maxDeflection_LE = reports.createMaxReport(new String[] {"Fluid", "Fluid.FSI_Top"}, "MaxDeflectionLeadingEdge");
		AreaAverageReport avgDeflection_LE = reports.createAverageReport(new String[] {"Fluid", "Fluid.FSI_Top"}, "AvgDeflectionLeadingEdge");

		maxDeflection_LE.setScalar(plateDeflection_Mag);
		avgDeflection_LE.setScalar(plateDeflection_Mag);
		maxDeflection_LE.printReport();
		avgDeflection_LE.printReport();
		
		MaxReport maxDeflection_TE = reports.createMaxReport(new String[] {"Fluid", "Fluid.FSI_Bottom"}, "MaxDeflectionTrailingEdge");
		AreaAverageReport avgDeflection_TE = reports.createAverageReport(new String[] {"Fluid", "Fluid.FSI_Bottom"}, "AvgDeflectionTrailingEdge");
		
		maxDeflection_TE.setScalar(plateDeflection_Mag);
		avgDeflection_TE.setScalar(plateDeflection_Mag);
		maxDeflection_TE.printReport();
		avgDeflection_TE.printReport();
		
		// Creating reports to find the average wall y+ values on the walls of the plate
		PrimitiveFieldFunction wallYplus = fieldFunctions.getWallYPlusFunction();
		AreaAverageReport wallYPlus_100mil = reports.createAverageReport(new String[] {"Fluid", "Fluid.FSI_Back"}, "AverageWally+_100mil");
		wallYPlus_100mil.setScalar(wallYplus);
		wallYPlus_100mil.printReport();
		
		AreaAverageReport wallYPlus_80mil = reports.createAverageReport(new String[] {"Fluid", "Fluid.FSI_Front"}, "AverageWallY+_80mil");
		wallYPlus_80mil.setScalar(wallYplus);
		wallYPlus_80mil.printReport();
		
		// Creating a report for the pressure drop through the model
		PrimitiveFieldFunction staticPressure = fieldFunctions.getStaticPressureFunction();
		MaxReport maxPressureInlet = reports.createMaxReport(new String[] {"Fluid", "Fluid.Inlet"}, "InletPressure");
		maxPressureInlet.setScalar(staticPressure);
		maxPressureInlet.printReport();
		
		// Creating reports for the leading edge pressure
		MaxReport maxPressure_100mil = reports.createMaxReport(new String[] {"Fluid", "Fluid.FSI_Back"}, "MaxPressure_100mil");
		MinReport minPressure_100mil = reports.createMinReport(new String[] {"Fluid", "Fluid.FSI_Back"}, "MinPressure_100mil");
		maxPressure_100mil.setScalar(staticPressure);
		minPressure_100mil.setScalar(staticPressure);
		//maxPressure_100mil.printReport();
		//minPressure_100mil.printReport();
		
		MaxReport maxPressure_80mil = reports.createMaxReport(new String[] {"Fluid", "Fluid.FSI_Front"}, "MaxPressure_80mil");
		MinReport minPressure_80mil = reports.createMinReport(new String[] {"Fluid", "Fluid.FSI_Front"}, "MinPressure_80mil");
		maxPressure_80mil.setScalar(staticPressure);
		minPressure_80mil.setScalar(staticPressure);
		//maxPressure_80mil.printReport();
		//minPressure_80mil.printReport();
		
		activeSim.println(maxPressure_80mil.getValue() - maxPressure_100mil.getValue());
		activeSim.println(minPressure_80mil.getValue() - minPressure_100mil.getValue());
		
		// Creating reports for finding the mass flow rate in the two flow channels
		double[] viewUpVector = {1.0, 0.0, 0.0};
		PrimitiveFieldFunction velocity = fieldFunctions.getVelocityFunction();
		VectorMagnitudeFieldFunction velocityVector = ((VectorMagnitudeFieldFunction) velocity.getMagnitudeFunction());
		
		AreaAverageReport velocity_100mil = reports.createAverageReport(new String[] {"Fluid"}, "AvgVelocity_100mil");
		velocity_100mil.getParts().addObjects(constrainedPlane100mil);
		velocity_100mil.setScalar(velocityVector);
		double largeChannelVelocity = velocity_100mil.getValue();
		
		FrontalAreaReport area_100mil = reports.createAreaReport("Area_100mil", viewUpVector, normalVector);
		area_100mil.getParts().setObjects(constrainedPlane100mil);
		double largeChannelArea = area_100mil.getValue();
		activeSim.println(largeChannelVelocity*largeChannelArea*997.561);
		
		AreaAverageReport velocity_80mil = reports.createAverageReport(new String[] {"Fluid"}, "AvgVelocity_80mil");
		velocity_80mil.getParts().addObjects(constrainedPlane80mil);
		velocity_80mil.setScalar(velocityVector);
		double smallChannelVelocity = velocity_80mil.getValue();
		
		FrontalAreaReport area_80mil = reports.createAreaReport("Area_80mil", viewUpVector, normalVector);
		area_80mil.getParts().setObjects(constrainedPlane80mil);
		double smallChannelArea = area_80mil.getValue();
		activeSim.println(smallChannelVelocity*smallChannelArea*997.561);

	}
}