// STAR-CCM+ macro: MainMacro.java
// Last Modified by Viktor Velev (Formula Student Team Delft)
package macro;

import java.util.*;
import java.net.*;
import java.io.*;
import star.common.*;
import star.base.neo.*;
 
import star.vis.*;
import star.coupledflow.*;
import star.base.neo.*;
import star.flow.*;
import star.meshing.*;
import java.nio.file.Paths;

/** 
 * Brace yourself. You will see some dark magic 
 * JVM tricks to be able to modulize
 * the whole framework while working with STAR-CCM 
 * [JVMH] - An annotation for JVM Hack comments explaining stuff
 * */

public class FullCore extends StarMacro {
	
	/** 
	 * This script assumes it's running environment 
	 * is the STAR-CCM+ 11.04 Java Virtual Machine (JVM) 
	 * 		- Has STAR-CCM Server configured 
	 * 		and running with the loaded .sim file
	 * */

	/** Change 'maximumIterations' to X to run */
    int maximumIterations = 3500;
	
    private int clipping;
	private DoubleVector scaleSCp;
	private DoubleVector scaleTCp;
	private DoubleVector scaleVel;
	private DoubleVector scaleVor;

	private double sliceStep;
	private HashMap<String, DoubleVector> ranges;

	/** [JVMH] For future Inter cross-JVM communication hacks */
	// Class CFDPipeline;
	// pulic String pwd = Paths.get(".").toAbsolutePath()
	// public String projectPath = "/home/viktorv/Projects/FSTeamDelft/Tirion/";
	
	public Simulation simulation;

	private MeshPipelineController meshPipelineController;
	private SolverStoppingCriterionManager solverCriterionManager;

	private String OS = System.getProperty("os.name").toLowerCase();

	public void execute() {
        // Variables used for post processing
        clipping = 0;
		sliceStep = 0.05;
		// Maybe change this from -0.1
		// Reference
		ranges = new HashMap<String, DoubleVector>();
		ranges.put("x", new DoubleVector(new double[] { -0.05, 2.95 })); 	// X
		ranges.put("y", new DoubleVector(new double[] { 0, 0.8 })); 		// Y [in the case of non-symmetry, goes from -0.8]
		ranges.put("z", new DoubleVector(new double[] { 0, 1.1 })); 		// Z

		// TODO: Number of slices is absolute distance [(last point - first point) /
		// sliceStep]
		// Z doesn't display but it outputs
		/**
		 * Scale variables
		 */
		scaleSCp = new DoubleVector(new double[] { -5, 1 });
		scaleTCp = new DoubleVector(new double[] { -.1, 1 });
		scaleVel = new DoubleVector(new double[] { 0, 30 });
        scaleVor = new DoubleVector(new double[] { 0, 1000 });
        
        simulation = getActiveSimulation();

		/** The whole sequential CFD pipeline */
		runSimulation(maximumIterations);
		runPostProcessing();
		// csvDataExport();
		// pythonPostProcExport();

		/** 
		 * You can just comment out any step of the pipeline and the rest will work 
		 * (keep initialization though) 
		 * */
	}

	private void runSimulation(int maxIterations) {
		/** 
		 * Get the needed criterion, modify them and run the simulation 
		 * The simulation will stop if the number of iterations has reached 
		 * the previously specified "maximumIterations" (class global)
		 * */
        log("Meshing...");

		meshPipelineController = simulation.get(MeshPipelineController.class);
		solverCriterionManager = simulation.getSolverStoppingCriterionManager();
		
		meshPipelineController.generateVolumeMesh();
		StepStoppingCriterion stepStoppingCriterion = (StepStoppingCriterion) solverCriterionManager
			.getSolverStoppingCriterion("Maximum Steps");
		
		stepStoppingCriterion.setMaximumNumberSteps(maxIterations);
	
		simulation.getSimulationIterator().run();
	}

    public void runPostProcessing() {
        
        log("Post Processing...");

        try {
			execPostProcessing();
		} catch(Exception e) {
			log(e);
		}
	}

