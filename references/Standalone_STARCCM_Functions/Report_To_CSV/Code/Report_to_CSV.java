// STAR-CCM+ macro: Report_to_csv.java
package macro;

import java.util.*;
import java.io.*;
import java.nio.*;
import star.common.*;
import star.base.neo.*;
import star.base.report.*;
import star.flow.*;

public class Report_to_CSV extends StarMacro {

BufferedWriter bwout = null;

public void execute() {

try {

Simulation simulation_0 = getActiveSimulation();

// Collecting the simualtion file name
String simulationName = simulation_0.getPresentationName();
simulation_0.println("Simulation Name:" + simulationName);

// Open Buffered Input and Output Readers
// Creating file with name "<sim_file_name>+report.csv"
bwout = new BufferedWriter(new FileWriter(resolvePath(simulationName +"report.csv")));
bwout.write("Report Name, Value, Unit, \n");

Collection<Report> reportCollection = simulation_0.getReportManager().getObjects();

for (Report thisReport : reportCollection){

String fieldLocationName = thisReport.getPresentationName();
Double fieldValue = thisReport.getReportMonitorValue();
String fieldUnits = thisReport.getUnits().toString();

// Printing to chek in output window
simulation_0.println("Field Location :" + fieldLocationName);
simulation_0.println(" Field Value :" + fieldValue);
simulation_0.println(" Field Units :" + fieldUnits);
simulation_0.println("");

// Write Output file as "sim file name"+report.csv
bwout.write( fieldLocationName + ", " +fieldValue + ", " + fieldUnits +"\n");

}
bwout.close();

} catch (IOException iOException) {
}

}
} 