/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import star.base.neo.DoubleVector;
import star.base.neo.IntVector;
import star.base.neo.NeoObjectVector;
import star.common.CartesianCoordinateSystem;
import star.common.Coordinate;
import star.common.LabCoordinateSystem;
import star.common.OrthoNormalBasis;
import star.common.Region;
import star.common.Simulation;
import star.common.StarMacro;
import star.common.Units;
import star.vis.ConstrainedPlaneSection;

/**
 *
 * @author aarong
 */
public class ConstrainedPlane extends StarMacro {

    @Override
    public void execute() {
        Simulation simulation_0 = getActiveSimulation();
        Units units_0 = simulation_0.getUnitsManager().getPreferredUnits(new IntVector(new int[]{0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));
        ConstrainedPlaneSection constrainedPlaneSection_1 = (ConstrainedPlaneSection) simulation_0.getPartManager().createConstrainedPlaneImplicitPart(new NeoObjectVector(new Object[]{}), new DoubleVector(new double[]{}), units_0);
        constrainedPlaneSection_1.setReevaluateStatus(false);

        LabCoordinateSystem labCoordinateSystem_0 = simulation_0.getCoordinateSystemManager().getLabCoordinateSystem();
        CartesianCoordinateSystem cartesianCoordinateSystem_0 = ((CartesianCoordinateSystem) labCoordinateSystem_0.getLocalCoordinateSystemManager().getObject("Cartesian 1"));
        constrainedPlaneSection_1.setCoordinateSystem(cartesianCoordinateSystem_0);

        Region region_0 = simulation_0.getRegionManager().getRegion("Viz");
        Region region_1 = simulation_0.getRegionManager().getRegion("Wind Tunnel");
        constrainedPlaneSection_1.getInputParts().setObjects(region_0, region_1);

        Coordinate coordinate_5 = constrainedPlaneSection_1.getOriginCoordinate();
        coordinate_5.setCoordinateSystem(cartesianCoordinateSystem_0);
        coordinate_5.setValue(new DoubleVector(new double[]{0.0, 0.0, -33.341}));
        coordinate_5.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[]{0.0, 0.0, -33.341}));

        Coordinate coordinate_6 = constrainedPlaneSection_1.getNormalCoordinate();
        coordinate_6.setCoordinateSystem(cartesianCoordinateSystem_0);
        coordinate_6.setValue(new DoubleVector(new double[]{0.0, 0.0, 1.0}));
        coordinate_6.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[]{0.0, 0.0, 1.0}));

        OrthoNormalBasis orthoNormalBasis_1 = constrainedPlaneSection_1.getPlaneBasis();
        orthoNormalBasis_1.setBasisVectors(new DoubleVector(new double[]{-8.429369496854022E-8, -0.9999989266046683, 0.00146519265086231, 2.1684043449710089E-19, 0.0014651926508622575, 0.9999989266046719, -0.9999999999999966, 8.429360448808218E-8, -1.2350650216508977E-10}));
        
        constrainedPlaneSection_1.setLoop(new DoubleVector(new double[]{-35.93751174251017, -17.616 + 1.493, 1.8732, -35.93751133715688, -10.630 - 1.493, 1.8732, -35.93751133428441, -10.630 - 1.493, -1.1268, -35.93751174614337, -17.616 + 1.493, -1.1268}));

    }

}
