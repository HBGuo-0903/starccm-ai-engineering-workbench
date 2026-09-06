// Simcenter STAR-CCM+ macro: batch_set_sections.java
// Written by Simcenter STAR-CCM+ 17.04.008
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class batch_set_sections extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    // get initial reference section
    PlaneSection planeSection_1 = 
      ((PlaneSection) simulation_0.getPartManager().getObject("0.25"));

    // copy and set sections
    for (double prop = 0.26; prop <= 1.0; prop += 0.01){

      String prop_str = String.format("%.2f",prop);

      // build new section
      PlaneSection planeSection_3 = 
        (PlaneSection) simulation_0.getPartManager().createImplicitPart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 0.0, 1.0}), new DoubleVector(new double[] {0.0, 0.0, 0.0}), 0, 1, new DoubleVector(new double[] {0.0}));

      // set section name
      planeSection_3.setPresentationName(prop_str);

      // copy initial reference section's properties
      planeSection_3.copyProperties(planeSection_1);

      Units units_1 = 
        simulation_0.getUnitsManager().getInternalUnits(new IntVector(new int[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

      // set section origin
      planeSection_3.getOriginCoordinate().setDefinition("["+prop_str+"*${R}, 0.0, 0.0]");
    }
  }
}
