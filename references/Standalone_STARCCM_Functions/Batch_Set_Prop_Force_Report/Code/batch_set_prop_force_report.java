// Simcenter STAR-CCM+ macro: batch_set_prop_force_report.java
// Written by Simcenter STAR-CCM+ 17.04.008
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.base.report.*;
import star.vis.*;

public class batch_set_prop_force_report extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    // get initial reference section report
    LineIntegralReport lineIntegralReport_0 = 
      ((LineIntegralReport) simulation_0.getReportManager().getReport("0.25FZ"));

    LineIntegralReport lineIntegralReport_1 = 
      ((LineIntegralReport) simulation_0.getReportManager().getReport("0.25FX"));

    // copy and set reports
    for (double prop = 0.26; prop < 1.0; prop += 0.01){

      String prop_str = String.format("%.2f", prop);

      // get section
      PlaneSection planeSection_1 = 
        ((PlaneSection) simulation_0.getPartManager().getObject(prop_str));

      // build new report
      LineIntegralReport lineIntegralReport_2 = 
        simulation_0.getReportManager().createReport(LineIntegralReport.class);

      // copy properties
      lineIntegralReport_2.copyProperties(lineIntegralReport_0);

      // set report name
      lineIntegralReport_2.setPresentationName(prop_str + "FZ");

      lineIntegralReport_2.getParts().setQuery(null);

      // set report section
      lineIntegralReport_2.getParts().setObjects(planeSection_1);

      LineIntegralReport lineIntegralReport_3 = 
        simulation_0.getReportManager().createReport(LineIntegralReport.class);

      lineIntegralReport_3.copyProperties(lineIntegralReport_1);

      lineIntegralReport_3.setPresentationName(prop_str + "FX");

      lineIntegralReport_3.getParts().setQuery(null);

      lineIntegralReport_3.getParts().setObjects(planeSection_1);
    }
  }
}
