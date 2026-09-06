/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimate.macroInsertion;

import star.common.StarMacro;

/**
 *
 * @author aarong
 */
public class BeforeMeshing2 extends StarMacro {
    
    @Override
    public void execute() {
        getActiveSimulation().println(getClass().getSimpleName() + " macro was played before meshing.");
    }
}
