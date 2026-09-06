/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package macros;

import core.OptimateFile;
import core.OptimateMessage;
import core.Project;
import core.SolverProcess;
import core.apiFramework.AbstractModelView;
import core.apiFramework.Exceptions.DesignSetLimitExceededException;
import core.apiFramework.Exceptions.InvalidStateException;
import core.apiFramework.OptimateProject;
import core.apiFramework.OptimateStudy;
import core.listeners.OptimateProjectListener;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import parameters.Response;
import parameters.var.AbstractVariable;
import star.base.neo.ClientServerObject;
import star.base.report.Report;
import star.cadmodeler.CadModel;
import star.cadmodeler.ScalarQuantityDesignParameter;
import star.cadmodeler.SolidModelManager;
import star.common.Simulation;
import star.common.StarMacro;


/**
 *
 * @author aarong
 */
public class OptTut1AsMacro extends StarMacro implements OptimateProjectListener {
    
    Simulation sim;    
    OptimateProject project;
    

    @Override
    public void execute() {
        sim = getActiveSimulation();
       
        project = OptimateProject.newInstance(sim);
        project.addProjectListener(this);
        
        OptimateStudy study = project.getActiveStudy();
        
        study.setStudyType(OptimateStudy.StudyType.DESIGN_SWEEP);
        AbstractVariable var = study.getVariableManager().createChild(getParameterForVar());
        
        var.setOptimizationMinimum(0.08);
        var.setOptimizationMaximum(0.11);
        var.setResolution(7);
        
        Response resp = study.getResponseManager().createChild(getReportForResponse());
        
        for (ClientServerObject cso : getPlotsAndScenesForModelView()) {
            AbstractModelView mv = study.getModelViewManager().createChild(cso);
            mv.setResolution(new Dimension(1920, 1080));
        }
        
        study.getAnalysis().setNumJobs(2);
        study.getSimulationSettings().clearSolution(true);
        study.getSimulationSettings().saveSims(true);
        study.setSaveMode(Project.HeedsSaveMode.ALL);
        
        
        try {
            project.saveAs(new File(sim.getSessionDir() + File.separator + "test1.optm"));
//            project.writeInputFiles(true);
            SolverProcess sp = project.createSolverProcess(true, true, true, SolverProcess.SolverMode.START);
            sp.start();
        } catch (DesignSetLimitExceededException | IOException | InterruptedException | BackingStoreException | InvalidStateException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            sim.println(sw.toString());
        }
        
        
        
    }
    
    private ScalarQuantityDesignParameter getParameterForVar() {
        CadModel cm = (CadModel) sim.get(SolidModelManager.class).getObject("3D-CAD Model 1");
        return (ScalarQuantityDesignParameter) cm.getDesignParameterManager().getObject("depth");
    }
    
    private Report getReportForResponse() {
        return sim.getReportManager().getReport("InletPressure");
    }
    
    private List<ClientServerObject> getPlotsAndScenesForModelView() {
        List<ClientServerObject> list = new ArrayList<>();
        
        list.add(sim.getSceneManager().getSceneByName("Velocity"));
        list.add(sim.getPlotManager().getPlot("Residuals"));
        
        return list;
    }

    @Override
    public void processMessage(OptimateMessage om) {
        sim.println(om.toString());
    }

    @Override
    public void activeStudyChanged() {
    }
    
}
