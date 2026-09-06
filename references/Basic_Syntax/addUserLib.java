// STAR-CCM+ macro: addUserLib.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class addUserLib extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    UserLibrary userLibrary_1 = 
      simulation_0.getUserFunctionManager().createUserLibrary(resolvePath("/home/mek/jroben/couetteCell/couette-cell/sim/libuser.so"));

    simulation_0.getSceneManager().createScalarScene("Scalar Scene", "Outline", "Scalar");

    Scene scene_0 = 
      simulation_0.getSceneManager().getScene("Scalar Scene 1");

    scene_0.initializeAndWait();

    PartDisplayer partDisplayer_0 = 
      ((PartDisplayer) scene_0.getDisplayerManager().getDisplayer("Outline 1"));

    partDisplayer_0.initialize();

    ScalarDisplayer scalarDisplayer_0 = 
      ((ScalarDisplayer) scene_0.getDisplayerManager().getDisplayer("Scalar 1"));

    scalarDisplayer_0.initialize();

    Legend legend_0 = 
      scalarDisplayer_0.getLegend();

    BlueRedLookupTable blueRedLookupTable_0 = 
      ((BlueRedLookupTable) simulation_0.get(LookupTableManager.class).getObject("blue-red"));

    legend_0.setLookupTable(blueRedLookupTable_0);

    SceneUpdate sceneUpdate_0 = 
      scene_0.getSceneUpdate();

    HardcopyProperties hardcopyProperties_0 = 
      sceneUpdate_0.getHardcopyProperties();

    hardcopyProperties_0.setCurrentResolutionWidth(25);

    hardcopyProperties_0.setCurrentResolutionHeight(25);

    hardcopyProperties_0.setCurrentResolutionWidth(1024);

    hardcopyProperties_0.setCurrentResolutionHeight(494);

    scene_0.resetCamera();

    CurrentView currentView_0 = 
      scene_0.getCurrentView();

    currentView_0.setInput(new DoubleVector(new double[] {1.552230566304047E-14, -8.213661445433118E-9, 0.07000000000000002}), new DoubleVector(new double[] {1.552230566304047E-14, -8.213661445433118E-9, 0.47714309349357603}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 0.10628567536481787, 1, 30.0);

    scene_0.setViewOrientation(new DoubleVector(new double[] {0.0, 1.0, 0.0}), new DoubleVector(new double[] {0.0, 0.0, 1.0}));

    scene_0.close();

    scene_0.setPresentationName("Saturation Rate");

    scene_0.setBackgroundColorMode(BackgroundColorMode.SOLID);

    LogoAnnotation logoAnnotation_0 = 
      ((LogoAnnotation) simulation_0.getAnnotationManager().getObject("Logo"));

    scene_0.getAnnotationPropManager().removePropsForAnnotations(logoAnnotation_0);

    PhysicalTimeAnnotation physicalTimeAnnotation_0 = 
      ((PhysicalTimeAnnotation) simulation_0.getAnnotationManager().getObject("Solution Time"));

    PhysicalTimeAnnotationProp physicalTimeAnnotationProp_0 = 
      (PhysicalTimeAnnotationProp) scene_0.getAnnotationPropManager().createPropForAnnotation(physicalTimeAnnotation_0);

    sceneUpdate_0.getUpdateModeOption().setSelected(StarUpdateModeOption.Type.DELTATIME);

    DeltaTimeUpdateFrequency deltaTimeUpdateFrequency_0 = 
      sceneUpdate_0.getDeltaTimeUpdateFrequency();

    Units units_0 = 
      ((Units) simulation_0.getUnitsManager().getObject("s"));

    deltaTimeUpdateFrequency_0.setDeltaTime("0.02", units_0);

    sceneUpdate_0.setAnimationFilenameBase("SaturationRate");

    sceneUpdate_0.setAnimationFilePath("SaturationRate");

    sceneUpdate_0.setSaveAnimation(true);
  }
}
