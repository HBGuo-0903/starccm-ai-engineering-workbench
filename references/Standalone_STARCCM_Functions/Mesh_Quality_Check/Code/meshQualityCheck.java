/*
 * Author: Michael Elmore - michael.elmore@cd-adapco.com
 *
 * Updated 6/25/2015
 * 
 * This macro generates numerous plots and scenes useful to check the quality of a mesh.
 * It also generates views of the mesh based on the parts' locations
 * Options are available if running on clusters or in batch to save scenes.
 * See the user variable section below for quality criterion.
 * The macro can be run multiple times in the same sim file to update any criterion parameters
 * 
 * 
 * Note this version is for 9.06 and later
 * 
 */


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;
import star.base.neo.*;
import star.base.report.ElementCountReport;
import star.base.report.MaxReport;
import star.base.report.MinReport;
import star.common.*;
import star.meshing.SurfaceRep;
import star.metrics.CellQualityRemediationModel;
import star.vis.*;

public class meshQualityCheck extends StarMacro {

    public ArrayList<String> output = new ArrayList<String>();

    public void execute() {
        //*************************************************************************
        //******************USER VARIABLE SECTION**********************************
        //*************************************************************************
        //lowest allowable cell quality
        double cellQualityThreshold = 0.1;
        //highest allowable skewness
        double skewnessThreshold = 85.0;
        //lowest allowable volume change
        double volumeChangeThreshold = 0.01;
        //generate threshold based on the bad cell indicator function?
        //if this enables cell quality remediation, it will be disabled afterward
        Boolean doBadCellIndicator = true;
        //make the geometry translucent in scenes? Useful for internal flow cases
        Boolean translucentGeometry = true;
        //generate threshold containing prismatic cells?
        Boolean doPrisms = true;
        //create scenes of the mesh using cell surfaces? This extracts the volume mesh surface
        Boolean doMeshScenes = true;
        //generate STAR-VIEW files of each scene? useful for large files on clusters
        Boolean doStarView = false;
        //save simulation after finished?
        Boolean saveSim = false;
        //*************************************************************************
        //******************END USER VARIABLE SECTION******************************
        //*************************************************************************

        //get initial variables
        Simulation sim = getActiveSimulation();
        ArrayList<PhysicsContinuum> cqrContinua = new ArrayList<PhysicsContinuum>();
        Boolean solids = false;

        sim.println("\n");

        //determine if solids exist
        solids = determineIfSolids(sim);

        //close open scenes that meshQualityCheck generates
        closeScenes(sim);

        //delete the old stuff
        deleteOldSession(sim);

        //check version
        int version = version(sim);
        if (version <= 806) {
            sim.println("Bad cell metrics are not available in this version.");
        }

        //do cqr stuff if needed
        if (doBadCellIndicator && version > 806) {
            cqrContinua = determineCellQualityRemediation(sim);
            try {
                enableCellQualityRemediation(sim);
            } catch (Exception e) {
                //if no physics continuum suitable can be found, cqr enabling will be skipped
                sim.println("Cell quality remediation cannot be enabled, bad cell metrics will be disabled");
                doBadCellIndicator = false;
            }
        }

        //generate groups
        sim.getPartManager().getGroupsManager().createGroup("cellQualityParts");
        sim.getReportManager().getGroupsManager().createGroup("cellQualityReports");
        sim.getSceneManager().getGroupsManager().createGroup("cellQualityScenes");
        sim.getPlotManager().getGroupsManager().createGroup("cellQualityPlots");

        if (doMeshScenes) {
            //get views of the mesh
            sim.getSceneManager().getGroupsManager().createGroup("meshScenes");
            sceneGenMeshView(sim);
        }

        //get the cell count of fluids and solids
        double solidCellCount = getCellCount(sim, 2);
        double fluidCellCount = getCellCount(sim, 1);
        //print the cell quality report
        sim.println("=====================================================");
        sim.println("Cell Quality Report:");
        sim.println("=====================================================");
        String temp = "Fluid cells: " + String.format("%40s", fluidCellCount);
        sim.println(temp);
        output.add(temp);
        if (solids) {
            temp = "Solid cells: " + String.format("%40s", solidCellCount);
            sim.println(temp);
            output.add(temp);
        }
        createCellQualityMetric(sim, cellQualityThreshold, fluidCellCount, solidCellCount, solids);
        createSkewnessMetric(sim, skewnessThreshold, fluidCellCount, solidCellCount, solids);
        volChangeMetric(sim, volumeChangeThreshold, fluidCellCount, solidCellCount, solids);
        if (doBadCellIndicator && version > 806) {
            badCellIndicatorMetric(sim, fluidCellCount, solidCellCount, solids);
        }
        negVolCellCount(sim);
        maxSkewnessMetric(sim);
        minCellQualityMetric(sim);
        sim.println("=====================================================");

        //do prisms if desired
        if (doPrisms) {
            prismCells(sim);
        }

        //create scenes
        sceneGenCellQuality(sim, cellQualityThreshold, solids, translucentGeometry);
        sceneGenSkewness(sim, skewnessThreshold, solids, translucentGeometry);
        sceneGenVolChange(sim, volumeChangeThreshold, solids, translucentGeometry);
        if (doBadCellIndicator && version > 806) {
            sceneGenBadCell(sim, solids, translucentGeometry);
        }
        if (doPrisms) {
            sceneGenPrisms(sim, translucentGeometry);
        }
        sceneGenSurfaceQuality(sim);
        sceneGenSurfaceSkewness(sim);
        sceneGenNegVolume(sim, translucentGeometry);

        //create histogram
        cellQualityHistogram(sim, version);
        skewnessHistogram(sim);
        volumeChangeHistogram(sim);

        if (version <= 906) {
            disableImmediateMode(sim);
        }

        //generate STAR-VIEW files
        if (doStarView) {
            starView(sim);
        }

        //disable cqr if it was enabled before
        if (doBadCellIndicator && version > 806) {
            disableCellQualityRemediation(sim, cqrContinua);
        }

        //write text file
        writeOutput(sim);

        //save sim
        if (saveSim) {
            saveSim(sim);
        }

    }

    private Boolean determineIfSolids(Simulation sim) {
        Boolean solids = false;
        Collection<Region> regions = sim.getRegionManager().getRegions();
        for (Region ri : regions) {
            if (ri.getRegionType() instanceof SolidRegion) {
                solids = true;
            }
        }
        return solids;
    }

    private void deleteOldSession(Simulation sim) {

        //parts
        ArrayList<String> partNames = new ArrayList<String>(Arrays.asList("cellQualityFluid", "skewnessFluid", "volumeChangeFluid", "volume", "cellQualitySolid", "skewnessSolid", "volumeChangeSolid", "prisms", "badCellsSolid", "badCellsFluid", "XYsection", "XZsection", "YZsection", "XYsurface", "XZsurface", "YZsurface", "Zsection", "Ysection", "Xsection", "Zsurface", "Ysurface", "Xsurface"));
        for (int i = 0; i < partNames.size(); i++) {
            try {
                Part part = sim.getPartManager().getPart(partNames.get(i));
                deletePart(sim, part);
            } catch (Exception e) {
            }
        }
        //EC reports
        ArrayList<String> reportNames = new ArrayList<String>(Arrays.asList("cellQualityFluid", "skewnessFluid", "volumeChangeFluid", "negVolumeCells", "cellQualitySolid", "skewnessSolid", "volumeChangeSolid", "badCellsSolid", "badCellsFluid"));
        for (int i = 0; i < reportNames.size(); i++) {
            try {
                ElementCountReport report = ((ElementCountReport) sim.getReportManager().getReport(reportNames.get(i)));
                deleteReport(sim, report);
            } catch (Exception e) {
            }
        }
        //min report
        try {
            MinReport report = (MinReport) sim.getReportManager().getReport("minCellQuality");
            sim.getReportManager().remove(report);
        } catch (Exception e) {
        }
        //max report
        try {
            MaxReport report = (MaxReport) sim.getReportManager().getReport("maxCellSkewness");
            sim.getReportManager().remove(report);
        } catch (Exception e) {
        }
        //plots
        ArrayList<String> plotNames = new ArrayList<String>(Arrays.asList("Skewness Histogram", "Volume Change Histogram", "Cell Quality Histogram"));
        for (int i = 0; i < plotNames.size(); i++) {
            try {
                HistogramPlot HP = ((HistogramPlot) sim.getPlotManager().getPlot(plotNames.get(i)));
                deletePlot(sim, HP);
            } catch (Exception e) {
            }
        }
        //scenes
        ArrayList<String> scenes = new ArrayList<String>(Arrays.asList("Cell Quality: Volume", "Skewness: Volume", "Volume Change", "Skewness: Surface", "Cell Quality: Surface", "Negative Volume Cells", "Prism Cells", "Bad Cells", "Mesh View: XY", "Mesh View: XZ", "Mesh View: YZ", "Mesh View: Z", "Mesh View: Y", "Mesh View: X"));
        for (int i = 0; i < scenes.size(); i++) {
            try {
                Scene scene = sim.getSceneManager().getScene(scenes.get(i));
                deleteScene(sim, scene);
            } catch (Exception e) {
            }
        }

        //extracted rep
        try {
            SurfaceRep surfRep = ((SurfaceRep) sim.getRepresentationManager().getObject("Extracted Surface"));
            sim.getRepresentationManager().removeObjects(surfRep);
        } catch (Exception e) {
        }

        //remove groups
        try {
            ((ClientServerObjectGroup) sim.getPartManager().getGroupsManager().getObject("cellQualityParts")).getGroupsManager().unGroupObjects();
        } catch (Exception e) {
        }
        try {
            ((ClientServerObjectGroup) sim.getSceneManager().getGroupsManager().getObject("cellQualityScenes")).getGroupsManager().unGroupObjects();
        } catch (Exception e) {
        }
        try {
            ((ClientServerObjectGroup) sim.getSceneManager().getGroupsManager().getObject("meshScenes")).getGroupsManager().unGroupObjects();
        } catch (Exception e) {
        }
        try {
            ((ClientServerObjectGroup) sim.getPlotManager().getGroupsManager().getObject("cellQualityPlots")).getGroupsManager().unGroupObjects();
        } catch (Exception e) {
        }
        try {
            ((ClientServerObjectGroup) sim.getReportManager().getGroupsManager().getObject("cellQualityReports")).getGroupsManager().unGroupObjects();
        } catch (Exception e) {
        }
    }

