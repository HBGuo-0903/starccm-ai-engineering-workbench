/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import star.base.neo.DoubleVector;
import star.base.neo.NeoObjectVector;
import star.cadmodeler.CadModel;
import star.cadmodeler.CadModelCoordinate;
import star.cadmodeler.CoordinateDesignParameter;
import star.cadmodeler.SolidModelManager;
import star.cadmodeler.SolidModelPart;
import star.common.Simulation;
import star.common.SimulationPartManager;
import star.common.StarMacro;
import star.common.Units;
import star.common.UserFieldFunction;
import star.meshing.MeshOperationManager;
import star.meshing.TransformPartsOperation;
import star.vis.Scene;

/**
 *
 * @author aarong
 */
public class AnimateTranslateInlets extends StarMacro {

    Simulation sim;
    CoordinateDesignParameter inlet1dp;
    CoordinateDesignParameter inlet2dp;
    CoordinateDesignParameter exaustdp;
    double inlet1ff;
    double alphaff;
    Scene scene;

    final double ventWidth = 18; //inlet vent width (mm)

    @Override
    public void execute() {

        try {
            init();
            animate();
//            updateDp(inlet1dp, convertInlet1(5.0));
//            updateDp(inlet2dp, convertInlet2(5.0, 0.7));
//            updateDp(exaustdp, 0);
//
//            SolidModelPart solidModelPart_1
//                    = ((SolidModelPart) sim.get(SimulationPartManager.class).getPart("Casing"));
//
//            sim.get(SimulationPartManager.class).updateParts(new NeoObjectVector(new Object[]{solidModelPart_1}));
        } catch (Exception ex) {
            sim.println(print(ex));
        }

    }

    private void init() {
        sim = getActiveSimulation();
        CadModel cad = (CadModel) sim.get(SolidModelManager.class).getObject("CasingAndFan");
        inlet1dp = (CoordinateDesignParameter) cad.getDesignParameterManager().getObject("Inlet1Translation");
        inlet2dp = (CoordinateDesignParameter) cad.getDesignParameterManager().getObject("Inlet2Translation");
        exaustdp = (CoordinateDesignParameter) cad.getDesignParameterManager().getObject("ExhaustTranslation");
        inlet1ff = Double.parseDouble(((UserFieldFunction) sim.getFieldFunctionManager().getFunction("inlet1")).getDefinition());
        sim.println("inlet1 definition = " + inlet1ff);
        alphaff = Double.parseDouble(((UserFieldFunction) sim.getFieldFunctionManager().getFunction("alpha")).getDefinition());
        sim.println("alpha  definition = " + alphaff);
        scene = sim.getSceneManager().getSceneByName("Geometry - Design");
    }

    private double convertInlet1() {
        sim.println(inlet1ff - 5.0);
        return inlet1ff - 5.0;
    }

    private double convertInlet1(double d) {
        return d - 5;
    }

    private void animate() {
        int count = 1;
        for (double d : getVariableVals(5.0, 0.0, 43.0, 51)) {
            sim.println(d);
            updateDp(inlet1dp, convertInlet1(d));
            updateDp(inlet2dp, convertInlet2(d, 0.7));
            CadModel cadModel_1 = ((CadModel) sim.get(SolidModelManager.class).getObject("CasingAndFan"));

            cadModel_1.update();

            TransformPartsOperation transformPartsOperation_0 = ((TransformPartsOperation) sim.get(MeshOperationManager.class).getObject("Casing Post"));

            transformPartsOperation_0.execute();
            
            scene.printAndWait(sim.getSessionDir() + "/animations/" + count + ".png", 1, 1616, 892);
            count++;
        }
        
        updateDp(inlet1dp, convertInlet1(5.0));
        updateDp(inlet2dp, convertInlet2(5.0, 0.7));

        for (double d : getVariableVals(0.7, 0.0, 1.0, 51)) {
            updateDp(inlet1dp, convertInlet1(5.0));
            updateDp(inlet2dp, convertInlet2(5.0, d));
            CadModel cadModel_1 = ((CadModel) sim.get(SolidModelManager.class).getObject("CasingAndFan"));

            cadModel_1.update();

            TransformPartsOperation transformPartsOperation_0 = ((TransformPartsOperation) sim.get(MeshOperationManager.class).getObject("Casing Post"));

            transformPartsOperation_0.execute();
            
            scene.printAndWait(sim.getSessionDir() + "/animations/" + count + ".png", 1, 1616, 892);
            count++;

        }
        
        updateDp(inlet1dp, convertInlet1(5.0));
        updateDp(inlet2dp, convertInlet2(5.0, 0.7));
        
        for (double d : getVariableVals(0, -23.0, 28.0, 52)) {
            updateDp(exaustdp, d);
            CadModel cadModel_1 = ((CadModel) sim.get(SolidModelManager.class).getObject("CasingAndFan"));

            cadModel_1.update();

            TransformPartsOperation transformPartsOperation_0 = ((TransformPartsOperation) sim.get(MeshOperationManager.class).getObject("Casing Post"));

            transformPartsOperation_0.execute();
            
            scene.printAndWait(sim.getSessionDir() + "/animations/" + count + ".png", 1, 1616, 892);
            count++;            
        }
    }

    private double convertInlet2() {
        double d1 = inlet1ff + 20;
        double d2 = (d1 + alphaff * (63 - d1)) - 58;
        sim.println(d2);
        return d2;
    }

    private double convertInlet2(double i1, double alf) {
        double d1 = i1 + 20;
        double d2 = (d1 + alf * (63 - d1)) - 58;
        return d2;
    }

    private void updateDp(CoordinateDesignParameter dp, double val) {
        CadModelCoordinate cmc = dp.getQuantity();

        Units u0 = cmc.getUnits0();
        Units u1 = cmc.getUnits1();
        Units u2 = cmc.getUnits2();

        double[] value = cmc.getValue().toDoubleArray();
        value[1] = val;

        cmc.setCoordinate(u0, u1, u2, new DoubleVector(value));
    }

    private String print(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }

    private double[] getVariableVals(double baseline, double min, double max, int resolution) {

        int numFrames = 2 * resolution - 1;
        double increment = (max - min) / (resolution - 1);

        double currentVal = baseline;
        int i = 0;
        double[] vals = new double[numFrames];
        boolean changed;

        while (currentVal <= max) {
            vals[i] = currentVal;
            currentVal += increment;
            i++;
        }

        changed = true;

        while (currentVal >= min) {
            if (changed) {
                currentVal -= 2 * increment;
                changed = false;
            }
            vals[i] = currentVal;
            currentVal -= increment;
            i++;
        }

        changed = true;

        while (currentVal <= baseline) {
            if (changed) {
                currentVal += 2 * increment;
                changed = false;
            }
            vals[i] = currentVal;
            currentVal += increment;
            i++;
        }

        return vals;

    }

}
