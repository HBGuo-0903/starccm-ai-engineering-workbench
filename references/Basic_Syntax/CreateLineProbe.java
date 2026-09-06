// STAR-CCM+ macro: CreateLineProbe.java
// Written by STAR-CCM+ 15.02.007
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class CreateLineProbe extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    LinePart linePart_0 = 
      simulation_0.getPartManager().createLinePart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 0.0, 0.0}), new DoubleVector(new double[] {1.0, 0.0, 0.0}), 20);

    linePart_0.getInputParts().setQuery(null);

    Region region_0 = 
      simulation_0.getRegionManager().getRegion("Fluid");

    linePart_0.getInputParts().setObjects(region_0);

    Units units_0 = 
      ((Units) simulation_0.getUnitsManager().getObject("m"));

    linePart_0.getPoint1Coordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {0.0, -0.045, 0.0}));

    linePart_0.getPoint2Coordinate().setDefinition("[0.0, -0.045, ${RotorHeight}]");

    linePart_0.setResolution(300);

    linePart_0.setPresentationName("Velocity Probe");
  }
}
