// Simcenter STAR-CCM+ macro: CreateVectorScene.java
// Written by Simcenter STAR-CCM+ 15.06.008
package macro;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;
import star.flow.*;

public class ExportVelocity extends StarMacro {

    Simulation simulation;

    public void execute() {
        simulation = getActiveSimulation();
        createVelocityVectorScene("Instantaneous Velocity", true, 1);
        runTime(2);
    }

    private void createVelocityVectorScene(String name, Boolean recordScene, int frequency) {
        createEmptyVectorScene(name);
        Scene scene = getScene(name);
        standardizeZoomedScene(scene);

        if (recordScene) {
            createSubFolder("images");
            recordScene(scene, "images", name, frequency);
        }

        VectorDisplayer vectorDisplayer = ((VectorDisplayer) scene.getDisplayerManager().getDisplayer(name));
        vectorDisplayer.initialize();
        addFieldFunctionToVectorDisplayer(vectorDisplayer, "Velocity", 0, 1.0);
        addPartsToDisplayer(vectorDisplayer);
        setVectorDisplayerToTangential(vectorDisplayer);
        setVectorDisplayerToLIC(vectorDisplayer);

    }

    private void setVectorDisplayerToTangential(VectorDisplayer vectorDisplayer) {
        vectorDisplayer.setDisplayMode(VectorDisplayMode.VECTOR_DISPLAY_MODE_LIC);
    }

    private void setVectorDisplayerToLIC(VectorDisplayer vectorDisplayer) {
        vectorDisplayer.setConstrain(VectorProjectionMode.TANGENTIAL);
    }

    private void addFieldFunctionToVectorDisplayer(VectorDisplayer vectorDisplayer, String fieldFunctionName,
            double min, double max) {

        // Set Colorbar
        Legend legend = vectorDisplayer.getLegend();
        BlueRedLookupTable blueRedLookupTable = ((BlueRedLookupTable) simulation.get(LookupTableManager.class)
                .getObject("blue-red"));
        legend.setLookupTable(blueRedLookupTable);
        legend.setLevels(200);
        legend.setNumberOfLabels(5);
        legend.setLabelFormat("%-#6.2f");

        FieldFunction fieldFunction = simulation.getFieldFunctionManager().getFunction(fieldFunctionName);

        vectorDisplayer.getVectorDisplayQuantity().setFieldFunction(fieldFunction);
        vectorDisplayer.getVectorDisplayQuantity().setRange(new DoubleVector(new double[] { min, max }));
    }

    private void runTime(double time) {

        simulation = getActiveSimulation();
        PhysicalTimeStoppingCriterion physicalTimeStoppingCriterion = ((PhysicalTimeStoppingCriterion) simulation
                .getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Physical Time"));

        double curTime = simulation.getSolution().getPhysicalTime();
        physicalTimeStoppingCriterion.getMaximumTime().setValue(curTime + time);

        simulation.getSimulationIterator().run();
    }

    private void addPartsToDisplayer(Displayer displayer) {

        PlaneSection planeSection = ((PlaneSection) simulation.getPartManager().getObject("XZ Plane"));
        displayer.getInputParts().setQuery(null);
        displayer.getInputParts().setObjects(planeSection);
    }

    private void setSceneViewWide(Scene scene) {
        CurrentView currentView = scene.getCurrentView();
        currentView.setInput(new DoubleVector(new double[] { 0, 0, 0.07 }),
                new DoubleVector(new double[] { 0, 0, 0.54 }),
                 new DoubleVector(new double[] { 0.0, 1.0, 0.0 }), 0.12,
                1, 30.0);
        scene.setViewOrientation(new DoubleVector(new double[] { 0.0, -1.0, 0.0 }),
                new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));
    }

    private void setSceneViewZoomed(Scene scene) {
        CurrentView currentView = scene.getCurrentView();
        currentView.setInput(
            new DoubleVector(new double[] { -0.037, 0.005, 0.129 }),
            new DoubleVector(new double[] { -0.037, -0.47, 0.129 }), 
            new DoubleVector(new double[] { 0.0, 1.0, 0.0 }), 
            0.05,
            1, 30.0);

        scene.setViewOrientation(
            new DoubleVector(new double[] { 0.0, -1.0, 0.0 }),
            new DoubleVector(new double[] { 0.0, 0.0, 1.0 })
                );
    }

