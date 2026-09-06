// Simcenter STAR-CCM+ macro: a6mesheroperations.java
// Written by Simcenter STAR-CCM+ 19.02.009


// automated mesh - base size 0.05mm -- aprox. 1 mil cells


package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.cadmodeler.*;
import star.meshing.*;

public class a6mesheroperations extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    SolidModelPart solidModelPart_0 = 
      ((SolidModelPart) simulation_0.get(SimulationPartManager.class).getPart("Body 1"));

    AutoMeshOperation autoMeshOperation_0 = 
      simulation_0.get(MeshOperationManager.class).createAutoMeshOperation(new StringVector(new String[] {"star.resurfacer.ResurfacerAutoMesher", "star.resurfacer.AutomaticSurfaceRepairAutoMesher", "star.meshing.GeometricSensitivityAutoMesher", "star.dualmesher.DualAutoMesher", "star.prismmesher.PrismAutoMesher"}), new ArrayList<>(Arrays.<GeometryPart>asList(solidModelPart_0)));

    Units units_2 = 
      ((Units) simulation_0.getUnitsManager().getObject("m"));

    autoMeshOperation_0.getDefaultValues().get(BaseSize.class).setValueAndUnits(0.05, units_2);
  }
}
