// STAR-CCM+ macro: CreateTensorFieldFunctions.java
// Written by STAR-CCM+ 15.02.007
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;

public class CreateTensorFieldFunctions extends StarMacro {

  public void execute() {
    createTensorFieldFunction("Velocity Gradient Tensor", "GradU", Dimensions.Builder().time(-1).build(), "[ grad(${MeanVelocity_0Monitor})[0],grad(${MeanVelocity_0Monitor})[1],grad(${MeanVelocity_0Monitor})[2];  grad(${MeanVelocity_1Monitor})[1],grad(${MeanVelocity_1Monitor})[2]; grad(${MeanVelocity_2Monitor})[2] ]");
  }

  private void createTensorFieldFunction(String Name, String Identifier, String Definition, Dimensions Units) {
    Simulation simulation = getActiveSimulation();
    UserFieldFunction userFieldFunction = simulation.getFieldFunctionManager().createFieldFunction();
    userFieldFunction.getTypeOption().setSelected(FieldFunctionTypeOption.Type.SYMMETRIC_TENSOR);
    userFieldFunction.setPresentationName(Name);
    userFieldFunction.setFunctionName(Identifier);
    userFieldFunction.setDimensions(Units);
    userFieldFunction.setDefinition(Definition);
  }
}
