// Simcenter STAR-CCM+ macro: a5fieldfunctions.java
// Written by Simcenter STAR-CCM+ 19.02.009


// if using the CSF be cautious of 0 demoniator / infin. error occuring for certain variables 
// RRT - OSI / TA-WSS  --- ADD +0.001 denominator incase of warnings / CSF


package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;

public class a5fieldfunctions extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    UserFieldFunction userFieldFunction_0 = 
      simulation_0.getFieldFunctionManager().createFieldFunction();

    userFieldFunction_0.getTypeOption().setSelected(FieldFunctionTypeOption.Type.SCALAR);

    userFieldFunction_0.setPresentationName("number of samples");

    userFieldFunction_0.setFunctionName("n-samples");

    userFieldFunction_0.setDefinition("${Time}/${TimeStep}");

    UserFieldFunction userFieldFunction_1 = 
      simulation_0.getFieldFunctionManager().createFieldFunction();

    userFieldFunction_1.getTypeOption().setSelected(FieldFunctionTypeOption.Type.SCALAR);

    userFieldFunction_1.setPresentationName("OSI");

    Units units_3 = 
      simulation_0.getUnitsManager().getPreferredUnits(Dimensions.Builder().pressure(1).build());

    userFieldFunction_1.setDefinition("0.5*(1-((sqrt((pow(${SumWSSiMonitor},2)+pow(${SumWSSjMonitor},2)+pow(${SumWSSkMonitor},2))))/(${SumWSSMonitor})))");

    UserFieldFunction userFieldFunction_2 = 
      simulation_0.getFieldFunctionManager().createFieldFunction();

    userFieldFunction_2.getTypeOption().setSelected(FieldFunctionTypeOption.Type.SCALAR);

    userFieldFunction_2.setPresentationName("TA-WSS");

    userFieldFunction_1.setFunctionName("OSI");

    userFieldFunction_2.setFunctionName("TA-WSS");

    Units units_0 = 
      simulation_0.getUnitsManager().getPreferredUnits(Dimensions.Builder().build());

    userFieldFunction_2.setDefinition("${SumWSSMonitor}/${n-samples}");

    UserFieldFunction userFieldFunction_3 = 
      simulation_0.getFieldFunctionManager().createFieldFunction();

    userFieldFunction_3.getTypeOption().setSelected(FieldFunctionTypeOption.Type.SCALAR);

    userFieldFunction_3.setFunctionName("RRT");

    userFieldFunction_2.setDimensions(Dimensions.Builder().pressure(1).build());

    userFieldFunction_3.setPresentationName("RRT");

    userFieldFunction_3.setDimensions(Dimensions.Builder().pressure(-1).build());

    userFieldFunction_3.setDefinition("1/((1-2*${OSI})*${TA-WSS})");
  }
}
