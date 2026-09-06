// Simcenter STAR-CCM+ macro: Normals.java
// Written by Simcenter STAR-CCM+ 16.04.012
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class Normals extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    MonitorPlot monitorPlot_0 = 
      ((MonitorPlot) simulation_0.getPlotManager().getPlot("Downforce Monitor Plot"));

    monitorPlot_0.close();

    Scene scene_0 = 
      simulation_0.getSceneManager().getScene("Scalar Scene 1");

    scene_0.close();

    Scene scene_1 = 
      simulation_0.getSceneManager().getScene("Pressure Coefficient");

    scene_1.close();

    ResidualPlot residualPlot_0 = 
      ((ResidualPlot) simulation_0.getPlotManager().getPlot("Residuals"));

    residualPlot_0.close();

    Scene scene_2 = 
      simulation_0.getSceneManager().getScene("Turbulent Kinetic Energy");

    scene_2.open();

    SceneUpdate sceneUpdate_2 = 
      scene_2.getSceneUpdate();

    HardcopyProperties hardcopyProperties_4 = 
      sceneUpdate_2.getHardcopyProperties();

    hardcopyProperties_4.setCurrentResolutionWidth(428);

    hardcopyProperties_4.setCurrentResolutionHeight(179);

    hardcopyProperties_4.setCurrentResolutionWidth(861);

    hardcopyProperties_4.setCurrentResolutionHeight(387);

    CurrentView currentView_2 = 
      scene_2.getCurrentView();

    currentView_2.setInput(new DoubleVector(new double[] {-12.257771901457494, -2.628868240143934, 4.214026615744951}), new DoubleVector(new double[] {-12.257771901457494, 31.59114051551283, 4.214026615744951}), new DoubleVector(new double[] {0.0, 0.0, 1.0}), 14.508755292471486, 1, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {-4.282388681075502, -4.48074688257886, 2.9839923877258374}), new DoubleVector(new double[] {-4.282388681075502, 31.59114051551283, 2.9839923877258374}), new DoubleVector(new double[] {0.0, 0.0, 1.0}), 10.27295116104434, 1, 30.0);
  }
}
