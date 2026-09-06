// STAR-CCM+ macro: post_processing.java
// Written by STAR-CCM+ 13.06.012
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;
import star.flow.*;
import star.meshing.*;

public class post_processing extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    MonitorPlot monitorPlot_0 = 
      ((MonitorPlot) simulation_0.getPlotManager().getPlot("Total Downforce Monitor Plot"));

    PlotUpdate plotUpdate_1 = 
      monitorPlot_0.getPlotUpdate();

    HardcopyProperties hardcopyProperties_1 = 
      plotUpdate_1.getHardcopyProperties();

    hardcopyProperties_1.setCurrentResolutionWidth(608);

    hardcopyProperties_1.setCurrentResolutionWidth(133);

    hardcopyProperties_1.setCurrentResolutionWidth(1095);

    hardcopyProperties_1.setCurrentResolutionHeight(646);

    simulation_0.getSceneManager().createScalarScene("Scalar Scene", "Outline", "Scalar");

    Scene scene_0 = 
      simulation_0.getSceneManager().getScene("Scalar Scene 1");

    scene_0.initializeAndWait();

    PartDisplayer partDisplayer_0 = 
      ((PartDisplayer) scene_0.getDisplayerManager().getDisplayer("Outline 1"));

    partDisplayer_0.initialize();

    ScalarDisplayer scalarDisplayer_0 = 
      ((ScalarDisplayer) scene_0.getDisplayerManager().getDisplayer("Scalar 1"));

    scalarDisplayer_0.initialize();

    Legend legend_0 = 
      scalarDisplayer_0.getLegend();

    BlueRedLookupTable blueRedLookupTable_0 = 
      ((BlueRedLookupTable) simulation_0.get(LookupTableManager.class).getObject("blue-red"));

    legend_0.setLookupTable(blueRedLookupTable_0);

    SceneUpdate sceneUpdate_0 = 
      scene_0.getSceneUpdate();

    HardcopyProperties hardcopyProperties_2 = 
      sceneUpdate_0.getHardcopyProperties();

    hardcopyProperties_2.setCurrentResolutionWidth(25);

    hardcopyProperties_2.setCurrentResolutionHeight(25);

    hardcopyProperties_1.setCurrentResolutionWidth(1097);

    hardcopyProperties_1.setCurrentResolutionHeight(647);

    hardcopyProperties_2.setCurrentResolutionWidth(1095);

    hardcopyProperties_2.setCurrentResolutionHeight(646);

    scene_0.resetCamera();

    scalarDisplayer_0.getInputParts().setQuery(null);

    CadPart cadPart_0 = 
      ((CadPart) simulation_0.get(SimulationPartManager.class).getPart("boi_0"));

    PartSurface partSurface_0 = 
      ((PartSurface) cadPart_0.getPartSurfaceManager().getPartSurface("Faces"));

    CompositePart compositePart_0 = 
      ((CompositePart) simulation_0.get(SimulationPartManager.class).getPart("car"));

    CadPart cadPart_1 = 
      ((CadPart) compositePart_0.getChildParts().getPart("Body1"));

    PartSurface partSurface_1 = 
      ((PartSurface) cadPart_1.getPartSurfaceManager().getPartSurface("_rl_rlwheel_1"));

    PartSurface partSurface_2 = 
      ((PartSurface) cadPart_1.getPartSurfaceManager().getPartSurface("_rl_rlwheel_2"));

    PartSurface partSurface_3 = 
      ((PartSurface) cadPart_1.getPartSurfaceManager().getPartSurface("_rl_rlwheel_3"));

    PartSurface partSurface_4 = 
      ((PartSurface) cadPart_1.getPartSurfaceManager().getPartSurface("_rl_rlwheel_4"));

    PartSurface partSurface_5 = 
      ((PartSurface) cadPart_1.getPartSurfaceManager().getPartSurface("_rl_rlwheel_5"));

    CadPart cadPart_2 = 
      ((CadPart) compositePart_0.getChildParts().getPart("Body2"));

    PartSurface partSurface_6 = 
      ((PartSurface) cadPart_2.getPartSurfaceManager().getPartSurface("Faces"));

    CadPart cadPart_3 = 
      ((CadPart) compositePart_0.getChildParts().getPart("Body3"));

    PartSurface partSurface_7 = 
      ((PartSurface) cadPart_3.getPartSurfaceManager().getPartSurface("_fl_flwheel_1"));

    PartSurface partSurface_8 = 
      ((PartSurface) cadPart_3.getPartSurfaceManager().getPartSurface("_fl_flwheel_2"));

    PartSurface partSurface_9 = 
      ((PartSurface) cadPart_3.getPartSurfaceManager().getPartSurface("_fl_flwheel_3"));

    PartSurface partSurface_10 = 
      ((PartSurface) cadPart_3.getPartSurfaceManager().getPartSurface("_fl_flwheel_4"));

    PartSurface partSurface_11 = 
      ((PartSurface) cadPart_3.getPartSurfaceManager().getPartSurface("_fl_flwheel_5"));

    CadPart cadPart_4 = 
      ((CadPart) compositePart_0.getChildParts().getPart("Body4"));

    PartSurface partSurface_12 = 
      ((PartSurface) cadPart_4.getPartSurfaceManager().getPartSurface("_fr_frwheel_1"));

    PartSurface partSurface_13 = 
      ((PartSurface) cadPart_4.getPartSurfaceManager().getPartSurface("_fr_frwheel_2"));

    PartSurface partSurface_14 = 
      ((PartSurface) cadPart_4.getPartSurfaceManager().getPartSurface("_fr_frwheel_3"));

    PartSurface partSurface_15 = 
      ((PartSurface) cadPart_4.getPartSurfaceManager().getPartSurface("_fr_frwheel_4"));

    PartSurface partSurface_16 = 
      ((PartSurface) cadPart_4.getPartSurfaceManager().getPartSurface("_fr_frwheel_5"));

    CadPart cadPart_5 = 
      ((CadPart) compositePart_0.getChildParts().getPart("Body5"));

    PartSurface partSurface_17 = 
      ((PartSurface) cadPart_5.getPartSurfaceManager().getPartSurface("_wall_head_1_rep"));

    PartSurface partSurface_18 = 
      ((PartSurface) cadPart_5.getPartSurfaceManager().getPartSurface("_wall_head_2_rep"));

    PartSurface partSurface_19 = 
      ((PartSurface) cadPart_5.getPartSurfaceManager().getPartSurface("Faces"));

    CadPart cadPart_6 = 
      ((CadPart) simulation_0.get(SimulationPartManager.class).getPart("domain"));

    PartSurface partSurface_20 = 
      ((PartSurface) cadPart_6.getPartSurfaceManager().getPartSurface("_ground_floor_1"));

    PartSurface partSurface_21 = 
      ((PartSurface) cadPart_6.getPartSurfaceManager().getPartSurface("_inlet_in_1"));

    PartSurface partSurface_22 = 
      ((PartSurface) cadPart_6.getPartSurfaceManager().getPartSurface("_outlet_out_1"));

    PartSurface partSurface_23 = 
      ((PartSurface) cadPart_6.getPartSurfaceManager().getPartSurface("_symmetry_ext_1"));

    PartSurface partSurface_24 = 
      ((PartSurface) cadPart_6.getPartSurfaceManager().getPartSurface("wall_ext_1"));

    PartSurface partSurface_25 = 
      ((PartSurface) cadPart_6.getPartSurfaceManager().getPartSurface("wall_ext_2"));

    MeshOperationPart meshOperationPart_0 = 
      ((MeshOperationPart) simulation_0.get(SimulationPartManager.class).getPart("Subtract"));

    PartSurface partSurface_26 = 
      ((PartSurface) meshOperationPart_0.getPartSurfaceManager().getPartSurface("car.Body1._rl_rlwheel_1"));

    PartSurface partSurface_27 = 
      ((PartSurface) meshOperationPart_0.getPartSurfaceManager().getPartSurface("car.Body1._rl_rlwheel_2"));

    PartSurface partSurface_28 = 
      ((PartSurface) meshOperationPart_0.getPartSurfaceManager().getPartSurface("car.Body1._rl_rlwheel_3"));

    PartSurface partSurface_29 = 
      ((PartSurface) meshOperationPart_0.getPartSurfaceManager().getPartSurface("car.Body1._rl_rlwheel_4"));

    PartSurface partSurface_30 = 
      ((PartSurface) meshOperationPart_0.getPartSurfaceManager().getPartSurface("car.Body1._rl_rlwheel_5"));

    PartSurface partSurface_31 = 
      ((PartSurface) meshOperationPart_0.getPartSurfaceManager().getPartSurface("car.Body3._fl_flwheel_1"));

    PartSurface partSurface_32 = 
      ((PartSurface) meshOperationPart_0.getPartSurfaceManager().getPartSurface("car.Body3._fl_flwheel_2"));

    PartSurface partSurface_33 = 
      ((PartSurface) meshOperationPart_0.getPartSurfaceManager().getPartSurface("car.Body3._fl_flwheel_3"));

    PartSurface partSurface_34 = 
      ((PartSurface) meshOperationPart_0.getPartSurfaceManager().getPartSurface("car.Body3._fl_flwheel_4"));

    PartSurface partSurface_35 = 
      ((PartSurface) meshOperationPart_0.getPartSurfaceManager().getPartSurface("car.Body3._fl_flwheel_5"));

    PartSurface partSurface_36 = 
      ((PartSurface) meshOperationPart_0.getPartSurfaceManager().getPartSurface("car.Body5._wall_head_1_rep"));

    PartSurface partSurface_37 = 
      ((PartSurface) meshOperationPart_0.getPartSurfaceManager().getPartSurface("car.Body5._wall_head_2_rep"));

    PartSurface partSurface_38 = 
      ((PartSurface) meshOperationPart_0.getPartSurfaceManager().getPartSurface("car.Body5.Faces"));

    PartSurface partSurface_39 = 
      ((PartSurface) meshOperationPart_0.getPartSurfaceManager().getPartSurface("domain._ground_floor_1"));

    PartSurface partSurface_40 = 
      ((PartSurface) meshOperationPart_0.getPartSurfaceManager().getPartSurface("domain._inlet_in_1"));

    PartSurface partSurface_41 = 
      ((PartSurface) meshOperationPart_0.getPartSurfaceManager().getPartSurface("domain._outlet_out_1"));

    PartSurface partSurface_42 = 
      ((PartSurface) meshOperationPart_0.getPartSurfaceManager().getPartSurface("domain._symmetry_ext_1"));

    PartSurface partSurface_43 = 
      ((PartSurface) meshOperationPart_0.getPartSurfaceManager().getPartSurface("domain.wall_ext_1"));

    PartSurface partSurface_44 = 
      ((PartSurface) meshOperationPart_0.getPartSurfaceManager().getPartSurface("domain.wall_ext_2"));

    Region region_0 = 
      simulation_0.getRegionManager().getRegion("Region 1");

    Boundary boundary_0 = 
      region_0.getBoundaryManager().getBoundary("1");

    Boundary boundary_1 = 
      region_0.getBoundaryManager().getBoundary("2");

    Boundary boundary_2 = 
      region_0.getBoundaryManager().getBoundary("car.Body5.Faces");

    Boundary boundary_3 = 
      region_0.getBoundaryManager().getBoundary("Default");

    Boundary boundary_4 = 
      region_0.getBoundaryManager().getBoundary("ext");

    Boundary boundary_5 = 
      region_0.getBoundaryManager().getBoundary("floor");

    Boundary boundary_6 = 
      region_0.getBoundaryManager().getBoundary("flwheel");

    Boundary boundary_7 = 
      region_0.getBoundaryManager().getBoundary("head");

    Boundary boundary_8 = 
      region_0.getBoundaryManager().getBoundary("in");

    Boundary boundary_9 = 
      region_0.getBoundaryManager().getBoundary("out");

    Boundary boundary_10 = 
      region_0.getBoundaryManager().getBoundary("rlwheel");

    scalarDisplayer_0.getInputParts().setObjects(partSurface_0, partSurface_1, partSurface_2, partSurface_3, partSurface_4, partSurface_5, partSurface_6, partSurface_7, partSurface_8, partSurface_9, partSurface_10, partSurface_11, partSurface_12, partSurface_13, partSurface_14, partSurface_15, partSurface_16, partSurface_17, partSurface_18, partSurface_19, partSurface_20, partSurface_21, partSurface_22, partSurface_23, partSurface_24, partSurface_25, partSurface_26, partSurface_27, partSurface_28, partSurface_29, partSurface_30, partSurface_31, partSurface_32, partSurface_33, partSurface_34, partSurface_35, partSurface_36, partSurface_37, partSurface_38, partSurface_39, partSurface_40, partSurface_41, partSurface_42, partSurface_43, partSurface_44, boundary_0, boundary_1, boundary_2, boundary_3, boundary_4, boundary_5, boundary_6, boundary_7, boundary_8, boundary_9, boundary_10);

    scalarDisplayer_0.getInputParts().setQuery(null);

    scalarDisplayer_0.getInputParts().setObjects(partSurface_0, partSurface_1, partSurface_2, partSurface_3, partSurface_4, partSurface_5, partSurface_6, partSurface_7, partSurface_8, partSurface_9, partSurface_10, partSurface_11, partSurface_12, partSurface_13, partSurface_14, partSurface_15, partSurface_16, partSurface_17, partSurface_18, partSurface_19, partSurface_20, partSurface_21, partSurface_22, partSurface_23, partSurface_24, partSurface_25, partSurface_26, partSurface_27, partSurface_28, partSurface_29, partSurface_30, partSurface_31, partSurface_32, partSurface_33, partSurface_34, partSurface_35, partSurface_36, partSurface_37, partSurface_38, partSurface_39, partSurface_40, partSurface_41, partSurface_42, partSurface_43, partSurface_44, boundary_4, boundary_5, boundary_9);

    legend_0.updateLayout(new DoubleVector(new double[] {0.3, 0.05}), 0.6, 0.044, 0);

    CurrentView currentView_0 = 
      scene_0.getCurrentView();

    currentView_0.setInput(new DoubleVector(new double[] {4.999999999999992, 5.000000000000002, 0.0}), new DoubleVector(new double[] {4.999999999999992, 5.000000000000002, 156.94437017604233}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 40.62019202317981, 0);

    TotalPressureCoefficientFunction totalPressureCoefficientFunction_0 = 
      ((TotalPressureCoefficientFunction) simulation_0.getFieldFunctionManager().getFunction("TotalPressureCoefficient"));

    scalarDisplayer_0.getScalarDisplayQuantity().setFieldFunction(totalPressureCoefficientFunction_0);

    scene_0.setViewOrientation(new DoubleVector(new double[] {1.0, 1.0, 1.0}), new DoubleVector(new double[] {0.0, 1.0, 0.0}));

    scalarDisplayer_0.getInputParts().setQuery(null);

    scalarDisplayer_0.getInputParts().setObjects(partSurface_0, partSurface_1, partSurface_2, partSurface_3, partSurface_4, partSurface_5, partSurface_6, partSurface_7, partSurface_8, partSurface_9, partSurface_10, partSurface_11, partSurface_12, partSurface_13, partSurface_14, partSurface_15, partSurface_16, partSurface_17, partSurface_18, partSurface_19, partSurface_20, partSurface_21, partSurface_22, partSurface_23, partSurface_24, partSurface_25, partSurface_26, partSurface_27, partSurface_28, partSurface_29, partSurface_30, partSurface_31, partSurface_32, partSurface_33, partSurface_34, partSurface_35, partSurface_36, partSurface_37, partSurface_38, boundary_4, boundary_5, boundary_9);

    currentView_0.setInput(new DoubleVector(new double[] {4.972681922632702, 2.732401098135881, 0.13214129068244063}), new DoubleVector(new double[] {64.91597597795334, 62.675695153456516, 60.07543534600309}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 40.62019202317981, 0);

    currentView_0.setInput(new DoubleVector(new double[] {1.1737129831537452, 1.1042886205004123, 0.5411525145215101}), new DoubleVector(new double[] {24.792494350112246, 24.723069987458913, 24.159933881480015}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 40.62019202317981, 0);

    currentView_0.setInput(new DoubleVector(new double[] {0.8158519446742503, 1.111053655504513, 0.8900813127797846}), new DoubleVector(new double[] {18.05318762136293, 18.348389332193193, 18.127416989468472}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 40.62019202317981, 0);

    currentView_0.setInput(new DoubleVector(new double[] {0.5297323562725431, 0.4518365937669149, 0.6470688364396651}), new DoubleVector(new double[] {12.102030265523695, 12.024134503018066, 12.219366745690818}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 40.62019202317981, 0);

    currentView_0.setInput(new DoubleVector(new double[] {0.4639532664332542, 0.402365170198169, 1.0575867705940176}), new DoubleVector(new double[] {7.998380932005276, 7.9367928357701905, 8.592014436166039}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 40.62019202317981, 0);

    currentView_0.setInput(new DoubleVector(new double[] {0.3351319055280024, 0.286284535664306, 1.3008830354444116}), new DoubleVector(new double[] {4.792313772234398, 4.743466402370702, 5.7580649021508075}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 40.62019202317981, 0);
//----------------------Set Up Viridis------------------------------------------
    UserLookupTable userLookupTable_0 = 
      simulation_0.get(LookupTableManager.class).createLookupTable();

    userLookupTable_0.setPresentationName("Viridis");   

    userLookupTable_0.setColorMap(new ColorMap(new DoubleVector(new double[] {0.0, 0.26666666666666666, 0.00392156862745098, 0.32941176470588235, 0.055556, 0.2823529411764706, 0.08235294117647059, 0.403921568627451, 0.11111, 0.2823529411764706, 0.14901960784313725, 0.4666666666666667, 0.166667, 0.27058823529411763, 0.21568627450980393, 0.5058823529411764, 0.222222, 0.25098039215686274, 0.2784313725490196, 0.5333333333333333, 0.277778, 0.2235294117647059, 0.33725490196078434, 0.5490196078431373, 0.333333, 0.17647058823529413, 0.4392156862745098, 0.5568627450980392, 0.3888889, 0.1568627450980392, 0.49019607843137253, 0.5568627450980392, 0.444444, 0.13725490196078433, 0.5411764705882353, 0.5529411764705883, 0.5, 0.12156862745098039, 0.5882352941176471, 0.5450980392156862, 0.555556, 0.12549019607843137, 0.6392156862745098, 0.5294117647058824, 0.611111, 0.1607843137254902, 0.6862745098039216, 0.4980392156862745, 0.6666667, 0.23529411764705882, 0.7333333333333333, 0.4588235294117647, 0.7222222, 0.3333333333333333, 0.7764705882352941, 0.403921568627451, 0.777778, 0.45098039215686275, 0.8156862745098039, 0.3333333333333333, 0.8333333, 0.5843137254901961, 0.8470588235294118, 0.25098039215686274, 0.8888889, 0.7215686274509804, 0.8705882352941177, 0.1607843137254902, 0.944444, 0.8627450980392157, 0.8901960784313725, 0.09803921568627451, 1.0, 0.9921568627450981, 0.9058823529411765, 0.1450980392156863}), new DoubleVector(new double[] {0.0, 1.0, 1.0, 1.0}), 1));
    legend_0.setLookupTable(userLookupTable_0);
  }
}
