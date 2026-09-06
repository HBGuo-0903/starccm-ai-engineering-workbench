/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimate;

import star.base.report.ExpressionReport;
import star.base.report.Report;
import star.common.Boundary;
import star.common.ConstantScalarProfileMethod;
import star.common.Simulation;
import star.common.StarMacro;
import star.common.StepStoppingCriterion;
import star.flow.MachNumberProfile;

/**
 *
 * @author aarong
 */
public class RunSecondSpeed extends StarMacro {

    Simulation sim;
    Report rep2;
    ExpressionReport rep1;
    
    @Override
    public void execute() {
        
        sim = getActiveSimulation();
        rep1 = (ExpressionReport) sim.getReportManager().getReport("Avg Cl over Cd 1");
        rep2 = sim.getReportManager().getReport("Avg Cl over Cd 2");
        
        rep1.setDefinition(Double.toString(rep2.getReportMonitorValue()));
        
        Boundary bnd = sim.getRegionManager().getRegion("Domain").getBoundaryManager().getBoundary("Domain.00 Freestream");
        MachNumberProfile prof = bnd.getValues().get(MachNumberProfile.class);
        
        prof.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(0.1);
        
        ((StepStoppingCriterion) sim.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps")).setMaximumNumberSteps(1200);
        sim.getSimulationIterator().run();
    }
    
}
