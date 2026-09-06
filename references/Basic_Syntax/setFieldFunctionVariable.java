// STAR-CCM+ macro: setFieldFunctionVariable.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.vis.*;

public class setFieldFunctionVariable extends StarMacro {

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

    PrimitiveFieldFunction primitiveFieldFunction_3 = 
      ((PrimitiveFieldFunction) simulation_0.getFieldFunctionManager().getFunction("UserDebyeHuckelSaturationIndex"));

    scalarDisplayer_0.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_3);
  }
}
