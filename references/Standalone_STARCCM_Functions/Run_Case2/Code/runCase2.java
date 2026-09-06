/**
 * Update of runCase.java using MacroUtils v3.
 * 
 * @author Pablo Fernandez (pfernandez@theansweris27.com)
 */

import macroutils.*;

public class runCase2 extends MacroUtils {
    
    private final int xres = 1280;
    private final int yres = 720;

    public void execute() {
        _initUtils();
        clearSolution();
        initializeSolution();
        runCase(true);
        saveSimWithSuffix("@finished");
        postProcessing();
    }
    
    // Create elements for post-processing.
    void postProcessing() {
        hardCopyPictures(xres, yres, "\\imagenes\\" + simTitle);
    }
}