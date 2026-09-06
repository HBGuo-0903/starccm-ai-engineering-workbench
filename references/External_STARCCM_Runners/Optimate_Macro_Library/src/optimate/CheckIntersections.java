// STAR-CCM+ macro: test.java
// Written by STAR-CCM+ 10.03.077
package optimate;

import java.util.ArrayList;
import java.util.List;
import star.cadmodeler.*;
import star.common.*;
import star.base.neo.*;
import star.base.report.ExpressionReport;
import star.base.report.Report;
import star.base.report.ReportManager;
import star.vis.*;
import star.meshing.*;

public class CheckIntersections extends StarMacro {

    ExpressionReport report;

    @Override
    public void execute() {
        Simulation sim
                = getActiveSimulation();
        
//        List<SolidModelPart> toUpdate = new ArrayList<>();
//        
//        for(GeometryPart gpi : sim.get(SimulationPartManager.class).getParts()) {
//            if (gpi instanceof SolidModelPart) {
//                SolidModelPart smp = (SolidModelPart) gpi;
//                if (smp.isDirty()) {
//                    toUpdate.add(smp);
//                }
//            }
//        }
//        
//        sim.get(SimulationPartManager.class).updateParts(toUpdate);
        
//        report = (ExpressionReport) sim.get(ReportManager.class).getReport("CAD Success");

        Scene scene_12
                = sim.getSceneManager().createScene("Repair Surface");

        scene_12.initializeAndWait();

        PartRepresentation partRepresentation_0
                = ((PartRepresentation) sim.getRepresentationManager().getObject("Geometry"));

        PartSurfaceMeshWidget partSurfaceMeshWidget_3
                = partRepresentation_0.startSurfaceMeshWidget(scene_12);

        SolidModelPart solidModelPart_2
                = ((SolidModelPart) sim.get(SimulationPartManager.class).getPart("Airfoil"));

        SolidModelPart solidModelPart_3
                = ((SolidModelPart) sim.get(SimulationPartManager.class).getPart("Leading Flap"));

        SolidModelPart solidModelPart_4
                = ((SolidModelPart) sim.get(SimulationPartManager.class).getPart("Trailing Flap"));

        RootDescriptionSource rootDescriptionSource_0
                = ((RootDescriptionSource) sim.get(SimulationMeshPartDescriptionSourceManager.class).getObject("Root"));

        partSurfaceMeshWidget_3.setActiveParts(new NeoObjectVector(new Object[]{solidModelPart_2, solidModelPart_3, solidModelPart_4}), rootDescriptionSource_0);

        partSurfaceMeshWidget_3.startSurfaceMeshDiagnostics();

        partSurfaceMeshWidget_3.startSurfaceMeshRepair();

        partSurfaceMeshWidget_3.startMergeImprintController();

        partSurfaceMeshWidget_3.startIntersectController();

        partSurfaceMeshWidget_3.startLeakFinderController();

        partSurfaceMeshWidget_3.startSurfaceMeshQueryController();
        
        SurfaceMeshWidgetDiagnosticsController surfaceMeshWidgetDiagnosticsController_3
                = partSurfaceMeshWidget_3.getControllers().getController(SurfaceMeshWidgetDiagnosticsController.class);

        surfaceMeshWidgetDiagnosticsController_3.setCheckSoftFeatureErrors(false);

        surfaceMeshWidgetDiagnosticsController_3.setSoftFeatureErrorsActive(false);

        surfaceMeshWidgetDiagnosticsController_3.setCheckHardFeatureErrors(false);

        surfaceMeshWidgetDiagnosticsController_3.setHardFeatureErrorsActive(false);

        SurfaceMeshWidgetRepairController surfaceMeshWidgetRepairController_2
                = partSurfaceMeshWidget_3.getControllers().getController(SurfaceMeshWidgetRepairController.class);

        SurfaceMeshWidgetMergeImprintOptions surfaceMeshWidgetMergeImprintOptions_0
                = surfaceMeshWidgetRepairController_2.getOptions().getMergeImprintOptions();

        surfaceMeshWidgetMergeImprintOptions_0.setImprintMethod("NormalImprintMethod");

        surfaceMeshWidgetMergeImprintOptions_0.setMergeAction(SurfaceMeshWidgetMergedFaceOption.MAINTAIN_DEST_BOUNDARY);

        surfaceMeshWidgetMergeImprintOptions_0.getResultingMeshType().setSelected(ImprintResultingMeshTypeOption.CONFORMAL);

        surfaceMeshWidgetMergeImprintOptions_0.getMergeImprintMethod().setSelected(ImprintMergeImprintMethodOption.DISCRETE_IMPRINT);

        surfaceMeshWidgetMergeImprintOptions_0.getPreProcessType().setSelected(ImprintPreprocessOption.NO_PREPROCESS);

        surfaceMeshWidgetMergeImprintOptions_0.getResultingPartSurfaces().setSelected(ImprintPartSurfacesOption.CREATE_NEW);

        surfaceMeshWidgetMergeImprintOptions_0.getFaceOrientation().setSelected(ImprintFaceOrientationOption.OPPOSING_AND_ALIGNED);

        SurfaceMeshWidgetDisplayController surfaceMeshWidgetDisplayController_3
                = partSurfaceMeshWidget_3.getControllers().getController(SurfaceMeshWidgetDisplayController.class);

        surfaceMeshWidgetDisplayController_3.showAllFaces();

        SurfaceMeshWidgetDisplayer surfaceMeshWidgetDisplayer_3
                = ((SurfaceMeshWidgetDisplayer) scene_12.getDisplayerManager().getDisplayer("Widget displayer 1"));

        surfaceMeshWidgetDisplayer_3.initialize();

        PiercedFacesThreshold piercedFacesThreshold_0
                = ((PiercedFacesThreshold) surfaceMeshWidgetDiagnosticsController_3.getOptions().getThresholdDiagnosticsManager().getObject("Pierced faces (default) 1"));

        piercedFacesThreshold_0.setIsEnabled(true);

        piercedFacesThreshold_0.setPriorEnabledState(false);

        surfaceMeshWidgetDiagnosticsController_3.setDiagnosticsInputType(0);

        surfaceMeshWidgetDiagnosticsController_3.runThresholdDiagnostics();
        
        int count = piercedFacesThreshold_0.getThresholdCount();// + " pierced faces found.");

        if (count > 0) {
            sim.println("Error: Flap and/or Airfoil overlap detected!");
//            report.setDefinition("0");
            StepStoppingCriterion stoppingCriteria = (StepStoppingCriterion) sim.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps");
            stoppingCriteria.setMaximumNumberSteps(1);
            Report r = sim.getReportManager().getReport("Mass Cons");
            sim.getReportManager().remove(r);
        } else {
            sim.println("No Flap or Airfoil overlap detected");
        }
    }
}
