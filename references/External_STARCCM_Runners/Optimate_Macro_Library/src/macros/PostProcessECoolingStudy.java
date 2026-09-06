/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import star.base.neo.DoubleVector;
import star.base.neo.IntVector;
import star.common.LabCoordinateSystem;
import star.common.PrimitiveFieldFunction;
import star.common.StarPlot;
import star.common.Units;
import star.vis.CurrentView;
import star.vis.Legend;
import star.vis.PartDisplayer;
import star.vis.Scene;
import star.vis.SourceSeed;
import star.vis.StreamPart;
import star.vis.VectorDisplayer;

/**
 *
 * @author aarong
 */
public class PostProcessECoolingStudy extends PostProcessStudyDesigns {

    List<String> scenes;

    @Override
    protected boolean modifyScene(Scene scene) {
        return scenes.contains(scene.getPresentationName());
    }

    @Override
    protected boolean modifyPlot(StarPlot plot) {
        return false;
    }

    @Override
    protected void applyModifications(Scene scene) {

        if (scene.getPresentationName().equals("Vector - Velocity")) {
            velVector(scene);
        } else {
            streamlines1(scene);
        }
    }

    @Override
    public void execute() {
        initScenes();

        try {
            getActiveSimulation();
            setSceneResolution(new Dimension(1287, 900));
            postProcess();
        } catch (Exception ex) {
            print(ex);
        }
    }

    private void initScenes() {
        scenes = new ArrayList<>();

        scenes.add("Vector - Velocity");
        scenes.add("Streamlines - Velocity");
        scenes.add("Streamlines - Velocity (Tubes)");
    }

    private void velVector(Scene scene) {
        scene.getDisplayerManager().getDisplayer("CasingTransparent").setLineWidth(1.0);

        VectorDisplayer vectorDisplayer_1
                = ((VectorDisplayer) scene.getDisplayerManager().getDisplayer("Vector 1"));

        PrimitiveFieldFunction primitiveFieldFunction_0
                = ((PrimitiveFieldFunction) getSim().getFieldFunctionManager().getFunction("Temperature"));

        vectorDisplayer_1.getColoringScalar().setFieldFunction(primitiveFieldFunction_0);

        Units units_0
                = ((Units) getSim().getUnitsManager().getObject("C"));

        vectorDisplayer_1.getColoringScalar().setUnits(units_0);

        vectorDisplayer_1.getColoringScalar().setRange(new DoubleVector(new double[]{313.15, 358.15}));

        Legend legend_0
                = vectorDisplayer_1.getLegend();

        legend_0.setNumberOfLabels(4);
    }

    private void streamlines1(Scene scene) {

        scene.getDisplayerManager().getDisplayer("CasingTransparentOutline").setLineWidth(1.0);
        scene.getDisplayerManager().getDisplayer("Streamline Stream 1").setLineWidth(2.0);
        
        StreamPart streamPart_0
                = ((StreamPart) getSim().getPartManager().getObject("streamline"));

        SourceSeed sourceSeed_0
                = streamPart_0.getSourceSeed();

        sourceSeed_0.setNGridPoints(new IntVector(new int[]{20, 10}));
        
        PartDisplayer partDisplayer_6 = ((PartDisplayer) scene.getDisplayerManager().getDisplayer("CasingTransparent"));

        partDisplayer_6.setOpacity(0.05);

        CurrentView currentView_0 = scene.getCurrentView();

        LabCoordinateSystem labCoordinateSystem_0 = getSim().getCoordinateSystemManager().getLabCoordinateSystem();

        currentView_0.setCoordinateSystem(labCoordinateSystem_0);

        currentView_0.setInput(new DoubleVector(new double[]{-0.031207207245820368, 0.04012122209867861, -0.034453857289997336}), new DoubleVector(new double[]{-0.20261643706179214, 0.04012122209867861, 0.06450930295483728}), new DoubleVector(new double[]{0.4999999820728632, 0.0, 0.8660254141346757}), 0.05166913975984367, 0);

    }

}
