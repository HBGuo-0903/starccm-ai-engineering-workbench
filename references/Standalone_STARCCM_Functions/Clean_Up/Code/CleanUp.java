// Simcenter STAR-CCM+ macro: CleanUp.java
// Written by Simcenter STAR-CCM+ 16.04.012
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.screenplay.*;
import star.vis.*;

public class CleanUp extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    Screenplay screenplay_0 = 
      ((Screenplay) simulation_0.get(ScreenplayManager.class).getObject("Screenplay 1"));

    simulation_0.get(ScreenplayManager.class).remove(screenplay_0);

    Scene scene_0 = 
      simulation_0.getSceneManager().getScene("Velocity");

    simulation_0.getSceneManager().deleteScenes(new NeoObjectVector(new Object[] {scene_0}));

    Scene scene_1 = 
      simulation_0.getSceneManager().getScene("Scalar Scene 1");

    SceneUpdate sceneUpdate_1 = 
      scene_1.getSceneUpdate();

    HardcopyProperties hardcopyProperties_1 = 
      sceneUpdate_1.getHardcopyProperties();

    hardcopyProperties_1.setCurrentResolutionWidth(861);

    hardcopyProperties_1.setCurrentResolutionHeight(387);

    PlaneSection planeSection_3 = 
      ((PlaneSection) simulation_0.getPartManager().getObject("X Normal"));

    simulation_0.getPartManager().removeObjects(planeSection_3);

    PlaneSection planeSection_4 = 
      ((PlaneSection) simulation_0.getPartManager().getObject("Y Normal"));

    simulation_0.getPartManager().removeObjects(planeSection_4);

    PlaneSection planeSection_5 = 
      ((PlaneSection) simulation_0.getPartManager().getObject("Z Normal"));

    simulation_0.getPartManager().removeObjects(planeSection_5);
  }
}
