/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros.jira;

import java.util.HashMap;
import star.base.neo.IntVector;
import star.common.Simulation;
import star.common.SimulationPartManager;
import star.common.StarMacro;
import star.common.Units;
import star.common.UserFieldFunction;
import star.meshing.LeafMeshPart;
import star.meshing.PartImportManager;

/**
 *
 * @author aarong
 */
public class ReImportGeomByMacroForOPT660 extends StarMacro {

    final Simulation sim = getActiveSimulation();
    final String cadName = "cadPart";
    final String ff_name = "cad_replace_funciton";
    final String cad_path = "/my/path/to/the/CAD/files/";
    final HashMap<Integer, String> cadMap = new HashMap<Integer, String>();
    
    @Override
    public void execute() {
        popMap();
        reimport();
    }
    
    private void popMap() {
        cadMap.put(1, "cadFile1.nas");
        cadMap.put(2, "cadFile2.nas");
    }
    
    private void reimport() {
        LeafMeshPart part = (LeafMeshPart) sim.get(SimulationPartManager.class).getPart(cadName);
        PartImportManager importer = sim.get(PartImportManager.class);
        Units units = sim.getUnitsManager().getPreferredUnits(new IntVector(new int[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));
        
        importer.reimportNastranPart(part, cad_path + cadMap.get(getFFVal()) ,true, units);
    }
    
    private int getFFVal() {
        UserFieldFunction f = (UserFieldFunction) sim.getFieldFunctionManager().getFunction(ff_name);
        return Integer.parseInt(f.getDefinition());
    }
    
    
    
}
