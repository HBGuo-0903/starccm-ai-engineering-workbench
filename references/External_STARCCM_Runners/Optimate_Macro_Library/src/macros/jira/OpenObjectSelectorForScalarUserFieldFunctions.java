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
import star.base.query.EnumeratedOptionPropertyPredicate;
import star.base.query.EnumeratedOptionValue;
import star.base.query.EnumerationOperator;
import star.base.query.FromQuerySelectorDescriptor;
import star.base.query.Query;
import star.base.query.QueryPredicate;
import star.base.query.RelationshipOperator;
import star.base.query.RelationshipPredicate;
import star.base.query.TypeOperator;
import star.base.query.TypePredicate;
import star.cadmodeler.ScalarQuantityDesignParameter;
import star.common.FieldFunctionTypeOption;
import star.common.Simulation;
import star.common.StarMacro;
import star.common.UserFieldFunction;
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

/**
 *
 * @author aarong
 *
 * Modified to work with 11.01.054+.
 */
public class OpenObjectSelectorForScalarUserFieldFunctions extends StarMacro {

    Simulation sim;

    @Override
    public void execute() {
        sim = getActiveSimulation();

        Query query = new Query(new CompoundPredicate(CompoundOperator.Or, getQueryPredicate()),
                Collections.<Query.Modifier>emptySet());

        final ObjectSelectDialog dialog = new ObjectSelectDialog(query, "Select Base Sizes");

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    dialog.show(SimulationProcessNodeManager.getSingleton().getSimulationNode(sim).getSimulationProcessObject());
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {

        }

    }

    private ArrayList<QueryPredicate> getQueryPredicate() {
        ArrayList<QueryPredicate> queryPredicateList = new ArrayList<>();

        EnumeratedOptionValue<FieldFunctionTypeOption.Type> scalarFF = new EnumeratedOptionValue<>(FieldFunctionTypeOption.class, FieldFunctionTypeOption.Type.class, FieldFunctionTypeOption.Type.SCALAR);

        QueryPredicate scalarFFPredicate = new EnumeratedOptionPropertyPredicate<>(UserFieldFunction.class, "TypeOption", EnumerationOperator.Equals, scalarFF);

        queryPredicateList.add(scalarFFPredicate);

        QueryPredicate dpPredicate = new TypePredicate(TypeOperator.Is, ScalarQuantityDesignParameter.class);

        queryPredicateList.add(dpPredicate);

        return queryPredicateList;
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
