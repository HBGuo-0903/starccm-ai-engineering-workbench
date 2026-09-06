/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import java.io.PrintWriter;
import java.io.StringWriter;
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

/**
 *
 * @author aarong
 */
public class TranslateInlets extends StarMacro {

    Simulation sim;
    CoordinateDesignParameter inlet1dp;
    CoordinateDesignParameter inlet2dp;
    double inlet1ff;
    double alphaff;

    final double ventWidth = 18; //inlet vent width (mm)

    @Override
    public void execute() {

        try {
            init();
            updateDp(inlet1dp, convertInlet1());
            updateDp(inlet2dp, convertInlet2());

            SolidModelPart solidModelPart_1
                    = ((SolidModelPart) sim.get(SimulationPartManager.class).getPart("Casing"));

            sim.get(SimulationPartManager.class).updateParts(new NeoObjectVector(new Object[]{solidModelPart_1}));
        } catch (Exception ex) {
            sim.println(print(ex));
        }

    }

    private void init() {
        sim = getActiveSimulation();
        CadModel cad = (CadModel) sim.get(SolidModelManager.class).getObject("CasingAndFan");
        inlet1dp = (CoordinateDesignParameter) cad.getDesignParameterManager().getObject("Inlet1Translation");
        inlet2dp = (CoordinateDesignParameter) cad.getDesignParameterManager().getObject("Inlet2Translation");
        inlet1ff = Double.parseDouble(((UserFieldFunction) sim.getFieldFunctionManager().getFunction("inlet1")).getDefinition());
        sim.println("inlet1 definition = " + inlet1ff);
        alphaff = Double.parseDouble(((UserFieldFunction) sim.getFieldFunctionManager().getFunction("alpha")).getDefinition());
        sim.println("alpha  definition = " + alphaff);
    }

    private double convertInlet1() {
        sim.println(inlet1ff - 5.0);
        return inlet1ff - 5.0;
    }

    private double convertInlet2() {
        double d1 = inlet1ff + 20;
        double d2 = (d1 + alphaff * (63 - d1)) - 58;
        sim.println(d2);
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

}
