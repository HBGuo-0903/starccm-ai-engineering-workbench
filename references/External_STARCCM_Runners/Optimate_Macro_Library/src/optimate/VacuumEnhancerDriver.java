/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import star.base.report.AreaAverageReport;
import star.base.report.ExpressionReport;
import star.base.report.Report;
import star.base.report.ReportManager;
import star.common.Boundary;
import star.common.ConstantScalarProfileMethod;
import star.common.PrimitiveFieldFunction;
import star.common.Region;
import star.common.Simulation;
import star.common.StagnationBoundary;
import star.common.StarMacro;
import star.common.StepStoppingCriterion;
import star.common.WallBoundary;
import star.flow.MassFlowReport;
import star.flow.StaticPressureProfile;
import star.flow.TotalPressureProfile;

/**
 *
 * @author aarong
 */
public class VacuumEnhancerDriver extends StarMacro {

    //***********************CUSTOM VARIABLES TO BE EDITED********************************
    /**
     * This array contains double values for each engine pressure operating
     * condition to be investigated.
     */
    private final double[] enginePressures = new double[]{-20000.0};
    /**
     * Number of intermediate pressures between closed and atmospheric to be run for each
     * engine condition.
     */
    private final int numIntermediatePressures = 1;
    /**
     * The number of iterations for each operating condition;
     */
    private final int iterations = 700;
    /**
     * This is the common prefix for all reports that export the pressure on the
     * port in the closed position.
     */
    private final String closedPortPrefix = "Port2Pressure_";
    /**
     * this is the common prefix for all reports that export the mass flow
     * through the port in the opened position.
     */
    private final String openedPortPrefix = "EngineMassFlow_";
    /**
     * This is the common prefix for all reports that export the time time to empty a tank.
     */
    private final String timeCalcReportPrefix = "TimeToEmpty_";
    /**
     * The name of the port boundary in the simulation file.
     */
    private final String portBoundaryName = "Secondary_Port";
    /**
     * The name of the engine intake boundary in the simulation file.
     */
    private final String engineBoundaryName = "Outlet";
    
    private final double R = 287.058; 
    private final double tankVolume = 11e-3;
    private final double tankTemperature = 293.15;
    private final double atmosphericPressure = 100000.0;
    //**************************DO NOT EDIT BELOW HERE*************************************
    
    private final HashMap<String, ExpressionReport> reportMap = new HashMap<String, ExpressionReport>();
    private double[] massFlows;
    private double[] pressures;
    private Simulation sim;
    private Boundary port;
    private Boundary engine;
    
    @Override
    public void execute() {
        boolean cont;
        sim = getActiveSimulation();

        cont = validateReports();

        if (cont) {
            cont = validateBoundaries();
        }

        if (cont) {
            evaluateDesigns();
            exportResults();
        }
    }

    /**
     * Ensures that the simulation contains a report for each design point of
     * interest and that the naming convention is respected. Specifically there
     * must be nxm + n expression reports (where n is the number of engine
     * pressure conditions and m is the number of port pressure conditions) that
     * follow the naming convention.
     *
     * The naming convention is: closedPortPrefix_0 - closedPortPrefix_n-1 and
     * openPortPrefix_0_0 - openPortPrefix_n-1_m-1
     *
     * @return True is reports can be validated otherwise false;
     */
    private boolean validateReports() {
        ReportManager man = sim.getReportManager();

        for (int i = 0; i < enginePressures.length; i++) {
            try {

                String name = closedPortPrefix + i;
                Report report = man.getReport(name);
                if (report instanceof ExpressionReport) {
                    reportMap.put(name, (ExpressionReport) report);
                } else {
                    sim.println("Error: All reports must be expression reports.");
                    return false;
                }
                
                name = timeCalcReportPrefix + i;
                report = man.getReport(name);
                if (report instanceof ExpressionReport) {
                    reportMap.put(name, (ExpressionReport) report);
                } else {
                    sim.println("Error: All reports must be expression reports.");
                    return false;
                }

                for (int j = 0; j < numIntermediatePressures + 1; j++) {

                    String name2 = openedPortPrefix + i + "_" + j;
                    Report report2 = man.getReport(name2);
                    if (report2 instanceof ExpressionReport) {
                        reportMap.put(name2, (ExpressionReport) report2);
                    } else {
                        sim.println("Error: All reports must be expression reports.");
                        return false;
                    }

                }
                
            } catch (Exception ex) {
                sim.println("Error: The sim does not contain a valid set of existing reports for this study.");
                return false;
            }

        }
        return true;
    }

