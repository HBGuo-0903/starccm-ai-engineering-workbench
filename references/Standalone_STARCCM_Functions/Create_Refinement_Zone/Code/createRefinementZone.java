// STAR-CCM+ macro: createRefinementZone.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.dualmesher.*;
import star.meshing.*;

public class createRefinementZone extends StarMacro {


  double StatorHeight = 140e-3;
  public void execute() {
    CreateVerticalRefinementZone(0.25*StatorHeight, 0.75*StatorHeight,0.5);
  }

  private void CreateVerticalRefinementZone(double Start, double End, double RelativeSize) {

    Simulation simulation = 
      getActiveSimulation();

    Units units_0 = 
      simulation.getUnitsManager().getPreferredUnits(new IntVector(new int[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

    MeshPartFactory meshPartFactory =   simulation.get(MeshPartFactory.class);

    SimpleBlockPart refinementBlockPart = 
      meshPartFactory.createNewBlockPart(simulation.get(SimulationPartManager.class));

    refinementBlockPart.setDoNotRetessellate(true);

    LabCoordinateSystem labCoordinateSystem_0 = 
      simulation.getCoordinateSystemManager().getLabCoordinateSystem();

    refinementBlockPart.setCoordinateSystem(labCoordinateSystem_0);

    refinementBlockPart.getCorner1().setCoordinateSystem(labCoordinateSystem_0);

    refinementBlockPart.getCorner1().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {0.0, 0.0, 0.0}));

    refinementBlockPart.getCorner2().setCoordinateSystem(labCoordinateSystem_0);

    refinementBlockPart.getCorner2().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {1.0, 1.0, 1.0}));

    refinementBlockPart.rebuildSimpleShapePart();

    refinementBlockPart.setDoNotRetessellate(false);

    refinementBlockPart.setPresentationName("RefinementZone");

    refinementBlockPart.getCorner1().setDefinition(
    "[-$StatorDiameter, -$StatorDiameter, "+ Start +"] ");

    refinementBlockPart.getCorner2().setDefinition(
    "[$StatorDiameter, $StatorDiameter, "+End+"]");

    AutoMeshOperation autoMeshOperation_0 = 
      ((AutoMeshOperation) simulation.get(MeshOperationManager.class).getObject("Automated Mesh"));

    VolumeCustomMeshControl volumeCustomMeshControl_1 = 
      autoMeshOperation_0.getCustomMeshControls().createVolumeControl();

    volumeCustomMeshControl_1.getGeometryObjects().setQuery(null);

    volumeCustomMeshControl_1.getGeometryObjects().setObjects(refinementBlockPart);

    VolumeControlDualMesherSizeOption volumeControlDualMesherSizeOption_0 = 
      volumeCustomMeshControl_1.getCustomConditions().get(VolumeControlDualMesherSizeOption.class);

    volumeControlDualMesherSizeOption_0.setVolumeControlBaseSizeOption(true);

    VolumeControlSize volumeControlSize_0 = 
      volumeCustomMeshControl_1.getCustomValues().get(VolumeControlSize.class);

    volumeControlSize_0.getRelativeSizeScalar().setValue(RelativeSize);
  }
}
