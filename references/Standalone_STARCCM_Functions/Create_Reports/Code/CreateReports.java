// STAR-CCM+ macro: CreateReports.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.base.report.*;
import star.flow.*;
import star.vis.*;

public class CreateReports extends StarMacro {

  public void execute() {
    execute0();
  }

  private void CreateReportsAndPlots() {

    Simulation simulation_0 = 
      getActiveSimulation();

    MomentReport momentReport_3 = 
      simulation_0.getReportManager().createReport(MomentReport.class);

    momentReport_3.setPresentationName("Total Moment");

    momentReport_3.getParts().setQuery(null);

    Region region_0 = 
      simulation_0.getRegionManager().getRegion("Fluid");

    Boundary boundary_0 = 
      region_0.getBoundaryManager().getBoundary("Rotor");

    Boundary boundary_1 = 
      region_0.getBoundaryManager().getBoundary("Stator");

    momentReport_3.getParts().setObjects(boundary_0, boundary_1);

    MomentReport momentReport_4 = 
      simulation_0.getReportManager().createReport(MomentReport.class);

    momentReport_4.setPresentationName("Stator Moment");

    momentReport_4.getParts().setQuery(null);

    momentReport_4.getParts().setObjects(boundary_1);

    MomentReport momentReport_5 = 
      simulation_0.getReportManager().createReport(MomentReport.class);

    momentReport_5.setPresentationName("Rotor Moment");

    momentReport_5.getParts().setQuery(null);

    momentReport_5.getParts().setObjects(boundary_0);

    VolumeIntegralReport volumeIntegralReport_0 = 
      simulation_0.getReportManager().createReport(VolumeIntegralReport.class);

    volumeIntegralReport_0.setPresentationName("Kinetic Engery");

    UserFieldFunction userFieldFunction_0 = 
      ((UserFieldFunction) simulation_0.getFieldFunctionManager().getFunction("KineticEnergy"));

    volumeIntegralReport_0.setFieldFunction(userFieldFunction_0);

    volumeIntegralReport_0.getParts().setQuery(null);

    volumeIntegralReport_0.getParts().setObjects(region_0);

    VolumeIntegralReport volumeIntegralReport_1 = 
      simulation_0.getReportManager().createReport(VolumeIntegralReport.class);

    volumeIntegralReport_1.setPresentationName("Angular Momentum Z");

    UserFieldFunction userFieldFunction_1 = 
      ((UserFieldFunction) simulation_0.getFieldFunctionManager().getFunction("AngularMomentum"));

    VectorComponentFieldFunction vectorComponentFieldFunction_0 = 
      ((VectorComponentFieldFunction) userFieldFunction_1.getComponentFunction(2));

    volumeIntegralReport_1.setFieldFunction(vectorComponentFieldFunction_0);

    volumeIntegralReport_1.getParts().setQuery(null);

    volumeIntegralReport_1.getParts().setObjects(region_0);

    ReportMonitor reportMonitor_3 = 
      volumeIntegralReport_1.createMonitor();

    ReportMonitor reportMonitor_4 = 
      volumeIntegralReport_0.createMonitor();

    ReportMonitor reportMonitor_5 = 
      momentReport_5.createMonitor();

    ReportMonitor reportMonitor_6 = 
      momentReport_4.createMonitor();

    ReportMonitor reportMonitor_7 = 
      momentReport_3.createMonitor();

    StarUpdate starUpdate_0 = 
      reportMonitor_7.getStarUpdate();

    IterationUpdateFrequency iterationUpdateFrequency_0 = 
      starUpdate_0.getIterationUpdateFrequency();

    iterationUpdateFrequency_0.setIterations(50);

    StarUpdate starUpdate_1 = 
      reportMonitor_6.getStarUpdate();

    IterationUpdateFrequency iterationUpdateFrequency_1 = 
      starUpdate_1.getIterationUpdateFrequency();

    iterationUpdateFrequency_1.setIterations(50);

    StarUpdate starUpdate_2 = 
      reportMonitor_5.getStarUpdate();

    IterationUpdateFrequency iterationUpdateFrequency_2 = 
      starUpdate_2.getIterationUpdateFrequency();

    iterationUpdateFrequency_2.setIterations(50);

    StarUpdate starUpdate_3 = 
      reportMonitor_4.getStarUpdate();

    IterationUpdateFrequency iterationUpdateFrequency_3 = 
      starUpdate_3.getIterationUpdateFrequency();

    iterationUpdateFrequency_3.setIterations(50);

    StarUpdate starUpdate_4 = 
      reportMonitor_3.getStarUpdate();

    IterationUpdateFrequency iterationUpdateFrequency_4 = 
      starUpdate_4.getIterationUpdateFrequency();

    iterationUpdateFrequency_4.setIterations(50);

    MonitorPlot monitorPlot_1 = 
      simulation_0.getPlotManager().createMonitorPlot(new NeoObjectVector(new Object[] {reportMonitor_3}), "Angular Momentum Z Monitor Plot");



    MonitorPlot monitorPlot_2 = 
      simulation_0.getPlotManager().createMonitorPlot(new NeoObjectVector(new Object[] {reportMonitor_4}), "Kinetic Engery Monitor Plot");

   

    MonitorPlot monitorPlot_3 = 
      simulation_0.getPlotManager().createMonitorPlot(new NeoObjectVector(new Object[] {reportMonitor_7}), "Total Moment Monitor Plot");

    

    monitorPlot_3.getDataSetManager().addDataProviders(new NeoObjectVector(new Object[] {reportMonitor_5, reportMonitor_6}));

    MonitorDataSet monitorDataSet_0 = 
      ((MonitorDataSet) monitorPlot_3.getDataSetManager().getDataSet("Total Moment Monitor"));

    LineStyle lineStyle_0 = 
      monitorDataSet_0.getLineStyle();

    lineStyle_0.setColor(new DoubleVector(new double[] {0.0, 0.0, 0.0}));

    MonitorDataSet monitorDataSet_1 = 
      ((MonitorDataSet) monitorPlot_3.getDataSetManager().getDataSet("Rotor Moment Monitor"));

    LineStyle lineStyle_1 = 
      monitorDataSet_1.getLineStyle();

    lineStyle_1.setColor(new DoubleVector(new double[] {0.8899999856948853, 0.07000000029802322, 0.1899999976158142}));

    monitorPlot_3.setTitle("Moments Monitor Plot");
  }
}
