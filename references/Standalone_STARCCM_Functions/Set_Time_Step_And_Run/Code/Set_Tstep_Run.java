
import macroutils.*;
import star.common.*;
import star.vis.*;

public class Set_Tstep_Run extends StarMacro {

    public void execute() {
        initMacro();
        if (!mu.check.has.volumeMesh()) {
            mu.update.volumeMesh();
        }
        for (Displayer d : mu.get.scenes.allDisplayers(vo)) {
            d.setRepresentation(mu.get.mesh.fvr());
        }
        
        mu.set.solver.timestep(1/(3657./60.*360./.125));
        
        mu.clear.solutionHistory();
        mu.step(2880);
        
        ud.simTitle = ud.simTitle + "_tStep.125";

        mu.io.write.picture(mu.get.plots.byREGEX("Prop", vo),
                ud.simTitle, ud.picResX, ud.picResY, vo);

        String fileName = ud.simPath + "/" + ud.simTitle;
        MonitorPlot propPlot = (MonitorPlot) mu.get.plots.byREGEX("Prop", vo);
        propPlot.export(fileName + "_prop.csv", ",");
        MonitorPlot gcPlot = (MonitorPlot) mu.get.plots.byREGEX("Gearcase", vo);
        gcPlot.export(fileName + "_gc.csv", ",");

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