    /**
     * Ensures that the port and engine boundaries can be retrieved by the
     * specified port and engine boundary names. It assumes the simulation
     * contains only one region.
     *
     * @return True if boundaries are retrieved otherwise false.
     */
    private boolean validateBoundaries() {
        Collection<Region> regs = sim.getRegionManager().getRegions();
        try {
            Region r = regs.toArray(new Region[regs.size()])[0];
            port = r.getBoundaryManager().getBoundary(portBoundaryName);
            engine = r.getBoundaryManager().getBoundary(engineBoundaryName);
            return true;
        } catch (Exception ex) {
            sim.println("Error: The sim file doesn't contain a boundary " + portBoundaryName + "\n or does not contain a boundary " + engineBoundaryName + "!");
        }
        return false;
    }

    /**
     * Systematically evaluate each closed and open operating condition and
     * updates the applicable report with the appropriate values.
     */
    private void evaluateDesigns() {
        int i = 0;
        double closedPressure;
        pressures = new double[numIntermediatePressures + 2];
        massFlows = new double[numIntermediatePressures + 2];
        
        massFlows[0] = 0.0;
        
        for (double d : enginePressures) {
            closeBoundary(d);
            closedPressure = evaluateClosedCondition(i);
            setPressures(closedPressure, pressures);
            
            int j = 0;
            for (int k = 1; k < pressures.length; k++) {
                openBoundary(pressures[k]);
                massFlows[j + 1] = evaluateOpenCondition(i, j);
                j++;
            }
            
            computeTimeToEmpty(i);
            i++;
        }
        
        
    }
    
    private void setPressures(double reference, double[] input) {
        double inc = reference / (numIntermediatePressures + 1);
        
        for (int i = 0; i < input.length - 1; i++) {
            input[i] = reference - (i * inc);
        }
        
        input[input.length - 1] = 0.0;
    }
    
    private void updatePressuresToAbsolute(double reference, double[] input) {
        for (double d : input) {
            d = d + reference;
        }
    }

    private void closeBoundary(double d) {
        port.setBoundaryType(WallBoundary.class);
        StaticPressureProfile prof = engine.getValues().get(StaticPressureProfile.class);
        prof.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(d);
    }

    private void openBoundary(double d) {
        port.setBoundaryType(StagnationBoundary.class);
        TotalPressureProfile prof = port.getValues().get(TotalPressureProfile.class);
        prof.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(d);
    }

    private double evaluateClosedCondition(int enginePressureIndex) {
        StepStoppingCriterion stop = (StepStoppingCriterion) sim.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps");
        int currSteps = stop.getMaximumNumberSteps();
        stop.setMaximumNumberSteps(currSteps + iterations);
        sim.println("Evaluating closed condition " + enginePressureIndex + ".");
        sim.getSimulationIterator().run();
        String reportName = closedPortPrefix + enginePressureIndex;
        ExpressionReport r = reportMap.get(reportName);
        double val = evaluateAreaReport(port, (PrimitiveFieldFunction) sim.getFieldFunctionManager().getFunction("Pressure"));
        r.setDefinition(Double.toString(val));
        return val;
    }

    private double evaluateOpenCondition(int enginePressureIndex, int portPressureIndex) {
        StepStoppingCriterion stop = (StepStoppingCriterion) sim.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps");
        int currSteps = stop.getMaximumNumberSteps();
        stop.setMaximumNumberSteps(currSteps + iterations);
        sim.println("Evaluating open condition " + enginePressureIndex + " " + portPressureIndex + ".");
        sim.getSimulationIterator().run();
        String reportName = openedPortPrefix + enginePressureIndex + "_" + portPressureIndex;
        ExpressionReport r = reportMap.get(reportName);
        double val = evaluateMassFlowReport(engine);
        r.setDefinition(Double.toString(val));
        return val;
    }
    
    private double computeTimeToEmpty(int enginePressureIndex) {
        String reportName = timeCalcReportPrefix + enginePressureIndex;
        ExpressionReport r = reportMap.get(reportName);
        updatePressuresToAbsolute(atmosphericPressure, pressures);
        double val = calcEmptyingTimeIsothermalEuler(sim, tankVolume, tankTemperature, R, pressures, massFlows, 0.001);
        r.setDefinition(Double.toString(val));
        return val;        
    }

