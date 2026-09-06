// Simcenter STAR-CCM+ macro: PlaneSectionY.java
// Written by Simcenter STAR-CCM+ 16.04.012
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class PlaneSectionY extends StarMacro {

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

    PlaneSection planeSection_4 = 
      (PlaneSection) simulation_0.getPartManager().createImplicitPart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 0.0, 1.0}), new DoubleVector(new double[] {0.0, 0.0, 0.0}), 0, 1, new DoubleVector(new double[] {0.0}));

    LabCoordinateSystem labCoordinateSystem_0 = 
      simulation_0.getCoordinateSystemManager().getLabCoordinateSystem();

    planeSection_4.setCoordinateSystem(labCoordinateSystem_0);

    planeSection_4.getInputParts().setQuery(null);

    planeSection_4.getInputParts().setObjects(region_0, region_1);

    planeSection_4.getOriginCoordinate().setUnits0(units_0);

    planeSection_4.getOriginCoordinate().setUnits1(units_0);

    planeSection_4.getOriginCoordinate().setUnits2(units_0);

    planeSection_4.getOriginCoordinate().setDefinition("");

    planeSection_4.getOriginCoordinate().setValue(new DoubleVector(new double[] {0.0, 0.0, 0.0}));

    planeSection_4.getOriginCoordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {0.0, 0.0, 0.0}));

    planeSection_4.getOriginCoordinate().setCoordinateSystem(labCoordinateSystem_0);

    planeSection_4.getOrientationCoordinate().setUnits0(units_0);

    planeSection_4.getOrientationCoordinate().setUnits1(units_0);

    planeSection_4.getOrientationCoordinate().setUnits2(units_0);

    planeSection_4.getOrientationCoordinate().setDefinition("");

    planeSection_4.getOrientationCoordinate().setValue(new DoubleVector(new double[] {0.0, 1.0, 0.0}));

    planeSection_4.getOrientationCoordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {0.0, 1.0, 0.0}));

    planeSection_4.getOrientationCoordinate().setCoordinateSystem(labCoordinateSystem_0);

    SingleValue singleValue_1 = 
      planeSection_4.getSingleValue();

    singleValue_1.getValueQuantity().setValue(0.0);

    singleValue_1.getValueQuantity().setUnits(units_0);

    RangeMultiValue rangeMultiValue_1 = 
      planeSection_4.getRangeMultiValue();

    rangeMultiValue_1.setNValues(2);

    rangeMultiValue_1.getStartQuantity().setValue(0.0);

    rangeMultiValue_1.getStartQuantity().setUnits(units_0);

    rangeMultiValue_1.getEndQuantity().setValue(1.0);

    rangeMultiValue_1.getEndQuantity().setUnits(units_0);

    DeltaMultiValue deltaMultiValue_1 = 
      planeSection_4.getDeltaMultiValue();

    deltaMultiValue_1.setNValues(2);

    deltaMultiValue_1.getStartQuantity().setValue(0.0);

    deltaMultiValue_1.getStartQuantity().setUnits(units_0);

    deltaMultiValue_1.getDeltaQuantity().setValue(1.0);

    deltaMultiValue_1.getDeltaQuantity().setUnits(units_0);

    MultiValue multiValue_1 = 
      planeSection_4.getArbitraryMultiValue();

    multiValue_1.getValueQuantities().setUnits(units_0);

    multiValue_1.getValueQuantities().setArray(new DoubleVector(new double[] {0.0}));

    planeSection_4.setValueMode(ValueMode.SINGLE);

    scene_0.setTransparencyOverrideMode(SceneTransparencyOverride.USE_DISPLAYER_PROPERTY);

    planeSection_4.setPresentationName("Y Normal");
  }
}
