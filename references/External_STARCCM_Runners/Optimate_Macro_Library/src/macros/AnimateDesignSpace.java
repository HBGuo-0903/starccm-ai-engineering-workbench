/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.JTextComponent;
import star.base.neo.ClientServerObject;
import star.base.query.CompoundOperator;
import star.base.query.CompoundPredicate;
import star.base.query.FromQuerySelectorDescriptor;
import star.base.query.Query;
import star.base.query.QueryPredicate;
import star.base.query.TypeOperator;
import star.base.query.TypePredicate;
import star.cadmodeler.CadModel;
import star.cadmodeler.CadModelCoordinate;
import star.cadmodeler.CoordinateDesignParameter;
import star.cadmodeler.DesignParameter;
import star.cadmodeler.ScalarQuantityDesignParameter;
import star.cadmodeler.SolidModelManager;
import star.cadmodeler.UserDesignParameter;
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
import star.meshing.MeshOperationManager;
import star.meshing.TransformPartsOperation;
import star.starcad2.StarCadDesignParameterDouble;
import star.starcad2.StarCadDocument;
import star.starcad2.StarCadDocumentManager;
import star.vis.Displayer;
import star.vis.PartDisplayer;
import star.vis.Scene;
import star.vis.SimpleAnnotation;

/**
 *
 * @author aarong
 */
public class AnimateDesignSpace extends StarMacro {

    Simulation sim;
    Scene prototype;
    Scene scene;
    SimpleAnnotation annotation;
    ArrayList<Variable> vars = new ArrayList<Variable>();
    File imgDir;
    int xres = 1920;
    int yres = 1080;
    int count = 0;

    @Override
    public void execute() {
        try {
            sim = getActiveSimulation();
            imgDir = new File(sim.getSessionDir() + File.separator + "designSpaceAnimation");

            if (imgDir.exists()) {
                for (File f : imgDir.listFiles()) {
                    f.delete();
                }
            } else {
                imgDir.mkdir();
            }

            ObjectSelectDialog dialog = new ObjectSelectDialog(createQuery(), "Select Design Parameters");

            showDialog(dialog);

            createVars(dialog.getObjects());

            showSetUp();

            createScene();

            for (Variable v : vars) {
                v.updateAnimate();
            }

            cleanUp();
        } catch (Exception ex) {
            sim.println("unable to create scene");
            printEx(ex);
        }

    }

    private Query createQuery() {
        Query query;
        ArrayList<QueryPredicate> predicates = new ArrayList<QueryPredicate>();

        predicates.add(new TypePredicate(TypeOperator.Is, DesignParameter.class));
        predicates.add(new TypePredicate(TypeOperator.Is, UserDesignParameter.class));
        predicates.add(new TypePredicate(TypeOperator.Is, ScalarQuantityDesignParameter.class));
        predicates.add(new TypePredicate(TypeOperator.Is, CoordinateDesignParameter.class));
        predicates.add(new TypePredicate(TypeOperator.Is, VectorQuantityDesignParameter.class));
        predicates.add(new TypePredicate(TypeOperator.Is, StarCadDesignParameterDouble.class));

        query = new Query(new CompoundPredicate(CompoundOperator.Or, predicates), Collections.<Query.Modifier>emptySet());

        return query;
    }

