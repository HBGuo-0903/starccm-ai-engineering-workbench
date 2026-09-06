/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import star.common.Region;
import star.common.Simulation;
import star.common.StarMacro;

/**
 * Simple macro to write out a csv file listing all regions in the sim as well
 * as the number of boundaries in those regions.
 * @author aarong
 */
public class WriteFile extends StarMacro {

    //*********** USER Parameters***************
    String path = "some path for an output file";
    
    //*********** USER Parameters***************
    
    @Override
    public void execute() {
        Simulation sim = getActiveSimulation();
        
        File output = new File(path);
        
        try (BufferedWriter out = new BufferedWriter(new FileWriter(output))) {
            out.write("This is an outout file from STAR-CCM+.  It lists all Regions and the number of boundaries.");
            out.newLine();
            out.write("Simulation: " + sim.getPresentationName());
            out.newLine();
            out.write("Region Name, Number of Boundaries\n");
            
            for (Region r : sim.getRegionManager().getRegions()) {
                out.write(r.getPresentationName() + ", " + r.getBoundaryManager().getBoundaries().size() + "\n");
            }
            out.close();
        } catch (IOException ex) {
            //Do something with exception
        }
    }
    
}
