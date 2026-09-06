// STAR-CCM+ macro: setBladeGeo.java
// Written by STAR-CCM+ 15.02.009
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.cadmodeler.*;
import star.vis.*;

public class setBladeGeo extends StarMacro {

  public void execute() {
    execute0();
    execute1();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    Scene scene_0 = 
      simulation_0.getSceneManager().getScene("3D-CAD View 1");

    CadModel cadModel_1 = 
      simulation_0.get(SolidModelManager.class).createSolidModel(scene_0);

    cadModel_1.resetSystemOptions();

    scene_0.initializeAndWait();

    scene_0.open();

    scene_0.setAdvancedRenderingEnabled(false);

    SceneUpdate sceneUpdate_0 = 
      scene_0.getSceneUpdate();

    HardcopyProperties hardcopyProperties_0 = 
      sceneUpdate_0.getHardcopyProperties();

    hardcopyProperties_0.setCurrentResolutionWidth(25);

    hardcopyProperties_0.setCurrentResolutionHeight(25);

    hardcopyProperties_0.setCurrentResolutionWidth(946);

    hardcopyProperties_0.setCurrentResolutionHeight(409);

    ImportCadFileFeature importCadFileFeature_0 = 
      cadModel_1.importCadFile(resolvePath("/lustre/scratch/usr/tekajuna/ATLANTIS/star/shortSliding/geom/blade.igs"), true, false, false, false, true, false, false, true, false, false, NeoProperty.fromString("{\'STEP\': 0, \'NX\': 0, \'CATIAV5\': 0, \'SE\': 0, \'JT\': 0}"));

    scene_0.resetCamera();

    importCadFileFeature_0.setPresentationName("Blade");

    star.cadmodeler.Body cadmodelerBody_0 = 
      ((star.cadmodeler.Body) importCadFileFeature_0.getBodyByIndex(1));

    cadmodelerBody_0.getUnNamedFacesDefaultAttributeName();

    cadmodelerBody_0.getUnNamedFacesDefaultAttributeName();

    cadmodelerBody_0.getUnNamedEdgesDefaultAttributeName();

    cadmodelerBody_0.getUnNamedEdgesDefaultAttributeName();

    RotateBodyFeature rotateBodyFeature_0 = 
      cadModel_1.getFeatureManager().createRotateBodyFeature();

    cadModel_1.allowMakingPartDirty(false);

    Units units_0 = 
      simulation_0.getUnitsManager().getPreferredUnits(new IntVector(new int[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

    rotateBodyFeature_0.setIsBodyGroupCreation(false);

    scene_0.setTransparencyOverrideMode(SceneTransparencyOverride.MAKE_SCENE_TRANSPARENT);

    rotateBodyFeature_0.setAutoPreview(true);

    cadModel_1.getFeatureManager().deleteOnCancel(rotateBodyFeature_0);

    cadModel_1.allowMakingPartDirty(true);

    scene_0.setTransparencyOverrideMode(SceneTransparencyOverride.USE_DISPLAYER_PROPERTY);

    CurrentView currentView_0 = 
      scene_0.getCurrentView();

    currentView_0.setInput(new DoubleVector(new double[] {-3.6826612711396947, -0.4960271298215706, 4.07771354709485}), new DoubleVector(new double[] {-8.950115570707078, -1.2237448340256911, -3.0498327387804243}), new DoubleVector(new double[] {-0.24422482908815887, 0.9662594639030335, 0.08183447485432725}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-3.3373827672628336, -0.7074649854310376, 1.5069874242339985}), new DoubleVector(new double[] {-0.9882117435024702, -1.5691405729091423, -9.220554716412519}), new DoubleVector(new double[] {-0.29156073499692975, 0.9462730891163014, -0.13985556343008226}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-4.862370792344674, -0.803670312104303, 1.13239387642278}), new DoubleVector(new double[] {-1.5056513267542144, -2.0349145204897456, -14.196140809744616}), new DoubleVector(new double[] {-0.29156073499692975, 0.9462730891163014, -0.13985556343008226}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-1.4395857974736808, -0.24281749930983748, 2.4903666149418475}), new DoubleVector(new double[] {-0.29788534188451443, -0.6615930592173773, -2.7232341086542915}), new DoubleVector(new double[] {-0.29156073499692975, 0.9462730891163014, -0.13985556343008226}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.5572386007594652, -0.12948350351524449, 2.5481866662811834}), new DoubleVector(new double[] {-0.007316889479817894, -0.331194706676327, 0.03695684429668944}), new DoubleVector(new double[] {-0.29156073499692975, 0.9462730891163014, -0.13985556343008226}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.3600281467177415, -0.1653961297514373, 1.9274430230220312}), new DoubleVector(new double[] {0.01244721388924324, -0.30202003747288836, 0.22652593914455574}), new DoubleVector(new double[] {-0.29156073499692975, 0.9462730891163014, -0.13985556343008226}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.2642922470700087, -0.2583822816488531, 1.2131952255017002}), new DoubleVector(new double[] {-0.0029856341373356004, -0.35422950900408173, 0.01993271991461812}), new DoubleVector(new double[] {-0.29156073499692975, 0.9462730891163014, -0.13985556343008226}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.37998441213497647, -0.3262446737563306, 1.2134266687757496}), new DoubleVector(new double[] {-0.032399999195513944, -0.45373858551862545, -0.37382534460526207}), new DoubleVector(new double[] {-0.29156073499692975, 0.9462730891163014, -0.13985556343008226}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.35500846460588426, -0.448861942199479, 0.5953878910467958}), new DoubleVector(new double[] {-0.060507440384181765, -0.5568848747541866, -0.7494576378005553}), new DoubleVector(new double[] {-0.29156073499692975, 0.9462730891163014, -0.13985556343008226}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.31524493073021964, -0.06710978968550421, 0.728019901871797}), new DoubleVector(new double[] {0.011361822692657935, -0.18690909900930386, -0.7634371590769842}), new DoubleVector(new double[] {-0.29156073499692975, 0.9462730891163014, -0.13985556343008226}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.30932016722713906, -0.07183180985275738, 0.7141227812081326}), new DoubleVector(new double[] {-0.4123947987793747, 0.33541394445432315, -0.7224333091751363}), new DoubleVector(new double[] {-0.32287580969360435, 0.9042281997392221, 0.2795041579495203}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.28863449758684506, -0.08386417113935411, 0.7872506629286928}), new DoubleVector(new double[] {-1.0569137436513176, -0.09660650350295706, -0.5097713369332414}), new DoubleVector(new double[] {-0.34888768853800345, 0.9160827857504327, 0.19766059404530997}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.28863449758684506, -0.08386417113935411, 0.7872506629286928}), new DoubleVector(new double[] {-1.0569137436513176, -0.09660650350295706, -0.5097713369332414}), new DoubleVector(new double[] {-0.34888768853800345, 0.9160827857504327, 0.19766059404530997}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.28863449758684506, -0.08386417113935411, 0.7872506629286928}), new DoubleVector(new double[] {-1.0569137436513176, -0.09660650350295706, -0.5097713369332414}), new DoubleVector(new double[] {-0.34888768853800345, 0.9160827857504327, 0.19766059404530997}), 2.3015629593758664, 0, 30.0);

    Face face_0 = 
      ((Face) importCadFileFeature_0.getFaceByLocation(cadmodelerBody_0,new DoubleVector(new double[] {0.14010720791809977, -0.05515436613821389, 3.148999968942829})));

    cadModel_1.setFaceNameAttributes(new NeoObjectVector(new Object[] {face_0}), "LE");

    currentView_0.setInput(new DoubleVector(new double[] {-0.3145249666299551, -0.02805904695846717, 0.42380592988811006}), new DoubleVector(new double[] {0.07829109705030521, 0.3551373565191075, -1.0547547112085458}), new DoubleVector(new double[] {-0.3565948134743736, 0.9230199368854071, 0.14447953182112314}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.07502334663074778, 0.0926289724941382, 0.5995861889046189}), new DoubleVector(new double[] {0.47741527124483774, 0.48516673394894605, -0.9150180165736104}), new DoubleVector(new double[] {-0.3565948134743736, 0.9230199368854071, 0.14447953182112314}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.10114191849381254, 0.03772028908808156, 0.927333148813416}), new DoubleVector(new double[] {-0.5398112003758937, 0.14670616211874365, -0.5516300803799797}), new DoubleVector(new double[] {-0.3506512052141874, 0.910524778039839, 0.21906245880424613}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.10114191849381254, 0.03772028908808156, 0.927333148813416}), new DoubleVector(new double[] {-0.5398112003758937, 0.14670616211874365, -0.5516300803799797}), new DoubleVector(new double[] {-0.3506512052141874, 0.910524778039839, 0.21906245880424613}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.16615499067777972, 0.13977997538099174, 0.9067599665583684}), new DoubleVector(new double[] {-0.4748276675055453, 0.2487708711938023, -0.5722714229378193}), new DoubleVector(new double[] {-0.3506512052141874, 0.910524778039839, 0.21906245880424613}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.17694497601322703, 0.2024292857383264, 0.6505746757386963}), new DoubleVector(new double[] {0.9352610330612714, 0.7747049803804604, -0.656226350247698}), new DoubleVector(new double[] {-0.34976525286495436, 0.9156688354190817, 0.1980274014640501}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.17694497601322703, 0.2024292857383264, 0.6505746757386963}), new DoubleVector(new double[] {0.9352610330612714, 0.7747049803804604, -0.656226350247698}), new DoubleVector(new double[] {-0.34976525286495436, 0.9156688354190817, 0.1980274014640501}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.17694497601322703, 0.2024292857383264, 0.6505746757386963}), new DoubleVector(new double[] {0.9352610330612714, 0.7747049803804604, -0.656226350247698}), new DoubleVector(new double[] {-0.34976525286495436, 0.9156688354190817, 0.1980274014640501}), 2.3015629593758664, 0, 30.0);

    Face face_1 = 
      ((Face) importCadFileFeature_0.getFaceByLocation(cadmodelerBody_0,new DoubleVector(new double[] {0.4560282397003833, 0.009277673463363435, 3.1489999689428294})));

    Edge edge_0 = 
      ((Edge) cadModel_1.getEdgeByLocation(new NeoObjectVector(new Object[] {face_0, face_1})));

    cadModel_1.setEdgeNameAttributes(new NeoObjectVector(new Object[] {edge_0}), "TE");

    currentView_0.setInput(new DoubleVector(new double[] {0.23716977139082746, 0.272002660518464, 0.2557476837607721}), new DoubleVector(new double[] {-0.06831039748085846, 0.21694582793228415, -1.3348925701815495}), new DoubleVector(new double[] {-0.32330332415826224, 0.9458400740504765, 0.029351574207070182}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.2758606777296938, 0.29842187734162895, 0.5802120047044517}), new DoubleVector(new double[] {0.9324966598423934, -0.3782637813639508, -1.029435182423183}), new DoubleVector(new double[] {-0.2793275022284918, 0.839137581919277, -0.46671647186432974}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.2758606777296938, 0.29842187734162895, 0.5802120047044517}), new DoubleVector(new double[] {0.9324966598423934, -0.3782637813639508, -1.029435182423183}), new DoubleVector(new double[] {-0.2793275022284918, 0.839137581919277, -0.46671647186432974}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.2758606777296938, 0.29842187734162895, 0.5802120047044517}), new DoubleVector(new double[] {0.9324966598423934, -0.3782637813639508, -1.029435182423183}), new DoubleVector(new double[] {-0.2793275022284918, 0.839137581919277, -0.46671647186432974}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.3041128700050778, 0.27477619734811226, 0.4920652810083268}), new DoubleVector(new double[] {0.5551488284263827, 0.36081211341843533, -1.314041408825506}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.43910939779986513, 0.4207892796607169, 0.6842296632525366}), new DoubleVector(new double[] {0.8334877488545682, 0.5559519986778473, -2.1531701365407843}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.28725553362645073, 0.2800826938651365, 0.7426263532139759}), new DoubleVector(new double[] {0.5860780135650688, 0.38249617265759484, -1.4072858883785444}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.11292512652119793, 0.1142965417614617, 0.8303994207586931}), new DoubleVector(new double[] {0.3021821030520736, 0.17915935046397638, -0.5312313733304843}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.11292512652119793, 0.1142965417614617, 0.8303994207586931}), new DoubleVector(new double[] {0.3021821030520736, 0.17915935046397638, -0.5312313733304843}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.11292512652119793, 0.1142965417614617, 0.8303994207586931}), new DoubleVector(new double[] {0.3021821030520736, 0.17915935046397638, -0.5312313733304843}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.11292512652119793, 0.1142965417614617, 0.8303994207586931}), new DoubleVector(new double[] {0.3021821030520736, 0.17915935046397638, -0.5312313733304843}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.10031427992651723, 0.13492589377426684, 0.8502834953261249}), new DoubleVector(new double[] {0.31130663650893736, 0.20723792746284053, -0.6677249570238368}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.1222776652040381, 0.0485105668300726, 0.8869130342748663}), new DoubleVector(new double[] {0.2650376703786692, 0.09743777192309828, -0.14019002302455896}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.09209836680402576, 0.012056098283399909, 1.0871984804012234}), new DoubleVector(new double[] {0.21657510594665272, 0.05471719890393024, 0.19163645531119786}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.09209836680402576, 0.012056098283399909, 1.0871984804012234}), new DoubleVector(new double[] {0.21657510594665272, 0.05471719890393024, 0.19163645531119786}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.09209836680402576, 0.012056098283399909, 1.0871984804012234}), new DoubleVector(new double[] {0.21657510594665272, 0.05471719890393024, 0.19163645531119786}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.09209836680402576, 0.012056098283399909, 1.0871984804012234}), new DoubleVector(new double[] {0.21657510594665272, 0.05471719890393024, 0.19163645531119786}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.09209836680402576, 0.012056098283399909, 1.0871984804012234}), new DoubleVector(new double[] {0.21657510594665272, 0.05471719890393024, 0.19163645531119786}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.09209836680402576, 0.012056098283399909, 1.0871984804012234}), new DoubleVector(new double[] {0.21657510594665272, 0.05471719890393024, 0.19163645531119786}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.09209836680402576, 0.012056098283399909, 1.0871984804012234}), new DoubleVector(new double[] {0.21657510594665272, 0.05471719890393024, 0.19163645531119786}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.10096985387832823, -0.016814423360074143, 1.113930755743876}), new DoubleVector(new double[] {0.20526205368330236, 0.018928961793343833, 0.36358868941277467}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.10096985387832823, -0.016814423360074143, 1.113930755743876}), new DoubleVector(new double[] {0.20526205368330236, 0.018928961793343833, 0.36358868941277467}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.10096985387832823, -0.016814423360074143, 1.113930755743876}), new DoubleVector(new double[] {0.20526205368330236, 0.018928961793343833, 0.36358868941277467}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.10096985387832823, -0.016814423360074143, 1.113930755743876}), new DoubleVector(new double[] {0.20526205368330236, 0.018928961793343833, 0.36358868941277467}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.12692919065348188, -0.014630630735493859, 0.823500218681885}), new DoubleVector(new double[] {0.21229198419904524, 0.014625202521225407, 0.20934791374873774}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.12692919065348188, -0.014630630735493859, 0.823500218681885}), new DoubleVector(new double[] {0.21229198419904524, 0.014625202521225407, 0.20934791374873774}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.12692919065348188, -0.014630630735493859, 0.823500218681885}), new DoubleVector(new double[] {0.21229198419904524, 0.014625202521225407, 0.20934791374873774}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.17430132108943416, -0.0014271336169977833, 0.5160396533835013}), new DoubleVector(new double[] {0.2089131122121021, 0.010435139719460963, 0.26702119448659284}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.18771822217389958, -0.006064264656614294, 0.5201097782075856}), new DoubleVector(new double[] {0.19880633647783236, -0.002264107588955158, 0.4403350815947377}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.18771822217389958, -0.006064264656614294, 0.5201097782075856}), new DoubleVector(new double[] {0.19880633647783236, -0.002264107588955158, 0.4403350815947377}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.18771822217389958, -0.006064264656614294, 0.5201097782075856}), new DoubleVector(new double[] {0.19880633647783236, -0.002264107588955158, 0.4403350815947377}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    Edge edge_1 = 
      ((Edge) cadModel_1.getEdgeByLocation(face_0,new DoubleVector(new double[] {0.194238, -0.0031694755, 0.508, 0.0837722312679641, -0.10899004844990991, 0.508, -0.023761987814499997, 1.04537726294E-4, 0.508})));

    Face face_2 = 
      ((Face) importCadFileFeature_0.getFaceByLocation(cadmodelerBody_0,new DoubleVector(new double[] {0.13614252977129435, 0.05854702911608471, 3.148999968942829})));

    Edge edge_2 = 
      ((Edge) cadModel_1.getEdgeByLocation(face_2,new DoubleVector(new double[] {0.194238, 0.0031694755, 0.508, 0.08387674243656584, 0.10899140462604581, 0.508, -0.023761987814499997, 1.04537726294E-4, 0.508})));

    Edge edge_3 = 
      ((Edge) cadModel_1.getEdgeByLocation(face_1,new DoubleVector(new double[] {0.194238, -0.0031694755, 0.508, 0.194238, 0.0, 0.508, 0.194238, 0.0031694755, 0.508})));

    FillFeature fillFeature_0 = 
      cadModel_1.getFeatureManager().createFillFeature(new NeoObjectVector(new Object[] {edge_1, edge_2, edge_3}), new NeoObjectVector(new Object[] {}));

    fillFeature_0.setContinuityData(new IntVector(new int[] {0, 0, 0}));

    cadModel_1.allowMakingPartDirty(false);

    fillFeature_0.setEdges(new NeoObjectVector(new Object[] {edge_1, edge_2, edge_3}));

    fillFeature_0.setCadFilters(new NeoObjectVector(new Object[] {}));

    fillFeature_0.setIsAllEdgesTangent(false);

    fillFeature_0.setContinuityData(new IntVector(new int[] {0, 0, 0}));

    fillFeature_0.setGuideSketches(Collections.<CurveFeature>emptyList());

    fillFeature_0.setProfileSketches(Collections.<CurveFeature>emptyList());

    fillFeature_0.setColorVector(new IntVector(new int[] {-16711936, -16776961, -14336}));

    cadModel_1.allowMakingPartDirty(true);

    fillFeature_0.setEdges(new NeoObjectVector(new Object[] {edge_1, edge_2, edge_3}));

    fillFeature_0.setCadFilters(new NeoObjectVector(new Object[] {}));

    fillFeature_0.setIsAllEdgesTangent(false);

    fillFeature_0.setContinuityData(new IntVector(new int[] {0, 0, 0}));

    fillFeature_0.setGuideSketches(Collections.<CurveFeature>emptyList());

    fillFeature_0.setProfileSketches(Collections.<CurveFeature>emptyList());

    fillFeature_0.markFeatureForEdit();

    cadModel_1.getFeatureManager().execute(fillFeature_0);

    currentView_0.setInput(new DoubleVector(new double[] {0.16803904025031116, -0.0058551507785896025, 0.5217357388503471}), new DoubleVector(new double[] {0.1949135641131634, 0.0033553794340335677, 0.32838392726112414}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.018830631246589014, -0.0019675852023649243, 0.4893144194584549}), new DoubleVector(new double[] {0.2761969890667727, 0.09914530611142872, -1.6332952739283764}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.00292734668221048, -0.07016502108114758, 0.5094827073392094}), new DoubleVector(new double[] {0.09067920701450391, -0.038083857696710355, -0.16398026548395697}), new DoubleVector(new double[] {-0.3995350066595783, 0.916641121619638, -0.011867291580296015}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-1.5471731580873893E-4, -0.06594756703472605, 0.49324201190852723}), new DoubleVector(new double[] {0.1234919010031913, 0.23227804767848173, -0.10603579364082127}), new DoubleVector(new double[] {-0.4560628848188114, 0.8306979523637912, 0.31929227524225806}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-1.5471731580873893E-4, -0.06594756703472605, 0.49324201190852723}), new DoubleVector(new double[] {0.1234919010031913, 0.23227804767848173, -0.10603579364082127}), new DoubleVector(new double[] {-0.4560628848188114, 0.8306979523637912, 0.31929227524225806}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-1.5471731580873893E-4, -0.06594756703472605, 0.49324201190852723}), new DoubleVector(new double[] {0.1234919010031913, 0.23227804767848173, -0.10603579364082127}), new DoubleVector(new double[] {-0.4560628848188114, 0.8306979523637912, 0.31929227524225806}), 2.3015629593758664, 0, 30.0);

    Face face_3 = 
      ((Face) fillFeature_0.getFaceByLocation(cadmodelerBody_0,new DoubleVector(new double[] {0.08909095415160122, 0.015569217204081873, 0.508000000000002})));

    ReferencePointFromEntityCenter referencePointFromEntityCenter_0 = 
      cadModel_1.getFeatureManager().createPointFromEntityCenter(face_3);

    cadModel_1.allowMakingPartDirty(false);

    currentView_0.setInput(new DoubleVector(new double[] {-1.5471731580873893E-4, -0.06594756703472605, 0.49324201190852723}), new DoubleVector(new double[] {0.1234919010031913, 0.23227804767848173, -0.10603579364082127}), new DoubleVector(new double[] {-0.4560628848188114, 0.8306979523637912, 0.31929227524225806}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-1.5471731580873893E-4, -0.06594756703472605, 0.49324201190852723}), new DoubleVector(new double[] {0.1234919010031913, 0.23227804767848173, -0.10603579364082127}), new DoubleVector(new double[] {-0.4560628848188114, 0.8306979523637912, 0.31929227524225806}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-1.5471731580873893E-4, -0.06594756703472605, 0.49324201190852723}), new DoubleVector(new double[] {0.1234919010031913, 0.23227804767848173, -0.10603579364082127}), new DoubleVector(new double[] {-0.4560628848188114, 0.8306979523637912, 0.31929227524225806}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-1.5471731580873893E-4, -0.06594756703472605, 0.49324201190852723}), new DoubleVector(new double[] {0.1234919010031913, 0.23227804767848173, -0.10603579364082127}), new DoubleVector(new double[] {-0.4560628848188114, 0.8306979523637912, 0.31929227524225806}), 2.3015629593758664, 0, 30.0);

    referencePointFromEntityCenter_0.setColorVector(new IntVector(new int[] {-16711936}));

    cadModel_1.allowMakingPartDirty(true);

    referencePointFromEntityCenter_0.setEntity(face_3);

    referencePointFromEntityCenter_0.setCenterPointType(0);

    referencePointFromEntityCenter_0.markFeatureForEdit();

    cadModel_1.getFeatureManager().execute(referencePointFromEntityCenter_0);

    MoveBodyFeature moveBodyFeature_0 = 
      cadModel_1.getFeatureManager().createMoveBodyFeature();

    cadModel_1.allowMakingPartDirty(false);

    moveBodyFeature_0.setIsBodyGroupCreation(false);

    scene_0.setTransparencyOverrideMode(SceneTransparencyOverride.MAKE_SCENE_TRANSPARENT);

    moveBodyFeature_0.setColorVector(new IntVector(new int[] {-16711936, -65536, -65536}));

    cadModel_1.allowMakingPartDirty(true);

    moveBodyFeature_0.setDirectionOption(0);

    moveBodyFeature_0.setCoordinateSystemSourceOption(1);

    CanonicalReferenceCoordinateSystem canonicalReferenceCoordinateSystem_0 = 
      ((CanonicalReferenceCoordinateSystem) cadModel_1.getFeature("Lab Coordinate System"));

    moveBodyFeature_0.setReferenceCoordinateSystem(canonicalReferenceCoordinateSystem_0);

    LabCoordinateSystem labCoordinateSystem_0 = 
      simulation_0.getCoordinateSystemManager().getLabCoordinateSystem();

    moveBodyFeature_0.setImportedCoordinateSystem(labCoordinateSystem_0);

    moveBodyFeature_0.getTranslationVector().setCoordinateSystem(labCoordinateSystem_0);

    moveBodyFeature_0.getTranslationVector().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {-0.0854258299294647, 9.333897686868816E-8, 0.0}));

    moveBodyFeature_0.setCopyOption(0);

    moveBodyFeature_0.setBodies(new NeoObjectVector(new Object[] {cadmodelerBody_0}));

    moveBodyFeature_0.setBodyGroups(new NeoObjectVector(new Object[] {}));

    moveBodyFeature_0.setCadFilters(new NeoObjectVector(new Object[] {}));

    moveBodyFeature_0.markFeatureForEdit();

    cadModel_1.getFeatureManager().execute(moveBodyFeature_0);

    scene_0.setTransparencyOverrideMode(SceneTransparencyOverride.USE_DISPLAYER_PROPERTY);

    currentView_0.setInput(new DoubleVector(new double[] {-6.680789776418727E-4, -0.055379150024485346, 0.5117112806888253}), new DoubleVector(new double[] {0.1833348847385887, 0.3884210744325446, -0.3800954974468505}), new DoubleVector(new double[] {-0.4560628848188114, 0.8306979523637912, 0.31929227524225806}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.011831159950482673, 0.0353141649090416, 0.5673159931953078}), new DoubleVector(new double[] {0.5383749458660356, 1.3052950698696728, -1.9846826691136836}), new DoubleVector(new double[] {-0.4560628848188114, 0.8306979523637912, 0.31929227524225806}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.03371563628346713, -0.035607992313293646, 0.5368264644361334}), new DoubleVector(new double[] {0.1713241989272858, 0.4589314599397676, -0.4569396746287346}), new DoubleVector(new double[] {-0.4560628848188114, 0.8306979523637912, 0.31929227524225806}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.03371563628346713, -0.035607992313293646, 0.5368264644361334}), new DoubleVector(new double[] {0.1713241989272858, 0.4589314599397676, -0.4569396746287346}), new DoubleVector(new double[] {-0.4560628848188114, 0.8306979523637912, 0.31929227524225806}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.03371563628346713, -0.035607992313293646, 0.5368264644361334}), new DoubleVector(new double[] {0.1713241989272858, 0.4589314599397676, -0.4569396746287346}), new DoubleVector(new double[] {-0.4560628848188114, 0.8306979523637912, 0.31929227524225806}), 2.3015629593758664, 0, 30.0);

    ReferencePointFromEntityCenter referencePointFromEntityCenter_1 = 
      cadModel_1.getFeatureManager().createPointFromEntityCenter(face_3);

    cadModel_1.allowMakingPartDirty(false);

    referencePointFromEntityCenter_1.setColorVector(new IntVector(new int[] {-16711936}));

    cadModel_1.allowMakingPartDirty(true);

    referencePointFromEntityCenter_1.setEntity(face_3);

    referencePointFromEntityCenter_1.setCenterPointType(0);

    referencePointFromEntityCenter_1.markFeatureForEdit();

    cadModel_1.getFeatureManager().execute(referencePointFromEntityCenter_1);

    currentView_0.setInput(new DoubleVector(new double[] {-0.03371563628346713, -0.035607992313293646, 0.5368264644361334}), new DoubleVector(new double[] {0.1713241989272858, 0.4589314599397676, -0.4569396746287346}), new DoubleVector(new double[] {-0.4560628848188114, 0.8306979523637912, 0.31929227524225806}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.007834711790497662, -0.04660542184559513, 0.5579784799582856}), new DoubleVector(new double[] {0.24382916932488374, 0.5603874475680715, -0.6617603245576709}), new DoubleVector(new double[] {-0.4560628848188114, 0.8306979523637912, 0.31929227524225806}), 2.3015629593758664, 0, 30.0);

    scene_0.setViewOrientation(new DoubleVector(new double[] {0.0, 0.0, 1.0}), new DoubleVector(new double[] {0.0, 1.0, 0.0}));

    currentView_0.setInput(new DoubleVector(new double[] {-0.24031678549014093, -0.16842602846424168, 2.0486762998341637}), new DoubleVector(new double[] {-0.24031678549014093, -0.16842602846424168, 3.3629030495205363}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-1.6075789579521977, -0.8848714068345731, 1.877058618469409}), new DoubleVector(new double[] {-1.6075789579521977, -0.8848714068345731, 11.71090698957356}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-1.4471918057632682, -0.8017295375013412, 2.528822812329082}), new DoubleVector(new double[] {-1.4471918057632682, -0.8017295375013412, 10.810846852265472}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.2651703146943788, 0.40177370744248125, 2.528822687255346}), new DoubleVector(new double[] {-0.2651703146943788, 0.40177370744248125, 10.810846852265472}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 2.3015629593758664, 0, 30.0);

    RotateBodyFeature rotateBodyFeature_1 = 
      cadModel_1.getFeatureManager().createRotateBodyFeature();

    cadModel_1.allowMakingPartDirty(false);

    rotateBodyFeature_1.setIsBodyGroupCreation(false);

    scene_0.setTransparencyOverrideMode(SceneTransparencyOverride.MAKE_SCENE_TRANSPARENT);

    Units units_1 = 
      simulation_0.getUnitsManager().getInternalUnits(new IntVector(new int[] {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

    rotateBodyFeature_1.setColorVector(new IntVector(new int[] {-16711936, -65536, -65536}));

    cadModel_1.allowMakingPartDirty(true);

    rotateBodyFeature_1.setRotationAxisOption(0);

    rotateBodyFeature_1.setCoordinateSystemSourceOption(1);

    rotateBodyFeature_1.setReferenceCoordinateSystem(canonicalReferenceCoordinateSystem_0);

    rotateBodyFeature_1.setImportedCoordinateSystem(labCoordinateSystem_0);

    rotateBodyFeature_1.getAxis().setCoordinateSystem(labCoordinateSystem_0);

    rotateBodyFeature_1.getPosition().setCoordinateSystem(labCoordinateSystem_0);

    rotateBodyFeature_1.getAxis().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {0.0, 0.0, 1.0}));

    rotateBodyFeature_1.getPosition().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {0.0, 0.0, 0.0}));

    rotateBodyFeature_1.getAngle().setValue(-90.0);

    rotateBodyFeature_1.setCopyOption(0);

    rotateBodyFeature_1.setBodies(new NeoObjectVector(new Object[] {cadmodelerBody_0}));

    rotateBodyFeature_1.setBodyGroups(new NeoObjectVector(new Object[] {}));

    rotateBodyFeature_1.setCadFilters(new NeoObjectVector(new Object[] {}));

    rotateBodyFeature_1.markFeatureForEdit();

    cadModel_1.getFeatureManager().execute(rotateBodyFeature_1);

    scene_0.setTransparencyOverrideMode(SceneTransparencyOverride.USE_DISPLAYER_PROPERTY);

    cadmodelerBody_0.setPresentationName("Blade");

    currentView_0.setInput(new DoubleVector(new double[] {-0.5838359478103808, 0.603173997127873, 2.472074116787997}), new DoubleVector(new double[] {-0.5838359478103808, 0.603173997127873, 15.87765956441906}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.15718351445553772, 0.13231788047989632, 2.4555730274332364}), new DoubleVector(new double[] {0.15718351445553772, 0.13231788047989632, 4.095382339707756}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.29694990363190826, 0.33580130001625874, 2.096412511842016}), new DoubleVector(new double[] {0.29694990363190826, 0.33580130001625874, 7.232744746020117}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.29576926841138085, 0.3361047178153944, 2.152837482881735}), new DoubleVector(new double[] {0.07925964022759602, 0.39174669234999293, 7.283863318367215}), new DoubleVector(new double[] {2.284670798863465E-4, 0.9999412850156014, -0.010833943148359485}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.2972958572988078, 0.30165558719200153, 2.3111066480054436}), new DoubleVector(new double[] {0.6179457723424098, -1.920136909745955, 6.926920885490252}), new DoubleVector(new double[] {0.004982813891480509, 0.9011742790939672, 0.43342829887444095}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.45818461622755235, -0.5827029818442535, 4.029370766411429}), new DoubleVector(new double[] {0.6772927155873747, -2.100909313225295, 7.183471975001423}), new DoubleVector(new double[] {0.004982813891480509, 0.9011742790939672, 0.43342829887444095}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.19165780909089175, -0.29707029381456973, 4.222462057654908}), new DoubleVector(new double[] {0.27691617627406195, -0.8878279757025894, 5.449771863912836}), new DoubleVector(new double[] {0.004982813891480509, 0.9011742790939672, 0.43342829887444095}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.17562513176859845, -0.28503752680967254, 4.246553173326321}), new DoubleVector(new double[] {0.2516645574424512, -0.811916897448721, 5.341154628585744}), new DoubleVector(new double[] {0.004982813891480509, 0.9011742790939672, 0.43342829887444095}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.03361656140076946, 0.06291043910341024, 4.428562180174083}), new DoubleVector(new double[] {0.04242337237260846, -0.463972452171883, 5.523170949619203}), new DoubleVector(new double[] {0.004982813891480509, 0.9011742790939672, 0.43342829887444095}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.08067579089992515, 0.058975752447644136, 4.414653918358076}), new DoubleVector(new double[] {-0.030066043723807247, -0.29170064343580865, 5.143190495513463}), new DoubleVector(new double[] {0.004982813891480509, 0.9011742790939672, 0.43342829887444095}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.08067579089992515, 0.058975752447644136, 4.414653918358076}), new DoubleVector(new double[] {-0.030066043723807247, -0.29170064343580865, 5.143190495513463}), new DoubleVector(new double[] {0.004982813891480509, 0.9011742790939672, 0.43342829887444095}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.08067579089992515, 0.058975752447644136, 4.414653918358076}), new DoubleVector(new double[] {-0.030066043723807247, -0.29170064343580865, 5.143190495513463}), new DoubleVector(new double[] {0.004982813891480509, 0.9011742790939672, 0.43342829887444095}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.08067579089992515, 0.058975752447644136, 4.414653918358076}), new DoubleVector(new double[] {-0.030066043723807247, -0.29170064343580865, 5.143190495513463}), new DoubleVector(new double[] {0.004982813891480509, 0.9011742790939672, 0.43342829887444095}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.07924224487608222, 0.08254968091283663, 4.381799551271097}), new DoubleVector(new double[] {-0.1862140268545715, -0.49217669431682776, 4.94268027524714}), new DoubleVector(new double[] {-0.007323353110007187, 0.6991127100503656, 0.7149739765510773}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.08197048570284604, -0.01156786446531552, 4.529569815651446}), new DoubleVector(new double[] {6.093722562273141E-4, -0.27842096796736493, 5.290032998979291}), new DoubleVector(new double[] {-2.3788388905460995E-4, 0.9435826127678584, 0.33113712611762697}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.08236699284753998, -0.009752726542959427, 4.525177416577755}), new DoubleVector(new double[] {-0.030466294202317168, -0.3585842893385972, 5.2545326811010735}), new DoubleVector(new double[] {0.008771002551319972, 0.902337752755746, 0.4309404244973533}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.08236699284753998, -0.009752726542959427, 4.525177416577755}), new DoubleVector(new double[] {-0.030466294202317168, -0.3585842893385972, 5.2545326811010735}), new DoubleVector(new double[] {0.008771002551319972, 0.902337752755746, 0.4309404244973533}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.08236699284753998, -0.009752726542959427, 4.525177416577755}), new DoubleVector(new double[] {-0.030466294202317168, -0.3585842893385972, 5.2545326811010735}), new DoubleVector(new double[] {0.008771002551319972, 0.902337752755746, 0.4309404244973533}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.08236699284753998, -0.009752726542959427, 4.525177416577755}), new DoubleVector(new double[] {-0.030466294202317168, -0.3585842893385972, 5.2545326811010735}), new DoubleVector(new double[] {0.008771002551319972, 0.902337752755746, 0.4309404244973533}), 2.3015629593758664, 0, 30.0);

    Edge edge_4 = 
      ((Edge) cadModel_1.getEdgeByLocation(new NeoObjectVector(new Object[] {face_1, face_2})));

    cadModel_1.setEdgeNameAttributes(new NeoObjectVector(new Object[] {edge_4}), "TE_Face");

    currentView_0.setInput(new DoubleVector(new double[] {-0.16115653500290944, -1.0539474422166517E-4, 4.456029068927252}), new DoubleVector(new double[] {-0.08707277162706537, -0.4980323134709046, 5.497120696676376}), new DoubleVector(new double[] {0.008771002551319972, 0.902337752755746, 0.4309404244973533}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.2513125733932803, 0.31982284158575414, 4.619136064387562}), new DoubleVector(new double[] {-0.17744094964608034, -0.17667825821897787, 5.657246515408732}), new DoubleVector(new double[] {0.008771002551319972, 0.902337752755746, 0.4309404244973533}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.06867401933331665, 0.16006045061297025, 4.579171165592436}), new DoubleVector(new double[] {0.002346060583997084, -0.317275040317035, 5.577209159813687}), new DoubleVector(new double[] {0.008771002551319972, 0.902337752755746, 0.4309404244973533}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.3580550548860884, 0.05482804868085392, 4.975886489064205}), new DoubleVector(new double[] {-1.405808756098525, 0.1769777727887024, 5.316862275678234}), new DoubleVector(new double[] {0.17736893113701904, 0.9636478000846193, 0.19980785685100408}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.17189232176866354, 0.03695118797810576, 4.916007215267561}), new DoubleVector(new double[] {-1.3767181796778223, 0.4381960586969288, 5.3561206370639844}), new DoubleVector(new double[] {0.35784033568025203, 0.9235927191810646, 0.13757464605070926}), 2.3015629593758664, 0, 30.0);

    NamedFaces namedFaces_0 = 
      ((NamedFaces) cadmodelerBody_0.getNamedFacesManager().getObject("LE"));

    cadmodelerBody_0.getNamedFacesManager().delete(new NeoObjectVector(new Object[] {namedFaces_0}));

    NamedEdges namedEdges_0 = 
      ((NamedEdges) cadmodelerBody_0.getNamedEdgesManager().getObject("TE"));

    NamedEdges namedEdges_1 = 
      ((NamedEdges) cadmodelerBody_0.getNamedEdgesManager().getObject("TE_Face"));

    cadmodelerBody_0.getNamedEdgesManager().delete(new NeoObjectVector(new Object[] {namedEdges_0, namedEdges_1}));

    currentView_0.setInput(new DoubleVector(new double[] {-0.2616224298721407, -0.010492624924880841, 4.9874807007793835}), new DoubleVector(new double[] {-1.005556119092369, -0.864817397734135, 5.810734676848091}), new DoubleVector(new double[] {-0.28817639069931256, 0.782633306200713, 0.5517603427837698}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.25234523611551496, -0.05856878114258717, 4.98262870553379}), new DoubleVector(new double[] {-0.2878505262644451, -1.5296341279605012, 5.153779268601519}), new DoubleVector(new double[] {-0.5207705498731715, 0.11104833743446409, 0.8464433242325451}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.029550416097262333, -0.06898192756424465, 4.909606802565396}), new DoubleVector(new double[] {-0.06513805061530094, -1.5434589898587228, 5.081154300430144}), new DoubleVector(new double[] {-0.5207705498731715, 0.11104833743446409, 0.8464433242325451}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.04477015411928495, -0.1505026664544511, 4.884735408585154}), new DoubleVector(new double[] {-0.08959265821899455, -2.007601489252649, 5.100798896099057}), new DoubleVector(new double[] {-0.5207705498731715, 0.11104833743446409, 0.8464433242325451}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.028310109620295187, -0.13209946716772353, 4.969961552427947}), new DoubleVector(new double[] {0.018961864339931148, -0.5194186420798036, 5.015024061049011}), new DoubleVector(new double[] {-0.5207705498731715, 0.11104833743446409, 0.8464433242325451}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.02557888934838696, -0.15196913375372195, 4.974578089185274}), new DoubleVector(new double[] {-0.06730412756561055, -0.5086071846334816, 5.102335869311852}), new DoubleVector(new double[] {-0.4805163479485299, 0.4039885840537565, 0.7783940283098618}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.02557888934838696, -0.15196913375372195, 4.974578089185274}), new DoubleVector(new double[] {-0.06730412756561055, -0.5086071846334816, 5.102335869311852}), new DoubleVector(new double[] {-0.4805163479485299, 0.4039885840537565, 0.7783940283098618}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.02557888934838696, -0.15196913375372195, 4.974578089185274}), new DoubleVector(new double[] {-0.06730412756561055, -0.5086071846334816, 5.102335869311852}), new DoubleVector(new double[] {-0.4805163479485299, 0.4039885840537565, 0.7783940283098618}), 2.3015629593758664, 0, 30.0);

    cadModel_1.setFaceNameAttributes(new NeoObjectVector(new Object[] {face_1}), "TE");

    currentView_0.setInput(new DoubleVector(new double[] {-0.00245256155032314, -0.17650791533897217, 5.142647173600931}), new DoubleVector(new double[] {0.028334153129092723, 0.1257764217761905, 5.38721078886441}), new DoubleVector(new double[] {-0.22630436591502606, 0.6264851664922406, -0.74585700381038}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.14026021513500425, -0.11344898262409478, 4.952769007744961}), new DoubleVector(new double[] {0.1756976502118749, 0.2344992048593739, 5.234277032498862}), new DoubleVector(new double[] {-0.22630436591502606, 0.6264851664922406, -0.74585700381038}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.06126770511694418, -0.031681994563741836, 4.861647685826059}), new DoubleVector(new double[] {0.09670514019380352, 0.316266192919616, 5.14315571057987}), new DoubleVector(new double[] {-0.22630436591502606, 0.6264851664922406, -0.74585700381038}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.061410922928170825, -0.010135910189898609, 4.880323351462899}), new DoubleVector(new double[] {0.03046866006673901, 0.3181820665431736, 5.184987762743851}), new DoubleVector(new double[] {-0.22614107831517904, 0.651050605483764, -0.724564228897399}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.061410922928170825, -0.010135910189898609, 4.880323351462899}), new DoubleVector(new double[] {0.03046866006673901, 0.3181820665431736, 5.184987762743851}), new DoubleVector(new double[] {-0.22614107831517904, 0.651050605483764, -0.724564228897399}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.03569868605867635, 0.05394822538302205, 4.930601395964431}), new DoubleVector(new double[] {-0.19524032325849658, 0.37831972641079875, 5.138010634010388}), new DoubleVector(new double[] {-0.14784966333923963, 0.45592449573793375, -0.8776521698466878}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.03569868605867635, 0.05394822538302205, 4.930601395964431}), new DoubleVector(new double[] {-0.19524032325849658, 0.37831972641079875, 5.138010634010388}), new DoubleVector(new double[] {-0.14784966333923963, 0.45592449573793375, -0.8776521698466878}), 2.3015629593758664, 0, 30.0);

    Edge edge_5 = 
      ((Edge) cadModel_1.getEdgeByLocation(new NeoObjectVector(new Object[] {face_2, face_0})));

    cadModel_1.setEdgeNameAttributes(new NeoObjectVector(new Object[] {edge_5}), "LE");

    currentView_0.setInput(new DoubleVector(new double[] {0.024806831577728172, 0.03544099120783667, 4.924159273369309}), new DoubleVector(new double[] {-0.2601571431745701, 0.43569468807156053, 5.180089014510388}), new DoubleVector(new double[] {-0.14784966333923963, 0.45592449573793375, -0.8776521698466878}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {0.057193708426616356, 0.01454990006100805, 4.8794231919274464}), new DoubleVector(new double[] {-0.19730516374913426, -0.037825889406918195, 5.368719551311578}), new DoubleVector(new double[] {-0.6114488263879587, 0.7548859046086276, -0.2372290111517101}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.04949225811587987, -0.19037235817074952, 4.689959075242432}), new DoubleVector(new double[] {-0.844889281606454, -0.35406482049721694, 6.219179478192395}), new DoubleVector(new double[] {-0.6114488263879587, 0.7548859046086276, -0.2372290111517101}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.4374300662809855, -0.2871054003052539, 5.067871284229868}), new DoubleVector(new double[] {-1.9550490278257486, -0.700161798402944, 5.792024688004731}), new DoubleVector(new double[] {-0.420423049742254, 0.8025179413709372, -0.4233313277129307}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-0.697558359432767, -0.008782163814405122, 2.2994988165629167}), new DoubleVector(new double[] {-3.088294057125727, -0.6594782219919143, 3.44027224973629}), new DoubleVector(new double[] {-0.420423049742254, 0.8025179413709372, -0.4233313277129307}), 2.3015629593758664, 0, 30.0);
  }

  private void execute1() {

    Simulation simulation_0 = 
      getActiveSimulation();

    Scene scene_0 = 
      simulation_0.getSceneManager().getScene("3D-CAD View 1");

    CurrentView currentView_0 = 
      scene_0.getCurrentView();

    currentView_0.setInput(new DoubleVector(new double[] {-0.5146472285647652, 0.6154900578276122, 1.8054411200565197}), new DoubleVector(new double[] {-3.3575142446695363, -0.15826438231703205, 3.161955421970661}), new DoubleVector(new double[] {-0.420423049742254, 0.8025179413709372, -0.4233313277129307}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-2.1908599527257104, 0.025091581649634653, 0.6999033728827637}), new DoubleVector(new double[] {-2.856204428523739, -0.4189516952978518, -2.7166667251187366}), new DoubleVector(new double[] {0.10689024305827301, 0.9831053770463343, -0.14858766288473538}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-1.072583412163492, 0.26022637298190554, 0.8569944119960113}), new DoubleVector(new double[] {-1.8127775573109006, -0.23377071536000205, -2.9439316872147363}), new DoubleVector(new double[] {0.10689024305827301, 0.9831053770463343, -0.14858766288473538}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-1.072583412163492, 0.26022637298190554, 0.8569944119960113}), new DoubleVector(new double[] {-1.8127775573109006, -0.23377071536000205, -2.9439316872147363}), new DoubleVector(new double[] {0.10689024305827301, 0.9831053770463343, -0.14858766288473538}), 2.3015629593758664, 0, 30.0);

    currentView_0.setInput(new DoubleVector(new double[] {-1.072583412163492, 0.26022637298190554, 0.8569944119960113}), new DoubleVector(new double[] {-1.8127775573109006, -0.23377071536000205, -2.9439316872147363}), new DoubleVector(new double[] {0.10689024305827301, 0.9831053770463343, -0.14858766288473538}), 2.3015629593758664, 0, 30.0);

    CadModel cadModel_1 = 
      ((CadModel) simulation_0.get(SolidModelManager.class).getObject("3D-CAD Model 1"));

    FillFeature fillFeature_0 = 
      ((FillFeature) cadModel_1.getFeature("Fill Surface 1"));

    star.cadmodeler.Body cadmodelerBody_0 = 
      ((star.cadmodeler.Body) cadModel_1.getBody("Blade"));

    Face face_3 = 
      ((Face) fillFeature_0.getFaceByLocation(cadmodelerBody_0,new DoubleVector(new double[] {0.015569310543058767, -0.003665124222136544, 0.508000000000002})));

    cadModel_1.setFaceNameAttributes(new NeoObjectVector(new Object[] {face_3}), "Hub");

    currentView_0.setInput(new DoubleVector(new double[] {-1.3412839588562622, 0.18973177366539254, 0.553231389958922}), new DoubleVector(new double[] {-4.583466819707796, -0.5304595902623956, -1.8226909282746842}), new DoubleVector(new double[] {0.009742169547585007, 0.9531868702194959, -0.3022248841073298}), 2.3015629593758664, 0, 30.0);
  }
}
