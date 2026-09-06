// Simcenter STAR-CCM+ macro: rocket_macro.java
// Written by Simcenter STAR-CCM+ 18.02.008
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.meshing.*;

public class rocket_macro extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {
    Simulation simulation_0 = 
      getActiveSimulation();

    simulation_0.println("Running a macro...");

    // Set the starting base size
    ScalarGlobalParameter scalarGlobalParameter_12 = 
      ((ScalarGlobalParameter) simulation_0.get(GlobalParameterManager.class).getObject("base_size_ratio"));
    Units units_1 = 
      ((Units) simulation_0.getUnitsManager().getObject(""));
    scalarGlobalParameter_12.getQuantity().setValueAndUnits(1, units_1);

    // Run the mesher
    MeshPipelineController meshPipelineController_0 = 
      simulation_0.get(MeshPipelineController.class);
    meshPipelineController_0.generateVolumeMesh();

    // Iterate 1500 times
    StepStoppingCriterion stepStoppingCriterion_0 = 
      ((StepStoppingCriterion) simulation_0.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps"));
    IntegerValue integerValue_0 = 
      stepStoppingCriterion_0.getMaximumNumberStepsObject();
    integerValue_0.getQuantity().setValue(1500.0);
    simulation_0.getSimulationIterator().run();

    // Half the base size and mesh
    scalarGlobalParameter_12.getQuantity().setValueAndUnits(0.5, units_1);
    meshPipelineController_0.generateVolumeMesh();

    // Run another 2500 iterations
    integerValue_0.getQuantity().setValue(2500.0);
    simulation_0.getSimulationIterator().run();

    simulation_0.println("Saving...");

    simulation_0.saveState("/projects/ng_inletcfd/Training/rocket_example.sim");
  }
}
