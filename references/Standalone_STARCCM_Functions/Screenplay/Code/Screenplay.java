// Simcenter STAR-CCM+ macro: Screenplay.java
// Written by Simcenter STAR-CCM+ 16.04.012
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.screenplay.operations.neo.*;
import star.screenplay.operations.*;
import star.common.screenplay.traits.*;
import star.screenplay.*;
import star.vis.*;

public class Screenplay extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    Scene scene_0 = 
      simulation_0.getSceneManager().getScene("Scalar Scene 1");

    star.screenplay.Screenplay screenplayScreenplay_3 = 
      simulation_0.get(ScreenplayManager.class).createScreenplay(scene_0);

    simulation_0.get(ScreenplayManager.class).setActiveScreenplay(screenplayScreenplay_3);

    screenplayScreenplay_3.openEditor();

    Region region_0 = 
      simulation_0.getRegionManager().getRegion("Region");

    Region region_1 = 
      simulation_0.getRegionManager().getRegion("Rad");

    FvRepresentation fvRepresentation_0 = 
      ((FvRepresentation) simulation_0.getRepresentationManager().getObject("Volume Mesh"));

    simulation_0.getDataSourceManager().getPartExtents(new NeoObjectVector(new Object[] {region_0, region_1}), fvRepresentation_0);

    Action action_0 = 
      screenplayScreenplay_3.getActionManager().createAction(Action.class);

    action_0.setTime(0.0);

    action_0.setRow(0);

    ScalarKeyframeSequence scalarKeyframeSequence_0 = 
      action_0.getOperationManager().createOperation(ScalarKeyframeSequence.class);

    PlaneSection planeSection_0 = 
      ((PlaneSection) simulation_0.getPartManager().getObject("X Normal"));

    SingleValue singleValue_0 = 
      planeSection_0.getSingleValue();

    ScreenplayReference screenplayReference_0 = 
      new ScreenplayReference(singleValue_0, ScreenplayProperty.SingleValue_Offset, 1);

    scalarKeyframeSequence_0.setScreenplayReferences(screenplayReference_0);

    scalarKeyframeSequence_0.setPresentationName("Offset");

    action_0.setDuration(2.0);

    scalarKeyframeSequence_0.createDefaultValues();

    ScalarKeyframe scalarKeyframe_0 = 
      ((ScalarKeyframe) scalarKeyframeSequence_0.getKeyframeManager().getKeyframe(1));

    ((ScalarPhysicalQuantity) scalarKeyframe_0.getSource()).setValue(3.0);

    Units units_0 = 
      ((Units) simulation_0.getUnitsManager().getObject("m"));

    ((ScalarPhysicalQuantity) scalarKeyframe_0.getSource()).setUnits(units_0);

    ScreenplayDirector screenplayDirector_0 = 
      screenplayScreenplay_3.getScreenplayDirector();

    screenplayDirector_0.setFramesPerSecond(50.0);

    action_0.setDuration(2.5);

    screenplayDirector_0.setIsRecording(true);

    screenplayDirector_0.record(1920, 1080, 50.0, 0.0, 3.5, resolvePath("N:\\1 - USM23 CAD\\Cooling\\CFD\\Radiator\\Rad Pitch"), 1, true, false, VideoEncodingQualityEnum.Q10);

    screenplayDirector_0.setIsRecording(false);
  }
}
