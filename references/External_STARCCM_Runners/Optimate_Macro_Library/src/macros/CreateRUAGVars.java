/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import star.common.FieldFunctionManager;
import star.common.Simulation;
import star.common.StarMacro;
import star.common.UserFieldFunction;

/**
 *
 * @author aarong
 */
public class CreateRUAGVars extends StarMacro {

    Simulation _sim;
    
    int nx = 17;
    int ny = 10;
    
    @Override
    public void execute() {
        _sim = getActiveSimulation();
        
        deleteFFs();
        createFFVars();
        createBetaFF();
    }
    
    private void deleteFFs() {
        FieldFunctionManager man = _sim.getFieldFunctionManager();
        
        man.remove(man.getFunction("beta"));
        for (UserFieldFunction uff : man.getObjectsOf(UserFieldFunction.class)) {
            if (uff.getPresentationName().toLowerCase().contains("beta")) {
                man.remove(uff);
            }
        }
    }
    
    private void createFFVars() {
        int count = 1;
        for (int i = 1; i <= nx; i++) {
            for (int j = 1; j <= ny; j++) {
                UserFieldFunction uff = _sim.getFieldFunctionManager().createFieldFunction();
                String name = "ZZ_beta" + Integer.toString(i) + "_" + Integer.toString(j);
                uff.setFunctionName(name);
                uff.setPresentationName(name);
                uff.setDefinition(Integer.toString(count));
                count++;
            }
        }
    }
    
    private void createBetaFF() {
        UserFieldFunction ff = _sim.getFieldFunctionManager().createFieldFunction();
        ff.setPresentationName("beta");
        ff.setFunctionName("beta");
        ff.setDefinition(createBetaFFDef());
    }
    
    private String createBetaFFDef() {
        String s = "";
        for (int i = 1; i < nx; i++) {
            for (int j = 1; j <= ny; j++) {
                s += "($$meshCentroid[0] < " + i + " && $$meshCentroid[1] < " + j + ") ? $ZZ_beta" + Integer.toString(i) + "_" + Integer.toString(j) + " :\n";
            }
        }
        
        for (int j = 1; j < ny; j++) {
            s += "($$meshCentroid[0] < " + 17 + " && $$meshCentroid[1] < " + j + ") ? $ZZ_beta" + Integer.toString(nx) + "_" + Integer.toString(j) + " :\n";
        }
        
        s += "($$meshCentroid[0] < " + nx + " && $$meshCentroid[1] < " + ny + ") ? $ZZ_beta" + Integer.toString(nx) + "_" + Integer.toString(ny) + " : 0";
        return s;
    }
}
