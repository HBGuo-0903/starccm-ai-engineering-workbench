// STAR-CCM+ macro: CreateDerivedParts.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class CreateDerivedParts extends StarMacro {

  public void execute() {
    execute0();
  }

  private void CreateDerivedParts() {

    Simulation simulation = 
      getActiveSimulation();

    PlaneSection planeSection_0 = 
      (PlaneSection) simulation.getPartManager().createImplicitPart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 0.0, 1.0}), new DoubleVector(new double[] {0.0, 0.0, 0.0}), 0, 1, new DoubleVector(new double[] {0.0}));

    Units units_2 = 
      simulation.getUnitsManager().getInternalUnits(new IntVector(new int[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

    planeSection_0.getOriginCoordinate().setDefinition("[0.0, 0.0, ($StatorHeight/2)] ");

    planeSection_0.setPresentationName("XY Plane");

    PlaneSection planeSection_1 = 
      (PlaneSection) simulation.getPartManager().createImplicitPart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 0.0, 1.0}), new DoubleVector(new double[] {0.0, 0.0, 0.0}), 0, 1, new DoubleVector(new double[] {0.0}));

    planeSection_1.getOrientationCoordinate().setCoordinate(units_2, units_2, units_2, new DoubleVector(new double[] {0.0, 1.0, 0.0}));

    planeSection_1.setPresentationName("XZ Plane");

    PlaneSection planeSection_2 = 
      (PlaneSection) simulation.getPartManager().createImplicitPart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 0.0, 1.0}), new DoubleVector(new double[] {0.0, 0.0, 0.0}), 0, 1, new DoubleVector(new double[] {0.0}));

    planeSection_2.getOrientationCoordinate().setCoordinate(units_2, units_2, units_2, new DoubleVector(new double[] {1.0, 0.0, 0.0}));

    planeSection_2.setPresentationName("YZ Plane");
  }
}
