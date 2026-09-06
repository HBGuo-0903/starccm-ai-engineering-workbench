// STAR-CCM+ macro: test_macro.java
// William Snell - last edited 2/01/2020
package macro;

import java.util.*;
import java.nio.file.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;
import star.flow.*;
import star.meshing.*;

public class post_macro extends StarMacro {

  public void execute() {
    execute0(); //Set Up simulation

    execute1(); //Get active simulation
    
  }

  private void execute0() {
      
      //Resolution (res x res) pixels
      Integer res = 4096;

//------------------------Initialise Simulation---------------------------------
    Simulation simulation_0 = 
      getActiveSimulation();

    simulation_0.getSceneManager().createScalarScene("Scalar Scene", "Outline", "Scalar");

    Scene scene_0 = 
      simulation_0.getSceneManager().getScene("Scalar Scene 1");

    scene_0.initializeAndWait();    
    
    PartDisplayer partDisplayer_0 = 
      ((PartDisplayer) scene_0.getDisplayerManager().getDisplayer("Outline 1"));

    partDisplayer_0.initialize();
   
    ScalarDisplayer scalarDisplayer_0 = 
      (ScalarDisplayer) scene_0.getDisplayerManager().getDisplayer("Scalar");
    
    scalarDisplayer_0.initialize();
    
   // scene_0.open();
    
    SceneUpdate sceneUpdate_1 = 
      scene_0.getSceneUpdate();
    
    HardcopyProperties hardcopyProperties_1 = 
      sceneUpdate_1.getHardcopyProperties();

    hardcopyProperties_1.setCurrentResolutionWidth(1086);

    hardcopyProperties_1.setCurrentResolutionHeight(646);

    scene_0.resetCamera();
    
    Region region_0 = 
      simulation_0.getRegionManager().getRegion("Region 1");
    
    //Set up scalar displayer 2 for plane section
    ScalarDisplayer scalarDisplayer_1 = 
      scene_0.getDisplayerManager().createScalarDisplayer("Section Scalar");

    scalarDisplayer_1.initialize();

    
    //Setup Viridis
    UserLookupTable userLookupTable_0 = 
      simulation_0.get(LookupTableManager.class).createLookupTable();

    userLookupTable_0.setPresentationName("Viridis");   

    userLookupTable_0.setColorMap(new ColorMap(new DoubleVector(new double[] {0.0, 0.26666666666666666, 0.00392156862745098, 0.32941176470588235, 0.055556, 0.2823529411764706, 0.08235294117647059, 0.403921568627451, 0.11111, 0.2823529411764706, 0.14901960784313725, 0.4666666666666667, 0.166667, 0.27058823529411763, 0.21568627450980393, 0.5058823529411764, 0.222222, 0.25098039215686274, 0.2784313725490196, 0.5333333333333333, 0.277778, 0.2235294117647059, 0.33725490196078434, 0.5490196078431373, 0.333333, 0.17647058823529413, 0.4392156862745098, 0.5568627450980392, 0.3888889, 0.1568627450980392, 0.49019607843137253, 0.5568627450980392, 0.444444, 0.13725490196078433, 0.5411764705882353, 0.5529411764705883, 0.5, 0.12156862745098039, 0.5882352941176471, 0.5450980392156862, 0.555556, 0.12549019607843137, 0.6392156862745098, 0.5294117647058824, 0.611111, 0.1607843137254902, 0.6862745098039216, 0.4980392156862745, 0.6666667, 0.23529411764705882, 0.7333333333333333, 0.4588235294117647, 0.7222222, 0.3333333333333333, 0.7764705882352941, 0.403921568627451, 0.777778, 0.45098039215686275, 0.8156862745098039, 0.3333333333333333, 0.8333333, 0.5843137254901961, 0.8470588235294118, 0.25098039215686274, 0.8888889, 0.7215686274509804, 0.8705882352941177, 0.1607843137254902, 0.944444, 0.8627450980392157, 0.8901960784313725, 0.09803921568627451, 1.0, 0.9921568627450981, 0.9058823529411765, 0.1450980392156863}), new DoubleVector(new double[] {0.0, 1.0, 1.0, 1.0}), 1));

    
    //Setup Legends
    Legend legend_0 = 
      scalarDisplayer_0.getLegend();
        
    legend_0.setLookupTable(userLookupTable_0);
    
    Legend legend_1 = 
      scalarDisplayer_1.getLegend();
    
    legend_1.setLookupTable(userLookupTable_0);
    

    //Setup Planes
        //Plane 0
    PlaneSection planeSection_0 = 
      (PlaneSection) simulation_0.getPartManager().createImplicitPart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 0.0, 1.0}), new DoubleVector(new double[] {0.0, 0.0, 0.0}), 0, 1, new DoubleVector(new double[] {0.0}));
    
    LabCoordinateSystem labCoordinateSystem_0 = 
      simulation_0.getCoordinateSystemManager().getLabCoordinateSystem();
      
    planeSection_0.setCoordinateSystem(labCoordinateSystem_0);

    planeSection_0.getInputParts().setQuery(null);
    
    Units units_0 = 
      simulation_0.getUnitsManager().getPreferredUnits(new IntVector(new int[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

    planeSection_0.getInputParts().setObjects(region_0);
    
    planeSection_0.setValueMode(ValueMode.SINGLE);
    
    scalarDisplayer_1.getVisibleParts().addParts(planeSection_0);
    
    scalarDisplayer_1.getHiddenParts().addParts();
    
     Coordinate coordinate_0 = 
      planeSection_0.getOriginCoordinate();

    coordinate_0.setValue(new DoubleVector(new double[] {0, 0.0, 0.0}));

    coordinate_0.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {0, 0.0, -0.4}));

    coordinate_0.setCoordinateSystem(labCoordinateSystem_0);

    Coordinate coordinate_1 = 
      planeSection_0.getOrientationCoordinate();

    coordinate_1.setValue(new DoubleVector(new double[] {0.0, 1.0, 0.0}));

    coordinate_1.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {0.0, 1.0, 0.0}));

    coordinate_1.setCoordinateSystem(labCoordinateSystem_0);
        //Plane section 1
    PlaneSection planeSection_1 = 
      (PlaneSection) simulation_0.getPartManager().createImplicitPart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 0.0, 1.0}), new DoubleVector(new double[] {0.0, 0.0, 0.0}), 0, 1, new DoubleVector(new double[] {0.0}));
      
    planeSection_1.setCoordinateSystem(labCoordinateSystem_0);

    planeSection_1.getInputParts().setQuery(null);

    planeSection_1.getInputParts().setObjects(region_0);
    
    planeSection_1.setValueMode(ValueMode.SINGLE);
    
    SingleValue singleValue_1 = 
                planeSection_1.getSingleValue();
    
    singleValue_1.setValue(-0.3);
    
    scalarDisplayer_1.getVisibleParts().addParts(planeSection_1);
    
    scalarDisplayer_1.getHiddenParts().addParts();
    
     Coordinate coordinate_2 = 
      planeSection_1.getOriginCoordinate();

    coordinate_2.setValue(new DoubleVector(new double[] {0, 0.0, 0.0}));

    coordinate_2.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {0, 0.0, 0.0}));

    coordinate_2.setCoordinateSystem(labCoordinateSystem_0);

    Coordinate coordinate_3 = 
      planeSection_0.getOrientationCoordinate();

    coordinate_3.setValue(new DoubleVector(new double[] {0.0, 1.0, 0.0}));

    coordinate_3.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {0.0, 1.0, 0.0}));

    coordinate_3.setCoordinateSystem(labCoordinateSystem_0);
    
    CurrentView currentView_1 = 
      scene_0.getCurrentView();

    currentView_1.setInput(new DoubleVector(new double[] {0.06575538879698407, -0.04591674142788449, 0.8876203332727922}), new DoubleVector(new double[] {0.06575538879698407, 91.24038404635962, 0.8876203332727922}), new DoubleVector(new double[] {0.0, 0.0, 1.0}), 2.20457867416491, 1);
 
