// STAR-CCM+ macro: SetMassFlowInlet.java
// Written by STAR-CCM+ 15.02.007
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.flow.*;

public class SetMassFlowInlet extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    simulation_0.get(GlobalParameterManager.class).createGlobalParameter(ScalarGlobalParameter.class, "Scalar");

    ScalarGlobalParameter scalarGlobalParameter_0 = 
      ((ScalarGlobalParameter) simulation_0.get(GlobalParameterManager.class).getObject("Scalar"));

    scalarGlobalParameter_0.setPresentationName("MassFlow");

    scalarGlobalParameter_0.setDimensions(Dimensions.Builder().mass(1).time(-1).build());

    scalarGlobalParameter_0.getQuantity().setValue(1.37996E-4);

    Region region_0 = 
      simulation_0.getRegionManager().getRegion("Fluid");

    Boundary boundary_0 = 
      region_0.getBoundaryManager().getBoundary("InletA");

    MassFlowBoundary massFlowBoundary_0 = 
      ((MassFlowBoundary) simulation_0.get(ConditionTypeManager.class).get(MassFlowBoundary.class));

    boundary_0.setBoundaryType(massFlowBoundary_0);

    Boundary boundary_1 = 
      region_0.getBoundaryManager().getBoundary("InletB");

    boundary_1.setBoundaryType(massFlowBoundary_0);

    Units units_0 = 
      simulation_0.getUnitsManager().getPreferredUnits(new IntVector(new int[] {1, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

    MassFlowRateProfile massFlowRateProfile_0 = 
      boundary_0.getValues().get(MassFlowRateProfile.class);

    massFlowRateProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setDefinition("${MassFlow}");

    MassFlowRateProfile massFlowRateProfile_1 = 
      boundary_1.getValues().get(MassFlowRateProfile.class);

    massFlowRateProfile_1.getMethod(ConstantScalarProfileMethod.class).getQuantity().setDefinition("${MassFlow}");
  }
}