    private void deletePart(Simulation sim, Part part) {
        sim.getPartManager().remove(part);
    }

    private void deleteReport(Simulation sim, ElementCountReport report) {
        sim.getReportManager().removeObjects(report);
    }

    private void deletePlot(Simulation sim, HistogramPlot HP) {
        sim.getPlotManager().remove(HP);
    }

    private void deleteScene(Simulation sim, Scene scene) {
        sim.getSceneManager().remove(scene);
    }

    private double getCellCount(Simulation sim, int regionSwitch) {
        double cellCount = 0;
        //generate reports and collections of regions
        ElementCountReport ECR = sim.getReportManager().createReport(ElementCountReport.class);
        Collection<Region> loopRegions = sim.getRegionManager().getRegions();
        Collection<Region> fluidRegions = sim.getRegionManager().getRegions();
        Collection<Region> solidRegions = sim.getRegionManager().getRegions();

        for (Region ri : loopRegions) {
            if (((ri.getRegionType() instanceof FluidRegion) || (ri.getRegionType() instanceof PorousRegion)) && (!(ri instanceof ShellRegion))) {
                //this region is NOT of type solid, so remove it from the fluids
                solidRegions.remove(ri);
            } else if (!(ri instanceof ShellRegion)) {
                //this region is NOT of type fluid/porous, so we know solids exist
                //remove this non-solid from the solid boundaries
                fluidRegions.remove(ri);
            }
        }
        //generate cell count depending on what type of region
        if (regionSwitch == 1) {
            ECR.getParts().setObjects(fluidRegions);
            cellCount = ECR.getReportMonitorValue();
        } else if (regionSwitch == 2) {
            ECR.getParts().setObjects(solidRegions);
            cellCount = ECR.getReportMonitorValue();
        }

        sim.getReportManager().remove(ECR);
        return cellCount;
    }

    private Boolean createCellQualityMetric(Simulation sim, double cellQualityThreshold, double fluidCellCount, double solidCellCount, Boolean solids) {
        //generate variables required for cell quality metric
        Units units = ((Units) sim.getUnitsManager().getObject("m"));
        CellQualityFunction cqf = ((CellQualityFunction) sim.getFieldFunctionManager().getFunction("CellQuality"));
        double range[] = {cellQualityThreshold, 1.0};
        Collection<Region> fluidRegions = sim.getRegionManager().getRegions();
        Collection<Region> solidRegions = sim.getRegionManager().getRegions();
        Collection<Boundary> fluidBoundaries = new ArrayList<Boundary>();
        Collection<Boundary> solidBoundaries = new ArrayList<Boundary>();
        Collection<Region> loopRegions = sim.getRegionManager().getRegions();
        //find out which regions are solid and get all boundaries in collections
        for (Region ri : loopRegions) {
            if (((ri.getRegionType() instanceof FluidRegion) || (ri.getRegionType() instanceof PorousRegion)) && (!(ri instanceof ShellRegion))) {
                //this region is NOT of type solid, so remove it from the fluids
                solidRegions.remove(ri);
                //this region IS of type fluid/porous, so add its boundaries
                fluidBoundaries.addAll(ri.getBoundaryManager().getBoundaries());
            } else if (!(ri instanceof ShellRegion)) {
                //this region is NOT of type fluid/porous, so we know solids exist
                solids = true;
                //remove this non-solid from the solid boundaries
                fluidRegions.remove(ri);
                solidBoundaries.addAll(ri.getBoundaryManager().getBoundaries());
            } else if (ri instanceof ShellRegion) {
                //this is a shell region, remove it from the fluids
                fluidRegions.remove(ri);
            }
        }
        //generate the parts for cell quality on fluids
        ThresholdPart cqTPf = sim.getPartManager().createThresholdPart(new NeoObjectVector(fluidRegions.toArray()), new DoubleVector(range), units, cqf, 2);
        cqTPf.getInputParts().addParts(fluidBoundaries);
        cqTPf.setPresentationName("cellQualityFluid");
        ElementCountReport cqRf = sim.getReportManager().createReport(ElementCountReport.class);
        cqRf.setPresentationName("cellQualityFluid");
        cqRf.getParts().setObjects(cqTPf);
        //group the parts
        sim.getPartManager().getGroupsManager().getObject("cellQualityParts").addObjects(new NeoObjectVector(new Object[]{cqTPf}));
        sim.getReportManager().getGroupsManager().getObject("cellQualityReports").addObjects(new NeoObjectVector(new Object[]{cqRf}));
        String outputTemp = String.format("%-40s", "Fluid cells below quality " + String.format("%1.2f", cellQualityThreshold) + ": ") + String.format("%6s", String.format("%5.0f", cqRf.getReportMonitorValue())) + " " + String.format("%5.2f", (cqRf.getReportMonitorValue() / fluidCellCount * 100)) + "%";
        sim.println(outputTemp);
        output.add(outputTemp);
        //if solid regions exist, also do it on solids

        if (solids) {
            ThresholdPart cqTPs = sim.getPartManager().createThresholdPart(new NeoObjectVector(solidRegions.toArray()), new DoubleVector(range), units, cqf, 2);
            cqTPs.getInputParts().addParts(solidBoundaries);
            cqTPs.setPresentationName("cellQualitySolid");
            ElementCountReport cqRs = sim.getReportManager().createReport(ElementCountReport.class);
            cqRs.setPresentationName("cellQualitySolid");
            cqRs.getParts().setObjects(cqTPs);
            sim.getPartManager().getGroupsManager().getObject("cellQualityParts").addObjects(new NeoObjectVector(new Object[]{cqTPs}));
            sim.getReportManager().getGroupsManager().getObject("cellQualityReports").addObjects(new NeoObjectVector(new Object[]{cqRs}));
            outputTemp = String.format("%-40s", "Solid cells below quality " + String.format("%1.2f", cellQualityThreshold) + ": ") + String.format("%6s", String.format("%5.0f", cqRs.getReportMonitorValue())) + " " + String.format("%5.2f", (cqRs.getReportMonitorValue() / solidCellCount * 100)) + "%";
            sim.println(outputTemp);
            output.add(outputTemp);
        }
        return solids;
    }

