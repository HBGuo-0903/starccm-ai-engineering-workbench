// STAR-CCM+ macro: AddIsoThermal.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.segregatedenergy.*;

public class AddIsoThermal extends StarMacro {

  public void execute() {
    addIsoThermal(25);
  }

  private void addIsoThermal(double TempC) {

    Simulation simulation = 
      getActiveSimulation();

    PhysicsContinuum physicsContinuum = 
      ((PhysicsContinuum) simulation.getContinuumManager().getContinuum("Physics 1"));

    physicsContinuum.enable(SegregatedFluidIsothermalModel.class);

    SegregatedFluidIsothermalModel segregatedFluidIsothermalModel_0 = 
      physicsContinuum.getModelManager().getModel(SegregatedFluidIsothermalModel.class);

    Units units_0 = 
      ((Units) simulation.getUnitsManager().getObject("C"));

    segregatedFluidIsothermalModel_0.getContinuumTemperature().setUnits(units_0);

    segregatedFluidIsothermalModel_0.getContinuumTemperature().setValue(TempC);
  }
}
