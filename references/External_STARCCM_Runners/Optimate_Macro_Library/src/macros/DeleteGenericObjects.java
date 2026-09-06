/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import star.base.generic.GenericObject;
import star.base.generic.GenericObjectManager;
import star.base.neo.NeoProperty;
import star.common.Simulation;
import star.common.StarMacro;

/**
 *
 * @author aarong
 */
public class DeleteGenericObjects extends StarMacro {
    Simulation sim;
    
    @Override
    public void execute() {
        sim = getActiveSimulation();
        loadStarGeneric();
        GenericObjectManager gom = GenericObjectManager.get(sim);
        
        for (GenericObject go : gom.getObjects()) {
            gom.deleteGenericObject(go);
        }
    }
    
    private void loadStarGeneric() {
      NeoProperty args = new NeoProperty();
      args.put("BaseName", "StarGeneric");
      sim.execute("LoadLibrary", args); // NOI18N    
  }
    
}
