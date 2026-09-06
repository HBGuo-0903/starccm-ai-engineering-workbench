/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package macros.jira;

import java.awt.BorderLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.swing.SwingUtilities;
import star.base.neo.ClientServerObject;
import star.base.query.CompoundOperator;
import star.base.query.CompoundPredicate;
import star.base.query.FromQuerySelectorDescriptor;
import star.base.query.Query;
import star.base.query.QueryPredicate;
import star.base.query.TypeOperator;
import star.base.query.TypePredicate;
import star.cadmodeler.CoordinateDesignParameter;
import star.cadmodeler.DesignParameter;
import star.cadmodeler.VectorQuantityDesignParameter;
import star.common.FilterModel;
import star.common.MeshContinuum;
import star.common.ScalarProfile;
import star.common.Simulation;
import star.common.StarMacro;
import star.common.ui.SimulationProcessNodeManager;
import star.coremodule.objectselector.ModelDescriptor;
import star.coremodule.objectselector.ObjectSelector;
import star.coremodule.objectselector.SelectionEvent;
import star.coremodule.objectselector.SelectionListener;
import star.coremodule.objectselector.SelectorUtils;
import star.coremodule.ui.OkCancelSimulationDialog;
import star.coremodule.ui.SimpleSimulationPanel;
import star.coremodule.ui.SimulationPanel;
import star.locale.annotation.StarDialog;
import star.meshing.AutoMeshOperation;
import star.starcad2.StarCadDesignParameterDouble;

/**
 *
 * @author aarong
 * 
 * Modified to work with 10.01.038+.
 */
public class OpenObjectSelectorForOptimateVariablesForCCMP72725 extends StarMacro {
    Simulation sim;
    
    @Override
    public void execute() {
        sim = getActiveSimulation();

        ArrayList<QueryPredicate> queryPredicateList = getPredicates();
                
        Query query = new Query(new CompoundPredicate(CompoundOperator.Or, queryPredicateList),
            Collections.<Query.Modifier>emptySet());
        
        final ObjectSelectDialog dialog = new ObjectSelectDialog(query, "OptimateObjectSelector");
        
        try {
            SwingUtilities.invokeAndWait(new Runnable(){
                @Override
                public void run(){
                    dialog.show(SimulationProcessNodeManager.getSingleton().getSimulationNode(sim).getSimulationProcessObject());
                }
            });
        } catch (InterruptedException ex) {
            
        } catch (InvocationTargetException ex) {
            
        }
        
    }
    
    private ArrayList<QueryPredicate> getPredicates() {
        ArrayList<QueryPredicate> list = new ArrayList<QueryPredicate>();
        
        list.addAll(getDPQP());
        list.addAll(getSPQP());
        list.addAll(getMCQP());
        
        return list;        
    }
    
    private ArrayList<QueryPredicate> getDPQP() {
        ArrayList<QueryPredicate> queryPredicateList = new ArrayList<QueryPredicate>();
        
        QueryPredicate notVQDP = new TypePredicate(TypeOperator.IsNot, VectorQuantityDesignParameter.class);
        QueryPredicate notCDP = new TypePredicate(TypeOperator.IsNot, CoordinateDesignParameter.class);
        
        for (Class c : new Class[] {DesignParameter.class}) {
            ArrayList<QueryPredicate> list = new ArrayList<QueryPredicate>();
            list.add(notVQDP);
            list.add(notCDP);
            list.add(new TypePredicate(TypeOperator.Is, c));
            queryPredicateList.add(new CompoundPredicate(CompoundOperator.And, list));
        }
        return queryPredicateList;
    }
    
    private ArrayList<QueryPredicate> getSPQP() {
        ArrayList<QueryPredicate> queryPredicateList = new ArrayList<QueryPredicate>();
        for (Class c : new Class[] {ScalarProfile.class}) {
            queryPredicateList.add(new TypePredicate(TypeOperator.Is, c));
        }
        return queryPredicateList;
    }
    
    private ArrayList<QueryPredicate> getMCQP() {
        ArrayList<QueryPredicate> queryPredicateList = new ArrayList<QueryPredicate>();
        for (Class c : new Class[] {MeshContinuum.class, AutoMeshOperation.class}) {
            queryPredicateList.add(new TypePredicate(TypeOperator.Is, c));
        }
        return queryPredicateList;
    }
    
    @StarDialog(title = "StarCadDesignParameterDouble selector dialog for CCMP-71011")
    class ObjectSelectDialog extends OkCancelSimulationDialog {

    private final ModelDescriptor _descriptor;
    private SimulationPanel _panel;
    private Collection<ClientServerObject> _selectedObjects;

    public ObjectSelectDialog(Query query, String title) {
        _descriptor = new FromQuerySelectorDescriptor.Builder(sim, query).multiSelect(true).build();
        setTitle(title);
        enableApply(false);
    }

    @Override
    protected SimulationPanel createPanel() {
        _panel = new OSSelectPanel();
        return _panel;
    }

    @Override
    protected void onOk() {
        _panel.onApply();
    }
    
    @Override
    protected void onCancel() {
        super.onCancel();
    }

    public Collection<ClientServerObject> getObjects() {
        return _selectedObjects;
    }

    private class OSSelectPanel extends SimpleSimulationPanel {

        private ObjectSelector selector;

        public OSSelectPanel() {
            super();
            createContent();
        }

        private void createContent() {
            setLayout(new BorderLayout());
            selector = new ObjectSelector(_descriptor);
            selector.addSelectionListener(new SelectionListener() {
                @Override
                public void selectionChanged(SelectionEvent e) {
                    enableApply(selector.hasSelection());
                }
            });

            add(selector, java.awt.BorderLayout.CENTER);

        }

        @Override
        public void onApply() {
            _selectedObjects = SelectorUtils.createObjects(sim, selector.getSelected());
        }
    }
}
    
}
