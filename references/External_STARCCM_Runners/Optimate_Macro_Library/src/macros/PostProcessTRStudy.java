/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import star.common.StarPlot;
import star.vis.Legend;
import star.vis.LookupTableManager;
import star.vis.PredefinedLookupTable;
import star.vis.ScalarDisplayer;
import star.vis.Scene;

/**
 *
 * @author aarong
 */
public class PostProcessTRStudy extends PostProcessStudyDesigns {

    @Override
    public void execute() {
        try {
            getActiveSimulation();
//            setSceneResolution(new Dimension(1240, 964));
//            save(false);
//            postProcess();
            save();
        } catch (Exception ex) {
            print(ex);
        }
    }

    @Override
    protected boolean modifyScene(Scene scene) {
        List<String> scenes = new ArrayList<>();

//        scenes.add("Mach");
        scenes.add("Scalar - Mach");
        scenes.add("Scalar - Mach Basket");
        scenes.add("Scalar - Mach Cascade");
        scenes.add("Scalar - Mach Plume");
        scenes.add("Scalar - Total Pressure");
        scenes.add("Scalar - Total Pressure Basket");
        scenes.add("Scalar - Total Pressure Cascade");
        scenes.add("Scalar - Total Pressure Plume");
//        scenes.add("Total Pressure");

        return scenes.contains(scene.getPresentationName());
    }

    @Override
    protected boolean modifyPlot(StarPlot plot) {
        return false;
    }

    @Override
    protected void applyModifications(Scene scene) {
        
        ScalarDisplayer scalarDisplayer_0 = ((ScalarDisplayer) scene.getDisplayerManager().getDisplayer("Scalar 1"));
        Legend legend_0 = scalarDisplayer_0.getLegend();
        PredefinedLookupTable predefinedLookupTable_0;

        if (scene.getPresentationName().toLowerCase().contains("mach")) {
            predefinedLookupTable_0 = ((PredefinedLookupTable) getSim().get(LookupTableManager.class).getObject("blue-red balanced"));
        } else {
            predefinedLookupTable_0 = ((PredefinedLookupTable) getSim().get(LookupTableManager.class).getObject("blue-red bright"));
        }

        legend_0.setLookupTable(predefinedLookupTable_0);

        legend_0.setLevels(512);
        
        scalarDisplayer_0.setFillMode(1);
    }
    
    private void save() {
        for (Scene s : getSim().getSceneManager().getScenes()) {
            s.printAndWait(getSim().getSessionDirFile().getParent() + File.separator + s.getPresentationName() + ".png", 1, 1240, 964);
        }
    }

}
