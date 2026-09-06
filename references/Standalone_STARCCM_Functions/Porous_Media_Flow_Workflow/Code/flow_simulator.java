/*
Pore microstructural and flow modelling of rocks.
Generating surface files of pore microstructures and simulating fluid flow through the pore microstructures.

Investigators: Aaron Alexander, Olubukola Ishola (olubukola.ishola@okstate.edu), Javier Vilcaez

Paper 1: Statistical and neural network analysis of the relationship between the stochastic nature of pore connectivity and flow properties of heterogeneous rocks.
DOI: https://doi.org/10.1016/j.jngse.2022.104719

Paper 2: Machine learning modeling of permeability in 3D heterogeneous porous media using a novel stochastic pore-scale simulation approach.
DOI: https://doi.org/10.1016/j.fuel.2022.124044

Paper 3: Augmenting Xray micro-CT data with MICP data for high resolution pore-microstructural and flow modelling of carbonate rocks.
DOI:

This STAR CCM+ java code takes in two csv files (pore bodies and pore throats), use them to generate pore microstructures, and simulate fluid flow through it. The output of this script is a csv file of flow properties through a set number of iterations.
*/

// Loading libraries.
package macro;
import java.util.*;
import star.common.*;
import star.base.neo.*;
import star.vis.*;
import star.cadmodeler.*;
import star.meshing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.JFileChooser;
import java.lang.Math;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import star.resurfacer.*;
import star.segregatedflow.*;
import star.material.*;
import star.flow.*;
import star.metrics.*;
import star.base.report.*;
import star.energy.*;
import star.trimmer.*;
import star.prismmesher.*;
import star.dualmesher.*;

public class flow_simulator extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {
    /*Set up parameters.*/
    int starting = 1; // Suffix of the first data in sequence, note the number is the suffix of the pore_bodies/pore_throats file name.
    int finishing = 1; // Suffix of the last data in sequence, note the number is the suffix of the pore_bodies/pore_throats file name.
  
