// Simcenter STAR-CCM+ macro: ChangePerspective.java
// Written by Simcenter STAR-CCM+ 16.04.007


import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class ChangePerspective extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    Scene scene_1 = 
      simulation_0.getSceneManager().getScene("Vector Scene 1");

    CurrentView currentView_1 = 
      scene_1.getCurrentView();

    currentView_1.setInput(new DoubleVector(new double[] {0.8, 0.0, 0.8}), new DoubleVector(new double[] {0.8, -38.591138454536214, 0.8}), new DoubleVector(new double[] {0.0, 0.0, 1.0}), 1.3052619222005157, 1, 30.0);

    Units units_0 = 
      ((Units) simulation_0.getUnitsManager().getObject("m"));

    currentView_1.getPositionCoordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {0.8, -5.0, 0.8}));
  }
}
