// Simcenter STAR-CCM+ macro: PlaneSections.java
// Written by Simcenter STAR-CCM+ 16.04.012
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class PlaneSections extends StarMacro {

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

    PartDisplayer partDisplayer_1 = 
      ((PartDisplayer) scene_0.getCreatorDisplayer());

    partDisplayer_1.initialize();

    scene_0.getCreatorGroup().setQuery(null);

    Region region_0 = 
      simulation_0.getRegionManager().getRegion("Region");

    Region region_1 = 
      simulation_0.getRegionManager().getRegion("Rad");

    scene_0.getCreatorGroup().setObjects(region_0, region_1);

    scene_0.getCreatorGroup().setQuery(null);

    //redundant
    scene_0.getCreatorGroup().setObjects(region_0, region_1);

	//This section creates the part
    PlaneSection planeSection_3 = 
      (PlaneSection) simulation_0.getPartManager().createImplicitPart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 0.0, 1.0}), new DoubleVector(new double[] {0.0, 0.0, 0.0}), 0, 1, new DoubleVector(new double[] {0.0}));


	//Gets the cooridoinate system used by the sim???
    LabCoordinateSystem labCoordinateSystem_0 = 
      simulation_0.getCoordinateSystemManager().getLabCoordinateSystem();

	//Sets the coordinate system for the plane section 
    planeSection_3.setCoordinateSystem(labCoordinateSystem_0);

	//Dunno?????
    planeSection_3.getInputParts().setQuery(null);

    planeSection_3.getInputParts().setObjects(region_0, region_1);

	//This section sets the units I suppose, fuck knows why it does it 3 times
    planeSection_3.getOriginCoordinate().setUnits0(units_0);

    planeSection_3.getOriginCoordinate().setUnits1(units_0);
	
	//It does it 3 times to specify the units of the 1st, 2nd and 3rd value of the coordinates, setUnits(int units[]) takes in an array of length 3, more efficient perhaps? 
    planeSection_3.getOriginCoordinate().setUnits2(units_0);

	//unsure if this is needed tbh
    planeSection_3.getOriginCoordinate().setDefinition("");

	//Origin is set to 0
    planeSection_3.getOriginCoordinate().setValue(new DoubleVector(new double[] {0.0, 0.0, 0.0}));
	
	
    planeSection_3.getOriginCoordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {0.0, 0.0, 0.0}));

    planeSection_3.getOriginCoordinate().setCoordinateSystem(labCoordinateSystem_0);

    planeSection_3.getOrientationCoordinate().setUnits0(units_0);

    planeSection_3.getOrientationCoordinate().setUnits1(units_0);

    planeSection_3.getOrientationCoordinate().setUnits2(units_0);

    planeSection_3.getOrientationCoordinate().setDefinition("");

    planeSection_3.getOrientationCoordinate().setValue(new DoubleVector(new double[] {1.0, 0.0, 0.0}));

    planeSection_3.getOrientationCoordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {1.0, 0.0, 0.0}));

    planeSection_3.getOrientationCoordinate().setCoordinateSystem(labCoordinateSystem_0);

    SingleValue singleValue_0 = 
      planeSection_3.getSingleValue();

    singleValue_0.getValueQuantity().setValue(0.0);

    singleValue_0.getValueQuantity().setUnits(units_0);

    RangeMultiValue rangeMultiValue_0 = 
      planeSection_3.getRangeMultiValue();

    rangeMultiValue_0.setNValues(2);

    rangeMultiValue_0.getStartQuantity().setValue(0.0);

    rangeMultiValue_0.getStartQuantity().setUnits(units_0);

    rangeMultiValue_0.getEndQuantity().setValue(1.0);

    rangeMultiValue_0.getEndQuantity().setUnits(units_0);

    DeltaMultiValue deltaMultiValue_0 = 
      planeSection_3.getDeltaMultiValue();

    deltaMultiValue_0.setNValues(2);

    deltaMultiValue_0.getStartQuantity().setValue(0.0);

    deltaMultiValue_0.getStartQuantity().setUnits(units_0);

    deltaMultiValue_0.getDeltaQuantity().setValue(1.0);

    deltaMultiValue_0.getDeltaQuantity().setUnits(units_0);

    MultiValue multiValue_0 = 
      planeSection_3.getArbitraryMultiValue();

    multiValue_0.getValueQuantities().setUnits(units_0);

    multiValue_0.getValueQuantities().setArray(new DoubleVector(new double[] {0.0}));

    planeSection_3.setValueMode(ValueMode.SINGLE);

    scene_0.setTransparencyOverrideMode(SceneTransparencyOverride.USE_DISPLAYER_PROPERTY);

    planeSection_3.setPresentationName("X Normal");
  }
}
