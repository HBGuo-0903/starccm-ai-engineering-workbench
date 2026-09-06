/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimate;

import parameters.var.user.CustomScalarVariable;
import star.base.neo.ClientServerObject;
import star.segregatedflow.PressureSolver;

/**
 * This class defines an Optimate CustomScalarVariable that can be used in Optimate
 * or Optimate+ studies to control the PressureSolver URF property.
 * <p>
 * This class provides a basic template that can be used to create other
 * CustomScalarVariables. It is meant to illustrate the minimum requirements 
 * to correctly use the CustomScalarVariable API.
 * <p>
 * The entire API provides significantly more customization and control than is 
 * descibed here.
 * 
 * @author aarong
 * @see CustomScalarVariable
 */
public class PressureURFVariable extends CustomScalarVariable {

    /**
     * Constructs a PressureURFVariable and maps it to the supplied PressureSolver
     * and assigns the key to the provided key.
     * 
     * @param cso The PressureSolver that this Variable is going to control.
     * @param key The String value to be this CustomVariable's unique immutable
     * persistent identifier.
     */
    public PressureURFVariable(PressureSolver cso, String key) {
        super(cso, key);
    }
    
    /**
     * Default no-argument constructor.  A no-argument constructor that calls the
     * super classes no argument constructor is required for all CustomVariables.
     */
    public PressureURFVariable() {
        super();
    }
    
    /**
     * Returns a String object that can be inserted in a macro to assign the value
     * of this Variable a double value.  The doubleName argument contains the local
     * variable name for the double value that is to be assigned.  The objName argument
     * contains the local variable name for the PresureSolver who's URF is being
     * updated.
     * <p>
     * The String value may involve multiple lines of code.  In such cases take care
     * to end all lines of code with a semicolon ";" and include all new line "\n"
     * characters.
     * 
     * @param doubleName the local variable name of the double value in the
     * StarDriver.java macro
     * @param objName the local object variable name for the PressureSolver who's URF
     * is being updated.
     * @return a String that can be inserted into the StarDriver.java macro to 
     * update this object.
     */
    @Override
    public String exportAPIToUpdate(String doubleName, String objName) {
        return objName + ".setUrf(" + doubleName + ")";
    }  
    
    /**
     * Returns a Class Array object that contains a list of all of the ClientServerObject
     * Classes that this variable knows how to update.
     * 
     * Please note that for each class in this array an independent constructor
     * is required.
     * @return Class Array of ClientServerObject classes that can up updated by
     * this CustomVariable.
     */
    @Override
    protected Class[] getStarClass() {
        return new Class[] {PressureSolver.class};
    }

    /**
     * Returns the current value of the ClientServerObejct
     * @param cso ClientServerObject who's class is among the <code>getStarClass</code>
     * Classes
     * @return the current value of the property of this object that the CustomVariable controls. 
     */
    @Override
    protected double getCurrentValueInSimulation(ClientServerObject cso) {
        return ((PressureSolver) cso).getUrf();
    }  
}