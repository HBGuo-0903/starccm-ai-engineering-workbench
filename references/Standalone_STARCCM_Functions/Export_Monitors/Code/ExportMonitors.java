// STAR-CCM+ macro: ExportMonitor.java
// Written by STAR-CCM+ 15.02.007
package macro;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import star.common.*;
import star.base.neo.*;
import star.base.report.*;

public class ExportMonitors extends StarMacro {

    public void execute() {
        ExportAllMonitors();
    }

    private void ExportAllMonitors() {
        Simulation simulation = getActiveSimulation();

        try {
            String SessionDirectory = simulation.getSessionDir();
            Files.createDirectories(Paths.get(SessionDirectory + "/monitors"));

            Collection<Monitor> monitors = simulation.getMonitorManager().getMonitors();

            for (Monitor monitor : monitors) {
                monitor.export(
                        SessionDirectory + "/monitors/" + monitor.getPresentationName().replace(" ", "") + ".csv");
            }
        } catch (Exception ex) {
            simulation.println(ex);
        }

    }
}