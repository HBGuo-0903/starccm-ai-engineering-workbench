// STAR-CCM+ macro: ExportPlotsScenes.java
// Written by STAR-CCM+ 12.02.011
//@author : Yogesh Nalam (nalam_y)

package macro;

import java.io.File;
import javax.swing.JFileChooser;
import star.common.*;
import star.vis.*;

public class ExportPlotsScenes extends StarMacro 
{

	public void execute() {
	execute0();
	}
	
	private void execute0() 
	{
	
        Simulation sim = getActiveSimulation();
	
        JFileChooser f = new JFileChooser();
        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
        f.showSaveDialog(null);
	
        File afilePath = f.getSelectedFile();
	String fileName;
	String filePath = afilePath.getPath();
        String sep = System.getProperty("file.separator");
        String FilePath;
        
        
//All Plots Exporting	
// for loop runs through the plotManager
	for(StarPlot plot : sim.getPlotManager().getObjects())
		{
		fileName =  plot.getPresentationName();
                FilePath = filePath+sep+fileName;
                plot.encode(resolvePath(FilePath + ".png"), "png" ,600,600);
		}

//Scalar Scene Exporting	
// for loop runs through the sceneManager
	for(Scene scene : sim.getSceneManager().getObjects())
		{
		
                try{
//Looping through the displayers in the scene
                for(Displayer d : scene.getDisplayerManager().getObjects())
                {
//Checking if theere is a displayer with name containing "scalar"
                    if(d.getPresentationName().matches("(?i).*scalar.*"))
                        
                {
                    fileName =  scene.getPresentationName();
                    FilePath = filePath+sep+fileName;
                    scene.printAndWait(resolvePath(FilePath + ".png"), 1, 1455, 712, true, false);
                }
                } 
                }catch(Exception e){}
                }
    }
	
}


