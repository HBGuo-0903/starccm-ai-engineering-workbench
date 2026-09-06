/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package macros.jira;

import core.Config;
import java.awt.BorderLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.swing.SwingUtilities;
import star.base.neo.ClientServerObject;
import star.base.query.CompoundOperator;
import star.base.query.CompoundPredicate;
import star.base.query.FromQuerySelectorDescriptor;
import star.base.query.Query;
import star.base.query.QueryFilter;
import star.base.query.QueryFilterManager;
import star.base.query.QueryPredicate;
import star.base.query.TypeOperator;
import star.base.query.TypePredicate;
import star.cadmodeler.CoordinateDesignParameter;
import star.cadmodeler.DesignParameter;
import star.cadmodeler.SolidModelPart;
import star.cadmodeler.VectorQuantityDesignParameter;
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
import star.meshing.CadPart;
import star.meshing.LeafMeshPart;
import star.meshing.MeshPart;

/**
 *
 * @author aarong
 * 
 * Modified to work with 10.01.038+.
 */
public class OpenObjectSelectorForDesignParameter extends StarMacro {
    Simulation sim;
    
    @Override
    public void execute() {
        sim = getActiveSimulation();
               
        createFilter("Fileter", getQueryPredicate());
        
        Query query = new Query(new CompoundPredicate(CompoundOperator.Or, getQueryPredicate()),
            Collections.<Query.Modifier>emptySet());
        
        final ObjectSelectDialog dialog = new ObjectSelectDialog(query, "Select Base Sizes");
        
        try {
            SwingUtilities.invokeAndWait(new Runnable(){
                @Override
                public void run(){
                    dialog.show(SimulationProcessNodeManager.getSingleton().getSimulationNode(sim).getSimulationProcessObject());
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            
        }
        
    }
        
    protected ArrayList<QueryPredicate> getQueryPredicate() {
        ArrayList<QueryPredicate> queryPredicateList = new ArrayList<>();
        
        QueryPredicate notVQDP = new TypePredicate(TypeOperator.IsNot, VectorQuantityDesignParameter.class);
        QueryPredicate notCDP = new TypePredicate(TypeOperator.IsNot, CoordinateDesignParameter.class);
        
        for (Class c : getStarClass()) {
            ArrayList<QueryPredicate> list = new ArrayList<>();
            list.add(notVQDP);
            list.add(notCDP);
            list.add(new TypePredicate(TypeOperator.Is, c));
            queryPredicateList.add(new CompoundPredicate(CompoundOperator.And, list));
        }
        return queryPredicateList;
    }
    
    private void createFilter(String filterName, ArrayList<QueryPredicate> query) {
        try {
            QueryFilter qf = Config.getActiveProject().getSimulation().get(QueryFilterManager.class).getObject(filterName);
            Config.getActiveProject().getSimulation().get(QueryFilterManager.class).remove(qf);
        } catch (Exception e) {
        }
        Query totalQuery = new Query(new CompoundPredicate(CompoundOperator.Or, query), Collections.<Query.Modifier>emptySet());
        QueryFilter qf = Config.getActiveProject().getSimulation().get(QueryFilterManager.class).createQueryFilter(filterName, totalQuery);
    }
    
    protected Class[] getStarClass() {
        return new Class[]{DesignParameter.class};
    }
    
    @StarDialog(title = "Base Size Object Selector")
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
