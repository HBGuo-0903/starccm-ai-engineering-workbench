
/* Write report values to a txt file
  .sim file has 5 report values
   1) Heat Transfer: Heat Transfer Report
   2) Temperature @ Point 1: Mass flow averge report
   3) Temperature @ Point 2: Mass flow averge report
   4) Temperature @ Point 3: Mass flow averge report
   5) Temperature @ Point 4: Mass flow averge report
*/

import java.io.*;
import java.util.*;
import java.lang.*; 


import star.base.report.*;
import star.common.*;
import star.energy.HeatTransferReport;
import star.flow.MassFlowAverageReport;


public class exportValuesWithReport extends StarMacro {

    
    public void execute() {
		 exportValue();	
	}
	
	public void exportValue(){
		
        Simulation simulation_0 = getActiveSimulation();
		
		/* Path and file name */
		String name = getActiveSimulation().getPresentationName();		
		String fullPath = resolvePath(name);				
		String filePath = fullPath.substring(0,fullPath.length()-name.length());
		String fileName = name;

		/* Execute simulation */
		simulation_0.getSimulationIterator().run();	
        
        Collection<Report> reports = simulation_0.getReportManager().getObjects();
        double[] reportValues = new double[reports.size()];
        
        String data = "";

        try {
			
            File f = new File(resolvePath("output.txt"));
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
	 

            for (Report rep: reports) {				
                if (rep instanceof HeatTransferReport) {
                    reportValues[0] = rep.getReportMonitorValue();
                } else if (rep instanceof MassFlowAverageReport) {
                    if (rep.getPresentationName().contains("Report 1")) {
                        reportValues[1] = rep.getReportMonitorValue();
                    } else if (rep.getPresentationName().contains("Report 2")) {
                        reportValues[2] = rep.getReportMonitorValue();
                    } else if (rep.getPresentationName().contains("Report 3")) {
                        reportValues[3] = rep.getReportMonitorValue();
                    } else if (rep.getPresentationName().contains("Report 4")) {
                        reportValues[4] = rep.getReportMonitorValue();
                    } else {
                        simulation_0.println("ERROR: unidentified report found.");
                    }
                }
            }

            // Generate output string			
			for (double value: reportValues){
				data += String.format("%.1f", value) + "\t";
			}

			/* Write to a file */
            bw.write(data);            
            bw.close();
			
        } catch (Exception ex) {
            simulation_0.println("ERROR: cannot write a file.");
        }
    }
}