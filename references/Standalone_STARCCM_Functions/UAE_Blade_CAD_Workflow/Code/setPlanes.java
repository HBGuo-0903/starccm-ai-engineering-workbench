// STAR-CCM+ macro: setPlanes.java
// Written by STAR-CCM+ 15.02.009
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;
import star.cadmodeler.*;

public class setPlanes extends StarMacro {

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

    currentView_0.setInput(new DoubleVector(new double[] {-0.364916508221935, 0.2942574608394095, 0.897747233630017}), new DoubleVector(new double[] {-1.4043373239990111, 0.15847530892352216, -1.6937220416360703}), new DoubleVector(new double[] {0.17583832171176217, 0.976866037078954, -0.12171125757000566}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.21801301428472086, 0.18310208004042422, 0.6122119402808703}), new DoubleVector(new double[] {-0.6819926715005971, 0.12249125202379633, -0.5445755780877337}), new DoubleVector(new double[] {0.17583832171176217, 0.976866037078954, -0.12171125757000566}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.21801301428472086, 0.18310208004042422, 0.6122119402808703}), new DoubleVector(new double[] {-0.6819926715005971, 0.12249125202379633, -0.5445755780877337}), new DoubleVector(new double[] {0.17583832171176217, 0.976866037078954, -0.12171125757000566}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.21801301428472086, 0.18310208004042422, 0.6122119402808703}), new DoubleVector(new double[] {-0.6819926715005971, 0.12249125202379633, -0.5445755780877337}), new DoubleVector(new double[] {0.17583832171176217, 0.976866037078954, -0.12171125757000566}), 2.3015629593758664, 0, 30.0);

    CadModel cadModel_1 = 
      ((CadModel) simulation_0.get(SolidModelManager.class).getObject("3D-CAD Model 1"));

    star.cadmodeler.Body cadmodelerBody_0 = 
      ((star.cadmodeler.Body) cadModel_1.getBody("Blade"));

    Face face_3 = 
      ((Face) cadmodelerBody_0.getFace("Hub"));

    TransformSketchPlane transformSketchPlane_0 = 
      cadModel_1.getFeatureManager().createPlaneByTransformation(face_3);

    cadModel_1.allowMakingPartDirty(false);

    Units units_0 = 
      simulation_0.getUnitsManager().getPreferredUnits(new IntVector(new int[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

    currentView_0.setInput(new DoubleVector(new double[] {-0.23893868328240736, 0.19419352554976224, 0.6200172313381143}), new DoubleVector(new double[] {-0.7029160282820928, 0.13358299958374303, -0.5367645222457893}), new DoubleVector(new double[] {0.17583832171176217, 0.976866037078954, -0.12171125757000566}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.2856020338417948, 0.20124074627836272, 0.5564230190234383}), new DoubleVector(new double[] {-1.1467837062979043, 0.39365661227272436, -0.36696228231944894}), new DoubleVector(new double[] {0.23957793277961406, 0.9706462005886021, -0.021174687907540648}), 2.3015629593758664, 0, 30.0);

    transformSketchPlane_0.setColorVector(new IntVector(new int[] {-16711936}));

    cadModel_1.allowMakingPartDirty(true);

    transformSketchPlane_0.setRefFace(face_3);

    transformSketchPlane_0.setCoordinateSystemOption(0);

    transformSketchPlane_0.getTranslationVector().setComponents(0.0, 0.0, 0.1);

    transformSketchPlane_0.getTranslationVector().setUnits(units_0);

    transformSketchPlane_0.getAngle().setComponents(0.0, 0.0, 0.0);

    Units units_2 = 
      ((Units) simulation_0.getUnitsManager().getObject("deg"));

    transformSketchPlane_0.getAngle().setUnits(units_2);

    transformSketchPlane_0.markFeatureForEdit();

    cadModel_1.getFeatureManager().execute(transformSketchPlane_0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.2065680761717723, 0.18418699127265936, 0.6337277736503784}), new DoubleVector(new double[] {-1.4212838701339334, 0.3966775874745949, 0.05560075262332764}), new DoubleVector(new double[] {0.1964059202369258, 0.9790997743710099, -0.052804794503413674}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.17322475011072433, 0.17835936312433137, 0.6496921807001943}), new DoubleVector(new double[] {-1.447973790842172, 0.4048545673377755, 0.10794487643477024}), new DoubleVector(new double[] {0.19640592023692544, 0.9790997743710107, -0.05280479450341332}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.17322475011072433, 0.17835936312433137, 0.6496921807001943}), new DoubleVector(new double[] {-1.447973790842172, 0.4048545673377755, 0.10794487643477024}), new DoubleVector(new double[] {0.19640592023692544, 0.9790997743710107, -0.05280479450341332}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.17322475011072433, 0.17835936312433137, 0.6496921807001943}), new DoubleVector(new double[] {-1.447973790842172, 0.4048545673377755, 0.10794487643477024}), new DoubleVector(new double[] {0.19640592023692544, 0.9790997743710107, -0.05280479450341332}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.17322475011072433, 0.17835936312433137, 0.6496921807001943}), new DoubleVector(new double[] {-1.447973790842172, 0.4048545673377755, 0.10794487643477024}), new DoubleVector(new double[] {0.19640592023692544, 0.9790997743710107, -0.05280479450341332}), 2.3015629593758664, 0, 30.0);

    TransformSketchPlane transformSketchPlane_1 = 
      cadModel_1.getFeatureManager().createPlaneByTransformation(face_3);

    cadModel_1.allowMakingPartDirty(false);

    transformSketchPlane_1.setColorVector(new IntVector(new int[] {-16711936}));

    cadModel_1.allowMakingPartDirty(true);

    transformSketchPlane_1.setRefFace(face_3);

    transformSketchPlane_1.setCoordinateSystemOption(0);

    transformSketchPlane_1.getTranslationVector().setComponents(0.0, 0.0, -0.1);

    transformSketchPlane_1.getTranslationVector().setUnits(units_0);

    transformSketchPlane_1.getAngle().setComponents(0.0, 0.0, 0.0);

    transformSketchPlane_1.getAngle().setUnits(units_2);

    transformSketchPlane_1.markFeatureForEdit();

    cadModel_1.getFeatureManager().execute(transformSketchPlane_1);

    currentView_0.setInput(new DoubleVector(new double[] {-0.18700490187460836, 0.18150774257734295, 0.64590513311239}), new DoubleVector(new double[] {-1.523149869992897, 0.49323722504094486, 0.29779839216891096}), new DoubleVector(new double[] {0.2366717321748825, 0.9708039186077551, -0.03906715762881052}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.18700490187460836, 0.18150774257734295, 0.64590513311239}), new DoubleVector(new double[] {-1.523149869992897, 0.49323722504094486, 0.29779839216891096}), new DoubleVector(new double[] {0.2366717321748825, 0.9708039186077551, -0.03906715762881052}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.18700490187460836, 0.18150774257734295, 0.64590513311239}), new DoubleVector(new double[] {-1.523149869992897, 0.49323722504094486, 0.29779839216891096}), new DoubleVector(new double[] {0.2366717321748825, 0.9708039186077551, -0.03906715762881052}), 2.3015629593758664, 0, 30.0);

    TransformSketchPlane transformSketchPlane_2 = 
      cadModel_1.getFeatureManager().createPlaneByTransformation(transformSketchPlane_1);

    cadModel_1.allowMakingPartDirty(false);

    transformSketchPlane_2.setColorVector(new IntVector(new int[] {-16711936}));

    cadModel_1.allowMakingPartDirty(true);

    transformSketchPlane_2.setRefSketchPlane(transformSketchPlane_1);

    transformSketchPlane_2.getTranslationVector().setComponents(0.0, 0.0, -0.1);

    transformSketchPlane_2.getTranslationVector().setUnits(units_0);

    transformSketchPlane_2.getAngle().setComponents(0.0, 0.0, 0.0);

    transformSketchPlane_2.getAngle().setUnits(units_2);

    transformSketchPlane_2.markFeatureForEdit();

    cadModel_1.getFeatureManager().execute(transformSketchPlane_2);

    currentView_0.setInput(new DoubleVector(new double[] {-0.18700490187460836, 0.18150774257734295, 0.64590513311239}), new DoubleVector(new double[] {-1.523149869992897, 0.49323722504094486, 0.29779839216891096}), new DoubleVector(new double[] {0.2366717321748825, 0.9708039186077551, -0.03906715762881052}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.1984608743744833, 0.18465414581150533, 0.6384315272150707}), new DoubleVector(new double[] {-1.4413815252274864, 0.5093492416881809, -0.014387552423585526}), new DoubleVector(new double[] {0.2662193001679001, 0.9635162726982, -0.027634696739926162}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.1984608743744833, 0.18465414581150533, 0.6384315272150707}), new DoubleVector(new double[] {-1.4413815252274864, 0.5093492416881809, -0.014387552423585526}), new DoubleVector(new double[] {0.2662193001679001, 0.9635162726982, -0.027634696739926162}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.1984608743744833, 0.18465414581150533, 0.6384315272150707}), new DoubleVector(new double[] {-1.4413815252274864, 0.5093492416881809, -0.014387552423585526}), new DoubleVector(new double[] {0.2662193001679001, 0.9635162726982, -0.027634696739926162}), 2.3015629593758664, 0, 30.0);

    TransformSketchPlane transformSketchPlane_3 = 
      cadModel_1.getFeatureManager().createPlaneByTransformation(face_3);

    cadModel_1.allowMakingPartDirty(false);

    transformSketchPlane_3.setColorVector(new IntVector(new int[] {-16711936}));

    cadModel_1.allowMakingPartDirty(true);

    transformSketchPlane_3.setRefFace(face_3);

    transformSketchPlane_3.setCoordinateSystemOption(0);

    transformSketchPlane_3.getTranslationVector().setComponents(0.0, 0.0, 0.0);

    transformSketchPlane_3.getTranslationVector().setUnits(units_0);

    transformSketchPlane_3.getAngle().setComponents(0.0, 0.0, 0.0);

    transformSketchPlane_3.getAngle().setUnits(units_2);

    transformSketchPlane_3.markFeatureForEdit();

    cadModel_1.getFeatureManager().execute(transformSketchPlane_3);

    currentView_0.setInput(new DoubleVector(new double[] {-0.217471582130574, 0.19849282329505613, 0.6262346910149124}), new DoubleVector(new double[] {-1.305951881716503, 0.9225033802468292, -0.055072843332469534}), new DoubleVector(new double[] {0.5029858817284522, 0.8575558203491077, 0.10771823321636899}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.217471582130574, 0.19849282329505613, 0.6262346910149124}), new DoubleVector(new double[] {-1.305951881716503, 0.9225033802468292, -0.055072843332469534}), new DoubleVector(new double[] {0.5029858817284522, 0.8575558203491077, 0.10771823321636899}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.217471582130574, 0.19849282329505613, 0.6262346910149124}), new DoubleVector(new double[] {-1.305951881716503, 0.9225033802468292, -0.055072843332469534}), new DoubleVector(new double[] {0.5029858817284522, 0.8575558203491077, 0.10771823321636899}), 2.3015629593758664, 0, 30.0);

    TransformSketchPlane transformSketchPlane_4 = 
      cadModel_1.getFeatureManager().createPlaneByTransformation(transformSketchPlane_2);

    cadModel_1.allowMakingPartDirty(false);

    transformSketchPlane_4.setColorVector(new IntVector(new int[] {-16711936}));

    cadModel_1.allowMakingPartDirty(true);

    transformSketchPlane_4.setRefSketchPlane(transformSketchPlane_2);

    transformSketchPlane_4.getTranslationVector().setComponents(0.0, 0.0, -0.1);

    transformSketchPlane_4.getTranslationVector().setUnits(units_0);

    transformSketchPlane_4.getAngle().setComponents(0.0, 0.0, 0.0);

    transformSketchPlane_4.getAngle().setUnits(units_2);

    transformSketchPlane_4.markFeatureForEdit();

    cadModel_1.getFeatureManager().execute(transformSketchPlane_4);
  }
}
