/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package optimate;

import parameters.var.user.*;
import star.base.neo.*;
import star.segregatedenergy.SegregatedEnergySolver;

/**
 *
 * @author peterj
 */
public class EnergyURF extends CustomScalarVariable {
    
    public EnergyURF(SegregatedEnergySolver cso, String key) {
        super(cso, key);
        
    }
    
    public EnergyURF() {
        super();
    }
    
    
    @Override
    public String exportAPIToUpdate(String doubleName, String objName) {
        return objName + ".setFluidUrf(" + doubleName + ")";
        
    }
    
    @Override
    protected Class[] getStarClass() {
        return new Class[] {SegregatedEnergySolver.class};
        
    }
 
    @Override
    protected double getCurrentValueInSimulation(ClientServerObject cso) {
        return ((SegregatedEnergySolver) cso).getFluidUrf();
        
    }
    
}
