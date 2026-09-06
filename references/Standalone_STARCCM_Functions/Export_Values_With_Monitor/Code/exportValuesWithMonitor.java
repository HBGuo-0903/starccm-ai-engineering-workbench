// Use monitor values instead of report values to get an averaged value

package macro;

import java.util.*;
import java.io.*;


import star.common.*;
import star.base.neo.*;
import star.base.report.*;
import star.flow.*;


public class exportValuesWithMonitor extends StarMacro {

  public void execute() {
    exportValue();	
  }
  
  private void exportValue() {

    Simulation simulation_0 = getActiveSimulation();
		
/* Path and file name */
	String name = getActiveSimulation().getPresentationName();		
	String fullPath = resolvePath(name);				
	String filePath = fullPath.substring(0,fullPath.length()-name.length());
	String fileName = name;

/* Execute simulation */
	simulation_0.getSimulationIterator().run();	
	
/* Exporting data in CSV format (Using Report)*/	
	MonitorPlot monitorPlot_0 = ((MonitorPlot) simulation_0.getPlotManager().getPlot("Reports Plot"));    
	monitorPlot_0.export(filePath+"output.csv", ",");
	
/* Exporting averaged data in txt format (Using Monitor) */
	
	// compute the average	
	ReportMonitor reportMonitor_0 = ((ReportMonitor) simulation_0.getMonitorManager().getMonitor("Average 1 Monitor"));
	double[] monitorData = reportMonitor_0.getAllYValues();		
	float pct = 10; // % of data using for calcuating the average
	int range = (int)(pct/100*monitorData.length); // the number of data points
	
	double monitorAverage = 0;
	for(int i=monitorData.length-range;i<monitorData.length;i++){
		monitorAverage += monitorData[i]/range;	
	}
		
	simulation_0.println(monitorData); // print the average value in the console
	
	// export the average value in txt format 
	
	String outputfile = "output.txt";
	try {
		simulation_0.println("Writing values to file " + resolvePath(outputfile));
		PrintWriter out = new PrintWriter(new FileWriter(new File(resolvePath(outputfile)),true));
		out.append(minAverage + " , "+ maxAverage);
		out.close();
	} catch (IOException ex) {
	simulation_0.println(ex);
	}
  }
  	
}
  
  
  

