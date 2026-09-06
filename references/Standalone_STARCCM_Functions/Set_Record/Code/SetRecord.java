// Simcenter STAR-CCM+ macro: SetRecord.java
// Written by Simcenter STAR-CCM+ 16.04.012

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class SetRecord extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    Scene scene_1 = 
      simulation_0.getSceneManager().getScene("Pressure Coefficient");

    ScalarDisplayer scalarDisplayer_1 = 
      ((ScalarDisplayer) scene_1.getDisplayerManager().getObject("Scalar 1"));

    scalarDisplayer_1.getAnimationManager().setMode(DisplayerAnimationMode.SWEEP);

    SectionAnimationSettings sectionAnimationSettings_1 = 
      ((SectionAnimationSettings) scalarDisplayer_1.getAnimationManager().getObject("Y Normal"));

    sectionAnimationSettings_1.setCycleTime(20.0);

    sectionAnimationSettings_1.setAutoRange(false);

    sectionAnimationSettings_1.setStart(-1.0);

    AnimationDirector animationDirector_1 = 
      scene_1.getAnimationDirector();

    animationDirector_1.setFramesPerSecond(15.0);

    CurrentView currentView_1 = 
      scene_1.getCurrentView();

    Units units_0 = 
      ((Units) simulation_0.getUnitsManager().getObject("m"));

    currentView_1.getFocalPointCoordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {0.8, 0.0, 0.8}));

    currentView_1.getPositionCoordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {0.8, -5.0, 0.8}));

    currentView_1.getViewUpCoordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {0.0, 0.0, 1.0}));

    currentView_1.setInput(new DoubleVector(new double[] {0.8, 0.0, 0.8}), new DoubleVector(new double[] {0.8, -30.8059436014962, 0.8}), new DoubleVector(new double[] {0.0, 0.0, 1.0}), 1.3052619222005157, 1, 30.0);

    animationDirector_1.start();

    animationDirector_1.setIsPlaying(true);

    animationDirector_1.setIsPlaying(false);

    animationDirector_1.stop(true);
  }
}
