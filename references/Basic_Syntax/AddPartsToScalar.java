// Simcenter STAR-CCM+ macro: AddPartsToScalar.java
// Written by Simcenter STAR-CCM+ 16.04.012
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class AddPartsToScalar extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    Scene scene_2 = 
      simulation_0.getSceneManager().getScene("test scene");

    ScalarDisplayer scalarDisplayer_2 = 
      ((ScalarDisplayer) scene_2.getDisplayerManager().getObject("Scalar 1"));

    scalarDisplayer_2.getInputParts().setQuery(null);

    PlaneSection planeSection_3 = 
      ((PlaneSection) simulation_0.getPartManager().getObject("X Normal"));

    PlaneSection planeSection_4 = 
      ((PlaneSection) simulation_0.getPartManager().getObject("Y Normal"));

    scalarDisplayer_2.getInputParts().setObjects(planeSection_3, planeSection_4);

    AnimationDirector animationDirector_0 = 
      scene_2.getAnimationDirector();

    animationDirector_0.setFramesPerSecond(15.0);

    animationDirector_0.start();

    animationDirector_0.setIsPlaying(true);

    animationDirector_0.setIsPlaying(false);

    animationDirector_0.stop(true);
  }
}
