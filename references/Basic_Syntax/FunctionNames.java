// Simcenter STAR-CCM+ macro: FunctionNames.java
// Written by Simcenter STAR-CCM+ 16.04.012
package macro;

import java.util.*;

import star.common.*;
import star.vis.*;
import star.flow.*;

public class FunctionNames extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    Scene scene_2 = 
      simulation_0.getSceneManager().getScene("Scalar Scene 1");

    ScalarDisplayer scalarDisplayer_1 = 
      ((ScalarDisplayer) scene_2.getDisplayerManager().getObject("Scalar 1"));

    TotalPressureCoefficientFunction totalPressureCoefficientFunction_0 = 
      ((TotalPressureCoefficientFunction) simulation_0.getFieldFunctionManager().getFunction("TotalPressureCoefficient"));

    scalarDisplayer_1.getScalarDisplayQuantity().setFieldFunction(totalPressureCoefficientFunction_0);

    PressureCoefficientFunction pressureCoefficientFunction_0 = 
      ((PressureCoefficientFunction) simulation_0.getFieldFunctionManager().getFunction("PressureCoefficient"));

    scalarDisplayer_1.getScalarDisplayQuantity().setFieldFunction(pressureCoefficientFunction_0);

    PrimitiveFieldFunction primitiveFieldFunction_0 = 
      ((PrimitiveFieldFunction) simulation_0.getFieldFunctionManager().getFunction("TurbulentKineticEnergy"));

    scalarDisplayer_1.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_0);
  }
}
