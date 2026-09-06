
import macroutils.*;
import star.common.*;
import star.vis.*;

public class Run extends StarMacro {

    public void execute() {
        initMacro();

        //mu.update.volumeMesh();
        
        ud.defColormap = mu.get.objects.colormap(
                StaticDeclarations.Colormaps.BLUE_RED);

        for (Displayer d : mu.get.scenes.allDisplayers(vo)) {
            d.setRepresentation(mu.get.mesh.fvr());
        }

        mu.step(360);

        mu.saveSim();
    }

    void initMacro() {
        mu = new MacroUtils(getSimulation(), intrusive);
        ud = mu.userDeclarations;
    }

    MacroUtils mu;
    UserDeclarations ud;
    boolean vo = true;
    boolean intrusive = true;
}
