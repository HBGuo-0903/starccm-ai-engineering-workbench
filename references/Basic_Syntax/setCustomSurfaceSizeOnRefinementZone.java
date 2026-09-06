// STAR-CCM+ macro: setCustomSurfaceSizeOnRefinementZone.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.resurfacer.*;
import star.meshing.*;

public class setCustomSurfaceSizeOnRefinementZone extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    AutoMeshOperation autoMeshOperation_0 = 
      ((AutoMeshOperation) simulation_0.get(MeshOperationManager.class).getObject("Automated Mesh"));

    VolumeCustomMeshControl volumeCustomMeshControl_0 = 
      ((VolumeCustomMeshControl) autoMeshOperation_0.getCustomMeshControls().getObject("Volumetric Control"));

    VolumeControlResurfacerSizeOption volumeControlResurfacerSizeOption_0 = 
      volumeCustomMeshControl_0.getCustomConditions().get(VolumeControlResurfacerSizeOption.class);

    volumeControlResurfacerSizeOption_0.setVolumeControlBaseSizeOption(true);
  }
}
