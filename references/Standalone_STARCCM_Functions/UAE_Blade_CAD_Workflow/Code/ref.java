// STAR-CCM+ macro: ref.java
// Written by STAR-CCM+ 15.02.009
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;
import star.cadmodeler.*;

public class ref extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    Scene scene_0 = 
      simulation_0.getSceneManager().getScene("3D-CAD View 1");

    CurrentView currentView_0 = 
      scene_0.getCurrentView();

    currentView_0.setInput(new DoubleVector(new double[] {-0.04250809417099567, 0.016780718633909952, 0.5081931796550644}), new DoubleVector(new double[] {-0.10647757569918483, -0.08454352202496404, -0.14343266327916995}), new DoubleVector(new double[] {0.6133071689247316, 0.7691075085359876, -0.17979976879601423}), 2.3015422693647807, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.04250809417099567, 0.016780718633909952, 0.5081931796550644}), new DoubleVector(new double[] {-0.10647757569918483, -0.08454352202496404, -0.14343266327916995}), new DoubleVector(new double[] {0.6133071689247316, 0.7691075085359876, -0.17979976879601423}), 2.3015422693647807, 0, 30.0);

    CadModel cadModel_0 = 
      ((CadModel) simulation_0.get(SolidModelManager.class).getObject("3D-CAD Model 1"));

    ReferencePointFromEntityCenter referencePointFromEntityCenter_1 = 
      ((ReferencePointFromEntityCenter) cadModel_0.getFeature("Point 1"));

    cadModel_0.getFeatureManager().rollBack(referencePointFromEntityCenter_1, true);

    cadModel_0.allowMakingPartDirty(false);

    referencePointFromEntityCenter_1.setColorVector(new IntVector(new int[] {-16711936}));

    cadModel_0.allowMakingPartDirty(true);

    FillFeature fillFeature_0 = 
      ((FillFeature) cadModel_0.getFeature("Fill Surface 1"));

    ImportCadFileFeature importCadFileFeature_0 = 
      ((ImportCadFileFeature) cadModel_0.getFeature("ImportCad 1"));

    star.cadmodeler.Body cadmodelerBody_0 = 
      ((star.cadmodeler.Body) importCadFileFeature_0.getBodyByIndex(1));

    Face face_0 = 
      ((Face) fillFeature_0.getFaceByLocation(cadmodelerBody_0,new DoubleVector(new double[] {0.08855328562561882, 0.018382443857779318, 0.508000000000002})));

    referencePointFromEntityCenter_1.setEntity(face_0);

    referencePointFromEntityCenter_1.setCenterPointType(0);

    referencePointFromEntityCenter_1.markFeatureForEdit();

    cadModel_0.getFeatureManager().markDependentNotUptodate(referencePointFromEntityCenter_1);

    cadModel_0.getFeatureManager().rollForwardToEnd(false);

    currentView_0.setInput(new DoubleVector(new double[] {-0.04250809417099567, 0.016780718633909952, 0.5081931796550644}), new DoubleVector(new double[] {-0.10647757569918483, -0.08454352202496404, -0.14343266327916995}), new DoubleVector(new double[] {0.6133071689247316, 0.7691075085359876, -0.17979976879601423}), 2.3015422693647807, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.04250809417099567, 0.016780718633909952, 0.5081931796550644}), new DoubleVector(new double[] {-0.10647757569918483, -0.08454352202496404, -0.14343266327916995}), new DoubleVector(new double[] {0.6133071689247316, 0.7691075085359876, -0.17979976879601423}), 2.3015422693647807, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.04250809417099567, 0.016780718633909952, 0.5081931796550644}), new DoubleVector(new double[] {-0.10647757569918483, -0.08454352202496404, -0.14343266327916995}), new DoubleVector(new double[] {0.6133071689247316, 0.7691075085359876, -0.17979976879601423}), 2.3015422693647807, 0, 30.0);

    Units units_0 = 
      simulation_0.getUnitsManager().getPreferredUnits(new IntVector(new int[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));
  }
}
