 // STAR-CCM+ macro
// Written by Andrew Gunderson, December 2016
package prop;

import java.io.*;
import java.util.*;
import org.apache.poi.hslf.usermodel.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.commons.math3.stat.descriptive.*;
import com.opencsv.*;
import star.common.*;
import star.vis.*;
import star.base.neo.*;
import star.flow.*;
import star.motion.*;
import star.vof.*;
import star.meshing.*;

public class excel extends StarMacro {

    public void execute() {
        execute0();
    }

    private void execute0() {

        //----------------------------------------------------------------------
        // User inputs
        //----------------------------------------------------------------------
        // Titles, filenames, and headers
        String simTitle = "BravoI";
        String TR = "2016-1027-014";
        String propExcelFileName = "prop_data_" + simTitle + ".xls";
        String gcExcelFileName = "gc_data_" + simTitle + ".xls";
        String[] propHeaders = {"Model",
            "Speed (mph)",
            "Trim (deg)",
            "Height (in.)",
            "RPM",
            "Prop Lift (lbf)",
            "Prop Sideforce (lbf)",
            "Prop Thrust Net (lbf)",
            "Prop Thrust Normal (lbf)",
            "Prop Pitch Moment (lbf-ft)",
            "Prop Yaw Moment (lbf-ft)",
            "Prop Thrust",
            "Mean Blade Thrust (lbf)",
            "Max Blade Thrust (lbf)",
            "Min Blade Thrust (lbf)",
            "Prop Torque (lbf-ft)",
            "Mean Blade Torque (lbf-ft)",
            "Max Blade Torque",
            "Min Blade Torque",
            "SHP",
            "J",
            "KT_norm",
            "KQ_norm",
            "eta"
        };
        String[] gcHeaders = {"Model",
            "Speed (mph)",
            "Trim (deg)",
            "Height (in.)",
            "RPM",
            "Gearcase Drag (lbf",
            "Gearcase Lift (lbf)",
            "Gearcase Sideforce (lbf)",
            "Gearcase Pitch Moment (lbf-ft)",
            "Gearcase Roll Moment (lbf-ft)",
            "Gearcase Yaw Moment (lbf-ft)"
        };
        // Simulation parameters
        double[] set_speeds = {62.7, 58.6}; // mph
        double[] set_trim = {0, 5, 10}; // deg, positive is trim out
        double[] set_height = {7.19, 6.44}; // // level trim propshaft depth below water (in.)
        double[] set_rpm = {3135, 3265.5, 3396, 3526.5, 3657};
        double stepSize = 1.0; // degrees
        double Dprop = 14.95973; // prop diameter (in)
        int numToAve = (int) (360 / stepSize); // number of iterations or timesteps to average for monitor data
        int numPropCol = 19; // number of columns in prop excel ss before calculation data is added
        int numGcReports = 6; // number of GC reports
        double[] subAreaRatio = {
            0.9950,
            0.9663,
            0.8746,
            0.8184,
            0.6645,
            0.6059,};

        //----------------------------------------------------------------------
        //----------------------------------------------------------------------
        // Declare variables
        //----------------------------------------------------------------------
        String filename;
        String ImageFileName;

        double speed;
        double trim;
        double trim_rad;
        double height;
        double rpm;
        double timestep;
        double SHP;
        double J;
        double KT;
        double KQ;
        double eta;
        double KT_norm;
        double KQ_norm;

        int i;
        int j;
        int k;
        int m;
        int numSteps;
        int columnIterator;
        int rowIterator;
        int ssCount;
        int slCount;
        int reportIterator;
        int meshCount;

        FileOutputStream fileOut;
        HSLFSlideShow ppt;
        HSSFWorkbook propWB;
        HSSFWorkbook gcWB;
        HSSFSheet sheet;
        HSSFRow row;
        NPOIFSFileSystem fs;
        CSVReader reader;
        List<String[]> data;
        SummaryStatistics stats;
        HSLFSlide slide;
        HSLFTextParagraph tp;
        HSLFTextRun tr;

        Simulation sim
                = getActiveSimulation();

        try {

            // Set working directory to sim file location
            String workingDir = sim.getSessionDir() + "\\";
            // Create prop excel workbook with headers
            propWB = new HSSFWorkbook();
            sheet = propWB.createSheet("prop data");
            row = sheet.createRow(0);
            for (i = 0; i < propHeaders.length; i++) {
                row.createCell(i).setCellValue(propHeaders[i]);
            }
            fileOut = new FileOutputStream(workingDir + propExcelFileName);
            propWB.write(fileOut);
            fileOut.close();

            // Create gc excel workbook with headers
            gcWB = new HSSFWorkbook();
            sheet = gcWB.createSheet("gc data");
            row = sheet.createRow(0);
            for (i = 0; i < gcHeaders.length; i++) {
                row.createCell(i).setCellValue(gcHeaders[i]);
            }
            fileOut = new FileOutputStream(workingDir + gcExcelFileName);
            gcWB.write(fileOut);
            fileOut.close();

            // Initialize spreadsheet row count (start at 1 to skip header row)
            ssCount = 1;
            // ---------------------------------------------------------------------
            // Loop through speeds
            //----------------------------------------------------------------------
            for (i = 0; i < set_speeds.length; i++) {
                speed = set_speeds[i];
                // Initialize number of meshes generated
                meshCount = -1;

                //------------------------------------------------------------------
                // Loop through all the trim angles
                //------------------------------------------------------------------
                for (j = 0; j < set_trim.length; j++) {
                    trim = set_trim[j];

                    //--------------------------------------------------------------
                    // Loop through all the heights
                    //--------------------------------------------------------------
                    for (k = 0; k < set_height.length; k++) {
                        height = set_height[k];
                        // increment mesh count
                        meshCount++;
                        ssCount++;
                        //----------------------------------------------------------
                        // Loop through all the prop speeds
                        //----------------------------------------------------------
                        for (m = 0; m < set_rpm.length; m++) {
                            rpm = set_rpm[m];

                            // Update base filename for current run conditions
                            filename = workingDir + simTitle + "_" + speed + "mph_" + trim + "deg_" + height + "in_" + rpm + "rpm";
                            //--------------------------------------------------
                            // Excel
                            //--------------------------------------------------
                            //---------------
                            // Prop
                            //---------------
                            // Read prop monitor plot files
                            reader = new CSVReader(new FileReader(filename + "_prop.csv"));
                            data = reader.readAll();

                            // Open prop excel workbook
                            fs = new NPOIFSFileSystem(new File(propExcelFileName));
                            propWB = new HSSFWorkbook(fs.getRoot(), true);
                            sheet = propWB.getSheet("prop data");
                            row = sheet.createRow(ssCount);
                            row.createCell(0).setCellValue(simTitle);
                            row.createCell(1).setCellValue(speed);
                            row.createCell(2).setCellValue(trim);
                            row.createCell(3).setCellValue(height);
                            row.createCell(4).setCellValue(rpm);

                            // Compute mean and blade max/min of prop data
                            stats = new SummaryStatistics();
                            reportIterator = 1;
                            for (columnIterator = 5; columnIterator < numPropCol; columnIterator++) {
                                for (rowIterator = data.size() - 1; rowIterator >= data.size() - numToAve; rowIterator--) {
                                    String[] array = data.get(rowIterator);
                                    stats.addValue(Double.parseDouble(array[reportIterator]));

                                }
                                if (columnIterator == 12 || columnIterator == 16) {
                                    row.createCell(columnIterator).setCellValue(stats.getMean());
                                    row.createCell(columnIterator + 1).setCellValue(stats.getMax());
                                    row.createCell(columnIterator + 2).setCellValue(stats.getMin());
                                    columnIterator += 2;
                                } else {
                                    row.createCell(columnIterator).setCellValue(stats.getMean());
                                }
                                stats = new SummaryStatistics();
                                reportIterator++;
                            }
                            // Compute prop parameters of interest
                            SHP = rpm * 2 * Math.PI / 60 * row.getCell(15).getNumericCellValue() / 550;
                            J = speed * 1.467 / (rpm / 60 * Dprop / 12);
                            KT = row.getCell(7).getNumericCellValue() / (Math.pow(rpm / 60, 2) * Math.pow(Dprop / 12, 4) * 1.94);
                            KT_norm = KT / subAreaRatio[meshCount];
                            KQ = row.getCell(15).getNumericCellValue() / (Math.pow(rpm / 60, 2) * Math.pow(Dprop / 12, 5) * 1.94);
                            KQ_norm = KQ / subAreaRatio[meshCount];
                            eta = J / 2 / Math.PI * KT_norm / KQ_norm;

                            // Write prop parameters to excel ss
                            row.createCell(columnIterator).setCellValue(SHP);
                            row.createCell(columnIterator + 1).setCellValue(J);
                            row.createCell(columnIterator + 2).setCellValue(KT_norm);
                            row.createCell(columnIterator + 3).setCellValue(KQ_norm);
                            row.createCell(columnIterator + 4).setCellValue(eta);

                            // Save prop excel file
                            fileOut = new FileOutputStream(workingDir + propExcelFileName);
                            propWB.write(fileOut);
                            fileOut.close();
                            fs.close();

                            //---------------
                            // Gearcase (gc)
                            //---------------
                            // Read gc monitor plot files
                            reader = new CSVReader(new FileReader(filename + "_gc.csv"));
                            data = reader.readAll();

                            // Open gc excel workbook
                            fs = new NPOIFSFileSystem(new File(gcExcelFileName));
                            gcWB = new HSSFWorkbook(fs.getRoot(), true);
                            sheet = gcWB.getSheet("gc data");
                            row = sheet.createRow(ssCount);
                            row.createCell(0).setCellValue(simTitle);
                            row.createCell(1).setCellValue(speed);
                            row.createCell(2).setCellValue(trim);
                            row.createCell(3).setCellValue(height);
                            row.createCell(4).setCellValue(rpm);

                            // Compute mean and standard deviation of gc data
                            stats = new SummaryStatistics();
                            for (columnIterator = 1; columnIterator <= numGcReports; columnIterator++) {
                                for (rowIterator = data.size() - 1; rowIterator >= data.size() - numToAve; rowIterator--) {
                                    String[] array = data.get(rowIterator);
                                    stats.addValue(Double.parseDouble(array[columnIterator]));
                                }
                                row.createCell(columnIterator + 4).setCellValue(stats.getMean());
                                stats = new SummaryStatistics(); // clear report data
                            }

                            // Save gc excel file
                            fileOut = new FileOutputStream(workingDir + gcExcelFileName);
                            gcWB.write(fileOut);
                            fileOut.close();
                            fs.close();

                            // Update excel data row number
                            ssCount++;
                        }
                    }
                }
            }
        } catch (IOException ex) {
            sim.println(ex);
        }
    }
}
