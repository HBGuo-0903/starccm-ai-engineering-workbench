/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import star.base.neo.ClientServerObject;
import star.base.neo.ClientServerObjectKey;
import star.common.Simulation;
import star.common.StarMacro;

/**
 *
 * @author aarong
 */
public class IdentifyCSO extends StarMacro {

    Simulation _sim;
    ClientServerObject _cso;

    @Override
    public void execute() {
        _sim = getActiveSimulation();
        getCSOKey();
        identifyCSO();
    }

    private void getCSOKey() {
        GetKeyGUI gui = new GetKeyGUI();

        try {
            SwingUtilities.invokeAndWait(gui);
            if (gui.obj != null) {
                _cso = gui.obj;
            }
        } catch (InterruptedException ex) {
        } catch (InvocationTargetException ex) {
        }
    }

    private void identifyCSO() {
        
        ArrayList<ClientServerObject> list = new ArrayList<ClientServerObject>();
       
        ClientServerObject parent = _cso.getParent();
        list.add(parent);
        
        do {
            parent = parent.getParent();
            list.add(parent);            
        } while (parent != _sim);
        
        for (int i = list.size() - 1; i >= 0; i--) {
            _sim.print(list.get(i).getPresentationName() + " > ");
        }
        
        _sim.print(_cso.getPresentationName());
    }

    private class GetKeyGUI implements Runnable {

        String key;
        ClientServerObject obj;

        public GetKeyGUI() {
            key = null;
        }

        @Override
        public void run() {

            key = JOptionPane.showInputDialog("Enter a ClientServerObjectKey:");
            ClientServerObjectKey csoKey = _sim.getObjectRegistry().generateObjectKey(key);
            if (_sim.getObjectRegistry().hasObject(csoKey) != null) {
                obj = _sim.getObjectRegistry().getObject(csoKey);
            }
            ClientServerObject cso = _sim.getObjectRegistry().getObject(csoKey);
        }

        public String getCS0KeyAsString() {
            return key;
        }

        public ClientServerObject getCSO() {
            return obj;
        }
    }
}
