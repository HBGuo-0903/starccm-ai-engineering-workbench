// STAR-CCM+ macro: AssignPartsToRegion.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.meshing.*;

public class AssignPartsToRegion extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation = 
      getActiveSimulation();

    Region fluidRegion = 
      simulation.getRegionManager().getRegion("Fluid");

    fluidRegion.getPartGroup().setQuery(null);

    MeshOperationPart meshOperationPart_0 = 
      ((MeshOperationPart) simulation.get(SimulationPartManager.class).getPart("FluidDomain"));

    fluidRegion.getPartGroup().setObjects(meshOperationPart_0);

    Boundary boundary_0 = 
      fluidRegion.getBoundaryManager().getBoundary("Stator");

    boundary_0.getPartSurfaceGroup().setQuery(null);

    PartSurface partSurface_0 = 
      ((PartSurface) meshOperationPart_0.getPartSurfaceManager().getPartSurface("StatorUnit.InletA.Cylinder Surface"));

    PartSurface partSurface_1 = 
      ((PartSurface) meshOperationPart_0.getPartSurfaceManager().getPartSurface("StatorUnit.InletB.Cylinder Surface"));

    PartSurface partSurface_2 = 
      ((PartSurface) meshOperationPart_0.getPartSurfaceManager().getPartSurface("StatorUnit.OutletA.Cylinder Surface"));

    PartSurface partSurface_3 = 
      ((PartSurface) meshOperationPart_0.getPartSurfaceManager().getPartSurface("StatorUnit.OutletB.Cylinder Surface"));

    PartSurface partSurface_4 = 
      ((PartSurface) meshOperationPart_0.getPartSurfaceManager().getPartSurface("StatorUnit.Stator.Cylinder Surface"));

    boundary_0.getPartSurfaceGroup().setObjects(partSurface_0, partSurface_1, partSurface_2, partSurface_3, partSurface_4);

    Boundary boundary_1 = 
      fluidRegion.getBoundaryManager().getBoundary("Rotor");

    boundary_1.getPartSurfaceGroup().setQuery(null);

    PartSurface partSurface_5 = 
      ((PartSurface) meshOperationPart_0.getPartSurfaceManager().getPartSurface("RotorUnit.Rotor.Cylinder Surface"));

    PartSurface partSurface_6 = 
      ((PartSurface) meshOperationPart_0.getPartSurfaceManager().getPartSurface("RotorUnit.Shaft.Cylinder Surface"));

    boundary_1.getPartSurfaceGroup().setObjects(partSurface_5, partSurface_6);
  }
}
