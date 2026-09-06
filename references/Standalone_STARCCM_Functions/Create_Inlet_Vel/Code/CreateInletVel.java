// STAR-CCM+ macro: CreateInletVel.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.flow.*;

public class CreateInletVel extends StarMacro {

  public void execute() {
    execute0();
  }

  private void CreateInletVelocityParameter() {

    Simulation simulation_0 = 
      getActiveSimulation();

    simulation_0.get(GlobalParameterManager.class).createGlobalParameter(ScalarGlobalParameter.class, "Scalar");

    ScalarGlobalParameter scalarGlobalParameter_1 = 
      ((ScalarGlobalParameter) simulation_0.get(GlobalParameterManager.class).getObject("Scalar"));

    scalarGlobalParameter_1.setPresentationName("InletHoleArea");

    scalarGlobalParameter_1.setDimensions(Dimensions.Builder().length(2).build());

    Units units_0 = 
      simulation_0.getUnitsManager().getInternalUnits(new IntVector(new int[] {0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

    scalarGlobalParameter_1.getQuantity().setDefinition("= $InletHoleDiameter^2/4.0");

    scalarGlobalParameter_1.getQuantity().setDefinition("= pow($InletHoleDiameter,2)/4.0");

    scalarGlobalParameter_1.getQuantity().setDefinition("= $InletHoleDiameter*$InletHoleDiameter/4.0");

    scalarGlobalParameter_1.getQuantity().setDefinition("$InletHoleDiameter*$InletHoleDiameter/4.0");

    simulation_0.get(GlobalParameterManager.class).createGlobalParameter(ScalarGlobalParameter.class, "Scalar");

    ScalarGlobalParameter scalarGlobalParameter_2 = 
      ((ScalarGlobalParameter) simulation_0.get(GlobalParameterManager.class).getObject("Scalar"));

    scalarGlobalParameter_2.setPresentationName("FlowRate");

    scalarGlobalParameter_2.setDimensions(Dimensions.Builder().length(3).time(-1).build());

    Units units_1 = 
      simulation_0.getUnitsManager().getInternalUnits(new IntVector(new int[] {0, 3, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

    scalarGlobalParameter_2.getQuantity().setDefinition("70ml/min");

    scalarGlobalParameter_2.getQuantity().setValue(1.1666666666666666E-6);

    simulation_0.get(GlobalParameterManager.class).createGlobalParameter(ScalarGlobalParameter.class, "Scalar");

    ScalarGlobalParameter scalarGlobalParameter_3 = 
      ((ScalarGlobalParameter) simulation_0.get(GlobalParameterManager.class).getObject("Scalar"));

    scalarGlobalParameter_3.setPresentationName("InletVelocity");

    scalarGlobalParameter_3.setDimensions(Dimensions.Builder().length(1).time(-1).build());

    Units units_2 = 
      simulation_0.getUnitsManager().getInternalUnits(new IntVector(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

    scalarGlobalParameter_3.getQuantity().setDefinition("$FlowRate/(2*InletHoleArea)");

    scalarGlobalParameter_3.getQuantity().setDefinition("$FlowRate/(2*$InletHoleArea)");

    Region region_0 = 
      simulation_0.getRegionManager().getRegion("Fluid");

    Boundary boundary_1 = 
      region_0.getBoundaryManager().getBoundary("InletA");

    VelocityMagnitudeProfile velocityMagnitudeProfile_0 = 
      boundary_1.getValues().get(VelocityMagnitudeProfile.class);

    velocityMagnitudeProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setDefinition("$InletVelocity");

    Boundary boundary_2 = 
      region_0.getBoundaryManager().getBoundary("InletB");

    VelocityMagnitudeProfile velocityMagnitudeProfile_1 = 
      boundary_2.getValues().get(VelocityMagnitudeProfile.class);

    velocityMagnitudeProfile_1.getMethod(ConstantScalarProfileMethod.class).getQuantity().setDefinition("$InletVelocity");
  }
}