    for (int count=starting;count<=finishing;count++){ // Iterating through dataset

      /*Set up 3D model.*/
      // Simulation simulation_0 = getActiveSimulation(); // Activate if its only one simulation and needing visual output.
      Simulation simulation_0 = new Simulation("Star.sim"); // New simulation, activate if the program is beeen run without visual display.
      Scene scene_0 = simulation_0.getSceneManager().createScene("3D-CAD View");
      scene_0.initializeAndWait();
      Units units_0 = simulation_0.getUnitsManager().getPreferredUnits(new IntVector(new int[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));
      LabCoordinateSystem labCoordinateSystem_0 = simulation_0.getCoordinateSystemManager().getLabCoordinateSystem();

      try {
        simulation_0.println("Loading files.");

        /*
        There are two files required to implement this code and they are both expected from the pore_microstructural_generator.
        1. pore_bodies_*: Colum names: [X, Y, Z, pore body radius (microns), half domain length (microns), number of branches]. The number of rows is the number of pores.
        2. pore_throats_*: Colum names: [X_1, Y_1, Z_1, X_2, Y_2, Z_2, pore throat radius (microns)].
        3. Star.sim: This file is in the root folder, please not that the software requires the file to be saved in the sever working directory of your STAR CCM+ setup.
        */

        String pore_bodies_file = "./Data/pore_bodies_" + count + ".csv"; // This is the pore_bodies output from  pore_microstructural_generator.
        String pore_throats_file = "./Data/pore_throats_" + count + ".csv"; // This is the pore_throats output from  pore_microstructural_generator.

        File checker = new File("simulation_" + count + ".csv");
        if(checker.exists()){
            simulation_0.println("Existing simulation.");
            continue;
        }
        else{
            simulation_0.println("New simulation.");
        }

        FileTable pore_bodies_fileTable = ((FileTable) simulation_0.getTableManager().createFromFile(resolvePath(pore_bodies_file)));
        FileTable pore_throats_fileTable = ((FileTable) simulation_0.getTableManager().createFromFile(resolvePath(pore_throats_file)));
        int num_of_pore_bodies = pore_bodies_fileTable.getNumRows();
        int pore_bodies_columns = pore_bodies_fileTable.getNumColumns();
        int num_of_pore_throats = pore_throats_fileTable.getNumRows();
        int pore_throats_columns = pore_throats_fileTable.getNumColumns();
        double [][] pore_bodies_array = new double[num_of_pore_bodies][pore_bodies_columns];
        double [][] pore_throats_array = new double[num_of_pore_throats][pore_throats_columns];
        ArrayList<Integer> pore_bodies_element_list = new ArrayList<Integer>();
        ArrayList<Integer> pore_throats_element_list = new ArrayList<Integer>();
        
        /*Imports the sphere locations and radii from text file for flow domain.*/
        simulation_0.println("Importing the sphere locations and radii from text file for flow domain.");
        for (int i=0; i<num_of_pore_bodies; i++) {

          Object position_x = pore_bodies_fileTable.getTableDataItem(i, 0);
          Object position_y = pore_bodies_fileTable.getTableDataItem(i, 1);
          Object position_z = pore_bodies_fileTable.getTableDataItem(i, 2);
          Object radiusobj = pore_bodies_fileTable.getTableDataItem(i, 3);

          Double xpos = new Double(position_x.toString());
          Double ypos = new Double(position_y.toString());
          Double zpos = new Double(position_z.toString());
          Double radius = new Double(radiusobj.toString());

          /*
          ######################################
          ###### Important Read Me ##############
          ######################################
        
          All the data imported are in microns. However, STAR CCM+ in this code is in S.I unit which is meters. So we need to divide the micron numbers by 1,000,000.
          We did this in two steps because the code kept crashing if we go directly.
          First step: We divided by 100, so you will notice that when all the data from the loaded files were first used.
          Second step: That leaves a second division by 10000 to arrive at the correct convertion of " / 1000000". 
          The second division by 10000 was applied at a scale tool in star CCM+ later in the code before meshing and flow simulation. So all dimesnsions were probably scale at that point.
          
          It is also important to note that all the output files are in S.I units and permeability already converted to mD.
          */
          pore_bodies_array[i][0] = xpos /100;
          pore_bodies_array[i][1] = ypos /100;
          pore_bodies_array[i][2] = zpos /100;
          pore_bodies_array[i][3] = radius /100;
          pore_bodies_element_list.add(i);
        }

        /*Imports the sphere locations and radii from text file for flow domain.*/
        simulation_0.println("Importing the sphere locations and radii from text file for flow domain.");
        for (int i=0; i<num_of_pore_throats; i++) {
          Object position_x1 = pore_throats_fileTable.getTableDataItem(i, 0);
          Object position_y1 = pore_throats_fileTable.getTableDataItem(i, 1);
          Object position_z1 = pore_throats_fileTable.getTableDataItem(i, 2);
          Object position_x2 = pore_throats_fileTable.getTableDataItem(i, 3);
          Object position_y2 = pore_throats_fileTable.getTableDataItem(i, 4);
          Object position_z2 = pore_throats_fileTable.getTableDataItem(i, 5);
          Object radiusobj2 = pore_throats_fileTable.getTableDataItem(i, 6);
          
          Double xpos1 = new Double(position_x1.toString());
          Double ypos1 = new Double(position_y1.toString());
          Double zpos1 = new Double(position_z1.toString());
          Double xpos2 = new Double(position_x2.toString());
          Double ypos2 = new Double(position_y2.toString());
          Double zpos2 = new Double(position_z2.toString());
          Double radius2 = new Double(radiusobj2.toString());

          pore_throats_array[i][0] = xpos1 /100; 
          pore_throats_array[i][1] = ypos1 /100;
          pore_throats_array[i][2] = zpos1 /100;
          pore_throats_array[i][3] = xpos2 /100;
          pore_throats_array[i][4] = ypos2 /100;
          pore_throats_array[i][5] = zpos2 /100;
          pore_throats_array[i][6] = radius2 /100;
          pore_throats_element_list.add(i);
        }

        Object area_of_interest = pore_bodies_fileTable.getTableDataItem(0, 4);
        Double half_domain_length = new Double(area_of_interest.toString());
        half_domain_length = half_domain_length / 100;
        MeshPartFactory meshPartFactory_0 = simulation_0.get(MeshPartFactory.class);

        /*Create Spheres for pore bodies.*/
        simulation_0.println("Creating Spheres for flow domain.");
        SimpleSpherePart[] SphereArray = new SimpleSpherePart[num_of_pore_bodies];
        for (int i=0; i<num_of_pore_bodies; i++) {
          if (!Double.isNaN(pore_bodies_array[i][0])){
            SphereArray[i] = meshPartFactory_0.createNewSpherePart(simulation_0.get(SimulationPartManager.class));
            SphereArray[i].setDoNotRetessellate(true);
            SphereArray[i].setCoordinateSystem(labCoordinateSystem_0);
            SphereArray[i].getOrigin().setCoordinateSystem(labCoordinateSystem_0);
            SphereArray[i].getOrigin().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {pore_bodies_array[i][0], pore_bodies_array[i][1], pore_bodies_array[i][2]}));
            SphereArray[i].getRadius().setUnits(units_0);
            SphereArray[i].getRadius().setValue(pore_bodies_array[i][3]);
            SphereArray[i].getTessellationDensityOption().setSelected(TessellationDensityOption.Type.FINE);
            SphereArray[i].rebuildSimpleShapePart();
            SphereArray[i].setDoNotRetessellate(false);
            SphereArray[i].setPresentationName("Sphere "+i);
          }
        }

        /*Unite spheres.*/
        simulation_0.println("Uniting spheres.");
        ArrayList<MeshPart> SpheresToUnite = new ArrayList<MeshPart>();
        CadPart[] UnitedSpheres = new CadPart[num_of_pore_bodies];
        MeshActionManager meshActionManager_0 = simulation_0.get(MeshActionManager.class);
        for (int j=0; j<pore_bodies_element_list.size(); j++){
          SpheresToUnite.add((SimpleSpherePart) simulation_0.get(SimulationPartManager.class).getPart("Sphere "+Integer.toString(j)));
        }
        UnitedSpheres[0] = (CadPart) meshActionManager_0.uniteParts(SpheresToUnite, "CAD");
        UnitedSpheres[0].setPresentationName("Pores");
        simulation_0.get(SimulationPartManager.class).removeParts(SpheresToUnite);

          /*Create Cylinders for pore throats.*/
          simulation_0.println("Creating Cylinders for pore throats.");
          SimpleCylinderPart[] CylinderArray = new SimpleCylinderPart[num_of_pore_throats];
          for (int i=0; i<num_of_pore_throats; i++) {
            if (!Double.isNaN(pore_throats_array[i][0])){
              CylinderArray[i] = meshPartFactory_0.createNewCylinderPart(simulation_0.get(SimulationPartManager.class));
              CylinderArray[i].setDoNotRetessellate(true);
              CylinderArray[i].setCoordinateSystem(labCoordinateSystem_0);
              CylinderArray[i].getStartCoordinate().setCoordinateSystem(labCoordinateSystem_0);
              CylinderArray[i].getStartCoordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {pore_throats_array[i][0], pore_throats_array[i][1], pore_throats_array[i][2]}));
              CylinderArray[i].getEndCoordinate().setCoordinateSystem(labCoordinateSystem_0);
              CylinderArray[i].getEndCoordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {pore_throats_array[i][3], pore_throats_array[i][4], pore_throats_array[i][5]}));
              CylinderArray[i].getRadius().setUnits(units_0);
              CylinderArray[i].getRadius().setValue(pore_throats_array[i][6]);
              CylinderArray[i].getTessellationDensityOption().setSelected(TessellationDensityOption.Type.FINE);
              CylinderArray[i].rebuildSimpleShapePart();
              CylinderArray[i].setDoNotRetessellate(false);
              CylinderArray[i].setPresentationName("Cylinder "+i);
            }
          }
  
          /*Unite cylinders.*/
          simulation_0.println("uniting cylinders.");
          ArrayList<MeshPart> CylindersToUnite = new ArrayList<MeshPart>();
          CadPart[] UnitedCylinders = new CadPart[num_of_pore_throats];
          MeshActionManager meshActionManager_2 = simulation_0.get(MeshActionManager.class);
          for (int j=0; j<pore_throats_element_list.size(); j++){
            CylindersToUnite.add((SimpleCylinderPart) simulation_0.get(SimulationPartManager.class).getPart("Cylinder "+Integer.toString(j)));
          }
          UnitedCylinders[0] = (CadPart) meshActionManager_2.uniteParts(CylindersToUnite, "CAD");
          UnitedCylinders[0].setPresentationName("Skeleton");
          simulation_0.get(SimulationPartManager.class).removeParts(CylindersToUnite);

        /*Create boxes to bound flow domain.*/
        simulation_0.println("Creating in-out bounding boxes.");
        SimpleBlockPart simpleBlockPart_0 = meshPartFactory_0.createNewBlockPart(simulation_0.get(SimulationPartManager.class));
        simpleBlockPart_0.setDoNotRetessellate(true);
        simpleBlockPart_0.setCoordinateSystem(labCoordinateSystem_0);
        simpleBlockPart_0.getCorner1().setCoordinateSystem(labCoordinateSystem_0);
        simpleBlockPart_0.getCorner1().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {(-10*half_domain_length), (-1*half_domain_length), (-1*half_domain_length)})); // inlet and outlet boxes were made to be 10 times longer in flow direction (x), to help converge faster and less chnace of crashing. other directions are same as the rock domain (cube)
        simpleBlockPart_0.getCorner2().setCoordinateSystem(labCoordinateSystem_0);
        simpleBlockPart_0.getCorner2().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {(-1*half_domain_length), half_domain_length, half_domain_length}));
        simpleBlockPart_0.rebuildSimpleShapePart();
        simpleBlockPart_0.setDoNotRetessellate(false);
        scene_0.setTransparencyOverrideMode(SceneTransparencyOverride.USE_DISPLAYER_PROPERTY);
        simpleBlockPart_0.setPresentationName("Inlet");
        SimpleBlockPart simpleBlockPart_1 = meshPartFactory_0.createNewBlockPart(simulation_0.get(SimulationPartManager.class));
        simpleBlockPart_1.setDoNotRetessellate(true);
        simpleBlockPart_1.setCoordinateSystem(labCoordinateSystem_0);
        simpleBlockPart_1.getCorner1().setCoordinateSystem(labCoordinateSystem_0);
        simpleBlockPart_1.getCorner1().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {(1*half_domain_length), (-1*half_domain_length), (-1*half_domain_length)}));
        simpleBlockPart_1.getCorner2().setCoordinateSystem(labCoordinateSystem_0);
        simpleBlockPart_1.getCorner2().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {(10*half_domain_length), (half_domain_length), (half_domain_length)})); // inlet and outlet boxes were made to be 10 times longer in flow direction (x), to help converge faster and less chnace of crashing. other directions are same as the rock domain (cube)
        simpleBlockPart_1.rebuildSimpleShapePart();
        simpleBlockPart_1.setDoNotRetessellate(false);
        simpleBlockPart_1.setPresentationName("Outlet");

        // Identifying in-out faces
        simulation_0.println("Identifying in-out faces, uniting the boxes to pores, adjusting to proper scale.");
        CadPart cadPart_1 = (CadPart) meshActionManager_0.uniteParts(new NeoObjectVector(new Object[] {simpleBlockPart_0, simpleBlockPart_1}), "CAD");
        cadPart_1.setPresentationName("In-Out-Box");
        simulation_0.get(SimulationPartManager.class).removeParts(new NeoObjectVector(new Object[] {simpleBlockPart_0, simpleBlockPart_1}));
        simulation_0.get(SolidModelManager.class).createSolidModelForCadParts(scene_0, new NeoObjectVector(new Object[] {cadPart_1}));
        CadModel cadModel_0 = ((CadModel) simulation_0.get(SolidModelManager.class).getObject("3D-CAD Model 1"));
        cadModel_0.resetSystemOptions();
        scene_0.open();
        scene_0.setAdvancedRenderingEnabled(false);
        SceneUpdate sceneUpdate_0 = scene_0.getSceneUpdate();
        HardcopyProperties hardcopyProperties_0 = sceneUpdate_0.getHardcopyProperties();
        hardcopyProperties_0.setCurrentResolutionWidth(25);
        hardcopyProperties_0.setCurrentResolutionHeight(25);
        hardcopyProperties_0.setCurrentResolutionWidth(966);
        hardcopyProperties_0.setCurrentResolutionHeight(647);
        scene_0.resetCamera();
        CurrentView currentView_0 = scene_0.getCurrentView();
        currentView_0.setInput(new DoubleVector(new double[] {-9.407612183272644, -0.07675402546466792, 20.338499346731982}), new DoubleVector(new double[] {-120.29917380336167, -0.9814866588461744, 114.55956366996998}), new DoubleVector(new double[] {-0.002875843499474591, 0.9999765368128618, 0.006217342513018224}), 37.66272042697116, 0, 30.0);
        currentView_0.setInput(new DoubleVector(new double[] {-9.407612183272644, -0.07675402546466792, 20.338499346731982}), new DoubleVector(new double[] {-120.29917380336167, -0.9814866588461744, 114.55956366996998}), new DoubleVector(new double[] {-0.002875843499474591, 0.9999765368128618, 0.006217342513018224}), 37.66272042697116, 0, 30.0);
        ImportCadFileFeature importCadFileFeature_0 = ((ImportCadFileFeature) cadModel_0.getFeature("ImportCad 1"));
        star.cadmodeler.Body cadmodelerBody_0 = ((star.cadmodeler.Body) importCadFileFeature_0.getBodyByIndex(1));
        Face face_0 = ((Face) cadmodelerBody_0.getFace("Block Surface 3"));
        cadModel_0.setFaceNameAttributes(new NeoObjectVector(new Object[] {face_0}), "Inlet");
        currentView_0.setInput(new DoubleVector(new double[] {-5.690731263063027, -0.9374884920926774, 43.58337781403836}), new DoubleVector(new double[] {123.51612393288993, -5.955272033127375, 113.7182882459666}), new DoubleVector(new double[] {0.0032476419406032926, 0.9978533096292221, 0.06540814386480341}), 37.66272042697116, 0, 30.0);
        currentView_0.setInput(new DoubleVector(new double[] {-5.690731263063027, -0.9374884920926774, 43.58337781403836}), new DoubleVector(new double[] {123.51612393288993, -5.955272033127375, 113.7182882459666}), new DoubleVector(new double[] {0.0032476419406032926, 0.9978533096292221, 0.06540814386480341}), 37.66272042697116, 0, 30.0);
        currentView_0.setInput(new DoubleVector(new double[] {-5.690731263063027, -0.9374884920926774, 43.58337781403836}), new DoubleVector(new double[] {123.51612393288993, -5.955272033127375, 113.7182882459666}), new DoubleVector(new double[] {0.0032476419406032926, 0.9978533096292221, 0.06540814386480341}), 37.66272042697116, 0, 30.0);
        star.cadmodeler.Body cadmodelerBody_1 = ((star.cadmodeler.Body) importCadFileFeature_0.getBodyByIndex(2));
        Face face_1 = ((Face) cadmodelerBody_1.getFace("Block Surface 6"));
        cadModel_0.setFaceNameAttributes(new NeoObjectVector(new Object[] {face_1}), "Outlet");
        cadModel_0.update();
        simulation_0.get(SolidModelManager.class).endEditCadModel(cadModel_0);
        simulation_0.getSceneManager().deleteScenes(new NeoObjectVector(new Object[] {scene_0}));

        // Combining all the components into a network.
        simulation_0.println("Combining all the components into a network.");
        SolidModelPart solidModelPart_1 = ((SolidModelPart) simulation_0.get(SimulationPartManager.class).getPart("In-Out-Box"));
        simulation_0.get(SimulationPartManager.class).updateParts(new NeoObjectVector(new Object[] {solidModelPart_1}));
        CadPart cadPart_2 = ((CadPart) simulation_0.get(SimulationPartManager.class).getPart("Pores"));
        SolidModelPart solidModelPart_0 = ((SolidModelPart) simulation_0.get(SimulationPartManager.class).getPart("In-Out-Box"));
        CadPart cadPart_5 = ((CadPart) simulation_0.get(SimulationPartManager.class).getPart("Pores"));
        CadPart cadPart_55 = ((CadPart) simulation_0.get(SimulationPartManager.class).getPart("Skeleton"));
        CadPart cadPart_3 = (CadPart) meshActionManager_0.uniteParts(new NeoObjectVector(new Object[] {cadPart_5, cadPart_55,solidModelPart_0}), "CAD");
        cadPart_3.setPresentationName("Flow Domain");
        LabCoordinateSystem labCoordinateSystem_1 = simulation_0.getCoordinateSystemManager().getLabCoordinateSystem();

        // Assigning parts to region.
        simulation_0.println("Assigning parts to region.");
        simulation_0.getRegionManager().newRegionsFromParts(new NeoObjectVector(new Object[] {cadPart_3}), "OneRegionPerPart", null, "OneBoundaryPerPartSurface", null, "OneFeatureCurve", null, RegionManager.CreateInterfaceMode.BOUNDARY);
        Region region_1 = simulation_0.getRegionManager().createEmptyRegion();
        region_1.setPresentationName("Region");
        Boundary boundary_2 = region_1.getBoundaryManager().getBoundary("Default");
        region_1.getBoundaryManager().removeBoundaries(new NeoObjectVector(new Object[] {boundary_2}));
        FeatureCurve featureCurve_0 = ((FeatureCurve) region_1.getFeatureCurveManager().getObject("Default"));
        region_1.getFeatureCurveManager().removeObjects(featureCurve_0);
        FeatureCurve featureCurve_1 = region_1.getFeatureCurveManager().createEmptyFeatureCurveWithName("Feature Curve");
        SolidModelPart solidModelPart_2 = ((SolidModelPart) simulation_0.get(SimulationPartManager.class).getPart("In-Out-Box"));
        simulation_0.getRegionManager().newRegionsFromParts(new NeoObjectVector(new Object[] {solidModelPart_2}), "OneRegion", region_1, "OneBoundaryPerPart", null, "OneFeatureCurve", featureCurve_1, RegionManager.CreateInterfaceMode.BOUNDARY);

        // Rescaling domain to actual size of the samples.
        simulation_0.println("Rescaling domain to actual size of the samples.");
        simulation_0.get(SimulationPartManager.class).scaleParts(new NeoObjectVector(new Object[] {solidModelPart_0, cadPart_2, cadPart_55, cadPart_3}), new DoubleVector(new double[] {1.0E-4, 1.0E-4, 1.0E-4}), labCoordinateSystem_0);

        // Meshing surface file.
        simulation_0.println("Meshing surface file.");
        AutoMeshOperation autoMeshOperation_0 = simulation_0.get(MeshOperationManager.class).createAutoMeshOperation(new StringVector(new String[] {"star.resurfacer.ResurfacerAutoMesher", "star.dualmesher.DualAutoMesher"}), new NeoObjectVector(new Object[] {cadPart_3}));
        autoMeshOperation_0.getDefaultValues().get(BaseSize.class).setValue(1E-5*1);
        PartsTargetSurfaceSize partsTargetSurfaceSize_0 = autoMeshOperation_0.getDefaultValues().get(PartsTargetSurfaceSize.class);
        partsTargetSurfaceSize_0.getRelativeSizeScalar().setValue(100.0);
        PartsMinimumSurfaceSize partsMinimumSurfaceSize_0 = autoMeshOperation_0.getDefaultValues().get(PartsMinimumSurfaceSize.class);
        partsMinimumSurfaceSize_0.getRelativeSizeScalar().setValue(2.0);
        autoMeshOperation_0.getMesherParallelModeOption().setSelected(MesherParallelModeOption.Type.PARALLEL);
        AutoMeshOperation autoMeshOperation_1 = simulation_0.get(MeshOperationManager.class).createAutoMeshOperation(new StringVector(new String[] {"star.resurfacer.ResurfacerAutoMesher", "star.trimmer.TrimmerAutoMesher"}), new NeoObjectVector(new Object[] {}));
        autoMeshOperation_1.getInputGeometryObjects().setQuery(null);
        autoMeshOperation_1.getInputGeometryObjects().setObjects(solidModelPart_2);MeshPipelineController meshPipelineController_0 = simulation_0.get(MeshPipelineController.class);
        meshPipelineController_0.generateVolumeMesh();

        // Setting flow physics
        simulation_0.println("Setting flow physics");        
        PhysicsContinuum physicsContinuum_0 = ((PhysicsContinuum) simulation_0.getContinuumManager().getContinuum("Physics 1"));
        physicsContinuum_0.enable(ThreeDimensionalModel.class);
        physicsContinuum_0.enable(SingleComponentLiquidModel.class);
        physicsContinuum_0.enable(SegregatedFlowModel.class);
        physicsContinuum_0.enable(ConstantDensityModel.class);
        physicsContinuum_0.enable(SteadyModel.class);
        physicsContinuum_0.enable(LaminarModel.class);
        physicsContinuum_0.enable(CellQualityRemediationModel.class);

         // Setting up boundary conditions.
         simulation_0.println("Setting up boundary conditions.");
         Region region_0 = simulation_0.getRegionManager().getRegion("Flow Domain");
         Boundary boundary_5 = region_0.getBoundaryManager().getBoundary("Inlet");
         StagnationBoundary stagnationboundary_5 = ((StagnationBoundary) simulation_0.get(ConditionTypeManager.class).get(StagnationBoundary.class));
         boundary_5.setBoundaryType(stagnationboundary_5);
         TotalPressureProfile totalPressureProfile_0 = boundary_5.getValues().get(TotalPressureProfile.class);
         totalPressureProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(1.0);
         Boundary boundary_6 = region_0.getBoundaryManager().getBoundary("Outlet");
         PressureBoundary pressureboundary_5 = ((PressureBoundary) simulation_0.get(ConditionTypeManager.class).get(PressureBoundary.class));
         boundary_6.setBoundaryType(pressureboundary_5);

         // Deriving parts.
         simulation_0.println("Deriving parts.");
         simulation_0.getSceneManager().createGeometryScene("Geometry Scene", "Outline", "Surface", 1);
         Scene scene_2 = simulation_0.getSceneManager().getScene("Geometry Scene 1");
         scene_2.initializeAndWait();
         PartDisplayer partDisplayer_0 = ((PartDisplayer) scene_2.getDisplayerManager().getDisplayer("Outline 1"));
         partDisplayer_0.initialize();
         PartDisplayer partDisplayer_1 = ((PartDisplayer) scene_2.getDisplayerManager().getDisplayer("Surface 1"));
         partDisplayer_1.initialize();
         SceneUpdate sceneUpdate_1 = scene_2.getSceneUpdate();
         HardcopyProperties hardcopyProperties_3 = sceneUpdate_1.getHardcopyProperties();
         hardcopyProperties_3.setCurrentResolutionWidth(25);
         hardcopyProperties_3.setCurrentResolutionHeight(25);
         hardcopyProperties_3.setCurrentResolutionWidth(1171);
         hardcopyProperties_3.setCurrentResolutionHeight(559);
         scene_2.resetCamera();
         scene_2.setTransparencyOverrideMode(SceneTransparencyOverride.MAKE_SCENE_TRANSPARENT);
         scene_2.getCreatorGroup().setQuery(null);
         scene_2.getCreatorGroup().setObjects(region_0);
         scene_2.getCreatorGroup().setQuery(null);
         scene_2.getCreatorGroup().setObjects(region_0);
         PrimitiveFieldFunction primitiveFieldFunction_0 = ((PrimitiveFieldFunction) simulation_0.getFieldFunctionManager().getFunction("Position"));
         VectorComponentFieldFunction vectorComponentFieldFunction_0 = ((VectorComponentFieldFunction) primitiveFieldFunction_0.getComponentFunction(2));
         ThresholdPart thresholdPart_0 = simulation_0.getPartManager().createThresholdPart(new NeoObjectVector(new Object[] {region_0}), new DoubleVector(new double[] {-half_domain_length/10000, half_domain_length/10000}), units_0, vectorComponentFieldFunction_0, 0);
         scene_2.setTransparencyOverrideMode(SceneTransparencyOverride.USE_DISPLAYER_PROPERTY);
         thresholdPart_0.setPresentationName("Z");
         scene_2.setTransparencyOverrideMode(SceneTransparencyOverride.MAKE_SCENE_TRANSPARENT);
         scene_2.getCreatorGroup().setQuery(null);
         scene_2.getCreatorGroup().setObjects(region_0);
         scene_2.getCreatorGroup().setQuery(null);
         scene_2.getCreatorGroup().setObjects(thresholdPart_0);
         VectorComponentFieldFunction vectorComponentFieldFunction_3 = ((VectorComponentFieldFunction) primitiveFieldFunction_0.getComponentFunction(1));
         ThresholdPart thresholdPart_1 = simulation_0.getPartManager().createThresholdPart(new NeoObjectVector(new Object[] {thresholdPart_0}), new DoubleVector(new double[] {-half_domain_length/10000, half_domain_length/10000}), units_0, vectorComponentFieldFunction_3, 0);
         scene_2.setTransparencyOverrideMode(SceneTransparencyOverride.USE_DISPLAYER_PROPERTY);
         thresholdPart_1.setPresentationName("Y");
         scene_2.setTransparencyOverrideMode(SceneTransparencyOverride.MAKE_SCENE_TRANSPARENT);
         scene_2.getCreatorGroup().setQuery(null);
         scene_2.getCreatorGroup().setObjects(region_0);
         scene_2.getCreatorGroup().setQuery(null);
         scene_2.getCreatorGroup().setObjects(thresholdPart_1);
         VectorComponentFieldFunction vectorComponentFieldFunction_2 = ((VectorComponentFieldFunction) primitiveFieldFunction_0.getComponentFunction(0));
         ThresholdPart thresholdPart_2 = simulation_0.getPartManager().createThresholdPart(new NeoObjectVector(new Object[] {thresholdPart_1}), new DoubleVector(new double[] {-half_domain_length/10000, half_domain_length/10000}), units_0, vectorComponentFieldFunction_2, 0);
         scene_2.setTransparencyOverrideMode(SceneTransparencyOverride.USE_DISPLAYER_PROPERTY);
         thresholdPart_2.setPresentationName("X"); // pore microstructural volume (area of interest)
         scene_2.setTransparencyOverrideMode(SceneTransparencyOverride.MAKE_SCENE_TRANSPARENT);
         scene_2.getCreatorGroup().setQuery(null);
         scene_2.getCreatorGroup().setObjects(region_0);
         scene_2.getCreatorGroup().setQuery(null);
         scene_2.getCreatorGroup().setObjects(thresholdPart_2);
         PlaneSection planeSection_2 = (PlaneSection) simulation_0.getPartManager().createImplicitPart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 0.0, 1.0}), new DoubleVector(new double[] {0.0, 0.0, 0.0}), 0, 1, new DoubleVector(new double[] {0.0}));
         planeSection_2.setCoordinateSystem(labCoordinateSystem_0);
         planeSection_2.getInputParts().setQuery(null);
         planeSection_2.getInputParts().setObjects(thresholdPart_2);
         planeSection_2.getOriginCoordinate().setUnits0(units_0);
         planeSection_2.getOriginCoordinate().setUnits1(units_0);
         planeSection_2.getOriginCoordinate().setUnits2(units_0);
         planeSection_2.getOriginCoordinate().setDefinition("");
         planeSection_2.getOriginCoordinate().setValue(new DoubleVector(new double[] {-half_domain_length/10000, 0.0, 0.0}));
         planeSection_2.getOriginCoordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {-half_domain_length/10000, 0.0, 0.0}));
         planeSection_2.getOriginCoordinate().setCoordinateSystem(labCoordinateSystem_0);
         planeSection_2.getOrientationCoordinate().setUnits0(units_0);
         planeSection_2.getOrientationCoordinate().setUnits1(units_0);
         planeSection_2.getOrientationCoordinate().setUnits2(units_0);
         planeSection_2.getOrientationCoordinate().setDefinition("");
         planeSection_2.getOrientationCoordinate().setValue(new DoubleVector(new double[] {1.0, 0.0, 0.0}));
         planeSection_2.getOrientationCoordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {1.0, 0.0, 0.0}));
         planeSection_2.getOrientationCoordinate().setCoordinateSystem(labCoordinateSystem_0);
         SingleValue singleValue_2 = planeSection_2.getSingleValue();
         singleValue_2.getValueQuantity().setValue(0.0);
         singleValue_2.getValueQuantity().setUnits(units_0);
         RangeMultiValue rangeMultiValue_2 = planeSection_2.getRangeMultiValue();
         rangeMultiValue_2.setNValues(2);
         rangeMultiValue_2.getStartQuantity().setValue(0.0);
         rangeMultiValue_2.getStartQuantity().setUnits(units_0);
         rangeMultiValue_2.getEndQuantity().setValue(1.0);
         rangeMultiValue_2.getEndQuantity().setUnits(units_0);
         DeltaMultiValue deltaMultiValue_2 = planeSection_2.getDeltaMultiValue();
         deltaMultiValue_2.setNValues(2);
         deltaMultiValue_2.getStartQuantity().setValue(0.0);
         deltaMultiValue_2.getStartQuantity().setUnits(units_0);
         deltaMultiValue_2.getDeltaQuantity().setValue(1.0);
         deltaMultiValue_2.getDeltaQuantity().setUnits(units_0);
         MultiValue multiValue_2 = planeSection_2.getArbitraryMultiValue();
         multiValue_2.getValueQuantities().setUnits(units_0);
         multiValue_2.getValueQuantities().setArray(new DoubleVector(new double[] {0.0}));
         planeSection_2.setValueMode(ValueMode.SINGLE);
         scene_2.setTransparencyOverrideMode(SceneTransparencyOverride.USE_DISPLAYER_PROPERTY);
         planeSection_2.setPresentationName("In"); // pore microstructural inlet (area of interest)
         PlaneSection planeSection_3 = (PlaneSection) simulation_0.getPartManager().createImplicitPart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 0.0, 1.0}), new DoubleVector(new double[] {0.0, 0.0, 0.0}), 0, 1, new DoubleVector(new double[] {0.0}));
         planeSection_3.setPresentationName("Copy of In");
         planeSection_3.copyProperties(planeSection_2);
         planeSection_3.setPresentationName("Out");// pore microstructural outlet (area of interest)
         planeSection_3.getOriginCoordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {half_domain_length/10000, 0.0, 0.0}));

         // Generating reports.
         simulation_0.println("Generating reports.");
         SumReport sumReport_3 = simulation_0.getReportManager().createReport(SumReport.class);
         sumReport_3.setPresentationName("AreaA");
         PrimitiveFieldFunction primitiveFieldFunction_1 = ((PrimitiveFieldFunction) simulation_0.getFieldFunctionManager().getFunction("Area"));
         VectorMagnitudeFieldFunction vectorMagnitudeFieldFunction_1 = ((VectorMagnitudeFieldFunction) primitiveFieldFunction_1.getMagnitudeFunction());
         sumReport_3.setFieldFunction(vectorMagnitudeFieldFunction_1);
         sumReport_3.getParts().setQuery(null);
         Boundary boundary_0 = region_0.getBoundaryManager().getBoundary("Inlet");
         sumReport_3.getParts().setObjects(boundary_0);
         ExpressionReport expressionReport_8 = simulation_0.getReportManager().createReport(ExpressionReport.class);
         expressionReport_8.setPresentationName("LengthA"); // domain length, equal in all directions
         expressionReport_8.setDefinition("sqrt(${AreaAReport})");
         VolumeAverageReport volumeAverageReport_0 = simulation_0.getReportManager().createReport(VolumeAverageReport.class);
         PrimitiveFieldFunction primitiveFieldFunction_2 = ((PrimitiveFieldFunction) simulation_0.getFieldFunctionManager().getFunction("Velocity"));
         VectorMagnitudeFieldFunction vectorMagnitudeFieldFunction_0 = ((VectorMagnitudeFieldFunction) primitiveFieldFunction_2.getMagnitudeFunction());
         volumeAverageReport_0.setFieldFunction(vectorMagnitudeFieldFunction_0);
         volumeAverageReport_0.setPresentationName("Vel"); // average velocity magnitude through the area of interest
         volumeAverageReport_0.getParts().setQuery(null);
         volumeAverageReport_0.getParts().setObjects(thresholdPart_2);
         VolumeAverageReport volumeAverageReport_1 = simulation_0.getReportManager().createReport(VolumeAverageReport.class);
         volumeAverageReport_1.copyProperties(volumeAverageReport_0);
         volumeAverageReport_1.setPresentationName("Copy of Vel");
         volumeAverageReport_1.setPresentationName("VelX"); // average velocity in the x direction through the area of interest
         VectorComponentFieldFunction vectorComponentFieldFunction_1 = ((VectorComponentFieldFunction) primitiveFieldFunction_2.getComponentFunction(0));
         volumeAverageReport_1.setFieldFunction(vectorComponentFieldFunction_1);
         ExpressionReport expressionReport_0 = simulation_0.getReportManager().createReport(ExpressionReport.class);
         expressionReport_0.setPresentationName("Tort2");
         CurrentView currentView_1 = scene_2.getCurrentView();
         currentView_1.setInput(new DoubleVector(new double[] {0.0, -1.6141672660295744E-6, 8.109259456273767E-7}), new DoubleVector(new double[] {0.0, -1.6141672660295744E-6, 1.5872502382741305E-4}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 4.0871176021989765E-5, 0, 30.0);
         expressionReport_0.setPresentationName("Tort1"); // Tortuosity based on use of average velocities
         expressionReport_0.setDefinition("(${VelReport}+1e-99)/(${VelXReport}+1e-99)"); // +1e-99 anywhere in the code is to prevent the syetem from crashing due to division by 0
         SumReport sumReport_0 = simulation_0.getReportManager().createReport(SumReport.class);
         sumReport_0.setPresentationName("Vel2"); // total velocity magnitude through the area of interest
         sumReport_0.setFieldFunction(vectorMagnitudeFieldFunction_0);
         SumReport sumReport_1 = simulation_0.getReportManager().createReport(SumReport.class);
         sumReport_1.copyProperties(sumReport_0);
         sumReport_1.setPresentationName("Copy of Vel2");
         sumReport_1.setPresentationName("VelX2"); // total velocity in the x direction through the area of interest
         sumReport_1.setFieldFunction(vectorComponentFieldFunction_1);
         sumReport_1.getParts().setQuery(null);
         sumReport_1.getParts().setObjects(thresholdPart_2);
         sumReport_0.getParts().setQuery(null);
         sumReport_0.getParts().setObjects(thresholdPart_2);
         ExpressionReport expressionReport_1 = simulation_0.getReportManager().createReport(ExpressionReport.class);
         expressionReport_1.copyProperties(expressionReport_0);
         expressionReport_1.setPresentationName("Copy of Tort1");
         expressionReport_1.setPresentationName("Tort2"); // Tortuosity based on use of total velocities
         expressionReport_1.setDefinition("(${Vel2Report}+1e-99)/(${VelX2Report}+1e-99)"); 
         SumReport sumReport_2 = simulation_0.getReportManager().createReport(SumReport.class);
         sumReport_2.setPresentationName("PorVol"); // volume of pores
         PrimitiveFieldFunction primitiveFieldFunction_5 = ((PrimitiveFieldFunction) simulation_0.getFieldFunctionManager().getFunction("Volume"));
         sumReport_2.setFieldFunction(primitiveFieldFunction_5);
         sumReport_2.getParts().setQuery(null);
         sumReport_2.getParts().setObjects(thresholdPart_2);
         ExpressionReport expressionReport_2 = simulation_0.getReportManager().createReport(ExpressionReport.class);
         expressionReport_2.setPresentationName("Porosity"); //porosity in decimals
         expressionReport_2.setDefinition("${PorVolReport}/(${LengthAReport}*${LengthAReport}*${LengthAReport})");
         ExpressionReport expressionReport_3 = simulation_0.getReportManager().createReport(ExpressionReport.class);
         expressionReport_3.setPresentationName("ReyNo"); // reynolds number estimation
         expressionReport_3.setDefinition("((${VelReport}+1e-99)*${LengthAReport}*997)/0.00089 ");
         ExpressionReport expressionReport_4 = simulation_0.getReportManager().createReport(ExpressionReport.class);
         currentView_1.setInput(new DoubleVector(new double[] {0.0, -1.6141672660295744E-6, 8.109259456273767E-7}), new DoubleVector(new double[] {0.0, -1.6141672660295744E-6, 1.5872502382741305E-4}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 4.0871176021989765E-5, 0, 30.0);
         expressionReport_4.setPresentationName("Perm1"); // permeability estimation using the volume average velocity in area of interest
         PressureDropReport pressureDropReport_0 = simulation_0.getReportManager().createReport(PressureDropReport.class);
         simulation_0.getReportManager().removeObjects(pressureDropReport_0);
         AreaAverageReport areaAverageReport_0 = simulation_0.getReportManager().createReport(AreaAverageReport.class);
         areaAverageReport_0.setPresentationName("In Pres"); // inlet pressure
         PrimitiveFieldFunction primitiveFieldFunction_6 = ((PrimitiveFieldFunction) simulation_0.getFieldFunctionManager().getFunction("Pressure"));
         areaAverageReport_0.setFieldFunction(primitiveFieldFunction_6);
         areaAverageReport_0.getParts().setQuery(null);
         areaAverageReport_0.getParts().setObjects(planeSection_2);
         AreaAverageReport areaAverageReport_1 = simulation_0.getReportManager().createReport(AreaAverageReport.class);
         areaAverageReport_1.copyProperties(areaAverageReport_0);
         areaAverageReport_1.setPresentationName("Copy of In Pres");
         areaAverageReport_1.setPresentationName("Out Pres"); // outlet pressure
         areaAverageReport_1.getParts().setQuery(null);
         areaAverageReport_1.getParts().setObjects(planeSection_3);
         ExpressionReport expressionReport_5 = simulation_0.getReportManager().createReport(ExpressionReport.class);
         expressionReport_5.setPresentationName("Pres Drop"); //pressure drop
         expressionReport_5.setDefinition("${InPresReport}-${OutPresReport}");
         expressionReport_4.setDefinition("((${VelReport}+1e-99)*0.00089*${LengthAReport}/(${PresDropReport}+1e-99))*1013249965828.14*1000");
         MassFlowReport massFlowReport_0 = simulation_0.getReportManager().createReport(MassFlowReport.class);
         massFlowReport_0.setPresentationName("In Mass Flow"); // inlet massflow
         massFlowReport_0.getParts().setQuery(null);
         massFlowReport_0.getParts().setObjects(boundary_5);
         MassFlowReport massFlowReport_1 = simulation_0.getReportManager().createReport(MassFlowReport.class);
         massFlowReport_1.copyProperties(massFlowReport_0);
         massFlowReport_1.setPresentationName("Copy of In Mass Flow");
         massFlowReport_1.setPresentationName("Out Mass Flow"); // outlet massflow
         massFlowReport_1.getParts().setQuery(null);
         massFlowReport_1.getParts().setObjects(boundary_6);
         ExpressionReport expressionReport_6 = simulation_0.getReportManager().createReport(ExpressionReport.class);
         expressionReport_6.setPresentationName("Perm2");// permeability estimation using massflow into the system 
         expressionReport_6.setDefinition("(((${InMassFlowReport}/997+1e-99)/(${LengthAReport}*${LengthAReport}))*0.00089*${LengthAReport}/(${PresDropReport}+1e-99))*1013249965828.14*1000");
         ExpressionReport expressionReport_7 = simulation_0.getReportManager().createReport(ExpressionReport.class);
         expressionReport_7.copyProperties(expressionReport_6);
         expressionReport_7.setPresentationName("Copy of Perm2");
         expressionReport_7.setPresentationName("Perm3"); // permeability estimation using massflow out of the system 
         expressionReport_7.setDefinition("(((${OutMassFlowReport}/997+1e-99)/(${LengthAReport}*${LengthAReport}))*0.00089*${LengthAReport}/(${PresDropReport}+1e-99))*1013249965828.14*1000");
         simulation_0.getMonitorManager().createMonitorAndPlot(new NeoObjectVector(new Object[] {expressionReport_0, expressionReport_1}), true, "Reports Plot");
         ReportMonitor reportMonitor_0 = ((ReportMonitor) simulation_0.getMonitorManager().getMonitor("Tort1 Monitor"));
         ReportMonitor reportMonitor_1 = ((ReportMonitor) simulation_0.getMonitorManager().getMonitor("Tort2 Monitor"));
         MonitorPlot monitorPlot_0 = simulation_0.getPlotManager().createMonitorPlot(new NeoObjectVector(new Object[] {reportMonitor_0, reportMonitor_1}), "Reports Plot");
         monitorPlot_0.open();
         PlotUpdate plotUpdate_1 = monitorPlot_0.getPlotUpdate();
         HardcopyProperties hardcopyProperties_6 = plotUpdate_1.getHardcopyProperties();
         hardcopyProperties_6.setCurrentResolutionWidth(25);
         hardcopyProperties_6.setCurrentResolutionHeight(25);
         hardcopyProperties_6.setCurrentResolutionWidth(1029);
         hardcopyProperties_6.setCurrentResolutionHeight(648);
         hardcopyProperties_6.setCurrentResolutionWidth(1027);
         hardcopyProperties_6.setCurrentResolutionHeight(647);
         simulation_0.getMonitorManager().createMonitorAndPlot(new NeoObjectVector(new Object[] {expressionReport_4}), true, "%1$s Plot");
         ReportMonitor reportMonitor_2 = ((ReportMonitor) simulation_0.getMonitorManager().getMonitor("Perm1 Monitor"));
         MonitorPlot monitorPlot_1 = simulation_0.getPlotManager().createMonitorPlot(new NeoObjectVector(new Object[] {reportMonitor_2}), "Perm1 Monitor Plot");
         monitorPlot_1.open();
         PlotUpdate plotUpdate_2 = monitorPlot_1.getPlotUpdate();
         HardcopyProperties hardcopyProperties_7 = plotUpdate_2.getHardcopyProperties();
         hardcopyProperties_7.setCurrentResolutionWidth(25);
         hardcopyProperties_7.setCurrentResolutionHeight(25);
         hardcopyProperties_6.setCurrentResolutionWidth(1029);
         hardcopyProperties_6.setCurrentResolutionHeight(648);
         hardcopyProperties_7.setCurrentResolutionWidth(1027);
         hardcopyProperties_7.setCurrentResolutionHeight(647);
         simulation_0.getMonitorManager().createMonitorAndPlot(new NeoObjectVector(new Object[] {expressionReport_6, expressionReport_7}), true, "Reports Plot");
         ReportMonitor reportMonitor_3 = ((ReportMonitor) simulation_0.getMonitorManager().getMonitor("Perm2 Monitor"));
         ReportMonitor reportMonitor_4 = ((ReportMonitor) simulation_0.getMonitorManager().getMonitor("Perm3 Monitor"));
         MonitorPlot monitorPlot_2 = simulation_0.getPlotManager().createMonitorPlot(new NeoObjectVector(new Object[] {reportMonitor_3, reportMonitor_4}), "Reports Plot");
         monitorPlot_2.open();
         MonitorPlot monitorPlot_3 = ((MonitorPlot) simulation_0.getPlotManager().getPlot("Reports Plot 2"));
         PlotUpdate plotUpdate_3 = monitorPlot_3.getPlotUpdate();
         HardcopyProperties hardcopyProperties_8 = plotUpdate_3.getHardcopyProperties();
         hardcopyProperties_8.setCurrentResolutionWidth(25);
         hardcopyProperties_8.setCurrentResolutionHeight(25);
         MonitorPlot monitorPlot_4 = ((MonitorPlot) simulation_0.getPlotManager().getPlot("Perm1 Monitor Plot"));
         PlotUpdate plotUpdate_4 = monitorPlot_4.getPlotUpdate();
         HardcopyProperties hardcopyProperties_4 = plotUpdate_4.getHardcopyProperties();
         hardcopyProperties_4.setCurrentResolutionWidth(1029);
         hardcopyProperties_4.setCurrentResolutionHeight(648);
         hardcopyProperties_8.setCurrentResolutionWidth(1027);
         hardcopyProperties_8.setCurrentResolutionHeight(647);
         ElementCountReport elementCountReport_1 = simulation_0.getReportManager().createReport(ElementCountReport.class);
         elementCountReport_1.setPresentationName("Cell");
         elementCountReport_1.getParts().setObjects();
         elementCountReport_1.getParts().setQuery(null);
         elementCountReport_1.getParts().setObjects(thresholdPart_2);
         ReportMonitor reportMonitor_5 = elementCountReport_1.createMonitor();
         MonitorPlot monitorPlot_5 = ((MonitorPlot) simulation_0.getPlotManager().getPlot("Reports Plot 2"));
         Cartesian2DAxisManager cartesian2DAxisManager_1 = ((Cartesian2DAxisManager) monitorPlot_5.getAxisManager());
         cartesian2DAxisManager_1.setAxesBounds(new Vector(Arrays.<AxisManager.AxisBounds>asList(new AxisManager.AxisBounds("Bottom Axis", 0.0, false, 1.0, false))));
         cartesian2DAxisManager_1.setAxesBounds(new Vector(Arrays.<AxisManager.AxisBounds>asList(new AxisManager.AxisBounds("Left Axis", 0.0, false, 1.0, false))));
         ReportMonitor reportMonitor_6 = expressionReport_3.createMonitor();
         VolumeAverageReport volumeAverageReport_2 = ((VolumeAverageReport) simulation_0.getReportManager().getReport("Vel"));
         ReportMonitor reportMonitor_7 = volumeAverageReport_2.createMonitor();
         VolumeAverageReport volumeAverageReport_3 = ((VolumeAverageReport) simulation_0.getReportManager().getReport("VelX"));
         ReportMonitor reportMonitor_8 = volumeAverageReport_3.createMonitor();
         ReportMonitor reportMonitor_9 = sumReport_2.createMonitor();
         ReportMonitor reportMonitor_10 = massFlowReport_0.createMonitor();
         ReportMonitor reportMonitor_11 = massFlowReport_1.createMonitor();
         ReportMonitor reportMonitor_12 = expressionReport_5.createMonitor();
         ExpressionReport expressionReport_9 = ((ExpressionReport) simulation_0.getReportManager().getReport("Porosity"));
         ReportMonitor reportMonitor_13 = expressionReport_9.createMonitor();
         ExpressionReport expressionReport_10 = ((ExpressionReport) simulation_0.getReportManager().getReport("LengthA"));
         ReportMonitor reportMonitor_14 = expressionReport_10.createMonitor();
         SumReport sumReport_4 = simulation_0.getReportManager().createReport(SumReport.class);
         sumReport_4.setPresentationName("SurfArea"); //surface area of the area of interest
         PrimitiveFieldFunction primitiveFieldFunction_3 = ((PrimitiveFieldFunction) simulation_0.getFieldFunctionManager().getFunction("Area"));
         VectorMagnitudeFieldFunction vectorMagnitudeFieldFunction_2 = ((VectorMagnitudeFieldFunction) primitiveFieldFunction_3.getMagnitudeFunction());
         sumReport_4.setFieldFunction(vectorMagnitudeFieldFunction_2);
         sumReport_4.getParts().setQuery(null);
         Boundary boundary_10 = region_0.getBoundaryManager().getBoundary("Sphere Surface");
         sumReport_4.getParts().setObjects(boundary_10);
         ReportMonitor reportMonitor_15 = sumReport_4.createMonitor();
         SumReport sumReport_7 = simulation_0.getReportManager().createReport(SumReport.class);
         PrimitiveFieldFunction primitiveFieldFunction_4 = ((PrimitiveFieldFunction) simulation_0.getFieldFunctionManager().getFunction("Area"));
         VectorMagnitudeFieldFunction vectorMagnitudeFieldFunction_3 = ((VectorMagnitudeFieldFunction) primitiveFieldFunction_4.getMagnitudeFunction());
         sumReport_7.setFieldFunction(vectorMagnitudeFieldFunction_3);
         sumReport_7.getParts().setQuery(null);
         sumReport_7.getParts().setObjects(region_1);
         sumReport_7.getParts().setQuery(null);
         Boundary boundary_7 = region_1.getBoundaryManager().getBoundary("In-Out-Box");
         sumReport_7.getParts().setObjects(region_1, boundary_7);
         SumReport sumReport_6 = simulation_0.getReportManager().createReport(SumReport.class);
         sumReport_6.setFieldFunction(vectorMagnitudeFieldFunction_3);
         sumReport_6.getParts().setQuery(null);
         Region region_2 = simulation_0.getRegionManager().getRegion("Flow Domain");
         Boundary boundary_8 = region_2.getBoundaryManager().getBoundary("Block Surface");
         Boundary boundary_3 = region_2.getBoundaryManager().getBoundary("Inlet");
         Boundary boundary_4 = region_2.getBoundaryManager().getBoundary("Outlet");
         sumReport_6.getParts().setObjects(boundary_8, boundary_3, boundary_4);
         ExpressionReport expressionReport_12 = simulation_0.getReportManager().createReport(ExpressionReport.class);
         expressionReport_12.setPresentationName("in-out-area"); // area of surfaces in to and out of the arae of interest
         expressionReport_12.setDefinition("${Sum1Report}-${Sum2Report}");
         PlaneSection planeSection_0 = (PlaneSection) simulation_0.getPartManager().createImplicitPart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 0.0, 1.0}), new DoubleVector(new double[] {0.0, 0.0, 0.0}), 0, 1, new DoubleVector(new double[] {0.0}));
         planeSection_0.setPresentationName("Copy of In");
         PlaneSection planeSection_1 = ((PlaneSection) simulation_0.getPartManager().getObject("In"));
         planeSection_0.copyProperties(planeSection_1);
         planeSection_0.setPresentationName("in-area"); // area of surfaces into the arae of interest
         planeSection_0.getOriginCoordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {-0.9999*half_domain_length/10000, 0.0, 0.0}));
         SumReport sumReport_5 = simulation_0.getReportManager().createReport(SumReport.class);
         sumReport_5.setFieldFunction(vectorMagnitudeFieldFunction_3);
         sumReport_5.getParts().setQuery(null);
         sumReport_5.getParts().setObjects(planeSection_0);
         sumReport_5.setPresentationName("in-area");
         ExpressionReport expressionReport_11 = simulation_0.getReportManager().createReport(ExpressionReport.class);
         expressionReport_11.setDefinition("${in-out-area}-${in-area}");
         expressionReport_11.setPresentationName("out-area"); // area of surfaces out of the arae of interest
         SumReport sumReport_8 = ((SumReport) simulation_0.getReportManager().getReport("in-area"));
         ReportMonitor reportMonitor_16 = sumReport_8.createMonitor();
         ExpressionReport expressionReport_13 = ((ExpressionReport) simulation_0.getReportManager().getReport("in-out-area"));
         ReportMonitor reportMonitor_18 = expressionReport_13.createMonitor();
         ExpressionReport expressionReport_14 = ((ExpressionReport) simulation_0.getReportManager().getReport("out-area"));
         ReportMonitor reportMonitor_17 = expressionReport_14.createMonitor();
         PlaneSection planeSection_4 = ((PlaneSection) simulation_0.getPartManager().getObject("in-area"));
         Region region_3 = simulation_0.getRegionManager().getRegion("Flow Domain");
         Boundary boundary_9 = region_3.getBoundaryManager().getBoundary("Block Surface");
         Boundary boundary_13 = region_3.getBoundaryManager().getBoundary("Inlet");
         Boundary boundary_11 = region_3.getBoundaryManager().getBoundary("Outlet");
         Boundary boundary_12 = region_3.getBoundaryManager().getBoundary("Sphere Surface");
         planeSection_4.getInputParts().setObjects(region_3, boundary_9, boundary_13, boundary_11, boundary_12);
         AreaAverageReport areaAverageReport_0a = ((AreaAverageReport) simulation_0.getReportManager().getReport("In Pres"));
         ReportMonitor reportMonitor_10a = areaAverageReport_0a.createMonitor();
         AreaAverageReport areaAverageReport_1a = ((AreaAverageReport) simulation_0.getReportManager().getReport("Out Pres"));
         ReportMonitor reportMonitor_11a = areaAverageReport_1a.createMonitor();
         VolumeAverageReport volumeAverageReport_0a = ((VolumeAverageReport) simulation_0.getReportManager().getReport("Vel"));
         ReportMonitor reportMonitor_12a = volumeAverageReport_0a.createMonitor();
         VolumeAverageReport volumeAverageReport_1a = ((VolumeAverageReport) simulation_0.getReportManager().getReport("VelX"));
         ReportMonitor reportMonitor_13a =  volumeAverageReport_1a.createMonitor();

         // Run flow simulation.
         simulation_0.println("Starting flow simulation.");
         StepStoppingCriterion stepStoppingCriterion_0 = ((StepStoppingCriterion) simulation_0.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps"));
         stepStoppingCriterion_0.setMaximumNumberSteps(200);
         simulation_0.getSimulationIterator().run();

         // Exporting reports as a single csv file
         simulation_0.println("Exporting results.");
         ReportMonitor reportMonitor_0b = ((ReportMonitor) simulation_0.getMonitorManager().getMonitor("Tort1 Monitor"));
         ReportMonitor reportMonitor_1b = ((ReportMonitor) simulation_0.getMonitorManager().getMonitor("Cell Monitor"));
         ReportMonitor reportMonitor_2b = ((ReportMonitor) simulation_0.getMonitorManager().getMonitor("LengthA Monitor"));
         ReportMonitor reportMonitor_3b = ((ReportMonitor) simulation_0.getMonitorManager().getMonitor("Perm1 Monitor"));
         ReportMonitor reportMonitor_4b = ((ReportMonitor) simulation_0.getMonitorManager().getMonitor("Porosity Monitor"));
         ReportMonitor reportMonitor_5b = ((ReportMonitor) simulation_0.getMonitorManager().getMonitor("ReyNo Monitor"));
         ReportMonitor reportMonitor_6b = ((ReportMonitor) simulation_0.getMonitorManager().getMonitor("Perm2 Monitor"));
         ReportMonitor reportMonitor_7b = ((ReportMonitor) simulation_0.getMonitorManager().getMonitor("In Mass Flow Monitor"));
         ReportMonitor reportMonitor_8b = ((ReportMonitor) simulation_0.getMonitorManager().getMonitor("Out Mass Flow Monitor"));
         ReportMonitor reportMonitor_9b = ((ReportMonitor) simulation_0.getMonitorManager().getMonitor("Tort2 Monitor"));
         ReportMonitor reportMonitor_10b = ((ReportMonitor) simulation_0.getMonitorManager().getMonitor("Perm3 Monitor"));
         ReportMonitor reportMonitor_11b = ((ReportMonitor) simulation_0.getMonitorManager().getMonitor("Pres Drop Monitor"));
         ReportMonitor reportMonitor_12b = ((ReportMonitor) simulation_0.getMonitorManager().getMonitor("PorVol Monitor"));
         ResidualMonitor residualMonitor_0n = ((ResidualMonitor) simulation_0.getMonitorManager().getMonitor("Continuity"));
         ResidualMonitor residualMonitor_1n = ((ResidualMonitor) simulation_0.getMonitorManager().getMonitor("X-momentum"));
         ResidualMonitor residualMonitor_2n = ((ResidualMonitor) simulation_0.getMonitorManager().getMonitor("Y-momentum"));
         ResidualMonitor residualMonitor_3n = ((ResidualMonitor) simulation_0.getMonitorManager().getMonitor("Z-momentum"));
         simulation_0.getMonitorManager().export("simulation_" + count + ".csv", ",", new NeoObjectVector(new Object[] {reportMonitor_0b, reportMonitor_1b, reportMonitor_2b, reportMonitor_3b, reportMonitor_4b, reportMonitor_5b, reportMonitor_6b, reportMonitor_7b, reportMonitor_8b, reportMonitor_9b, reportMonitor_10b, reportMonitor_11b, reportMonitor_12b, reportMonitor_15, reportMonitor_16, reportMonitor_17, reportMonitor_18, reportMonitor_10a, reportMonitor_11a, reportMonitor_12a, reportMonitor_13a, residualMonitor_0n, residualMonitor_1n, residualMonitor_2n, residualMonitor_3n}));
        //  simulation_0.saveState("simulation_" + count + ".sim"); // if there is need to save the simulation file
      
         // Close file
         simulation_0.println("The END!!!");
         simulation_0.close();

      } catch(Exception e2) {
        simulation_0.println("Data set " + count + " imploded"); // sets of actions to take if simulation crashes for any reason
        simulation_0.println(e2);
        simulation_0.close();

        continue;

      }
    }
  }
}
