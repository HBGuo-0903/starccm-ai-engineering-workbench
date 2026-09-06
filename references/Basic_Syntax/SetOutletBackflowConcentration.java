// STAR-CCM+ macro: SetOutletBackflowConcentration.java
// Written by STAR-CCM+ 15.02.007
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.passivescalar.*;

public class SetOutletBackflowConcentration extends StarMacro {

  public void execute() {
    setOutletBackflowConcentration();
  }

  private void setOutletBackflowConcentration() {

    Simulation simulation_0 = 
      getActiveSimulation();

    Region region_0 = 
      simulation_0.getRegionManager().getRegion("Fluid");

    Boundary boundary_0 = 
      region_0.getBoundaryManager().getBoundary("OutletA");

    PassiveScalarProfile passiveScalarProfile_0 = 
      boundary_0.getValues().get(PassiveScalarProfile.class);

    passiveScalarProfile_0.setMethod(CompositeArrayProfileMethod.class);

    Units units_0 = 
      simulation_0.getUnitsManager().getPreferredUnits(new IntVector(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

    ScalarProfile scalarProfile_0 = 
      passiveScalarProfile_0.getMethod(CompositeArrayProfileMethod.class).getProfile(1);

    scalarProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setDefinition("${MassFlowAveragedOutletConcentrationofSodiumReport}");

    ScalarProfile scalarProfile_1 = 
      passiveScalarProfile_0.getMethod(CompositeArrayProfileMethod.class).getProfile(0);

    scalarProfile_1.getMethod(ConstantScalarProfileMethod.class).getQuantity().setDefinition("2*${MassFlowAveragedOutletConcentrationofSodiumReport}");
  }
}
