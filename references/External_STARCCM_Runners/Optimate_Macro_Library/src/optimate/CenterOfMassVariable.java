/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimate;

import core.apiFramework.SelectorTitle;
import core.apiFramework.properties.StringKey;
import parameters.var.user.CustomVectorVariable;
import star.base.neo.ClientServerObject;
import star.sixdof.CenterOfMass;

/**
 *
 * @author yuvrajd
 */
@SelectorTitle(title = "Select a DFBI Center of Mass Position")
public class CenterOfMassVariable extends CustomVectorVariable {

    public static final StringKey UNIT_NAME = new StringKey("unit name", true);

    public CenterOfMassVariable(CenterOfMass cso, String key, int index) {
        super(cso, key, index);
        set(UNIT_NAME, cso.getPositionInput().getUnits(index).getPresentationName());
        allowMultipleReferences(true);
    }

    public CenterOfMassVariable() {
        super();
        allowMultipleReferences(true);
    }

    @Override
    protected Class[] getStarClass() {
        return new Class[]{CenterOfMass.class};
    }

    @Override
    protected double getCurrentValueInSimulation(ClientServerObject cso) {
        return ((CenterOfMass) cso).getPositionAsVector().toDoubleArray()[getVectorIndex()];
    }

    @Override
    public String exportAPIToUpdate(String doubleName, String CSOName) {
        String s;
        s = "Coordinate centerOfMassCoordinate = " + CSOName + ".getPosition();\n";
        s += "double[] vector = " + CSOName + ".getPositionAsVector().toDoubleArray();\n";
        s += "vector[" + getVectorIndex() + "] = d;\n";
        s += "Units u = sim.getUnitsManager().getUnits(\"" + get(UNIT_NAME) + "\");\n";
        s += "centerOfMassCoordinate.setCoordinate(u, u, u, new DoubleVector(new double[] {vector[0], vector[1], vector[2]}));\n";
        return s;
    }

    @Override
    protected String getStarObjectMissingMessage() {
        return "DFBI Body not found!";
    }

    @Override
    protected String getFilterName() {
        return "DFBI Center of Mass";
    }

    @Override
    protected void setPropertyValuesForNewMapping(ClientServerObject cso) {
        if (cso instanceof CenterOfMass) {
            CenterOfMass com = (CenterOfMass) cso;
            set(UNIT_NAME, com.getPositionInput().getUnits(getVectorIndex()).getPresentationName());
        }
    }

}