    private void createSkewnessMetric(Simulation sim, double skewnessThreshold, double fluidCellCount, double solidCellCount, Boolean solids) {
        //generate variables required for cell quality metric
        Units units = ((Units) sim.getUnitsManager().getObject("m"));
        SkewnessAngleFunction skf = ((SkewnessAngleFunction) sim.getFieldFunctionManager().getFunction("SkewnessAngle"));
        double range[] = {0.0, skewnessThreshold};
        Collection<Region> fluidRegions = sim.getRegionManager().getRegions();
        Collection<Region> solidRegions = sim.getRegionManager().getRegions();
        Collection<Boundary> fluidBoundaries = new ArrayList<Boundary>();
        Collection<Boundary> solidBoundaries = new ArrayList<Boundary>();
        Collection<Region> loopRegions = sim.getRegionManager().getRegions();
        //find out which regions are solid and get all boundaries in collections
        for (Region ri : loopRegions) {
            if (((ri.getRegionType() instanceof FluidRegion) || (ri.getRegionType() instanceof PorousRegion)) && (!(ri instanceof ShellRegion))) {
                //this region is NOT of type solid, so remove it from the fluids
                solidRegions.remove(ri);
                //this region IS of type fluid/porous, so add its boundaries
                fluidBoundaries.addAll(ri.getBoundaryManager().getBoundaries());
            } else if (!(ri instanceof ShellRegion)) {
                //this region is NOT of type fluid/porous, so we know solids exist
                solids = true;
                //remove this non-solid from the solid boundaries
                fluidRegions.remove(ri);
                solidBoundaries.addAll(ri.getBoundaryManager().getBoundaries());
            } else if (ri instanceof ShellRegion) {
                //this is a shell region, remove it from the fluids
                fluidRegions.remove(ri);
            }
        }
        //generate the parts for cell quality on fluids
        ThresholdPart skTPf = sim.getPartManager().createThresholdPart(new NeoObjectVector(fluidRegions.toArray()), new DoubleVector(range), units, skf, 1);
        skTPf.getInputParts().addParts(fluidBoundaries);
        skTPf.setPresentationName("skewnessFluid");
        ElementCountReport skRf = sim.getReportManager().createReport(ElementCountReport.class);
        skRf.setPresentationName(
                "skewnessFluid");
        skRf.getParts().setObjects(skTPf);
        //group the parts
        sim.getPartManager().getGroupsManager().getObject("cellQualityParts").addObjects(new NeoObjectVector(new Object[]{skTPf
        }));
        sim.getReportManager().getGroupsManager().getObject("cellQualityReports").addObjects(new NeoObjectVector(new Object[]{skRf
        }));
        String outputTemp = String.format("%-40s", "Fluid cells above skewness angle " + String.format("%1.1f", skewnessThreshold) + ": ") + String.format("%6s", String.format("%5.0f", skRf.getReportMonitorValue())) + " " + String.format("%5.2f", (skRf.getReportMonitorValue() / fluidCellCount * 100)) + "%";

        sim.println(outputTemp);
        //if solid regions exist, also do it on solids
        output.add("Fluid cells above skewness angle " + skewnessThreshold + ": " + skRf.getReportMonitorValue() + " " + String.format("%.2f", (skRf.getReportMonitorValue() / fluidCellCount * 100)) + "%");

        if (solids) {
            ThresholdPart skTPs = sim.getPartManager().createThresholdPart(new NeoObjectVector(solidRegions.toArray()), new DoubleVector(range), units, skf, 1);
            skTPs.getInputParts().addParts(solidBoundaries);
            skTPs.setPresentationName("skewnessSolid");
            ElementCountReport skRs = sim.getReportManager().createReport(ElementCountReport.class);
            skRs.setPresentationName("skewnessSolid");
            skRs.getParts().setObjects(skTPs);
            sim.getPartManager().getGroupsManager().getObject("cellQualityParts").addObjects(new NeoObjectVector(new Object[]{skTPs}));
            sim.getReportManager().getGroupsManager().getObject("cellQualityReports").addObjects(new NeoObjectVector(new Object[]{skRs}));
            outputTemp = String.format("%-40s", "Solid cells above skewness angle " + String.format("%1.1f", skewnessThreshold) + ": ") + String.format("%6s", String.format("%5.0f", skRs.getReportMonitorValue())) + " " + String.format("%5.2f", (skRs.getReportMonitorValue() / solidCellCount * 100)) + "%";
            sim.println(outputTemp);
            output.add(outputTemp);
        }
    }

    private void volChangeMetric(Simulation sim, double volumeChangeThreshold, double fluidCellCount, double solidCellCount, Boolean solids) {
        //generate variables required for cell quality metric
        Units units = ((Units) sim.getUnitsManager().getObject("m"));
        VolumeChangeFunction vcf = ((VolumeChangeFunction) sim.getFieldFunctionManager().getFunction("VolumeChange"));
        double range[] = {volumeChangeThreshold, 1.0};
        Collection<Region> fluidRegions = sim.getRegionManager().getRegions();
        Collection<Region> solidRegions = sim.getRegionManager().getRegions();
        Collection<Boundary> fluidBoundaries = new ArrayList<Boundary>();
        Collection<Boundary> solidBoundaries = new ArrayList<Boundary>();
        Collection<Region> loopRegions = sim.getRegionManager().getRegions();
        //find out which regions are solid and get all boundaries in collections
        for (Region ri : loopRegions) {
            if (((ri.getRegionType() instanceof FluidRegion) || (ri.getRegionType() instanceof PorousRegion)) && (!(ri instanceof ShellRegion))) {
                //this region is NOT of type solid, so remove it from the fluids
                solidRegions.remove(ri);
                //this region IS of type fluid/porous, so add its boundaries
                fluidBoundaries.addAll(ri.getBoundaryManager().getBoundaries());
            } else if (!(ri instanceof ShellRegion)) {
                //this region is NOT of type fluid/porous, so we know solids exist
                solids = true;
                //remove this non-solid from the solid boundaries
                fluidRegions.remove(ri);
                solidBoundaries.addAll(ri.getBoundaryManager().getBoundaries());
            } else if (ri instanceof ShellRegion) {
                //this is a shell region, remove it from the fluids
                fluidRegions.remove(ri);
            }
        }
        //generate the parts for cell quality on fluids
        ThresholdPart vcTPf = sim.getPartManager().createThresholdPart(new NeoObjectVector(fluidRegions.toArray()), new DoubleVector(range), units, vcf, 2);
        vcTPf.getInputParts().addParts(fluidBoundaries);
        vcTPf.setPresentationName("volumeChangeFluid");
        ElementCountReport vcRf = sim.getReportManager().createReport(ElementCountReport.class);
        vcRf.setPresentationName("volumeChangeFluid");
        vcRf.getParts().setObjects(vcTPf);
        //group the parts
        sim.getPartManager().getGroupsManager().getObject("cellQualityParts").addObjects(new NeoObjectVector(new Object[]{vcTPf}));
        sim.getReportManager().getGroupsManager().getObject("cellQualityReports").addObjects(new NeoObjectVector(new Object[]{vcRf}));
        String outputTemp = String.format("%-40s", "Fluid cells below volume change " + String.format("%1.2f", volumeChangeThreshold) + ": ") + String.format("%6s", String.format("%5.0f", vcRf.getReportMonitorValue())) + " " + String.format("%5.2f", (vcRf.getReportMonitorValue() / fluidCellCount * 100)) + "%";

        sim.println(outputTemp);
        //if solid regions exist, also do it on solids
        output.add(outputTemp);

        if (solids) {
            ThresholdPart vcTPs = sim.getPartManager().createThresholdPart(new NeoObjectVector(solidRegions.toArray()), new DoubleVector(range), units, vcf, 2);
            vcTPs.getInputParts().addParts(solidBoundaries);
            vcTPs.setPresentationName("volumeChangeSolid");
            ElementCountReport vcRs = sim.getReportManager().createReport(ElementCountReport.class);
            vcRs.setPresentationName("volumeChangeSolid");
            vcRs.getParts().setObjects(vcTPs);
            sim.getPartManager().getGroupsManager().getObject("cellQualityParts").addObjects(new NeoObjectVector(new Object[]{vcTPs}));
            sim.getReportManager().getGroupsManager().getObject("cellQualityReports").addObjects(new NeoObjectVector(new Object[]{vcRs}));
            outputTemp = String.format("%-40s", "Solid cells below volume change " + String.format("%1.2f", volumeChangeThreshold) + ": ") + String.format("%6s", String.format("%5.0f", vcRs.getReportMonitorValue())) + " " + String.format("%5.2f", (vcRs.getReportMonitorValue() / solidCellCount * 100)) + "%";

            sim.println(outputTemp);
            output.add(outputTemp);
        }
    }

