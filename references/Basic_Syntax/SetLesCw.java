// STAR-CCM+ macro: SetLesCw.java
// Written by STAR-CCM+ 15.02.007
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.lesturb.*;

public class SetLesCw extends StarMacro {

  public void execute() {
    setLesCw(0.35);
  }

  private void setLesCw(double Cw) {
    Simulation simulation = getActiveSimulation();
    PhysicsContinuum physicsContinuum = 
      ((PhysicsContinuum) simulation.getContinuumManager().getContinuum("Physics 1"));
    WaleSgsModel waleSgsModel = physicsContinuum.getModelManager().getModel(WaleSgsModel.class);
    waleSgsModel.setCw(Cw);
  }
}
