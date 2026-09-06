/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimate;

import parameters.var.user.CustomScalarVariable;
import star.base.neo.ClientServerObject;
import star.meshing.BaseSize;

/**
 *
 * @author aarong
 */
public class BaseSizeVariable extends CustomScalarVariable {
    
    public BaseSizeVariable(BaseSize baseSize, String key) {
        super(baseSize, key);
    }
    
    public BaseSizeVariable() {
        super();
    }

    @Override
    protected Class[] getStarClass() {
        return new Class[] {BaseSize.class};
    }
    
    @Override
    protected String getFilterName() {
        return "Meshing Base Sizes";
    }

    @Override
    protected double getCurrentValueInSimulation(ClientServerObject cso) {
        return ((BaseSize) cso).getValue();
    }

    @Override
    public String exportAPIToUpdate(String doubleName, String baseSizeName) {
        return baseSizeName + ".setValue(" + doubleName + ")";
    }
    
}
