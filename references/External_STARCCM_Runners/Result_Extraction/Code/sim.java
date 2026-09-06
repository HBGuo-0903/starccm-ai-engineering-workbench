// Simcenter STAR-CCM+ macro: sim.java
// Written by Simcenter STAR-CCM+ 17.06.008
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.meshing.*;

public class {{MACRO_NAME}} extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    AutoMeshOperation autoMeshOperation_0 = 
      ((AutoMeshOperation) simulation_0.get(MeshOperationManager.class).getObject("Automated Mesh"));

    Units units_0 = 
      ((Units) simulation_0.getUnitsManager().getObject("mm"));

    autoMeshOperation_0.getDefaultValues().get(BaseSize.class).setValueAndUnits({{MESH_SIZE}}, units_0);

    autoMeshOperation_0.execute();

    Solution solution_0 = 
      simulation_0.getSolution();

    solution_0.clearSolution(Solution.Clear.History, Solution.Clear.Fields, Solution.Clear.LagrangianDem);

    StepStoppingCriterion stepStoppingCriterion_0 = 
      ((StepStoppingCriterion) simulation_0.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps"));

    IntegerValue integerValue_0 = 
      stepStoppingCriterion_0.getMaximumNumberStepsObject();

    integerValue_0.getQuantity().setValue(500.0);

    simulation_0.getSimulationIterator().run();

    ResidualPlot residualPlot_0 = 
      ((ResidualPlot) simulation_0.getPlotManager().getPlot("Residuals"));

    residualPlot_0.openInteractive();

    PlotUpdate plotUpdate_0 = 
      residualPlot_0.getPlotUpdate();

    HardcopyProperties hardcopyProperties_0 = 
      plotUpdate_0.getHardcopyProperties();

    hardcopyProperties_0.setCurrentResolutionWidth(25);

    hardcopyProperties_0.setCurrentResolutionHeight(25);

    hardcopyProperties_0.setCurrentResolutionWidth(1426);

    hardcopyProperties_0.setCurrentResolutionHeight(589);

    Cartesian2DAxisManager cartesian2DAxisManager_0 = 
      ((Cartesian2DAxisManager) residualPlot_0.getAxisManager());

    cartesian2DAxisManager_0.setAxesBounds(new Vector(Arrays.<AxisManager.AxisBounds>asList(new AxisManager.AxisBounds("Left Axis", 1.0E-8, false, 10, false), new AxisManager.AxisBounds("Bottom Axis", 1.0, false, 1000.0, false))));

    residualPlot_0.encode(resolvePath("/nobackup/people/t0031gj/Automation/StarCCM_Automation/bend_tube_Residuals_{{CASE_NAME}}.png"), "png", 1426, 589, true, false);

    MonitorPlot monitorPlot_0 = 
      ((MonitorPlot) simulation_0.getPlotManager().getPlot("Pressure-Drop Monitor Plot"));

    Cartesian2DAxisManager cartesian2DAxisManager_1 = 
      ((Cartesian2DAxisManager) monitorPlot_0.getAxisManager());

    cartesian2DAxisManager_1.setAxesBounds(new Vector(Arrays.<AxisManager.AxisBounds>asList(new AxisManager.AxisBounds("Left Axis", -100, false, 0, false), new AxisManager.AxisBounds("Bottom Axis", 1.0, false, 1000.0, false))));

    monitorPlot_0.encode(resolvePath("/nobackup/people/t0031gj/Automation/StarCCM_Automation/bend_tube_Pressure-Drop_Monitor_Plot_{{CASE_NAME}}.png"), "png", 1426, 589, true, false);
    
    monitorPlot_0.export(resolvePath("/nobackup/people/t0031gj/Automation/StarCCM_Automation/pd_plot_{{CASE_NAME}}.csv"), ",");
  }
}
