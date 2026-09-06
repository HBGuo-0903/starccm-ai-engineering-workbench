// Simcenter STAR-CCM+ macro: SceneCreate.java
// Written by Simcenter STAR-CCM+ 16.04.012

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;
public class SceneCreate extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    simulation_0.getSceneManager().createScalarScene("Scalar Scene", "Outline", "Scalar");

    Scene scene_1 = 
      simulation_0.getSceneManager().getScene("Scalar Scene 2");

    scene_1.initializeAndWait();

    PartDisplayer partDisplayer_3 = 
      ((PartDisplayer) scene_1.getDisplayerManager().getObject("Outline 1"));

    partDisplayer_3.initialize();

    ScalarDisplayer scalarDisplayer_1 = 
      ((ScalarDisplayer) scene_1.getDisplayerManager().getObject("Scalar 1"));

    scalarDisplayer_1.initialize();

    Legend legend_1 = 
      scalarDisplayer_1.getLegend();

    PredefinedLookupTable predefinedLookupTable_0 = 
      ((PredefinedLookupTable) simulation_0.get(LookupTableManager.class).getObject("blue-yellow-red"));

    legend_1.setLookupTable(predefinedLookupTable_0);

    SceneUpdate sceneUpdate_1 = 
      scene_1.getSceneUpdate();

    HardcopyProperties hardcopyProperties_1 = 
      sceneUpdate_1.getHardcopyProperties();

    hardcopyProperties_1.setCurrentResolutionWidth(25);

    hardcopyProperties_1.setCurrentResolutionHeight(25);

    hardcopyProperties_1.setCurrentResolutionWidth(1330);

    hardcopyProperties_1.setCurrentResolutionHeight(901);

    scene_1.resetCamera();

    partDisplayer_3.getInputParts().setQuery(null);

    partDisplayer_3.getInputParts().setObjects();

    scalarDisplayer_1.getInputParts().setQuery(null);

    PlaneSection planeSection_4 = 
      ((PlaneSection) simulation_0.getPartManager().getObject("Y Normal"));

    scalarDisplayer_1.getInputParts().setObjects(planeSection_4);

    PrimitiveFieldFunction primitiveFieldFunction_1 = 
      ((PrimitiveFieldFunction) simulation_0.getFieldFunctionManager().getFunction("Velocity"));

    VectorMagnitudeFieldFunction vectorMagnitudeFieldFunction_0 = 
      ((VectorMagnitudeFieldFunction) primitiveFieldFunction_1.getMagnitudeFunction());

    scalarDisplayer_1.getScalarDisplayQuantity().setFieldFunction(vectorMagnitudeFieldFunction_0);

    scalarDisplayer_1.getScalarDisplayQuantity().getMinimumValue().setValue(0.0);

    Units units_5 = 
      ((Units) simulation_0.getUnitsManager().getObject("m/s"));

    scalarDisplayer_1.getScalarDisplayQuantity().getMinimumValue().setUnits(units_5);

    scalarDisplayer_1.getScalarDisplayQuantity().getMaximumValue().setValue(27.5);

    scalarDisplayer_1.getScalarDisplayQuantity().getMaximumValue().setUnits(units_5);
  }
}
