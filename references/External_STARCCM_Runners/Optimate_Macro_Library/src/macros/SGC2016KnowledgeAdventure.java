/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import core.PostProcess;
import core.Project;
import core.SolverProcess;
import core.apiFramework.AbstractModelView;
import core.apiFramework.Exceptions.InvalidStateException;
import core.apiFramework.LicenseManager;
import core.apiFramework.OptimateAnalysis;
import core.apiFramework.OptimateProject;
import core.apiFramework.OptimateStudy;
import core.apiFramework.SimulationSettings;
import core.apiFramework.StatusMessage;
import core.listeners.ProcessStreamListener;
import core.listeners.SolverListener;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Date;
import parameters.Constraint;
import parameters.Objective;
import parameters.Response;
import parameters.var.AbstractVariable;
import star.cadmodeler.CadModel;
import star.cadmodeler.CoordinateDesignParameter;
import star.cadmodeler.SolidModelManager;
import star.common.Simulation;
import star.common.StarMacro;
import star.common.StarPlot;
import star.vis.Scene;
import workers.RunManager;

/**
 *
 * @author aarong
 */
public class SGC2016KnowledgeAdventure extends StarMacro implements ProcessStreamListener, SolverListener {

    Simulation _sim;
    OptimateProject _proj;
    Long _start;
    Long _finish;

    @Override
    public void execute() {
        _sim = getActiveSimulation();
        _proj = OptimateProject.newInstance(_sim);

        try {

            createParameters();
            createResponses();
            createModelViews();
            setUpStudy();
            runStudy();

        } catch (InvalidStateException ex) {
            for (StatusMessage sm : _proj.getStatusMessage()) {
                _sim.println(sm);
            }
            print(ex);
        } catch (Exception ex) {
            print(ex);
        }
    }

    private void createParameters() {

        CadModel cad = (CadModel) _sim.get(SolidModelManager.class).getObject("3D-CAD Model 1");

        AbstractVariable v0 = _proj.getActiveStudy().getVariableManager().createChild(cad.getDesignParameterManager().getObject("Bottom_Angle_Param"));
        v0.setOptimizationMinimum(0.0);
        v0.setBaseline(0.0);
        v0.setOptimizationMaximum(15.);
        v0.setResolution(16);

        AbstractVariable v1 = _proj.getActiveStudy().getVariableManager().createChild(cad.getDesignParameterManager().getObject("HorizontalExt"));
        v1.setOptimizationMinimum(0.25);
        v1.setBaseline(5.0);
        v1.setOptimizationMaximum(15.);
        v1.setResolution(148);

        AbstractVariable v2 = _proj.getActiveStudy().getVariableManager().createChild((CoordinateDesignParameter) cad.getDesignParameterManager().getObject("MoveVane"));
        v2.setOptimizationMinimum(-4.5);
        v2.setBaseline(0.0);
        v2.setOptimizationMaximum(4.5);
        v2.setResolution(91);

        AbstractVariable v3 = _proj.getActiveStudy().getVariableManager().getChildAt(3);
        v3.setOptimizationMinimum(-2.0);
        v3.setBaseline(0.0);
        v3.setOptimizationMaximum(2.0);
        v3.setResolution(41);

        _proj.getActiveStudy().getVariableManager().removeChildAt(3);

        AbstractVariable v4 = _proj.getActiveStudy().getVariableManager().createChild(cad.getDesignParameterManager().getObject("Thickness"));
        v4.setVariableType(AbstractVariable.VariableType.DISCRETE);
        v4.setList("1 1.25 1.5 1.625 1.75 2 2.5 3");
        v4.setBaseline(2.0);

        AbstractVariable v5 = _proj.getActiveStudy().getVariableManager().createChild(cad.getDesignParameterManager().getObject("Top_Angle_Param"));
        v5.setOptimizationMinimum(0.);
        v5.setBaseline(0.);
        v5.setOptimizationMaximum(15.);
        v5.setResolution(16);

        AbstractVariable v6 = _proj.getActiveStudy().getVariableManager().createChild(cad.getDesignParameterManager().getObject("Turning_Vane_Radius"));
        v6.setOptimizationMinimum(0.25);
        v6.setBaseline(0.25);
        v6.setOptimizationMaximum(9.);
        v6.setResolution(88);

        AbstractVariable v7 = _proj.getActiveStudy().getVariableManager().createChild(cad.getDesignParameterManager().getObject("VerticalExt"));
        v7.setOptimizationMinimum(0.25);
        v7.setBaseline(5.);
        v7.setOptimizationMaximum(15.);
        v7.setResolution(148);
    }

