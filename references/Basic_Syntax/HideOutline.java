// Simcenter STAR-CCM+ macro: HideOutline.java
// Written by Simcenter STAR-CCM+ 16.04.012
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class HideOutline extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    Scene scene_1 = 
      simulation_0.getSceneManager().getScene("Turbulent Kinetic Energy");

    PartDisplayer partDisplayer_2 = 
      ((PartDisplayer) scene_1.getDisplayerManager().getObject("Outline 1"));

    partDisplayer_2.setOutline(true);

    partDisplayer_2.setOutline(false);
  }
}
