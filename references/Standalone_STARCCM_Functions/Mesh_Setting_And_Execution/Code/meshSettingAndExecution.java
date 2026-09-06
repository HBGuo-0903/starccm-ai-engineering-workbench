// meshSettingAndExecution.java
// 1) Generates and set-up "Automated Mesh" operation, including
//   a) Basic parameter setting
//   b) Custom controls
// 2) Execute "Automated Mesh" then save a file

import java.io.*;
import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.base.query.*;
import star.meshing.*;
import star.prismmesher.*;
import star.resurfacer.*;
import star.solidmesher.*;


public class meshSettingAndExecution extends StarMacro {

    
    public void execute() {
        meshSettings();			
	generateMesh();		
    }
    
    private void meshSettings() {

	// Get active simulation
        Simulation simulation_0 = getActiveSimulation();
        String dir = simulation_0.getSessionDir();
	String name = simulation_0.getPresentationName();	
        String sep = System.getProperty("file.separator");
        
        // get Parts(assembly) for meshing
        GeometryPart composite_Manifold = simulation_0.get(SimulationPartManager.class).getPart("Manifold");

        // create Automated Mesh Operation
	/*  Options (Selected Meshers)
	  1) Surface remesher - Automatic surface Repair
	  2) Polyhedral Mesher Mesher
	  3) Thin Mesher (Solid only)
	  4) Prism layer Mesher (Fluid only) 
	*/
        AutoMeshOperation autoMeshOperation = simulation_0.get(MeshOperationManager.class).createAutoMeshOperation(new StringVector(new String[]{"star.resurfacer.ResurfacerAutoMesher","star.dualmesher.DualAutoMesher", "star.solidmesher.ThinAutoMesher", "star.prismmesher.PrismAutoMesher"}), Arrays.asList(composite_Manifold));
		
	// set units to mm
        Units units_mm = simulation_0.getUnitsManager().getObject("mm");

	// Mesh settings
		//1) Base size = 100.0 mm
		double baseSize = 100.0;
		autoMeshOperation.getDefaultValues().get(BaseSize.class).setUnits(units_mm);
		autoMeshOperation.getDefaultValues().get(BaseSize.class).setValue(baseSize);	
		//2) Target surface size: 10% of base size
		double targetSizePct = 10.0;
		autoMeshOperation.getDefaultValues().get(PartsTargetSurfaceSize.class).getRelativeSizeScalar().setValue(targetSizePct);			
		//3) Minimum surface size: 1% of base size
		double minimumSizePct = 1.0;
		autoMeshOperation.getDefaultValues().get(PartsMinimumSurfaceSize.class).getRelativeSizeScalar().setValue(minimumSizePct);			
		//4) number of thin layers = 5
		int numThinLayers = 5;
		autoMeshOperation.getDefaultValues().get(ThinNumLayers.class).setLayers(numThinLayers);
		// 5) Prism Layer absolute size = 2.0 mm
		double prismLayerSize = 2.0;
		autoMeshOperation.getDefaultValues().get(PrismThickness.class).setAbsoluteSize(prismLayerSize, units_mm);
        
	// Mesh custom control - disable thin mesher in fluid region, while disable prism layers in solid region
        meshCustomControl(autoMeshOperation);
		
	}
	
	private void meshCustomControl(AutoMeshOperation meshOperation) {
      
	// disable proximity refinement
        meshOperation.getMeshers().hasMesher(ResurfacerAutoMesher.class).setDoProximityRefinement(false);

        // disable prism mesh in solids 
        SurfaceCustomMeshControl surfaceCustomMeshControl = meshOperation.getCustomMeshControls().createSurfaceControl();
        surfaceCustomMeshControl.setPresentationName("noPrismLayerInSolid");
        surfaceCustomMeshControl.getGeometryObjects().setQuery(new Query(new NamePredicate(NameOperator.Contains, "solid"), Query.STANDARD_MODIFIERS));
        PartsCustomizePrismMesh partsCustomizePrismMesh_0 = surfaceCustomMeshControl.getCustomConditions().get(PartsCustomizePrismMesh.class);
        partsCustomizePrismMesh_0.getCustomPrismOptions().setSelected(PartsCustomPrismsOption.Type.DISABLE);

        // disable thin mesh in fluids
        PartCustomMeshControl partCustomMeshControl_0 = meshOperation.getCustomMeshControls().createPartControl();
        partCustomMeshControl_0.setPresentationName("noThinMeshInFluid");
        partCustomMeshControl_0.getGeometryObjects().setQuery(new Query(new NamePredicate(NameOperator.Contains, "fluid"), Query.STANDARD_MODIFIERS));
        PartsCustomizeThinMesh partsCustomizeThinMesh_0 = partCustomMeshControl_0.getCustomConditions().get(PartsCustomizeThinMesh.class);
        partsCustomizeThinMesh_0.getCustomThinOptions().setSelected(PartsCustomThinOption.Type.DISABLE);
        
    }

	
	public void generateMesh(){
		
	// Get active simulation
        Simulation simulation_0 = getActiveSimulation();
        String dir = simulation_0.getSessionDir();
	String name = simulation_0.getPresentationName();	
        String sep = System.getProperty("file.separator");

        // start meshing pipeline
       simulation_0.get(MeshPipelineController.class).generateVolumeMesh();
       
	// Save data file
       simulation_0.saveState(resolvePath(name) + "_meshFinished.sim");
    }
       
    

}
