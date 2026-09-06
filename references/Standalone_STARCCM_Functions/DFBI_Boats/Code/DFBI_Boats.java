/**
 * DFBI simulation for planing boat hulls
 * 
 * @author Andrew Gunderson
 * 
 * 2017, v11.06
 */

import java.util.*;
import macroutils.*;
import star.common.*;
import star.vis.*;

public class DFBI_Boats extends StarMacro {
    /* STATIC PITCH AND HEAVE */
    double pitch0 = -.8; // deg
    double waterline = 24.2; // in
    
    /* RUN MATRIX */
    double[] speedsForward = {1, 2, 3}; // fps
    double[] speedsAft = {.5, 1, 2}; // fps
    double[] speedsOblique = {.5, 1, 2}; // fps
    double[] staticRolls = {3, 6, 9, 12}; // deg
    double[] dynRolls = {-6, 6}; // deg
    double[] pitches = {pitch0-2, pitch0-1, pitch0+1, pitch0+2}; // deg
    
    /* BOAT SPECS */
    double length = 28 * 12;  // approximate keel length (in) 2x front, 5x back
    double beam = 9 * 12;     // approximate beam length at the chines (in) 6x
    double height = 5.5 * 12;   // approximate total height (in) 5x
    double weight = 12003; // boat weight (lb)
    
    /* DOFs */
    boolean pitchDOF = false;
    boolean heaveDOF = false;
    boolean yawDOF   = false;
    boolean rollDOF  = false;
    boolean swayDOF  = false;
    boolean surgeDOF = false;

    /* PICTURE IMAGE RES */
    int resx  = 1200;
    int resy = 300;
    
    public void execute() {
        
    staticRollStability();
    staticPitchStability();
    rollResistance();
    forwardMotion();
    aftMotion();
    swayMotion();
    obliqueMotion();
    
    }

    void staticRollStability() {
        if (mu.check.is.simFile("staticRoll_")) {
            return;
        }
        title = "staticRollStability";
        for (double roll : staticRolls) {
            run(roll, 0, 0);
        }
    }
    
    void staticPitchStability() {
        title = "staticRollStability";
        for (double pitch : pitches) {
            run(0, pitch, 0);
        }
    }
    
    void rollResistance() {
        title = "rollResistance";
        yaw = 90.;
        for (double roll : dynRolls) {
        run(0, roll, yaw);
        }
    }
    
    void forwardMotion() {
        title = "forwardMotion";
        
    }
    
    void aftMotion() {
        title = "aftMotion";
        
    }
    
    void swayMotion() {
        title = "swayMotion";
        
    }
    
    void obliqueMotion() {
        title = "obliqueMotion";
        
    }
    
    void run(double roll, double pitch, double yaw) {
        initMacro();
        physics();
        mesh();
        monitors();
        solve();
        post();
        mu.saveSim();
    }
    
    void initMacro() {
        mu = new MacroUtils(getActiveSimulation());
        ud = mu.userDeclarations;
        mu.set.userDefault.pictureResolution(resx, resy);
        mu.set.userDefault.tessellation(
                StaticDeclarations.Tessellation.DISTANCE_BIASED);
        ud.defUnitLength = ud.unit_in;
        // add the geometry from working directory
        mu.add.geometry.importPart("geom.x_b");
    }
    
    void physics() {
        ud.physCont = mu.add.physicsContinua.generic(
                StaticDeclarations.Space.THREE_DIMENSIONAL,
                StaticDeclarations.Time.IMPLICIT_UNSTEADY, 
                StaticDeclarations.Material.VOF_AIR_WATER,
                StaticDeclarations.Solver.SEGREGATED, 
                StaticDeclarations.Density.CONSTANT,
                StaticDeclarations.Energy.ISOTHERMAL, 
                StaticDeclarations.Viscous.SST_KW);
        mu.enable.cellQualityRemediation(ud.physCont, true);
    }

