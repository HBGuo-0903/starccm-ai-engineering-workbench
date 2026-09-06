// STAR-CCM+ macro: CreateFieldFunctions.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;

public class CreateFieldFunctions extends StarMacro {

  public void execute() {
    CreateFieldFunctions();
  }

  private void CreateFieldFunctions() {

    Simulation simulation = 
      getActiveSimulation();

    UserFieldFunction KinEnergyFieldFunction = 
      simulation.getFieldFunctionManager().createFieldFunction();

    KinEnergyFieldFunction.getTypeOption().setSelected(FieldFunctionTypeOption.Type.SCALAR);

    KinEnergyFieldFunction.setPresentationName("Kinetic Energy");

    KinEnergyFieldFunction.setFunctionName("KineticEnergy");

    KinEnergyFieldFunction.setDimensions(Dimensions.Builder().length(-3).energy(1).build());

    KinEnergyFieldFunction.setDefinition("${Density}*mag2($${Velocity})");

    UserFieldFunction AngMomentumFieldFunction = 
      simulation.getFieldFunctionManager().createFieldFunction();

    AngMomentumFieldFunction.getTypeOption().setSelected(FieldFunctionTypeOption.Type.VECTOR);

    AngMomentumFieldFunction.setPresentationName("Angular Momentum");

    AngMomentumFieldFunction.setFunctionName("AngularMomentum");

    AngMomentumFieldFunction.setDimensions(Dimensions.Builder().length(-2).force(1).time(-1).build());

    AngMomentumFieldFunction.setDefinition("${Density}*cross($${Centroid}, $${Velocity})");
  }
}
