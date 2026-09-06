// STAR-CCM+ macro: CreateFFT.java
// Written by STAR-CCM+ 15.02.007
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.base.report.*;
import star.vis.*;
import star.post.*;

public class CreateFFT extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    SolutionHistory solutionHistory_0 = 
      ((SolutionHistory) simulation_0.get(SolutionHistoryManager.class).getObject("RecordedVelocities"));

    solutionHistory_0.rescanFile();

    RecordedSolutionView recordedSolutionView_0 = 
      solutionHistory_0.createRecordedSolutionView(true);

    LineTimeHistoryDataSetFunction lineTimeHistoryDataSetFunction_0 = 
      (LineTimeHistoryDataSetFunction) simulation_0.get(DataSetFunctionManager.class).createLineTimeHistoryDataSetFunction();

    simulation_0.get(DerivedDataManager.class).createProbeTimeHistoryImportedModelDerivedData(lineTimeHistoryDataSetFunction_0.getImportedDerivedDataGroup());

    ProbeTimeHistoryImportedModelDerivedData probeTimeHistoryImportedModelDerivedData_0 = 
      ((ProbeTimeHistoryImportedModelDerivedData) lineTimeHistoryDataSetFunction_0.getImportedDerivedDataGroup().getObject("Multi-Point Time History"));

    SolutionRepresentation solutionRepresentation_0 = 
      ((SolutionRepresentation) simulation_0.getRepresentationManager().getObject("RecordedVelocities"));

    probeTimeHistoryImportedModelDerivedData_0.setRepresentation(solutionRepresentation_0);

    LinePart linePart_0 = 
      ((LinePart) simulation_0.getPartManager().getObject("Velocity Probe"));

    probeTimeHistoryImportedModelDerivedData_0.setPart(linePart_0);

    probeTimeHistoryImportedModelDerivedData_0.getSourceParts().setQuery(null);

    PlaneSection planeSection_0 = 
      ((PlaneSection) simulation_0.getPartManager().getObject("XZ Plane"));

    probeTimeHistoryImportedModelDerivedData_0.getSourceParts().setObjects(planeSection_0);

    solutionHistory_0.rescanFile();
  }
}
