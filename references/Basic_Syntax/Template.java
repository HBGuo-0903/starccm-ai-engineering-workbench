/**
 * Propeller parametric simulation
 * 
 * @author Andrew Gunderson
 * 
 * 2017, v11.06
 */

import star.common.*;
import macroutils.*;
import java.util.*;

public class Template extends StarMacro {

    public void execute() {
        initMacro();
    }
    
    void initMacro(){
        mu = new MacroUtils(getActiveSimulation());
        ud = mu.userDeclarations;
    }
    
    MacroUtils mu;
    UserDeclarations ud;
}