    private void negVolCellCount(Simulation sim) {
        //generate variables required for cell quality metric
        Units units = ((Units) sim.getUnitsManager().getObject("m"));
        Collection<Region> regions = sim.getRegionManager().getRegions();
        Collection<Boundary> boundaries = new ArrayList<Boundary>();
        for (Region ri : regions) {
            Collection<Boundary> boundTemp = ri.getBoundaryManager().getBoundaries();
            for (Boundary bi : boundTemp) {
                boundaries.add(bi);
            }
        }
        ElementCountReport cellVolume = sim.getReportManager().createReport(ElementCountReport.class);
        cellVolume.setPresentationName("negVolumeCells");
        PrimitiveFieldFunction volume = ((PrimitiveFieldFunction) sim.getFieldFunctionManager().getFunction("Volume"));
        ThresholdPart volumePart = sim.getPartManager().createThresholdPart(new NeoObjectVector(regions.toArray()), new DoubleVector(new double[]{0.0, 0.5}), units, volume, 2);
        volumePart.getInputParts().addParts(boundaries);
        volumePart.setPresentationName("volume");
        cellVolume.getParts().setObjects(volumePart);
        sim.getPartManager().getGroupsManager().getObject("cellQualityParts").addObjects(new NeoObjectVector(new Object[]{volumePart}));
        sim.getReportManager().getGroupsManager().getObject("cellQualityReports").addObjects(new NeoObjectVector(new Object[]{cellVolume}));
        String temp = "Cells with negative volume: " + String.format("%25s", cellVolume.getReportMonitorValue());
        sim.println(temp);
        output.add(temp);
    }

    private void maxSkewnessMetric(Simulation sim) {
        MaxReport mSR = sim.getReportManager().createReport(MaxReport.class);
        mSR.setPresentationName("maxCellSkewness");
        SkewnessAngleFunction skf = ((SkewnessAngleFunction) sim.getFieldFunctionManager().getFunction("SkewnessAngle"));
        mSR.setScalar(skf);

        Collection<Region> regions = sim.getRegionManager().getRegions();
        for (Region ri : regions) {
            mSR.getParts().addObjects(ri);
            Collection<Boundary> boundaries = ri.getBoundaryManager().getBoundaries();
            mSR.getParts().addObjects(boundaries);
        }
        String temp = "Maximum skewness angle: " + String.format("%29s", String.format("%.2f", (mSR.getReportMonitorValue())));
        sim.println(temp);
        sim.getReportManager().getGroupsManager().getObject("cellQualityReports").addObjects(new NeoObjectVector(new Object[]{mSR}));
        output.add(temp);
    }

    private void minCellQualityMetric(Simulation sim) {
        MinReport mCQ = sim.getReportManager().createReport(MinReport.class);
        mCQ.setPresentationName("minCellQuality");
        CellQualityFunction cqf = ((CellQualityFunction) sim.getFieldFunctionManager().getFunction("CellQuality"));
        mCQ.setScalar(cqf);

        Collection<Region> regions = sim.getRegionManager().getRegions();
        for (Region ri : regions) {
            mCQ.getParts().addObjects(ri);
            Collection<Boundary> boundaries = ri.getBoundaryManager().getBoundaries();
            mCQ.getParts().addObjects(boundaries);
        }
        String temp = "Minimum cell quality: " + String.format("%31s", String.format("%.5f", (mCQ.getReportMonitorValue())));
        sim.println(temp);
        output.add(temp);

        sim.getReportManager().getGroupsManager().getObject("cellQualityReports").addObjects(new NeoObjectVector(new Object[]{mCQ}));
    }

    private void badCellIndicatorMetric(Simulation sim, double fluidCellCount, double solidCellCount, Boolean solids) {
        //generate variables required for cell quality metric
        Units units = ((Units) sim.getUnitsManager().getObject("m"));
        PrimitiveFieldFunction badCellFn = ((PrimitiveFieldFunction) sim.getFieldFunctionManager().getFunction("BadCellFlag"));
        double range[] = {0.5, 1.5};
        Collection<Region> fluidRegions = sim.getRegionManager().getRegions();
        Collection<Region> solidRegions = sim.getRegionManager().getRegions();
        Collection<Boundary> fluidBoundaries = new ArrayList<Boundary>();
        Collection<Boundary> solidBoundaries = new ArrayList<Boundary>();
        Collection<Region> loopRegions = sim.getRegionManager().getRegions();
        //find out which regions are solid and get all boundaries in collections
        for (Region ri : loopRegions) {
            if (((ri.getRegionType() instanceof FluidRegion) || (ri.getRegionType() instanceof PorousRegion)) && (!(ri instanceof ShellRegion))) {
                //this region is NOT of type solid, so remove it from the fluids
                solidRegions.remove(ri);
                //this region IS of type fluid/porous, so add its boundaries
                fluidBoundaries.addAll(ri.getBoundaryManager().getBoundaries());
            } else if (!(ri instanceof ShellRegion)) {
                //this region is NOT of type fluid/porous, so we know solids exist
                solids = true;
                //remove this non-solid from the solid boundaries
                fluidRegions.remove(ri);
                solidBoundaries.addAll(ri.getBoundaryManager().getBoundaries());
            } else if (ri instanceof ShellRegion) {
                //this is a shell region, remove it from the fluids
                fluidRegions.remove(ri);
            }
        }
        //generate the parts for cell quality on fluids
        ThresholdPart bcTPf = sim.getPartManager().createThresholdPart(new NeoObjectVector(fluidRegions.toArray()), new DoubleVector(range), units, badCellFn, 0);
        bcTPf.getInputParts().addParts(fluidBoundaries);
        bcTPf.setPresentationName("badCellsFluid");
        ElementCountReport bcRf = sim.getReportManager().createReport(ElementCountReport.class);
        bcRf.setPresentationName(
                "badCellsFluid");
        bcRf.getParts().setObjects(bcTPf);
        //group the parts
        sim.getPartManager().getGroupsManager().getObject("cellQualityParts").addObjects(new NeoObjectVector(new Object[]{bcTPf
        }));
        sim.getReportManager().getGroupsManager().getObject("cellQualityReports").addObjects(new NeoObjectVector(new Object[]{bcRf
        }));
        String outputTemp = String.format("%-40s", "Fluid cells marked bad: ") + String.format("%6s", String.format("%5.0f", bcRf.getReportMonitorValue())) + " " + String.format("%5.2f", (bcRf.getReportMonitorValue() / fluidCellCount * 100)) + "%";
        sim.println(outputTemp);
//if solid regions exist, also do it on solids
        output.add("Fluid cells marked bad: " + bcRf.getReportMonitorValue() + " " + String.format("%.2f", (bcRf.getReportMonitorValue() / fluidCellCount * 100)) + "%");
        if (solids) {
            ThresholdPart bcTPs = sim.getPartManager().createThresholdPart(new NeoObjectVector(solidRegions.toArray()), new DoubleVector(range), units, badCellFn, 1);
            bcTPs.getInputParts().addParts(solidBoundaries);
            bcTPs.setPresentationName("badCellsSolid");
            ElementCountReport bcRs = sim.getReportManager().createReport(ElementCountReport.class);
            bcRs.setPresentationName("badCellsSolid");
            bcRs.getParts().setObjects(bcTPs);
            sim.getPartManager().getGroupsManager().getObject("cellQualityParts").addObjects(new NeoObjectVector(new Object[]{bcTPs}));
            sim.getReportManager().getGroupsManager().getObject("cellQualityReports").addObjects(new NeoObjectVector(new Object[]{bcRs}));
            outputTemp = String.format("%-40s", "Solid cells marked bad: ") + String.format("%6s", String.format("%5.0f", bcRs.getReportMonitorValue())) + " " + String.format("%5.2f", (bcRs.getReportMonitorValue() / fluidCellCount * 100)) + "%";
            sim.println(outputTemp);
            output.add(outputTemp);
        }
    }

    private void prismCells(Simulation sim) {
        //generate variables required for cell quality metric
        Units units = ((Units) sim.getUnitsManager().getObject("m"));
        Collection<Region> regions = sim.getRegionManager().getRegions();
        PrimitiveFieldFunction prismFn = ((PrimitiveFieldFunction) sim.getFieldFunctionManager().getFunction("PrismLayerCells"));
        //generate a threshold of just prisms to visualize the prism surface
        ThresholdPart prisms = sim.getPartManager().createThresholdPart(new NeoObjectVector(regions.toArray()), new DoubleVector(new double[]{0.0, 0.5}), units, prismFn, 1);
        prisms.setPresentationName("prisms");
        sim.getPartManager().getGroupsManager().getObject("cellQualityParts").addObjects(new NeoObjectVector(new Object[]{prisms}));
    }

