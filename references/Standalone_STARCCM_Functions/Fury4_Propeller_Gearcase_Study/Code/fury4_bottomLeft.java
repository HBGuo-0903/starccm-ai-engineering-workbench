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

public class fury4_bottomLeft extends StarMacro {

    public void execute() {
        execute0();
    }

    private void execute0() {

        //----------------------------------------------------------------------
        // User inputs
        //----------------------------------------------------------------------
        // Titles, filenames, and headers
        String simTitle = "Fury4_6062";
        String TR = "2016-1027-014";
        String PPTFileName = "TR" + TR + ".ppt";
        String propExcelFileName = "prop_data.xls";
        String gcExcelFileName = "gc_data.xls";
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
            "Blade 1 Thrust (lbf)",
            "Prop Torque (lbf-ft)",
            "Blade 1 Torque (lbf-ft)",};
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
        double mdot = 0.4; // exhaust mass flow rate (kg/s)
        double stepsize = 1.0; // degrees
        double revs_init = 4; // number of prop revolutions for initial rpm setting
        double revs = 2; // number of prop revolutions for subsequent rpms
        double x_prop = 12.8799; // prop center from GC center (in)
        double trimPoint_z = 1.097; // z-coord of trim rotation point (m)
        double trimPoint_x = .282; // x-coord of trim rotation point (m)
        int numToAve = 300; // number of iterations or timesteps to average for monitor data
        int numPropReports = 10; // number of reports being exported to csv file
        int numGcReports = 6;

        // Inclusion filters
        boolean IncludeAllImages = false;
        String InclusionFilter = "Scalar";

        // Image Resolution for hardcopy
        int ImageResolutionX = 1200;
        int ImageResolutionY = 700;
        double MagnificationFactor = 1.0;

        // Page, Title and Image Sizes of PPT file
        int PageSizeX = 720; // 10 inches
        int PageSizeY = 540; // 7.5 inches
        int TitleMarginX = 20;
        int TitleMarginY = 0;
        int TitleSizeY = 60;

        // Image size and placement
        int ImageMarginX = 40;
        int ImageMarginY = 285;
        int ImageSizeX = ImageResolutionX / 4;
        int ImageSizeY = ImageResolutionY / 4;

        // Caption size and placement
        int CaptionMarginX = ImageMarginX;
        int CaptionMarginY = ImageMarginY + ImageSizeY;
        int CaptionSizeX = ImageSizeX;
        int CaptionSizeY = 40;

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

        int i;
        int j;
        int k;
        int m;
        int numSteps;
        int columnIterator;
        int rowIterator;
        int ssCount;
        int slCount;

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

