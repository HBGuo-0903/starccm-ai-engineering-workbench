/**
 * Exports Scenes for Star-View
 * *
 * @author Andrew Gunderson
 * 2017
 * star v11.06
 */

import macroutils.*;
import star.common.*;
import star.flow.*;
import star.vis.*;

public class PCoeff_3D_Scene extends StarMacro {

    String[] propModels = {
        "BravoI",
        //"Fury4_6036",
    };
    
    String[] runState = {
        "62.7mph_10.0deg_7.19in_3396.0rpm",
    };
    
    public void execute() {
        
        // kill default server that starts upon macro execution
        sim = getActiveSimulation();
        sim.kill();
        for (String folder : propModels) {
            for (String state : runState) {
                
                simName = folder + "_" + state;
                fileName = "\\\\MMFDLHPCP01\\scratch\\Gunderson\\CFD_TRs\\TR2016-prop\\star\\" + folder + "\\" + simName + ".sim";
                speed = Double.parseDouble(state.substring(0,3));
                
                initMacro();
                
                createAndExportScenes();
                
                mu.getSimulation().kill();
            }
        }
    }

    void initMacro() {       
        sim = new Simulation(fileName);
        mu = new MacroUtils(sim);
        ud = mu.userDeclarations;        
        pCoeff = ((PressureCoefficientFunction) mu.getSimulation().getFieldFunctionManager().getFunction("PressureCoefficient"));
        pCoeff.getReferenceVelocity().setValue(speed);        
    }

    void createAndExportScenes() {
        // Create Cp scalar scene
        ud.namedObjects.addAll(mu.get.boundaries.allByREGEX("Blade1|Blades|Hub|Strut", true));
        ud.scene = mu.add.scene.scalar(ud.namedObjects, pCoeff , ud.unit_Dimensionless, true);
        ud.scene.setPresentationName("Cp");
        ScalarDisplayer sd = (ScalarDisplayer) mu.get.scenes.displayerByREGEX(ud.scene, ".*", true);
        sd.getScalarDisplayQuantity().setClip(false);
        sd.getScalarDisplayQuantity().setRange(new double[]{-1., 1.});
        ud.scene.export3DSceneFileAndWait("\\\\MMFDLHPCP01\\scratch\\Gunderson\\CFD_TRs\\TR2016-prop\\star\\3dScenes\\" + simName + ".sce", "Cp", "", true, false);        
    }

    private MacroUtils mu;
    private UserDeclarations ud;
    PressureCoefficientFunction pCoeff;
    Simulation sim;
    String simName;
    String fileName;
    Double speed;

}
