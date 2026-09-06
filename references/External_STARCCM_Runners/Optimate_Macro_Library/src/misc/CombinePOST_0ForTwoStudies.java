/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import star.common.StarMacro;

/**
 *
 * @author aarong
 */
public class CombinePOST_0ForTwoStudies extends StarMacro {
    
    private String study2Design;
    private String study1Design;
    private File study1Dir;
    private File study2Dir;
    private File study1PostDir;
    private File study2PostDir;
    private final int oldIdCol = 1;
    
    @Override
    public void execute() {
        File csvFile = getInsertFile();
        
        try (Scanner scanner = new Scanner(csvFile)) {
            int design = 0;
            String s = "Design";
            while(scanner.hasNextLine() && design <= 36) {
                if (design == 0) {
                    scanner.nextLine();
                } else {
                    study2Design = s + design;
                    study1Design = s + getStudy1DesignNumber(scanner.nextLine());
                    updateDirs();
                    copy();
                }
                design++;
            }
        } catch (FileNotFoundException ex) {
            print(ex);
        } catch (IOException ex) {
            print(ex);
        }
    }
    
    private File getInsertFile() {
        return new File("/u/xeons24/people/aarong/optimate/projects/airbus/phase2/1102/pOpt3.csv");
    }
    
    private void print(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        System.out.println(sw.toString());
    }
    
    private String getStudy1DesignNumber(String s) {
        String[] array = s.split(",");
        return array[oldIdCol].trim();
    }
    
    private void updateDirs() {
        study1Dir = getStudy1Dir(study1Design);
        study1PostDir = getStudy1PostDir(study1Design);
        study2Dir = getStudy2Dir(study2Design);
        study2PostDir = getStudy2PostDir(study2Design);
    }
    
    private File getStudy1Dir(String design) {
        return new File("/u/xeons24/people/aarong/optimate/projects/airbus/phase2/1102/airbusStaticMixer3_pOpt3/RESULTS_BACKUP_0_0_05-02-16_181455PM/star_0/" + design + "/star");
    }
    
    private File getStudy2Dir(String design) {
        return new File("/u/xeons24/people/aarong/optimate/projects/airbus/phase2/1102/airbusStaticMixer3_sOpt1/star_0/" + design + "/star");
    }
    
    private File getStudy1PostDir(String design) {
        return new File("/u/xeons24/people/aarong/optimate/projects/airbus/phase2/1102/airbusStaticMixer3_pOpt3/RESULTS_BACKUP_0_0_05-02-16_181455PM/POST_0/" + design + "/star");
    }
    
    private File getStudy2PostDir(String design) {
        return new File("/u/xeons24/people/aarong/optimate/projects/airbus/phase2/1102/airbusStaticMixer3_sOpt1/POST_0/" + design + "/star");
    }
    
    private void copy() throws IOException {
        
        getActiveSimulation().println("Copying data from " + study1Design + " to " + study2Design);
        
        if (!study1Dir.exists()) {
            getActiveSimulation().println("Couldn't find " + study1Dir.getAbsolutePath());
            return;
        }
        
        if (!study2Dir.exists()) {
            study2Dir.mkdirs();
        }
        
        for (File f : study1Dir.listFiles()) {
            if (!f.isDirectory()) {
                Path source = new File(study1Dir.getAbsolutePath() + File.separator + f.getName()).toPath();
                Path target = new File(study2Dir.getAbsolutePath() + File.separator + f.getName()).toPath();
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        
        if (!study1PostDir.exists()) {
            getActiveSimulation().println("Couldn't find " + study1PostDir.getAbsolutePath());
            return;
        }
        
        if (!study2PostDir.exists()) {
            study2PostDir.mkdirs();
        }
        
        for (File f : study1PostDir.listFiles()) {
            if (!f.isDirectory()) {
                Path source = new File(study1PostDir.getAbsolutePath() + File.separator + f.getName()).toPath();
                Path target = new File(study2PostDir.getAbsolutePath() + File.separator + f.getName()).toPath();
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        
    }
    
}
