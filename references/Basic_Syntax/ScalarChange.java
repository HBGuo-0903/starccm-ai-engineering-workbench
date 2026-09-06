// Simcenter STAR-CCM+ macro: ScalarChange.java
// Written by Simcenter STAR-CCM+ 16.04.012
package macro;

import java.util.*;

import star.common.*;
import star.vis.*;
import star.flow.*;

public class ScalarChange extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    Scene scene_0 = 
      simulation_0.getSceneManager().getScene("Scalar Scene 1");

    ScalarDisplayer scalarDisplayer_0 = 
      ((ScalarDisplayer) scene_0.getDisplayerManager().getObject("Scalar 1"));

    PressureCoefficientFunction pressureCoefficientFunction_0 = 
      ((PressureCoefficientFunction) simulation_0.getFieldFunctionManager().getFunction("PressureCoefficient"));

    scalarDisplayer_0.getScalarDisplayQuantity().setFieldFunction(pressureCoefficientFunction_0);
  }
}