    private void createResponses() {
        Response r1 = _proj.getActiveStudy().getResponseManager().createChild(_sim.getReportManager().getReport("Uniformity - Velocity"));
        Objective o = _proj.getActiveStudy().getObjectiveManager().createChild(r1.getKey());
        r1.addObjective(o);
        o.setGoal(Objective.ObjectiveGoal.MAXIMIZE);

        Response r2 = _proj.getActiveStudy().getResponseManager().createChild(_sim.getReportManager().getReport("Pressure Drop"));
        Constraint c = _proj.getActiveStudy().getConstraintManager().createChild(r2.getKey());
        r2.addConstraint(c);
        c.setUpperLimit(0.011);
    }

    private void createModelViews() {
        for (Scene s : _sim.getSceneManager().getObjects()) {
            _proj.getActiveStudy().getModelViewManager().createChild(s);
        }

        for (StarPlot sp : _sim.getPlotManager().getPlots()) {
            _proj.getActiveStudy().getModelViewManager().createChild(sp);
        }

        for (AbstractModelView amv : _proj.getActiveStudy().getModelViewManager().getEnabledChildren()) {
            amv.setResolution(new Dimension(1054, 892));
        }
    }

    private void setUpStudy() {
        OptimateStudy study = _proj.getActiveStudy();
        OptimateAnalysis anal = study.getAnalysis();
        SimulationSettings sSet = study.getSimulationSettings();
        LicenseManager lMan = study.getLicenseManager();

        study.setStudyType(OptimateStudy.StudyType.OPTIMIZATION);
        study.setOptType(OptimateStudy.OptimizationType.SINGLE);
        study.setNumEvals(80);
        study.setSaveMode(Project.HeedsSaveMode.LASTBEST);

        sSet.setNumberOfCPUsPerDesign(1);
        sSet.clearSolution(false);

        anal.setExecType(OptimateAnalysis.ExecType.LOCAL);
        anal.setMaxTime(600);
        anal.setNumJobs(4);

        lMan.add(LicenseManager.CoreLicenseType.SUITE, 0);
        lMan.add(LicenseManager.ParallelLicenseType.HPC, 0);
        lMan.remove(LicenseManager.CoreLicenseType.POWERTOKEN);
        lMan.remove(LicenseManager.ParallelLicenseType.POWERTOKEN);

    }

    private void runStudy() throws Exception {
        _proj.saveAs(new File(_sim.getSessionDir() + File.separator + "optimate.optm"));
        _proj.writeInputFiles(true);
        _sim.saveState(_sim.getSessionDir() + File.separator + _sim.getPresentationName() + ".sim");
        final SolverProcess solverProc = new SolverProcess(_proj, false, SolverProcess.SolverMode.START);
        RunManager man = new RunManager(solverProc);
        man.addProcessStreamListener(this);
        man.addSolverListener(this);
        man.execute();
    }

    private void openPost() throws IOException {
        PostProcess postProc = new PostProcess(PostProcess.PostMode.OPEN_AND_LOAD, new File(_sim.getSessionDir() + File.separator + "optimate.optm"));
        postProc.start();
    }

    private void print(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        _sim.println(sw.toString());
    }

    private String elapsedTime(long start, long finish) {
        long diff = finish - start;
        long remainder = diff;
        long oneSec = 1000l;
        long oneMin = 60l * oneSec;
        long oneHrr = 60l * oneMin;
        long oneDay = 24l * oneHrr;

        String s = "";

        if (remainder > oneDay) {
            s += remainder / oneDay + "d:";
            remainder = remainder % oneDay;
        }

        if (remainder > oneHrr) {
            s += remainder / oneHrr + "h:";
            remainder = remainder % oneHrr;
        } else {
            s += "0h:";
        }

        if (remainder > oneMin) {
            s += remainder / oneMin + "m:";
            remainder = remainder % oneMin;
        } else {
            s += "0m:";
        }

        if (remainder > oneSec) {
            s += remainder / oneSec + "s";
//            remainder = remainder % oneSec;
        } else {
            s += "0s";
        }

        return s;
    }

    @Override
    public void streamUpdated(String string) {
        try {
            if (_sim != null) {
                _sim.println(string);
            }
        } catch (Exception ex) {

        }
    }

    @Override
    public void solverStarted(SolverProcess.SolverMode sm) {
        try {
            openPost();
            Date date = new Date();
            _start = date.getTime();
            if (_sim != null) {
                _sim.println("Study started: " + new Timestamp(_start));
            }
        } catch (Exception ex) {

        }
    }

    @Override
    public void solverStopped() {
        try {
            Date date = new Date();
            _finish = date.getTime();
            if (_sim != null) {
                _sim.println("Study started: " + new Timestamp(_finish));
                _sim.println("Study elapsed time: " + elapsedTime(_start, _finish));
            }
        } catch (Exception ex) {

        }
    }

    @Override
    public void solverPaused() {
        try {
            if (_sim != null) {
            }
        } catch (Exception ex) {

        }
    }

    @Override
    public void solverResumed() {
        try {
            if (_sim != null) {
            }
        } catch (Exception ex) {

        }
    }

}
