// Simcenter STAR-CCM+ macro: VectorSceneCreate.java
// Written by Simcenter STAR-CCM+ 16.04.012
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class VectorSceneCreate extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    simulation_0.getSceneManager().createVectorScene("Vector Scene", "Outline", "Vector");

    Scene scene_3 = 
      simulation_0.getSceneManager().getScene("Vector Scene 2");

    scene_3.initializeAndWait();

    PartDisplayer partDisplayer_3 = 
      ((PartDisplayer) scene_3.getDisplayerManager().getObject("Outline 1"));

    partDisplayer_3.initialize();

    VectorDisplayer vectorDisplayer_1 = 
      ((VectorDisplayer) scene_3.getDisplayerManager().getObject("Vector 1"));

    vectorDisplayer_1.initialize();

    SceneUpdate sceneUpdate_3 = 
      scene_3.getSceneUpdate();

    HardcopyProperties hardcopyProperties_3 = 
      sceneUpdate_3.getHardcopyProperties();

    hardcopyProperties_3.setCurrentResolutionWidth(25);

    hardcopyProperties_3.setCurrentResolutionHeight(25);

    Scene scene_2 = 
      simulation_0.getSceneManager().getScene("Scalar Scene 2");

    SceneUpdate sceneUpdate_2 = 
      scene_2.getSceneUpdate();

    HardcopyProperties hardcopyProperties_2 = 
      sceneUpdate_2.getHardcopyProperties();

    hardcopyProperties_2.setCurrentResolutionWidth(970);

    hardcopyProperties_2.setCurrentResolutionHeight(447);

    hardcopyProperties_3.setCurrentResolutionWidth(968);

    hardcopyProperties_3.setCurrentResolutionHeight(446);

    Legend legend_0 = 
      vectorDisplayer_1.getLegend();

    PredefinedLookupTable predefinedLookupTable_0 = 
      ((PredefinedLookupTable) simulation_0.get(LookupTableManager.class).getObject("blue-yellow-red"));

    legend_0.setLookupTable(predefinedLookupTable_0);

    scene_3.resetCamera();

    CurrentView currentView_3 = 
      scene_3.getCurrentView();

    currentView_3.setInput(new DoubleVector(new double[] {8.49864969187957, -3.434001216174703, 3.2911888522154413}), new DoubleVector(new double[] {8.49864969187957, -3.434001216174703, 70.63233236427794}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 15.795569227268112, 0, 30.0);

    currentView_3.setInput(new DoubleVector(new double[] {7.0022632272297045, -3.6320523659077737, 2.6633259070689945}), new DoubleVector(new double[] {7.0022632272297045, -3.6320523659077737, 52.31816118803582}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 15.795569227268112, 0, 30.0);

    currentView_3.setInput(new DoubleVector(new double[] {4.152301171695878, -4.009253226199014, 1.2409460428529009}), new DoubleVector(new double[] {4.152301171695878, -4.009253226199014, 17.437671312010313}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 15.795569227268112, 0, 30.0);

    currentView_3.setInput(new DoubleVector(new double[] {4.01996343112612, -4.026768515392071, 1.1854189876660737}), new DoubleVector(new double[] {4.01996343112612, -4.026768515392071, 15.817998793495004}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 15.795569227268112, 0, 30.0);

    currentView_3.setInput(new DoubleVector(new double[] {3.8617253457375407, -3.2531600979368234, 1.1854183398444462}), new DoubleVector(new double[] {3.8617253457375407, -3.2531600979368234, 15.817998793495004}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 15.795569227268112, 0, 30.0);

    currentView_3.setInput(new DoubleVector(new double[] {3.7961479657909223, -4.021738753670023, 2.032667321315938}), new DoubleVector(new double[] {2.557628651669775, -18.5374072711007, 3.401553048167898}), new DoubleVector(new double[] {-0.07678194813674484, 0.10010179529059796, 0.9920101627604069}), 15.795569227268112, 0, 30.0);

    currentView_3.setInput(new DoubleVector(new double[] {3.7961479657909223, -4.021738753670023, 2.032667321315938}), new DoubleVector(new double[] {2.557628651669775, -18.5374072711007, 3.401553048167898}), new DoubleVector(new double[] {-0.07678194813674484, 0.10010179529059796, 0.9920101627604069}), 15.795569227268112, 0, 30.0);

    currentView_3.setInput(new DoubleVector(new double[] {3.7961479657909223, -4.021738753670023, 2.032667321315938}), new DoubleVector(new double[] {2.557628651669775, -18.5374072711007, 3.401553048167898}), new DoubleVector(new double[] {-0.07678194813674484, 0.10010179529059796, 0.9920101627604069}), 15.795569227268112, 0, 30.0);

    currentView_3.setInput(new DoubleVector(new double[] {3.7961479657909223, -4.021738753670023, 2.032667321315938}), new DoubleVector(new double[] {2.557628651669775, -18.5374072711007, 3.401553048167898}), new DoubleVector(new double[] {-0.07678194813674484, 0.10010179529059796, 0.9920101627604069}), 15.795569227268112, 0, 30.0);

    currentView_3.setInput(new DoubleVector(new double[] {3.7938801132646764, -4.0705813136659845, 2.036192702229087}), new DoubleVector(new double[] {3.6798764213884105, -18.68431447200331, 2.7698165817257507}), new DoubleVector(new double[] {-0.07908056962232443, 0.05059619049855338, 0.9955833912913832}), 15.795569227268112, 0, 30.0);

    currentView_3.setInput(new DoubleVector(new double[] {3.7938801132646764, -4.0705813136659845, 2.036192702229087}), new DoubleVector(new double[] {3.6798764213884105, -18.68431447200331, 2.7698165817257507}), new DoubleVector(new double[] {-0.07908056962232443, 0.05059619049855338, 0.9955833912913832}), 15.795569227268112, 0, 30.0);

    currentView_3.setInput(new DoubleVector(new double[] {3.7938801132646764, -4.0705813136659845, 2.036192702229087}), new DoubleVector(new double[] {3.6798764213884105, -18.68431447200331, 2.7698165817257507}), new DoubleVector(new double[] {-0.07908056962232443, 0.05059619049855338, 0.9955833912913832}), 15.795569227268112, 0, 30.0);

    currentView_3.setInput(new DoubleVector(new double[] {3.7938801132646764, -4.0705813136659845, 2.036192702229087}), new DoubleVector(new double[] {3.6798764213884105, -18.68431447200331, 2.7698165817257507}), new DoubleVector(new double[] {-0.07908056962232443, 0.05059619049855338, 0.9955833912913832}), 15.795569227268112, 0, 30.0);

    currentView_3.setInput(new DoubleVector(new double[] {3.7908252819493153, -4.122225485851648, 2.044857716092298}), new DoubleVector(new double[] {2.214141021422611, -18.090996043862162, 6.1068676196107345}), new DoubleVector(new double[] {-0.06519108220267132, 0.2854096755853774, 0.956185881449577}), 15.795569227268112, 0, 30.0);

    currentView_3.setInput(new DoubleVector(new double[] {3.787299546947426, -4.153886831668772, 2.053553062404706}), new DoubleVector(new double[] {2.239751406910856, -18.24094143284905, 5.696682809664272}), new DoubleVector(new double[] {-0.06837410574161969, 0.2568259018966486, 0.9640360147727975}), 15.795569227268112, 0, 30.0);

    currentView_3.setInput(new DoubleVector(new double[] {3.7244816036925643, -4.596526151094952, 2.1296217328171294}), new DoubleVector(new double[] {1.2341836516602742, -18.96166313970061, 3.3760641501794564}), new DoubleVector(new double[] {-0.09039854260194204, 0.10163338693332755, 0.9907061916410514}), 15.795569227268112, 0, 30.0);

    currentView_3.setInput(new DoubleVector(new double[] {3.7244816036925643, -4.596526151094952, 2.1296217328171294}), new DoubleVector(new double[] {1.2341836516602742, -18.96166313970061, 3.3760641501794564}), new DoubleVector(new double[] {-0.09039854260194204, 0.10163338693332755, 0.9907061916410514}), 15.795569227268112, 0, 30.0);

    legend_0.setVisible(false);

    currentView_3.setInput(new DoubleVector(new double[] {3.7244816036925643, -4.596526151094952, 2.1296217328171294}), new DoubleVector(new double[] {1.2341836516602742, -18.96166313970061, 3.3760641501794564}), new DoubleVector(new double[] {-0.09039854260194204, 0.10163338693332755, 0.9907061916410514}), 15.795569227268112, 0, 30.0);

    currentView_3.setInput(new DoubleVector(new double[] {3.7244816036925643, -4.596526151094952, 2.1296217328171294}), new DoubleVector(new double[] {1.2341836516602742, -18.96166313970061, 3.3760641501794564}), new DoubleVector(new double[] {-0.09039854260194204, 0.10163338693332755, 0.9907061916410514}), 15.795569227268112, 0, 30.0);

    currentView_3.setInput(new DoubleVector(new double[] {3.7244816036925643, -4.596526151094952, 2.1296217328171294}), new DoubleVector(new double[] {1.2341836516602742, -18.96166313970061, 3.3760641501794564}), new DoubleVector(new double[] {-0.09039854260194204, 0.10163338693332755, 0.9907061916410514}), 15.795569227268112, 0, 30.0);
  }
}
