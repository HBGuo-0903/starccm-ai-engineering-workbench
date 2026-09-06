// Simcenter STAR-CCM+ macro: make_plane.java
// Written by Simcenter STAR-CCM+ 18.04.008
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.base.report.*;
import star.flow.*;
import star.vis.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;
import java.lang.Math;

public class make_plane_sweep extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {
	  
	//inputs
	int ny = 2001; //number of slices	
	double Ymax = 2.0; // maximum y-value [m]
	double Ymin = 0.0; // minimum y-value [m]
	double Y = Ymin; // initialize counter [m]
	double X = 0.005; // x-value [m]
	double Z = 0.17; // mid z-value [m]
	
	boolean csv_flag = true;
	// end of inputs
	
	
	double dy = (Ymax - Ymin)/ny; // distance per slices

	String domain = "coolant";
	String wall = "xu [coolant/plate]";
	String outputfile;
	
	if(csv_flag == false)
		outputfile = "HTC-tb-Star.out"; 
	else
		outputfile = "HTC-tb-Star.csv";
	
	Simulation s = getActiveSimulation();
	
	// flags to ensure parts and reports are not duplicated
	boolean calcSlice = false;
	boolean calcPoint = false;
	boolean repBulkTemp = false;
	boolean repWallTemp = false;
	boolean rep_q = false;
	
	Collection<Part> myParts = s.getPartManager().getObjects();
  	  
    for (Part part : myParts) {
		String partName = part.getPresentationName();
		s.println(partName);
		if (partName.equals("calc-slice")){
			calcSlice = true;
			s.println("calc-slice plane exists"); // print to console to check

		}
		if (partName.equals("calc-point")){
			calcPoint = true;
			s.println("calc-point point exists"); // print to console to check
		}
	}
	
	if (calcSlice == false){
		PlaneSection p = (PlaneSection) s.getPartManager().createImplicitPart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), new DoubleVector(new double[] {X, Y, Z}), 0, 1, new DoubleVector(new double[] {0.0}), null);
		p.setPresentationName("calc-slice");		
	}
	PlaneSection p = ((PlaneSection) s.getPartManager().getObject("calc-slice"));
	
	Region r = s.getRegionManager().getRegion(domain);
	
	if (calcPoint == false){
		PointPart pt = s.getPartManager().createPointPart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {X, Y, Z}), null);
		pt.setPresentationName("calc-point");
	}
	PointPart pt = ((PointPart) s.getPartManager().getObject("calc-point"));
	
	
	InterfaceBoundary b = ((InterfaceBoundary) r.getBoundaryManager().getBoundary(wall));
	
	p.getInputParts().setObjects(r);	
	pt.getInputParts().setObjects(b);
	
	Collection<Report> myReports = s.getReportManager().getObjects();
	
	for (Report report : myReports) {
		String reportName = report.getPresentationName();
		s.println(reportName);
		
		if (reportName.equals("Tb-calc-slice-rep")){
			repBulkTemp = true;
			s.println("Tb-calc-slice-rep exists"); // print to console to check
		}
		
		if (reportName.equals("T-calc-point-rep")){
			repWallTemp = true;
			s.println("T-calc-point-rep exists"); // print to console to check
		}
		
		if (reportName.equals("q-calc-point-rep")){
			rep_q = true;
			s.println("q-calc-point-rep exists"); // print to console to check
		}
	}

	if (repBulkTemp == false){
		MassFlowAverageReport mf = s.getReportManager().createReport(MassFlowAverageReport.class);
		mf.setPresentationName("Tb-calc-slice-rep");
	}
	MassFlowAverageReport mf = ((MassFlowAverageReport) s.getReportManager().getReport("Tb-calc-slice-rep"));
	PrimitiveFieldFunction ff = ((PrimitiveFieldFunction) s.getFieldFunctionManager().getFunction("Temperature"));
	mf.setFieldFunction(ff);
	mf.getParts().setObjects(p);
	
	if (repWallTemp == false){
		MaxReport sr_t = s.getReportManager().createReport(MaxReport.class);
		sr_t.setPresentationName("T-calc-point-rep");
	}
	MaxReport sr_t = ((MaxReport) s.getReportManager().getReport("T-calc-point-rep"));
	sr_t.setFieldFunction(ff);
	sr_t.getParts().setObjects(pt);
	
	if (rep_q == false){
		MaxReport sr_q = s.getReportManager().createReport(MaxReport.class);
		sr_q.setPresentationName("q-calc-point-rep");
	}
	MaxReport sr_q = ((MaxReport) s.getReportManager().getReport("q-calc-point-rep"));
	PrimitiveFieldFunction ffq = ((PrimitiveFieldFunction) s.getFieldFunctionManager().getFunction("BoundaryHeatFlux"));
	sr_q.setFieldFunction(ffq);
	sr_q.getParts().setObjects(pt);
	
	
	// initialization of variables
	double T_bulk = 0.0;
	double T_wall = 0.0;
	double q_wall = 0.0;
	double h_wall = 0.0;
	
	Units u =((Units) s.getUnitsManager().getObject("m"));
	
	try {
		
		//String currentPath = new java.io.File(".").getCanonicalPath();
		//s.println("Current dir:" + currentPath);

		//String currentDir = System.getProperty("user.dir");
		//s.println("Current dir using System:" + currentDir);
		
		s.println("Writing report values to file " + resolvePath(outputfile));

		PrintWriter out = new PrintWriter(new FileWriter(new File(resolvePath(outputfile))));
		
		//out.println(reportName + " = " + reportValue + " " + reportUnits);
		if(csv_flag == false)
			out.format("%1s %s\t\t%s\t\t%s\t\t%s\t\t%s%n","#", "Y", "T_bulk", "T_wall", "q_wall", "HTC");
		else
			out.format("%s,%s,%s,%s,%s%n","Y(m)", "T_bulk(K)", "T_wall(K)", "q_wall(W/m^2)", "HTC(W/m^2-K)");
		
	
		for (int i=0;i<ny+1;i++){
			if (i == ny)
				dy = dy * 0.99999999; // if the absolute end point is use, Tb returns zero value
			else {
				Y = Ymin + dy*i; // update Y-value
				p.getOriginCoordinate().setCoordinate(u, u, u, new DoubleVector(new double[] {X, Y, Z})); //move plane
				pt.getPointCoordinate().setCoordinate(u, u, u, new DoubleVector(new double[] {X, Y, Z}));
				T_bulk = mf.getReportMonitorValue(); // get bulk temp value
				T_wall = sr_t.getReportMonitorValue(); //get wall temp sum-temperature-calc-line
				q_wall = Math.abs(sr_q.getReportMonitorValue()); //get wall heat flux value
				h_wall = q_wall / Math.abs(T_wall - T_bulk);
				
				s.println(String.format("Tbulk: %.3f, Twall: %.3f, q_wall: %.0f, HTC = %.0f", T_bulk, T_wall, q_wall, h_wall)); // print to console to check
				
				if(csv_flag == false)
					out.format("%.8f\t%.8f\t%.8f\t%.8f\t%.8f%n", Y, T_bulk, T_wall, q_wall, h_wall);
				else
					out.format("%.8f,%.8f,%.8f,%.8f,%.8f%n", Y, T_bulk, T_wall, q_wall, h_wall);
			}
		}
		out.close(); 
		
	} catch(IOException ex){
		ex.printStackTrace();
		s.println("failure"); // print to console

	}
  }
}
