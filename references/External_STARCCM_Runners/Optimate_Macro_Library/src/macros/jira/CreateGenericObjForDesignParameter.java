/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros.jira;

import star.base.generic.GenericObject;
import star.base.generic.GenericObjectManager;
import star.base.neo.ClientServerObjectKey;
import star.base.neo.NeoProperty;
import star.cadmodeler.CadModel;
import star.cadmodeler.ScalarQuantityDesignParameter;
import star.cadmodeler.SolidModelManager;
import star.common.Simulation;
import star.common.StarMacro;

/**
 *
 * @author aarong
 */
public class CreateGenericObjForDesignParameter extends StarMacro {

    Simulation sim;
    GenericObjectManager gom;
    ScalarQuantityDesignParameter dp;

    @Override
    public void execute() {
        sim = getActiveSimulation();

        NeoProperty args = new NeoProperty();
        args.put("BaseName", "StarGeneric");
        sim.execute("LoadLibrary", args); // NOI18N

        gom = GenericObjectManager.get(sim);

        dp = (ScalarQuantityDesignParameter) ((CadModel) sim.get(SolidModelManager.class).getObject("3D-CAD Model 1")).getDesignParameterManager().getObject("depth");

        try {
            gom.deleteGenericObject(gom.getObject(dp.getPresentationName() + "_GenericObject"));
        } catch (Exception ex) {
        }

        NeoProperty props = new NeoProperty();
        props.put("Optimate tagged object", dp);
        props.put("most recent reference time stamp", "");
        gom.createGenericObject(dp.getPresentationName() + "_GenericObject", props);
        
        args = new NeoProperty();
        args.put("BaseName", "StarGeneric");
        sim.execute("LoadLibrary", args); // NOI18N

        gom = GenericObjectManager.get(sim);
        
        GenericObject go = gom.getObject(dp.getPresentationName() + "_GenericObject");
        
        ClientServerObjectKey starObjKey = go.getGenericProperties().getObjectKey("Optimate tagged object", sim.getObjectRegistry());
        
        if (starObjKey != null) {
            dp = sim.getObjectRegistry().getObject(starObjKey);
        }
    }

}
