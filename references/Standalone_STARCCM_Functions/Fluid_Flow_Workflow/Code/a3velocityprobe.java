// Simcenter STAR-CCM+ macro: a3velocityprobe.java
// Written by Simcenter STAR-CCM+ 19.02.009


// long code

// NEEDS Scenes prior to running - geometry, mesh, and scalar scenes


// velocity probe may be in incorrect position to due pre-proccesing on Blender
// double check velocity probe location




package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.base.report.*;
import star.vis.*;
import star.common.graph.*;

public class a3velocityprobe extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    Scene scene_2 = 
      simulation_0.getSceneManager().getScene("Geometry Scene 1");

    CurrentView currentView_2 = 
      scene_2.getCurrentView();

    currentView_2.setInput(new DoubleVector(new double[] {3.585970800191329, 9.061470370231156, 14.843441890300344}), new DoubleVector(new double[] {-1.495223522856089, 14.03615507111492, 15.559431644433134}), new DoubleVector(new double[] {0.03950788817397728, 0.18176852598379956, -0.9825473676794035}), 13.584650580483277, 0, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {4.35206124059502, 9.687618132530838, 14.569869192615526}), new DoubleVector(new double[] {-2.3394770901828656, 16.238891562186556, 15.512772096858397}), new DoubleVector(new double[] {0.03950788817397728, 0.18176852598379956, -0.9825473676794035}), 13.584650580483277, 0, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {1.842559798022878, 11.093604588979302, 14.99192209404135}), new DoubleVector(new double[] {-7.142584542895353, 13.325089364768813, 16.68557689177751}), new DoubleVector(new double[] {-0.11386119029157725, 0.2676919036230377, -0.9567531939220691}), 13.584650580483277, 0, 30.0);

    Units units_2 = 
      simulation_0.getUnitsManager().getPreferredUnits(Dimensions.Builder().length(1).build());

    scene_2.setTransparencyOverrideMode(SceneTransparencyOverride.MAKE_SCENE_TRANSPARENT);

    scene_2.getCreatorGroup().setQuery(null);

    Region region_0 = 
      simulation_0.getRegionManager().getRegion("Body 1");

    scene_2.getCreatorGroup().setObjects(region_0);

    currentView_2.setInput(new DoubleVector(new double[] {31.511598159898114, 36.88043467516918, 13.88762050542691}), new DoubleVector(new double[] {-70.86543163525793, 62.30604551795429, 33.18517890979468}), new DoubleVector(new double[] {-0.11386119029157725, 0.2676919036230377, -0.9567531939220691}), 13.584650580483277, 0, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {21.83641566377333, 4.769152344752698, 2.0962273970260945}), new DoubleVector(new double[] {-14.78690665260383, 13.86465299415864, 8.999540598985321}), new DoubleVector(new double[] {-0.11386119029157725, 0.2676919036230377, -0.9567531939220691}), 13.584650580483277, 0, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {19.194731867794253, 8.406352977748641, 1.6342636901235164}), new DoubleVector(new double[] {-24.96117456109171, 19.37259116142769, 9.957431347108445}), new DoubleVector(new double[] {-0.11386119029157725, 0.2676919036230377, -0.9567531939220691}), 13.584650580483277, 0, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {19.14166521116261, 10.88889701444749, 0.08383105238464772}), new DoubleVector(new double[] {-36.57426293702236, 24.726097699745694, 10.586005022897998}), new DoubleVector(new double[] {-0.11386119029157725, 0.2676919036230377, -0.9567531939220691}), 13.584650580483277, 0, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {10.448330362430179, 14.905897088401197, 3.339472303840626}), new DoubleVector(new double[] {-26.460424410978526, 47.8734279050682, 31.010530347790617}), new DoubleVector(new double[] {-0.3200135242512318, 0.37278720950405575, -0.8709885422475501}), 13.584650580483277, 0, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {16.629866436830664, 1.0764793962832293, 1.5279100152359106}), new DoubleVector(new double[] {-11.920830804748615, 26.578450322358062, 22.932804475023186}), new DoubleVector(new double[] {-0.3200135242512318, 0.37278720950405575, -0.8709885422475501}), 13.584650580483277, 0, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {17.002168811249906, -4.245263623164373, 2.850647394181591}), new DoubleVector(new double[] {-3.2417449638206435, 13.836946284128882, 18.027819220912434}), new DoubleVector(new double[] {-0.3200135242512318, 0.37278720950405575, -0.8709885422475501}), 13.584650580483277, 0, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {14.152090218317824, -4.2894743289602975, 5.660224490725611}), new DoubleVector(new double[] {0.8972540831841755, 7.549971933521476, 15.597577899423543}), new DoubleVector(new double[] {-0.3200135242512318, 0.37278720950405575, -0.8709885422475501}), 13.584650580483277, 0, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {14.67696728659142, -0.7690833967735031, 4.107992463929083}), new DoubleVector(new double[] {-6.797819916099769, 18.41256370111648, 20.207968927921357}), new DoubleVector(new double[] {-0.3200135242512318, 0.37278720950405575, -0.8709885422475501}), 13.584650580483277, 0, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {8.996123884732595, 1.555740676458246, 7.9490507222644595}), new DoubleVector(new double[] {-18.906459202370314, 2.0795238815286083, 25.15892936771783}), new DoubleVector(new double[] {-0.46768612611100907, 0.4315250435297752, -0.7713986156650319}), 13.584650580483277, 0, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {-1.8200499634211518, 8.578306703363705, 10.60108424151291}), new DoubleVector(new double[] {-17.736256410224197, 36.50454663838261, 4.135195909986432}), new DoubleVector(new double[] {0.28649292663296166, -0.05813733542251815, -0.9563168163424057}), 13.584650580483277, 0, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {-1.4604316261167734, 7.947183938410036, 10.747525128305858}), new DoubleVector(new double[] {-17.609061900573888, 36.42806492210419, 3.8328908906634815}), new DoubleVector(new double[] {0.2912707262352107, -0.06654201940232889, -0.9543235948525517}), 13.584650580483277, 0, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {-6.401910686117604, 11.380897010552435, 11.34540710919281}), new DoubleVector(new double[] {-37.65060743590817, 17.939188950328887, 22.913701499383986}), new DoubleVector(new double[] {-0.2613698864807205, 0.344860921179983, -0.9015302144043615}), 13.584650580483277, 0, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {6.232066543393776, 5.194794945959698, 13.395636197253877}), new DoubleVector(new double[] {0.6739004139269904, 6.361310030370854, 15.453274074766071}), new DoubleVector(new double[] {-0.2613698864807205, 0.344860921179983, -0.9015302144043615}), 13.584650580483277, 0, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {6.121442250572521, 5.20789139733786, 13.442656647232933}), new DoubleVector(new double[] {0.6791001895467659, 5.3446318753271695, 16.060180280303268}), new DoubleVector(new double[] {-0.3968377309984944, 0.360910226709542, -0.84395712184474}), 13.584650580483277, 0, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {6.384587559603392, 4.8951171529253426, 12.046669036438313}), new DoubleVector(new double[] {-6.365245794499894, 5.215460543247913, 18.178769839338546}), new DoubleVector(new double[] {-0.3968377309984944, 0.360910226709542, -0.84395712184474}), 13.584650580483277, 0, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {5.819998382482323, 3.6319235554032945, 13.551227693970045}), new DoubleVector(new double[] {14.241175762916912, -6.374743798749662, 18.95592262093144}), new DoubleVector(new double[] {-0.8026210204681568, -0.5471410200981186, 0.23756304769191833}), 13.584650580483277, 0, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {5.59936071707601, 3.99124563534047, 12.925976597059424}), new DoubleVector(new double[] {15.234045630944301, -7.457401345918149, 19.109498250200062}), new DoubleVector(new double[] {-0.8026210204681568, -0.5471410200981186, 0.23756304769191833}), 13.584650580483277, 0, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {6.5679605808658605, 2.060275479361558, 14.576234624001891}), new DoubleVector(new double[] {7.853350068722063, -8.255275352684922, 26.990316654465563}), new DoubleVector(new double[] {-0.9785298365992506, -0.19655582953571993, -0.062009392519189306}), 13.584650580483277, 0, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {4.691159276677769, 5.268689243036658, 13.28168883213723}), new DoubleVector(new double[] {5.568853947356998, -1.7750154674384253, 21.758320812133373}), new DoubleVector(new double[] {-0.9785298365992506, -0.19655582953571993, -0.062009392519189306}), 13.584650580483277, 0, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {7.663003344362723, 8.589327473693174, 4.581133546393751}), new DoubleVector(new double[] {10.236806839492441, -12.066046665075056, 29.43850792256331}), new DoubleVector(new double[] {-0.9785298365992506, -0.19655582953571993, -0.062009392519189306}), 13.584650580483277, 0, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {4.012857002684331, 2.8711903888066246, 11.916272053738078}), new DoubleVector(new double[] {-19.845557016468657, -9.816829730139887, 29.831293124581812}), new DoubleVector(new double[] {-0.6683911971814183, 0.2892916328287137, -0.6852470785824056}), 13.584650580483277, 0, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {4.955432829089521, 5.906446638183671, 11.91851811429196}), new DoubleVector(new double[] {-10.269133954268591, -2.1900517080358357, 23.350478113786142}), new DoubleVector(new double[] {-0.6683911971814183, 0.2892916328287137, -0.6852470785824056}), 13.584650580483277, 0, 30.0);

    currentView_2.setInput(new DoubleVector(new double[] {5.357651726687381, 6.001993685224021, 11.755247664201894}), new DoubleVector(new double[] {-15.103093824806582, 5.629509667240762, 14.799377588585998}), new DoubleVector(new double[] {-0.14130591049179098, 0.41438296731233, -0.8990658463436332}), 13.584650580483277, 0, 30.0);

    scene_2.getCreatorGroup().setQuery(null);

    scene_2.getCreatorGroup().setObjects(region_0);

    PartDisplayer partDisplayer_1 = 
      scene_2.getDisplayerManager().createPartDisplayer("Probe Surface", -1, 1);

    scene_2.setTransparencyOverrideMode(SceneTransparencyOverride.MAKE_SCENE_TRANSPARENT);

    PointPart pointPart_0 = 
      simulation_0.getPartManager().createPointPart(new ArrayList<>(Collections.<NamedObject>emptyList()), new DoubleVector(new double[] {0.0, 0.0, 0.0}), null);

    LabCoordinateSystem labCoordinateSystem_0 = 
      simulation_0.getCoordinateSystemManager().getLabCoordinateSystem();

    pointPart_0.getPointCoordinate().setCoordinateSystem(labCoordinateSystem_0);

    pointPart_0.getPointCoordinate().setUnits0(units_2);

    pointPart_0.getPointCoordinate().setUnits1(units_2);

    pointPart_0.getPointCoordinate().setUnits2(units_2);

    pointPart_0.getPointCoordinate().setDefinition("");

    pointPart_0.getPointCoordinate().setValue(new DoubleVector(new double[] {1.3011718143070952, 6.561765643234292, 14.941871996129471}));

    pointPart_0.getPointCoordinate().setCoordinate(units_2, units_2, units_2, new DoubleVector(new double[] {1.3011718143070952, 6.561765643234292, 14.941871996129471}));

    pointPart_0.setCoordinateSystem(labCoordinateSystem_0);

    pointPart_0.getInputParts().setQuery(null);

    pointPart_0.getInputParts().setObjects(region_0);

    partDisplayer_1.getVisibleParts().addParts(pointPart_0);

    partDisplayer_1.getHiddenParts().addParts();

    scene_2.setTransparencyOverrideMode(SceneTransparencyOverride.USE_DISPLAYER_PROPERTY);

    SceneUpdate sceneUpdate_2 = 
      scene_2.getSceneUpdate();

    HardcopyProperties hardcopyProperties_2 = 
      sceneUpdate_2.getHardcopyProperties();

    hardcopyProperties_2.setCurrentResolutionWidth(767);

    hardcopyProperties_2.setCurrentResolutionWidth(486);

    hardcopyProperties_2.setCurrentResolutionHeight(664);

    SumReport sumReport_0 = 
      simulation_0.getReportManager().create("star.base.report.SumReport");

    PrimitiveFieldFunction primitiveFieldFunction_0 = 
      ((PrimitiveFieldFunction) simulation_0.getFieldFunctionManager().getFunction("Velocity"));

    VectorMagnitudeFieldFunction vectorMagnitudeFieldFunction_0 = 
      ((VectorMagnitudeFieldFunction) primitiveFieldFunction_0.getMagnitudeFunction());

    sumReport_0.setFieldFunction(vectorMagnitudeFieldFunction_0);

    sumReport_0.getParts().setQuery(null);

    sumReport_0.getParts().setObjects(pointPart_0);

    sumReport_0.setSmooth(true);

    sumReport_0.setSmooth(false);

    sumReport_0.setSmooth(true);

    sumReport_0.setPresentationName("velocity_inlet");

    simulation_0.getMonitorManager().createMonitorAndPlot(new ArrayList<>(Arrays.<Report>asList(sumReport_0)), true, "%1$s Plot");

    ReportMonitor reportMonitor_0 = 
      ((ReportMonitor) simulation_0.getMonitorManager().getMonitor("velocity_inlet Monitor"));

    MonitorPlot monitorPlot_0 = 
      simulation_0.getPlotManager().createMonitorPlot(new ArrayList<>(Arrays.<Monitor>asList(reportMonitor_0)), "velocity_inlet Monitor Plot");

    monitorPlot_0.openInteractive();

    PlotUpdate plotUpdate_0 = 
      monitorPlot_0.getPlotUpdate();

    HardcopyProperties hardcopyProperties_4 = 
      plotUpdate_0.getHardcopyProperties();

    hardcopyProperties_4.setCurrentResolutionWidth(25);

    hardcopyProperties_4.setCurrentResolutionHeight(25);

    hardcopyProperties_2.setCurrentResolutionWidth(488);

    hardcopyProperties_2.setCurrentResolutionHeight(665);

    Scene scene_3 = 
      simulation_0.getSceneManager().getScene("Scalar Scene 1");

    SceneUpdate sceneUpdate_3 = 
      scene_3.getSceneUpdate();

    HardcopyProperties hardcopyProperties_3 = 
      sceneUpdate_3.getHardcopyProperties();

    hardcopyProperties_3.setCurrentResolutionWidth(488);

    hardcopyProperties_3.setCurrentResolutionHeight(665);

    hardcopyProperties_4.setCurrentResolutionWidth(486);

    hardcopyProperties_4.setCurrentResolutionHeight(664);

    Cartesian2DAxisManager cartesian2DAxisManager_0 = 
      ((Cartesian2DAxisManager) monitorPlot_0.getAxisManager());

    cartesian2DAxisManager_0.setAxesBounds(new Vector(Arrays.<AxisManager.AxisBounds>asList(new AxisManager.AxisBounds("Left Axis", 0.0, false, 1.0, false), new AxisManager.AxisBounds("Bottom Axis", 0.0, false, 1.0, false))));

    monitorPlot_0.setTitleFont(new java.awt.Font("Siemens Sans Global", 0, 14));

    MultiColLegend multiColLegend_0 = 
      monitorPlot_0.getLegend();

    multiColLegend_0.setFont(new java.awt.Font("Siemens Sans Global", 0, 12));

    Cartesian2DAxis cartesian2DAxis_0 = 
      ((Cartesian2DAxis) cartesian2DAxisManager_0.getAxis("Bottom Axis"));

    AxisTitle axisTitle_0 = 
      cartesian2DAxis_0.getTitle();

    axisTitle_0.setFont(new java.awt.Font("Siemens Sans Global", 0, 12));

    AxisLabels axisLabels_0 = 
      cartesian2DAxis_0.getLabels();

    axisLabels_0.setFont(new java.awt.Font("Siemens Sans Global", 0, 12));

    axisLabels_0.setGridColor(new DoubleVector(new double[] {0.7843137383460999, 0.7843137383460999, 0.7843137383460999}));

    axisLabels_0.getGridLinePatternOption().setSelected(LinePatternOption.Type.SOLID);

    axisLabels_0.setGridWidth(1);

    AxisTicks axisTicks_0 = 
      cartesian2DAxis_0.getTicks();

    axisTicks_0.setCount(3);

    axisTicks_0.setGridColor(new DoubleVector(new double[] {0.8627451062202454, 0.8627451062202454, 0.8627451062202454}));

    axisTicks_0.getGridLinePatternOption().setSelected(LinePatternOption.Type.SOLID);

    axisTicks_0.setGridWidth(1);

    Cartesian2DAxis cartesian2DAxis_1 = 
      ((Cartesian2DAxis) cartesian2DAxisManager_0.getAxis("Left Axis"));

    AxisTitle axisTitle_1 = 
      cartesian2DAxis_1.getTitle();

    axisTitle_1.setFont(new java.awt.Font("Siemens Sans Global", 0, 12));

    axisTitle_1.getRotationOption().setSelected(RotationOption.Type.NINETY_DEGREES);

    AxisLabels axisLabels_1 = 
      cartesian2DAxis_1.getLabels();

    axisLabels_1.setFont(new java.awt.Font("Siemens Sans Global", 0, 12));

    axisLabels_1.setGridColor(new DoubleVector(new double[] {0.7843137383460999, 0.7843137383460999, 0.7843137383460999}));

    axisLabels_1.getGridLinePatternOption().setSelected(LinePatternOption.Type.SOLID);

    axisLabels_1.setGridWidth(1);

    AxisTicks axisTicks_1 = 
      cartesian2DAxis_1.getTicks();

    axisTicks_1.setCount(3);

    axisTicks_1.setGridColor(new DoubleVector(new double[] {0.8627451062202454, 0.8627451062202454, 0.8627451062202454}));

    axisTicks_1.getGridLinePatternOption().setSelected(LinePatternOption.Type.SOLID);

    axisTicks_1.setGridWidth(1);

    MonitorDataSet monitorDataSet_0 = 
      ((MonitorDataSet) monitorPlot_0.getDataSetManager().getDataSet("velocity_inlet Monitor"));

    LineStyle lineStyle_0 = 
      monitorDataSet_0.getLineStyle();

    lineStyle_0.setLineWidth(3.0);

    LineStyle lineStyle_1 = 
      monitorDataSet_0.getCyclicDrawDataManager().getLineStyle();

    lineStyle_1.setLineWidth(3.0);

    LineStyle lineStyle_2 = 
      monitorDataSet_0.getMultiValuedMonitorDrawDataManager().getLineStyle();

    lineStyle_2.setLineWidth(3.0);

    SymbolStyleWithSpacing symbolStyleWithSpacing_0 = 
      monitorDataSet_0.getSymbolStyle();

    symbolStyleWithSpacing_0.setSize(12);

    SymbolStyleWithSpacing symbolStyleWithSpacing_1 = 
      monitorDataSet_0.getCyclicDrawDataManager().getSymbolStyle();

    symbolStyleWithSpacing_1.setSize(12);

    SymbolStyleWithSpacing symbolStyleWithSpacing_2 = 
      monitorDataSet_0.getMultiValuedMonitorDrawDataManager().getSymbolStyle();

    symbolStyleWithSpacing_2.setSize(12);
  }
}
