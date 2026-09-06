
/**
 * ITC Prop RPM Parametric Run
 *
 * @author Andrew Gunderson
 *
 * 2017, v12.02
 */
import macroutils.*;
import star.common.*;
import star.flow.*;
import star.motion.*;
import star.vis.*;

public class Itc_Props extends StarMacro {

    double[] rpms = {3429.}; // rpm
    double speed = 3.; // mph
    double rpm_wot = 3429.;
    double mfr_wot = 0.3; // kgps
    double degPerTstep = 1;
    double revs = 10;
    boolean rightHanded = true;

    public void execute() {
        varyRPM();
    }

    void varyRPM() {
        mu = new MacroUtils(getSimulation());
        ud = mu.userDeclarations;

        ud.defColormap = mu.get.objects.colormap(
                StaticDeclarations.Colormaps.BLUE_RED);

        // clear previous solution and mesh (if no mesh exists)
        //mu.clear.solution();
        mu.update.volumeMesh();
        for (Displayer d : mu.get.scenes.allDisplayers(vo)) {
            d.setRepresentation(mu.get.mesh.fvr());
        }
        mu.saveSim();

        // set inflow speed
        mu.get.boundaries.byREGEX("Inlet", vo)
                .getValues().get(VelocityProfile.class)
                .getMethod(ConstantVectorProfileMethod.class)
                .getQuantity().setComponents(speed, 0., 0.);
        mu.get.objects.physicsContinua("Physics 1", vo)
                .getInitialConditions().get(VelocityProfile.class)
                .getMethod(ConstantVectorProfileMethod.class)
                .getQuantity().setComponents(speed, 0., 0.);

        // set params for handedness of prop rotation
        rm = (RotatingMotion) mu.getSimulation().get(
                MotionManager.class).getObject("Rotation");
        MomentReport mrep = (MomentReport) mu.getSimulation()
                .getReportManager().getReport("Prop_Torque");
        if (rightHanded) {
            rm.getAxisDirection().setComponents(-1., 0., 0.);
            mrep.getDirection().setComponents(1., 0., 0.);
        } else {
            rm.getAxisDirection().setComponents(1., 0., 0.);
            mrep.getDirection().setComponents(-1., 0., 0.);
        }

        // get exhaust inlet boundary
        ud.bdry = mu.get.boundaries.byREGEX("Inlet_Exhaust", true);

        for (double rpm : rpms) {

            // set rpm, timestep, and exhaust mass flow rate
            rm.getRotationRate().setValue(rpm);
            tStep = 60. / 360. / rpm * degPerTstep;
            mu.set.solver.timestep(tStep);
            mfr = Math.pow(rpm / rpm_wot, 3) * mfr_wot;
            mu.set.boundary.values(ud.bdry,
                    StaticDeclarations.Vars.MFR, mfr, ud.unit_kgps);

            //mu.clear.solutionHistory();
            int steps = (int) (revs * 360 / degPerTstep);
            mu.step(steps);

            // output csv data
            MonitorPlot propPlot = (MonitorPlot) mu.get.plots.byREGEX("Prop", vo);
            propPlot.export(ud.simPath + "/" + rpm + "rpm_prop.csv", ",");
            MonitorPlot gcPlot = (MonitorPlot) mu.get.plots.byREGEX("Gearcase", vo);
            gcPlot.export(ud.simPath + "/" + rpm + "rpm_gc.csv", ",");

            //ud.simTitle = rpm + "rpm";
            mu.saveSim();
        }
    }

    MacroUtils mu;
    UserDeclarations ud;
    RotatingMotion rm;
    double mfr;
    double tStep;
    boolean vo = true;

}
