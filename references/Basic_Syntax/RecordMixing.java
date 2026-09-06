// STAR-CCM+ macro: RecordMixing.java
// Written by STAR-CCM+ 15.02.007
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.base.report.*;
import star.flow.*;

public class RecordMixing extends StarMacro {

  public void execute() {
    execute0();
  }

  private void RecordOutletConcentrations(String Scalar) {

    Simulation simulation = 
      getActiveSimulation();

    MassFlowAverageReport massFlowAverageReport = 
      simulation.getReportManager().createReport(MassFlowAverageReport.class);

    PrimitiveFieldFunction primitiveFieldFunction = 
      ((PrimitiveFieldFunction) simulation.getFieldFunctionManager().getFunction(Scalar));

    massFlowAverageReport.setFieldFunction(primitiveFieldFunction);

    massFlowAverageReport.setPresentationName("Mass Flow Averaged Outlet Concentration of " + Scalar);

    simulation.getMonitorManager().createMonitorAndPlot(new NeoObjectVector(new Object[] {massFlowAverageReport}), true, "%1$s Plot");

    ReportMonitor reportMonitor = 
      ((ReportMonitor) simulation.getMonitorManager().getMonitor("Mass Flow Averaged Outlet Concentration of " + Scalar + " Monitor"));

    MonitorPlot monitorPlot_1 = 
      simulation.getPlotManager().createMonitorPlot(new NeoObjectVector(new Object[] {reportMonitor}), "Mass Flow Averaged Outlet Concentration of " + Scalar + " Monitor Plot");
  }
}
