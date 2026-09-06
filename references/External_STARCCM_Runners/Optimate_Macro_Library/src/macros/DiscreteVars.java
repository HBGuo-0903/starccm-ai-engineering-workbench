/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import core.OptimateFile;
import core.apiFramework.OptimateProject;
import java.io.File;
import parameters.var.AbstractVariable;
import star.common.StarMacro;

/**
 *
 * @author aarong
 */
public class DiscreteVars extends StarMacro {

    @Override
    public void execute() {
        OptimateProject project;
        try {
            project = OptimateProject.newInstance(getActiveSimulation(), OptimateFile.newInstance(getActiveSimulation().getSessionDir() + File.separator + "opt.optm"));
            if (project == null) {
                getActiveSimulation().println("null");
            }

            for (AbstractVariable av : project.getActiveStudy().getVariableManager().getEnabledChildren()) {
                av.setVariableType(AbstractVariable.VariableType.DISCRETE);
            }
            
            project.save();
            
            
        } catch (Exception ex) {
            
        }

    }

}
