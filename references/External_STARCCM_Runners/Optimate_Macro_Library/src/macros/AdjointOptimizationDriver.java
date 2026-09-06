/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import parameters.Objective;
import star.base.report.Report;
import star.common.*;
import star.coupledflow.AdjointFlowSolver;
import star.coupledflow.AdjointMeshSolver;
import star.morpher.MeshDeformationSolver;

/**
 *
 * @author aarong
 */
public class AdjointOptimizationDriver extends StarMacro {

    private final int primalIterations = 250;
    private final int adjointIterations = 350;
    private final int optimizationIterations = 10;
    
    private final String reportName = "Cl";
    private final double criticalVl = 2.3;
    private final Objective.ObjectiveGoal goal = Objective.ObjectiveGoal.MAXIMIZE;

    private Simulation _sim;
    private AdjointFlowSolver _adjFlowSolver;
    private AdjointRunnableSolver _adjRunnableSolver;
    private AdjointMeshSolver _adjMeshSolver;
    private MeshDeformationSolver _meshDefSolver;

    private List<AdjointCondition> _conditions;

    @Override
    public void execute() {

        try {
            _sim = getActiveSimulation();
            init();
            runOptimization();
        } catch (Exception ex) {
            print(ex);
        }
    }

    private void runOptimization() {
        if (conditionsSatisfied()) {
            for (int i = 1; i <= optimizationIterations; i++) {
                _sim.getSimulationIterator().step(_adjRunnableSolver, adjointIterations);
                _adjFlowSolver.computeAdjointErrorEstimates();
                _adjMeshSolver.computeMeshSensitivity();
                _meshDefSolver.deformMesh();
                
                if (i < optimizationIterations) {
                    _sim.getSimulationIterator().step(primalIterations);
                }
            }
        }
    }

    private boolean conditionsSatisfied() {
        for (AdjointCondition ac : _conditions) {
            if (!ac.isSatisfied()) {
                return false;
            }
        }

        return true;
    }

    private void init() {
        _adjFlowSolver = _sim.getSolverManager().getSolver(AdjointFlowSolver.class);
        _adjRunnableSolver = _adjFlowSolver.getAdjointRunnableSolver();
        _adjMeshSolver = _sim.getSolverManager().getSolver(AdjointMeshSolver.class);
        _meshDefSolver = _sim.getSolverManager().getSolver(MeshDeformationSolver.class);

        _conditions = new ArrayList<>();

        _conditions.add(new ConvergenceCondition());
        _conditions.add(new PerformanceCondition());

    }

    private void print(Exception ex) {
        _sim.println(ex.getLocalizedMessage());
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        _sim.println(sw.toString());
    }

    interface AdjointCondition {

        public boolean isSatisfied();
    }

    class ConvergenceCondition implements AdjointCondition {

        @Override
        public boolean isSatisfied() {
            for (SolverStoppingCriterion ci : _sim.getSolverStoppingCriterionManager().getObjects()) {
                if (isAsymptoticStoppingCriterion(ci)) {
                    if (!ci.getIsSatisfied()) {
                        return false;
                    }
                }
            }

            return true;

        }

        private boolean isAsymptoticStoppingCriterion(SolverStoppingCriterion c) {
            if (!(c instanceof MonitorIterationStoppingCriterion)) {
                return false;
            } else {
                return ((MonitorIterationStoppingCriterion) c).getCriterionType() instanceof MonitorIterationStoppingCriterionAsymptoticType;
            }
        }

    }
    
    class PerformanceCondition implements AdjointCondition {

        @Override
        public boolean isSatisfied() {
            Report rep = _sim.getReportManager().getReport(reportName);
            
            if (rep == null) {
                return false;
            }
            
            if (goal == Objective.ObjectiveGoal.MAXIMIZE) {
                return rep.getReportMonitorValue() >= criticalVl;
            } else {
                return rep.getReportMonitorValue() <= criticalVl;
            }
            
        }
        
    }

}
