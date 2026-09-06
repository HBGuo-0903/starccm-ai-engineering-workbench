// Simcenter STAR-CCM+ macro: PlaneSectionZ.java
// Written by Simcenter STAR-CCM+ 16.04.012
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class PlaneSectionZ extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    Units units_0 = 
      simulation_0.getUnitsManager().getPreferredUnits(Dimensions.Builder().length(1).build());

    Scene scene_0 = 
      simulation_0.getSceneManager().getScene("Scalar Scene 1");

    scene_0.setTransparencyOverrideMode(SceneTransparencyOverride.MAKE_SCENE_TRANSPARENT);

    scene_0.getCreatorGroup().setQuery(null);

    Region region_0 = 
      simulation_0.getRegionManager().getRegion("Region");

    Region region_1 = 
      simulation_0.getRegionManager().getRegion("Rad");

    scene_0.getCreatorGroup().setObjects(region_0, region_1);

    scene_0.getCreatorGroup().setQuery(null);

    scene_0.getCreatorGroup().setObjects(region_0, region_1);

    PlaneSection planeSection_5 = 
      (PlaneSection) simulation_0.getPartManager().createImplicitPart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 0.0, 1.0}), new DoubleVector(new double[] {0.0, 0.0, 0.0}), 0, 1, new DoubleVector(new double[] {0.0}));

    LabCoordinateSystem labCoordinateSystem_0 = 
      simulation_0.getCoordinateSystemManager().getLabCoordinateSystem();

    planeSection_5.setCoordinateSystem(labCoordinateSystem_0);

    planeSection_5.getInputParts().setQuery(null);

    planeSection_5.getInputParts().setObjects(region_0, region_1);

    planeSection_5.getOriginCoordinate().setUnits0(units_0);

    planeSection_5.getOriginCoordinate().setUnits1(units_0);

    planeSection_5.getOriginCoordinate().setUnits2(units_0);

    planeSection_5.getOriginCoordinate().setDefinition("");

    planeSection_5.getOriginCoordinate().setValue(new DoubleVector(new double[] {0.0, 0.0, 0.0}));

    planeSection_5.getOriginCoordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {0.0, 0.0, 0.0}));

    planeSection_5.getOriginCoordinate().setCoordinateSystem(labCoordinateSystem_0);

    planeSection_5.getOrientationCoordinate().setUnits0(units_0);

    planeSection_5.getOrientationCoordinate().setUnits1(units_0);

    planeSection_5.getOrientationCoordinate().setUnits2(units_0);

    planeSection_5.getOrientationCoordinate().setDefinition("");

    planeSection_5.getOrientationCoordinate().setValue(new DoubleVector(new double[] {0.0, 0.0, 1.0}));

    planeSection_5.getOrientationCoordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {0.0, 0.0, 1.0}));

    planeSection_5.getOrientationCoordinate().setCoordinateSystem(labCoordinateSystem_0);

    SingleValue singleValue_2 = 
      planeSection_5.getSingleValue();

    singleValue_2.getValueQuantity().setValue(0.0);

    singleValue_2.getValueQuantity().setUnits(units_0);

    RangeMultiValue rangeMultiValue_2 = 
      planeSection_5.getRangeMultiValue();

    rangeMultiValue_2.setNValues(2);

    rangeMultiValue_2.getStartQuantity().setValue(0.0);

    rangeMultiValue_2.getStartQuantity().setUnits(units_0);

    rangeMultiValue_2.getEndQuantity().setValue(1.0);

    rangeMultiValue_2.getEndQuantity().setUnits(units_0);

    DeltaMultiValue deltaMultiValue_2 = 
      planeSection_5.getDeltaMultiValue();

    deltaMultiValue_2.setNValues(2);

    deltaMultiValue_2.getStartQuantity().setValue(0.0);

    deltaMultiValue_2.getStartQuantity().setUnits(units_0);

    deltaMultiValue_2.getDeltaQuantity().setValue(1.0);

    deltaMultiValue_2.getDeltaQuantity().setUnits(units_0);

    MultiValue multiValue_2 = 
      planeSection_5.getArbitraryMultiValue();

    multiValue_2.getValueQuantities().setUnits(units_0);

    multiValue_2.getValueQuantities().setArray(new DoubleVector(new double[] {0.0}));

    planeSection_5.setValueMode(ValueMode.SINGLE);

    scene_0.setTransparencyOverrideMode(SceneTransparencyOverride.USE_DISPLAYER_PROPERTY);

    planeSection_5.setPresentationName("Z Normal");
  }
}
