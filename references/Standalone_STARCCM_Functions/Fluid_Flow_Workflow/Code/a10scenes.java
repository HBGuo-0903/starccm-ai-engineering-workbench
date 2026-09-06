// Simcenter STAR-CCM+ macro: a10scenes.java
// Written by Simcenter STAR-CCM+ 19.02.009
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;
import star.meshing.*;

public class a10scenes extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    simulation_0.getSceneManager().createScalarScene("Scalar Scene", "Outline", "Scalar", null);

    Scene scene_16 = 
      simulation_0.getSceneManager().getScene("Scalar Scene 2");

    scene_16.initializeAndWait();

    ScalarDisplayer scalarDisplayer_1 = 
      ((ScalarDisplayer) scene_16.getDisplayerManager().getObject("Scalar 1"));

    Legend legend_1 = 
      scalarDisplayer_1.getLegend();

    PredefinedLookupTable predefinedLookupTable_0 = 
      ((PredefinedLookupTable) simulation_0.get(LookupTableManager.class).getObject("blue-yellow-red"));

    legend_1.setLookupTable(predefinedLookupTable_0);

    SceneUpdate sceneUpdate_15 = 
      scene_16.getSceneUpdate();

    HardcopyProperties hardcopyProperties_15 = 
      sceneUpdate_15.getHardcopyProperties();

    hardcopyProperties_15.setCurrentResolutionWidth(25);

    hardcopyProperties_15.setCurrentResolutionHeight(25);

    Scene scene_15 = 
      simulation_0.getSceneManager().getScene("Geometry Scene 1");

    SceneUpdate sceneUpdate_14 = 
      scene_15.getSceneUpdate();

    HardcopyProperties hardcopyProperties_14 = 
      sceneUpdate_14.getHardcopyProperties();

    hardcopyProperties_14.setCurrentResolutionWidth(1330);

    hardcopyProperties_14.setCurrentResolutionHeight(667);

    hardcopyProperties_15.setCurrentResolutionWidth(1328);

    hardcopyProperties_15.setCurrentResolutionHeight(666);

    scene_16.resetCamera();

    simulation_0.getSceneManager().createScalarScene("Scalar Scene", "Outline", "Scalar", null);

    Scene scene_17 = 
      simulation_0.getSceneManager().getScene("Scalar Scene 3");

    scene_17.initializeAndWait();

    ScalarDisplayer scalarDisplayer_2 = 
      ((ScalarDisplayer) scene_17.getDisplayerManager().getObject("Scalar 1"));

    Legend legend_2 = 
      scalarDisplayer_2.getLegend();

    legend_2.setLookupTable(predefinedLookupTable_0);

    SceneUpdate sceneUpdate_16 = 
      scene_17.getSceneUpdate();

    HardcopyProperties hardcopyProperties_16 = 
      sceneUpdate_16.getHardcopyProperties();

    hardcopyProperties_16.setCurrentResolutionWidth(25);

    hardcopyProperties_16.setCurrentResolutionHeight(25);

    hardcopyProperties_15.setCurrentResolutionWidth(1330);

    hardcopyProperties_15.setCurrentResolutionHeight(667);

    hardcopyProperties_16.setCurrentResolutionWidth(1328);

    hardcopyProperties_16.setCurrentResolutionHeight(666);

    scene_17.resetCamera();

    simulation_0.getSceneManager().createScalarScene("Scalar Scene", "Outline", "Scalar", null);

    Scene scene_18 = 
      simulation_0.getSceneManager().getScene("Scalar Scene 4");

    scene_18.initializeAndWait();

    ScalarDisplayer scalarDisplayer_3 = 
      ((ScalarDisplayer) scene_18.getDisplayerManager().getObject("Scalar 1"));

    Legend legend_3 = 
      scalarDisplayer_3.getLegend();

    legend_3.setLookupTable(predefinedLookupTable_0);

    SceneUpdate sceneUpdate_17 = 
      scene_18.getSceneUpdate();

    HardcopyProperties hardcopyProperties_17 = 
      sceneUpdate_17.getHardcopyProperties();

    hardcopyProperties_17.setCurrentResolutionWidth(25);

    hardcopyProperties_17.setCurrentResolutionHeight(25);

    hardcopyProperties_16.setCurrentResolutionWidth(1330);

    hardcopyProperties_16.setCurrentResolutionHeight(667);

    hardcopyProperties_17.setCurrentResolutionWidth(1328);

    hardcopyProperties_17.setCurrentResolutionHeight(666);

    scene_18.resetCamera();

    simulation_0.getSceneManager().createScalarScene("Scalar Scene", "Outline", "Scalar", null);

    Scene scene_19 = 
      simulation_0.getSceneManager().getScene("Scalar Scene 5");

    scene_19.initializeAndWait();

    ScalarDisplayer scalarDisplayer_4 = 
      ((ScalarDisplayer) scene_19.getDisplayerManager().getObject("Scalar 1"));

    Legend legend_4 = 
      scalarDisplayer_4.getLegend();

    legend_4.setLookupTable(predefinedLookupTable_0);

    SceneUpdate sceneUpdate_18 = 
      scene_19.getSceneUpdate();

    HardcopyProperties hardcopyProperties_18 = 
      sceneUpdate_18.getHardcopyProperties();

    hardcopyProperties_18.setCurrentResolutionWidth(25);

    hardcopyProperties_18.setCurrentResolutionHeight(25);

    hardcopyProperties_17.setCurrentResolutionWidth(1330);

    hardcopyProperties_17.setCurrentResolutionHeight(667);

    hardcopyProperties_18.setCurrentResolutionWidth(1328);

    hardcopyProperties_18.setCurrentResolutionHeight(666);

    scene_19.resetCamera();

    simulation_0.getSceneManager().createScalarScene("Scalar Scene", "Outline", "Scalar", null);

    Scene scene_20 = 
      simulation_0.getSceneManager().getScene("Scalar Scene 6");

    scene_20.initializeAndWait();

    ScalarDisplayer scalarDisplayer_5 = 
      ((ScalarDisplayer) scene_20.getDisplayerManager().getObject("Scalar 1"));

    Legend legend_5 = 
      scalarDisplayer_5.getLegend();

    legend_5.setLookupTable(predefinedLookupTable_0);

    SceneUpdate sceneUpdate_19 = 
      scene_20.getSceneUpdate();

    HardcopyProperties hardcopyProperties_19 = 
      sceneUpdate_19.getHardcopyProperties();

    hardcopyProperties_19.setCurrentResolutionWidth(25);

    hardcopyProperties_19.setCurrentResolutionHeight(25);

    hardcopyProperties_18.setCurrentResolutionWidth(1330);

    hardcopyProperties_18.setCurrentResolutionHeight(667);

    hardcopyProperties_19.setCurrentResolutionWidth(1328);

    hardcopyProperties_19.setCurrentResolutionHeight(666);

    scene_20.resetCamera();

    hardcopyProperties_15.setCurrentResolutionWidth(1328);

    hardcopyProperties_15.setCurrentResolutionHeight(666);

    CurrentView currentView_11 = 
      scene_16.getCurrentView();

    currentView_11.setRepresentation(null);

    VisView visView_0 = 
      ((VisView) simulation_0.getViewManager().getObject("View 1"));

    currentView_11.setView(visView_0);

    hardcopyProperties_16.setCurrentResolutionWidth(1328);

    hardcopyProperties_16.setCurrentResolutionHeight(666);

    CurrentView currentView_12 = 
      scene_17.getCurrentView();

    currentView_12.setRepresentation(null);

    currentView_12.setView(visView_0);

    hardcopyProperties_17.setCurrentResolutionWidth(1328);

    hardcopyProperties_17.setCurrentResolutionHeight(666);

    CurrentView currentView_13 = 
      scene_18.getCurrentView();

    currentView_13.setRepresentation(null);

    currentView_13.setView(visView_0);

    hardcopyProperties_18.setCurrentResolutionWidth(1328);

    hardcopyProperties_18.setCurrentResolutionHeight(666);

    CurrentView currentView_14 = 
      scene_19.getCurrentView();

    currentView_14.setRepresentation(null);

    currentView_14.setView(visView_0);

    CurrentView currentView_15 = 
      scene_20.getCurrentView();

    currentView_15.setRepresentation(null);

    currentView_15.setView(visView_0);

    scalarDisplayer_1.getInputParts().setQuery(null);

    Region region_0 = 
      simulation_0.getRegionManager().getRegion("Body 1");

    Boundary boundary_2 = 
      region_0.getBoundaryManager().getBoundary("wall");

    scalarDisplayer_1.getInputParts().setObjects(boundary_2);

    BlueRedLookupTable blueRedLookupTable_0 = 
      ((BlueRedLookupTable) simulation_0.get(LookupTableManager.class).getObject("blue-red"));

    legend_1.setLookupTable(blueRedLookupTable_0);

    scalarDisplayer_1.setFillMode(ScalarFillMode.NODE_FILLED);

    legend_1.updateLayout(new DoubleVector(new double[] {0.73, 0.08}), 0.19999999999999996, 0.035, 0);

    Units units_2 = 
      ((Units) simulation_0.getUnitsManager().getObject("Pa"));

    scalarDisplayer_1.getScalarDisplayQuantity().getMinimumValue().setValueAndUnits(0.0, units_2);

    PrimitiveFieldFunction primitiveFieldFunction_0 = 
      ((PrimitiveFieldFunction) simulation_0.getFieldFunctionManager().getFunction("WallShearStress"));

    VectorMagnitudeFieldFunction vectorMagnitudeFieldFunction_0 = 
      ((VectorMagnitudeFieldFunction) primitiveFieldFunction_0.getMagnitudeFunction());

    scalarDisplayer_1.getScalarDisplayQuantity().setFieldFunction(vectorMagnitudeFieldFunction_0);

    scalarDisplayer_1.getScalarDisplayQuantity().getMaximumValue().setValueAndUnits(0.0, units_2);

    scalarDisplayer_1.getScalarDisplayQuantity().setClip(ClipMode.NONE);

    scalarDisplayer_1.getScalarDisplayQuantity().setAutoRange(AutoRangeMode.BOTH);

    legend_1.setLookupTable(blueRedLookupTable_0);

    LatestMeshProxyRepresentation latestMeshProxyRepresentation_0 = 
      ((LatestMeshProxyRepresentation) simulation_0.getRepresentationManager().getObject("Latest Surface/Volume"));

    scalarDisplayer_1.setRepresentation(latestMeshProxyRepresentation_0);

    Scene scene_11 = 
      simulation_0.getSceneManager().getScene("Scalar Scene 1");

    scene_11.setPresentationName("WSS");

    scene_11.setPresentationName("Scalar Scene 1");

    scene_16.setPresentationName("WSS");

    sceneUpdate_15.setSaveAnimation(true);

    sceneUpdate_15.setAnimationFilePath("C2coil-photo-WSS");

    scalarDisplayer_2.getInputParts().setQuery(null);

    scalarDisplayer_2.getInputParts().setObjects(boundary_2);

    legend_2.updateLayout(new DoubleVector(new double[] {0.73, 0.08}), 0.19999999999999996, 0.035, 0);

    UserFieldFunction userFieldFunction_0 = 
      ((UserFieldFunction) simulation_0.getFieldFunctionManager().getFunction("TA-WSS"));

    scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(userFieldFunction_0);

    scalarDisplayer_2.getScalarDisplayQuantity().getMinimumValue().setValueAndUnits(0.0, units_2);

    scalarDisplayer_2.getScalarDisplayQuantity().getMaximumValue().setValueAndUnits(0.0, units_2);

    scalarDisplayer_2.getScalarDisplayQuantity().setClip(ClipMode.NONE);

    scalarDisplayer_2.getScalarDisplayQuantity().setAutoRange(AutoRangeMode.BOTH);

    legend_2.setLookupTable(predefinedLookupTable_0);

    scalarDisplayer_2.setRepresentation(latestMeshProxyRepresentation_0);

    scalarDisplayer_2.setFillMode(ScalarFillMode.NODE_FILLED);

    scene_17.setPresentationName("TA-WSS");

    legend_3.updateLayout(new DoubleVector(new double[] {0.73, 0.08}), 0.19999999999999996, 0.035, 0);

    Units units_3 = 
      ((Units) simulation_0.getUnitsManager().getObject("/Pa"));

    scalarDisplayer_3.getScalarDisplayQuantity().getMinimumValue().setValueAndUnits(0.0, units_3);

    scalarDisplayer_3.getScalarDisplayQuantity().getMaximumValue().setValueAndUnits(0.0, units_3);

    scalarDisplayer_3.getScalarDisplayQuantity().setClip(ClipMode.NONE);

    scalarDisplayer_3.getScalarDisplayQuantity().setAutoRange(AutoRangeMode.BOTH);

    UserFieldFunction userFieldFunction_1 = 
      ((UserFieldFunction) simulation_0.getFieldFunctionManager().getFunction("RRT"));

    scalarDisplayer_3.getScalarDisplayQuantity().setFieldFunction(userFieldFunction_1);

    legend_3.setLookupTable(predefinedLookupTable_0);

    scalarDisplayer_3.setRepresentation(latestMeshProxyRepresentation_0);

    scalarDisplayer_3.setFillMode(ScalarFillMode.NODE_FILLED);

    legend_3.setLookupTable(blueRedLookupTable_0);

    legend_2.updateLayout(new DoubleVector(new double[] {0.73, 0.08}), 0.19999999999999996, 0.035, 0);

    legend_2.updateLayout(new DoubleVector(new double[] {0.73, 0.08}), 0.19999999999999996, 0.035, 0);

    scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(userFieldFunction_0);

    scalarDisplayer_2.getScalarDisplayQuantity().getMinimumValue().setValueAndUnits(0.0, units_2);

    scalarDisplayer_2.getScalarDisplayQuantity().getMaximumValue().setValueAndUnits(0.0, units_2);

    scalarDisplayer_2.getScalarDisplayQuantity().setClip(ClipMode.NONE);

    scalarDisplayer_2.getScalarDisplayQuantity().setAutoRange(AutoRangeMode.BOTH);

    legend_2.setLookupTable(blueRedLookupTable_0);

    scalarDisplayer_2.setRepresentation(latestMeshProxyRepresentation_0);

    scalarDisplayer_3.getInputParts().setQuery(null);

    scalarDisplayer_3.getInputParts().setObjects(boundary_2);

    scene_18.setPresentationName("RRT");

    legend_4.updateLayout(new DoubleVector(new double[] {0.73, 0.08}), 0.19999999999999996, 0.035, 0);

    Units units_1 = 
      ((Units) simulation_0.getUnitsManager().getObject(""));

    scalarDisplayer_4.getScalarDisplayQuantity().getMinimumValue().setValueAndUnits(0.0, units_1);

    UserFieldFunction userFieldFunction_2 = 
      ((UserFieldFunction) simulation_0.getFieldFunctionManager().getFunction("OSI"));

    scalarDisplayer_4.getScalarDisplayQuantity().setFieldFunction(userFieldFunction_2);

    scalarDisplayer_4.getScalarDisplayQuantity().getMaximumValue().setValueAndUnits(0.0, units_1);

    scalarDisplayer_4.getScalarDisplayQuantity().setClip(ClipMode.NONE);

    scalarDisplayer_4.getScalarDisplayQuantity().setAutoRange(AutoRangeMode.BOTH);

    legend_4.setLookupTable(blueRedLookupTable_0);

    scalarDisplayer_4.setRepresentation(latestMeshProxyRepresentation_0);

    scalarDisplayer_4.getInputParts().setQuery(null);

    scalarDisplayer_4.getInputParts().setObjects(boundary_2);

    scalarDisplayer_4.setFillMode(ScalarFillMode.NODE_FILLED);

    currentView_14.setInput(new DoubleVector(new double[] {8.581555227538537, -1.4214708685374464, -4.783393581394005}), new DoubleVector(new double[] {38.068772590284055, -1.4214708685374464, -4.783393581394005}), new DoubleVector(new double[] {0.0, 0.0, -1.0}), 13.584650444192585, 0, 30.0);

    sceneUpdate_18.setSaveAnimation(true);

    sceneUpdate_18.setAnimationFilePath("C2coil-photo-OSI");

    scene_19.setPresentationName("OSI");

    legend_5.updateLayout(new DoubleVector(new double[] {0.73, 0.08}), 0.19999999999999996, 0.035, 0);

    scalarDisplayer_5.getScalarDisplayQuantity().getMinimumValue().setValueAndUnits(0.0, units_2);

    PrimitiveFieldFunction primitiveFieldFunction_1 = 
      ((PrimitiveFieldFunction) simulation_0.getFieldFunctionManager().getFunction("MeanWSSMonitor"));

    scalarDisplayer_5.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_1);

    scalarDisplayer_5.getScalarDisplayQuantity().getMaximumValue().setValueAndUnits(0.0, units_2);

    scalarDisplayer_5.getScalarDisplayQuantity().setClip(ClipMode.NONE);

    scalarDisplayer_5.getScalarDisplayQuantity().setAutoRange(AutoRangeMode.BOTH);

    legend_5.setLookupTable(predefinedLookupTable_0);

    scalarDisplayer_5.setRepresentation(latestMeshProxyRepresentation_0);

    scalarDisplayer_5.setFillMode(ScalarFillMode.NODE_FILLED);

    scalarDisplayer_5.getInputParts().setQuery(null);

    scalarDisplayer_5.getInputParts().setObjects(boundary_2);

    legend_5.updateLayout(new DoubleVector(new double[] {0.73, 0.08}), 0.19999999999999996, 0.035, 0);

    scalarDisplayer_5.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_1);

    scalarDisplayer_5.getScalarDisplayQuantity().getMinimumValue().setValueAndUnits(0.0, units_2);

    scalarDisplayer_5.getScalarDisplayQuantity().getMaximumValue().setValueAndUnits(0.0, units_2);

    scalarDisplayer_5.getScalarDisplayQuantity().setClip(ClipMode.NONE);

    scalarDisplayer_5.getScalarDisplayQuantity().setAutoRange(AutoRangeMode.BOTH);

    legend_5.setLookupTable(blueRedLookupTable_0);

    scalarDisplayer_5.setRepresentation(latestMeshProxyRepresentation_0);

    scene_20.setPresentationName("Mean WSS");

    scene_11.setPresentationName("!Streamlines");

    ScalarDisplayer scalarDisplayer_0 = 
      ((ScalarDisplayer) scene_11.getDisplayerManager().getObject("Scalar 1"));

    Legend legend_0 = 
      scalarDisplayer_0.getLegend();

    legend_0.updateLayout(new DoubleVector(new double[] {0.73, 0.08}), 0.19999999999999996, 0.035, 0);

    PrimitiveFieldFunction primitiveFieldFunction_2 = 
      ((PrimitiveFieldFunction) simulation_0.getFieldFunctionManager().getFunction("Velocity"));

    VectorMagnitudeFieldFunction vectorMagnitudeFieldFunction_1 = 
      ((VectorMagnitudeFieldFunction) primitiveFieldFunction_2.getMagnitudeFunction());

    scalarDisplayer_0.getScalarDisplayQuantity().setFieldFunction(vectorMagnitudeFieldFunction_1);

    Units units_4 = 
      ((Units) simulation_0.getUnitsManager().getObject("m/s"));

    scalarDisplayer_0.getScalarDisplayQuantity().getMinimumValue().setValueAndUnits(0.0, units_4);

    scalarDisplayer_0.getScalarDisplayQuantity().getMaximumValue().setValueAndUnits(0.0, units_4);

    scalarDisplayer_0.getScalarDisplayQuantity().setClip(ClipMode.NONE);

    scalarDisplayer_0.getScalarDisplayQuantity().setAutoRange(AutoRangeMode.BOTH);

    legend_0.setLookupTable(blueRedLookupTable_0);

    scalarDisplayer_0.setRepresentation(latestMeshProxyRepresentation_0);

    scalarDisplayer_0.setFillMode(ScalarFillMode.NODE_FILLED);

    SceneUpdate sceneUpdate_11 = 
      scene_11.getSceneUpdate();

    sceneUpdate_11.setSaveAnimation(true);

    sceneUpdate_15.setAnimationFilePath("C2coil-photo-Streamlines");
  }
}
