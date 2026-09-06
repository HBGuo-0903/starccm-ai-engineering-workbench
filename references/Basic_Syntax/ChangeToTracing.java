// STAR-CCM+ macro: ChangeToTracing.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.segregatedflow.*;
import star.base.neo.*;
import star.segregatedenergy.*;
import star.passivescalar.*;

public class ChangeToTracing extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    PhysicsContinuum physicsContinuum_0 = 
      ((PhysicsContinuum) simulation_0.getContinuumManager().getContinuum("Physics 1"));

    SteadyModel steadyModel_0 = 
      physicsContinuum_0.getModelManager().getModel(SteadyModel.class);

    physicsContinuum_0.disableModel(steadyModel_0);

    physicsContinuum_0.enable(ImplicitUnsteadyModel.class);

    physicsContinuum_0.enable(PassiveScalarModel.class);

    physicsContinuum_0.enable(SegregatedFluidIsothermalModel.class);

    SegregatedFlowSolver segregatedFlowSolver_0 = 
      ((SegregatedFlowSolver) simulation_0.getSolverManager().getSolver(SegregatedFlowSolver.class));

    segregatedFlowSolver_0.setFreezeFlow(true);

    SegregatedFluidIsothermalModel segregatedFluidIsothermalModel_0 = 
      physicsContinuum_0.getModelManager().getModel(SegregatedFluidIsothermalModel.class);

    Units units_0 = 
      ((Units) simulation_0.getUnitsManager().getObject("C"));

    segregatedFluidIsothermalModel_0.getContinuumTemperature().setUnits(units_0);

    segregatedFluidIsothermalModel_0.getContinuumTemperature().setValue(25.0);
  }
}
