// STAR-CCM+ macro: test.java
// Written by STAR-CCM+ 11.01.028
package optimate;

import star.base.report.ExpressionReport;
import star.base.report.SurfaceUniformityReport;
import star.common.*;
import star.energy.PressureDropReport;
import star.flow.*;

public class UseCase2a_RunInletConditions extends StarMacro {

    Simulation _sim;

    @Override
    public void execute() {
        _sim = getActiveSimulation();
        
        updateExpressionReports();
        updateFlowDirection();
        
        runSolver();
    }
    
    private void updateExpressionReports() {
        PressureDropReport dp2Rep = (PressureDropReport) _sim.getReportManager().getReport("Pressure Drop wo Swirl");
        ExpressionReport dp1Rep = (ExpressionReport) _sim.getReportManager().getReport("Pressure Drop w Swirl");
        
        SurfaceUniformityReport su2Rep = (SurfaceUniformityReport) _sim.getReportManager().getReport("Methanol Uniformity wo Swirl");
        ExpressionReport su1Rep = (ExpressionReport) _sim.getReportManager().getReport("Methanol Uniformity w Swirl");
        
        dp1Rep.setDefinition(Double.toString(dp2Rep.getReportMonitorValue()));
        
        su1Rep.setDefinition(Double.toString(su2Rep.getReportMonitorValue()));
        
    }

    private void updateFlowDirection() {
        Region r = _sim.getRegionManager().getRegion("Region");
        Boundary w = r.getBoundaryManager().getBoundary("Body 1.Default");
        Boundary m = r.getBoundaryManager().getBoundary("Body 1.IN");
        
        w.getValues().get(FlowDirectionProfile.class).getMethod(CompositeVectorProfileMethod.class).getProfile(0).getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(0);
        w.getValues().get(FlowDirectionProfile.class).getMethod(CompositeVectorProfileMethod.class).getProfile(1).getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(0);
        
        m.getValues().get(FlowDirectionProfile.class).getMethod(CompositeVectorProfileMethod.class).getProfile(0).getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(0);
        m.getValues().get(FlowDirectionProfile.class).getMethod(CompositeVectorProfileMethod.class).getProfile(1).getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(0);
    }
    
    private void runSolver() {
        ((StepStoppingCriterion) _sim.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps")).setMaximumNumberSteps(850);
        _sim.getSimulationIterator().run();
    }
}