//------------------------Set up Field Functions--------------------------------
    TotalPressureCoefficientFunction totalPressureCoefficientFunction_0 = 
      ((TotalPressureCoefficientFunction) simulation_0.getFieldFunctionManager().getFunction("TotalPressureCoefficient"));
    
    PrimitiveFieldFunction primitiveFieldFunction_0 = 
      ((PrimitiveFieldFunction) simulation_0.getFieldFunctionManager().getFunction("Pressure"));
    
    PrimitiveFieldFunction primitiveFieldFunction_1 = 
      ((PrimitiveFieldFunction) simulation_0.getFieldFunctionManager().getFunction("Velocity"));
    
    VectorMagnitudeFieldFunction vectorMagnitudeFieldFunction_0 = 
      ((VectorMagnitudeFieldFunction) primitiveFieldFunction_1.getMagnitudeFunction());
    //find the transform after the identity - should correspond to the symmetry plane
    Collection<Object> functions = new ArrayList();
    functions.add(totalPressureCoefficientFunction_0);
    functions.add(primitiveFieldFunction_0);
    functions.add(primitiveFieldFunction_1);
    functions.add(vectorMagnitudeFieldFunction_0);
    
    try{
    Collection<VisTransform> transforms = simulation_0.getTransformManager().getObjects();
    Iterator<VisTransform> iter = 
            transforms.iterator();
    VisTransform sR = iter.next();
    
    
    SymmetricRepeat symmetricRepeat_0 = null;
    if (sR.getMenuPresentationName().matches("Identity")){
        symmetricRepeat_0 = (SymmetricRepeat) iter.next();
        simulation_0.println(symmetricRepeat_0.getPresentationName());
    } else {
        symmetricRepeat_0 = (SymmetricRepeat) sR;
    }
    
    scalarDisplayer_0.setVisTransform(symmetricRepeat_0);
    scalarDisplayer_1.setVisTransform(symmetricRepeat_0);
    
    simulation_0.println("Applied symmetry transform over face " + symmetricRepeat_0.getPresentationName());
    
    
    } catch (Exception e) {
        simulation_0.println("Couldn't apply symmetry transform. "
                + "No symmetry plane called \"sym 1\" found. If you are not using a symmetry plane,"
                + " ignore this message.");
    }
    //Get all surfaces associated with the "car" part in the subtract
    MeshOperationPart meshOperationPart_0 = 
      ((MeshOperationPart) simulation_0.get(SimulationPartManager.class).getPart("Subtract"));
    
    Collection<PartSurface> mySurfaces;
      mySurfaces = meshOperationPart_0.getPartSurfaceManager().getPartSurfaces();
      
    for (Iterator<PartSurface> it = mySurfaces.iterator(); it.hasNext();) {
            PartSurface iSurface = it.next();
            String iName = iSurface.getPresentationName();
            //Find all surfaces with the "part" prefix
            if (iName.startsWith("car")){
                scalarDisplayer_0.getInputParts().addObjects(iSurface);                
            }
                
            }
    
