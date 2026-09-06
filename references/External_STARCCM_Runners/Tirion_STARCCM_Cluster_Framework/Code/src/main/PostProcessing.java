package macro;

import java.io.*;
import java.util.*;
import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class PostProcessing extends StarMacro {

	private Simulation simulation;

	private int clipping;
	private DoubleVector scaleSCp;
	private DoubleVector scaleTCp;
	private DoubleVector scaleVel;
	private DoubleVector scaleVor;

	private double sliceStep;
	private HashMap<String, DoubleVector> ranges;

	public void execute() {
		simulation = getActiveSimulation();
		try {
			run();
		} catch(Exception e) {
			log(e);
		}
	}

	private void run() throws Exception {
        log("Post Processing...");

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

		// simulation = getActiveSimulation();

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
			
			progress("Y-Axis", iterY, (int) iterateUntilY);

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
			progress("X-Axis", iterX, (int) iterateUntilX);

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
			progress("Z-Axis", iterZ, 27);

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
			progress("Z-Axis", iterZ, 27);

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
			progress("Z-Axis", iterZ, 27);

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
			progress("Z-Axis", iterZ, 27);

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
			progress("Z-Axis", iterZ, 27);

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
			progress("Z-Axis", iterZ, 27);

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


		// TODO Fix Skin friction coefficient
		// Scales toggles
		// scalarDisplayer.getScalarDisplayQuantity().setFieldFunction(meanStaticCPMonitorFieldFunction
		// );
		// scalarDisplayer.getInputParts().setQuery(null);

		// scalarDisplayer.getInputParts().setObjects(planeSection);

		// scalarScene.setViewOrientation(new DoubleVector(new double[] { 0.0, -1.0, 0.0
		// }),
		// new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));
		// scalarDisplayer.getScalarDisplayQuantity().setRange(new DoubleVector(new
		// double[] { -3.0, 1.0 }));
		// scalarDisplayer.getScalarDisplayQuantity().setClip(clipping);

		// coordinate.setCoordinate(units, units, units, new DoubleVector(new double[] {
		// 0.0, 0.0001, 0.0 }));
		// scalarScene.setMeshOverrideMode(0);
		// currentView.setInput(new DoubleVector(new double[] { 0.6, 0.0001, 0.5 }),
		// new DoubleVector(new double[] { 0.6, -50, 1.0 }), new DoubleVector(new
		// double[] { 0.0, 0.0, 1.0 }), 1,
		// 1);

		// figName = simName + " " + " " + " Side";
		// simpleAnnotation.setText(figName);
		// namePath = SCpFolder + "/SCp_" + "Side" + ".png";
		// scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		// coordinate.setCoordinate(units, units, units, new DoubleVector(new double[] {
		// 0.0, 0.0, 0.0001 }));
		// scalarScene.setMeshOverrideMode(0);

		// currentView.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 }),
		// new DoubleVector(new double[] { 0.6, -0.1, 10.0 }), new DoubleVector(new
		// double[] { 0.0, 0.0, 1.0 }), 1,
		// 1);

		// namePath = SCpFolder + "/SCp_" + "Top" + ".png";
		// figName = simName + " " + "Top";
		// simpleAnnotation.setText(figName);

		// scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		// scalarScene.setViewOrientation(new DoubleVector(new double[] { 0.0, 0.0, 1.0
		// }),
		// new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));
		// namePath = SCpFolder + "/SCp_" + "Bottom" + ".png";
		// figName = simName + " Bottom";

		// simpleAnnotation.setText(figName);
		// scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		// coordinate.setCoordinate(units, units, units, new DoubleVector(new double[] {
		// -0.85, 0.0, 0.0 }));
		// scalarScene.setMeshOverrideMode(0);
		// currentView.setInput(new DoubleVector(new double[] { -0.85, 0.0, 0.5 }),
		// new DoubleVector(new double[] { -40 - 0.8, 0.0, 1.0 }),
		// new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

		// namePath = SCpFolder + "/SCp_" + "Front" + ".png";
		// figName = simName + " Front";
		// simpleAnnotation.setText(figName);

		// scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		// scalarScene.setViewOrientation(new DoubleVector(new double[] { -1.0, 0.0, 0.0
		// }),
		// new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));
		// namePath = SCpFolder + "/SCp_" + "Rear" + ".png";
		// figName = simName + " Rear";
		// simpleAnnotation.setText(figName);

		// scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		// scalarDisplayer.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_4);
		// scalarScene.setViewOrientation(new DoubleVector(new double[] { 0.0, -1.0, 0.0
		// }),
		// new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));
		// scalarDisplayer.getScalarDisplayQuantity().setRange(new DoubleVector(new
		// double[] { 0.0, 0.05 }));
		// scalarDisplayer.getScalarDisplayQuantity().setClip(clipping);
		// coordinate.setCoordinate(units, units, units, new DoubleVector(new double[] {
		// 0.0, 0.0001, 0.0 }));
		// scalarScene.setMeshOverrideMode(0);

		// currentView.setInput(new DoubleVector(new double[] { 0.6, 0.0001, 0.5 }),
		// new DoubleVector(new double[] { 0.6, -50, 1.0 }), new DoubleVector(new
		// double[] { 0.0, 0.0, 1.0 }), 1,
		// 1);

		// figName = simName + " " + " Side";
		// simpleAnnotation.setText(figName);
		// namePath = SkinFolder + "/Skin_" + "Side" + ".png";

		// scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		// coordinate.setCoordinate(units, units, units, new DoubleVector(new double[] {
		// 0.0, 0.0, 0.0001 }));
		// scalarScene.setMeshOverrideMode(0);
		// currentView.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 }),
		// new DoubleVector(new double[] { 0.6, -0.1, 10.0 }), new DoubleVector(new
		// double[] { 0.0, 0.0, 1.0 }), 1,
		// 1);

		// namePath = SkinFolder + "/Skin_" + "Top" + ".png";
		// figName = simName + " " + "Top";
		// simpleAnnotation.setText(figName);

		// scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		// scalarScene.setViewOrientation(new DoubleVector(new double[] { 0.0, 0.0, 1.0
		// }),
		// new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));

		// namePath = SkinFolder + "/Skin_" + "Bottom" + ".png";
		// figName = simName + " Bottom";
		// simpleAnnotation.setText(figName);

		// scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		// coordinate.setCoordinate(units, units, units, new DoubleVector(new double[] {
		// -0.85, 0.0, 0.0 }));
		// scalarScene.setMeshOverrideMode(0);
		// currentView.setInput(new DoubleVector(new double[] { -0.85, 0.0, 0.5 }),
		// new DoubleVector(new double[] { -40 - 0.8, 0.0, 1.0 }),
		// new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

		// namePath = SkinFolder + "/Skin_" + "Front" + ".png";
		// figName = simName + " Front";
		// simpleAnnotation.setText(figName);

		// scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		// scalarScene.setViewOrientation(new DoubleVector(new double[] { 1.0, 0.0, 0.0
		// }),
		// new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));

		// namePath = SkinFolder + "/Skin_" + "Rear" + ".png";
		// figName = simName + " Rear";
		// simpleAnnotation.setText(figName);

		// scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		// log("Calculating Vector Velocities.");

		// simulation.getSceneManager().createVectorScene("Vector Scene", "Outline", "Vector");
		// Scene vectorScene = simulation.getSceneManager().getScene("Vector Scene 1");
		// vectorScene.initializeAndWait();

		// PartDisplayer vectorPartDisplayer = ((PartDisplayer) vectorScene.getDisplayerManager()
		// 		.getDisplayer("Outline 1"));
		// VectorDisplayer vectorDisplayer = ((VectorDisplayer) vectorScene.getDisplayerManager()
		// 		.getDisplayer("Vector 1"));
		// vectorDisplayer.initialize();

		// vectorScene.open(true);

		// vectorDisplayer.setDisplayMode(1);
		// vectorDisplayer.getInputParts().setQuery(null);
		// vectorDisplayer.getInputParts().setObjects(planeSection);
		// vectorDisplayer.getVectorDisplayQuantity().setClip(clipping);

		// UserFieldFunction userFieldFunction = ((UserFieldFunction) simulation.getFieldFunctionManager()
		// 		.getFunction("Mean of Velocity"));

		// vectorDisplayer.getVectorDisplayQuantity().setFieldFunction(userFieldFunction);
		// vectorDisplayer.getVectorDisplayQuantity().setRange(scaleVel);
		// vectorScene.getDisplayerManager().deleteDisplayers(new NeoObjectVector(new Object[] { vectorPartDisplayer }));

		// CurrentView currentVectorView = vectorScene.getCurrentView();
		// coordinate_1.setCoordinate(units, units, units, new DoubleVector(new double[] { 0, 0, 1 }));

		// SimpleAnnotation otherAnnotation = simulation.getAnnotationManager().createSimpleAnnotation();
		// otherAnnotation.setPresentationName("Figure_name");
		// otherAnnotation.setDefaultHeight(0.03);

		// FixedAspectAnnotationProp fixedAspectAnnotationProp_2 = (FixedAspectAnnotationProp) vectorScene
		// 		.getAnnotationPropManager().createPropForAnnotation(otherAnnotation);

		// vectorDisplayer.getVectorDisplayQuantity().setFieldFunction(userFieldFunction);
		// vectorDisplayer.getVectorDisplayQuantity().setRange(scaleVel);
		// coordinate_1.setCoordinate(units, units, units, new DoubleVector(new double[] { 0.0, 1.0, 0.0 }));

		// for (int iterY = 0; iterY < 17; iterY++) {
		// 	progress("Y-Axis", (iterY), 17);
			
		// 	coordinate.setCoordinate(units, units, units,
		// 			new DoubleVector(new double[] { 0.0, 0.0001 + sliceStep * iterY, 0.0 }));
		// 	vectorScene.setMeshOverrideMode(0);
		// 	currentVectorView.setInput(new DoubleVector(new double[] { 0.6, 0.0001 + sliceStep * iterY, 0.5 }),
		// 			new DoubleVector(new double[] { 0.6, -50 + sliceStep * iterY, 1.0 }),
		// 			new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

		// 	iterVel = (iterY >= 10) ? String.valueOf(iterY) : "0" + String.valueOf(iterY);
		// 	figName = simName + " " + "y=" + String.format("%.2f", sliceStep * iterY) + "m";
		// 	otherAnnotation.setText(figName);
		// 	namePath = VectVelYFolder + "/VectVelY_" + iterVel + ".png";
			
		// 	// log(resolvePath(namePath));
		// 	vectorScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		// }

		// vectorDisplayer.setVisTransform(symmetricRepeat);
		// coordinate_1.setCoordinate(units, units, units, new DoubleVector(new double[] { 0, 0, 1 }));

		// for (int iterZ = 0; iterZ < 6; iterZ++) {
		// 	progress("Z-Axis", iterZ, 27);

		// 	coordinate.setCoordinate(units, units, units,
		// 			new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.01 * iterZ }));
		// 	vectorScene.setMeshOverrideMode(0);

		// 	currentVectorView.setInput(new DoubleVector(
		// 		new double[] { 0.6, -0.1, 0.0001 + 0.01 * iterZ }),
		// 		new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
		// 		new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 
		// 		1, 1
		// 	);
		// 	iterVel = (iterZ >= 10) ? String.valueOf(iterZ) : "0" + String.valueOf(iterZ);

		// 	namePath = VectVelZFolder + "/VectVelZ_" + iterVel + ".jpg";
		// 	figName = simName + " " + "z=" + String.format("%.3f", 0.01 * iterZ) + "m";

		// 	otherAnnotation.setText(figName);
		// 	vectorScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		// }

		// for (int iterZ = 6; iterZ < 27; iterZ++) {
		// 	progress("Z-Axis", iterZ, 27);
			
		// 	int something = iterZ - 4;
		// 	coordinate.setCoordinate(units, units, units, 
		// 			new DoubleVector(new double[] { 
		// 				0.0, 0.0, 
		// 				0.0001 + sliceStep * something }));
		// 	vectorScene.setMeshOverrideMode(0);
			
		// 	currentVectorView.setInput(
		// 		new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + sliceStep * something }),
		// 		new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
		// 		new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 
		// 		1, 1
		// 	);

		// 	iterVel = (iterZ >= 10) ? String.valueOf(iterZ) : "0" + String.valueOf(iterZ);
		// 	namePath = VectVelZFolder + "/VectVelZ_" + iterVel + ".jpg";
		// 	figName = simName + " " + "z=" + String.format("%.3f", sliceStep * (iterZ - 4)) + "m";
		// 	otherAnnotation.setText(figName);
			
		// 	vectorScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		// }
		
		// vectorDisplayer.setVisTransform(symmetricRepeat);
		// vectorDisplayer.getVectorDisplayQuantity().setFieldFunction(userFieldFunction);
		// vectorDisplayer.getVectorDisplayQuantity().setRange(scaleVel);
		// // Set the coordinate to the x-axis
		// coordinate_1.setCoordinate(units, units, units, new DoubleVector(new double[] { 1.0, 0.0, 0.0 }));
		
		// for (int iterX = -1; iterX < 60; iterX++) {
		// 	progress("X-Axis", iterX, 60);
			
		// 	coordinate.setCoordinate(units, units, units,
		// 			new DoubleVector(new double[] { -0.85 + sliceStep * iterX, 0.0, 0.0 }));
		// 	vectorScene.setMeshOverrideMode(0);
		// 	currentVectorView.setInput(new DoubleVector(new double[] { -0.85 + sliceStep * iterX, 0.0, 0.5 }),
		// 			new DoubleVector(new double[] { -40 - 0.8 + sliceStep * iterX, 0.0, 1.0 }),
		// 			new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

		// 	iterVel = String.valueOf(iterX);
		// 	figName = simName + " " + "x=" + String.format("%.2f", sliceStep * iterX) + "m";
		// 	namePath = VectVelXFolder + "/VectVelX_" + iterVel + ".jpg";
		// 	otherAnnotation.setText(figName);

		// 	vectorScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		// }

		log("Finished.");
	}

	/**
	 * A logging utility for debugging and additional information. Needs
	 * initialization beforehand
	 */
	private void log(Object x) {
		// System.out.println("[LOG]: " + x); 	// This logs only in the terminal if you've run STAR-CCM through shell.
		simulation.println("[LOG]: " + x); 		// This logs in the output window in the program itself and the terminal.
	}

	/**
	 * Prints a progress bar with $done hashtags and $(all - done) dashes. Needs
	 * initialization beforehand because of the implicit use of log();
	 */
	private void progress(String mode, int done, int all) {
		
		if (done < 0) done = 0;
		String repeatedHash = new String(new char[done]).replace("\0", "#");
		String repeatedDelta = new String(new char[((int) all)  - (done + 1)]).replace("\0", "-");

		log(String.format("{%s}: ", mode) + repeatedHash + repeatedDelta + String.format(" %d/%d Done.", done, (int) all - 1));
	}
}