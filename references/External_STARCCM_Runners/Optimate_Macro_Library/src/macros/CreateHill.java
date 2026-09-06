/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import java.util.ArrayList;
import star.base.neo.DoubleVector;
import star.base.neo.IntVector;
import star.cadmodeler.Body;
import star.cadmodeler.CadModel;
import star.cadmodeler.CadModelCoordinate;
import star.cadmodeler.CanonicalSketchPlane;
import star.cadmodeler.ExtrusionMerge;
import star.cadmodeler.Face;
import star.cadmodeler.LineSketchPrimitive;
import star.cadmodeler.PointSketchPrimitive;
import star.cadmodeler.Sketch;
import star.cadmodeler.SolidModelManager;
import star.cadmodeler.SplineSketchPrimitive;
import star.common.LabCoordinateSystem;
import star.common.Simulation;
import star.common.StarMacro;
import star.common.Units;

/**
 *
 * @author aarong
 */
public class CreateHill extends StarMacro {

    double domainHeight = 40.0;
    double preRoadLength = 5.0;
    double roadWidth = 20.0;
    double preHillWidth = 5.0;
    double hillHeight = 5.0;
    double hillWidth = 15.0;
    double postHillLength = 100.0;
    double omega = Math.PI / hillWidth;

    ArrayList<Double> hillVals = new ArrayList<Double>();

    Units units;
    Simulation sim;

