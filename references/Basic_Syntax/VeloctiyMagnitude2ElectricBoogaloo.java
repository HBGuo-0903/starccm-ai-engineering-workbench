// Simcenter STAR-CCM+ macro: VeloctiyMagnitude2ElectricBoogaloo.java
// Written by Simcenter STAR-CCM+ 16.04.012
package macro;

import java.util.*;

import star.common.*;
import star.vis.*;

public class VeloctiyMagnitude2ElectricBoogaloo extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    Scene scene_15 = 
      simulation_0.getSceneManager().getScene("Velocity");

    ScalarDisplayer scalarDisplayer_4 = 
      ((ScalarDisplayer) scene_15.getDisplayerManager().getObject("Scalar 1"));

    PrimitiveFieldFunction primitiveFieldFunction_1 = 
      ((PrimitiveFieldFunction) simulation_0.getFieldFunctionManager().getFunction("Velocity"));

    VectorMagnitudeFieldFunction vectorMagnitudeFieldFunction_0 = 
      ((VectorMagnitudeFieldFunction) primitiveFieldFunction_1.getMagnitudeFunction());

    scalarDisplayer_4.getScalarDisplayQuantity().setFieldFunction(vectorMagnitudeFieldFunction_0);
  }
}
