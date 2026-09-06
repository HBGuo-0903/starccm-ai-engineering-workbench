// STAR-CCM+ macro: CreateSceneForTracing.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class CreateSceneForTracing extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    simulation_0.getSceneManager().createScalarScene("Scalar Scene", "Outline", "Scalar");

    Scene scene_1 = 
      simulation_0.getSceneManager().getScene("Scalar Scene 1");

    scene_1.initializeAndWait();

    PartDisplayer partDisplayer_0 = 
      ((PartDisplayer) scene_1.getDisplayerManager().getDisplayer("Outline 1"));

    partDisplayer_0.initialize();

    ScalarDisplayer scalarDisplayer_0 = 
      ((ScalarDisplayer) scene_1.getDisplayerManager().getDisplayer("Scalar 1"));

    scalarDisplayer_0.initialize();

    Legend legend_0 = 
      scalarDisplayer_0.getLegend();

    BlueRedLookupTable blueRedLookupTable_0 = 
      ((BlueRedLookupTable) simulation_0.get(LookupTableManager.class).getObject("blue-red"));

    legend_0.setLookupTable(blueRedLookupTable_0);

    SceneUpdate sceneUpdate_0 = 
      scene_1.getSceneUpdate();

    HardcopyProperties hardcopyProperties_0 = 
      sceneUpdate_0.getHardcopyProperties();

    hardcopyProperties_0.setCurrentResolutionWidth(25);

    hardcopyProperties_0.setCurrentResolutionHeight(25);

    hardcopyProperties_0.setCurrentResolutionWidth(1330);

    hardcopyProperties_0.setCurrentResolutionHeight(625);

    scene_1.resetCamera();

    scalarDisplayer_0.getInputParts().setQuery(null);

    PlaneSection planeSection_0 = 
      ((PlaneSection) simulation_0.getPartManager().getObject("XY Plane"));

    PlaneSection planeSection_1 = 
      ((PlaneSection) simulation_0.getPartManager().getObject("XZ Plane"));

    PlaneSection planeSection_2 = 
      ((PlaneSection) simulation_0.getPartManager().getObject("YZ Plane"));

    scalarDisplayer_0.getInputParts().setObjects(planeSection_0, planeSection_1, planeSection_2);

    PrimitiveFieldFunction primitiveFieldFunction_0 = 
      ((PrimitiveFieldFunction) simulation_0.getFieldFunctionManager().getFunction("Ba_2+"));

    scalarDisplayer_0.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_0);

    scene_1.setBackgroundColorMode(BackgroundColorMode.SOLID);

    CurrentView currentView_0 = 
      scene_1.getCurrentView();

    currentView_0.setInput(new DoubleVector(new double[] {1.552230566304047E-14, -8.213661445433118E-9, 0.07000000000000002}), new DoubleVector(new double[] {1.552230566304047E-14, -8.213661445433118E-9, 0.47714309349357603}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 0.10628567536481787, 1, 30.0);

    LogoAnnotation logoAnnotation_0 = 
      ((LogoAnnotation) simulation_0.getAnnotationManager().getObject("Logo"));

    scene_1.getAnnotationPropManager().removePropsForAnnotations(logoAnnotation_0);

    PhysicalTimeAnnotation physicalTimeAnnotation_0 = 
      ((PhysicalTimeAnnotation) simulation_0.getAnnotationManager().getObject("Solution Time"));

    PhysicalTimeAnnotationProp physicalTimeAnnotationProp_0 = 
      (PhysicalTimeAnnotationProp) scene_1.getAnnotationPropManager().createPropForAnnotation(physicalTimeAnnotation_0);
  }
}
