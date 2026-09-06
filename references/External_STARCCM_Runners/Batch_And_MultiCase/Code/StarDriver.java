package macro;

import java.util.*;
import star.common.*;
import star.common.StarScript;
import star.coremodule.services.*;
import star.base.neo.*;
//import star.saturb.*;
//import star.keturb.*;
import star.kwturb.*;
import star.common.StarPlot.*;
import star.base.report.*;
import star.energy.*;
import star.vis.*;
import star.flow.*;
import java.io.*;
import star.meshing.*;
import star.cadmodeler.*;
import star.coupledflow.*;
import star.motion.*;
import star.species.*;
import star.turbulence.*;

public class StarDriver extends StarMacro {

    Simulation sim;
    String scale = "ss";
    String runName = "ss/run1/";
    String directoryPOST = "/home/aa411/2014/tyler/"+runName+"results/";
    
    String wallBndrys[] = {"fence","rudder","fuselage","fuselageAft","fuselageBlend",
        "fuselageBlendTE","fuselageBlendTElarge","nose","noseTip","canardTip",
        "tailTip","wingTip","canardTE","inboardTE","outboardTE","rudderTE","canard",
        "inboard","outboard","tail"};
    String reports[] = {"CDWA","CLWA","CMWA_25","CYWA","CRWA_25","CNWA_25","AvgDensity","AvgVelocity","Drag","Lift","SideForce"};
    String plots[] = {"Residuals","forceCoefficientsWindAxis","momentCoefficientsWindAxis25","CpAlongWingInboard","CpAlongWingOutboard"};
    String scenes[] = {"geomtry","mach","mesh","meshCut","pressureCoefficient","wallShearStress","streamlines","yPlus","turbulenceIsoSurface","machIsoSurface"};

    public void execute() {

        try {
            sim = getActiveSimulation();
            String simname = sim.getPresentationName();
            
            //get iterator
            SimulationIterator simIter = sim.getSimulationIterator();
            
            //mesh 
            MeshPipelineController mPC = sim.getMeshPipelineController();
            mPC.generateVolumeMesh();
            
            //create output file
            File fileOut = new File(sim.getSessionDir() + System.getProperty("file.separator") + simname + ".out");

            //coupled solver only:
            CoupledImplicitSolver cIS = ((CoupledImplicitSolver) sim.getSolverManager().getSolver(CoupledImplicitSolver.class));
            cIS.setCFL($CFL);
            
            //set alpha and beta
            Region domain = sim.getRegionManager().getRegion("Domain");
            Boundary freestream = domain.getBoundaryManager().getBoundary("freestream");
            PhysicsContinuum pC = domain.getPhysicsContinuum();
            FlowDirectionProfile fDP = freestream.getValues().get(FlowDirectionProfile.class);
            // BE SURE TO ALWAYS START WITH (1.0, 0.0 , 0.0) FORMAT DUE TO ISSUES WITH THE runStarccm SCRIPT.
            ((ConstantVectorProfileMethod) fDP.getMethod()).getQuantity().setVector(new DoubleVector(new double[] {1.0, 0.0, 0.0}));

            // Adjusts the Wind Axis Coordinate System for the AOA

           LabCoordinateSystem lCS = ((LabCoordinateSystem) sim.getCoordinateSystemManager().getLabCoordinateSystem());

            CartesianCoordinateSystem windAxis = ((CartesianCoordinateSystem) lCS.getLocalCoordinateSystemManager().getObject("WindAxis"));

            windAxis.setBasis0(new DoubleVector(new double[] {1.0, 0.0, 0.0}));

            // Set number of steps
            StepStoppingCriterion sSC = ((StepStoppingCriterion) sim.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps"));
            sSC.setMaximumNumberSteps($MaxSteps);
            
            // Run solution
            sim.getSimulationIterator().run();
            // Save solution
            sim.saveState(sim.getPresentationName() + ".sim");
            
            // Post Processing
            //File dir = new File(directoryPOST);
            boolean dirCreated = true;
            //if (!dir.exists()) {
                //sim.println("Directory specified for post processing doesn't exist! Attempting to create...");
                //try {
                  //  dir.mkdirs();
                //} catch (Exception ex) {
                  //  sim.println("Unable to create directory! images will be saved in current directory");
                    //directoryPOST = sim.getSessionDir().toString()+System.getProperty("file.separator");        
                    //dir = new File(directoryPOST);
                //}
            //}
            try { //Save all reports
                BufferedWriter out = new BufferedWriter(new FileWriter(fileOut));
                for (String s : reports) {
                    Report r = sim.getReportManager().getReport(s);
                    r.printReport(directoryPOST, false);
                    //r.printReport(fileOut, false);
                    out.append(r.getPresentationName() + "," + r.getReportMonitorValue() + "\n");
                }
                out.close();
            } catch (Exception ex) {
                sim.println("Error Writing Output File!");
                sim.println(ex.getClass().getName());
                dirCreated = false;
            }
            if (dirCreated) {
                for (String s : plots) {
                    StarPlot plot = sim.getPlotManager().getPlot(s);
                    plot.encode(directoryPOST + plot.getPresentationName() + ".png");
                }
                for (String s : scenes) {
                    Scene scene = sim.getSceneManager().getScene(s);
                    scene.printAndWait(directoryPOST + scene.getPresentationName() + ".png", 10);
                    scene.export3DSceneFileAndWait(directoryPOST + scene.getPresentationName() + ".sce", dirCreated);
                }
            }
            
        } catch (Exception e) {
            sim.println("Error during analysis");
        }
        finally {
            File f = new File(sim.getSessionDir() + System.getProperty("file.separator") + "DONE");
            try {
                f.createNewFile();
            } catch (IOException ex) {
            }
        }
    }

}
