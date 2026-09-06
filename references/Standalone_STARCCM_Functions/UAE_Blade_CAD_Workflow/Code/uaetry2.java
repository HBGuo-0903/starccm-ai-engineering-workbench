// STAR-CCM+ macro: uaetry2.java
// Written by STAR-CCM+ 15.02.009
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;
import star.cadmodeler.*;

public class uaetry2 extends StarMacro {

  public void execute() {
    execute0();
    execute1();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    Scene scene_0 = 
      simulation_0.getSceneManager().getScene("3D-CAD View 1");

    CurrentView currentView_0 = 
      scene_0.getCurrentView();

    currentView_0.setInput(new DoubleVector(new double[] {122.12908820847332, 167.74471748767314, -62.01033580460131}), new DoubleVector(new double[] {122.12908820847332, 167.74471748767314, 239.12770362971344}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 601.5130612291392, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {66.4818513946378, 70.38486514197618, -14.769750278778758}), new DoubleVector(new double[] {66.4818513946378, 70.38486514197618, 239.12770362971344}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 235.11474697213, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-54.43651435956806, -9.497154526049396, -6.582334663091814}), new DoubleVector(new double[] {-54.43651435956806, -9.497154526049396, 239.12770362971344}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 35.92676737375293, 1, 30.0);

    CadModel cadModel_0 = 
      ((CadModel) simulation_0.get(SolidModelManager.class).getObject("3D-CAD Model 1"));

    CanonicalSketchPlane canonicalSketchPlane_0 = 
      ((CanonicalSketchPlane) cadModel_0.getFeature("XY"));

    Sketch sketch_3 = 
      cadModel_0.getFeatureManager().createSketch(canonicalSketchPlane_0);

    cadModel_0.allowMakingPartDirty(false);

    cadModel_0.getFeatureManager().startSketchEdit(sketch_3);

    Units units_0 = 
      simulation_0.getUnitsManager().getPreferredUnits(new IntVector(new int[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

    currentView_0.setInput(new DoubleVector(new double[] {23.77261559901941, 11.1585299099658, -6.582335406939592}), new DoubleVector(new double[] {23.77261559901941, 11.1585299099658, 239.12770362971344}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 35.92676737375293, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {40.63613820095005, 23.058193036328145, -11.354614969584816}), new DoubleVector(new double[] {40.63613820095005, 23.058193036328145, 239.12770362971344}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 63.33493468273511, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {27.448837920395608, 13.784197310572381, -16.311417116799674}), new DoubleVector(new double[] {27.448837920395608, 13.784197310572381, 239.12770362971344}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 41.81604483833798, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {459.5882161498351, 317.6870933824954, -329.40879410550053}), new DoubleVector(new double[] {459.5882161498351, 317.6870933824954, 239.12770362971344}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 524.5587143201565, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {1163.3096789565157, 844.0707475618926, -605.7175798852528}), new DoubleVector(new double[] {1163.3096789565157, 844.0707475618926, 911.5191265133159}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 1393.7174809263545, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {1872.5384123117467, 1464.1506177858796, -1227.852697852072}), new DoubleVector(new double[] {1872.5384123117467, 1464.1506177858796, 1856.1066629116656}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 1956.8253274483268, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {5034.834434714546, 4228.951330110115, -2330.3064853905207}), new DoubleVector(new double[] {5034.834434714546, 4228.951330110115, 4276.907969101357}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 4331.16371996659, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {14970.164096831752, 13362.485300696371, -8355.688850607346}), new DoubleVector(new double[] {14970.164096831752, 13362.485300696371, 11767.451401375156}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 10162.604728528844, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {6530.457958643183, 1726.677429702656, 8656.723724550911}), new DoubleVector(new double[] {6530.457958643183, 1726.677429702656, 20674.378565955292}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 10162.604728528844, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {35381.11554546452, 19059.436832637963, -17754.762877041125}), new DoubleVector(new double[] {35381.11554546452, 19059.436832637963, 23481.22024504478}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 43969.07491433481, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {101206.8233929413, 72759.35639242164, -49230.39134972736}), new DoubleVector(new double[] {101206.8233929413, 72759.35639242164, 78874.05414431452}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 92828.47092197079, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {231133.3748477848, 186527.52962064758, -117010.73600878517}), new DoubleVector(new double[] {231133.3748477848, 186527.52962064758, 202868.3616769144}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 183229.7670332427, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {60959.291460645385, 10050.70240435483, 100585.74014236446}), new DoubleVector(new double[] {60959.291460645385, 10050.70240435483, 307956.570367258}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 183229.7670332427, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {131023.19698013455, 53571.23069265912, -32806.94724503701}), new DoubleVector(new double[] {131023.19698013455, 53571.23069265912, 307956.570367258}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 418676.92192727217, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {177434.1713319541, 91835.56656216254, -78714.86494214373}), new DoubleVector(new double[] {177434.1713319541, 91835.56656216254, 307956.570367258}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 536175.8812820677, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {281433.1404685258, 204371.7645084528, -173324.04425303667}), new DoubleVector(new double[] {281433.1404685258, 204371.7645084528, 307956.570367258}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 768749.7239706639, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {430779.3460011501, 381171.3166463389, -268188.1114489295}), new DoubleVector(new double[] {430779.3460011501, 381171.3166463389, 436826.37381127523}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 1077458.9236561353, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {662725.0659623693, 702447.4828088385, -515424.17022538173}), new DoubleVector(new double[] {662725.0659623693, 702447.4828088385, 641210.460825456}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 1474794.491777139, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {1539608.2475712234, 1991038.0118803864, -1408872.0330963014}), new DoubleVector(new double[] {1539608.2475712234, 1991038.0118803864, 1165033.028825072}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 2708334.6948669124, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {860860.9776045769, 433911.9219569032, 1448282.178913144}), new DoubleVector(new double[] {860860.9776045769, 433911.9219569032, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 2708334.6948669124, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {46907.95089432159, -428562.99664137897, 299.4791537662968}), new DoubleVector(new double[] {46907.95089432159, -428562.99664137897, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 5258.216816232994, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {46708.99133911278, -428507.4429993401, 128.90614575939253}), new DoubleVector(new double[] {46708.99133911278, -428507.4429993401, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 4732.395308196898, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {47081.07155990467, -420740.26839030936, 116.78250256739557}), new DoubleVector(new double[] {47081.07155990467, -420740.26839030936, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 4732.395308196898, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {47685.70191869149, -414810.23987143853, 116.78250256739557}), new DoubleVector(new double[] {47685.70191869149, -414810.23987143853, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 4732.395308196898, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {48708.922525869195, -408159.3059247834, 116.78250256739557}), new DoubleVector(new double[] {48708.922525869195, -408159.3059247834, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 4732.395308196898, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {48476.37238787426, -401182.8017849354, 116.78250256739557}), new DoubleVector(new double[] {48476.37238787426, -401182.8017849354, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 4732.395308196898, 1, 30.0);

    scene_0.setViewOrientation(new DoubleVector(new double[] {0.0, 0.0, 1.0}), new DoubleVector(new double[] {0.0, 1.0, 0.0}));

    currentView_0.setInput(new DoubleVector(new double[] {40566.82615324949, -306296.2327487639, -1486.3624077653512}), new DoubleVector(new double[] {40566.82615324949, -306296.2327487639, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 610941.7508784356, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-45062.74735448083, 168457.0801004751, -57254.0232880814}), new DoubleVector(new double[] {-45062.74735448083, 168457.0801004751, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 4936240.521931111, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {100840.07209458266, -206509.86978994007, -140364.12245853944}), new DoubleVector(new double[] {100840.07209458266, -206509.86978994007, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 2160774.842221502, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {130116.17045809329, -130633.28448518076, -32208.415619780775}), new DoubleVector(new double[] {130116.17045809329, -130633.28448518076, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 837665.5139251793, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {47243.54125674774, -20849.489260225746, -15263.825172307435}), new DoubleVector(new double[] {47243.54125674774, -20849.489260225746, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 325147.290027244, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {36253.9707497051, -18376.835896141154, -7296.656756736804}), new DoubleVector(new double[] {36253.9707497051, -18376.835896141154, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 213335.0006863808, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {27621.684768072813, -28807.51479061351, -3104.503152281046}), new DoubleVector(new double[] {27621.684768072813, -28807.51479061351, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 66947.84322414723, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {13935.297131608779, -16868.236503422093, -1124.261802487541}), new DoubleVector(new double[] {13935.297131608779, -16868.236503422093, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 28819.92467394682, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {10557.905964287349, -13625.173393891855, -674.3403451684862}), new DoubleVector(new double[] {10557.905964287349, -13625.173393891855, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 21009.8579645353, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {4363.353247471046, 7746.033479124391, -516.7213942096569}), new DoubleVector(new double[] {4363.353247471046, 7746.033479124391, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 21009.8579645353, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {2960.4692115330863, 2306.0490847872725, -385.72523252665997}), new DoubleVector(new double[] {2960.4692115330863, 2306.0490847872725, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 11165.552436618707, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {2246.7516500619754, 1480.8131543363013, -249.39624387584627}), new DoubleVector(new double[] {2246.7516500619754, 1480.8131543363013, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 8139.690522389683, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {1329.4840355500598, 653.6343233925202, -181.23267726833}), new DoubleVector(new double[] {1329.4840355500598, 653.6343233925202, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 4806.407144742835, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {1197.219268913402, 534.3598463362483, -119.19352398812771}), new DoubleVector(new double[] {1197.219268913402, 534.3598463362483, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 4325.766548198369, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {709.7455408813375, 94.76300230733307, -78.19561285339296}), new DoubleVector(new double[] {709.7455408813375, 94.76300230733307, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 2554.3220824076393, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {421.8971642511408, -41.45096163374221, -78.35529924090952}), new DoubleVector(new double[] {421.8971642511408, -41.45096163374221, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 1508.3016733142076, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {343.03559266470364, -78.76938390232411, -33.89287442341447}), new DoubleVector(new double[] {343.03559266470364, -78.76938390232411, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 1221.7243593920857, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {101.78888150490644, -132.61909621477886, -27.53976449277252}), new DoubleVector(new double[] {101.78888150490644, -132.61909621477886, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 345.05104623512494, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {34.274738589397906, -25.115807110853698, -8.147247618064284}), new DoubleVector(new double[] {34.274738589397906, -25.115807110853698, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 133.67984528309708, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {15.876958194132792, -6.931954394603302, -5.049454805441201}), new DoubleVector(new double[] {15.876958194132792, -6.931954394603302, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 46.61127992904124, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {6.495851847322166, 1.7984734426430087, -5.049005291890353}), new DoubleVector(new double[] {6.495851847322166, 1.7984734426430087, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 4.590143218806699, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {5.7983103609024065, 2.194803832654235, -5.049005021341145}), new DoubleVector(new double[] {5.7983103609024065, 2.194803832654235, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 2.4393903023460815, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {1.1472860989354317, 1.3796758692167232, -5.049004830420017}), new DoubleVector(new double[] {1.1472860989354317, 1.3796758692167232, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 2.4393903023460815, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {1.1472860989354317, 1.3796758692167232, -5.049004830420017}), new DoubleVector(new double[] {1.1472860989354317, 1.3796758692167232, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 2.4393903023460815, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-1.9169969487583662, 2.1238326745581544, -5.049008467700332}), new DoubleVector(new double[] {-1.9169969487583662, 2.1238326745581544, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 6.959865224342391, 1, 30.0);

    sketch_3.createRectangle(new DoubleVector(new double[] {0.0, 0.0}), new DoubleVector(new double[] {10.700000000000001, 4.57}));

    currentView_0.setInput(new DoubleVector(new double[] {-1.9169969487583662, 2.1238326745581544, -5.049008467700332}), new DoubleVector(new double[] {-1.9169969487583662, 2.1238326745581544, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 6.959865224342391, 1, 30.0);

    PointSketchPrimitive pointSketchPrimitive_1 = 
      ((PointSketchPrimitive) sketch_3.getSketchPrimitive("Point 1"));

    sketch_3.getProjectedSketchPrimitives(new NeoObjectVector(new Object[] {}), new NeoObjectVector(new Object[] {pointSketchPrimitive_1}), new NeoObjectVector(new Object[] {}), new NeoObjectVector(new Object[] {}), new NeoObjectVector(new Object[] {}));

    currentView_0.setInput(new DoubleVector(new double[] {-1.9169969487583662, 2.1238326745581544, -5.049008467700332}), new DoubleVector(new double[] {-1.9169969487583662, 2.1238326745581544, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 6.959865224342391, 1, 30.0);

    FixationConstraint fixationConstraint_1 = 
      sketch_3.createFixationConstraint(pointSketchPrimitive_1);

    PointSketchPrimitive pointSketchPrimitive_2 = 
      ((PointSketchPrimitive) sketch_3.getSketchPrimitive("Point 4"));

    sketch_3.translateSketchPrimitive(pointSketchPrimitive_2, new DoubleVector(new double[] {189.3, 0.0}));

    PointSketchPrimitive pointSketchPrimitive_3 = 
      ((PointSketchPrimitive) sketch_3.getSketchPrimitive("Point 3"));

    sketch_3.translateSketchPrimitive(pointSketchPrimitive_3, new DoubleVector(new double[] {0.0, 95.43}));

    currentView_0.setInput(new DoubleVector(new double[] {-8.962048940104756, 5.52055417038588, -5.049023523926735}), new DoubleVector(new double[] {-8.962048940104756, 5.52055417038588, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 24.027342369979724, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-20.597871924413333, 4.431411565800269, -5.0491164163686335}), new DoubleVector(new double[] {-20.597871924413333, 4.431411565800269, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 42.56590267573087, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {51.98379013199262, 39.990150901791665, -5.049004830420017}), new DoubleVector(new double[] {51.98379013199262, 39.990150901791665, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 42.56590267573087, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {213.0268681951495, 288.095680319932, -5.068461362738162}), new DoubleVector(new double[] {213.0268681951495, 288.095680319932, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 507.3079412627097, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {195.07788943794307, 259.8011096401691, -5.089040995575488}), new DoubleVector(new double[] {195.07788943794307, 259.8011096401691, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 456.57714757705025, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {123.67101840693381, 169.3646191193291, 10.52412097202614}), new DoubleVector(new double[] {123.67101840693381, 169.3646191193291, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 299.5602673901064, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {67.0063748722964, 99.73944541453527, 7.865131027530879}), new DoubleVector(new double[] {67.0063748722964, 99.73944541453527, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 176.88734254163887, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {11.704959317758004, 89.21447468652269, 5.046823534183204}), new DoubleVector(new double[] {11.704959317758004, 89.21447468652269, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 116.05578552168568, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {11.704959317758004, 89.21447468652269, 5.046823534183204}), new DoubleVector(new double[] {11.704959317758004, 89.21447468652269, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 116.05578552168568, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {11.704959317758004, 89.21447468652269, 5.046823534183204}), new DoubleVector(new double[] {11.704959317758004, 89.21447468652269, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 116.05578552168568, 1, 30.0);

    LineSketchPrimitive lineSketchPrimitive_0 = 
      sketch_3.createLine(pointSketchPrimitive_1, new DoubleVector(new double[] {-132.5800060340925, 0.0}));

    sketch_3.createHorizontalConstraint(lineSketchPrimitive_0);

    currentView_0.setInput(new DoubleVector(new double[] {11.704959317758004, 89.21447468652269, 5.046823534183204}), new DoubleVector(new double[] {11.704959317758004, 89.21447468652269, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 116.05578552168568, 1, 30.0);

    sketch_3.getProjectedSketchPrimitives(new NeoObjectVector(new Object[] {}), new NeoObjectVector(new Object[] {lineSketchPrimitive_0}), new NeoObjectVector(new Object[] {}), new NeoObjectVector(new Object[] {}), new NeoObjectVector(new Object[] {}));

    currentView_0.setInput(new DoubleVector(new double[] {11.704959317758004, 89.21447468652269, 5.046823534183204}), new DoubleVector(new double[] {11.704959317758004, 89.21447468652269, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 116.05578552168568, 1, 30.0);

    PointSketchPrimitive pointSketchPrimitive_4 = 
      ((PointSketchPrimitive) sketch_3.getSketchPrimitive("Point 5"));

    sketch_3.translateSketchPrimitive(pointSketchPrimitive_4, new DoubleVector(new double[] {32.58000603409249, 0.0}));

    Units units_1 = 
      simulation_0.getUnitsManager().getPreferredUnits(new IntVector(new int[] {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

    currentView_0.setInput(new DoubleVector(new double[] {11.704959317758004, 89.21447468652269, 5.046823534183204}), new DoubleVector(new double[] {11.704959317758004, 89.21447468652269, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 116.05578552168568, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {11.704959317758004, 89.21447468652269, 5.046823534183204}), new DoubleVector(new double[] {11.704959317758004, 89.21447468652269, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 116.05578552168568, 1, 30.0);

    CircularArcSketchPrimitive circularArcSketchPrimitive_0 = 
      sketch_3.createCircularArc(pointSketchPrimitive_1, pointSketchPrimitive_3, pointSketchPrimitive_4);

    currentView_0.setInput(new DoubleVector(new double[] {11.704959317758004, 89.21447468652269, 5.046823534183204}), new DoubleVector(new double[] {11.704959317758004, 89.21447468652269, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 116.05578552168568, 1, 30.0);

    LineSketchPrimitive lineSketchPrimitive_1 = 
      ((LineSketchPrimitive) sketch_3.getSketchPrimitive("Line 2"));

    sketch_3.getProjectedSketchPrimitives(new NeoObjectVector(new Object[] {}), new NeoObjectVector(new Object[] {circularArcSketchPrimitive_0, lineSketchPrimitive_1}), new NeoObjectVector(new Object[] {}), new NeoObjectVector(new Object[] {}), new NeoObjectVector(new Object[] {}));

    currentView_0.setInput(new DoubleVector(new double[] {11.704959317758004, 89.21447468652269, 5.046823534183204}), new DoubleVector(new double[] {11.704959317758004, 89.21447468652269, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 116.05578552168568, 1, 30.0);

    TangentConstraint tangentConstraint_0 = 
      sketch_3.createTangentConstraint(circularArcSketchPrimitive_0, lineSketchPrimitive_1);

    LineSketchPrimitive lineSketchPrimitive_2 = 
      ((LineSketchPrimitive) sketch_3.getSketchPrimitive("Line 1"));

    sketch_3.getProjectedSketchPrimitives(new NeoObjectVector(new Object[] {}), new NeoObjectVector(new Object[] {lineSketchPrimitive_2}), new NeoObjectVector(new Object[] {}), new NeoObjectVector(new Object[] {}), new NeoObjectVector(new Object[] {}));

    currentView_0.setInput(new DoubleVector(new double[] {11.704959317758004, 89.21447468652269, 5.046823534183204}), new DoubleVector(new double[] {11.704959317758004, 89.21447468652269, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 116.05578552168568, 1, 30.0);

    sketch_3.deleteSketchPrimitives(new NeoObjectVector(new Object[] {lineSketchPrimitive_2}));

    currentView_0.setInput(new DoubleVector(new double[] {11.704959317758004, 89.21447468652269, 5.046823534183204}), new DoubleVector(new double[] {11.704959317758004, 89.21447468652269, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 116.05578552168568, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {5.146524263706969, 91.52418442294936, 5.046084024943411}), new DoubleVector(new double[] {5.146524263706969, 91.52418442294936, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 104.45020697385772, 1, 30.0);

    sketch_3.markFeatureForEdit();

    cadModel_0.allowMakingPartDirty(true);

    cadModel_0.getFeatureManager().stopSketchEdit(sketch_3, true);

    sketch_3.setIsUptoDate(true);

    cadModel_0.getFeatureManager().rollForwardToEnd(false);

    RevolveMerge revolveMerge_0 = 
      (RevolveMerge) cadModel_0.getFeatureManager().createRevolveMerge(sketch_3);

    cadModel_0.allowMakingPartDirty(false);

    scene_0.setTransparencyOverrideMode(SceneTransparencyOverride.MAKE_SCENE_TRANSPARENT);

    revolveMerge_0.setCoordinateSystemSourceOption(0);

    LabCoordinateSystem labCoordinateSystem_0 = 
      simulation_0.getCoordinateSystemManager().getLabCoordinateSystem();

    revolveMerge_0.setImportedCoordinateSystem(labCoordinateSystem_0);

    revolveMerge_0.getSpecifiedAxisDirection().setCoordinateSystem(labCoordinateSystem_0);

    revolveMerge_0.getSpecifiedAxisPosition().setCoordinateSystem(labCoordinateSystem_0);

    currentView_0.setInput(new DoubleVector(new double[] {-48.746702921821566, -5.996893341340375, 5.046856265515089}), new DoubleVector(new double[] {-48.746702921821566, -5.996893341340375, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 104.45020697385772, 1, 30.0);

    revolveMerge_0.setColorVector(new IntVector(new int[] {-65536, -65536, -16776961, -16711936}));

    cadModel_0.allowMakingPartDirty(true);

    revolveMerge_0.getAngle().setValue(360.0);

    revolveMerge_0.setAngleOption(0);

    revolveMerge_0.setRevolvedBodyTypeOption(0);

    revolveMerge_0.setDirectionOption(0);

    revolveMerge_0.setRotationAxisOption(0);

    revolveMerge_0.setCoordinateSystemSourceOption(1);

    CanonicalReferenceCoordinateSystem canonicalReferenceCoordinateSystem_0 = 
      ((CanonicalReferenceCoordinateSystem) cadModel_0.getFeature("Lab Coordinate System"));

    revolveMerge_0.setReferenceCoordinateSystem(canonicalReferenceCoordinateSystem_0);

    revolveMerge_0.getSpecifiedAxisDirection().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {1.0, 0.0, 0.0}));

    revolveMerge_0.getSpecifiedAxisPosition().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {0.0, 0.0, 0.0}));

    revolveMerge_0.setSketch(sketch_3);

    revolveMerge_0.setPostOption(0);

    revolveMerge_0.setInteractingBodies(new NeoObjectVector(new Object[] {}));

    revolveMerge_0.setInteractingBodiesBodyGroups(new NeoObjectVector(new Object[] {}));

    revolveMerge_0.setInteractingBodiesCadFilters(new NeoObjectVector(new Object[] {}));

    revolveMerge_0.setInteractingSelectedBodies(false);

    revolveMerge_0.markFeatureForEdit();

    cadModel_0.getFeatureManager().execute(revolveMerge_0);

    scene_0.setTransparencyOverrideMode(SceneTransparencyOverride.USE_DISPLAYER_PROPERTY);

    LoftMerge loftMerge_2 = 
      ((LoftMerge) cadModel_0.getFeature("Loft 1"));

    star.cadmodeler.Body cadmodelerBody_2 = 
      ((star.cadmodeler.Body) loftMerge_2.getBodyByLocation(new DoubleVector(new double[] {-0.098639865319869, 0.04628044824200296, -0.508000000000002})));

    cadmodelerBody_2.setPresentationName("Blade");

    star.cadmodeler.Body cadmodelerBody_3 = 
      ((star.cadmodeler.Body) revolveMerge_0.getBody(lineSketchPrimitive_1));

    cadmodelerBody_3.setPresentationName("Domain");

    cadModel_0.getBodyManager().hideBodies(new NeoObjectVector(new Object[] {cadmodelerBody_3}));

    currentView_0.setInput(new DoubleVector(new double[] {-32.33339226526127, -4.536386884612551, 5.047032050788403}), new DoubleVector(new double[] {-32.33339226526127, -4.536386884612551, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 76.14420088990971, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-7.516010901875171, -2.372080370363763, 1.1686624600552022}), new DoubleVector(new double[] {-7.516010901875171, -2.372080370363763, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 17.419350804852378, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-3.496867715930364, -1.3672945738775604, 1.1686717146076262}), new DoubleVector(new double[] {-3.496867715930364, -1.3672945738775604, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 8.33162148999173, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-2.804544770496653, -1.1144800151517669, 0.7771966140717268}), new DoubleVector(new double[] {-2.804544770496653, -1.1144800151517669, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 6.748613406894881, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.8821808903522168, -0.4124932049866632, 0.6653482830151916}), new DoubleVector(new double[] {-0.8821808903522168, -0.4124932049866632, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 2.353095995554977, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.9203392037936489, -0.3772256728665518, 0.6653485093265772}), new DoubleVector(new double[] {-0.9203392037936489, -0.3772256728665518, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 2.117786395999484, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {1.0673620327464068, 0.42409890940352324, 0.6653485596179962}), new DoubleVector(new double[] {1.0673620327464068, 0.42409890940352324, 2679859.56734949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 2.117786395999484, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.5496876000039606, 0.37817359352168894, 2.270214211112412}), new DoubleVector(new double[] {-1564766.8109789866, -138817.44008236512, 2171148.616032666}), new DoubleVector(new double[] {-0.016709033132993174, 0.9985176636585144, 0.05180042059390128}), 2.117786395999484, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.9991006046184339, 0.312949871426099, 2.8412155143450946}), new DoubleVector(new double[] {-1564766.2426981595, -138817.49476076773, 2171149.022102462}), new DoubleVector(new double[] {-0.016709033132993174, 0.9985176636585144, 0.05180042059390128}), 2.8187736930750473, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.2365714114621369, 0.8064308897862702, 2.5565931512282987}), new DoubleVector(new double[] {-1564767.2066513645, -138816.97717430504, 2171148.360464755}), new DoubleVector(new double[] {-0.016709033132993174, 0.9985176636585144, 0.05180042059390128}), 2.8187736930750473, 1, 30.0);

    scene_0.setViewOrientation(new DoubleVector(new double[] {-1.0, 0.0, 0.0}), new DoubleVector(new double[] {0.0, 1.0, 0.0}));

    currentView_0.setInput(new DoubleVector(new double[] {-0.1085090241394937, 0.6402132764354076, 0.5758332587971846}), new DoubleVector(new double[] {-2679858.4696441106, 0.6402132764354076, 0.5758332587971846}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 2.8187736930750473, 1, 30.0);

    CanonicalSketchPlane canonicalSketchPlane_1 = 
      ((CanonicalSketchPlane) cadModel_0.getFeature("YZ"));

    Sketch sketch_4 = 
      cadModel_0.getFeatureManager().createSketch(canonicalSketchPlane_1);

    cadModel_0.allowMakingPartDirty(false);

    cadModel_0.getFeatureManager().startSketchEdit(sketch_4);

    currentView_0.setInput(new DoubleVector(new double[] {-0.1085090241394937, 0.6402132764354076, 0.5758332587971846}), new DoubleVector(new double[] {-2679858.4696441106, 0.6402132764354076, 0.5758332587971846}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 2.8187736930750473, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.10973242344334722, -0.46141550277456905, 1.0360453531486884}), new DoubleVector(new double[] {-2679858.4696441106, -0.46141550277456905, 1.0360453531486884}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 0.8845610528667995, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.10973024601116776, 0.6542036065761234, -1.1790828423129742}), new DoubleVector(new double[] {-2679858.4696441106, 0.6542036065761234, -1.1790828423129742}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 2.523755917507633, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.11053580138832331, 2.424949181377797, -6.332481923795586}), new DoubleVector(new double[] {-2679858.4696441106, 2.424949181377797, -6.332481923795586}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 4.918086312660878, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.11053371848538518, 3.203988553803141, -8.672137628603949}), new DoubleVector(new double[] {-2679858.4696441106, 3.203988553803141, -8.672137628603949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 5.950884438309228, 1, 30.0);

    CircleSketchPrimitive circleSketchPrimitive_4 = 
      sketch_4.createCircle(new DoubleVector(new double[] {0.02, -0.01}), 4.924428900898052);

    currentView_0.setInput(new DoubleVector(new double[] {-0.11053371848538518, 3.203988553803141, -8.672137628603949}), new DoubleVector(new double[] {-2679858.4696441106, 3.203988553803141, -8.672137628603949}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 5.950884438309228, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.11048267316073179, 2.326708292627335, -2.0632929944128633}), new DoubleVector(new double[] {-2679858.4696441106, 2.326708292627335, -2.0632929944128633}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 5.950884438309228, 1, 30.0);

    PointSketchPrimitive pointSketchPrimitive_5 = 
      ((PointSketchPrimitive) sketch_4.getSketchPrimitive("Point 1"));

    sketch_4.translateSketchPrimitive(pointSketchPrimitive_5, new DoubleVector(new double[] {0.0, 0.01}));

    sketch_4.translateSketchPrimitive(pointSketchPrimitive_5, new DoubleVector(new double[] {-0.02, 0.0}));

    sketch_4.setCircleRadius_2(circleSketchPrimitive_4, 5.3);

    sketch_4.getProjectedSketchPrimitives(new NeoObjectVector(new Object[] {}), new NeoObjectVector(new Object[] {pointSketchPrimitive_5}), new NeoObjectVector(new Object[] {}), new NeoObjectVector(new Object[] {}), new NeoObjectVector(new Object[] {}));

    currentView_0.setInput(new DoubleVector(new double[] {-0.11048267316073179, 2.326708292627335, -2.0632929944128633}), new DoubleVector(new double[] {-2679858.4696441106, 2.326708292627335, -2.0632929944128633}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 5.950884438309228, 1, 30.0);

    FixationConstraint fixationConstraint_2 = 
      sketch_4.createFixationConstraint(pointSketchPrimitive_5);

    sketch_4.markFeatureForEdit();

    cadModel_0.allowMakingPartDirty(true);

    cadModel_0.getFeatureManager().stopSketchEdit(sketch_4, true);

    sketch_4.setIsUptoDate(true);

    cadModel_0.getFeatureManager().rollForwardToEnd(false);

    Sketch sketch_5 = 
      cadModel_0.getFeatureManager().createSketch(canonicalSketchPlane_1);

    cadModel_0.allowMakingPartDirty(false);

    cadModel_0.getFeatureManager().startSketchEdit(sketch_5);

    currentView_0.setInput(new DoubleVector(new double[] {-0.11048267316073179, 2.326708292627335, -2.0632929944128633}), new DoubleVector(new double[] {-2679858.4696441106, 2.326708292627335, -2.0632929944128633}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 5.950884438309228, 1, 30.0);

    CircleSketchPrimitive circleSketchPrimitive_5 = 
      sketch_5.createCircle(new DoubleVector(new double[] {0.0, 0.01}), 8.4988587469142);

    currentView_0.setInput(new DoubleVector(new double[] {-0.11048267316073179, 2.326708292627335, -2.0632929944128633}), new DoubleVector(new double[] {-2679858.4696441106, 2.326708292627335, -2.0632929944128633}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 5.950884438309228, 1, 30.0);

    PointSketchPrimitive pointSketchPrimitive_6 = 
      ((PointSketchPrimitive) sketch_5.getSketchPrimitive("Point 1"));

    sketch_5.translateSketchPrimitive(pointSketchPrimitive_6, new DoubleVector(new double[] {0.0, -0.01}));

    sketch_5.setCircleRadius_2(circleSketchPrimitive_5, 6.3);

    sketch_5.getProjectedSketchPrimitives(new NeoObjectVector(new Object[] {}), new NeoObjectVector(new Object[] {pointSketchPrimitive_6}), new NeoObjectVector(new Object[] {}), new NeoObjectVector(new Object[] {}), new NeoObjectVector(new Object[] {}));

    currentView_0.setInput(new DoubleVector(new double[] {-0.11048267316073179, 2.326708292627335, -2.0632929944128633}), new DoubleVector(new double[] {-2679858.4696441106, 2.326708292627335, -2.0632929944128633}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 5.950884438309228, 1, 30.0);

    FixationConstraint fixationConstraint_3 = 
      sketch_5.createFixationConstraint(pointSketchPrimitive_6);

    sketch_5.markFeatureForEdit();

    cadModel_0.allowMakingPartDirty(true);

    cadModel_0.getFeatureManager().stopSketchEdit(sketch_5, true);

    sketch_5.setIsUptoDate(true);

    cadModel_0.getFeatureManager().rollForwardToEnd(false);

    ExtrusionMerge extrusionMerge_0 = 
      cadModel_0.getFeatureManager().createExtrusionMerge(sketch_4);

    cadModel_0.allowMakingPartDirty(false);

    scene_0.setTransparencyOverrideMode(SceneTransparencyOverride.MAKE_SCENE_TRANSPARENT);

    extrusionMerge_0.setColorVector(new IntVector(new int[] {-16711681, -65536, -16776961, -16711936, -65536}));

    cadModel_0.allowMakingPartDirty(true);

    extrusionMerge_0.setDirectionOption(0);

    extrusionMerge_0.setExtrudedBodyTypeOption(0);

    extrusionMerge_0.getDistance().setValue(0.2);

    extrusionMerge_0.getDistanceAsymmetric().setValue(0.3);

    extrusionMerge_0.setDistanceOption(2);

    extrusionMerge_0.setCoordinateSystemOption(0);

    extrusionMerge_0.setDraftOption(0);

    extrusionMerge_0.setImportedCoordinateSystem(labCoordinateSystem_0);

    extrusionMerge_0.getDirectionAxis().setCoordinateSystem(labCoordinateSystem_0);

    extrusionMerge_0.getDirectionAxis().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {0.0, 0.0, 1.0}));

    extrusionMerge_0.setFace(null);

    extrusionMerge_0.setBody(null);

    extrusionMerge_0.setSketch(sketch_4);

    extrusionMerge_0.setPostOption(0);

    extrusionMerge_0.setExtrusionOption(0);

    extrusionMerge_0.setInteractingBodies(new NeoObjectVector(new Object[] {}));

    extrusionMerge_0.setInteractingBodiesBodyGroups(new NeoObjectVector(new Object[] {}));

    extrusionMerge_0.setInteractingBodiesCadFilters(new NeoObjectVector(new Object[] {}));

    extrusionMerge_0.setInteractingSelectedBodies(false);

    extrusionMerge_0.markFeatureForEdit();

    cadModel_0.getFeatureManager().execute(extrusionMerge_0);

    scene_0.setTransparencyOverrideMode(SceneTransparencyOverride.USE_DISPLAYER_PROPERTY);

    currentView_0.setInput(new DoubleVector(new double[] {-8.48715925388046, 2.8780847169753434, 7.484815609776305}), new DoubleVector(new double[] {353172.17343879584, 153152.18311597127, 2652072.630371348}), new DoubleVector(new double[] {0.057148283419641545, 0.9962383187430647, -0.0651405094406145}), 5.950884438309228, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-3.767394355585111, 1.303829305700674, 6.852765577499667}), new DoubleVector(new double[] {353176.905518144, 153150.61420046008, 2652072.090791619}), new DoubleVector(new double[] {0.057148283419641545, 0.9962383187430647, -0.0651405094406145}), 5.950884438309228, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-5.446523746511957, 1.4344243522991746, 1.673442201106809}), new DoubleVector(new double[] {353175.93007757183, 153151.0499352427, 2652072.195530474}), new DoubleVector(new double[] {0.057148283419641545, 0.9962383187430647, -0.0651405094406145}), 7.2005701703517735, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.13187914528680267, -0.19586799692115164, 2.323397522442974}), new DoubleVector(new double[] {353181.0799220872, 153149.34818083036, 2652071.6079872213}), new DoubleVector(new double[] {0.057148283419641545, 0.9962383187430647, -0.0651405094406145}), 1.4825335441037173, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.12443434202378212, -0.20235049639460184, 2.4143839268133793}), new DoubleVector(new double[] {78679.1006880623, -529193.158041889, 2625917.042976052}), new DoubleVector(new double[] {0.07840831038634889, 0.9777261041741053, 0.1946889880781344}), 1.4825335441037173, 1, 30.0);

    cadModel_0.getFeatureManager().rollBack(extrusionMerge_0, true);

    cadModel_0.allowMakingPartDirty(false);

    scene_0.setTransparencyOverrideMode(SceneTransparencyOverride.MAKE_SCENE_TRANSPARENT);

    extrusionMerge_0.setColorVector(new IntVector(new int[] {-16711681, -65536, -16776961, -16711936, -65536}));

    cadModel_0.allowMakingPartDirty(true);

    extrusionMerge_0.setDirectionOption(0);

    extrusionMerge_0.setExtrudedBodyTypeOption(0);

    extrusionMerge_0.getDistance().setValue(0.3);

    extrusionMerge_0.setDistanceOption(2);

    extrusionMerge_0.setCoordinateSystemOption(0);

    extrusionMerge_0.setDraftOption(0);

    extrusionMerge_0.setImportedCoordinateSystem(labCoordinateSystem_0);

    extrusionMerge_0.getDirectionAxis().setCoordinateSystem(labCoordinateSystem_0);

    extrusionMerge_0.getDirectionAxis().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {0.0, 0.0, 1.0}));

    extrusionMerge_0.setFace(null);

    extrusionMerge_0.setBody(null);

    extrusionMerge_0.setSketch(sketch_4);

    extrusionMerge_0.setPostOption(0);
  }

  private void execute1() {

    Simulation simulation_0 = 
      getActiveSimulation();

    CadModel cadModel_0 = 
      ((CadModel) simulation_0.get(SolidModelManager.class).getObject("3D-CAD Model 1"));

    ExtrusionMerge extrusionMerge_0 = 
      ((ExtrusionMerge) cadModel_0.getFeature("Extrude 1"));

    extrusionMerge_0.setExtrusionOption(0);

    extrusionMerge_0.setInteractingBodies(new NeoObjectVector(new Object[] {}));

    extrusionMerge_0.setInteractingBodiesBodyGroups(new NeoObjectVector(new Object[] {}));

    extrusionMerge_0.setInteractingBodiesCadFilters(new NeoObjectVector(new Object[] {}));

    extrusionMerge_0.setInteractingSelectedBodies(false);

    extrusionMerge_0.markFeatureForEdit();

    cadModel_0.getFeatureManager().markDependentNotUptodate(extrusionMerge_0);

    cadModel_0.getFeatureManager().rollForwardToEnd(false);

    Scene scene_0 = 
      simulation_0.getSceneManager().getScene("3D-CAD View 1");

    scene_0.setTransparencyOverrideMode(SceneTransparencyOverride.USE_DISPLAYER_PROPERTY);

    scene_0.setViewOrientation(new DoubleVector(new double[] {0.0, 0.0, 1.0}), new DoubleVector(new double[] {0.0, 1.0, 0.0}));

    CurrentView currentView_0 = 
      scene_0.getCurrentView();

    currentView_0.setInput(new DoubleVector(new double[] {-4.037446349441503, -0.4774841531661607, 2.413687989115715}), new DoubleVector(new double[] {-4.037446349441503, -0.4774841531661607, 2679864.9559097737}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 5.629918666545515, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-6.553995094483801, -1.0745225033930326, 2.413682095706463}), new DoubleVector(new double[] {-6.553995094483801, -1.0745225033930326, 2679864.9559097737}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 8.242763919681266, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-2.8231393301922747, 0.08468227694414995, 2.4136878703720868}), new DoubleVector(new double[] {-2.8231393301922747, 0.08468227694414995, 2679864.9559097737}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 4.380542700249227, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-1.7629618926984323, -0.42422484958141643, 2.413690078537911}), new DoubleVector(new double[] {-1.7629618926984323, -0.42422484958141643, 2679864.9559097737}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 3.193415628482962, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.7926027735975046, -1.0460734964041056, 2.4136917223222554}), new DoubleVector(new double[] {-0.7926027735975046, -1.0460734964041056, 2679864.9559097737}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 1.8856799944633444, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.143965920465151, -1.342593200693182, 2.4136923612095416}), new DoubleVector(new double[] {-0.143965920465151, -1.342593200693182, 2679864.9559097737}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 1.8856799944633444, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.10690095742901706, -0.11018317974170846, 2.4136923612095416}), new DoubleVector(new double[] {-0.10690095742901706, -0.11018317974170846, 2679864.9559097737}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 1.8856799944633444, 1, 30.0);

    extrusionMerge_0.getDistance().setValue(0.4);

    extrusionMerge_0.setPropertyChangedFromClient(true);

    cadModel_0.getFeatureManager().rollBack(extrusionMerge_0, true);

    cadModel_0.allowMakingPartDirty(false);

    Units units_0 = 
      simulation_0.getUnitsManager().getPreferredUnits(new IntVector(new int[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

    scene_0.setTransparencyOverrideMode(SceneTransparencyOverride.MAKE_SCENE_TRANSPARENT);

    extrusionMerge_0.setColorVector(new IntVector(new int[] {-16711681, -65536, -16776961, -16711936, -65536}));

    cadModel_0.allowMakingPartDirty(true);

    extrusionMerge_0.setDirectionOption(0);

    extrusionMerge_0.setExtrudedBodyTypeOption(0);

    extrusionMerge_0.setDistanceOption(2);

    extrusionMerge_0.setCoordinateSystemOption(0);

    extrusionMerge_0.setDraftOption(0);

    LabCoordinateSystem labCoordinateSystem_0 = 
      simulation_0.getCoordinateSystemManager().getLabCoordinateSystem();

    extrusionMerge_0.setImportedCoordinateSystem(labCoordinateSystem_0);

    extrusionMerge_0.getDirectionAxis().setCoordinateSystem(labCoordinateSystem_0);

    extrusionMerge_0.getDirectionAxis().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {0.0, 0.0, 1.0}));

    extrusionMerge_0.setFace(null);

    extrusionMerge_0.setBody(null);

    Sketch sketch_4 = 
      ((Sketch) cadModel_0.getFeature("Sketch 4"));

    extrusionMerge_0.setSketch(sketch_4);

    extrusionMerge_0.setPostOption(0);

    extrusionMerge_0.setExtrusionOption(0);

    extrusionMerge_0.setInteractingBodies(new NeoObjectVector(new Object[] {}));

    extrusionMerge_0.setInteractingBodiesBodyGroups(new NeoObjectVector(new Object[] {}));

    extrusionMerge_0.setInteractingBodiesCadFilters(new NeoObjectVector(new Object[] {}));

    extrusionMerge_0.setInteractingSelectedBodies(false);

    extrusionMerge_0.markFeatureForEdit();

    cadModel_0.getFeatureManager().markDependentNotUptodate(extrusionMerge_0);

    cadModel_0.getFeatureManager().rollForwardToEnd(false);

    scene_0.setTransparencyOverrideMode(SceneTransparencyOverride.USE_DISPLAYER_PROPERTY);

    currentView_0.setInput(new DoubleVector(new double[] {-0.5012679210557058, -0.05340037368974343, 3.8492339464775016}), new DoubleVector(new double[] {-1367093.1545245468, 196840.36211277792, 2296517.461954836}), new DoubleVector(new double[] {0.020178389630836003, 0.997094620770686, -0.07345168358906908}), 1.8856799944633444, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {1.6310577375406865, -0.2938500085474516, 1.3588611590093933}), new DoubleVector(new double[] {-1367092.6748161404, 196840.35961473017, 2296517.7477347897}), new DoubleVector(new double[] {0.020178389630836003, 0.997094620770686, -0.07345168358906908}), 2.5098400726305603, 1, 30.0);

    Sketch sketch_5 = 
      ((Sketch) cadModel_0.getFeature("Sketch 5"));

    ExtrusionMerge extrusionMerge_1 = 
      cadModel_0.getFeatureManager().createExtrusionMerge(sketch_5);

    cadModel_0.allowMakingPartDirty(false);

    scene_0.setTransparencyOverrideMode(SceneTransparencyOverride.MAKE_SCENE_TRANSPARENT);

    currentView_0.setInput(new DoubleVector(new double[] {2.257464989903383, 2.510832160613063, 5.672287517430959}), new DoubleVector(new double[] {-1367090.2206675648, 196842.90113012664, 2296518.9908258496}), new DoubleVector(new double[] {0.020178389630836003, 0.997094620770686, -0.07345168358906908}), 7.876953304969387, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {2.8432565682680746, 2.248811181270459, 5.984042168044396}), new DoubleVector(new double[] {-143987.94809087232, -310544.15260730387, 2657919.3873252743}), new DoubleVector(new double[] {-0.03699142611981438, 0.9927927174030262, 0.11399234476551284}), 7.876953304969387, 1, 30.0);

    extrusionMerge_1.setColorVector(new IntVector(new int[] {-16711681, -65536, -16776961, -16711936, -65536}));

    cadModel_0.allowMakingPartDirty(true);

    extrusionMerge_1.setDirectionOption(0);

    extrusionMerge_1.setExtrudedBodyTypeOption(0);

    extrusionMerge_1.getDistance().setValue(10.2);

    extrusionMerge_1.getDistanceAsymmetric().setValue(0.5);

    extrusionMerge_1.setDistanceOption(2);

    extrusionMerge_1.setCoordinateSystemOption(0);

    extrusionMerge_1.setDraftOption(0);

    extrusionMerge_1.setImportedCoordinateSystem(labCoordinateSystem_0);

    extrusionMerge_1.getDirectionAxis().setCoordinateSystem(labCoordinateSystem_0);

    extrusionMerge_1.getDirectionAxis().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {0.0, 0.0, 1.0}));

    extrusionMerge_1.setFace(null);

    extrusionMerge_1.setBody(null);

    extrusionMerge_1.setSketch(sketch_5);

    extrusionMerge_1.setPostOption(1);

    extrusionMerge_1.setExtrusionOption(0);

    extrusionMerge_1.setInteractingBodies(new NeoObjectVector(new Object[] {}));

    extrusionMerge_1.setInteractingBodiesBodyGroups(new NeoObjectVector(new Object[] {}));

    extrusionMerge_1.setInteractingBodiesCadFilters(new NeoObjectVector(new Object[] {}));

    extrusionMerge_1.setInteractingSelectedBodies(false);

    extrusionMerge_1.markFeatureForEdit();

    cadModel_0.getFeatureManager().execute(extrusionMerge_1);

    scene_0.setTransparencyOverrideMode(SceneTransparencyOverride.USE_DISPLAYER_PROPERTY);

    cadModel_0.getFeatureManager().rollBack(extrusionMerge_1, true);

    cadModel_0.allowMakingPartDirty(false);

    scene_0.setTransparencyOverrideMode(SceneTransparencyOverride.MAKE_SCENE_TRANSPARENT);

    extrusionMerge_1.setColorVector(new IntVector(new int[] {-16711681, -65536, -16776961, -16711936, -65536}));

    cadModel_0.allowMakingPartDirty(true);

    extrusionMerge_1.setDirectionOption(0);

    extrusionMerge_1.setExtrudedBodyTypeOption(0);

    extrusionMerge_1.setDistanceOption(2);

    extrusionMerge_1.setCoordinateSystemOption(0);

    extrusionMerge_1.setDraftOption(0);

    extrusionMerge_1.setImportedCoordinateSystem(labCoordinateSystem_0);

    extrusionMerge_1.getDirectionAxis().setCoordinateSystem(labCoordinateSystem_0);

    extrusionMerge_1.getDirectionAxis().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {0.0, 0.0, 1.0}));

    extrusionMerge_1.setFace(null);

    extrusionMerge_1.setBody(null);

    extrusionMerge_1.setSketch(sketch_5);

    extrusionMerge_1.setPostOption(0);

    extrusionMerge_1.setExtrusionOption(0);

    extrusionMerge_1.setInteractingBodies(new NeoObjectVector(new Object[] {}));

    extrusionMerge_1.setInteractingBodiesBodyGroups(new NeoObjectVector(new Object[] {}));

    extrusionMerge_1.setInteractingBodiesCadFilters(new NeoObjectVector(new Object[] {}));

    extrusionMerge_1.setInteractingSelectedBodies(false);

    extrusionMerge_1.markFeatureForEdit();

    cadModel_0.getFeatureManager().markDependentNotUptodate(extrusionMerge_1);

    cadModel_0.getFeatureManager().rollForwardToEnd(false);

    scene_0.setTransparencyOverrideMode(SceneTransparencyOverride.USE_DISPLAYER_PROPERTY);

    currentView_0.setInput(new DoubleVector(new double[] {-8.239153572563737, 2.810826357537735, 5.117295457923319}), new DoubleVector(new double[] {-143999.04819500505, -310543.62875293964, 2657918.8471904113}), new DoubleVector(new double[] {-0.03699142611981438, 0.9927927174030262, 0.11399234476551284}), 24.721253774259928, 1, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-16.080260216889656, 2.7965640378643, 3.9334738627658226}), new DoubleVector(new double[] {-144006.9296621723, -310543.73006120883, 2657918.408380173}), new DoubleVector(new double[] {-0.03699142611981438, 0.9927927174030262, 0.11399234476551284}), 36.19438765056583, 1, 30.0);

    CircleSketchPrimitive circleSketchPrimitive_4 = 
      ((CircleSketchPrimitive) sketch_4.getSketchPrimitive("Circle 1"));

    star.cadmodeler.Body cadmodelerBody_4 = 
      ((star.cadmodeler.Body) extrusionMerge_0.getBody(circleSketchPrimitive_4));

    cadmodelerBody_4.setPresentationName("Rotate");

    CircleSketchPrimitive circleSketchPrimitive_5 = 
      ((CircleSketchPrimitive) sketch_5.getSketchPrimitive("Circle 1"));

    star.cadmodeler.Body cadmodelerBody_5 = 
      ((star.cadmodeler.Body) extrusionMerge_1.getBody(circleSketchPrimitive_5));

    cadmodelerBody_5.setPresentationName("Refine");
  }
}
