/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import core.OptimateFile;
import core.apiFramework.OptimateAnalysis;
import core.apiFramework.OptimateProject;
import java.io.File;
import star.common.Simulation;
import star.common.StarMacro;

/**
 *
 * @author aarong
 */
public class FixForWinQueue extends StarMacro {

    @Override
    public void execute() {
        Simulation sim = getActiveSimulation();

        try {
            OptimateFile ofile = OptimateFile.newInstance(sim.getSessionDir() + File.separator + "sweep.optm");
            OptimateProject proj = OptimateProject.newInstance(sim, ofile);
            
            proj.getActiveStudy().getAnalysis().setExecType(OptimateAnalysis.ExecType.WIN_HPC_QUEUE);
            proj.save();
            proj.writeInputFiles(true);
        } catch (Exception ex) {

        }
    }

}
