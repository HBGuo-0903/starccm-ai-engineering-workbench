## Monitoring and Reports

### Creating Reports

**Area-averaged report (inlet pressure):**
```java
AreaAverageReport report =
  simulation.getReportManager().create("star.base.report.AreaAverageReport");
report.setPresentationName("area average inlet pressure");
report.setFieldFunction(simulation.getFieldFunctionManager().getFunction("Pressure"));
report.getParts().setObjects(inletBoundary);
```

**Moment report (torque on blades):**
```java
MomentReport moment =
  simulation.getReportManager().create("star.flow.MomentReport");
moment.getParts().setObjects(bladeBoundary);
```

**DFBI translation report (heave/z-motion):**
```java
TranslationReport trans =
  simulation.getReportManager().create("star.sixdof.TranslationReport");
trans.setPresentationName("z motion");
trans.getDirection().setComponentsAndUnits(0.0, 0.0, 1.0, dimensionless);
```

**DFBI angle report (pitch):**
```java
AngleReport angle =
  simulation.getReportManager().create("star.sixdof.AngleReport");
angle.setPresentationName("y rotation");
angle.getAngleReportOption().setSelected(AngleReportOption.Type.ROTATION_Y_AXIS);
```

### Creating Monitors and Plots

```java
// Create monitor + plot from report
simulation.getMonitorManager().createMonitors(
    new ArrayList<>(Arrays.asList(report)),
    new PlotCreationInfo(PlotCreationInfo.CreatePlotChoice.SINGLE_PLOT, " 绘图"));

// Open plot interactively
Cartesian2DPlot plot =
  (Cartesian2DPlot) simulation.getPlotManager().getPlot("report_name Monitor 绘图");
plot.openInteractive();

// Set update frequency
ReportMonitor monitor =
  (ReportMonitor) simulation.getMonitorManager().getMonitor("report_name Monitor");
monitor.getStarUpdateStageable().getStarUpdate()
    .getUpdateModeOption().setSelected(StarUpdateModeOption.Type.TIMESTEP);
// Options: ITERATION (every iteration) or TIMESTEP (every time step)

// Set X-axis to physical time (transient)
PhysicalTimeMonitor physTimeMonitor =
  (PhysicalTimeMonitor) simulation.getMonitorManager().getMonitor("Physical Time");
plot.setXAxisMonitor(physTimeMonitor);
```

---