    private ArrayList<PhysicsContinuum> determineCellQualityRemediation(Simulation sim) {
        ArrayList<PhysicsContinuum> cqrContinua = new ArrayList<PhysicsContinuum>();
        Boolean modelEnabled = false;
        Collection<Continuum> continua = sim.getContinuumManager().getObjects();
        for (Continuum ci : continua) {
            if (ci instanceof PhysicsContinuum) {
                modelEnabled = false;
                //look through the models in all continua to see if cqr is on
                Collection<Model> models = ci.getModelManager().getObjects();
                for (Model mi : models) {
                    if (mi.getClass().equals(CellQualityRemediationModel.class)) {
                        //if we find it on, the user must have not turned it on, no action is required
                        modelEnabled = true;
                        break;
                    }

                }
                if (!modelEnabled) {
                    //cqrContinua.add(ci.getPhysicsContinuum());
                    cqrContinua.add((PhysicsContinuum) ci);
                }
            }
        }
        return cqrContinua;
    }

    private void disableCellQualityRemediation(Simulation sim, ArrayList<PhysicsContinuum> cqrContinua) {
        for (int i = 0; i < cqrContinua.size(); i++) {
            PhysicsContinuum ci = cqrContinua.get(i);
            CellQualityRemediationModel cellQualityRemediationModel = ci.getModelManager().getModel(CellQualityRemediationModel.class);
            ci.disableModel(cellQualityRemediationModel);
            //sim.println("Disabled CQR in " + ci.getPresentationName());
        }
    }

    private void enableCellQualityRemediation(Simulation sim) {
        Collection<Continuum> continua = sim.getContinuumManager().getObjects();
        for (Continuum ci : continua) {
            if (ci instanceof PhysicsContinuum) {
                Boolean needToEnable = true;
                //look through the models in all continua to see if cqr is on
                Collection<Model> models = ci.getModelManager().getObjects();
                for (Model mi : models) {
                    if (mi.getClass().equals(CellQualityRemediationModel.class)) {
                        //if we find it on, no need to enable it
                        needToEnable = false;
                        break;
                    }
                }
                if (needToEnable) {
                    sim.println("Cell Quality Remediation is disabled in " + ci.getPresentationName());
                    //sim.println("WARNING: enabling cell quality remediation in " + ci.getPresentationName());
                    ci.enable(CellQualityRemediationModel.class);
                }
            }
        }
    }

    private void sceneGenCellQuality(Simulation sim, double cellQualityThreshold, Boolean solids, Boolean translucentGeometry) {
        sim.getSceneManager().createScene("Cell Quality: Volume");
        Scene scene = sim.getSceneManager().getScene("Cell Quality: Volume 1");
        scene.setPresentationName("Cell Quality: Volume");
        //geometry displayer
        PartDisplayer geomDisplayer = ((PartDisplayer) scene.getDisplayerManager().createPartDisplayer("geometry", -1, 4));
        geomDisplayer.setPresentationName("geometry");
        geomDisplayer.setOutline(false);
        geomDisplayer.setSurface(true);
        geomDisplayer.setColorMode(1);
        geomDisplayer.getParts().setObjects(getGeometryParts(sim));
        if (translucentGeometry) {
            geomDisplayer.setOpacity(0.2);
        }
        //scalar fluid displayer
        ScalarDisplayer scalarDisplayerFluid = ((ScalarDisplayer) scene.getDisplayerManager().createScalarDisplayer("scalar-fluid"));
        scalarDisplayerFluid.setPresentationName("scalar-fluid");
        CellQualityFunction cqf = ((CellQualityFunction) sim.getFieldFunctionManager().getFunction("CellQuality"));
        scalarDisplayerFluid.getScalarDisplayQuantity().setFieldFunction(cqf);
        scalarDisplayerFluid.getParts().addObjects(sim.getPartManager().getPart("cellQualityFluid"));
        scalarDisplayerFluid.getScalarDisplayQuantity().setClip(0);
        scalarDisplayerFluid.getScalarDisplayQuantity().setAutoRange(0);
        scalarDisplayerFluid.setDisplayMesh(1);
        scalarDisplayerFluid.getScalarDisplayQuantity().setRange(new DoubleVector(new double[]{0.0, cellQualityThreshold}));
        Legend fleg = scalarDisplayerFluid.getLegend();
        fleg.setReverse(true);
        //scalar solid displayer
        if (solids) {
            ScalarDisplayer scalarDisplayerSolid = ((ScalarDisplayer) scene.getDisplayerManager().createScalarDisplayer("scalar-solid"));
            scalarDisplayerSolid.setPresentationName("scalar-solid");
            scalarDisplayerSolid.getParts().addObjects(sim.getPartManager().getPart("cellQualitySolid"));
            scalarDisplayerSolid.copyProperties(scalarDisplayerFluid);
            scalarDisplayerSolid.setPresentationName("scalar-solid");
            Legend sleg = scalarDisplayerSolid.getLegend();
            sleg.setVisible(false);
            sleg.setReverse(true);
        }
        sim.getSceneManager().getGroupsManager().getObject("cellQualityScenes").addObjects(new NeoObjectVector(new Object[]{scene}));
    }

    private void sceneGenSkewness(Simulation sim, double skewnessThreshold, Boolean solids, Boolean translucentGeometry) {
        sim.getSceneManager().createScene("Skewness: Volume");
        Scene scene = sim.getSceneManager().getScene("Skewness: Volume 1");
        scene.setPresentationName("Skewness: Volume");
        //geometry displayer
        PartDisplayer geomDisplayer = ((PartDisplayer) scene.getDisplayerManager().createPartDisplayer("geometry", -1, 4));
        geomDisplayer.setPresentationName("geometry");
        geomDisplayer.setOutline(false);
        geomDisplayer.setSurface(true);
        geomDisplayer.setColorMode(1);
        geomDisplayer.getParts().setObjects(getGeometryParts(sim));
        if (translucentGeometry) {
            geomDisplayer.setOpacity(0.2);
        }
        //scalar fluid displayer
        ScalarDisplayer scalarDisplayerFluid = ((ScalarDisplayer) scene.getDisplayerManager().createScalarDisplayer("scalar-fluid"));
        scalarDisplayerFluid.setPresentationName("scalar-fluid");
        SkewnessAngleFunction skf = ((SkewnessAngleFunction) sim.getFieldFunctionManager().getFunction("SkewnessAngle"));
        scalarDisplayerFluid.getScalarDisplayQuantity().setFieldFunction(skf);
        scalarDisplayerFluid.getParts().addObjects(sim.getPartManager().getPart("skewnessFluid"));
        scalarDisplayerFluid.getScalarDisplayQuantity().setClip(1);
        scalarDisplayerFluid.getScalarDisplayQuantity().setAutoRange(1);
        scalarDisplayerFluid.setDisplayMesh(1);
        //scalarDisplayerFluid.getScalarDisplayQuantity().setRange(new DoubleVector(new double[]{skewnessThreshold, 150.0}));
        //scalar solid displayer
        if (solids) {
            ScalarDisplayer scalarDisplayerSolid = ((ScalarDisplayer) scene.getDisplayerManager().createScalarDisplayer("scalar-solid"));
            scalarDisplayerSolid.setPresentationName("scalar-solid");
            scalarDisplayerSolid.getParts().addObjects(sim.getPartManager().getPart("skewnessSolid"));
            scalarDisplayerSolid.copyProperties(scalarDisplayerFluid);
            scalarDisplayerSolid.setPresentationName("scalar-solid");
            Legend leg = scalarDisplayerSolid.getLegend();
            scalarDisplayerFluid.getScalarDisplayQuantity().setClip(1);
            scalarDisplayerFluid.getScalarDisplayQuantity().setAutoRange(1);
            leg.setVisible(false);
        }
        sim.getSceneManager().getGroupsManager().getObject("cellQualityScenes").addObjects(new NeoObjectVector(new Object[]{scene}));
    }

