// STAR-CCM+ macro: SetAdaptiveTimeStep.java
// Written by STAR-CCM+ 15.02.007
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.flow.*;

public class SetAdaptiveTimeStep extends StarMacro {

  public void execute() {
    EnableAdaptiveTimeStepping(10,50);
  }

  private void EnableAdaptiveTimeStepping(double getTargetMeanCfl, double getTargetMaxCfl) {

    Simulation simulation = getActiveSimulation();
    PhysicsContinuum physicsContinuum = ((PhysicsContinuum) simulation.getContinuumManager().getContinuum("Physics 1"));
    physicsContinuum.enable(AdaptiveTimeStepModel.class);

    AdaptiveTimeStepModel adaptiveTimeStepModel =  physicsContinuum.getModelManager().getModel(AdaptiveTimeStepModel.class);
    ConvectiveCflTimeStepProvider convectiveCflTimeStepProvider = adaptiveTimeStepModel.getTimeStepProviderManager().createObject(ConvectiveCflTimeStepProvider.class);

    convectiveCflTimeStepProvider.getTargetMeanCfl().setValue(getTargetMeanCfl);
    convectiveCflTimeStepProvider.getTargetMaxCfl().setValue(getTargetMaxCfl);
  }
}
