// Simcenter STAR-CCM+ macro: ParalellView.java
// Written by Simcenter STAR-CCM+ 16.04.012
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class ParalellView extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    Scene scene_2 = 
      simulation_0.getSceneManager().getScene("test scene");

    CurrentView currentView_1 = 
      scene_2.getCurrentView();

    currentView_1.setInput(new DoubleVector(new double[] {8.000000000000004, -3.4999989695117044, 3.500000000000001}), new DoubleVector(new double[] {8.000000000000004, -3.4999989695117044, 64.52939303022053}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 15.931868571471282, 1, 30.0);
  }
}
