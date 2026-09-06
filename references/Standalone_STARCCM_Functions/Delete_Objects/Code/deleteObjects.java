// Delete all scenes, continua, mesh operations
// Be careful: this script will not ask anything to users before removing everyting

import java.util.*;

import star.base.neo.*;
import star.common.*;
import star.meshing.*;
import star.vis.*;

public class deleteObjects extends StarMacro {

    public void execute() {
		deleteObjects();
	}
	public void deleteObjects()
	{
        Simulation sim = getActiveSimulation();

        // Delete all scenes
        if (!sim.getSceneManager().isEmpty()) {
            
            Collection<Scene> Scenes = sim.getSceneManager().getObjects();			
			
            for (Scene s : Scenes) {
                if (!s.getPresentationName().equals("Geometry Scene 1")) {
                sim.getSceneManager().deleteScene(s);
                sim.println("Deleted scene " + s.getPresentationName() + ".");
                }
            }            
            sim.getSceneManager().deleteScenes(sim.getSceneManager().getObjectsOf(Scene.class));
            sim.println("All scenes sucessfully deleted");
            sim.println("");
        }
        // Delete all continua
        if (!sim.getContinuumManager().isEmpty()) {
            Collection<Continuum> Continua = sim.getContinuumManager().getObjects();
            if (!sim.getRegionManager().isEmpty()) {
                for (Continuum c : Continua) {
                    // keep Physics 1 as a default continuum
                    if (!c.getPresentationName().equals("Physics 1")) {                        
                        sim.getContinuumManager().eraseFromContinuum(new NeoObjectVector(sim.getRegionManager().getObjects().toArray()), c);
                        sim.getContinuumManager().removeObjects(c);
                        sim.println("Deleted continuum " + c.getPresentationName() + ".");
                    }
                }
            }
            sim.println("All physics continua sucessfully detelted.");
			sim.println("");            		
        }
		
        // Delete all mesh operations
        if(!sim.get(MeshOperationManager.class).isEmpty()){
            Collection<MeshOperation> Operations = sim.get(MeshOperationManager.class).getObjects();
            for(MeshOperation m : Operations) {
                sim.get(MeshOperationManager.class).removeObjects(m);
				sim.println("Deleted operation" + m.getPresentationName() + ".");
            }
            sim.println("All operations sucessfully deleted");
        }
    }
}