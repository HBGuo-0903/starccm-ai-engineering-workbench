// regionAndBCSettings.java: Assign regions, then set boundary conditions
/* Used sim file has two regions: Fluid and Soild
   Fluid region has: 
	1) 4 velocity inlets 
	2) 1 pressure outlet
	3) Interface Wall (Fluid/Solid interface)
   Solid region has	
	1) Wall (outer surface)
	2) Interface Wall (Fluid/Solid Interface */
// This script recognizes and set boundary conditions by their names - this is why 'query' is required

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.base.query.*;
import star.energy.*;
import star.flow.*;
import star.keturb.*;
import star.material.*;
import star.metrics.*;
import star.segregatedenergy.*;
import star.segregatedflow.*;
import star.turbulence.*;


public class regionAndBCSettings extends StarMacro {


    public void execute() {
		
	// Load a file & get basic properties 
	Simulation sim = getActiveSimulation();			
	String name = getActiveSimulation().getPresentationName();		

	// Set up region and physics
	setUpRegions(sim);
		
	// set up boundary conditions
	setUpBoundaryConditions(sim);
		
	// Save a file	
	sim.saveState(resolvePath(name) + "_setUpFinished.sim");
			
	}
		
	public void setUpRegions(Simulation sim){
				
		
	// Get Regions & continua, 
        Region fluidRegion = sim.getRegionManager().getRegion("Manifold.Fluid");
        Region solidRegion = sim.getRegionManager().getRegion("Manifold.Solid");
	PhysicsContinuum fluidPhys = ((PhysicsContinuum) sim.getContinuumManager().getContinuum("Fluid"));
	PhysicsContinuum solidPhys = ((PhysicsContinuum) sim.getContinuumManager().getContinuum("Solid"));				
		
	// Assign each continuum to correponding regions
	fluidPhys.add(fluidRegion);
	solidPhys.add(solidRegion);
		
	}
	
	public void setUpBoundaryConditions(Simulation sim){
		
	// Get Regions to set boundary conditions
	Region fluidRegion = sim.getRegionManager().getRegion("Manifold.Fluid");
        Region solidRegion = sim.getRegionManager().getRegion("Manifold.Solid");

	// Change boundary type according to the name (use Query)
	// boundary will be set to "velocity inlet" if it has "inlet" in its name, "pressure outlet" if it has "outlet".
	// All the others will be set to wall by default
        Collection<Region> regions = sim.getRegionManager().getRegions();
        for (Region region : regions) {
            Collection<Boundary> boundaries = region.getBoundaryManager().getBoundaries();
            for (Boundary boundary : boundaries) {
                if (boundary.getPresentationName().contains("Inlet")) {
                    boundary.setBoundaryType(InletBoundary.class);
                } else if (boundary.getPresentationName().contains("Outlet")) {
                    boundary.setBoundaryType(PressureBoundary.class);
                }
            }
        }		
	
	// Set up BCs for the fluid     
	/* To Do:
	   1) Set up inlet 1 temperature 500 K, inlet magnitude 2.0 m/s
	   2) Other inlets have temperature 350K, inlet velocity of 0.1 m/s */
	Collection<Boundary> fluidBoundaries = sim.getRegionManager().getRegion("Manifold.Fluid").getBoundaryManager().getBoundaries();
	for (Boundary boundary : fluidBoundaries) {
            String boundaryName = boundary.getPresentationName();
            if (boundaryName.contains("Inlet")){
		if (!boundaryName.equalsIgnoreCase("Inlet 1")) {
        	        boundary.getValues().get(VelocityMagnitudeProfile.class).getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(0.1);
                	boundary.getValues().get(StaticTemperatureProfile.class).getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(350.0);
		} 
		else {
			boundary.getValues().get(VelocityMagnitudeProfile.class).getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(2.0); 
			boundary.getValues().get(StaticTemperatureProfile.class).getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(500.0);		
		}
            }
        }       
		
        // Set up BCs for the solid
	// To Do: set wall thermal type to convection, with a heat transfer coeffcient of 50 W/m^2K
        Boundary solidWall = solidRegion.getBoundaryManager().getBoundary("Wall");
        solidWall.getConditions().get(WallThermalOption.class).setSelected(WallThermalOption.Type.CONVECTION);
        solidWall.getValues().get(HeatTransferCoefficientProfile.class).getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(50.0);
	}
}