    void mesh() {
        if (mu.check.has.volumeMesh()) {
            return;
        }
        // create domain
        ud.geomPrt2 = mu.add.geometry.block(
                new double[] {-length*2, -beam*4, -height*5}, 
                new double[] {length*2, beam*4, height*5}, null);
        ud.geomPrt2.setPresentationName("Domain");
        // split part surfaces
        mu.get.partSurfaces.byREGEX(
                ud.geomPrt, ".*", true).setPresentationName("Faces");
        mu.set.geometry.splitPartSurfacesByAngle(
                mu.get.partSurfaces.all(ud.geomPrt, true), 70, true);
        // set outlet
        ud.partSrf = mu.get.partSurfaces.byRangeMin(
                mu.get.partSurfaces.allByREGEX(ud.geomPrt, ".*", true),
                StaticDeclarations.Axis.X, 1.);
        ud.partSrf.setPresentationName("Outlet");
        // set inlet
        ud.partSrf = mu.set.geometry.combinePartSurfaces(
                mu.get.partSurfaces.allByREGEX(
                        ud.geomPrt, "Faces.*", true), true);
        ud.partSrf.setPresentationName("Inlet");        
        // create volumetric mesh controls
        ud.geomPrt = mu.add.geometry.block(
                new double[] {-length*2, -beam*4, waterline-5}, 
                new double[] {length*2, beam*4, waterline+5}, null);
        ud.geomPrt.setPresentationName("Waterline Refine");
        ud.geomPrt1 = mu.add.geometry.block(
                new double[] {-length, -beam, -height}, 
                new double[] {length*2, beam, height*0.4}, null);
        ud.geomPrt1.setPresentationName("Hull Refine");
        // create subtract
        ud.geometryParts.clear();
        ud.geometryParts.add(mu.get.geometries.byREGEX("(?i).*hull.*", true));
        ud.geometryParts.add(ud.geomPrt2);
        ud.mshOpPrt = mu.add.meshOperation.subtract(ud.geometryParts, ud.geomPrt2);
        // assign subtract to region
        ud.region = mu.add.region.fromPart(ud.mshOpPrt,
                StaticDeclarations.BoundaryMode.ONE_FOR_EACH_PART_SURFACE,
                StaticDeclarations.InterfaceMode.CONTACT,
                StaticDeclarations.FeatureCurveMode.ONE_FOR_ALL, true);
---
        ud.geometryParts = mu.get.geometries.all(true);
        mu.add.scene.geometry();
        ud.mshBaseSize = .5; // in
        ud.prismsLayers = 6;
        ud.prismsRelSizeHeight = 33;
        ud.prismsStretching = 1.5;
        ud.autoMshOp = mu.add.meshOperation.automatedMesh(ud.geometryParts,
                StaticDeclarations.Meshers.SURFACE_REMESHER,
                StaticDeclarations.Meshers.POLY_MESHER,
                StaticDeclarations.Meshers.PRISM_LAYER_MESHER);
        // set hull volume control
        ud.geometryParts.clear();
        ud.geometryParts.add(ud.geomPrt1);
        mu.add.meshOperation.volumetricControl(ud.autoMshOp, ud.geometryParts, 100);
        // set waterline volume control
        ud.geometryParts.clear();
        ud.geometryParts.add(ud.geomPrt);
        mu.add.meshOperation.volumetricControl(ud.autoMshOp, ud.geometryParts, 100);
        
        mu.set.boundary.asMassFlowInlet(
                mu.get.boundaries.byREGEX(".*" + ud.bcInlet, true), 
                mfr, 20.0, 0.05, 10.0);
        mu.set.boundary.asPressureOutlet(
                mu.get.boundaries.byREGEX(".*" + ud.bcOutlet, true),
                0.0, 21.0, 0.05, 10.0);
        mu.update.volumeMesh();
        ud.scene = mu.add.scene.mesh();
    }
    
    void monitors() {
        // choose newly created region for pre-defined plane sections
        for (Part ps : sections){
            ud.plane = (PlaneSection) ps;
            ud.plane.getInputParts().setObjects(ud.region);
            
        }
        
    }

    void solve() {
        if (mu.check.has.solution()) {
            return;
        }
        
        ud.maxIter = 2000;
        //mu.set.solver.aggressiveSettings();
        mu.run();
        mu.saveSim();
    }

    void post() {
        
        mu.io.read.cameraViews("myCameras.txt");
        
        // create resampled volume scene
        ResampledVolumePart resVol = (ResampledVolumePart) mu.getSimulation().getPartManager().createResampledVolumePart();
        resVol.getInputParts().setObjects(ud.region);
        ud.namedObjects.add(resVol);
        ud.ff = mu.get.objects.fieldFunction(StaticDeclarations.Vars.VEL.getVar(), true);
        ud.scene = mu.add.scene.scalar(ud.namedObjects, ud.ff, ud.unit_mps, true);
        ud.scene.setPresentationName("Velocity Contours");
        scd = (ScalarDisplayer) mu.get.scenes.displayerByREGEX(ud.scene, ".*", true);
        scd.getScalarDisplayQuantity().setClip(false);
        scd.getScalarDisplayQuantity().setRange(new double[]{0,2});
        for (VisView vv : mu.get.cameras.allByREGEX(".*(1|2)", true)) {
            mu.set.scene.cameraView(ud.scene, vv, true);
            mu.io.sleep(1000);
            mu.io.write.picture(ud.scene, version + flowRate + " " + vv.getPresentationName(), resx, resy, true);
        }
        
        // create streamline scene
        pd1 = mu.add.scene.displayer_Geometry(ud.scene);
        pd1.setOpacity(1);
        pd1.setColorMode(PartColorMode.DEFAULT);

        pd2 = (PartDisplayer) mu.add.scene.displayer_Geometry(ud.scene);
        pd2.copyProperties(pd1);
        pd2.setMesh(true);
        pd2.setPresentationName("Mesh");
        pd2.setOpacity(0);

        ud.namedObjects2.add(mu.get.boundaries.byREGEX(".*" + ud.bcInlet, true));
        ud.namedObjects2.addAll(mu.get.regions.all(true));
        ud.postStreamlinesTubesWidth = 0.0005;
        std = mu.add.scene.displayer_Streamline(ud.scene, ud.namedObjects2, true);
        mu.templates.prettify.all();
        std.getScalarDisplayQuantity().setRange(new double[]{0, 3.0});
        std.getAnimationManager().setMode(StreamDisplayerAnimationMode.TRACER);
        std.setLegendPosition(scd.getLegend().getPositionCoordinate());
        std.setVisibilityOverrideMode(DisplayerVisibilityOverride.HIDE_ALL_PARTS);
        //--
    }
    
    private MacroUtils mu;
    private UserDeclarations ud;

    String title;
    Double yaw;
}
    

