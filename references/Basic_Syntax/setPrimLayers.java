// STAR-CCM+ macro: setPrimLayers.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.prismmesher.*;
import star.solidmesher.*;
import star.meshing.*;

public class setPrimLayers extends StarMacro {

  public void execute() {
    execute0();
  }

  private void setPrimLayers() {

    Simulation simulation_0 = 
      getActiveSimulation();

    AutoMeshOperation autoMeshOperation_0 = 
      ((AutoMeshOperation) simulation_0.get(MeshOperationManager.class).getObject("Automated Mesh"));

    ThinNumLayers thinNumLayers_0 = 
      autoMeshOperation_0.getDefaultValues().get(ThinNumLayers.class);

    thinNumLayers_0.setLayers(9);

    NumPrismLayers numPrismLayers_0 = 
      autoMeshOperation_0.getDefaultValues().get(NumPrismLayers.class);

    IntegerValue integerValue_0 = 
      numPrismLayers_0.getNumLayersValue();

    integerValue_0.getQuantity().setValue(9.0);

    PrismLayerStretching prismLayerStretching_0 = 
      autoMeshOperation_0.getDefaultValues().get(PrismLayerStretching.class);

    prismLayerStretching_0.getStretchingQuantity().setValue(1.25);

    PrismThickness prismThickness_0 = 
      autoMeshOperation_0.getDefaultValues().get(PrismThickness.class);

    ((ScalarPhysicalQuantity) prismThickness_0.getAbsoluteSizeValue()).setValue(4.0E-4);
  }
}
