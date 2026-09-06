// STAR-CCM+ macro: selectRoot.java
// Written by STAR-CCM+ 15.02.009
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;
import star.cadmodeler.*;

public class selectRoot extends StarMacro {

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

    currentView_0.setInput(new DoubleVector(new double[] {0.10153725923455996, -0.010511881284865812, 0.505940704861614}), new DoubleVector(new double[] {0.10150961569247499, 0.1249124460504727, -0.02003611320910957}), new DoubleVector(new double[] {0.09565157140941216, 0.9639769028734254, 0.2481920780634924}), 2.3015422693647807, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.10153725923455996, -0.010511881284865812, 0.505940704861614}), new DoubleVector(new double[] {0.10150961569247499, 0.1249124460504727, -0.02003611320910957}), new DoubleVector(new double[] {0.09565157140941216, 0.9639769028734254, 0.2481920780634924}), 2.3015422693647807, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.10153725923455996, -0.010511881284865812, 0.505940704861614}), new DoubleVector(new double[] {0.10150961569247499, 0.1249124460504727, -0.02003611320910957}), new DoubleVector(new double[] {0.09565157140941216, 0.9639769028734254, 0.2481920780634924}), 2.3015422693647807, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.10153725923455996, -0.010511881284865812, 0.505940704861614}), new DoubleVector(new double[] {0.10150961569247499, 0.1249124460504727, -0.02003611320910957}), new DoubleVector(new double[] {0.09565157140941216, 0.9639769028734254, 0.2481920780634924}), 2.3015422693647807, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.10153725923455996, -0.010511881284865812, 0.505940704861614}), new DoubleVector(new double[] {0.10150961569247499, 0.1249124460504727, -0.02003611320910957}), new DoubleVector(new double[] {0.09565157140941216, 0.9639769028734254, 0.2481920780634924}), 2.3015422693647807, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.10153725923455996, -0.010511881284865812, 0.505940704861614}), new DoubleVector(new double[] {0.10150961569247499, 0.1249124460504727, -0.02003611320910957}), new DoubleVector(new double[] {0.09565157140941216, 0.9639769028734254, 0.2481920780634924}), 2.3015422693647807, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.10153725923455996, -0.010511881284865812, 0.505940704861614}), new DoubleVector(new double[] {0.10150961569247499, 0.1249124460504727, -0.02003611320910957}), new DoubleVector(new double[] {0.09565157140941216, 0.9639769028734254, 0.2481920780634924}), 2.3015422693647807, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.10153725923455996, -0.010511881284865812, 0.505940704861614}), new DoubleVector(new double[] {0.10150961569247499, 0.1249124460504727, -0.02003611320910957}), new DoubleVector(new double[] {0.09565157140941216, 0.9639769028734254, 0.2481920780634924}), 2.3015422693647807, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.10153725923455996, -0.010511881284865812, 0.505940704861614}), new DoubleVector(new double[] {0.10150961569247499, 0.1249124460504727, -0.02003611320910957}), new DoubleVector(new double[] {0.09565157140941216, 0.9639769028734254, 0.2481920780634924}), 2.3015422693647807, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.10153725923455996, -0.010511881284865812, 0.505940704861614}), new DoubleVector(new double[] {0.10150961569247499, 0.1249124460504727, -0.02003611320910957}), new DoubleVector(new double[] {0.09565157140941216, 0.9639769028734254, 0.2481920780634924}), 2.3015422693647807, 0, 30.0);

    CadModel cadModel_0 = 
      ((CadModel) simulation_0.get(SolidModelManager.class).getObject("3D-CAD Model 1"));

    ImportCadFileFeature importCadFileFeature_0 = 
      ((ImportCadFileFeature) cadModel_0.getFeature("ImportCad 1"));

    star.cadmodeler.Body cadmodelerBody_0 = 
      ((star.cadmodeler.Body) importCadFileFeature_0.getBodyByIndex(1));

    Face face_0 = 
      ((Face) importCadFileFeature_0.getFaceByLocation(cadmodelerBody_0,new DoubleVector(new double[] {0.1342203898327811, 0.06282857617321425, 3.148999968942829})));

    FillFeature fillFeature_0 = 
      ((FillFeature) cadModel_0.getFeature("Fill Surface 1"));

    Face face_1 = 
      ((Face) fillFeature_0.getFaceByLocation(cadmodelerBody_0,new DoubleVector(new double[] {0.08855328562561882, 0.018382443857779318, 0.508000000000002})));

    Edge edge_0 = 
      ((Edge) cadModel_0.getEdgeByLocation(new NeoObjectVector(new Object[] {face_0, face_1})));

    Face face_2 = 
      ((Face) importCadFileFeature_0.getFaceByLocation(cadmodelerBody_0,new DoubleVector(new double[] {0.14178339473479024, -0.05069026328718468, 3.148999968942829})));

    Edge edge_1 = 
      ((Edge) cadModel_0.getEdgeByLocation(new NeoObjectVector(new Object[] {face_1, face_2})));

    Face face_3 = 
      ((Face) importCadFileFeature_0.getFaceByLocation(cadmodelerBody_0,new DoubleVector(new double[] {0.45550578972005196, 0.023712995610485933, 3.1489999689428294})));

    Edge edge_2 = 
      ((Edge) cadModel_0.getEdgeByLocation(new NeoObjectVector(new Object[] {face_3, face_1})));

    cadModel_0.setEdgeNameAttributes(new NeoObjectVector(new Object[] {edge_0, edge_1, edge_2}), "RootCircle");

    currentView_0.setInput(new DoubleVector(new double[] {0.10153725923455996, -0.010511881284865812, 0.505940704861614}), new DoubleVector(new double[] {0.10150961569247499, 0.1249124460504727, -0.02003611320910957}), new DoubleVector(new double[] {0.09565157140941216, 0.9639769028734254, 0.2481920780634924}), 2.3015422693647807, 0, 30.0);

    cadmodelerBody_0.getEdgeManager().getEdgeProperties(new NeoObjectVector(new Object[] {edge_0, edge_1, edge_2}));

    Units units_0 = 
      simulation_0.getUnitsManager().getPreferredUnits(new IntVector(new int[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

    cadmodelerBody_0.getEdgeManager().getEdgeProperties(new NeoObjectVector(new Object[] {edge_2, edge_1, edge_0}));
  }
}
