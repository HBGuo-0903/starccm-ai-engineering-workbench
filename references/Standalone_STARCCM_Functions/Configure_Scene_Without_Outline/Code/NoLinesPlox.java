// Simcenter STAR-CCM+ macro: NoLinesPlox.java
// Written by Simcenter STAR-CCM+ 16.04.012
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class NoLinesPlox extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    simulation_0.getSceneManager().createScalarScene("Scalar Scene", "Outline", "Scalar");

    Scene scene_2 = 
      simulation_0.getSceneManager().getScene("Scalar Scene 2");

    scene_2.initializeAndWait();

    PartDisplayer partDisplayer_4 = 
      ((PartDisplayer) scene_2.getDisplayerManager().getObject("Outline 1"));

    partDisplayer_4.initialize();

    ScalarDisplayer scalarDisplayer_2 = 
      ((ScalarDisplayer) scene_2.getDisplayerManager().getObject("Scalar 1"));

    scalarDisplayer_2.initialize();

    Legend legend_0 = 
      scalarDisplayer_2.getLegend();

    PredefinedLookupTable predefinedLookupTable_0 = 
      ((PredefinedLookupTable) simulation_0.get(LookupTableManager.class).getObject("blue-yellow-red"));

    legend_0.setLookupTable(predefinedLookupTable_0);

    SceneUpdate sceneUpdate_1 = 
      scene_2.getSceneUpdate();

    HardcopyProperties hardcopyProperties_1 = 
      sceneUpdate_1.getHardcopyProperties();

    hardcopyProperties_1.setCurrentResolutionWidth(25);

    hardcopyProperties_1.setCurrentResolutionHeight(25);

    Scene scene_1 = 
      simulation_0.getSceneManager().getScene("Turbulent Kinetic Energy");

    SceneUpdate sceneUpdate_2 = 
      scene_1.getSceneUpdate();

    HardcopyProperties hardcopyProperties_2 = 
      sceneUpdate_2.getHardcopyProperties();

    hardcopyProperties_2.setCurrentResolutionWidth(1242);

    hardcopyProperties_2.setCurrentResolutionHeight(603);

    hardcopyProperties_1.setCurrentResolutionWidth(1240);

    hardcopyProperties_1.setCurrentResolutionHeight(602);

    scene_2.resetCamera();

    scene_2.setTransparencyOverrideMode(SceneTransparencyOverride.MAKE_SCENE_TRANSPARENT);

    scene_2.setMeshOverrideMode(SceneMeshOverride.HIDE_ALL_MESHES);

    partDisplayer_4.setOutline(false);

    partDisplayer_4.setVisibilityOverrideMode(DisplayerVisibilityOverride.SHOW_ALL_PARTS);

    partDisplayer_4.setVisibilityOverrideMode(DisplayerVisibilityOverride.USE_PART_PROPERTY);
  }
}
