// STAR-CCM+ macro: createFieldFunctionYEtc.java
// Written by STAR-CCM+ 14.06.013
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;

public class createFieldFunctionYEtc extends StarMacro {

  public void execute() {
      String[] yEtcs1 = {"yEtc_1-", "${yCl_1-} + ${yNa_1+}"};
      createEtcFieldFuncs(yEtcs1);
      
      String[] yEtcs2 = {"yEtc_2-", "${ySO4_2-} + ${yBa_2+}"};
      createEtcFieldFuncs(yEtcs2);
  }

  private void createEtcFieldFuncs(String[] yEtc) {

    Simulation simulation = getActiveSimulation();

    UserFieldFunction userFieldFunction = simulation.getFieldFunctionManager().createFieldFunction();

    userFieldFunction.getTypeOption().setSelected(FieldFunctionTypeOption.Type.SCALAR);

    userFieldFunction.setPresentationName(yEtc[0]);
    userFieldFunction.setDefinition(yEtc[1]);
   
  }
}
