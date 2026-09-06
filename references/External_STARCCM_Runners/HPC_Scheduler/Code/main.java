// Simcenter STAR-CCM+ macro: main.java
// Written by Simcenter STAR-CCM+ 18.02.008 < starting

// Jeffrey Filer
// Example of how macros can be used for more complex simulation operations

package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.amr.*;
import star.meshing.*;

import star.turbulence.*;import star.flow.*;import star.kwturb.*;import star.walldistance.*; // physics model defenitions

public class main extends StarMacro {

  public void execute() {
    Simulation sim = getActiveSimulation(); // main sim object
    AutoMeshOperation mesh = ((AutoMeshOperation) sim.get(MeshOperationManager.class).getObject("Automated Mesh")); // mesh object
    PhysicsContinuum physics = ((PhysicsContinuum) sim.getContinuumManager().getContinuum("Physics")); // physcis object

    prism_state(0, mesh); // no prism layers
    amr_state(0, physics); // no amr
    set_turb(0, physics); // invsicid
    set_base(1E-2, sim);
    mesh(sim); // run mesher

    iterate(100, sim);

    set_base(1E-3, sim);
    mesh(sim); // run mesher

    iterate(500, sim);
    amr_state(1, physics);
    iterate(500, sim);

    amr_state(1, physics);
    set_turb(1, physics);
    set_base(5E-4, sim);
    mesh(sim); // run mesher

    iterate(1000, sim);

    set_base(1E-4, sim);
    iterate(2000, sim);
  }

  public void iterate(int num_iter, Simulation sim){
    for(int i = 0; i < num_iter ; i++){
      sim.getSimulationIterator().step(1);
    }
  }
  public void prism_state(int state, AutoMeshOperation mesh){
    if(state == 1){ // if 1
      // do have prism layers
      mesh.getMeshers().setMeshersByNames(new StringVector(new String[] {"star.resurfacer.ResurfacerAutoMesher", "star.delaunaymesher.DelaunayAutoMesher", "star.prismmesher.PrismAutoMesher"}));
    }else{ // otherwise (0)
      // dont have prism layers
      mesh.getMeshers().setMeshersByNames(new StringVector(new String[] {"star.resurfacer.ResurfacerAutoMesher", "star.delaunaymesher.DelaunayAutoMesher"}));
    }
  }
  public void amr_state(int state, PhysicsContinuum physics){
    if(state == 1){ // if 1
      // enable amr
      safeEnable(physics, AmrModel.class); 
    }else{ // otherwise (0)
      // dont do amr
      safeDisable(physics, AmrModel.class);
    }
  }
  public void set_base(double base_size, Simulation sim){
    ScalarGlobalParameter baseParam = ((ScalarGlobalParameter) sim.get(GlobalParameterManager.class).getObject("base_size"));
    Units units_m = ((Units) sim.getUnitsManager().getObject("m"));
    baseParam.getQuantity().setValueAndUnits(base_size, units_m);
  }
  public void mesh(Simulation sim){ // hitting the mesh button
    MeshPipelineController meshPipelineController = sim.get(MeshPipelineController.class);
    meshPipelineController.generateVolumeMesh();
  }
  public void clear(Simulation sim){
    // resets solution, mesh, and adaption
    Solution solution = sim.getSolution();
    solution.clearSolution();
  }
  public void set_turb(int state, PhysicsContinuum physics) {
    if (state == 1) {
        // Switch to k-omega SST
        safeDisable(physics, InviscidModel.class);

        safeEnable(physics, TurbulentModel.class);
        safeEnable(physics, RansTurbulenceModel.class);
        safeEnable(physics, KOmegaTurbulence.class);
        safeEnable(physics, SstKwTurbModel.class);
        safeEnable(physics, KwAllYplusWallTreatment.class);

    } else {
        // Switch to inviscid
        safeDisable(physics, KwAllYplusWallTreatment.class);
        safeDisable(physics, SstKwTurbModel.class);
        safeDisable(physics, KOmegaTurbulence.class);
        safeDisable(physics, RansTurbulenceModel.class);
        safeDisable(physics, TurbulentModel.class);

        safeEnable(physics, InviscidModel.class);
    }
  }

  /*
    A few notes on these functions due to some Java syntax and API quirks:

    Model is a base class in the STAR-CCM+ API that all physics model classes extend
    (e.g. TurbulentModel, InviscidModel, SstKwTurbModel all inherit from it).

    <T extends Model> is a bounded type parameter — it tells Java that T can be
    any specific class that extends Model. Class<T> is then the class literal of
    that concrete type (e.g. SstKwTurbModel.class). This lets us pass different
    model class definitions into the same method without losing type safety.

    hasModel() is misleadingly named — it does NOT return a boolean. It returns
    the live instance of that model (typed as T) if it is active on the continuum,
    or null if it is not present. We use that null check to determine presence,
    and conveniently reuse the returned instance directly in disableModel().
*/
  private <T extends Model> void safeDisable(PhysicsContinuum physics, Class<T> modelClass) {
    T model = physics.getModelManager().hasModel(modelClass);
    if (model != null) {
      physics.disableModel(model);  // disableModel needs the instance anyway — two birds, one stone
    }
  }

  private <T extends Model> void safeEnable(PhysicsContinuum physics, Class<T> modelClass) {
    if (physics.getModelManager().hasModel(modelClass) == null) {
      physics.enable(modelClass);
    }
  }
}