// physicsSettingFluid.java
// an example of setting up a fluid physics continuum

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


public class physicsSettingFluid extends StarMacro {


    	public void execute() {
		physicsSettingFluid();
	}
	
	public void physicsSettingFluid(){

	// Get active simulation
        Simulation sim = getActiveSimulation();
	String name = sim.getPresentationName();	
        String dir = sim.getSessionDir();
        String sep = System.getProperty("file.separator");
        
        // Generate a new (fluid) continuum, then rename it to "Fluid"
	// Note: the Name of physics contina must be unique
	PhysicsContinuum fluidPhys = sim.getContinuumManager().createContinuum(PhysicsContinuum.class);
	fluidPhys.setPresentationName("Fluid");
        
        // Select up a model: Should be specified by users
	// In this example,
	/* 	1) Steady
		2) Single component gas
		3) Segregated Flow
		4) k-e turbulence model with 2 layer y+ wall model 
		5) Cell qaulity remediation
		have been selected
	*/
        fluidPhys.enable(SteadyModel.class);
        fluidPhys.enable(SingleComponentGasModel.class);
        fluidPhys.enable(SegregatedFlowModel.class);
        fluidPhys.enable(IdealGasModel.class);
        fluidPhys.enable(SegregatedFluidTemperatureModel.class);
        fluidPhys.enable(TurbulentModel.class);
        fluidPhys.enable(RansTurbulenceModel.class);
        fluidPhys.enable(KEpsilonTurbulence.class);
        fluidPhys.enable(RkeTwoLayerTurbModel.class);
        fluidPhys.enable(KeTwoLayerAllYplusWallTreatment.class);
        fluidPhys.enable(CellQualityRemediationModel.class);
	     
		
        // Setting up initial conditions
          // 1) Initial temperature
		fluidPhys.getInitialConditions().get(StaticTemperatureProfile.class).getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(500.0);
	  // 2) turbulence velocity scale	
		fluidPhys.getInitialConditions().get(TurbulentVelocityScaleProfile.class).getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(2.0);
          // 3) the velocity profile
		fluidPhys.getInitialConditions().get(VelocityProfile.class).getMethod(ConstantVectorProfileMethod.class).getQuantity().setComponents(1.0, 0.0, 0.0);
		
	// Save a file
	sim.saveState(resolvePath(name) + "_physics.sim");
	}
}