    private double evaluateAreaReport(Boundary b, PrimitiveFieldFunction function) {
        AreaAverageReport rep = sim.getReportManager().createReport(AreaAverageReport.class);
        rep.setScalar(function);
        rep.getParts().setObjects(b);
        double d = rep.getReportMonitorValue();
        sim.getReportManager().removeObjects(rep);
        return d;
    }

    private double evaluateMassFlowReport(Boundary b) {
        MassFlowReport rep = sim.getReportManager().createReport(MassFlowReport.class);
        rep.getParts().setObjects(b);
        double d = rep.getReportMonitorValue();
        sim.getReportManager().removeObjects(rep);
        return d;
    }

    private void exportResults() {
        File f = new File(sim.getSessionDir() + File.separator + "results");
        BufferedWriter out = null;

        try {
            out = new BufferedWriter(new PrintWriter(f));

            for (Entry<String, ExpressionReport> entry : reportMap.entrySet()) {
                out.write(entry.getKey() + ", " + entry.getValue().getReportMonitorValue() + "\n");
            }
            out.flush();
            out.close();
        } catch (FileNotFoundException ex) {
            sim.println("Error writing results file.");
        } catch (IOException ex) {
            sim.println("Error writing results file.");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                }
            }
        }
    }
    
    private double[] interpLinear(double[] x, double[] y, double[] xi) throws IllegalArgumentException {

        if (x.length != y.length) {
            throw new IllegalArgumentException("X and Y must be the same length");
        }
        if (x.length == 1) {
            throw new IllegalArgumentException("X must contain more than one value");
        }
        double[] dx = new double[x.length - 1];
        double[] dy = new double[x.length - 1];
        double[] slope = new double[x.length - 1];
        double[] intercept = new double[x.length - 1];

        // Calculate the line equation (i.e. slope and intercept) between each point
        for (int i = 0; i < x.length - 1; i++) {
            dx[i] = x[i + 1] - x[i];
            if (dx[i] == 0) {
                throw new IllegalArgumentException("X must be montotonic. A duplicate " + "x-value was found");
            }
            if (dx[i] < 0) {
                throw new IllegalArgumentException("X must be sorted");
            }
            dy[i] = y[i + 1] - y[i];
            slope[i] = dy[i] / dx[i];
            intercept[i] = y[i] - x[i] * slope[i];
        }

        // Perform the interpolation here
        double[] yi = new double[xi.length];
        for (int i = 0; i < xi.length; i++) {
            if ((xi[i] > x[x.length - 1]) || (xi[i] < x[0])) {
                yi[i] = Double.NaN;
            }
            else {
                int loc = Arrays.binarySearch(x, xi[i]);
                if (loc < -1) {
                    loc = -loc - 2;
                    yi[i] = slope[loc] * xi[i] + intercept[loc];
                }
                else {
                    yi[i] = y[loc];
                }
            }
        }

        return yi;
    }
    
    @SuppressWarnings("UnusedAssignment")
    private double calcEmptyingTimeIsothermalEuler(Simulation simulation, double V, double T, double R, double[] pAbsTank, double[] mflowTank, double timeInc) {
        double time = 0;
        double p_i0 = pAbsTank[pAbsTank.length-1];
        double p_i1 = pAbsTank[pAbsTank.length-1];
        double mflow_p_i0 = mflowTank[pAbsTank.length-1];
        
        double[] x = new double[1];
        double[] y = new double[1];

        while (p_i0 > (pAbsTank[0])+1e-5) {
            simulation.print("time = " + time + " p_i0 = " + p_i0 +" " );

            //I got a value already for mflow_p_i, either the initial value or coming from previous interpolation.
            p_i1 = p_i0 - timeInc * ((R*T)/V) * mflow_p_i0;
            time = time + timeInc;
            //I calculate the value mflow_p_i from interpolation:
            x[0] = p_i1;
            simulation.print("x = "+x[0] + " ");            
            y = interpLinear(pAbsTank, mflowTank, x);
            simulation.println("y = "+y[0]);
            p_i0 = p_i1;
            mflow_p_i0 = y[0];
        }
        
        return time;
    } 
}
