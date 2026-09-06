/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import star.common.Simulation;
import star.common.StarMacro;
import star.vis.Scene;

/**
 *
 * @author aarong
 */
public class StudyPost extends StarMacro {

    String[] scenesToSave = new String[]{"Geometry - Design",
        "Scalar - Boundary Heat Flux",
        "Scalar - Temperature No Heatsinks",
        "Scalar - Temperature With Heatsinks",
        "Streamlines - Velocity", "Streamlines - Velocity (Tubes)",
        "Vector - Velocity",
        "Volume - Temp",
        "Volume - Temp 2"};

    Simulation sim;
    String name = "Volume_-_Temp-scene.png";
    String designN;
    String postDir;
    String fs = File.separator;

    @Override
    public void execute() {

        try {
            sim = getActiveSimulation();
            initStrings();

            for (Scene s : sim.getSceneManager().getScenes()) {
                sim.println(s.getPresentationName());
                if (save(s)) {
                    sim.println("found");
                    turnOnLICDisplayer(s);
                    modifyLineWidth(s);
                    s.printAndWait(postDir + name, 1, 1287, 900);
                } else {
                    sim.println("not found");
                }
            }
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            sim.println(sw.toString());
        }

    }

    private boolean save(Scene scene) {
        boolean save = false;

        for (String s : scenesToSave) {
            sim.println(s);
            if (scene.getPresentationName().equals(s)) {
                save = true;
            }
        }

        if (save) {
            name = scene.getPresentationName().replace(" ", "_") + "-scene.png";
        }

        return save;
    }

    private void turnOnLICDisplayer(Scene scene) {
        if (scene.getPresentationName().equals("Vector - Velocity")) {
            scene.getDisplayerManager().getDisplayer("Vector 1").setVisibilityOverrideMode(0);
        }
    }

    private void modifyLineWidth(Scene scene) {
        try {
            if (scene.getPresentationName().equals("Streamlines - Velocity")) {
                scene.getDisplayerManager().getDisplayer("Streamline Stream 1").setLineWidth(2.0);
            }

            scene.getDisplayerManager().getDisplayer("CasingTransparentOutline").setLineWidth(1.0);
        } catch (Exception ex) {

        }
    }

    private void initStrings() {
        File designDir = sim.getSessionDirFile().getParentFile();
        designN = designDir.getName().replace("-ERROR", "");

        if (designDir.getAbsolutePath().contains("RESULTS_BACKUP_")) {
            postDir = designDir.getParentFile().getParentFile().getParent() + fs + "POST_0" + fs + designN + fs + "star" + fs;
        } else {
            postDir = designDir.getParentFile().getParent() + fs + "POST_0" + fs + designN + fs + "star" + fs;
        }
    }

}
