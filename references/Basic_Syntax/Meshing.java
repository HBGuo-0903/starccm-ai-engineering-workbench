// STAR-CCM+ macro: Meshing.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.meshing.*;

public class Meshing extends StarMacro {

  public void execute() {
    execute0();
  }

  private void CreateAutomatedMeshOperation(double BaseSize) {

    Simulation simulation = 
      getActiveSimulation();

    AutoMeshOperation autoMeshOperation_0 = 
      simulation.get(MeshOperationManager.class).createAutoMeshOperation(new StringVector(new String[] {"star.resurfacer.ResurfacerAutoMesher", "star.resurfacer.AutomaticSurfaceRepairAutoMesher", "star.dualmesher.DualAutoMesher", "star.prismmesher.PrismAutoMesher", "star.solidmesher.ThinAutoMesher"}), new NeoObjectVector(new Object[] {}));

    autoMeshOperation_0.setLinkOutputPartName(false);

    autoMeshOperation_0.getDefaultValues().get(BaseSize.class).setValue(BaseSize);

    autoMeshOperation_0.getInputGeometryObjects().setQuery(null);

    MeshOperationPart meshOperationPart_0 = 
      ((MeshOperationPart) simulation.get(SimulationPartManager.class).getPart("FluidDomain"));

    autoMeshOperation_0.getInputGeometryObjects().setObjects(meshOperationPart_0);
  }
}
