/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimate;

import java.util.ArrayList;
import java.util.List;
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
public class RunSpeeds extends StarMacro {

    String repPrefix = "Avg Cl over Cd ";       //String used to find the main report and expression reports
    
    double[] addConds = {0.15, 0.2, 0.25};      //additional boundary conditions to be run. In this example the design goes through
                                                //4 conitions.  1 from the sim and 3 from this macro.
    
    int solverSteps = 600;                      //solver steps required to reconverge for a new condition
    
    
    
    List<ExpressionReport> expReps;             //list of expression reports to store the report value for conditions 1 to (N-1)
    Report mainRep;                             //Main report that contains the value of interest live.
    Simulation sim;                         
    
    @Override
    public void execute() {
        
        sim = getActiveSimulation();
        
        init();
        
        //Store value for 1st conditon from sim file in the first expression report
        updateExpRepDef(expReps.get(0));
        
        //Run all but the last condition in the addConds array
        //Update the approraite expression report
        for (int i = 0; i < addConds.length - 1; i++) {
            updateCond(addConds[i]);
            run();
            updateExpRepDef(expReps.get(i));
        }
        
        //Run the last conition in the addConds array
        updateCond(addConds[addConds.length - 1]);
        run();
    }
    
    private void init() {
        expReps = new ArrayList<>();
        
        mainRep = sim.getReportManager().getReport(repPrefix + "final");
        
        for (int i = 0; i < addConds.length; i++) {
            expReps.add((ExpressionReport) sim.getReportManager().getReport(repPrefix + i));
        }
    }
    
    private void updateCond(double val) {
        Boundary bnd = sim.getRegionManager().getRegion("Domain").getBoundaryManager().getBoundary("Domain.00 Freestream");
        MachNumberProfile prof = bnd.getValues().get(MachNumberProfile.class);
        
        prof.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(val);
    }
    
    private void run() {
        int currSteps = ((StepStoppingCriterion) sim.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps")).getMaximumNumberSteps();
        
        ((StepStoppingCriterion) sim.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps")).setMaximumNumberSteps(currSteps + solverSteps);
        sim.getSimulationIterator().run();
    }
    
    private void updateExpRepDef(ExpressionReport expRep) {
        expRep.setDefinition(Double.toString(mainRep.getReportMonitorValue()));
    }
    
}
