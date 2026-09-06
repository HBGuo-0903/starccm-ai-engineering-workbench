// Simcenter STAR-CCM+ macro: a1continua.java
// Written by Simcenter STAR-CCM+ 19.02.009


// PLS NOTE YOU NEED TO CREATE A CONTINUA BEFORE U RUN THE MACRO, NEW CONTINUA NEEDS TO BE NAMED PHYSICS 1

package macro;

import java.util.*;

import star.common.*;
import star.segregatedflow.*;
import star.base.neo.*;
import star.material.*;
import star.flow.*;
import star.metrics.*;
import star.mapping.*;

public class a1continua extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    PhysicsContinuum physicsContinuum_1 = 
      ((PhysicsContinuum) simulation_0.getContinuumManager().getContinuum("Physics 1"));

    physicsContinuum_1.enable(ThreeDimensionalModel.class);

    physicsContinuum_1.enable(SingleComponentLiquidModel.class);

    physicsContinuum_1.enable(SegregatedFlowModel.class);

    physicsContinuum_1.enable(ConstantDensityModel.class);

    physicsContinuum_1.enable(ImplicitUnsteadyModel.class);

    physicsContinuum_1.enable(LaminarModel.class);

    physicsContinuum_1.enable(SolutionInterpolationModel.class);

    SingleComponentLiquidModel singleComponentLiquidModel_0 = 
      physicsContinuum_1.getModelManager().getModel(SingleComponentLiquidModel.class);

    Liquid liquid_0 = 
      ((Liquid) singleComponentLiquidModel_0.getMaterial());

    liquid_0.setPresentationName("Blood");

    ConstantMaterialPropertyMethod constantMaterialPropertyMethod_0 = 
      ((ConstantMaterialPropertyMethod) liquid_0.getMaterialProperties().getMaterialProperty(DynamicViscosityProperty.class).getMethod());

    Units units_1 = 
      ((Units) simulation_0.getUnitsManager().getObject("Pa-s"));

    constantMaterialPropertyMethod_0.getQuantity().setValueAndUnits(0.004, units_1);

    ConstantMaterialPropertyMethod constantMaterialPropertyMethod_1 = 
      ((ConstantMaterialPropertyMethod) liquid_0.getMaterialProperties().getMaterialProperty(ConstantDensityProperty.class).getMethod());

    Units units_2 = 
      ((Units) simulation_0.getUnitsManager().getObject("kg/m^3"));

    constantMaterialPropertyMethod_1.getQuantity().setValueAndUnits(1000.0, units_2);
  }
}
