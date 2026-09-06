/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import star.common.Simulation;
import star.common.StarMacro;
import star.common.UserFieldFunction;
import star.starcad2.StarCadDesignParameterDouble;
import star.starcad2.StarCadDocument;
import star.starcad2.StarCadDocumentManager;

/**
 *
 * @author aarong
 */
public class AssignTRValues extends StarMacro {

    Simulation sim;
    StarCadDesignParameterDouble angle;
    StarCadDesignParameterDouble radius;
    UserFieldFunction angleFF;
    UserFieldFunction radiusFF;
    
    @Override
    public void execute() {
        sim = getActiveSimulation();
        getObjects();
        updateValues();
    }
    
    private void getObjects() {
        StarCadDocument doc = ((StarCadDocumentManager) sim.get(StarCadDocumentManager.class)).getDocument("ThrustReverser.prt");
        angle = (StarCadDesignParameterDouble) doc.getStarCadDesignParameters().getParameter("ThrustReverser.prt\\BullnoseAngle");
        radius = (StarCadDesignParameterDouble) doc.getStarCadDesignParameters().getParameter("ThrustReverser.prt\\BullnoseRadius");
        angleFF = (UserFieldFunction) sim.getFieldFunctionManager().getFunction("bullnoseAngleFF");
        radiusFF = (UserFieldFunction) sim.getFieldFunctionManager().getFunction("bullnoseRadiusFF");
    }
    
    private void updateValues() {
        angle.setParamValue(Double.parseDouble(angleFF.getDefinition()));
        radius.setParamValue(Double.parseDouble(radiusFF.getDefinition()));
    }
    
}
