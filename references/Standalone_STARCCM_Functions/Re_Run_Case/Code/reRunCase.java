/**
 * Update of runCase.java using MacroUtils v3.
 * 
 * @author Pablo Fernandez (pfernandez@theansweris27.com)
 */

import macroutils.*;

public class reRunCase extends MacroUtils {
    
    private int xres = 1280;
    private int yres = 720;

    public void execute() {
        _initUtils();
        clearSolutionHistory();
        //executeWrapAndMesh();
        runCase(true);
        saveSimWithSuffix("@finished");
        postProcessing();
    }
    
    // Generate volume mesh and save simulation.
    void executeWrapAndMesh() {
        genVolumeMesh();
        saveSimWithSuffix("@meshed");
    }
    
    // Create elements for post-processing.
    void postProcessing() {
        hardCopyPictures(xres, yres, "\\imagenes\\" + simTitle);
    }
}