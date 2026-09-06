/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import java.awt.Dimension;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import star.common.Simulation;
import star.common.StarMacro;
import star.common.StarPlot;
import star.vis.Scene;

/**
 *
 * @author aarong
 */
public abstract class PostProcessStudyDesigns extends StarMacro {

    private Simulation _sim;
    private int _sceneXRes = 1280;
    private int _sceneYRes = 1024;
    private int _sceneMag = 1;
    private int _plotXRes = 1280;
    private int _plotYRes = 1024;
    private boolean _save = true;
    private String name;
    private String designN;
    private String postDir;
    private final String fs = File.separator;

    protected void postProcess() throws Exception {
        initStrings();
        modifyScenes();
        modifyPlots();
    }

    protected void print(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        _sim.println(sw.toString());
    }

    @Override
    public Simulation getActiveSimulation() {
        _sim = super.getActiveSimulation();
        return _sim;
    }

    protected abstract boolean modifyScene(Scene scene);

    protected abstract boolean modifyPlot(StarPlot plot);

    protected void applyModifications(Scene scene) {
    }

    protected void applyModifications(StarPlot plot) {
    }

    protected Simulation getSim() {
        return _sim;
    }

    protected void setSceneResolution(Dimension dim) {
        _sceneXRes = dim.width;
        _sceneYRes = dim.height;
    }

    protected void setPlotResolution(Dimension dim) {
        _plotXRes = dim.width;
        _plotYRes = dim.height;
    }

    protected void setSceneMag(int mag) {
        _sceneMag = mag;
    }

    protected void save(boolean b) {
        _save = b;
    }

    private void initStrings() {
        File designDir = _sim.getSessionDirFile().getParentFile();
        designN = designDir.getName().replace("-ERROR", "");

        if (designDir.getAbsolutePath().contains("RESULTS_BACKUP_")) {
            postDir = designDir.getParentFile().getParentFile().getParent() + fs + "POST_0" + fs + designN + fs + "star" + fs;
        } else {
            postDir = designDir.getParentFile().getParent() + fs + "POST_0" + fs + designN + fs + "star" + fs;
        }
    }

    private void setName(Scene scene) {
        name = scene.getPresentationName().replace(" ", "_") + "-scene.png";
    }

    private void setName(StarPlot plot) {
        name = plot.getPresentationName().replace(" ", "_") + "-plot.png";
    }

    private void save(Scene scene) {
        scene.printAndWait(postDir + name, _sceneMag, _sceneXRes, _sceneYRes);
    }

    private void save(StarPlot plot) {
        plot.encode(name, _plotXRes, _plotYRes);
    }

    private void modifyScenes() throws Exception {
        for (Scene s : _sim.getSceneManager().getScenes()) {
            if (modifyScene(s)) {
                setName(s);
                applyModifications(s);
                if (_save) {
                    save(s);
                }
            }
        }
    }

    private void modifyPlots() throws Exception {
        for (StarPlot s : _sim.getPlotManager().getPlots()) {
            if (modifyPlot(s)) {
                setName(s);
                applyModifications(s);
                if (_save) {
                    save(s);
                }
            }
        }
    }
}
