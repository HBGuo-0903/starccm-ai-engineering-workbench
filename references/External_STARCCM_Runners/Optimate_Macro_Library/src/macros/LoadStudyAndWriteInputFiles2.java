/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import core.OptimateFile;
import core.apiFramework.Exceptions.DesignSetLimitExceededException;
import core.apiFramework.Exceptions.InvalidStateException;
import core.apiFramework.OptimateProject;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.prefs.BackingStoreException;
import star.common.Simulation;
import star.common.StarMacro;

/**
 *
 * @author aarong
 */
public class LoadStudyAndWriteInputFiles2 extends StarMacro {

    //**********PARAMETERS***********
    String optmFileDirPath = "/u/xeons24/people/aarong/dev/cdadapco/testing/jira/OPT-615/tst_power_doepower_doe_prefer_hpc_parallel_np3_hpc_0/old"; //path to the optm files that need to be upgraded
    String optmFileSaveDirPath = "/u/xeons24/people/aarong/dev/cdadapco/testing/jira/OPT-615/tst_power_doepower_doe_prefer_hpc_parallel_np3_hpc_0/new"; //path to directory where upgraded optm files should be saved
    //*******************************

    Simulation sim;

    @Override
    public void execute() {
        sim = getActiveSimulation();
        //optmFileDirPath = sim.getSessionDir() + File.separator + "oFiles";
        //optmFileSaveDirPath = sim.getSessionDir() + File.separator + "newOFiles";

        File optmFileDir = new File(optmFileDirPath);
        File optmFileSaveDir = new File(optmFileSaveDirPath);

        if (!optmFileDir.exists()) {
            sim.println("Specified path containing optm files \"" + optmFileDirPath + "\" does not exist.");
            return;
        }

        if (!optmFileSaveDir.exists()) {
            if (!optmFileSaveDir.mkdirs()) {
                sim.println("Unable to create \"" + optmFileSaveDirPath + "\" directory.");
                return;
            }
        }

        File[] files = optmFileDir.listFiles(new OptmFilter());

        for (File f : files) {

            try {
                OptimateFile oFile = OptimateFile.newInstance(f);

                if (oFile != null) {
                    sim.println("Upgrading \"" + f.getAbsolutePath() + "\".");
                    OptimateProject proj = OptimateProject.newInstance(sim, oFile);
                    File saveAs = new File(optmFileSaveDirPath + File.separator + proj.getProjectName());
                    sim.println("Saving \"" + saveAs.getAbsolutePath() + "\".");
                    proj.saveAs(saveAs);
                    sim.println("Writing input files for \"" + saveAs.getAbsolutePath() + "\".");
                    proj.writeInputFiles(false);
                }
            } catch (IOException | InvalidStateException | InterruptedException | BackingStoreException | DesignSetLimitExceededException ex) {
                sim.println("Unable to upgrade and write input files for \"" + f.getAbsolutePath() + "\".");
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                sim.println(sw.toString());
            }
        }
    }

    class OptmFilter implements FileFilter {

        String[] extensions = new String[]{".optm"};

        public OptmFilter() {
            super();
        }

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return false;
            } else {
                String path = f.getAbsolutePath().toLowerCase();
                for (String s : extensions) {
                    if (path.endsWith(s)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

}
