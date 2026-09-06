// STAR-CCM+ macro: InitializeGridSequencing.java
// Recorded in 8.06.007, edited by Tyler

package macro;


import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.coupledflow.*;


public class InitializeGridSequencing extends StarMacro {


  double initialRampValue = 1.0; //Coupled Solver initial CFL value
  int rampEndIteration = 50; //Coupled Solver CFL Ramp End Iteration
  double gS_CFL = 5.0; //Grid Sequencing CFL
  int maxGSlevels = 10; //Max Number of GS levels
  int maxGSiters = 50; //Max Number of iterations per GS level
  double convGStol = 0.05; //Grid Sequencing Convergance Tolerance

  public void execute() {
    execute0();

  }


  private void execute0() {

    Simulation sim = 
      getActiveSimulation();

    CoupledImplicitSolver coupledImplicitSolver = 
      ((CoupledImplicitSolver) sim.getSolverManager().getSolver(CoupledImplicitSolver.class));

    coupledImplicitSolver.getRampCalculatorManager().getRampCalculatorOption().setSelected(RampCalculatorOption.LINEAR_RAMP);

    LinearRampCalculator linRampCalc0 = 
      ((LinearRampCalculator) coupledImplicitSolver.getRampCalculatorManager().getCalculator());

    linRampCalc0.setInitialRampValue(initialRampValue);

    linRampCalc0.setEndIteration(rampEndIteration);

    //coupledImplicitSolver.getExpertInitManager().getExpertInitOption().setSelected(ExpertInitOption.NO_METHOD);

    coupledImplicitSolver.getExpertInitManager().getExpertInitOption().setSelected(ExpertInitOption.GRID_SEQ_METHOD);

    GridSequencingInit gridSeqInit = 
      ((GridSequencingInit) coupledImplicitSolver.getExpertInitManager().getInit());

    gridSeqInit.setConvGSTol(convGStol);

    gridSeqInit.setGSCfl(gS_CFL);

    gridSeqInit.setMaxGSLevels(maxGSlevels);

    gridSeqInit.setMaxGSIterations(maxGSiters);
  }
}
