// STAR-CCM+ macro: ApplyDerivedPartToScene.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class ApplyDerivedPartToScene extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    Scene scene_0 = 
      simulation_0.getSceneManager().getScene("Saturation Rate");

    ScalarDisplayer scalarDisplayer_0 = 
      ((ScalarDisplayer) scene_0.getDisplayerManager().getDisplayer("Scalar 1"));

    scalarDisplayer_0.getInputParts().setQuery(null);

    PlaneSection planeSection_0 = 
      ((PlaneSection) simulation_0.getPartManager().getObject("XY Plane"));

    PlaneSection planeSection_1 = 
      ((PlaneSection) simulation_0.getPartManager().getObject("XZ Plane"));

    PlaneSection planeSection_2 = 
      ((PlaneSection) simulation_0.getPartManager().getObject("YZ Plane"));

    scalarDisplayer_0.getInputParts().setObjects(planeSection_0, planeSection_1, planeSection_2);
  }
}