//-----------------------------Plane Sweep--------------------------------------


    scalarDisplayer_0.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_0); // set field function on surface to pressure
    //loop through field functions. As the different field functions have different types
    //I couldn't find a better way to do it then this abomination :(
    
    for (Integer i = 0; i < 3; i++){
        scalarDisplayer_0.setVisibilityOverrideMode(DisplayerVisibilityOverride.USE_PART_PROPERTY);
        String name = "";
        if (i == 0){
            scalarDisplayer_1.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_0);
            name = "Pressure";            
        } else if (i == 1){
            scalarDisplayer_1.getScalarDisplayQuantity().setFieldFunction(totalPressureCoefficientFunction_0);
            name = "TotalPressureCoeff";
        } else if (i == 2){
            scalarDisplayer_1.getScalarDisplayQuantity().setFieldFunction(vectorMagnitudeFieldFunction_0);
            name = "VelocityMagnitude";
        }
        //create folder structure
        try {   
            Files.createDirectory(Paths.get("/output/"+name));
            Files.createDirectory(Paths.get("/output/"+name+"/sweep/top"));
            Files.createDirectory(Paths.get("/output/"+name+"/sweep/front"));
        } catch (Exception e) {
            simulation_0.println("Couldn't create directory.");
        };
         
        
        //scalarDisplayer_1.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_0);
        
        //ISO
        scene_0.setViewOrientation(new DoubleVector(new double[] {1.0, 1.0, 1.0}), new DoubleVector(new double[] {0.0, 1.0, 0.0}));
        scene_0.printAndWait(resolvePath("\\output\\"+name+"\\ISO.png"), 1, res, res, true, true);
        //Front
        scene_0.setViewOrientation(new DoubleVector(new double[] {0.0, 0.0, 1.0}), new DoubleVector(new double[] {0.0, 1.0, 0.0}));
        scene_0.printAndWait(resolvePath("\\output\\"+name+"\\Front.png"), 1, res, res, true, true);
        //Top
        scene_0.setViewOrientation(new DoubleVector(new double[] {0.0, 1.0, 0.0}), new DoubleVector(new double[] {1.0, 0.0, 0.0}));
        scene_0.printAndWait(resolvePath("\\output\\"+name+"\\Top.png"), 1, res, res, true, true);
        //Side
            //Move plane to side view
        coordinate_3.setValue(new DoubleVector(new double[] {1.0, 0.0, 0.0}));

        coordinate_3.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {1.0, 0.0, 0.0}));
            
        scene_0.setViewOrientation(new DoubleVector(new double[] {1.0, 0.0, 0.0}), new DoubleVector(new double[] {0.0, 1.0, 0.0}));
        scene_0.printAndWait(resolvePath("\\output\\"+name+"\\Side.png"), 1, res, res, true, true);
            //Move plane back
        coordinate_3.setValue(new DoubleVector(new double[] {0.0, 1.0, 0.0}));

        coordinate_3.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {0.0, 1.0, 0.0}));
        
        //Top Sweep
        scalarDisplayer_0.setVisibilityOverrideMode(DisplayerVisibilityOverride.HIDE_ALL_PARTS);   
        scene_0.setViewOrientation(new DoubleVector(new double[] {0.0, 1.0, 0.0}), new DoubleVector(new double[] {1.0, 0.0, 0.0}));
        for (Integer j = 0; j < 13; j++){
                                  
            SingleValue singleValue_0 = 
                planeSection_0.getSingleValue();

            singleValue_0.getValueQuantity().setValue(j*0.1+0.05);

            singleValue_0.getValueQuantity().setUnits(units_0);
            
            //Top
            scene_0.printAndWait(resolvePath("\\output\\"+name+"\\sweep\\top\\Top" + Integer.toString(j) +".png"), 1, res, res, true, true);
            
       
        }
        SingleValue singleValue_0 = 
                planeSection_0.getSingleValue();        
        //Set to front view
        scene_0.setViewOrientation(new DoubleVector(new double[] {0.0, 0.0, 1.0}), new DoubleVector(new double[] {0.0, 1.0, 0.0}));
        singleValue_0.getValueQuantity().setValue(0.0);
        
        for (Integer h = 0; h < 15; h++){  
            
            singleValue_1.setValue(-0.3+h*0.2);
            //Front
            scene_0.printAndWait(resolvePath("\\output\\"+name+"\\sweep\\front\\Front" + Integer.toString(h) +".png"), 1, res, res, true, true);
        }
        singleValue_1.setValue(-0.3);
        
        
    }       
   
    
    
}
private void execute1() {

  Simulation simulation_0 = 
    getActiveSimulation();

}
}