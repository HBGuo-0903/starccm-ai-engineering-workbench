// STAR-CCM+ macro: post_process.java
// Written by STAR-CCM+ 11.04.012
package macro;

import java.util.*;
import java.io.*;
import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class post_process_19v2_rad extends StarMacro {

	/** Version 19v2_rad */

	public void execute() {
		execute0();
	}

	private void execute0() {

		String name_path;
		String Name_Sim;
		String name_figure;
		String iterCpY;
		String iterCpYName;
		String iterTCpy;
		String iterSCpx;
		String iterVel;
		String iterTCpx;
		String iterVorX;
		String MainFolderName;
		String TCpZFolder;
		String TCpYFolder;
		String TCpXFolder;
		String SCpXFolder;
		String SCpYFolder;
		String SkinFolder;
		String SCpFolder;
		String SCpZFolder;
		String VelXFolder;
		String VelYFolder;
		String VelZFolder;
		String VorXFolder;
		String VectVelYFolder;
		String VectVelXFolder;
		String VectVelZFolder;

		String Path_Sim;

		/*
		 * -----------------------------------------------------------------------------
		 * --------------        CREATE FOLDER TO SAVE SIMULATIONS        --------------
		 * -----------------------------------------------------------------------------
		 * 
		 */

		Simulation simulation_0 = getActiveSimulation();

		Name_Sim = simulation_0.getPresentationName();
		Path_Sim = simulation_0.getSessionDir();

		MainFolderName = Path_Sim + "\\PostProcessing_" + Name_Sim;
		TCpZFolder = MainFolderName + "\\TCpZ";
		TCpYFolder = MainFolderName + "\\TCpY";
		TCpXFolder = MainFolderName + "\\TCpX";
		SCpXFolder = MainFolderName + "\\SCpX";
		SCpYFolder = MainFolderName + "\\SCpY";
		SCpFolder = MainFolderName + "\\SCp";
		SkinFolder = MainFolderName + "\\Skin";
		SCpZFolder = MainFolderName + "\\SCpZ";
		VelXFolder = MainFolderName + "\\VelX";
		VelYFolder = MainFolderName + "\\VelY";
		VelZFolder = MainFolderName + "\\VelZ";
		VorXFolder = MainFolderName + "\\VorX";
		VectVelYFolder = MainFolderName + "\\VectVelY";
		VectVelXFolder = MainFolderName + "\\VectVelX";
		VectVelZFolder = MainFolderName + "\\VectVelZ";

		new File(MainFolderName).mkdir();
		new File(TCpZFolder).mkdir();
		new File(TCpYFolder).mkdir();
		new File(TCpXFolder).mkdir();
		new File(SCpXFolder).mkdir();
		new File(SCpYFolder).mkdir();
		new File(SCpZFolder).mkdir();
		new File(VelXFolder).mkdir();
		new File(VelYFolder).mkdir();
		new File(VelZFolder).mkdir();
		new File(VorXFolder).mkdir();
		new File(VectVelYFolder).mkdir();
		new File(VectVelXFolder).mkdir();
		new File(VectVelZFolder).mkdir();

		PlaneSection planeSection_4 = (PlaneSection) simulation_0.getPartManager().createImplicitPart(
				new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] { 0.0, 0.0, 1.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 0.0 }), 0, 1, new DoubleVector(new double[] { 0.0 }));

		Coordinate coordinate_4 = planeSection_4.getOriginCoordinate();

		Units units_0 = ((Units) simulation_0.getUnitsManager().getObject("m"));

		coordinate_4.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 0.1, 0.0 }));

		Coordinate coordinate_5 = planeSection_4.getOrientationCoordinate();

		coordinate_5.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 1.0, 0.0 }));

		simulation_0.getSceneManager().createScalarScene("Scalar Scene", "Outline", "Scalar");

		Scene scene_3 = simulation_0.getSceneManager().getScene("Scalar Scene 1");

		scene_3.initializeAndWait();

		PartDisplayer partDisplayer_3 = ((PartDisplayer) scene_3.getDisplayerManager().getDisplayer("Outline 1"));

		partDisplayer_3.initialize();

		ScalarDisplayer scalarDisplayer_2 = ((ScalarDisplayer) scene_3.getDisplayerManager().getDisplayer("Scalar 1"));

		scalarDisplayer_2.initialize();

		scene_3.open(true);

		SceneUpdate sceneUpdate_2 = scene_3.getSceneUpdate();

		HardcopyProperties hardcopyProperties_2 = sceneUpdate_2.getHardcopyProperties();

		hardcopyProperties_2.setCurrentResolutionWidth(1271);

		hardcopyProperties_2.setCurrentResolutionHeight(587);

		scene_3.resetCamera();

		scene_3.getDisplayerManager().deleteDisplayers(new NeoObjectVector(new Object[] { partDisplayer_3 }));

		scalarDisplayer_2.getInputParts().setQuery(null);

		scalarDisplayer_2.getInputParts().setObjects(planeSection_4);

		PrimitiveFieldFunction primitiveFieldFunction_0 = ((PrimitiveFieldFunction) simulation_0
				.getFieldFunctionManager().getFunction("MeanStatic_CPMonitor"));

		PrimitiveFieldFunction primitiveFieldFunction_1 = ((PrimitiveFieldFunction) simulation_0
				.getFieldFunctionManager().getFunction("MeanTotal_CPMonitor"));

		PrimitiveFieldFunction primitiveFieldFunction_2 = ((PrimitiveFieldFunction) simulation_0
				.getFieldFunctionManager().getFunction("MeanVelocityMonitor"));

		PrimitiveFieldFunction primitiveFieldFunction_3 = ((PrimitiveFieldFunction) simulation_0
				.getFieldFunctionManager().getFunction("MeanVorticityMonitor"));

		PrimitiveFieldFunction primitiveFieldFunction_4 = ((PrimitiveFieldFunction) simulation_0
				.getFieldFunctionManager().getFunction("MeanSkinFrinctionMonitor"));

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_0);

		planeSection_4.getInputParts().setQuery(null);

		Region region_0 = simulation_0.getRegionManager().getRegion("Region 1");

		Boundary boundary_0 = region_0.getBoundaryManager().getBoundary("Belt");

		Boundary boundary_1 = region_0.getBoundaryManager().getBoundary("Chassis_R");

		Boundary boundary_2 = region_0.getBoundaryManager().getBoundary("Diffuser_R");

		Boundary boundary_3 = region_0.getBoundaryManager().getBoundary("Fan_Inlet_R");

		Boundary boundary_4 = region_0.getBoundaryManager().getBoundary("Fan_Outlet_R");

		Boundary boundary_5 = region_0.getBoundaryManager().getBoundary("Floor_R");

		Boundary boundary_6 = region_0.getBoundaryManager().getBoundary("FrontWing_R");

		// Boundary boundary_7 =
		// region_0.getBoundaryManager().getBoundary("Ground");

		Boundary boundary_8 = region_0.getBoundaryManager().getBoundary("Inlet");

		Boundary boundary_9 = region_0.getBoundaryManager().getBoundary("Outlet");

		Boundary boundary_10 = region_0.getBoundaryManager().getBoundary("Rad_Case_R");

		Boundary boundary_11 = region_0.getBoundaryManager().getBoundary("Rad_Inlet_R");

		Boundary boundary_12 = region_0.getBoundaryManager().getBoundary("Rad_Outlet_R");

		Boundary boundary_13 = region_0.getBoundaryManager().getBoundary("RearWing_R");

		InterfaceBoundary interfaceBoundary_0 = ((InterfaceBoundary) region_0.getBoundaryManager()
				.getBoundary("Rad_Inlet_R [0]"));

		InterfaceBoundary interfaceBoundary_1 = ((InterfaceBoundary) region_0.getBoundaryManager()
				.getBoundary("Rad_Outlet_R [0]"));

		InterfaceBoundary interfaceBoundary_2 = ((InterfaceBoundary) region_0.getBoundaryManager()
				.getBoundary("Fan_Outlet_R [0]"));

		InterfaceBoundary interfaceBoundary_3 = ((InterfaceBoundary) region_0.getBoundaryManager()
				.getBoundary("Fan_Inlet_R [0]"));

		Boundary boundary_14 = region_0.getBoundaryManager().getBoundary("Right");

		Boundary boundary_15 = region_0.getBoundaryManager().getBoundary("Susp_FR");

		Boundary boundary_16 = region_0.getBoundaryManager().getBoundary("Susp_RR");

		Boundary boundary_17 = region_0.getBoundaryManager().getBoundary("Symmetry");

		Boundary boundary_18 = region_0.getBoundaryManager().getBoundary("Top");

		Boundary boundary_19 = region_0.getBoundaryManager().getBoundary("Wheel_FR");

		Boundary boundary_20 = region_0.getBoundaryManager().getBoundary("Wheel_RR");

		Region region_1 = simulation_0.getRegionManager().getRegion("Region 2");

		Region region_2 = simulation_0.getRegionManager().getRegion("Region 3");

		Boundary boundary_21 = region_1.getBoundaryManager().getBoundary("Rad_Inlet_R");

		Boundary boundary_22 = region_1.getBoundaryManager().getBoundary("Rad_Outlet_R");

		Boundary boundary_23 = region_1.getBoundaryManager().getBoundary("Rad_Side_R");

		InterfaceBoundary interfaceBoundary_4 = ((InterfaceBoundary) region_1.getBoundaryManager()
				.getBoundary("Rad_Inlet_R [1]"));

		InterfaceBoundary interfaceBoundary_5 = ((InterfaceBoundary) region_1.getBoundaryManager()
				.getBoundary("Rad_Outlet_R [1]"));

		Boundary boundary_24 = region_2.getBoundaryManager().getBoundary("Fan_Inlet_R");

		Boundary boundary_25 = region_2.getBoundaryManager().getBoundary("Fan_Outlet_R");

		Boundary boundary_26 = region_2.getBoundaryManager().getBoundary("Fan_Side_R");

		InterfaceBoundary interfaceBoundary_6 = ((InterfaceBoundary) region_2.getBoundaryManager()
				.getBoundary("Fan_Outlet_R [1]"));

		InterfaceBoundary interfaceBoundary_7 = ((InterfaceBoundary) region_2.getBoundaryManager()
				.getBoundary("Fan_Inlet_R [1]"));

		planeSection_4.getInputParts().setObjects(region_0, boundary_0, boundary_1, boundary_2, boundary_3, boundary_4,
				boundary_5, boundary_6,
				// boundary_7,
				boundary_8, boundary_9, boundary_10, boundary_11, boundary_12, boundary_13, interfaceBoundary_0,
				interfaceBoundary_1, interfaceBoundary_2, interfaceBoundary_3, boundary_14, boundary_15, boundary_16,
				boundary_17, boundary_18, boundary_19, boundary_20, region_1, boundary_21, boundary_22, boundary_23,
				interfaceBoundary_4, interfaceBoundary_5, region_2, boundary_24, boundary_25, boundary_26,
				interfaceBoundary_6, interfaceBoundary_7);

		CurrentView currentView_2 = scene_3.getCurrentView();

		scene_3.setViewOrientation(new DoubleVector(new double[] { 0.0, -1.0, 0.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));

		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { -3.0, 1.0 }));

		scalarDisplayer_2.getScalarDisplayQuantity().setClip(0);

		SimpleAnnotation simpleAnnotation_1 = simulation_0.getAnnotationManager().createSimpleAnnotation();

		simpleAnnotation_1.setPresentationName("Figure_name");

		simpleAnnotation_1.setDefaultHeight(0.03);

		FixedAspectAnnotationProp fixedAspectAnnotationProp_1 = (FixedAspectAnnotationProp) scene_3
				.getAnnotationPropManager().createPropForAnnotation(simpleAnnotation_1);

		for (int iterY = 0; iterY < 17; iterY++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0001 + 0.05 * iterY, 0.0 }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { 0.6, 0.0001 + 0.05 * iterY, 0.5 }),
					new DoubleVector(new double[] { 0.6, -50 + 0.05 * iterY, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterCpY = String.valueOf(iterY);

			name_figure = Name_Sim + " " + "y=" + String.format("%.2f", 0.05 * iterY) + "m";

			simpleAnnotation_1.setText(name_figure);

			name_path = SCpYFolder + "\\SCpY_" + iterCpY + ".png";

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_2);

		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { 0, 20.0 }));

		for (int iterY = 0; iterY < 17; iterY++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0001 + 0.05 * iterY, 0.0 }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { 0.6, 0.0001 + 0.05 * iterY, 0.5 }),
					new DoubleVector(new double[] { 0.6, -50 + 0.05 * iterY, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterVel = String.valueOf(iterY);

			name_figure = Name_Sim + " " + "y=" + String.format("%.2f", 0.05 * iterY) + "m";

			simpleAnnotation_1.setText(name_figure);

			name_path = VelYFolder + "\\VelY_" + iterVel + ".png";

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_1);

		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { -0.1, 1.0 }));

		for (int iterY = 0; iterY < 17; iterY++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0001 + 0.05 * iterY, 0.0 }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { 0.6, 0.0001 + 0.05 * iterY, 0.5 }),
					new DoubleVector(new double[] { 0.6, -50 + 0.05 * iterY, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpy = String.valueOf(iterY);

			name_figure = Name_Sim + " " + "y=" + String.format("%.2f", 0.05 * iterY) + "m";

			name_path = TCpYFolder + "\\TCpY_" + iterTCpy + ".png";

			simpleAnnotation_1.setText(name_figure);

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		SymmetricRepeat symmetricRepeat_0 = ((SymmetricRepeat) simulation_0.getTransformManager()
				.getObject("Symmetry 1"));

		scalarDisplayer_2.setVisTransform(symmetricRepeat_0);

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_1);

		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { -0.1, 1.0 }));

		coordinate_5.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 1.0, 0.0, 0.0 }));

		for (int iterX = -1; iterX < 0; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterX);

			name_figure = Name_Sim + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";

			name_path = TCpXFolder + "\\TCpX__" + iterTCpx + ".png";

			simpleAnnotation_1.setText(name_figure);
			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		for (int iterX = 0; iterX < 60; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterX);

			name_figure = Name_Sim + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";

			name_path = TCpXFolder + "\\TCpX_" + iterTCpx + ".png";

			simpleAnnotation_1.setText(name_figure);
			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_0);

		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { -3, 1 }));

		for (int iterX = -1; iterX < 0; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterSCpx = String.valueOf(iterX);

			name_figure = Name_Sim + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";

			name_path = SCpXFolder + "\\SCpX__" + iterSCpx + ".png";

			simpleAnnotation_1.setText(name_figure);

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		for (int iterX = 0; iterX < 60; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterSCpx = String.valueOf(iterX);

			name_figure = Name_Sim + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";

			name_path = SCpXFolder + "\\SCpX_" + iterSCpx + ".png";

			simpleAnnotation_1.setText(name_figure);

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_2);

		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { 0, 20 }));

		for (int iterX = -1; iterX < 0; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterSCpx = String.valueOf(iterX);

			name_figure = Name_Sim + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";

			name_path = VelXFolder + "\\VelX__" + iterSCpx + ".png";

			simpleAnnotation_1.setText(name_figure);

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		for (int iterX = 0; iterX < 60; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterSCpx = String.valueOf(iterX);

			name_figure = Name_Sim + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";

			name_path = VelXFolder + "\\VelX_" + iterSCpx + ".png";

			simpleAnnotation_1.setText(name_figure);

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_3);

		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { 0, 1000 }));

		for (int iterX = -1; iterX < 0; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterVorX = String.valueOf(iterX);

			name_figure = Name_Sim + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";

			name_path = VorXFolder + "\\VorX__" + iterVorX + ".png";

			simpleAnnotation_1.setText(name_figure);

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		for (int iterX = 0; iterX < 60; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterVorX = String.valueOf(iterX);

			name_path = VorXFolder + "\\VorX_" + iterVorX + ".png";

			name_figure = Name_Sim + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";

			simpleAnnotation_1.setText(name_figure);

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_1);

		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { -0.1, 1.0 }));

		coordinate_5.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 0.0, 0.1 }));

		for (int iterZ = 0; iterZ < 6; iterZ++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.01 * iterZ }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.01 * iterZ }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterZ);

			name_path = TCpZFolder + "\\TCpZ_" + iterTCpx + ".png";

			name_figure = Name_Sim + " " + "z=" + String.format("%.3f", 0.01 * iterZ) + "m";

			simpleAnnotation_1.setText(name_figure);

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		for (int iterZ = 6; iterZ < 27; iterZ++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.05 * (iterZ - 4) }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.05 * (iterZ - 4) }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterZ);

			name_path = TCpZFolder + "\\TCpZ_" + iterTCpx + ".png";

			name_figure = Name_Sim + " " + "z=" + String.format("%.3f", 0.05 * (iterZ - 4)) + "m";

			simpleAnnotation_1.setText(name_figure);

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_0);

		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { -3.0, 1.0 }));

		for (int iterZ = 0; iterZ < 6; iterZ++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.01 * iterZ }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.01 * iterZ }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterZ);

			name_path = SCpZFolder + "\\SCpZ_" + iterTCpx + ".png";

			name_figure = Name_Sim + " " + "z=" + String.format("%.3f", 0.01 * iterZ) + "m";

			simpleAnnotation_1.setText(name_figure);

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		for (int iterZ = 6; iterZ < 27; iterZ++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.05 * (iterZ - 4) }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.05 * (iterZ - 4) }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterZ);

			name_path = SCpZFolder + "\\SCpZ_" + iterTCpx + ".png";

			name_figure = Name_Sim + " " + "z=" + String.format("%.3f", 0.05 * (iterZ - 4)) + "m";

			simpleAnnotation_1.setText(name_figure);

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_2);

		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { 0, 20.0 }));

		for (int iterZ = 0; iterZ < 6; iterZ++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.01 * iterZ }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.01 * iterZ }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterZ);

			name_path = VelZFolder + "\\VelZ_" + iterTCpx + ".png";

			name_figure = Name_Sim + " " + "z=" + String.format("%.3f", 0.01 * iterZ) + "m";

			simpleAnnotation_1.setText(name_figure);

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		for (int iterZ = 6; iterZ < 27; iterZ++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.05 * (iterZ - 4) }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.05 * (iterZ - 4) }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterZ);

			name_path = VelZFolder + "\\VelZ_" + iterTCpx + ".png";

			name_figure = Name_Sim + " " + "z=" + String.format("%.3f", 0.05 * (iterZ - 4)) + "m";

			simpleAnnotation_1.setText(name_figure);

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_0);

		scalarDisplayer_2.getInputParts().setQuery(null);

		scalarDisplayer_2.getInputParts().setObjects(boundary_1, boundary_2, boundary_5, boundary_6, boundary_13,
				boundary_15, boundary_16, boundary_19, boundary_20);

		scene_3.setViewOrientation(new DoubleVector(new double[] { 0.0, -1.0, 0.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));

		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { -3.0, 1.0 }));

		scalarDisplayer_2.getScalarDisplayQuantity().setClip(0);

		coordinate_4.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 0.0001, 0.0 }));

		scene_3.setMeshOverrideMode(0);

		currentView_2.setInput(new DoubleVector(new double[] { 0.6, 0.0001, 0.5 }),
				new DoubleVector(new double[] { 0.6, -50, 1.0 }), new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1,
				1);

		name_figure = Name_Sim + " " + " " + " Side";

		simpleAnnotation_1.setText(name_figure);

		name_path = SCpFolder + "\\SCp_" + "Side" + ".png";

		scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		coordinate_4.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 0.0, 0.0001 }));

		scene_3.setMeshOverrideMode(0);

		currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 }),
				new DoubleVector(new double[] { 0.6, -0.1, 10.0 }), new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1,
				1);

		name_path = SCpFolder + "\\SCp_" + "Top" + ".png";

		name_figure = Name_Sim + " " + "Top";

		simpleAnnotation_1.setText(name_figure);

		scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		scene_3.setViewOrientation(new DoubleVector(new double[] { 0.0, 0.0, 1.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));

		name_path = SCpFolder + "\\SCp_" + "Bottom" + ".png";

		name_figure = Name_Sim + " Bottom";

		simpleAnnotation_1.setText(name_figure);

		scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		coordinate_4.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { -0.85, 0.0, 0.0 }));

		scene_3.setMeshOverrideMode(0);

		currentView_2.setInput(new DoubleVector(new double[] { -0.85, 0.0, 0.5 }),
				new DoubleVector(new double[] { -40 - 0.8, 0.0, 1.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

		name_path = SCpFolder + "\\SCp_" + "Front" + ".png";

		name_figure = Name_Sim + " Front";

		simpleAnnotation_1.setText(name_figure);

		scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		scene_3.setViewOrientation(new DoubleVector(new double[] { -1.0, 0.0, 0.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));

		name_path = SCpFolder + "\\SCp_" + "Rear" + ".png";

		name_figure = Name_Sim + " Rear";

		simpleAnnotation_1.setText(name_figure);

		scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_4);

		scene_3.setViewOrientation(new DoubleVector(new double[] { 0.0, -1.0, 0.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));

		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { 0.0, 0.05 }));

		scalarDisplayer_2.getScalarDisplayQuantity().setClip(0);

		coordinate_4.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 0.0001, 0.0 }));

		scene_3.setMeshOverrideMode(0);

		currentView_2.setInput(new DoubleVector(new double[] { 0.6, 0.0001, 0.5 }),
				new DoubleVector(new double[] { 0.6, -50, 1.0 }), new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1,
				1);

		name_figure = Name_Sim + " " + " Side";

		simpleAnnotation_1.setText(name_figure);

		name_path = SkinFolder + "\\Skin_" + "Side" + ".png";

		scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		coordinate_4.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 0.0, 0.0001 }));

		scene_3.setMeshOverrideMode(0);

		currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 }),
				new DoubleVector(new double[] { 0.6, -0.1, 10.0 }), new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1,
				1);

		name_path = SkinFolder + "\\Skin_" + "Top" + ".png";

		name_figure = Name_Sim + " " + "Top";

		simpleAnnotation_1.setText(name_figure);

		scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		scene_3.setViewOrientation(new DoubleVector(new double[] { 0.0, 0.0, 1.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));

		name_path = SkinFolder + "\\Skin_" + "Bottom" + ".png";

		name_figure = Name_Sim + " Bottom";

		simpleAnnotation_1.setText(name_figure);

		scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		coordinate_4.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { -0.85, 0.0, 0.0 }));

		scene_3.setMeshOverrideMode(0);

		currentView_2.setInput(new DoubleVector(new double[] { -0.85, 0.0, 0.5 }),
				new DoubleVector(new double[] { -40 - 0.8, 0.0, 1.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

		name_path = SkinFolder + "\\Skin_" + "Front" + ".png";

		name_figure = Name_Sim + " Front";

		simpleAnnotation_1.setText(name_figure);

		scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		scene_3.setViewOrientation(new DoubleVector(new double[] { 1.0, 0.0, 0.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));

		name_path = SkinFolder + "\\Skin_" + "Rear" + ".png";

		name_figure = Name_Sim + " Rear";

		simpleAnnotation_1.setText(name_figure);

		scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		simulation_0.getSceneManager().createVectorScene("Vector Scene", "Outline", "Vector");

		Scene scene_4 = simulation_0.getSceneManager().getScene("Vector Scene 1");

		scene_4.initializeAndWait();

		PartDisplayer partDisplayer_4 = ((PartDisplayer) scene_4.getDisplayerManager().getDisplayer("Outline 1"));

		VectorDisplayer vectorDisplayer_4 = ((VectorDisplayer) scene_4.getDisplayerManager().getDisplayer("Vector 1"));

		vectorDisplayer_4.initialize();

		scene_4.open(true);

		vectorDisplayer_4.setDisplayMode(1);

		vectorDisplayer_4.getInputParts().setQuery(null);

		vectorDisplayer_4.getInputParts().setObjects(planeSection_4);

		vectorDisplayer_4.getVectorDisplayQuantity().setClip(0);

		UserFieldFunction userFieldFunction_4 = ((UserFieldFunction) simulation_0.getFieldFunctionManager()
				.getFunction("Mean of Velocity"));

		vectorDisplayer_4.getVectorDisplayQuantity().setFieldFunction(userFieldFunction_4);

		vectorDisplayer_4.getVectorDisplayQuantity().setRange(new DoubleVector(new double[] { 0, 20.0 }));

		scene_4.getDisplayerManager().deleteDisplayers(new NeoObjectVector(new Object[] { partDisplayer_4 }));

		CurrentView currentView_3 = scene_4.getCurrentView();

		coordinate_5.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0, 0, 1 }));

		SimpleAnnotation simpleAnnotation_2 = simulation_0.getAnnotationManager().createSimpleAnnotation();

		simpleAnnotation_2.setPresentationName("Figure_name");

		simpleAnnotation_2.setDefaultHeight(0.03);

		FixedAspectAnnotationProp fixedAspectAnnotationProp_2 = (FixedAspectAnnotationProp) scene_4
				.getAnnotationPropManager().createPropForAnnotation(simpleAnnotation_2);

		vectorDisplayer_4.getVectorDisplayQuantity().setFieldFunction(userFieldFunction_4);

		vectorDisplayer_4.getVectorDisplayQuantity().setRange(new DoubleVector(new double[] { 0, 20.0 }));

		coordinate_5.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 1.0, 0.0 }));

		for (int iterY = 0; iterY < 17; iterY++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0001 + 0.05 * iterY, 0.0 }));

			scene_4.setMeshOverrideMode(0);

			currentView_3.setInput(new DoubleVector(new double[] { 0.6, 0.0001 + 0.05 * iterY, 0.5 }),
					new DoubleVector(new double[] { 0.6, -50 + 0.05 * iterY, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterVel = String.valueOf(iterY);

			name_figure = Name_Sim + " " + "y=" + String.format("%.2f", 0.05 * iterY) + "m";

			simpleAnnotation_2.setText(name_figure);

			name_path = VectVelYFolder + "\\VectVelY_" + iterVel + ".jpg";

			scene_4.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		vectorDisplayer_4.setVisTransform(symmetricRepeat_0);

		coordinate_5.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0, 0, 1 }));

		for (int iterZ = 0; iterZ < 6; iterZ++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.01 * iterZ }));

			scene_4.setMeshOverrideMode(0);

			currentView_3.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.01 * iterZ }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterZ);

			name_path = VectVelZFolder + "\\VectVelZ_" + iterTCpx + ".jpg";

			name_figure = Name_Sim + " " + "z=" + String.format("%.3f", 0.01 * iterZ) + "m";

			simpleAnnotation_2.setText(name_figure);

			scene_4.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		for (int iterZ = 6; iterZ < 27; iterZ++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.05 * (iterZ - 4) }));

			scene_4.setMeshOverrideMode(0);

			currentView_3.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.05 * (iterZ - 4) }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterZ);

			name_path = VectVelZFolder + "\\VectVelZ_" + iterTCpx + ".jpg";

			name_figure = Name_Sim + " " + "z=" + String.format("%.3f", 0.05 * (iterZ - 4)) + "m";

			simpleAnnotation_2.setText(name_figure);

			scene_4.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		vectorDisplayer_4.setVisTransform(symmetricRepeat_0);

		vectorDisplayer_4.getVectorDisplayQuantity().setFieldFunction(userFieldFunction_4);

		vectorDisplayer_4.getVectorDisplayQuantity().setRange(new DoubleVector(new double[] { 0, 20.0 }));

		coordinate_5.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 1.0, 0.0, 0.0 }));

		for (int iterX = -1; iterX < 0; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));

			scene_4.setMeshOverrideMode(0);

			currentView_3.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterVel = String.valueOf(iterX);

			name_figure = Name_Sim + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";

			name_path = VectVelXFolder + "\\VectVelX__" + iterVel + ".jpg";

			simpleAnnotation_2.setText(name_figure);
			scene_4.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		for (int iterX = 0; iterX < 60; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));

			scene_4.setMeshOverrideMode(0);

			currentView_3.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterVel = String.valueOf(iterX);

			name_figure = Name_Sim + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";

			name_path = VectVelXFolder + "\\VectVelX_" + iterVel + ".jpg";

			simpleAnnotation_2.setText(name_figure);
			scene_4.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

	}
}