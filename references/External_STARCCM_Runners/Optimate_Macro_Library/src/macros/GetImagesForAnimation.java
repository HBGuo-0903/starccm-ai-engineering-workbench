/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package macros;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Scanner;
import star.common.Simulation;
import star.common.StarMacro;

/**
 *
 * @author aarong
 */
public class GetImagesForAnimation extends StarMacro {

    String fs = File.separator;
    String imgName = "Scalar_-_Temperature_With_Heatsinks-scene";
    String ext = ".png";
    
//    Simulation sim = getActiveSimulation();
    
    @Override
    public void execute() {
        Simulation sim = getActiveSimulation();
        sim.println("here");
        File postDir = new File(sim.getSessionDir() + fs + "eCooling_opt6" + fs + "POST_0");
        File animDir = new File(sim.getSessionDir() + fs + imgName + "animation");
        
        if (animDir.exists()) {
            animDir.delete();
            animDir.mkdirs();
        } else {
            animDir.mkdirs();
        }
        
        try {
            //for (int i = 1; i < 203; i++){
            int j = 1;
            for (int i : getDesigns()) {
                File f = new File(postDir.getPath() + fs + "Design" + i + fs + "star" + fs + imgName + ext);
                
                if (f.exists()) {
                    Path source = f.toPath();
                    Path target = new File(animDir.getPath() + fs + imgName + j + ext).toPath();
                    
                    try {
                        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        sim.print(ex.getLocalizedMessage());
                    }
                }
                j++;
            }
        } catch (FileNotFoundException ex) {
            sim.print(ex.getLocalizedMessage());
        }
        
    }
    
    int[] getDesigns() throws FileNotFoundException {
        Simulation sim = getActiveSimulation();
        ArrayList<String> list = new ArrayList<>();
        
        File csvF = new File(sim.getSessionDir() + fs + "highMassDesigns.csv");
        
        try (Scanner scan = new Scanner(csvF)) {
            scan.nextLine();
            
            while (scan.hasNextLine()) {
                list.add(scan.nextLine().split(",")[0]);
            }
            
            int[] ints = new int[list.size()];
            
            int i = 0;
            for (String s : list) {
                ints[i] = Integer.parseInt(s);
                i++;
            }
            
            return ints;
        }
    }
    
}
