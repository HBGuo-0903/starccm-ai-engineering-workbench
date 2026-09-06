// Simcenter STAR-CCM+ macro: Normals2.java
// Written by Simcenter STAR-CCM+ 16.04.012
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class Normals2 extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    PlaneSection planeSection_0 = 
      ((PlaneSection) simulation_0.getPartManager().getObject("Y Normal"));

    FvRepresentation fvRepresentation_0 = 
      ((FvRepresentation) simulation_0.getRepresentationManager().getObject("Volume Mesh"));

    planeSection_0.exportDataSourceSTL(fvRepresentation_0, false, resolvePath("N:\\1 - USM23 CAD\\Cooling\\CFD\\Radiator\\Old Sims\\YNormal.stl"));

    Scene scene_2 = 
      simulation_0.getSceneManager().getScene("Turbulent Kinetic Energy");

    CurrentView currentView_2 = 
      scene_2.getCurrentView();

    currentView_2.setInput(new DoubleVector(new double[] {-4.282388681075502, -4.48074688257886, 2.9839923877258374}), new DoubleVector(new double[] {-4.282388681075502, 31.59114051551283, 2.9839923877258374}), new DoubleVector(new double[] {0.0, 0.0, 1.0}), 10.27295116104434, 1, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {-4.282388681075502, -4.48074688257886, 2.9839923877258374}), new DoubleVector(new double[] {-4.282388681075502, 31.59114051551283, 2.9839923877258374}), new DoubleVector(new double[] {0.0, 0.0, 1.0}), 10.27295116104434, 1, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {-4.282388681075502, -4.48074688257886, 2.9839923877258374}), new DoubleVector(new double[] {-4.282388681075502, 31.59114051551283, 2.9839923877258374}), new DoubleVector(new double[] {0.0, 0.0, 1.0}), 10.27295116104434, 1, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {-0.24753447828806152, -1.8971758741770373E-6, 5.213780236634685}), new DoubleVector(new double[] {-0.24753447828806152, 31.59114051551283, 5.213780236634685}), new DoubleVector(new double[] {0.0, 0.0, 1.0}), 10.27295116104434, 1, 30.0);
  }
}
