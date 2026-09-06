
import java.util.*;
import macroutils.*;
import star.common.*;
import star.meshing.*;
import star.motion.*;
import star.vis.*;

public class Useful_Stuff extends StarMacro {

    public void execute() {

        initMacro();

        // sort derived parts for use in for loops
        ud.Parts = mu.get.parts.allByREGEX("(?i).*flow.*", vo);
        mu.io.say.objects(ud.Parts, "parts", vo);
        Collections.sort(ud.Parts);
        mu.io.say.objects(ud.Parts, "parts", vo);

        // set mesh mode to serial
        ud.autoMshOp = (AutoMeshOperation) mu.get.mesh.operation("Automated Mesh", vo);
        ud.autoMshOp.getMesherParallelModeOption().setSelected(
                MesherParallelModeOption.Type.SERIAL);

        // set motion
        Motion m = null;
        mu.set.region.motion(m, mu.get.regions.byREGEX(".*rotating.*", true));

        // get value of report
        ud.rep.getReportMonitorValue();

        // disable autosave
        mu.getSimulation().getSimulationIterator()
                .getAutoSave().getStarUpdate().setEnabled(false);

        // set volume mesh repr for all displayers
        for (Displayer d : mu.get.scenes.allDisplayers(vo)) {
            d.setRepresentation(mu.get.mesh.fvr());
        }
        
        // set preferred units
        Units units = null;
        units.setPreferred(true);
    }

    void initMacro() {
        mu = new MacroUtils(getSimulation());
        ud = mu.userDeclarations;
    }

    private MacroUtils mu;
    private UserDeclarations ud;
    boolean vo = true;

}
