// batch STAR-CCM+ macro with post processing
// Set the directoryPost!
// Tyler Pierce
package macro;

import java.util.*;
import java.io.*;
import java.nio.*;
import star.flow.*;
import star.base.neo.*;   // for use with version >= 1.09
import star.base.report.*;
import star.common.*;
import star.coremodule.services.*;
import star.saturb.*;
import star.keturb.*;
import star.kwturb.*;
//import star.scenefile.*;
import star.common.StarPlot.*;
import star.energy.*;
import star.turbulence.*;
import star.walldistance.*;
import star.viewfactors.*;
import star.vis.*;
//import star.segregatedenergy.*;
//import star.segregatedflow.*;
import star.coupledflow.*;
import star.meshing.*;

public class run extends StarMacro {
   //declare global variables
   String directoryPost = "/home/aa411/2014/tyler/ss/reve/results/";
   Simulation sim;
   String currentDirectory;
   String simName;
   BufferedWriter bwout = null;
   String[] monitorPlots = {"ForceCoefficientsWindAxis","MomentCoefficientsWindAxis25"};
   String[] scenes = {"WingFenceVelocityMagnitude","WingRootVelocityMagnitude","WingTipVelocityMagnitude","YPlus3D","CenterlineVelocityMagnitude","CenterlineVelocityVectorField","PressureCoefficient3D","TurbulenceIsoSurface3D","Streamlines3D"};
   String[] forceTables = {"Accumulated Lift Table All Walls Streamwise","Accumulated Drag Table All Walls Spanwise","Accumulated Drag Table All Walls Streamwise","Accumulated Lift Table All Walls Streamwise","Accumulated Lift Table Wing Only Spanwise","Accumulated Lift Table Wing Only Streamwise"};
   
   public void execute() {
    try {
      //initialize global variables
      sim = getActiveSimulation();
      currentDirectory = sim.getSessionDir();
      simName = sim.getPresentationName();  
      bwout = new BufferedWriter(new FileWriter(resolvePath(directoryPost + simName + "_report.csv")));
      bwout.write("Report Name, Value, Unit, \n");
      
      //Mesh the current simulation if necessary
      MeshPipelineController meshPipelineController_0 = 
              sim.get(MeshPipelineController.class);

      meshPipelineController_0.generateVolumeMesh();
      
       //Get Volume Mesh:
      FvRepresentation volumeMesh =
            ((FvRepresentation) sim.getRepresentationManager().getObject("Volume Mesh"));

      // (COUPLED SOLVER ONLY): Courant number
      CoupledImplicitSolver coupledImplicitSolver_0 =
            ((CoupledImplicitSolver) sim.getSolverManager().getSolver(CoupledImplicitSolver.class));

      coupledImplicitSolver_0.setCFL($CFL);

      //Setup Alpha and Beta

      Region region_0 =
            sim.getRegionManager().getRegion("Domain");
      // Adjusts the Wind Axis Coordinate System for the AOA

      LabCoordinateSystem labCordSystm = 
            ((LabCoordinateSystem) sim.getCoordinateSystemManager().getLabCoordinateSystem());

      CartesianCoordinateSystem windAxis = 
            ((CartesianCoordinateSystem) labCordSystm.getLocalCoordinateSystemManager().getObject("WindAxis"));

      windAxis.setBasis0(new DoubleVector(new double[] {1.0, 0.0, 0.0})); //necessary for runStarccm ruby script

      // Max steps
      StepStoppingCriterion stepStopper =
            ((StepStoppingCriterion) sim.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps"));
      
      stepStopper.setMaximumNumberSteps($MaxSteps);

        // RUN until other stopping criteria are met
      sim.clearSolution();
      sim.initializeSolution(); //run grid sequencing
      SimulationIterator simIterator = sim.getSimulationIterator();
      simIterator.run();
      sim.saveState(sim.getPresentationName()+".sim");
      Collection<Report>  reports = sim.getReportManager().getObjects();
      try {
        for (Report thisReport : reports) {
          thisReport.getReportManager().applyRepresentation(volumeMesh);
          String fieldLocationName = thisReport.getPresentationName();
          Double fieldValue = thisReport.getReportMonitorValue();
          String fieldUnits = thisReport.getUnits().toString();
          // Printing to check in output window
          sim.println(" Field Location: " + fieldLocationName);
          sim.println(" Field Value: " + fieldValue);
          sim.println(" Field Units: " + fieldUnits);
          bwout.append( fieldLocationName + ", "+fieldValue+", "+fieldUnits+"\n");
          //bwout.write( fieldLocationName + ", "+fieldValue+", "+fieldUnits+"\n");
          //out.append(r.getPresentationName() + "," + r.getReportMonitorValue() + "\n");
        }
        bwout.close();
      } catch (IOException ex) {
          sim.println("Error Writing Output File!");
          sim.println(ex.getClass().getName());
      }

      //Save all plots
      //Save monitor plots
      for (String monitorPlot : monitorPlots) {
          MonitorPlot mp = ((MonitorPlot) sim.getPlotManager().getPlot(monitorPlot));
          mp.encode(directoryPost+simName+monitorPlot+".png");
      }
      //Save residual plots
      ResidualPlot residualPlot = 
            ((ResidualPlot) sim.getPlotManager().getPlot("Residuals"));
      residualPlot.encode(directoryPost+simName+residualPlot.getPresentationName()+".png");
   
      //Get vector of all scenes:
      //Collection<Scene> colSCN = sim.getSceneManager().getScenes();

      //if (!colSCN.isEmpty()) {//Make sure scenes exist
      
          for (String thisTable : forceTables) { //Save all table data out.
            AccumulatedForceTable accumulatedForceTable = 
               ((AccumulatedForceTable) sim.getTableManager().getTable(thisTable));
            accumulatedForceTable.applyRepresentation(volumeMesh);
            accumulatedForceTable.extract();
            accumulatedForceTable.export(resolvePath(directoryPost+simName+thisTable+".csv"), ",");
          }
          for (String sceName : scenes){//Save all scenes
            sim.println(" Getting Scene: "+sceName); 
      	   Scene sce = sim.getSceneManager().getSceneByName(sceName); 
	         CurrentView currentView = sce.getCurrentView();
            sce.getDisplayerManager().setRepresentation(volumeMesh);
            //for (String thisScene : scenes) { //check if collection matches string defined at top of file
                //if (sce.getPresentationName().equalsIgnoreCase(thisScene)) {
            sce.printAndWait(directoryPost+simName+sce+".png", 1);
                //} 
            //}
            if (sce.getPresentationName().endsWith("3D")) { //save 3D scene
                sce.export3DSceneFileAndWait(resolvePath(directoryPost+simName+sce+".sce"), true);
            }
        }
    //}

    } catch (Exception e) {
      sim.println("Error during analysis");
    }
    finally {
       File f = new File(currentDirectory + System.getProperty("file.separator") + "DONE");
       try {
           f.createNewFile();
       } catch (IOException ex) {
       }
   }
   }
}
