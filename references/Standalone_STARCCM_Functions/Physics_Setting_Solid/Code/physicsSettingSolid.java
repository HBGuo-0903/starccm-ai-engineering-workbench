// physicsSettingFluid.java
// an example of setting up a solid physics continuum

import java.util.*;


import star.common.*;
import star.energy.*;
import star.flow.*;
import star.keturb.*;
import star.material.*;
import star.metrics.*;
import star.segregatedenergy.*;
import star.segregatedflow.*;
import star.turbulence.*;



public class physicsSettingSolid extends StarMacro {


        public void execute() {
		physicsSettingSolid();
	}
	
	public void physicsSettingSolid(){

	// Get active simulation
        Simulation sim = getActiveSimulation();
        String dir = sim.getSessionDir();
	String name = sim.getPresentationName();	
        String sep = System.getProperty("file.separator");
		
	// Generate a new (solid) continuum, then rename it to "Solid"
	// Note: the Name of physics contina must be unique
	PhysicsContinuum solidPhys = sim.getContinuumManager().createContinuum(PhysicsContinuum.class);
	solidPhys.setPresentationName("Solid");
		
	// Select up a model: Should be specified by users
	// In this example,
	/* 	1) 3-D 
		2) Steady
		3) Segregated energy
		4) constant density
		5) Cell qaulity remediation
		have been selected
	*/
	solidPhys.enable(ThreeDimensionalModel.class);
        solidPhys.enable(SteadyModel.class);
        solidPhys.enable(SolidModel.class);
        solidPhys.enable(SegregatedSolidEnergyModel.class);
        solidPhys.enable(ConstantDensityModel.class);
        solidPhys.enable(CellQualityRemediationModel.class);
		
	// Material setting example: Change material in solid physics continuum
	  // 1) Get the default material settings (Al)
	SolidModel solid = solidPhys.getModelManager().getModel(SolidModel.class);
        Solid defaultMaterial = ((Solid) solid.getMaterial());
	  // 2) select material from solid database
        MaterialDataBase materialDB = sim.get(MaterialDataBaseManager.class).getMaterialDataBase("props");
        DataBaseMaterialManager dbMM = materialDB.getFolder("Solids");
        DataBaseSolid Copper = ((DataBaseSolid) dbMM.getMaterial("Cu_Solid"));
	  // 3) replace default material settings to copper
        solid.replaceMaterial(defaultMaterial, Copper);
        
 
	// Save a file
	sim.saveState(resolvePath(name) + "_physics.sim");
	}
}

