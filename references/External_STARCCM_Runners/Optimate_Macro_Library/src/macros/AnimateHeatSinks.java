/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import star.base.neo.NeoObjectVector;
import star.cadmodeler.CadModel;
import star.cadmodeler.DesignParameter;
import star.cadmodeler.ScalarQuantityDesignParameter;
import star.cadmodeler.SolidModelManager;
import star.cadmodeler.SolidModelPart;
import star.cadmodeler.UserDesignParameter;
import star.cadmodeler.VectorQuantityDesignParameter;
import star.common.Simulation;
import star.common.SimulationPartManager;
import star.common.StarMacro;
import star.vis.Scene;

/**
 *
 * @author aarong
 */
public class AnimateHeatSinks extends StarMacro {

    Simulation _sim;

    CadModel cad;
    ScalarQuantityDesignParameter majorRad;
    ScalarQuantityDesignParameter minorRad;
    ScalarQuantityDesignParameter radRatio;
    UserDesignParameter xPerFill;
    UserDesignParameter yPerFill;
    VectorQuantityDesignParameter pnHeight;
    Scene scene;

    @Override
    public void execute() {
        init();
        animate();

    }

    private void init() {
        _sim = getActiveSimulation();
        cad = (CadModel) _sim.get(SolidModelManager.class).getObject("Heatsinks");

        majorRad = (ScalarQuantityDesignParameter) cad.getDesignParameterManager().getObject("EllipseMajorRadius");
        minorRad = (ScalarQuantityDesignParameter) cad.getDesignParameterManager().getObject("EllipseMinorRadius");
        radRatio = (ScalarQuantityDesignParameter) cad.getDesignParameterManager().getObject("radiiRatio");
        xPerFill = (UserDesignParameter) cad.getDesignParameterManager().getObject("XPercentFill");
        yPerFill = (UserDesignParameter) cad.getDesignParameterManager().getObject("YPercentFill");
        pnHeight = (VectorQuantityDesignParameter) cad.getDesignParameterManager().getObject("pinHeight");

        scene = _sim.getSceneManager().getSceneByName("Geometry - Design");

    }

    private void animate() {
        SolidModelPart solidModelPart_0 = ((SolidModelPart) _sim.get(SimulationPartManager.class).getPart("HeatSink1"));
        int count = 1;

        for (double d : getVariableVals(0.9, 0.7, 2.5, 51)) {
            try {
                updateParam(majorRad, d);
                cad.update();
                _sim.get(SimulationPartManager.class).updateParts(new NeoObjectVector(new Object[]{solidModelPart_0}));

                scene.printAndWait(_sim.getSessionDir() + "/animations2/" + count + ".png", 1, 1616, 892);
                count++;
            } catch (Exception ex) {
            }
        }
        updateParam(majorRad, 0.9);

        for (double d : getVariableVals(0.9, 0.7, 2.5, 51)) {
            try {
                updateParam(minorRad, d);
                cad.update();
                _sim.get(SimulationPartManager.class).updateParts(new NeoObjectVector(new Object[]{solidModelPart_0}));
                scene.printAndWait(_sim.getSessionDir() + "/animations2/" + count + ".png", 1, 1616, 892);
                count++;
            } catch (Exception ex) {
            }
        }
        updateParam(minorRad, 0.9);

        for (double d : getVariableVals(1, 0.5, 1.0, 51)) {
            try {
                updateParam(radRatio, d);
                cad.update();
                _sim.get(SimulationPartManager.class).updateParts(new NeoObjectVector(new Object[]{solidModelPart_0}));
                scene.printAndWait(_sim.getSessionDir() + "/animations2/" + count + ".png", 1, 1616, 892);
                count++;
            } catch (Exception ex) {
            }
        }
        updateParam(radRatio, 1.0);

        for (double d : getVariableVals(1.2, 0.4, 1.3, 21)) {
            try {
                updateParam(xPerFill, d);
                cad.update();
                _sim.get(SimulationPartManager.class).updateParts(new NeoObjectVector(new Object[]{solidModelPart_0}));
                scene.printAndWait(_sim.getSessionDir() + "/animations2/" + count + ".png", 1, 1616, 892);
                count++;
            } catch (Exception ex) {
            }
        }
        updateParam(xPerFill, 1.2);

        for (double d : getVariableVals(1.2, 0.4, 1.3, 21)) {
            try {
                updateParam(yPerFill, d);
                cad.update();
                _sim.get(SimulationPartManager.class).updateParts(new NeoObjectVector(new Object[]{solidModelPart_0}));
                scene.printAndWait(_sim.getSessionDir() + "/animations2/" + count + ".png", 1, 1616, 892);
                count++;
            } catch (Exception ex) {
            }
        }
        updateParam(yPerFill, 1.2);

        for (double d : getVariableVals(18.0, 8.5, 18.0, 51)) {
            try {
                updateParam(pnHeight, d);
                cad.update();
                _sim.get(SimulationPartManager.class).updateParts(new NeoObjectVector(new Object[]{solidModelPart_0}));
                scene.printAndWait(_sim.getSessionDir() + "/animations2/" + count + ".png", 1, 1616, 892);
                count++;
            } catch (Exception ex) {
            }
        }

    }

    private void updateParam(DesignParameter param, double val) {
        if (param instanceof ScalarQuantityDesignParameter) {
            ((ScalarQuantityDesignParameter) param).getQuantity().setValue(val);
        } else if (param instanceof UserDesignParameter) {
            ((UserDesignParameter) param).getQuantity().setValue(val);
        } else if (param instanceof VectorQuantityDesignParameter) {
            ((VectorQuantityDesignParameter) param).getQuantity().setComponents(0.0, 0.0, val);
        }
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