    private void sceneGenVolChange(Simulation sim, double volChangeThreshold, Boolean solids, Boolean translucentGeometry) {
        sim.getSceneManager().createScene("Volume Change");
        Scene scene = sim.getSceneManager().getScene("Volume Change 1");
        scene.setPresentationName("Volume Change");
        //geometry displayer
        PartDisplayer geomDisplayer = ((PartDisplayer) scene.getDisplayerManager().createPartDisplayer("geometry", -1, 4));
        geomDisplayer.setPresentationName("geometry");
        geomDisplayer.setOutline(false);
        geomDisplayer.setSurface(true);
        geomDisplayer.setColorMode(1);
        geomDisplayer.getParts().setObjects(getGeometryParts(sim));
        if (translucentGeometry) {
            geomDisplayer.setOpacity(0.2);
        }
        //scalar fluid displayer
        ScalarDisplayer scalarDisplayerFluid = ((ScalarDisplayer) scene.getDisplayerManager().createScalarDisplayer("scalar-fluid"));
        scalarDisplayerFluid.setPresentationName("scalar-fluid");
        VolumeChangeFunction vcf = ((VolumeChangeFunction) sim.getFieldFunctionManager().getFunction("VolumeChange"));
        scalarDisplayerFluid.getScalarDisplayQuantity().setFieldFunction(vcf);
        scalarDisplayerFluid.getParts().addObjects(sim.getPartManager().getPart("volumeChangeFluid"));
        scalarDisplayerFluid.getScalarDisplayQuantity().setClip(0);
        scalarDisplayerFluid.getScalarDisplayQuantity().setAutoRange(0);
        scalarDisplayerFluid.setDisplayMesh(1);
        scalarDisplayerFluid.getScalarDisplayQuantity().setRange(new DoubleVector(new double[]{0, volChangeThreshold}));
        //scalar solid displayer
        if (solids) {
            ScalarDisplayer scalarDisplayerSolid = ((ScalarDisplayer) scene.getDisplayerManager().createScalarDisplayer("scalar-solid"));
            scalarDisplayerSolid.setPresentationName("scalar-solid");
            scalarDisplayerSolid.getParts().addObjects(sim.getPartManager().getPart("volumeChangeSolid"));
            scalarDisplayerSolid.copyProperties(scalarDisplayerFluid);
            scalarDisplayerSolid.setPresentationName("scalar-solid");
            Legend leg = scalarDisplayerSolid.getLegend();
            leg.setVisible(false);
        }
        sim.getSceneManager().getGroupsManager().getObject("cellQualityScenes").addObjects(new NeoObjectVector(new Object[]{scene}));
    }

    private void sceneGenBadCell(Simulation sim, Boolean solids, Boolean translucentGeometry) {
        sim.getSceneManager().createScene("Bad Cells");
        Scene scene = sim.getSceneManager().getScene("Bad Cells 1");
        scene.setPresentationName("Bad Cells");
        //geometry displayer
        PartDisplayer geomDisplayer = ((PartDisplayer) scene.getDisplayerManager().createPartDisplayer("geometry", -1, 4));
        geomDisplayer.setPresentationName("geometry");
        geomDisplayer.setOutline(false);
        geomDisplayer.setSurface(true);
        geomDisplayer.setColorMode(1);
        geomDisplayer.getParts().setObjects(getGeometryParts(sim));
        if (translucentGeometry) {
            geomDisplayer.setOpacity(0.2);
        }
        // fluid displayer
        PartDisplayer bcFluid = ((PartDisplayer) scene.getDisplayerManager().createPartDisplayer("badCellsFluid", -1, 4));
        bcFluid.setPresentationName("badCellsFluid");
        bcFluid.setOutline(false);
        bcFluid.setSurface(true);
        bcFluid.setColorMode(1);
        bcFluid.setMesh(true);
        bcFluid.setDisplayerColor(new DoubleVector(new double[]{0.11760000139474869, 0.5647000074386597, 1.0}));
        bcFluid.getParts().setObjects(sim.getPartManager().getPart("badCellsFluid"));
        // solid displayer
        if (solids) {
            PartDisplayer bcSolid = ((PartDisplayer) scene.getDisplayerManager().createPartDisplayer("badCellsSolidd", -1, 4));
            bcSolid.setPresentationName("badCellsSolid");
            bcSolid.setOutline(false);
            bcSolid.setSurface(true);
            bcSolid.setColorMode(1);
            bcSolid.setMesh(true);
            bcSolid.getParts().setObjects(sim.getPartManager().getPart("badCellsSolid"));
            bcSolid.setDisplayerColor(new DoubleVector(new double[]{0.8039000034332275, 0.5216000080108643, 0.24709999561309814}));
        }
        sim.getSceneManager().getGroupsManager().getObject("cellQualityScenes").addObjects(new NeoObjectVector(new Object[]{scene}));
    }

    private void sceneGenPrisms(Simulation sim, Boolean translucentGeometry) {
        sim.getSceneManager().createScene("Prism Cells");
        Scene scene = sim.getSceneManager().getScene("Prism Cells 1");
        scene.setPresentationName("Prism Cells");
        //geometry displayer
        PartDisplayer geomDisplayer = ((PartDisplayer) scene.getDisplayerManager().createPartDisplayer("geometry", -1, 4));
        geomDisplayer.setPresentationName("geometry");
        geomDisplayer.setOutline(false);
        geomDisplayer.setSurface(true);
        geomDisplayer.setColorMode(1);
        geomDisplayer.getParts().setObjects(getGeometryParts(sim));
        if (translucentGeometry) {
            geomDisplayer.setOpacity(0.2);
        }
        // prism displayer
        PartDisplayer prismDisp = ((PartDisplayer) scene.getDisplayerManager().createPartDisplayer("prism cells", -1, 4));
        prismDisp.setPresentationName("prism cells");
        prismDisp.setOutline(false);
        prismDisp.setSurface(true);
        prismDisp.setColorMode(1);
        prismDisp.setMesh(true);
        prismDisp.setDisplayerColor(new DoubleVector(new double[]{1.0, 0.41179999709129333, 0.7059000134468079}));
        prismDisp.getParts().setObjects(sim.getPartManager().getPart("prisms"));
        sim.getSceneManager().getGroupsManager().getObject("cellQualityScenes").addObjects(new NeoObjectVector(new Object[]{scene}));
    }

    private void sceneGenSurfaceQuality(Simulation sim) {
        sim.getSceneManager().createScene("Cell Quality: Surface");
        Scene scene = sim.getSceneManager().getScene("Cell Quality: Surface 1");
        scene.setPresentationName("Cell Quality: Surface");
        //scalar displayer
        ScalarDisplayer scalarDisplayerFluid = ((ScalarDisplayer) scene.getDisplayerManager().createScalarDisplayer("scalar"));
        scalarDisplayerFluid.setPresentationName("scalar");
        CellQualityFunction cqf = ((CellQualityFunction) sim.getFieldFunctionManager().getFunction("CellQuality"));
        scalarDisplayerFluid.getScalarDisplayQuantity().setFieldFunction(cqf);
        scalarDisplayerFluid.getParts().addObjects(getGeometryParts(sim));
        scalarDisplayerFluid.getScalarDisplayQuantity().setClip(0);
        scalarDisplayerFluid.getScalarDisplayQuantity().setAutoRange(0);
        scalarDisplayerFluid.setDisplayMesh(1);
        scalarDisplayerFluid.getScalarDisplayQuantity().setRange(new DoubleVector(new double[]{0.0, 1.0}));
        sim.getSceneManager().getGroupsManager().getObject("cellQualityScenes").addObjects(new NeoObjectVector(new Object[]{scene}));
        Legend fleg = scalarDisplayerFluid.getLegend();
        fleg.setReverse(true);
    }

    private void sceneGenSurfaceSkewness(Simulation sim) {
        sim.getSceneManager().createScene("Skewness: Surface");
        Scene scene = sim.getSceneManager().getScene("Skewness: Surface 1");
        scene.setPresentationName("Skewness: Surface");
        //scalar displayer
        ScalarDisplayer scalarDisplayerFluid = ((ScalarDisplayer) scene.getDisplayerManager().createScalarDisplayer("scalar"));
        scalarDisplayerFluid.setPresentationName("scalar");
        SkewnessAngleFunction skf = ((SkewnessAngleFunction) sim.getFieldFunctionManager().getFunction("SkewnessAngle"));
        scalarDisplayerFluid.getScalarDisplayQuantity().setFieldFunction(skf);
        scalarDisplayerFluid.getParts().addObjects(getGeometryParts(sim));
        scalarDisplayerFluid.getScalarDisplayQuantity().setClip(0);
        scalarDisplayerFluid.getScalarDisplayQuantity().setAutoRange(1);
        scalarDisplayerFluid.setDisplayMesh(1);
        //scalarDisplayerFluid.getScalarDisplayQuantity().setRange(new DoubleVector(new double[]{0.0, 150.0}));
        sim.getSceneManager().getGroupsManager().getObject("cellQualityScenes").addObjects(new NeoObjectVector(new Object[]{scene}));
    }

