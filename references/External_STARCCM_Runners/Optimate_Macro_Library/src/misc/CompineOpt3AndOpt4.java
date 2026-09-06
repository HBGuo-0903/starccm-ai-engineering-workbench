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

/**
 *
 * @author aarong
 */
public class CompineOpt3AndOpt4 {
    
    private static String opt4Design;
    private static String opt3Design;
    private static File opt3Dir;
    private static File opt4Dir;
    private static File opt3PostDir;
    private static File opt4PostDir;
    
    public static void main(String[] args) {
        
        File csvFile = getInsertFile();
        
        try (Scanner scanner = new Scanner(csvFile)) {
            int design = 0;
            String s = "Design";
            while(scanner.hasNextLine()) {
                if (design == 0) {
                    scanner.nextLine();
                } else {
                    opt4Design = s + design;
                    opt3Design = s + getOpt3DesignNumber(scanner.nextLine());
                    updateDirs();
                    copy();
                }
                design++;
                
//                if (design > 3) {
//                    break;
//                }
            }
        } catch (FileNotFoundException ex) {
            print(ex);
        } catch (IOException ex) {
            print(ex);
        }
        
    }
    
    private static File getInsertFile() {
        return new File("/u/xeons24/people/aarong/optimate/projects/ITAR/tr/Insert.csv");
    }
    
    private static void print(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        System.out.println(sw.toString());
    }
    
    private static String getOpt3DesignNumber(String s) {
        String[] array = s.split(",");
        return array[0].trim();
    }
    
    private static void updateDirs() {
        opt3Dir = getOpt3Dir(opt3Design);
        opt3PostDir = getOpt3PostDir(opt3Design);
        opt4Dir = getOpt4Dir(opt4Design);
        opt4PostDir = getOpt4PostDir(opt4Design);
    }
    
    private static File getOpt3Dir(String design) {
        return new File("/u/xeons24/people/aarong/optimate/projects/ITAR/tr/ThrustReverserAxi_opt3/star_0/" + design + "/star");
    }
    
    private static File getOpt4Dir(String design) {
        return new File("/u/xeons24/people/aarong/optimate/projects/ITAR/tr/ThrustReverserAxi_opt4/star_0/" + design + "/star");
    }
    
    private static File getOpt3PostDir(String design) {
        return new File("/u/xeons24/people/aarong/optimate/projects/ITAR/tr/ThrustReverserAxi_opt3/POST_0/" + design + "/star");
    }
    
    private static File getOpt4PostDir(String design) {
        return new File("/u/xeons24/people/aarong/optimate/projects/ITAR/tr/ThrustReverserAxi_opt4/POST_0/" + design + "/star");
    }
    
    private static void copy() throws IOException {
        if (!opt3Dir.exists()) {
            return;
        }
        
        if (!opt4Dir.exists()) {
            opt4Dir.mkdirs();
        }
        
        for (File f : opt3Dir.listFiles()) {
            if (!f.isDirectory()) {
                Path source = new File(opt3Dir.getAbsolutePath() + File.separator + f.getName()).toPath();
                Path target = new File(opt4Dir.getAbsolutePath() + File.separator + f.getName()).toPath();
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        
        if (!opt3PostDir.exists()) {
            return;
        }
        
        if (!opt4PostDir.exists()) {
            opt4PostDir.mkdirs();
        }
        
        for (File f : opt3PostDir.listFiles()) {
            if (!f.isDirectory()) {
                Path source = new File(opt3PostDir.getAbsolutePath() + File.separator + f.getName()).toPath();
                Path target = new File(opt4PostDir.getAbsolutePath() + File.separator + f.getName()).toPath();
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        
    }
    
}