    private void showDialog(final ObjectSelectDialog dialog) {

        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    dialog.show(SimulationProcessNodeManager.getSingleton().getSimulationNode(sim).getSimulationProcessObject());
                }
            });
        } catch (InterruptedException ex) {

        } catch (InvocationTargetException ex) {
        }
    }

    private void createVars(Collection<ClientServerObject> collection) {
        for (ClientServerObject csoi : collection) {
            if (csoi instanceof VectorQuantityDesignParameter || csoi instanceof CoordinateDesignParameter) {
                vars.add(new VectorComponentVariable(csoi, 0));
                vars.add(new VectorComponentVariable(csoi, 1));
                vars.add(new VectorComponentVariable(csoi, 2));
            } else {
                vars.add(new Variable(csoi));
            }
        }
    }

    private void showSetUp() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    new SetupDialog(null).showDialog();
                }
            });
        } catch (InterruptedException ex) {

        } catch (InvocationTargetException ex) {
        }
    }

    private void createScene() {

        if (prototype.getHeight() != -1 && prototype.getWidth() != -1) {
            xres = prototype.getWidth();
            yres = prototype.getHeight();
        }

        scene = sim.getSceneManager().createScene("Parameter Space Animation");

        scene.copyProperties(prototype);

        scene.initializeAndWait();

        scene.open(true);

        for (Displayer d : prototype.getDisplayerManager().getObjects()) {
            if (d instanceof PartDisplayer) {
                PartDisplayer newDisp = scene.getDisplayerManager().createPartDisplayer(d.getPresentationName(), -1, 0);
                newDisp.copyProperties(d);
                newDisp.initialize();
            }
        }

//        for (Displayer d : scene.getDisplayerManager().getObjects()) {
//            d.setRepresentation(sim.getRepresentationManager().getObject("Geometry"));
//        }

        scene.setCurrentView(prototype.getCurrentView());

        annotation = sim.getAnnotationManager().createSimpleAnnotation();
        scene.getAnnotationPropManager().createPropForAnnotation(annotation);

        scene.initializeAndWait();
        scene.open(true);
    }

    private void cleanUp() {
        sim.getSceneManager().deleteScene(scene);
        sim.getAnnotationManager().deleteAnnotation(annotation);
    }

    private void printEx(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        sim.println(sw.toString());
    }

    class Variable {

        ClientServerObject object;
        double min;
        double max;
        double baseline;
        int res;
        boolean enabled;
        String name;

        Variable(ClientServerObject cso) {
            object = cso;
            init();
        }

        public void setMin(double d) {
            min = d;
        }

        public double getMin() {
            return min;
        }

        public void setBaseline(double d) {
            baseline = d;
        }

        public double getBaseline() {
            return baseline;
        }

        public void setMax(double d) {
            max = d;
        }

        public double getMax() {
            return max;
        }

        public void setRes(int i) {
            res = i;
        }

        public int getRes() {
            return res;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void enabled(boolean b) {
            enabled = b;
        }

        public boolean enabled() {
            return enabled;
        }

        void updateAnimate() {

            if (!enabled()) {
                return;
            }

            double value = baseline;
            double inc = (max - min) / (res - 1);

            annotation.setText(getName() + ": " + min + " - " + max);

            while (value < max) {
                updateValue(value);
                value += inc;

                printImg();

            }
            
            value = value - 2 * inc;
            
            while (value > min) {
                updateValue(value);
                value -= inc;
                
                printImg();
            }
            
            value = value + 2 * inc;
            
            while (value < baseline) {
                updateValue(value);
                value += inc;
                
                printImg();
            }

            updateValue(baseline);
        }

        void updateValue(double d) {
            if (object instanceof UserDesignParameter) {
                UserDesignParameter dp = (UserDesignParameter) object;
                dp.getQuantity().setValue(d);
            } else if (object instanceof ScalarQuantityDesignParameter) {
                ScalarQuantityDesignParameter dp = (ScalarQuantityDesignParameter) object;
                dp.getQuantity().setValue(d);
            } else if (object instanceof StarCadDesignParameterDouble) {
                StarCadDesignParameterDouble dp = (StarCadDesignParameterDouble) object;
                dp.setParamValue(d);
            }
        }

        double getCurrentValue() {
            if (object instanceof UserDesignParameter) {
                return ((UserDesignParameter) object).getQuantity().getValue();
            } else if (object instanceof ScalarQuantityDesignParameter) {
                return ((ScalarQuantityDesignParameter) object).getQuantity().getValue();
            } else if (object instanceof StarCadDesignParameterDouble) {
                return ((StarCadDesignParameterDouble) object).getParamValue();
            }

            return 0.0;
        }

        private void init() {
            enabled = true;
            name = object.getPresentationName();
            baseline = getCurrentValue();
            min = baseline - 1;
            max = baseline + 1;
            res = 5;
        }

        private void printImg() {
            Collection<CadModel> cads = sim.get(SolidModelManager.class).getObjectsOf(CadModel.class);
            StarCadDocument scDoc = null;

            try {
                for (CadModel cm : cads) {
                    cm.updateParts();
                }
                TransformPartsOperation transformPartsOperation_0 = ((TransformPartsOperation) sim.get(MeshOperationManager.class).getObject("Casing Post"));
                transformPartsOperation_0.execute();

//                StarCadDocumentManager manager = sim.get(StarCadDocumentManager.class);
//
//                for (StarCadDocument doc : manager.getObjects()) {
//                    scDoc = doc;
//                    if (scDoc.needsUpdate()) {
//                        scDoc.updateModel();
//                    }
//                }
            } catch (Exception ex) {//CAD error
//                if (scDoc != null) {
//                    if (scDoc.isUpdateFailed()) {
//                        sim.println("Document failed to update: " + scDoc.getUpdateErrorCode().name());
                        printEx(ex);
//                    }
//                }
                return;
            }

            scene.printAndWait(imgDir.getAbsolutePath() + File.separator + scene.getPresentationName() + "_" + count + ".png", 1, xres, yres);
            sim.println("wrote: " + scene.getPresentationName() + "_" + count + ".png");
            count++;
        }
    }

    class VectorComponentVariable extends Variable {

        private final int component;

        VectorComponentVariable(ClientServerObject cso, int comp) {
            super(cso);
            component = comp;
        }

        @Override
        public String getName() {
            String localName = super.getName();

            switch (component) {
                case 0:
                    if (!localName.endsWith("_X")) {
                        return localName += "_X";
                    }
                    return localName;
                case 1:
                    if (!localName.endsWith("_Y")) {
                        return localName += "_Y";
                    }
                    return localName;
                default:
                    if (!localName.endsWith("_Z")) {
                        return localName += "_Z";
                    }
                    return localName;
            }
        }

        @Override
        void updateValue(double d) {
            if (object instanceof CoordinateDesignParameter) {
                CoordinateDesignParameter dp = (CoordinateDesignParameter) object;
                CadModelCoordinate cmc = dp.getQuantity();
                double[] vector = cmc.getValue().toDoubleArray();
                vector[component] = d;
                cmc.setComponents(vector[0], vector[1], vector[2]);
            } else if (object instanceof VectorQuantityDesignParameter) {
                VectorQuantityDesignParameter dp = (VectorQuantityDesignParameter) object;
                double[] vector = dp.getQuantity().getVector().toDoubleArray();
                vector[component] = d;
                dp.getQuantity().setComponents(vector[0], vector[1], vector[2]);
            }
        }

        @Override
        double getCurrentValue() {
            if (object instanceof CoordinateDesignParameter) {
                return ((CoordinateDesignParameter) object).getQuantity().getValue().toDoubleArray()[component];
            } else if (object instanceof VectorQuantityDesignParameter) {
                return ((VectorQuantityDesignParameter) object).getQuantity().getVector().toDoubleArray()[component];
            }

            return 0.0;
        }
    }

    @StarDialog(title = "Cad parameter selection dialog")
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

    class SetupDialog extends JDialog {

        JScrollPane scroll;
        Table table;
        JPanel buttonsPanel;
        JComboBox<Scene> sceneComboBox;
        JButton build;

        public SetupDialog(JFrame parent) {
            super(parent, true);

            setTitle("Define parameter space");

            initComponents();

            setLocationRelativeTo(null);

            pack();
        }

        private void initComponents() {
            setLayout(new BorderLayout(5, 5));

            table = new Table(new VariableTableModel());
            table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            table.setColumnSelectionAllowed(true);
            table.getTableHeader().setReorderingAllowed(false);
            scroll = new JScrollPane(table);

            add(scroll, BorderLayout.CENTER);

            buttonsPanel = new JPanel();
            buttonsPanel.setLayout(new FlowLayout());
            sceneComboBox = new JComboBox<Scene>(sim.getSceneManager().getScenesAsArray());
            sceneComboBox.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    prototype = sceneComboBox.getItemAt(sceneComboBox.getSelectedIndex());
                }
            });

            build = new JButton("Build");
            build.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    if (prototype == null) {
                        prototype = sceneComboBox.getItemAt(0);
                    }

                    dispose();
                }
            });

            buttonsPanel.add(sceneComboBox);
            buttonsPanel.add(build);

            add(buttonsPanel, BorderLayout.PAGE_END);
        }

        void showDialog() {
            setEnabled(true);
            setVisible(true);
        }
    }

    class VariableTableModel extends AbstractTableModel {

        String[] COL_NAMES;

        VariableTableModel() {
            super();
            COL_NAMES = new String[]{"Name", "Enabled", "Min", "Baseline", "Max", "Resolution"};
        }

        @Override
        public int getRowCount() {
            return vars.size();
        }

        @Override
        public int getColumnCount() {
            return COL_NAMES.length;
        }

        @Override
        public Class getColumnClass(int col) {
            return getValueAt(0, col).getClass();
        }

        @Override
        public String getColumnName(int col) {
            return COL_NAMES[col];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {

            Variable var = vars.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    return var.getName();
                case 1:
                    return var.enabled();
                case 2:
                    return var.getMin();
                case 3:
                    return var.getBaseline();
                case 4:
                    return var.getMax();
                default:
                    return var.getRes();
            }
        }

        @Override
        public void setValueAt(Object o, int row, int col) {
            Variable var = vars.get(row);

            switch (col) {
                case 0:
                    var.setName((String) o);
                    break;
                case 1:
                    var.enabled((Boolean) o);
                    break;
                case 2:
                    var.setMin((Double) o);
                    break;
                case 3:
                    var.setBaseline((Double) o);
                    break;
                case 4:
                    var.setMax((Double) o);
                    break;
                case 5:
                    var.setRes((Integer) o);
                    break;
            }
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return true;
        }

    }

    class Table extends JTable {

        private boolean isSelectAll = true;

        Table(AbstractTableModel model) {
            super(model);
        }

        public void isSelectAll(boolean b) {
            isSelectAll = b;
        }

        public boolean isSelectAll() {
            return isSelectAll;
        }

        @Override
        public boolean editCellAt(int row, int col, EventObject e) {
            boolean result = super.editCellAt(row, col, e);

            if (isSelectAll) {
                selectAll(e);
            }

            return result;
        }

        private void selectAll(EventObject e) {
            final Component editor = getEditorComponent();

            if (editor == null || !(editor instanceof JTextComponent)) {
                return;
            }

            final JTextComponent text = (JTextComponent) editor;

            if (e == null) {
                ((JTextComponent) editor).selectAll();
                return;
            }

            if (e instanceof KeyEvent) {
                text.selectAll();
                return;
            }

            if (e instanceof ActionEvent) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        text.selectAll();
                    }
                });
                return;
            }

            if (e instanceof MouseEvent) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        text.selectAll();
                    }
                });
            }

        }

    }
}
