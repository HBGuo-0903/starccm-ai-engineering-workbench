/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import star.common.StarMacro;
import star.common.UserFieldFunction;

/**
 *
 * @author aarong
 */
public class SetBetaValues extends StarMacro {

    @Override
    public void execute() {
        for (UserFieldFunction ff : getActiveSimulation().getFieldFunctionManager().getObjectsOf(UserFieldFunction.class)) {
            if (ff.getPresentationName().contains("ZZ_beta")) {
                ff.setDefinition("0.85");
            }
        }
    }    
}
