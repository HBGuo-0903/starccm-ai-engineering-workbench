/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimate;

import parameters.var.user.CustomScalarVariable;
import star.base.neo.ClientServerObject;
import star.common.ImplicitUnsteadySolver;

/**
 *
 * @author aarong
 */
public class TimeStepVariable extends CustomScalarVariable {

    public TimeStepVariable(ImplicitUnsteadySolver cso, String key) {
        super(cso, key);
        setBaseline(getCurrentValueInSimulation(cso));
    }

    public TimeStepVariable() {
        super();
    }

    @Override
    public String exportAPIToUpdate(String doubleName, String objName) {
        return objName + ".setTimeStep(" + doubleName + ")";
    }

    @Override
    protected Class[] getStarClass() {
        return new Class[]{ImplicitUnsteadySolver.class};
    }

    @Override
    protected double getCurrentValueInSimulation(ClientServerObject cso) {
        return ((ImplicitUnsteadySolver) cso).getTimeStep().getValue();
    }
}
