/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import star.common.PhysicsContinuum;
import star.common.Simulation;
import star.common.StarMacro;
import star.common.UserFieldFunction;
import star.keturb.RkeTwoLayerTurbModel;

/**
 *
 * @author aarong
 */
public class UpdateKE_Ct_FromFieldFunction extends StarMacro {

    @Override
    public void execute() {
        Simulation sim = getActiveSimulation();
        UserFieldFunction ff = (UserFieldFunction) sim.getFieldFunctionManager().getFunction("Ct");
        double newCt = Double.parseDouble(ff.getDefinition());

        PhysicsContinuum physics = ((PhysicsContinuum) sim.getContinuumManager().getContinuum("Physics 1"));

        RkeTwoLayerTurbModel turb = physics.getModelManager().getModel(RkeTwoLayerTurbModel.class);

        turb.setCt(newCt);
    }
}