    private void sceneGenNegVolume(Simulation sim, Boolean translucentGeometry) {
        sim.getSceneManager().createScene("Negative Volume Cells");
        Scene scene = sim.getSceneManager().getScene("Negative Volume Cells 1");
        scene.setPresentationName("Negative Volume Cells");
        //geometry displayer
        PartDisplayer geomDisplayer = ((PartDisplayer) scene.getDisplayerManager().createPartDisplayer("geometry", -1, 4));
        geomDisplayer.setPresentationName("geometry");
        geomDisplayer.setOutline(false);
        geomDisplayer.setSurface(true);
        geomDisplayer.setColorMode(1);
        geomDisplayer.getParts().setObjects(getGeometryParts(sim));
        if (translucentGeometry) {
            geomDisplayer.setOpacity(0.2);
        }
        // neg vol displayer
        PartDisplayer prismDisp = ((PartDisplayer) scene.getDisplayerManager().createPartDisplayer("negative volume cells", -1, 4));
        prismDisp.setPresentationName("negative volume cells");
        prismDisp.setOutline(false);
        prismDisp.setSurface(true);
        prismDisp.setColorMode(1);
        prismDisp.setMesh(true);
        prismDisp.setDisplayerColor(new DoubleVector(new double[]{1.0, 0.41179999709129333, 0.7059000134468079}));
        prismDisp.getParts().setObjects(sim.getPartManager().getPart("volume"));
        sim.getSceneManager().getGroupsManager().getObject("cellQualityScenes").addObjects(new NeoObjectVector(new Object[]{scene}));
    }

    private void sceneGenMeshView(Simulation sim) {
        //get all boundaries
        Collection<Region> regions = sim.getRegionManager().getRegions();
        Vector<Boundary> bounds = new Vector<Boundary>();
        for (Region ri : regions) {
            Collection<Boundary> riBoundaries = ri.getBoundaryManager().getBoundaries();
            for (Boundary bi : riBoundaries) {
                bounds.add(bi);
            }
        }

        //get the surface representation for the volume mesh
        SurfaceRep surfRep = null;
        FvRepresentation volume = ((FvRepresentation) sim.getRepresentationManager().getObject("Volume Mesh"));
        volume.extractBoundarySurface(new NeoObjectVector(regions.toArray()));
        surfRep = ((SurfaceRep) sim.getRepresentationManager().getObject("Extracted Surface"));

//        try {
//            surfRep = ((SurfaceRep) sim.getRepresentationManager().getObject("Initial Surface"));
//        } catch (Exception e) {
//            sim.println("Initial Surface representation not found!");
//            sim.println("Cannot continue with mesh scene generation.");
//            return;
//        }
        DoubleVector extents = surfRep.getExtents(bounds);

        //calculate the origin of the extents
        double originX = (extents.get(1) + extents.get(0)) / 2;
        double originY = (extents.get(3) + extents.get(2)) / 2;
        double originZ = (extents.get(5) + extents.get(4)) / 2;

        //generate the plane sections
        PlaneSection XYsection = (PlaneSection) sim.getPartManager().createImplicitPart(new NeoObjectVector(new Object[]{}), new DoubleVector(new double[]{0.0, 0.0, 1.0}), new DoubleVector(new double[]{originX, originY, originZ}), 0, 1, new DoubleVector(new double[]{0.0}));
        PlaneSection XZsection = (PlaneSection) sim.getPartManager().createImplicitPart(new NeoObjectVector(new Object[]{}), new DoubleVector(new double[]{0.0, 1.0, 0.0}), new DoubleVector(new double[]{originX, originY, originZ}), 0, 1, new DoubleVector(new double[]{0.0}));
        PlaneSection YZsection = (PlaneSection) sim.getPartManager().createImplicitPart(new NeoObjectVector(new Object[]{}), new DoubleVector(new double[]{1.0, 0.0, 0.0}), new DoubleVector(new double[]{originX, originY, originZ}), 0, 1, new DoubleVector(new double[]{0.0}));
        XYsection.setPresentationName("Zsection");
        XZsection.setPresentationName("Ysection");
        YZsection.setPresentationName("Xsection");
        XYsection.getInputParts().setObjects(regions);
        XZsection.getInputParts().setObjects(regions);
        YZsection.getInputParts().setObjects(regions);
        sim.getPartManager().getGroupsManager().getObject("cellQualityParts").addObjects(new NeoObjectVector(new Object[]{XYsection, XZsection, YZsection}));

        //generate cell surfaces
        CellSurfacePart XYcellSurface = sim.getPartManager().createCellSurfacePart(new NeoObjectVector(new Object[]{XYsection}));
        CellSurfacePart XZcellSurface = sim.getPartManager().createCellSurfacePart(new NeoObjectVector(new Object[]{XZsection}));
        CellSurfacePart YZcellSurface = sim.getPartManager().createCellSurfacePart(new NeoObjectVector(new Object[]{YZsection}));
        XYcellSurface.setPresentationName("Zsurface");
        XZcellSurface.setPresentationName("Ysurface");
        YZcellSurface.setPresentationName("Xsurface");
        sim.getPartManager().getGroupsManager().getObject("cellQualityParts").addObjects(new NeoObjectVector(new Object[]{XYcellSurface, XZcellSurface, YZcellSurface}));

        String nameString[] = {"Z", "Y", "X"};
        CellSurfacePart cellSurfaces[] = {XYcellSurface, XZcellSurface, YZcellSurface};

        int i = 0;
        for (String name : nameString) {
            sim.getSceneManager().createScene("Mesh View: " + name);
            Scene scene = sim.getSceneManager().getScene("Mesh View: " + name + " 1");
            scene.setPresentationName("Mesh View: " + name);

            //geometry displayer
            PartDisplayer geomDisplayer = ((PartDisplayer) scene.getDisplayerManager().createPartDisplayer("geometry", -1, 4));
            geomDisplayer.setPresentationName("geometry");
            geomDisplayer.setOutline(false);
            geomDisplayer.setSurface(true);
            geomDisplayer.setColorMode(1);
            geomDisplayer.getParts().setObjects(getGeometryParts(sim));
            geomDisplayer.setOpacity(0.2);

            //cell surface displayer
            PartDisplayer meshDisp = ((PartDisplayer) scene.getDisplayerManager().createPartDisplayer("mesh", -1, 4));
            meshDisp.setPresentationName("mesh");
            meshDisp.setOutline(false);
            meshDisp.setSurface(true);
            meshDisp.setColorMode(3);
            meshDisp.setMesh(true);
            meshDisp.getParts().setObjects(cellSurfaces[i]);
            sim.getSceneManager().getGroupsManager().getObject("meshScenes").addObjects(new NeoObjectVector(new Object[]{scene}));

            i = i + 1;
        }

        sim.getRepresentationManager().remove(surfRep);

    }

    private Collection<Boundary> getGeometryParts(Simulation sim) {
        Collection<Boundary> boundaries = new ArrayList<Boundary>();
        Collection<Region> loopRegions = sim.getRegionManager().getRegions();
        for (Region ri : loopRegions) {
            if (!(ri instanceof ShellRegion)) {
                Collection<Boundary> bir = ri.getBoundaryManager().getBoundaries();
                for (Boundary bi : bir) {
                    if ((bi.getBoundaryType() instanceof WallBoundary) || (bi.getBoundaryType() instanceof InternalBoundary) || (bi.getBoundaryType() instanceof ContactBoundary)) {
                        boundaries.add(bi);
                    }
                }
            }
        }
        return boundaries;
    }

    private void cellQualityHistogram(Simulation sim, int version) {
        HistogramPlot HP = sim.getPlotManager().createHistogramPlot();
        HP.setPresentationName("Cell Quality Histogram");

        sim.getPlotManager().getGroupsManager().getObject("cellQualityPlots").addObjects(new NeoObjectVector(new Object[]{HP}));
        Collection<Boundary> boundaries = new ArrayList<Boundary>();
        Collection<Region> loopRegions = sim.getRegionManager().getRegions();
        for (Region ri : loopRegions) {
            Collection<Boundary> bir = ri.getBoundaryManager().getBoundaries();
            boundaries.addAll(bir);
        }
        HP.getParts().setObjects(loopRegions);
        HP.getParts().addObjects(boundaries);
        HP.setTitle("Cell Quality");
        Axes axes = HP.getAxes();
        Axis yAxis = axes.getYAxis();
        AxisTitle yAxisTitle = yAxis.getTitle();
        yAxisTitle.setText("Number of Cells");
        HistogramAxisType histogramAxisType = HP.getXAxisType();
        histogramAxisType.setNumberOfBin(20);
        FieldFunctionUnits FFu = histogramAxisType.getBinFunction();
        CellQualityFunction cqf = ((CellQualityFunction) sim.getFieldFunctionManager().getFunction("CellQuality"));
        FFu.setFieldFunction(cqf);
        PlotUpdate PU = HP.getPlotUpdate();
        PU.setEnabled(false);
        HP.setAggregateParts(true);
    }