        //----------------------------------------------------------------------
        // Create sim objects
        //----------------------------------------------------------------------
        Simulation sim
                = getActiveSimulation();
        Solution solution
                = sim.getSolution();
        Units meter
                = ((Units) sim.getUnitsManager().getObject("m"));
        Units inch
                = ((Units) sim.getUnitsManager().getObject("in"));
        PhysicsContinuum physics
                = ((PhysicsContinuum) sim.getContinuumManager().getContinuum("Physics 1"));
        VofWaveModel vofWave
                = physics.getModelManager().getModel(VofWaveModel.class);
        FlatVofWave flatVofWave
                = ((FlatVofWave) vofWave.getVofWaveManager().getObject("FlatVofWave 1"));
        TransformPartsOperation transformPartsOperation_0
                = ((TransformPartsOperation) sim.get(MeshOperationManager.class).getObject("Rotate"));
        RotationControl rotationControl_0
                = ((RotationControl) transformPartsOperation_0.getTransforms().getObject("Rotate"));
        TransformPartsOperation transformPartsOperation_1
                = ((TransformPartsOperation) sim.get(MeshOperationManager.class).getObject("Translate"));
        TranslationControl translationControl_0
                = ((TranslationControl) transformPartsOperation_1.getTransforms().getObject("Translate"));
        Coordinate translate_height
                = translationControl_0.getTranslationVector();
        TransformPartsOperation transformPartsOperation_2
                = ((TransformPartsOperation) sim.get(MeshOperationManager.class).getObject("Translate_Refine_Outer"));
        TranslationControl translationControl_1
                = ((TranslationControl) transformPartsOperation_2.getTransforms().getObject("Translate"));
        Coordinate refine_translate
                = translationControl_1.getTranslationVector();
        LabCoordinateSystem labCoordinateSystem_0
                = sim.getCoordinateSystemManager().getLabCoordinateSystem();
        CartesianCoordinateSystem trimCenter
                = ((CartesianCoordinateSystem) labCoordinateSystem_0.getLocalCoordinateSystemManager().getObject("Trim_Center"));
        Coordinate trimCenterOrigin
                = trimCenter.getOrigin();
        CartesianCoordinateSystem gcCenter
                = ((CartesianCoordinateSystem) trimCenter.getLocalCoordinateSystemManager().getObject("GC_Center"));
        Coordinate gcCenterOrigin = gcCenter.getOrigin();
        CartesianCoordinateSystem propCenter
                = ((CartesianCoordinateSystem) gcCenter.getLocalCoordinateSystemManager().getObject("Prop_Center"));
        Coordinate propCenterOrigin
                = propCenter.getOrigin();
        ImplicitUnsteadySolver implicitUnsteadySolver_0
                = ((ImplicitUnsteadySolver) sim.getSolverManager().getSolver(ImplicitUnsteadySolver.class));
        Region region_0
                = sim.getRegionManager().getRegion("Rotating");
        Boundary exhaustInlet
                = region_0.getBoundaryManager().getBoundary("Inlet_Exhaust");
        MassFlowRateProfile massFlowRate
                = exhaustInlet.getValues().get(MassFlowRateProfile.class);
        RotatingMotion rotatingMotion
                = ((RotatingMotion) sim.get(MotionManager.class).getObject("Rotation"));
        StepStoppingCriterion stepStoppingCriterion
                = ((StepStoppingCriterion) sim.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps"));
        MonitorPlot propPlot
                = ((MonitorPlot) sim.getPlotManager().getPlot("Prop"));
        MonitorPlot gcPlot
                = ((MonitorPlot) sim.getPlotManager().getPlot("Gearcase"));
        PressureCoefficientFunction pressureCoefficientFunction
                = ((PressureCoefficientFunction) sim.getFieldFunctionManager().getFunction("PressureCoefficient"));

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

            // Open existing ppt and get the slides
            ppt = new HSLFSlideShow(new HSLFSlideShowImpl(workingDir + PPTFileName));
            List<HSLFSlide> slides = ppt.getSlides();
            
            // Initialize spreadsheet row count (start at 1 to skip header row)
            ssCount = 1;
            
            // Initialize ppt slide count (start at 1 to skip title slide)
            slCount = 1;
            // ---------------------------------------------------------------------
            // Loop through speeds
            //----------------------------------------------------------------------
            for (i = 0; i < set_speeds.length; i++) {
                speed = set_speeds[i];
                flatVofWave.getCurrent().setComponents(speed, 0.0, 0.0);
                flatVofWave.getWind().setComponents(speed, 0.0, 0.0);
                pressureCoefficientFunction.getReferenceVelocity().setValue(speed);

                //------------------------------------------------------------------
                // Loop through all the trim angles
                //------------------------------------------------------------------
                for (j = 0; j < set_trim.length; j++) {
                    trim = set_trim[j];
                    trim_rad = trim * 3.14159 / 180;
                    rotationControl_0.getAngle().setValue(trim);
                    // Move outer refinement zone to follow motion due to trim
                    refine_translate.setCoordinate(meter, meter, meter, new DoubleVector(new double[]{trimPoint_z * Math.sin(trim_rad), 0.0, (trimPoint_x + x_prop) * Math.sin(trim_rad)}));

                    //--------------------------------------------------------------
                    // Loop through all the heights
                    //--------------------------------------------------------------
                    for (k = 0; k < set_height.length; k++) {
                        height = set_height[k];
                        // Move gearcase to proper height
                        translate_height.setCoordinate(inch, inch, inch, new DoubleVector(new double[]{0.0, 0.0, -height}));
                        // Orient coordinate systems:
                        // Trim Center
                        trimCenterOrigin.setCoordinate(meter, meter, meter, new DoubleVector(new double[]{-trimPoint_x, 0.0, trimPoint_z + -height * .0254}));
                        trimCenter.setBasis0(new DoubleVector(new double[]{Math.cos(trim_rad), 0.0, Math.sin(trim_rad)}));
                        // GC Center
                        gcCenterOrigin.setCoordinate(meter, meter, meter, new DoubleVector(new double[]{trimPoint_x, 0.0, -trimPoint_z}));
                        // Prop Center
                        propCenterOrigin.setCoordinate(inch, inch, inch, new DoubleVector(new double[]{x_prop, 0.0, 0.0}));
                        // Clear solution history and fields
                        solution.clearSolution();
                        // Execute geometry and mesh operations
                        sim.get(MeshOperationManager.class).executeAll();

                        //----------------------------------------------------------
                        // Loop through all the prop speeds
                        //----------------------------------------------------------
                        for (m = 0; m < set_rpm.length; m++) {
                            rpm = set_rpm[m];
                            timestep = 1 / (rpm / 60 * 360 / stepsize);
                            implicitUnsteadySolver_0.getTimeStep().setValue(timestep);
                            // set exhaust mass flow rate
                            massFlowRate.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(mdot);
                            // set props speeds
                            rotatingMotion.getRotationRate().setValue(rpm);
                            // Set number of timesteps
                            if (m == 0) {
                                numSteps = (int) Math.round(revs_init * 360 / stepsize);
                            } else {
                                numSteps = (int) Math.round(revs * 360 / stepsize);
                            }
                            // Set stopping criteria and run
                            stepStoppingCriterion.setMaximumNumberSteps(numSteps);
                            sim.getSimulationIterator().run();

                            // Update base filename for current run conditions
                            filename = workingDir + simTitle + "_" + speed + "mph_" + trim + "deg_" + height + "in_" + rpm + "rpm";

                            // Export data
                            propPlot.export(filename + "_prop.csv", ",");
                            gcPlot.export(filename + "_gc.csv", ",");

                            //--------------------------------------------------
                            // Powerpoint
                            //--------------------------------------------------
                            // Skip section header slide
                            slCount++;
                            
                            // Loop through and scenes to ppt        
                            for (Scene scene : sim.getSceneManager().getScenes()) {

                                // Check inclusion filter String
                                if ((IncludeAllImages) || (scene.getPresentationName().contains(InclusionFilter))) {

                                    // Open the Scene and save it to a file
                                    scene.open(true);
                                    ImageFileName = filename + "_" + scene.getPresentationName() + ".png";
                                    scene.printAndWait(ImageFileName, 1, (int) (MagnificationFactor * ImageResolutionX), (int) (MagnificationFactor * ImageResolutionY));

                                    // Open slide
                                    slide = slides.get(slCount);

                                    // Add the new picture to this slideshow
                                    HSLFPictureData pd = ppt.addPicture(new File(ImageFileName), HSLFPictureData.PictureType.JPEG);
                                    HSLFPictureShape pictNew = new HSLFPictureShape(pd);
                                    pictNew.setAnchor(new java.awt.Rectangle(ImageMarginX, ImageMarginY, ImageSizeX, ImageSizeY));
                                    slide.addShape(pictNew);

                                    // Add picture caption
                                    HSLFTextBox caption = new HSLFTextBox();
                                    tp = caption.getTextParagraphs().get(0);
                                    tp.setTextAlign(HSLFTextParagraph.TextAlign.CENTER);
                                    tr = tp.getTextRuns().get(0);
                                    tr.setFontSize(18.);
                                    caption.setText(simTitle);
                                    caption.setAnchor(new java.awt.Rectangle(CaptionMarginX, CaptionMarginY, CaptionSizeX, CaptionSizeY));
                                    slide.addShape(caption);

                                    // Add the Scene name as title
                                    HSLFTextBox title = slide.addTitle();
                                    tp = title.getTextParagraphs().get(0);
                                    tp.setTextAlign(HSLFTextParagraph.TextAlign.RIGHT);
                                    tr = tp.getTextRuns().get(0);
                                    tr.setFontSize(36.);
                                    title.setText(scene.getPresentationName());
                                    title.setAnchor(new java.awt.Rectangle(TitleMarginX, TitleMarginY, PageSizeX - 2 * TitleMarginX, TitleSizeY));
                                    
                                    // Go to next slide
                                    slCount++;
                                }
                            }

                            // Save the ppt file
                            fileOut = new FileOutputStream(workingDir + PPTFileName);
                            ppt.write(fileOut);
                            fileOut.close();
                            sim.println("Generated PPT File: " + workingDir + PPTFileName);

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

                            // Compute mean and standard deviation of prop data
                            stats = new SummaryStatistics();
                            for (columnIterator = 1; columnIterator <= numPropReports; columnIterator++) {
                                for (rowIterator = data.size() - 1; rowIterator >= data.size() - numToAve ; rowIterator--) {
                                    String[] array = data.get(rowIterator);
                                    stats.addValue(Double.parseDouble(array[columnIterator]));
                                }
                                row.createCell(columnIterator + 4).setCellValue(stats.getMean());
                                row.createCell(columnIterator + 4 + numPropReports).setCellValue(stats.getStandardDeviation());
                                stats = new SummaryStatistics();
                            }

                            // Save prop excel file
                            fileOut = new FileOutputStream(workingDir + propExcelFileName);
                            propWB.write(fileOut);
                            fileOut.close();
                            fs.close();
                            sim.println("Generated prop excel file: " + workingDir + propExcelFileName);

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
                                for (rowIterator = data.size() - 1; rowIterator >= data.size() - numToAve ; rowIterator--) {
                                    String[] array = data.get(rowIterator);
                                    stats.addValue(Double.parseDouble(array[columnIterator]));
                                }
                                row.createCell(columnIterator + 4).setCellValue(stats.getMean());
                                row.createCell(columnIterator + 4 + numGcReports).setCellValue(stats.getStandardDeviation());
                                stats = new SummaryStatistics(); // clear report data
                            }

                            // Save gc excel file
                            fileOut = new FileOutputStream(workingDir + gcExcelFileName);
                            gcWB.write(fileOut);
                            fileOut.close();
                            fs.close();
                            sim.println("Generated gc excel file: " + workingDir + gcExcelFileName);
                            //--------------------------------------------------

                            // Save sim file                       
                            sim.saveState(resolvePath(filename + ".sim"));
                            // Clear solution history (not fields)
                            solution.clearSolution(Solution.Clear.History);

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