    private void recordScene(Scene scene, String path, String name, int frequency) {
        SceneUpdate sceneUpdate = scene.getSceneUpdate();
        sceneUpdate.setSaveAnimation(true);
        sceneUpdate.setAnimationFilePath(path);
        sceneUpdate.setAnimationFilenameBase(name);
        sceneUpdate.getUpdateModeOption().setSelected(StarUpdateModeOption.Type.TIMESTEP);
        TimeStepUpdateFrequency timeStepUpdateFrequency = sceneUpdate.getTimeStepUpdateFrequency();
        timeStepUpdateFrequency.setTimeSteps(frequency);

    }

    private void createEmptyVectorScene(String name) {

        // Initialize and select scene
        simulation.getSceneManager().createVectorScene(name, "Outline", name);
        Scene scene = simulation.getSceneManager().getSceneByName(name);
        scene.setPresentationName(name);

        PartDisplayer partDisplayer = ((PartDisplayer) scene.getDisplayerManager().getDisplayer("Outline 1"));
        partDisplayer.initialize();
    }

    private void createEmptyScalarScene(String name) {

        // Initialize and select scene
        simulation.getSceneManager().createScalarScene(name, "Outline", name);
        Scene scene = simulation.getSceneManager().getSceneByName(name);
        scene.setPresentationName(name);

        PartDisplayer partDisplayer = ((PartDisplayer) scene.getDisplayerManager().getDisplayer("Outline 1"));
        partDisplayer.initialize();
    }

    private Scene getScene(String name) {
        return simulation.getSceneManager().getSceneByName(name);
    }

    private void removeLogoFromScene(Scene scene) {
        LogoAnnotation logoAnnotation = ((LogoAnnotation) simulation.getAnnotationManager().getObject("Logo"));
        scene.getAnnotationPropManager().removePropsForAnnotations(logoAnnotation);
    }

    private void addPhysicalTimeToScene(Scene scene) {
        // Set Physical Time annotation
        PhysicalTimeAnnotation physicalTimeAnnotation = ((PhysicalTimeAnnotation) simulation.getAnnotationManager()
                .getObject("Solution Time"));
        PhysicalTimeAnnotationProp physicalTimeAnnotationProp = (PhysicalTimeAnnotationProp) scene
                .getAnnotationPropManager().createPropForAnnotation(physicalTimeAnnotation);
    }

    private void setSceneHardcopySize(Scene scene, int width, int height) {
        SceneUpdate sceneUpdate = scene.getSceneUpdate();
        HardcopyProperties hardcopyProperties = sceneUpdate.getHardcopyProperties();
        hardcopyProperties.setCurrentResolutionWidth(width);
        hardcopyProperties.setCurrentResolutionHeight(height);
    }

    private void standardizeScene(Scene scene) {
        scene.initializeAndWait();
        scene.resetCamera();
        scene.close();
        scene.setBackgroundColorMode(BackgroundColorMode.SOLID);
        setSceneViewWide(scene);
        removeLogoFromScene(scene);
        addPhysicalTimeToScene(scene);
        setSceneHardcopySize(scene, 1080, 1560);
    }

    private void standardizeZoomedScene(Scene scene) {
        scene.initializeAndWait();
        scene.resetCamera();
        scene.close();
        scene.setBackgroundColorMode(BackgroundColorMode.SOLID);
        setSceneViewZoomed(scene);
        removeLogoFromScene(scene);
        addPhysicalTimeToScene(scene);
        setSceneHardcopySize(scene, 1080, 1560);
    }

    private void createSubFolder(String folderName) {
        try {
            Files.createDirectories(Paths.get(simulation.getSessionDir() + "/" + folderName));
        } catch (Exception ex) {
            simulation.println(ex);
        }
    }
}