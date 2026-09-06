/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import star.cadmodeler.CadModelCoordinate;
import star.cadmodeler.CoordinateDesignParameter;
import star.cadmodeler.SolidModelManager;
import star.common.Coordinate;
import star.common.Simulation;
import star.common.StarMacro;
import star.common.Units;
import star.common.UserFieldFunction;
import star.meshing.MeshOperationManager;
import star.meshing.ScalingControl;
import star.meshing.TransformPartsOperation;
import star.meshing.TranslationControl;

/**
 *
 * @author aarong
 */
public class Scale extends StarMacro {
    String functionName = "scale";
    
    @Override
    public void execute() {
        Simulation sim = getActiveSimulation();
        UserFieldFunction function = (UserFieldFunction) sim.getFieldFunctionManager().getFunction(functionName);
        
        double newVal = Double.parseDouble(function.getDefinition());
        
        TransformPartsOperation transform = (TransformPartsOperation) sim.get(MeshOperationManager.class).getObject("Transform");
        
        ((ScalingControl) transform.getTransforms().getObject("Scale")).getScaleFactor().setComponents(newVal, newVal, newVal);
        
        SolidModelManager man = sim.get(SolidModelManager.class);
        CoordinateDesignParameter dp = new CoordinateDesignParameter(sim.getObjectRegistry().generateObjectKey("mykey"));
//        CadModelCoordinate coord = dp.getQuantity();
        
        TranslationControl tc = new TranslationControl(sim.getObjectRegistry().generateObjectKey(""));
        Coordinate coord = tc.getTranslationVector();
        
        Units u0 = coord.getUnits0();
        Units u1 = coord.getUnits1();
        Units u2 = coord.getUnits2();
        
        
        
        coord.getUnits0().getPresentationName();
    }
}
