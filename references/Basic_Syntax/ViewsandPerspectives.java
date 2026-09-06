// Simcenter STAR-CCM+ macro: ViewsandPerspectives.java
// Written by Simcenter STAR-CCM+ 16.04.012
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class ViewsandPerspectives extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    Scene scene_3 = 
      simulation_0.getSceneManager().getScene("Velocity");

    CurrentView currentView_2 = 
      scene_3.getCurrentView();

    VisView visView_0 = 
      ((VisView) simulation_0.getViewManager().getObject("View 1"));

    currentView_2.setView(visView_0);

    currentView_2.setInput(new DoubleVector(new double[] {1.1222830462226314, -1.0000203239712846E-4, 1.0809199577009214}), new DoubleVector(new double[] {1.1222830462226314, -38.591138454536214, 1.0809199577009214}), new DoubleVector(new double[] {0.0, 0.0, 1.0}), 1.8276821527868743, 1, 30.0);

    VisView visView_2 = 
      ((VisView) simulation_0.getViewManager().getObject("View 2"));

    currentView_2.setView(visView_2);

    VisView visView_1 = 
      ((VisView) simulation_0.getViewManager().getObject("View 3"));

    currentView_2.setView(visView_1);
  }
}