	private void execPostProcessing() throws Exception {
		// TODO: Fix skin friction

		String namePath, simName, figName, mainFolderName;
		String iterCpY, iterCpYName, iterTCpy, iterSCpx, iterVel, iterTCpx, iterVorX;
		String TCpZFolder, TCpYFolder, TCpXFolder;
		String SCpXFolder, SCpYFolder, SCpZFolder;
		String SkinFolder, SCpFolder;
		String VelXFolder, VelYFolder, VelZFolder;
		String VorXFolder;
		String VectVelYFolder, VectVelXFolder, VectVelZFolder;

		String simPath;

		/*
		 * -----------------------------------------------------------------------------
		 * -------------- CREATE FOLDER TO SAVE SIMULATIONS --------------
		 * -----------------------------------------------------------------------------
		 * 
		 */

		simulation = getActiveSimulation();

		simName = simulation.getPresentationName();
		simPath = simulation.getSessionDir();
			
		mainFolderName = simPath + "/PostProcessing#" + simName;
		TCpZFolder = mainFolderName + "/TCpZ";
		TCpYFolder = mainFolderName + "/TCpY";
		TCpXFolder = mainFolderName + "/TCpX";
		SCpXFolder = mainFolderName + "/SCpX";
		SCpYFolder = mainFolderName + "/SCpY";
		SCpFolder = mainFolderName + "/SCp";
		SkinFolder = mainFolderName + "/Skin";
		SCpZFolder = mainFolderName + "/SCpZ";
		VelXFolder = mainFolderName + "/VelX";
		VelYFolder = mainFolderName + "/VelY";
		VelZFolder = mainFolderName + "/VelZ";
		VorXFolder = mainFolderName + "/VorX";
		VectVelYFolder = mainFolderName + "/VectVelY";
		VectVelXFolder = mainFolderName + "/VectVelX";
		VectVelZFolder = mainFolderName + "/VectVelZ";

		new File(mainFolderName).mkdir();
		new File(TCpZFolder).mkdir();
		new File(TCpYFolder).mkdir();
		new File(TCpXFolder).mkdir();
		new File(SCpXFolder).mkdir();
		new File(SCpYFolder).mkdir();
		new File(SCpZFolder).mkdir();
		new File(SCpFolder).mkdir();
		new File(VelXFolder).mkdir();
		new File(VelYFolder).mkdir();
		new File(VelZFolder).mkdir();
		new File(VorXFolder).mkdir();
		new File(VectVelYFolder).mkdir();
		new File(VectVelXFolder).mkdir();
		new File(VectVelZFolder).mkdir();

		PlaneSection planeSection = (PlaneSection) simulation.getPartManager().createImplicitPart(
				new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] { 0.0, 0.0, 1.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 0.0 }), 0, 1, new DoubleVector(new double[] { 0.0 }));

		Coordinate coordinate = planeSection.getOriginCoordinate();
		Units units = ((Units) simulation.getUnitsManager().getObject("m"));
		coordinate.setCoordinate(units, units, units, new DoubleVector(new double[] { 0.0, 0.1, 0.0 }));

		Coordinate coordinate_1 = planeSection.getOrientationCoordinate();
		coordinate_1.setCoordinate(units, units, units, new DoubleVector(new double[] { 0.0, 1.0, 0.0 }));

		simulation.getSceneManager().createScalarScene("Scalar Scene", "Outline", "Scalar");
		Scene scalarScene = simulation.getSceneManager().getScene("Scalar Scene 1");
		scalarScene.initializeAndWait();

		PartDisplayer partDisplayer = ((PartDisplayer) scalarScene.getDisplayerManager().getDisplayer("Outline 1"));
		partDisplayer.initialize();

		ScalarDisplayer scalarDisplayer = ((ScalarDisplayer) scalarScene.getDisplayerManager()
				.getDisplayer("Scalar 1"));
		scalarDisplayer.initialize();
		scalarScene.open(true);

		SceneUpdate sceneUpdate = scalarScene.getSceneUpdate();
		HardcopyProperties hardcopyProperties = sceneUpdate.getHardcopyProperties();
		hardcopyProperties.setCurrentResolutionWidth(1271);
		hardcopyProperties.setCurrentResolutionHeight(587);

		scalarScene.resetCamera();
		scalarScene.getDisplayerManager().deleteDisplayers(new NeoObjectVector(new Object[] { partDisplayer }));
		// scalarDisplayer.getInputParts().setQuery(null); // Keep in mind
		scalarDisplayer.getInputParts().setObjects(planeSection);

		log("Searching for Primitive Field Functions");
		log(simulation.getFieldFunctionManager().getFunction("MeanStatic_CPMonitor"));
		PrimitiveFieldFunction meanStaticCPMonitorFieldFunction = ((PrimitiveFieldFunction) simulation
				.getFieldFunctionManager().getFunction("MeanStatic_CPMonitor"));

		log(simulation.getFieldFunctionManager().getFunction("MeanTotal_CPMonitor"));
		PrimitiveFieldFunction meanTotalCPMonitorFieldFunction = ((PrimitiveFieldFunction) simulation
				.getFieldFunctionManager().getFunction("MeanTotal_CPMonitor"));

		log(simulation.getFieldFunctionManager().getFunction("MeanVelocityMonitor"));
		PrimitiveFieldFunction meanVelocityMonitorFieldFunction = ((PrimitiveFieldFunction) simulation
				.getFieldFunctionManager().getFunction("MeanVelocityMonitor"));

		log(simulation.getFieldFunctionManager().getFunction("MeanVorticityMonitor"));
		PrimitiveFieldFunction meanVorticityMonitorFieldFunction = ((PrimitiveFieldFunction) simulation
				.getFieldFunctionManager().getFunction("MeanVorticityMonitor"));
		
		// Skin friction stuff
		// log(simulation.getFieldFunctionManager().getFunction("MeanSkinFrictionMonitor"));
		// PrimitiveFieldFunction primitiveFieldFunction_4 = ((PrimitiveFieldFunction)
		// simulation.getFieldFunctionManager().getFunction("MeanSkinFrinctionMonitor"));

		log("Performing a boundary check accross all regions.");

		int regionId = 0, boundaryIndex = 0;
		Map<Integer, ArrayList<Object>> regionBoundaries = new HashMap<Integer, ArrayList<Object>>();

		ArrayList<Boundary> allBoundaries = new ArrayList<Boundary>();
		ArrayList<Region> allRegions = new ArrayList<Region>();

		ArrayList<ModelPart> objects = new ArrayList<ModelPart>();

		for (Region region : simulation.getRegionManager().getObjects()) {
			regionBoundaries.put(regionId, new ArrayList<Object>());

			for (Boundary el : region.getBoundaryManager().getObjects()) {
				log(el.toString() + " ID: " + boundaryIndex);
				regionBoundaries.get(regionId).add(el);
				allBoundaries.add(el);
				objects.add(el);
				boundaryIndex++;
			}

			allRegions.add(region);
			objects.add(region);
			regionId++;
		}
		
		log("Boundary check done.");
		planeSection.getInputParts().setObjects(objects);

		CurrentView currentView = scalarScene.getCurrentView();

		scalarScene.setViewOrientation(
			new DoubleVector(new double[] { 0.0, -1.0, 0.0 }),
			new DoubleVector(new double[] { 0.0, 0.0, 1.0 })
		);

		SymmetricRepeat symmetricRepeat = null;

		try {
			if (simulation.getTransformManager().getObject("Symmetry 1") != null) {
				symmetricRepeat = ((SymmetricRepeat) simulation.getTransformManager().getObject("Symmetry 1"));
				log("'Symmetry 1' Object found.");
			}
		} catch (Exception e) {
			log("{WARNING}");
			log(e);
			log("Continuing...");
		}

		if (symmetricRepeat == null) {	
			try {
				if (simulation.getTransformManager().getObject("Symmetry") != null && symmetricRepeat == null) {
					symmetricRepeat = ((SymmetricRepeat) simulation.getTransformManager().getObject("Symmetry"));
					log("'Symmetry' Object found.");
				}
			} catch (Exception e) {
				log("{WARNING}");
				log(e);
				log("No Symmetry Transform Object. This script will probably not continue working. [stub! TODO]");
				throw new Exception("TODO: [Implement better symmetry handling.]");
			}
		}

		SimpleAnnotation simpleAnnotation = simulation.getAnnotationManager().createSimpleAnnotation();

		simpleAnnotation.setPresentationName("Figure_name");
		simpleAnnotation.setDefaultHeight(0.03);

		FixedAspectAnnotationProp fixedAspectAnnotationProp_1 = (FixedAspectAnnotationProp) scalarScene
				.getAnnotationPropManager().createPropForAnnotation(simpleAnnotation);

		double iterateUntilY = ((ranges.get("y").get(1) - ranges.get("y").get(0)) / sliceStep) + 1;
        // All Y axis Post processing
        
		for (int iterY = 0; iterY < iterateUntilY; iterY++) {
			
			progressPP("Y-Axis", iterY, (int) iterateUntilY);

			scalarDisplayer.getScalarDisplayQuantity().setFieldFunction(meanStaticCPMonitorFieldFunction);
			scalarDisplayer.getScalarDisplayQuantity().setRange(scaleSCp);
			scalarDisplayer.getScalarDisplayQuantity().setClip(clipping);
			planeSection.getInputParts().setQuery(null);

			coordinate.setCoordinate(units, units, units,
					new DoubleVector(new double[] { 0.0, 0.0001 + sliceStep * iterY, 0.0 }));

			scalarScene.setMeshOverrideMode(0);
			currentView.setInput(
				new DoubleVector(new double[] { 0.6, 0.0001 + sliceStep * iterY, 0.5 }),
				new DoubleVector(new double[] { 0.6, -50 + sliceStep * iterY, 1.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 
				1, 1
			);

			String iterString = (iterY >= 10) ? String.valueOf(iterY) : "0" + String.valueOf(iterY);
			figName = simName + " " + "y=" + String.format("%.2f", sliceStep * iterY) + "m";
			simpleAnnotation.setText(figName);

			namePath = SCpYFolder + "/SCpY_" + iterString + ".png";
			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

			// ***********************************************************************************
			scalarDisplayer.getScalarDisplayQuantity().setFieldFunction(meanVelocityMonitorFieldFunction);
			scalarDisplayer.getScalarDisplayQuantity().setRange(scaleVel);

			namePath = VelYFolder + "/VelY_" + iterString + ".png";
			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

			// ***********************************************************************************
			scalarDisplayer.getScalarDisplayQuantity().setFieldFunction(meanTotalCPMonitorFieldFunction);
			scalarDisplayer.getScalarDisplayQuantity().setRange(scaleTCp);

			namePath = TCpYFolder + "/TCpY_" + iterString + ".png";
			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		scalarDisplayer.setVisTransform(symmetricRepeat);
		coordinate_1.setCoordinate(units, units, units, new DoubleVector(new double[] { 1.0, 0.0, 0.0 }));

		// All X-axis Post processing
		double iterateUntilX = ((ranges.get("x").get(1) - ranges.get("x").get(0)) / sliceStep) + 1;

		for (int iterX = -1; iterX < iterateUntilX; iterX++) {
			progressPP("X-Axis", iterX, (int) iterateUntilX);

			coordinate.setCoordinate(units, units, units,
				new DoubleVector(new double[] { -0.85 + sliceStep * iterX, 0.0, 0.0 }));

			scalarScene.setMeshOverrideMode(0);
			currentView.setInput(
				new DoubleVector(new double[] { -0.85 + sliceStep * iterX, 0.0, 0.5 }),
				new DoubleVector(new double[] { -40 - 0.8 + sliceStep * iterX, 0.0, 1.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }),
				1, 1
			);

			figName = simName + " " + "x=" + String.format("%.2f", sliceStep * iterX) + "m";

			scalarDisplayer.getScalarDisplayQuantity().setFieldFunction(meanTotalCPMonitorFieldFunction);
			scalarDisplayer.getScalarDisplayQuantity().setRange(scaleTCp);

			String iterString = (iterX >= 10) ? String.valueOf(iterX) : "0" + String.valueOf(iterX);
			namePath = TCpXFolder + "/TCpX_" + iterString + ".png";

			simpleAnnotation.setText(figName);
			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
			// ************************************************************************************
			// */
			scalarDisplayer.getScalarDisplayQuantity().setFieldFunction(meanStaticCPMonitorFieldFunction);
			scalarDisplayer.getScalarDisplayQuantity().setRange(scaleSCp);

			namePath = SCpXFolder + "/SCpX_" + iterString + ".png";
			simpleAnnotation.setText(figName);

			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

			// ************************************************************************************
			// */
			scalarDisplayer.getScalarDisplayQuantity().setFieldFunction(meanVelocityMonitorFieldFunction);
			scalarDisplayer.getScalarDisplayQuantity().setRange(scaleVel);

			namePath = VelXFolder + "/VelX_" + iterString + ".png";
			simpleAnnotation.setText(figName);

			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

			// ************************************************************************************
			// */
			scalarDisplayer.getScalarDisplayQuantity().setFieldFunction(meanVorticityMonitorFieldFunction);
			scalarDisplayer.getScalarDisplayQuantity().setRange(scaleVor);

			namePath = VorXFolder + "/VorX_" + iterString + ".png";
			simpleAnnotation.setText(figName);

			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		// TODO Optimize the Z axis loops, Convert them to be with flexible stepsize

		scalarDisplayer.getScalarDisplayQuantity().setFieldFunction(meanTotalCPMonitorFieldFunction);
		
		scalarDisplayer.getScalarDisplayQuantity().setRange(scaleTCp);
		scalarDisplayer.setVisTransform(symmetricRepeat);
		coordinate_1.setCoordinate(units, units, units, new DoubleVector(new double[] { 0, 0, 1 }));
		
		log("{Z-Axis} Calculating.");
		
		for (int iterZ = 0; iterZ < 6; iterZ++) {
			progressPP("Z-Axis", iterZ, 27);

			coordinate.setCoordinate(units, units, units,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.01 * iterZ }));
			scalarScene.setMeshOverrideMode(0);
			currentView.setInput(new DoubleVector(
					new double[] { 0.6, -0.1, 0.0001 + 0.01 * iterZ }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = (iterZ >= 10) ? String.valueOf(iterZ) : "0" + String.valueOf(iterZ);

			namePath = TCpZFolder + "/TCpZ_" + iterTCpx + ".png";
			figName = simName + " " + "z=" + String.format("%.3f", 0.01 * iterZ) + "m";
			simpleAnnotation.setText(figName);

			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		for (int iterZ = 6; iterZ < 27; iterZ++) {
			progressPP("Z-Axis", iterZ, 27);

			coordinate.setCoordinate(units, units, units,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + sliceStep * (iterZ - 4) }));
			scalarScene.setMeshOverrideMode(0);
			currentView.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + sliceStep * (iterZ - 4) }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = (iterZ >= 10) ? String.valueOf(iterZ) : "0" + String.valueOf(iterZ);
			namePath = TCpZFolder + "/TCpZ_" + iterTCpx + ".png";
			figName = simName + " " + "z=" + String.format("%.3f", sliceStep * (iterZ - 4)) + "m";
			simpleAnnotation.setText(figName);

			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		scalarDisplayer.getScalarDisplayQuantity().setFieldFunction(meanStaticCPMonitorFieldFunction);
		scalarDisplayer.getScalarDisplayQuantity().setRange(scaleSCp);

		for (int iterZ = 0; iterZ < 6; iterZ++) {
			progressPP("Z-Axis", iterZ, 27);

			coordinate.setCoordinate(units, units, units,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.01 * iterZ }));
			scalarScene.setMeshOverrideMode(0);
			currentView.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.01 * iterZ }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterSCpx = (iterZ >= 10) ? String.valueOf(iterZ) : "0" + String.valueOf(iterZ);
			namePath = SCpZFolder + "/SCpZ_" + iterSCpx + ".png";
			figName = simName + " " + "z=" + String.format("%.3f", 0.01 * iterZ) + "m";
			simpleAnnotation.setText(figName);

			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		for (int iterZ = 6; iterZ < 27; iterZ++) {
			progressPP("Z-Axis", iterZ, 27);

			coordinate.setCoordinate(units, units, units,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + sliceStep * (iterZ - 4) }));
			scalarScene.setMeshOverrideMode(0);
			currentView.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + sliceStep * (iterZ - 4) }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterSCpx = (iterZ >= 10) ? String.valueOf(iterZ) : "0" + String.valueOf(iterZ);
			namePath = SCpZFolder + "/SCpZ_" + iterSCpx + ".png";
			figName = simName + " " + "z=" + String.format("%.3f", sliceStep * (iterZ - 4)) + "m";
			simpleAnnotation.setText(figName);

			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		scalarDisplayer.getScalarDisplayQuantity().setFieldFunction(meanVelocityMonitorFieldFunction);
		scalarDisplayer.getScalarDisplayQuantity().setRange(scaleVel);

		for (int iterZ = 0; iterZ < 6; iterZ++) {
			progressPP("Z-Axis", iterZ, 27);

			coordinate.setCoordinate(units, units, units,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.01 * iterZ }));
			scalarScene.setMeshOverrideMode(0);
			currentView.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.01 * iterZ }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterVel = (iterZ >= 10) ? String.valueOf(iterZ) : "0" + String.valueOf(iterZ);
			namePath = VelZFolder + "/VelZ_" + iterVel + ".png";
			figName = simName + " " + "z=" + String.format("%.3f", 0.01 * iterZ) + "m";
			simpleAnnotation.setText(figName);

			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		for (int iterZ = 6; iterZ < 27; iterZ++) {
			progressPP("Z-Axis", iterZ, 27);

			coordinate.setCoordinate(units, units, units,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + sliceStep * (iterZ - 4) }));
			scalarScene.setMeshOverrideMode(0);
			currentView.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + sliceStep * (iterZ - 4) }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = (iterZ >= 10) ? String.valueOf(iterZ) : "0" + String.valueOf(iterZ);
			namePath = VelZFolder + "/VelZ_" + iterTCpx + ".png";
			figName = simName + " " + "z=" + String.format("%.3f", sliceStep * (iterZ - 4)) + "m";
			simpleAnnotation.setText(figName);

			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
        }
        
		log("Finished.");
    }
    
	public void checkpoint(String path) {
		simulation.saveState(path);
    }
    
    // Work in progress
	private void csvDataExport() {

		/**
		 * Full export data (residuals) to .csv
		 * 
		 * Previously defined process in the full_export.java #Deprecated#
		 */

		String pathName, simName, mainFolderName, simPath, figName, saveName;

		simName = simulation.getPresentationName();
		simPath = simulation.getSessionDir();
		
		String separator = ((OS.indexOf("win") >= 0) ? "\\" : "/");
		mainFolderName = simPath + separator + "PlotsCSV#" + simName;
		
		log("Saving output to: " + mainFolderName);
		new File(mainFolderName).mkdir();
		
		int all = simulation.getPlotManager().getObjects().size();
		int i = 0;

		for (StarPlot plot : simulation.getPlotManager().getObjects()) {
			
			figName = plot.getPresentationName();
			MonitorPlot monitorPlot = ((MonitorPlot) simulation.getPlotManager().getPlot(figName));
			monitorPlot.export(resolvePath(mainFolderName + separator + figName + ".csv"), ",");	
			// log(resolvePath(mainFolderName + separator + figName + ".csv"));
			i++;
			progress(i, all);
		}
	}
	/** Utility Functions */

	private void checkpointLog(String what, String where) {
		log(String.format("Saved %s at %s.", what, where));
	}

	private void progress(int done, int all) {
		StringBuilder progressBar = new StringBuilder();

		for(int i = 0; i < all; i++) {
			if (i < done) {
				progressBar.append("#");
			} else {
				progressBar.append("-");
			} 
		}

		log(
			String.format(
				"Progress: [%s] %d/%d -> %.2f%%", 
				progressBar.toString(), done, all, 
				(((float) done/ (float) all))*100
			)
		);
	}

	private void log(Object x) {
		/** A logging utility for debugging and additional information. Needs initialization beforehand */
		// System.out.println("[LOG]: " + x); // This logs in the terminal if you've run STAR-CCM through shell.
		simulation.println("[LOG]: " + x); // This logs in the output window in the program itself.
    }

	/**
	 * Prints a progress bar with $done hashtags and $(all - done) dashes. Needs
	 * initialization beforehand because of the implicit use of log();
	 */
	private void progressPP(String mode, int done, int all) {
		
		if (done < 0) done = 0;
		String repeatedHash = new String(new char[done]).replace("\0", "#");
		String repeatedDelta = new String(new char[((int) all)  - (done + 1)]).replace("\0", "-");

		log(String.format("{%s}: ", mode) + repeatedHash + repeatedDelta + String.format(" %d/%d Done.", done, (int) all - 1));
	}
}
