// Simcenter STAR-CCM+ macro: ChangeFunction.java
// Written by Simcenter STAR-CCM+ 16.04.012
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;
import star.flow.*;

public class ChangeFunction extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    Scene scene_5 = 
      simulation_0.getSceneManager().getScene("Scalar Scene 3");

    ScalarDisplayer scalarDisplayer_3 = 
      ((ScalarDisplayer) scene_5.getDisplayerManager().getObject("Scalar 2"));

    PrimitiveFieldFunction primitiveFieldFunction_0 = 
      ((PrimitiveFieldFunction) simulation_0.getFieldFunctionManager().getFunction("Velocity"));

    VectorMagnitudeFieldFunction vectorMagnitudeFieldFunction_0 = 
      ((VectorMagnitudeFieldFunction) primitiveFieldFunction_0.getMagnitudeFunction());

    scalarDisplayer_3.getScalarDisplayQuantity().setFieldFunction(vectorMagnitudeFieldFunction_0);

    ScalarDisplayer scalarDisplayer_2 = 
      ((ScalarDisplayer) scene_5.getDisplayerManager().getObject("Scalar 1"));

    scalarDisplayer_2.setVisibilityOverrideMode(DisplayerVisibilityOverride.HIDE_ALL_PARTS);

    PressureCoefficientFunction pressureCoefficientFunction_0 = 
      ((PressureCoefficientFunction) simulation_0.getFieldFunctionManager().getFunction("PressureCoefficient"));

    scalarDisplayer_3.getScalarDisplayQuantity().setFieldFunction(pressureCoefficientFunction_0);

    TotalPressureCoefficientFunction totalPressureCoefficientFunction_0 = 
      ((TotalPressureCoefficientFunction) simulation_0.getFieldFunctionManager().getFunction("TotalPressureCoefficient"));

    scalarDisplayer_3.getScalarDisplayQuantity().setFieldFunction(totalPressureCoefficientFunction_0);

    PrimitiveFieldFunction primitiveFieldFunction_1 = 
      ((PrimitiveFieldFunction) simulation_0.getFieldFunctionManager().getFunction("TurbulentKineticEnergy"));

    scalarDisplayer_3.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_1);

    CurrentView currentView_5 = 
      scene_5.getCurrentView();

    currentView_5.setInput(new DoubleVector(new double[] {1.1803714332154638, -5.362813864259152, 1.0091599697683402}), new DoubleVector(new double[] {2.39298951436073, -8.59536886951814, 0.8147902057398786}), new DoubleVector(new double[] {0.31100450805641655, 0.0596317777527153, 0.9485358438407443}), 15.795569227268112, 0, 30.0);

    currentView_5.setInput(new DoubleVector(new double[] {1.1795632368350968, -5.360297661308031, 1.0092536326296286}), new DoubleVector(new double[] {2.060353895932575, -8.704095677332942, 0.9668509355474659}), new DoubleVector(new double[] {0.3079919596376539, 0.06909553565292105, 0.9488765777230626}), 15.795569227268112, 0, 30.0);

    currentView_5.setInput(new DoubleVector(new double[] {1.1809470346145257, -5.365576541094485, 1.0092450457514492}), new DoubleVector(new double[] {2.0536441343949194, -8.711507365861044, 1.0420516600168197}), new DoubleVector(new double[] {0.30295262994536026, 0.08832107700951128, 0.9489041528864267}), 15.795569227268112, 0, 30.0);

    currentView_5.setInput(new DoubleVector(new double[] {1.1845223000577745, -5.380499243606793, 1.0101333218201483}), new DoubleVector(new double[] {1.917604684046318, -8.739759832619342, 1.3780653473669635}), new DoubleVector(new double[] {0.2844684543257385, 0.16550480951193397, 0.944291192652977}), 15.795569227268112, 0, 30.0);

    currentView_5.setInput(new DoubleVector(new double[] {2.1000772431357433, -5.160362660870833, 1.1973241574378495}), new DoubleVector(new double[] {2.8331256284718265, -8.519467455206286, 1.5652391191548158}), new DoubleVector(new double[] {0.2844684543257385, 0.16550480951193397, 0.944291192652977}), 15.795569227268112, 0, 30.0);
  }
}
