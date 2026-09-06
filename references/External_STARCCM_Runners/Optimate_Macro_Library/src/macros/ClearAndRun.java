/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import star.common.Simulation;
import star.common.StarMacro;

/**
 *
 * @author aarong
 */
public class ClearAndRun extends StarMacro {

    @Override
    public void execute() {
        Simulation sim = getActiveSimulation();
        sim.getSolution().clearSolution();
        sim.getSimulationIterator().run(10);        
    }
    
}
