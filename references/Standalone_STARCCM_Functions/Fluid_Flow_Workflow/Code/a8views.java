// Simcenter STAR-CCM+ macro: a8views.java
// Written by Simcenter STAR-CCM+ 19.02.009

// needs geometry scene 1, just produces 2 saved views




package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class a8views extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    Scene scene_7 = 
      simulation_0.getSceneManager().getScene("Geometry Scene 1");

    CurrentView currentView_5 = 
      scene_7.getCurrentView();

    currentView_5.setInput(new DoubleVector(new double[] {7.2534133285905185, -1.7394170372722417, -0.2511296672858627}), new DoubleVector(new double[] {65.03484517957456, -0.313671388132569, -5.65333559161613}), new DoubleVector(new double[] {-0.09305976573434266, -0.0011454446258189047, -0.9956598655957158}), 13.584650444192585, 0, 30.0);

    scene_7.resetCamera();

    scene_7.setViewOrientation(new DoubleVector(new double[] {1.0, 0.0, 0.0}), new DoubleVector(new double[] {0.0, 0.0, -1.0}));

    scene_7.saveCurrentView();

    scene_7.setViewOrientation(new DoubleVector(new double[] {-1.0, 0.0, 0.0}), new DoubleVector(new double[] {0.0, 0.0, -1.0}));

    scene_7.saveCurrentView();
  }
}
