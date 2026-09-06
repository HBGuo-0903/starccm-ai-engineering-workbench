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
import star.common.Simulation;
import star.common.StarMacro;
import star.flow.MassFlowReport;

/**
 *
 * @author aarong
 */
public class FindMaxMassFlowDiff extends StarMacro {
    
    Simulation _sim;
    double _maxDiff;

    @Override
    public void execute() {
        _sim = getActiveSimulation();
        
        _maxDiff = findMaxDiff();
        
        updateExpReport();
        
    }
    
    private double findMaxDiff() {
        List<Double> list = new ArrayList<>();
        
        for (Report r : _sim.getReportManager().getObjects()) {
            if (r instanceof MassFlowReport) {
                list.add(r.getReportMonitorValue());
            }
        }
        
        double max = Double.NEGATIVE_INFINITY;
        double min = Double.POSITIVE_INFINITY;
        
        for (Double d : list) {
            if (d > max) {
                max = d;
            }
            
            if (d < min) {
                min = d;
            }
        }
        
        return Math.abs(max) - Math.abs(min);
    }
    
    private void updateExpReport() {
        for (Report r : _sim.getReportManager().getObjects()) {
            if (r instanceof ExpressionReport) {
                ((ExpressionReport) r).setDefinition(Double.toString(_maxDiff));
            }
        }
    }
    
}
