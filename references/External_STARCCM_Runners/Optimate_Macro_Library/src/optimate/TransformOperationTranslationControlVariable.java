/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimate;

import core.apiFramework.SelectorTitle;
import core.apiFramework.properties.StringKey;
import star.base.neo.ClientServerObject;
import star.meshing.TranslationControl;
import parameters.var.user.CustomVectorVariable;

/**
 *
 * @author aarong
 */
@SelectorTitle(title = "Select a Translation Control Operation")
public class TransformOperationTranslationControlVariable extends CustomVectorVariable {

    /**
     * Property key that can be paired with String values to indicate the units
     * of this variable. Cannot be set, only retrieved.
     */
    public static final StringKey UNIT_NAME = new StringKey("unit name", true);
    
    public TransformOperationTranslationControlVariable(TranslationControl cso, String key, int index) {
        super(cso, key, index);
        set(UNIT_NAME, cso.getTranslationVector().getUnits(index).getPresentationName());
        allowMultipleReferences(true);
    }
    
    public TransformOperationTranslationControlVariable() {
        super();
        allowMultipleReferences(true);
    }
    
    @Override
    protected Class[] getStarClass() {
        return new Class[] {TranslationControl.class};
    }

    @Override
    protected String getFilterName() {
        return "Translation Controls";
    }

    @Override
    protected String getStarObjectMissingMessage() {
        return "Tranlation Control not found!";
    }

    @Override
    protected void setPropertyValuesForNewMapping(ClientServerObject cso) {
        if (cso instanceof TranslationControl) {
            TranslationControl t = (TranslationControl) cso;
            set(UNIT_NAME, t.getTranslationVector().getUnits(getVectorIndex()).getPresentationName());
        }
    }
    
    @Override
    public String exportAPIToUpdate(String doubleName, String objName) {
        String s;
        s = "double[] vector = " + objName + ".getTranslationVector().getVector().toDoubleArray();\n";
        s += "vector[" + getVectorIndex() + "] = d;\n";
        s += "String u = \"" + get(UNIT_NAME) + "\";\n";
        s += objName + ".getTranslationVector().setCoordinate(u, u, u, new DoubleVector(new double[] {vector[0], vector[1], vector[2]}));\n";
        return s;
    }

    @Override
    protected double getCurrentValueInSimulation(ClientServerObject cso) {
        return ((TranslationControl) cso).getTranslationVector().getVector().toDoubleArray()[getVectorIndex()];
    }    
}