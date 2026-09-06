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

public class ExportVisScenes extends StarMacro {

    String[] propModels = {
        "BravoI",
        "Fury4_6036",
        "Fury4_6062"
    };
    String[] runState = {
        "62.7mph_5.0deg_7.19in_3396.0rpm",
        "58.6mph_5.0deg_7.19in_3265.5rpm"
    };
    String simName;
    public void execute() {
        // kill default server that starts upon macro execution
        sim = getActiveSimulation();
        sim.kill();
        for (String folder : propModels) {
            for (String state : runState) {
                initMacro(state, folder);
                createAndExportScenes(state, folder);
                ud.simTitle = folder + "_" + state + "_mod.sim";
                mu.saveSim();
                mu.getSimulation().kill();
            }
        }
    }

    void initMacro(String state, String folder) {
        double speed = Double.parseDouble(state.substring(0,3));
        simName = folder + "_" + state;
        String fileName = "\\\\MMFDLHPCP01\\scratch\\Gunderson\\CFD_TRs\\TR2016-prop\\star\\test\\" + folder + "\\" + simName + ".sim";
        sim = new Simulation(fileName);
        mu = new MacroUtils(sim);
        ud = mu.userDeclarations;
        //
        mu.getSimulation().println(ud.simTitle);
        sim.println(sim.getPresentationName());
        //
        pCoeff = ((PressureCoefficientFunction) mu.getSimulation().getFieldFunctionManager().getFunction("PressureCoefficient"));
        pCoeff.getReferenceVelocity().setValue(speed);
        ud.ff = mu.add.tools.fieldFunction("Cp", "${Pressure}/.5/${DensityWater}/pow(.447*" + speed + ",2)",
                ud.dimDimensionless, FieldFunctionTypeOption.Type.SCALAR);
    }

    void createAndExportScenes(String state, String folder) {
        // Grab pre-existing scalar scene modify
        ud.scene = mu.get.scenes.byREGEX("scalar scene", true);
        ScalarDisplayer sd = (ScalarDisplayer) mu.get.scenes.displayerByREGEX(ud.scene, "Scalar 1", true);
        sd.getScalarDisplayQuantity().setFieldFunction(ud.ff);
        sd.setSmoothShade(true);       
        sd.getScalarDisplayQuantity().setClip(false);
        sd.getScalarDisplayQuantity().setRange(new double[]{-1., 1.});
        ud.scene.export3DSceneFileAndWait(ud.simPath + "\\" + simName + ".sce", "User_Defined_Cp", "", true, false);
      
        // Create Cp scalar scene
        ud.namedObjects.addAll(mu.get.boundaries.allByREGEX("Blade1|Blades|Hub|Strut", true));
        ud.scene = mu.add.scene.scalar(ud.namedObjects, pCoeff , ud.unit_Dimensionless, true);
        ud.scene.setPresentationName("Cp");
        sd = (ScalarDisplayer) mu.get.scenes.displayerByREGEX(ud.scene, ".*", true);
        sd.getScalarDisplayQuantity().setClip(false);
        sd.getScalarDisplayQuantity().setRange(new double[]{-1., 1.});
        ud.scene.export3DSceneFileAndWait(ud.simPath + "\\" + simName + ".sce", "Star_Default_Cp", "", true, false);
        
        // Create isosurface and vorticity scene
        VorticityVectorFunction vvf = (VorticityVectorFunction) mu.getSimulation().getFieldFunctionManager().getFunction("VorticityVector");
        VectorMagnitudeFieldFunction vvf_mag = (VectorMagnitudeFieldFunction) vvf.getMagnitudeFunction();
        ud.namedObjects.clear();
        ud.namedObjects.addAll(mu.get.regions.all(true));
        Units units_vorticity
                = (mu.getSimulation().getUnitsManager().getObject("/s"));
        ud.namedObjects2.add(mu.add.derivedPart.isosurface(ud.namedObjects, vvf_mag, 200, units_vorticity));
        ud.ff = mu.get.objects.fieldFunction(StaticDeclarations.Vars.VEL.getVar(),true);
        ud.scene = mu.add.scene.scalar(ud.namedObjects2, ud.ff, ud.unit_mps, true);
        ud.scene.export3DSceneFileAndWait(ud.simPath + "\\" + simName + ".sce", "Vorticity", "", true, false);
        
    }

    private MacroUtils mu;
    private UserDeclarations ud;
    PressureCoefficientFunction pCoeff;
    Simulation sim;
          
}