    @Override
    public void execute() {
        try {
            sim = getActiveSimulation();

            CadModel domain = sim.get(SolidModelManager.class).createSolidModel();
            units = sim.getUnitsManager().getPreferredUnits(new IntVector(new int[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));
            computeHill();
            Sketch s = drawSketch(domain);
            extrudeDomain(domain, s);
            renameFaces(domain);
        } catch (Exception ex) {
            misc.Utilities.printStackTrace(ex, sim);
        }
    }

    private void computeHill() {

        hillVals.add(preRoadLength + roadWidth + preHillWidth);
        hillVals.add(0.0);

        int res = 5;
        double inc = hillWidth / res;
        double x = inc;
        int i = 0;

        while (x <= hillWidth) {
            double d1 = hillVals.get(i) + inc;
            double d2 = getHillHeight(x);
            hillVals.add(d1);
            hillVals.add(d2);
            x += inc;
            i += 2;
        }
    }

    private double getHillHeight(double x) {
        return hillHeight * Math.sin(omega * x);
    }

    private Sketch drawSketch(CadModel m) {
        CanonicalSketchPlane xyPlane = (CanonicalSketchPlane) m.getFeatureManager().getObject("XY");
        Sketch sketch = m.getFeatureManager().createSketch(xyPlane);
        m.getFeatureManager().startSketchEdit(sketch);
        PointSketchPrimitive p = drawInlet(sketch);
        p = drawPreRoad(sketch, p);
        p = drawRoad(sketch, p);
        p = drawPreHill(sketch, p);
        p = drawHill(sketch, p);
        p = drawPostHill(sketch, p);
        p = drawOutlet(sketch, p);
        drawTop(sketch, p);
        return sketch;
    }

    private PointSketchPrimitive drawInlet(Sketch s) {
        LineSketchPrimitive inlet = s.createLine(new DoubleVector(new double[]{0.0, 0.0}), new DoubleVector(new double[]{0.0, domainHeight}));
        PointSketchPrimitive origin = ((PointSketchPrimitive) s.getSketchPrimitiveManager().getObject("Point 1"));
        s.createFixationConstraint(origin);
        s.createVerticalConstraint(inlet);
        return origin;
    }

    private PointSketchPrimitive drawPreRoad(Sketch s, PointSketchPrimitive p) {
        LineSketchPrimitive preRoad = s.createLine(p, new DoubleVector(new double[]{preRoadLength, 0.0}));
        s.createHorizontalConstraint(preRoad);
        return preRoad.getEndPoint();
    }

    private PointSketchPrimitive drawRoad(Sketch s, PointSketchPrimitive start) {
        LineSketchPrimitive road = s.createLine(start, new DoubleVector(new double[]{preRoadLength + roadWidth, 0.0}));
        s.createHorizontalConstraint(road);
        return road.getEndPoint();
    }

    private PointSketchPrimitive drawPreHill(Sketch s, PointSketchPrimitive start) {
        LineSketchPrimitive preHill = s.createLine(start, new DoubleVector(new double[]{preRoadLength + roadWidth + preHillWidth, 0.0}));
        s.createHorizontalConstraint(preHill);
        return preHill.getEndPoint();
    }

    private PointSketchPrimitive drawHill(Sketch s, PointSketchPrimitive start) {
        double[] coords = new double[hillVals.size()];

        for (int i = 0; i < hillVals.size(); i++) {
            coords[i] = hillVals.get(i);
        }

        SplineSketchPrimitive spline = s.createSpline(true, start, false, null, new DoubleVector(coords));
        return spline.getEndPoint();
    }

    private PointSketchPrimitive drawPostHill(Sketch s, PointSketchPrimitive start) {
        LineSketchPrimitive postHill = s.createLine(start, new DoubleVector(new double[]{preRoadLength + roadWidth + preHillWidth + hillWidth + postHillLength, 0.0}));
        s.createHorizontalConstraint(postHill);
        return postHill.getEndPoint();
    }

    private PointSketchPrimitive drawOutlet(Sketch s, PointSketchPrimitive start) {
        LineSketchPrimitive outlet = s.createLine(start, new DoubleVector(new double[]{preRoadLength + roadWidth + preHillWidth + hillWidth + postHillLength, domainHeight}));
        s.createVerticalConstraint(outlet);
        return outlet.getEndPoint();
    }

    private void drawTop(Sketch s, PointSketchPrimitive start) {
        s.createLine(start, (PointSketchPrimitive) s.getSketchPrimitiveManager().getObject("Point 2"));
    }

    private void extrudeDomain(CadModel c, Sketch s) {
        ExtrusionMerge em = c.getFeatureManager().createExtrusionMerge(s);

        em.setDirectionOption(1);
        em.setExtrudedBodyTypeOption(0);
        em.getDistance().setValue(1.0);
        em.setDistanceOption(0);
        em.setCoordinateSystemOption(0);
        em.setDraftOption(0);

        LabCoordinateSystem labCoordinateSystem_0 = sim.getCoordinateSystemManager().getLabCoordinateSystem();

        em.setCoordinateSystem(labCoordinateSystem_0);

        CadModelCoordinate cadModelCoordinate_1 = em.getDirectionAxis();
        cadModelCoordinate_1.setCoordinateSystem(labCoordinateSystem_0);
        cadModelCoordinate_1.setCoordinate(units, units, units, new DoubleVector(new double[]{0.0, 0.0, 1.0}));

        em.setFace(null);
        em.setBody(null);
        em.setPostOption(1);
        em.setExtrusionOption(0);

        c.getFeatureManager().execute(em);
    }
    
    private void renameFaces(CadModel c) {
        Body body = c.getBodyManager().getObject("Body 1");
        body.setPresentationName("domain");
        
        ((Face) body.getFaceManager().getObject("Face 1")).setNameAttribute("00_Inlet");
        ((Face) body.getFaceManager().getObject("Face 8")).setNameAttribute("01_Ground");
        ((Face) body.getFaceManager().getObject("Face 6")).setNameAttribute("01_Ground");
        ((Face) body.getFaceManager().getObject("Face 4")).setNameAttribute("01_Ground");
        ((Face) body.getFaceManager().getObject("Face 7")).setNameAttribute("01_Road");
        ((Face) body.getFaceManager().getObject("Face 5")).setNameAttribute("01_Hill");
        ((Face) body.getFaceManager().getObject("Face 3")).setNameAttribute("00_Outlet");
        ((Face) body.getFaceManager().getObject("Face 2")).setNameAttribute("00_Top");
        ((Face) body.getFaceManager().getObject("Face 10")).setNameAttribute("01_Periodic_1");
        ((Face) body.getFaceManager().getObject("Face 9")).setNameAttribute("01_Periodic_2");
    }
}