    private void skewnessHistogram(Simulation sim) {
        HistogramPlot HP = sim.getPlotManager().createHistogramPlot();
        HP.setPresentationName("Skewness Histogram");
        Collection<Boundary> boundaries = new ArrayList<Boundary>();
        Collection<Region> loopRegions = sim.getRegionManager().getRegions();
        for (Region ri : loopRegions) {
            Collection<Boundary> bir = ri.getBoundaryManager().getBoundaries();
            boundaries.addAll(bir);
        }
        HP.getParts().setObjects(loopRegions);
        HP.getParts().addObjects(boundaries);
        HP.setTitle("Skewness Angle");
        Axes axes = HP.getAxes();
        Axis yAxis = axes.getYAxis();
        AxisTitle yAxisTitle = yAxis.getTitle();
        yAxisTitle.setText("Number of Cells");
        HistogramAxisType histogramAxisType = HP.getXAxisType();
        histogramAxisType.setNumberOfBin(20);
        yAxis.setLogarithmic(true);
        FieldFunctionUnits FFu = histogramAxisType.getBinFunction();
        SkewnessAngleFunction skf = ((SkewnessAngleFunction) sim.getFieldFunctionManager().getFunction("SkewnessAngle"));
        FFu.setFieldFunction(skf);
        sim.getPlotManager().getGroupsManager().getObject("cellQualityPlots").addObjects(new NeoObjectVector(new Object[]{HP}));
        PlotUpdate PU = HP.getPlotUpdate();
        PU.setEnabled(false);
        HP.setAggregateParts(true);
    }

    private void volumeChangeHistogram(Simulation sim) {
        HistogramPlot HP = sim.getPlotManager().createHistogramPlot();
        HP.setPresentationName("Volume Change Histogram");
        Collection<Boundary> boundaries = new ArrayList<Boundary>();
        Collection<Region> loopRegions = sim.getRegionManager().getRegions();
        for (Region ri : loopRegions) {
            Collection<Boundary> bir = ri.getBoundaryManager().getBoundaries();
            boundaries.addAll(bir);
        }
        HP.getParts().setObjects(loopRegions);
        HP.getParts().addObjects(boundaries);
        HP.setTitle("Volume Change");
        Axes axes = HP.getAxes();
        Axis yAxis = axes.getYAxis();
        yAxis.setLogarithmic(true);
        AxisTitle yAxisTitle = yAxis.getTitle();
        yAxisTitle.setText("Number of Cells");
        Axis xAxis = axes.getXAxis();
        xAxis.setLogarithmic(true);
        HistogramAxisType histogramAxisType = HP.getXAxisType();
        histogramAxisType.setNumberOfBin(1000);
        histogramAxisType.setBinMode(0);
        HistogramRange HR = histogramAxisType.getHistogramRange();
        HR.setRange(new DoubleVector(new double[]{1.0E-5, 1.0}));
        FieldFunctionUnits FFu = histogramAxisType.getBinFunction();
        VolumeChangeFunction vcf = ((VolumeChangeFunction) sim.getFieldFunctionManager().getFunction("VolumeChange"));
        FFu.setFieldFunction(vcf);
        sim.getPlotManager().getGroupsManager().getObject("cellQualityPlots").addObjects(new NeoObjectVector(new Object[]{HP}));
        PlotUpdate PU = HP.getPlotUpdate();
        PU.setEnabled(false);
        HP.setAggregateParts(true);
    }

    private void disableImmediateMode(Simulation sim) {
        ArrayList<String> scenes = new ArrayList<String>(Arrays.asList("Cell Quality: Volume", "Skewness: Volume", "Volume Change", "Skewness: Surface", "Cell Quality: Surface", "Negative Volume Cells", "Prism Cells", "Bad Cells", "Mesh View: XY", "Mesh View: XZ", "Mesh View: YZ"));

        for (int i = 0; i < scenes.size(); i++) {
            try {
                Scene scene = sim.getSceneManager().getScene(scenes.get(i));
                Collection<Displayer> displayers = scene.getDisplayerManager().getObjects();
                for (Displayer di : displayers) {
                    di.setImmediateModeRendering(false);
                }
            } catch (Exception e) {
                //sim.println("Error disabling immediate mode in scene: " + scenes.get(i));
            }
        }

    }

    private void starView(Simulation sim) {
        String dir = sim.getSessionDir();
        String name = sim.getPresentationName();
        String sep = System.getProperty("file.separator");
        //ArrayList<String> scenes = new ArrayList<String>(Arrays.asList("Cell Quality: Volume", "Skewness: Volume", "Volume Change", "Skewness: Surface", "Cell Quality: Surface", "Negative Volume Cells", "Prism Cells", "Bad Cells", "Mesh View: X", "Mesh View: Z", "Mesh View: Y"));
        ArrayList<String> scenes = new ArrayList<String>(Arrays.asList("Negative Volume Cells"));
        for (int i = 0; i < scenes.size(); i++) {
            try {
                Scene scene = sim.getSceneManager().getScene(scenes.get(i));
                if (i == 0) {
                    scene.export3DSceneFileAndWait(dir + sep + name + "_meshQualityScenes.sce", scenes.get(i), "", false, true);
                } else {
                    scene.export3DSceneFileAndWait(dir + sep + name + "_meshQualityScenes.sce", scenes.get(i), "", true, true);
                }

            } catch (Exception e) {
                sim.println("Error exporting scene: " + scenes.get(i));
            }
        }

        ArrayList<String> plots = new ArrayList<String>(Arrays.asList("Cell Quality Histogram", "Skewness Histogram", "Volume Change Histogram"));
        for (int i = 0; i < plots.size(); i++) {
            try {
                HistogramPlot HP = ((HistogramPlot) sim.getPlotManager().getPlot(plots.get(i)));
                HP.encode(dir + sep + name + "_" + HP.getPresentationName().replaceAll(" ", "_").replaceAll(":", "") + ".png", "png", 800, 600);
            } catch (Exception e) {
                sim.println("Error exporting plot: " + plots.get(i));
            }
        }
    }

    private void writeOutput(Simulation sim) {
        String fileRoot = sim.getSessionPath().replaceFirst(".sim", "");
        // Output to file
        String fs = System.getProperty("file.separator");
        String name = fileRoot + "_qualityInfo.txt";
        String newline = System.getProperty("line.separator");
        try {
            // Create file
            FileWriter fstream = new FileWriter(name, Boolean.FALSE);
            BufferedWriter out = new BufferedWriter(fstream);
            for (int i = 0; i < output.size(); i++) {
                out.write(output.get(i));
                out.write(newline);
            }
            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void saveSim(Simulation sim) {
        //get path
        String simPath = sim.getSessionPath();
        String dir = sim.getSessionDir();
        String sep = System.getProperty("file.separator");
        //save sim file
        sim.saveState(simPath);
    }

    private void closeScenes(Simulation sim) {
        ArrayList<String> scenes = new ArrayList<String>(Arrays.asList("Cell Quality: Volume", "Skewness: Volume", "Volume Change", "Skewness: Surface", "Cell Quality: Surface", "Negative Volume Cells", "Prism Cells", "Bad Cells", "Mesh View: XY", "Mesh View: XZ", "Mesh View: YZ"));

        for (int i = 0; i < scenes.size(); i++) {
            try {
                Scene scene = sim.getSceneManager().getScene(scenes.get(i));
                scene.close(true);
            } catch (Exception e) {
            }
        }
    }

    private int version(Simulation sim) {
        String[] versionField = sim.getStarVersion().toString().split(" ");
        String version = versionField[7];
        version = version.replace("'", "");
        version = version.replace(",", "");
        version = version.replace(".", "z");
        String[] versionValues = version.split("z");
        version = versionValues[0] + versionValues[1];
        int versionInt = Integer.parseInt(version);
        return versionInt;
    }
}
