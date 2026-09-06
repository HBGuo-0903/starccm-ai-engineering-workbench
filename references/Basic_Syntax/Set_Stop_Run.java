
/**
 * Propeller parametric simulation
 *
 * @author Andrew Gunderson
 *
 * 2017, v11.06
 */
import star.common.*;
import macroutils.*;
import star.motion.*;
import java.math.*;

public class Set_Stop_Run extends StarMacro {

    public void execute() {
        mu = new MacroUtils(getSimulation());
        ud = mu.userDeclarations;
        mu.get.solver.stoppingCriteria_MaxTime().setMaximumTime(200);
        mu.run();

    }

    MacroUtils mu;
    UserDeclarations ud;
    RotatingMotion rm;

}
