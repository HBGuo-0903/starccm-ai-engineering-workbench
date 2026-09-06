// Simcenter STAR-CCM+ macro: raports.java
// Ciobanu Dragos 24.02.2024
// tool description: generate temperature maximum report and monitor for the parts chosen by the user, for EMAG part groups all reports and derivate monitors in a specific group called EMAG

//next steps - improve the code and solving the issue with plots duplication


import java.util.*;
import java.util.stream.Collectors;
import javax.swing.*;

import star.common.*;
import star.base.neo.*;
import star.base.report.*;
import star.coremodule.objectselector.*;

public class TmaxReport extends StarMacro {

     private static Simulation sim;



    public void execute()  {

        sim = getActiveSimulation();

        Collection<GeometryPart> selectedParts = (Collection<GeometryPart>) getSelectedObjects(sim, "Select Parts", FilterModel.AllGeometryPartsFilterModel, ModelDescriptor.SelectionType.Multiple);

         if(selectedParts.stream().anyMatch(name -> name.getPresentationName().contains("EMAG"))){

                     setFoldersGroups("EMAG");
                     generateReports(selectedParts, "EMAG");

                     Collection<Monitor> selectedMonitors = new ArrayList<>(sim.getMonitorManager().getObjects());
                     createMonitorPlot(selectedMonitors, "EMAG");

         }
          if(selectedParts.stream().anyMatch(name -> name.getPresentationName().contains("SOLID"))){

                     setFoldersGroups("SOLID");
                     generateReports(selectedParts, "SOLID");

                     Collection<Monitor> selectedMonitors = new ArrayList<>(sim.getMonitorManager().getObjects());
                     createMonitorPlot(selectedMonitors, "SOLID");

          }
          if(selectedParts.stream().anyMatch(name -> name.getPresentationName().contains("FLUID"))){

                     setFoldersGroups("FLUID");
                     generateReports(selectedParts, "FLUID");

                     Collection<Monitor> selectedMonitors = new ArrayList<>(sim.getMonitorManager().getObjects());
                     createMonitorPlot(selectedMonitors, "FLUID");

          }



        setVolumeMeshRepresentation();



    }
    //the following method will generate the monitor plot
    private static void createMonitorPlot(Collection<Monitor> selectedMonitors, String regionName) {


        Collection<Monitor> selectedPlotMonitors = selectedMonitors.stream().filter(monitor -> monitor.getPresentationName().contains(regionName)).toList();

        sim.getPlotManager().createAndSelectMonitorPlot(selectedPlotMonitors, regionName + "_T_max");


    }


    //the following method will generate reports based on the collection of parts chose by user
    private static void generateReports(Collection<GeometryPart> selectedParts, String regionName) {

        List<GeometryPart> selectedPartsByRegion = selectedParts.stream().filter(geometryPart -> geometryPart.getPresentationName().contains(regionName)).toList();
        List<Report> reports = new ArrayList<>();

        for (GeometryPart part : selectedPartsByRegion) {

            MaxReport maxReportGenerate =
                    sim.getReportManager().createReport(MaxReport.class);

            maxReportGenerate.setPresentationName("max_temperature"+"_"+ part.getPresentationName());

            PrimitiveFieldFunction maxTemperature =
                    ((PrimitiveFieldFunction) sim.getFieldFunctionManager().getFunction("Temperature"));

            maxReportGenerate.setFieldFunction(maxTemperature);

            maxReportGenerate.getParts().setObjects(part);

            reports.add(maxReportGenerate);

            if(part.getPresentationName().contains(regionName)){

                ((ClientServerObjectGroup) sim.getReportManager().getGroupsManager().getObject(regionName)).getGroupsManager().groupObjects(regionName, new NeoObjectVector(new Object[] {maxReportGenerate}), true);


            }

        }

        generateMonitors(reports, regionName);



    }


    //JFrame for user interaction
    public static Collection<? extends ClientServerObject> getSelectedObjects(Simulation sim, String message, FilterModel filterModel, ModelDescriptor.SelectionType type) {
        ModelDescriptor descriptor = new SelectorDescriptor.Builder(sim, filterModel).selectionType(type).build();
        ObjectSelector selector = new ObjectSelector(descriptor);

        // Define frame, run the object selector
        JFrame parent = new JFrame("Selection pane");
        JOptionPane optionPane = new JOptionPane();
        optionPane.setMessage(new Object[]{selector});
        //optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
        optionPane.setOptionType(JOptionPane.PLAIN_MESSAGE);

        JDialog dialog = optionPane.createDialog(parent, message);
        dialog.setVisible(true);

        Collection<NamedObject> selected = SelectorUtils.createObjects(sim, selector.getSelected());

        return selected;
    }




    //the following method will set the representation to Volume Mesh for all the reports present in the simulation, including those that exist before running the script
    private void setVolumeMeshRepresentation() {

        Simulation sim = getActiveSimulation();
        FvRepresentation fvRepresentation_0 = ((FvRepresentation) sim.getRepresentationManager().getObject("Volume Mesh")); //Declare Volume Mesh representation
        sim.getReportManager().applyRepresentation(fvRepresentation_0); //Set Volume Mesh representation
    }

    //the following method will generate monitors according to user selection made for reports
    private static void generateMonitors( Collection<Report> selectedReports, String regionName) {

        List<Report> selectedReportsByRegion = selectedReports.stream().filter(report -> report.getPresentationName().contains(regionName)).toList();

        sim.println(selectedReports.size());


        for ( Report report : selectedReportsByRegion) {


                     ReportMonitor monitor  = report.createMonitor();
                     monitor.setPresentationName(report.getPresentationName());

                if(monitor.getPresentationName().contains(regionName)){


                      ((ClientServerObjectGroup) sim.getMonitorManager().getGroupsManager().getObject(regionName)).getGroupsManager().groupObjects(regionName, new NeoObjectVector(new Object[] {monitor}), true);

                }

        }

    }



    //the following method sets the folders group for reports and monitors
    private static void setFoldersGroups( String groupName) {

        //folder group reports
        if(!sim.getReportManager().getGroupsManager().has(groupName)){

            sim.getReportManager().getGroupsManager().createGroup("New Group");

            ((ClientServerObjectGroup) sim.getReportManager().getGroupsManager().getObject("New Group")).setPresentationName(groupName);
        }


        //folder group monitors
        if(!sim.getMonitorManager().getGroupsManager().has(groupName)){

            sim.getMonitorManager().getGroupsManager().createGroup("New Group");

            ((ClientServerObjectGroup) sim.getMonitorManager().getGroupsManager().getObject("New Group")).setPresentationName(groupName);
        }



    }



}

