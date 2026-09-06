// STAR-CCM+ macro: SetPassiveScalarInlets.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.passivescalar.*;

public class SetPassiveScalarInlets extends StarMacro {

  public void execute() {
      String[] scalars = {"yBa_2+","ySO4_2-", "yNa_1+", "yCl_1-"};
      double yNa2SO4 = 1.0 / 55.5 ;
      double yBaCl2 = (5.2e-4) / 55.5 ;
        double[] inletAConcentrations = {0.0,yNa2SO4,0.0,2*yBaCl2};
        double[] inletBConcentrations = {yBaCl2,0.0,2*yNa2SO4,0.0};
        SetPassiveScalarInlet("InletA", inletAConcentrations);
        SetPassiveScalarInlet("InletB", inletBConcentrations);
  }

  private void SetPassiveScalarInlet(String inletName, double[] inletConcentrations) {

    Simulation simulation = 
      getActiveSimulation();

    Region region = 
      simulation.getRegionManager().getRegion("Fluid");

    Boundary boundary = 
      region.getBoundaryManager().getBoundary(inletName);

    PassiveScalarProfile passiveScalarProfile = 
      boundary.getValues().get(PassiveScalarProfile.class);

    passiveScalarProfile.setMethod(CompositeArrayProfileMethod.class);

    for(int i = 0; i < inletConcentrations.length; i++)
    {
        ScalarProfile scalarProfile_0 = 
          passiveScalarProfile.getMethod(CompositeArrayProfileMethod.class).getProfile(i);

        scalarProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(inletConcentrations[i]);
        
    }

  }
}
