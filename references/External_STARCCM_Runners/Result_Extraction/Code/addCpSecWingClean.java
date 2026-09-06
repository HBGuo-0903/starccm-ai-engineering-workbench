// Simcenter STAR-CCM+ macro: addCpSec.java
// Written by Simcenter STAR-CCM+ 15.04.010
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class addCpSecWingClean extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation sim = 
      getActiveSimulation();
	  
	String simName = 
		sim.getPresentationName();

    Units unitMeas = 
      ((Units) sim.getUnitsManager().getObject("m"));
	  
	Region fluidDomain = 
      sim.getRegionManager().getRegion("Region");
    Boundary wing = 
      fluidDomain.getBoundaryManager().getBoundary("Wing");
    Boundary wingTe = 
      fluidDomain.getBoundaryManager().getBoundary("Wing_TE");
    	
    PlaneSection planeSection_0 = 
      (PlaneSection) sim.getPartManager().createImplicitPart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), new DoubleVector(new double[] {0.0, 0.0, 0.0}), 0, 1, new DoubleVector(new double[] {0.0}));
	PlaneSection planeSection_1 = 
      (PlaneSection) sim.getPartManager().createImplicitPart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), new DoubleVector(new double[] {0.0, 0.0, 0.0}), 0, 1, new DoubleVector(new double[] {0.0}));
    PlaneSection planeSection_2 = 
      (PlaneSection) sim.getPartManager().createImplicitPart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), new DoubleVector(new double[] {0.0, 0.0, 0.0}), 0, 1, new DoubleVector(new double[] {0.0}));
    PlaneSection planeSection_3 = 
      (PlaneSection) sim.getPartManager().createImplicitPart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), new DoubleVector(new double[] {0.0, 0.0, 0.0}), 0, 1, new DoubleVector(new double[] {0.0}));
    PlaneSection planeSection_4 = 
      (PlaneSection) sim.getPartManager().createImplicitPart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), new DoubleVector(new double[] {0.0, 0.0, 0.0}), 0, 1, new DoubleVector(new double[] {0.0}));
    PlaneSection planeSection_5 = 
      (PlaneSection) sim.getPartManager().createImplicitPart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), new DoubleVector(new double[] {0.0, 0.0, 0.0}), 0, 1, new DoubleVector(new double[] {0.0}));
    PlaneSection planeSection_6 = 
      (PlaneSection) sim.getPartManager().createImplicitPart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), new DoubleVector(new double[] {0.0, 0.0, 0.0}), 0, 1, new DoubleVector(new double[] {0.0}));
    PlaneSection planeSection_7 = 
      (PlaneSection) sim.getPartManager().createImplicitPart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), new DoubleVector(new double[] {0.0, 0.0, 0.0}), 0, 1, new DoubleVector(new double[] {0.0}));
    PlaneSection planeSection_8 = 
      (PlaneSection) sim.getPartManager().createImplicitPart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), new DoubleVector(new double[] {0.0, 0.0, 0.0}), 0, 1, new DoubleVector(new double[] {0.0}));
	PlaneSection planeSection_9 = 
      (PlaneSection) sim.getPartManager().createImplicitPart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), new DoubleVector(new double[] {0.0, 0.0, 0.0}), 0, 1, new DoubleVector(new double[] {0.0}));

	planeSection_0.getOriginCoordinate().setCoordinate(unitMeas, unitMeas, unitMeas, new DoubleVector(new double[] {0.0, 0.50, 0.0}));
	planeSection_1.getOriginCoordinate().setCoordinate(unitMeas, unitMeas, unitMeas, new DoubleVector(new double[] {0.0, 2.27, 0.0}));
    planeSection_2.getOriginCoordinate().setCoordinate(unitMeas, unitMeas, unitMeas, new DoubleVector(new double[] {0.0, 3.81, 0.0}));
	planeSection_3.getOriginCoordinate().setCoordinate(unitMeas, unitMeas, unitMeas, new DoubleVector(new double[] {0.0, 4.35, 0.0}));
    planeSection_4.getOriginCoordinate().setCoordinate(unitMeas, unitMeas, unitMeas, new DoubleVector(new double[] {0.0, 5.89, 0.0}));	
	planeSection_5.getOriginCoordinate().setCoordinate(unitMeas, unitMeas, unitMeas, new DoubleVector(new double[] {0.0, 6.43, 0.0}));
    planeSection_6.getOriginCoordinate().setCoordinate(unitMeas, unitMeas, unitMeas, new DoubleVector(new double[] {0.0, 7.97, 0.0}));
	planeSection_7.getOriginCoordinate().setCoordinate(unitMeas, unitMeas, unitMeas, new DoubleVector(new double[] {0.0, 8.51, 0.0}));
    planeSection_8.getOriginCoordinate().setCoordinate(unitMeas, unitMeas, unitMeas, new DoubleVector(new double[] {0.0, 10.05, 0.0}));
	planeSection_9.getOriginCoordinate().setCoordinate(unitMeas, unitMeas, unitMeas, new DoubleVector(new double[] {0.0, 11.5, 0.0}));
	
	planeSection_0.getInputParts().setObjects(wing, wingTe);
	planeSection_1.getInputParts().setObjects(wing, wingTe);
	planeSection_2.getInputParts().setObjects(wing, wingTe);
	planeSection_3.getInputParts().setObjects(wing, wingTe);
	planeSection_4.getInputParts().setObjects(wing, wingTe);
	planeSection_5.getInputParts().setObjects(wing, wingTe);
	planeSection_6.getInputParts().setObjects(wing, wingTe);
	planeSection_7.getInputParts().setObjects(wing, wingTe);
	planeSection_8.getInputParts().setObjects(wing, wingTe);
	planeSection_9.getInputParts().setObjects(wing, wingTe);
	
	planeSection_0.setPresentationName("CP_0");
    planeSection_1.setPresentationName("CP_1");
	planeSection_2.setPresentationName("CP_2");
    planeSection_3.setPresentationName("CP_3");
	planeSection_4.setPresentationName("CP_4");
    planeSection_5.setPresentationName("CP_5");
	planeSection_6.setPresentationName("CP_6");
    planeSection_7.setPresentationName("CP_7");
	planeSection_8.setPresentationName("CP_8");
    planeSection_9.setPresentationName("CP_9");


    XYPlot CpPlot = 
      ((XYPlot) sim.getPlotManager().getPlot("CP"));

    CpPlot.open();

    PlotUpdate plotUpdateCp = 
      CpPlot.getPlotUpdate();

    CpPlot.getParts().setQuery(null);

    CpPlot.getParts().setObjects(planeSection_0, planeSection_1, planeSection_2, planeSection_3, planeSection_4, planeSection_5, planeSection_6, planeSection_7, planeSection_8, planeSection_9);
    CpPlot.export(resolvePath(simName + "_Cp.csv"), ",");
  }
}
