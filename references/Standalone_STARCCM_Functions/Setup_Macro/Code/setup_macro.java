// STAR-CCM+ macro: setup_macro.java
// William Snell - last edited 2/01/2020
package macro;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

//xml parsing
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//star-specific
import star.common.*;
import star.base.neo.*;
import star.base.report.*;
import star.material.*;
import star.keturb.*;
import star.coupledflow.*;
import star.turbulence.*;
import star.vis.*;
import star.flow.*;
import star.metrics.*;
import star.meshing.*;
import star.resurfacer.*;
import star.prismmesher.*;
import star.vis.*;
import star.trimmer.*;

public class setup_macro extends StarMacro {

  public void execute() {
    execute0(); //Set Up simulation

    execute1(); //Get active simulation
    
  }

  private void execute0() {

//------------------------Initialise Simulation---------------------------------
    Simulation simulation_0 = 
      getActiveSimulation();

    //start loop test
    Region region_0 = 
    simulation_0.getRegionManager().createEmptyRegion();

    region_0.getPartGroup().setQuery(null);
    
//----------------------------Geometry Import-----------------------------------
    
    PartImportManager partImportManager_0 = 
      simulation_0.get(PartImportManager.class);

    partImportManager_0.importCadPart(resolvePath("domain.SLDPRT"), "SharpEdges", 30.0, 2, true, 1.0E-5, true, false, false, false);

    partImportManager_0.importCadPart(resolvePath("car.SLDPRT"), "SharpEdges", 18.0, 4, true, 1.0E-5, true, false, false, false);

    //Import domain

    CadPart cadPart_0 = 
      ((CadPart) simulation_0.get(SimulationPartManager.class).getPart("domain"));     
      
    //Import car as a composite part (groups bodies together)       
    
    try {
        CompositePart carPart_0 = 
            ((CompositePart) simulation_0.get(SimulationPartManager.class).getPart("car"));
         //Perform boolean subtract operation

    //Ugly code duplication necessary as carPart may be a different type depending on part
    SubtractPartsOperation subtractPartsOperation_0 = 
      (SubtractPartsOperation) simulation_0.get(MeshOperationManager.class).createSubtractPartsOperation(new NeoObjectVector(new Object[] {carPart_0, cadPart_0}));

    subtractPartsOperation_0.getTargetPartManager().setQuery(null);

    subtractPartsOperation_0.getTargetPartManager().setObjects(cadPart_0);

    subtractPartsOperation_0.setPerformCADBoolean(false);

    subtractPartsOperation_0.execute();
    } catch (Exception e){
        CadPart carPart_0 =
                ((CadPart) simulation_0.get(SimulationPartManager.class).getPart("car")); //in case the part only has one body
         //Perform boolean subtract operation

    SubtractPartsOperation subtractPartsOperation_0 = 
      (SubtractPartsOperation) simulation_0.get(MeshOperationManager.class).createSubtractPartsOperation(new NeoObjectVector(new Object[] {carPart_0, cadPart_0}));

    subtractPartsOperation_0.getTargetPartManager().setQuery(null);

    subtractPartsOperation_0.getTargetPartManager().setObjects(cadPart_0);

    subtractPartsOperation_0.setPerformCADBoolean(false);

    subtractPartsOperation_0.execute();
    }

   

    //Assign subtract part to new region
    
    MeshOperationPart meshOperationPart_0 = 
      ((MeshOperationPart) simulation_0.get(SimulationPartManager.class).getPart("Subtract"));

    region_0.getPartGroup().setObjects(meshOperationPart_0);
    
//------------------------------Physics Setup-----------------------------------
PhysicsContinuum physicsContinuum_0 = 
      simulation_0.getContinuumManager().createContinuum(PhysicsContinuum.class);

    physicsContinuum_0.enable(ThreeDimensionalModel.class);

    physicsContinuum_0.enable(SteadyModel.class);

    physicsContinuum_0.enable(SingleComponentGasModel.class);

    physicsContinuum_0.enable(CoupledFlowModel.class);

    physicsContinuum_0.enable(ConstantDensityModel.class);

    physicsContinuum_0.enable(TurbulentModel.class);

    physicsContinuum_0.enable(RansTurbulenceModel.class);

    physicsContinuum_0.enable(KEpsilonTurbulence.class);

    physicsContinuum_0.enable(LagKeTurbModel.class);

    physicsContinuum_0.enable(EblKeAllYplusWallTreatment.class);
    
    physicsContinuum_0.enable(CellQualityRemediationModel.class);
            
    
 //------------------------------Boundaries-------------------------------------   
    //Iterate through all surfaces in the subtract body and assign boundaries accordingly
    //Iterate through all parts in simulation
    Collection<PartSurface> mySurfaces;
      mySurfaces = meshOperationPart_0.getPartSurfaceManager().getPartSurfaces();
//------------------------Read XML File-----------------------------------------
    String xmlFilePath = resolvePath("sim_config.xml");
    //Include try catch to allow xml document to be read:
    try {
        
    //Build DOM
 
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true); // never forget this!
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFilePath);
 
        //Create XPath
 
        XPathFactory xpathfactory = XPathFactory.newInstance();
        XPath xpath = xpathfactory.newXPath();
        
                                
        //Add total force reports
        ForceReport dragReport = 
                simulation_0.getReportManager().createReport(ForceReport.class);
        ForceReport liftReport = 
                simulation_0.getReportManager().createReport(ForceReport.class);
        dragReport.setPresentationName("Total Drag");
        dragReport.getDirection().setComponents(0.0, 0.0, -1.0);

        liftReport.setPresentationName("Total Downforce");
        dragReport.getDirection().setComponents(0.0, -1.0, 0.0);
    
        for (Iterator<PartSurface> it = mySurfaces.iterator(); it.hasNext();) {
            PartSurface iSurface = it.next();
            String iName = iSurface.getPresentationName();
            //Find surface type
            String[] wall = iName.split("_",4);
            String[] owner = iName.split("\\.",2);
            String wallType;
            //Find surface name
            String wallName;

            //Add all surfaces with prefix "car" to the total force reports
            if (owner[0].matches("car")){
                dragReport.getParts().addObjects(iSurface);
                liftReport.getParts().addObjects(iSurface);                        
            }              
            
            //extract name and type from face properties            
            try {   
                wallType = wall[1];
                wallName = wall[2];
            } catch(Exception e){
                wallType = "_"; 
                wallName = wall[0];
            }
            //Check if part name has suffix "rep" and add to (or create) monitor
            if (iName.endsWith("rep")){
                try {
                    ForceReport oldDragReport = ((ForceReport)simulation_0.getReportManager().getReport(wallName+" Drag"));
                    ForceReport oldLiftReport = ((ForceReport)simulation_0.getReportManager().getReport(wallName+" Downforce"));
                    oldDragReport.getParts().addObjects(iSurface);
                    oldLiftReport.getParts().addObjects(iSurface);
                    
                    
                } catch (Exception e){
                    ForceReport newDragReport = 
                    simulation_0.getReportManager().createReport(ForceReport.class);
                    ForceReport newLiftReport = 
                    simulation_0.getReportManager().createReport(ForceReport.class);
                    
                    newDragReport.getDirection().setComponents(0.0, 0.0, -1.0);
                    newLiftReport.getDirection().setComponents(0.0, -1.0, 0.0);
                    
                    newDragReport.setPresentationName(wallName+" Drag");
                    newLiftReport.setPresentationName(wallName+" Downforce");    
                    
                    newDragReport.getParts().addObjects(iSurface);                    
                    newLiftReport.getParts().addObjects(iSurface);                    
                }
            }
            //if (end of name == rep):
            //if report exists with part name
                //add to existing report
            //else
                //create new report
            //finally, create a report for all car parts

            simulation_0.println(wallType + " "+ wallName);        


            try {
                Boundary boundary_0 = 
                    region_0.getBoundaryManager().getBoundary(wallName);
                boundary_0.getPartSurfaceGroup().addObjects(iSurface);

            } catch(Exception e) {

                Boundary boundary_0 =
                    region_0.getBoundaryManager().createEmptyBoundary();
                //Set boundary name
                boundary_0.setPresentationName(wallName);

                boundary_0.getPartSurfaceGroup().addObjects(iSurface);

            switch(wallType){
                case "wall":

                    break;
                case "inlet":
                    InletBoundary inletBoundary_0 = 
                        ((InletBoundary) simulation_0.get(ConditionTypeManager.class).get(InletBoundary.class));
                    boundary_0.setBoundaryType(inletBoundary_0);
                    VelocityMagnitudeProfile velocityMagnitudeProfile_0 = 
                        boundary_0.getValues().get(VelocityMagnitudeProfile.class);
                    
                    XPathExpression expr_0 = xpath.compile("//partsList/"+wallName+"/vel/text()");
                    NodeList nodes_0 = (NodeList) expr_0.evaluate(doc, XPathConstants.NODESET);
                                
                    Double vel;                   
                    vel = Double.parseDouble(nodes_0.item(0).getNodeValue());  
                    
                    velocityMagnitudeProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(vel);
                    break;
                case "outlet":
                    OutletBoundary outletBoundary_0 = 
                        ((OutletBoundary) simulation_0.get(ConditionTypeManager.class).get(OutletBoundary.class));
                    boundary_0.setBoundaryType(outletBoundary_0); 
                    break;        
                case "symmetry":
                    SymmetryBoundary symmetryBoundary_0 = 
                        ((SymmetryBoundary) simulation_0.get(ConditionTypeManager.class).get(SymmetryBoundary.class));
                    boundary_0.setBoundaryType(symmetryBoundary_0);    
                    break;
                //Set wheels as moving walls with rotational reference frames
                //All wheels reference the same setup, but with properties asoundary) simulation_0.get(ConditionTypeManager.class).get(OutletBoundary.class));

                //defined by the config xml.
                case "fl":
                case "fr":
                case "rl":
                case "rr":          //initialise boundary
                    boundary_0.getConditions().get(WallSlidingOption.class).setSelected(WallSlidingOption.Type.LOCAL_ROTATION_RATE);
                    WallRelativeRotationProfile wallRelativeRotationProfile_0 = 
                        boundary_0.getValues().get(WallRelativeRotationProfile.class);
                  
            
                    //Angular Velocity
                    
                    //evaluate xml path
                    XPathExpression expr = xpath.compile("//partsList/"+wallName+"/ang_vel/text()");
                    NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                                
                    Double ang_vel;                   
                    ang_vel = Double.parseDouble(nodes.item(0).getNodeValue());                                  
                    //set rotation rate
                    wallRelativeRotationProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(ang_vel);
                    
                    //Axis Vector 0 Coordinates
                    //Evaluate xml path
                    XPathExpression expr2 = xpath.compile("//partsList/"+wallName+"/vec0/*/text()");
                    
                    NodeList nodes2 = (NodeList) expr2.evaluate(doc, XPathConstants.NODESET);
                    ReferenceFrame referenceFrame_0 = 
                        boundary_0.getValues().get(ReferenceFrame.class);
                    
                    //Set origin vector (vec0)
                    referenceFrame_0.getOriginVector().setComponents(
                        (Double) Double.parseDouble(nodes2.item(0).getNodeValue()),
                        (Double) Double.parseDouble(nodes2.item(1).getNodeValue()),
                        (Double) Double.parseDouble(nodes2.item(2).getNodeValue()));
                    
                    //Axis Vector 1 Coordinates
                    XPathExpression expr3 = xpath.compile("//partsList/"+wallName+"/vec1/*/text()");
                    NodeList nodes3 = (NodeList) expr3.evaluate(doc, XPathConstants.NODESET);
                    //Set axis vector (vec1)
                    referenceFrame_0.getAxisVector().setComponents(
                        (Double) Double.parseDouble(nodes2.item(0).getNodeValue()),
                        (Double) Double.parseDouble(nodes2.item(1).getNodeValue()),
                        (Double) Double.parseDouble(nodes2.item(2).getNodeValue()));                    

                    break;
                case "ground":
                    //Set to moving wall
                    boundary_0.getConditions().get(WallSlidingOption.class).setSelected(WallSlidingOption.Type.VECTOR);
                    //Get (x,y,z) values from xml
                    XPathExpression expr0 = xpath.compile("//partsList/"+wallName+"/vec0/*/text()");                    
                    NodeList nodes0 = (NodeList) expr0.evaluate(doc, XPathConstants.NODESET);
                                        
                    WallRelativeVelocityProfile wallRelativeVelocityProfile_0 = 
                         boundary_0.getValues().get(WallRelativeVelocityProfile.class);
                    
                     //set in STAR
                    wallRelativeVelocityProfile_0.getMethod(ConstantVectorProfileMethod.class).getQuantity().setComponents(
                        (Double) Double.parseDouble(nodes0.item(0).getNodeValue()),
                        (Double) Double.parseDouble(nodes0.item(1).getNodeValue()),
                        (Double) Double.parseDouble(nodes0.item(2).getNodeValue()));   
                                  
                    break;
                default:
                    break;
                    //default to wall type
            //clear boundary object        

            }
            }
        }
//-------------------------------Stopping Criteria------------------------------
    //Create monitors from lift and drag reports
        ReportMonitor liftMonitor = liftReport.createMonitor();
        ReportMonitor dragMonitor = dragReport.createMonitor();
        
        MonitorIterationStoppingCriterion monitorIterationStoppingCriterion_0 = 
            simulation_0.getSolverStoppingCriterionManager().createIterationStoppingCriterion(liftMonitor);
        MonitorIterationStoppingCriterion monitorIterationStoppingCriterion_1 = 
            simulation_0.getSolverStoppingCriterionManager().createIterationStoppingCriterion(dragMonitor);
        
        ((MonitorIterationStoppingCriterionOption) monitorIterationStoppingCriterion_0.getCriterionOption()).setSelected(MonitorIterationStoppingCriterionOption.Type.STANDARD_DEVIATION);
        ((MonitorIterationStoppingCriterionOption) monitorIterationStoppingCriterion_1.getCriterionOption()).setSelected(MonitorIterationStoppingCriterionOption.Type.STANDARD_DEVIATION);
        
        MonitorIterationStoppingCriterionStandardDeviationType monitorIterationStoppingCriterionType_0 = 
            ((MonitorIterationStoppingCriterionStandardDeviationType) monitorIterationStoppingCriterion_0.getCriterionType());
        MonitorIterationStoppingCriterionStandardDeviationType monitorIterationStoppingCriterionType_1 = 
            ((MonitorIterationStoppingCriterionStandardDeviationType) monitorIterationStoppingCriterion_1.getCriterionType());
        
        //set solution convergence values from xml
        try {
            XPathExpression expr = xpath.compile("//solver/Std_Dev/text()");
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                                
            Double relTol;                   
            relTol = Double.parseDouble(nodes.item(0).getNodeValue()); 
            simulation_0.println(relTol);
            XPathExpression expr1 = xpath.compile("//solver/Max_Steps/text()");
            NodeList nodes1 = (NodeList) expr1.evaluate(doc, XPathConstants.NODESET);
                                
            Integer maxSteps;                   
            maxSteps = Integer.parseInt(nodes1.item(0).getNodeValue()); 
            simulation_0.println(maxSteps);
            
            monitorIterationStoppingCriterionType_0.getStandardDeviation().setValue(relTol);

            monitorIterationStoppingCriterionType_1.getStandardDeviation().setValue(relTol);

            //Set max iterations to value configured in xml     
            StepStoppingCriterion stepStoppingCriterion_0 = 
                ((StepStoppingCriterion) simulation_0.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps"));
            
            stepStoppingCriterion_0.setMaximumNumberSteps(maxSteps);
            } catch (Exception e){
                simulation_0.println("Error importing solver criterion from xml");
            }
            
    } catch (Exception e) {
        simulation_0.println("Error importing xml config file.");
        simulation_0.println(e.toString());
        simulation_0.println(e.getMessage());
    }
//-------------------------------Solver Settings--------------------------------
    CoupledImplicitSolver coupledImplicitSolver_0 = 
      ((CoupledImplicitSolver) simulation_0.getSolverManager().getSolver(CoupledImplicitSolver.class));

    coupledImplicitSolver_0.setCFL(100.0);

    AMGLinearSolver aMGLinearSolver_0 = 
      coupledImplicitSolver_0.getAMGLinearSolver();

    ((AMGVCycle) aMGLinearSolver_0.getCycleType()).setPreSweeps(1);

    coupledImplicitSolver_0.getExpertInitManager().getExpertInitOption().setSelected(ExpertInitOption.Type.GRID_SEQ_METHOD);

    GridSequencingInit gridSequencingInit_0 = 
      ((GridSequencingInit) coupledImplicitSolver_0.getExpertInitManager().getInit());

    gridSequencingInit_0.setMaxGSLevels(20);

    gridSequencingInit_0.setMaxGSIterations(70);

    gridSequencingInit_0.setConvGSTol(0.03);

    gridSequencingInit_0.setGSCfl(3.0);

    coupledImplicitSolver_0.getSolutionDriverManager().getExpertDriverOption().setSelected(ExpertDriverOption.Type.EXPERT_DRIVER);

    ExpertDriverCoupledSolver expertDriverCoupledSolver_0 = 
      ((ExpertDriverCoupledSolver) coupledImplicitSolver_0.getSolutionDriverManager().getDriver());

    expertDriverCoupledSolver_0.setMaxCorrectionScale(0.25);

    expertDriverCoupledSolver_0.setRelativeResidualCutoff(1.0E-10);

    expertDriverCoupledSolver_0.setAmgNrCyclesForCflLimiting(5);
    
//-------------------------------Meshing----------------------------------------
    //-----------------------Import XML Values----------------------------------
    Double base_Size = null;
    Integer no_Prism = null;
    Double prism_Total_Thickness = null;
    Double max_Cell_Size = null;
    Integer no_BOI = null;
    List BOI_Size = new ArrayList();    
    
    try {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true); // never forget this!
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFilePath);
 
        //Create XPath
 
        XPathFactory xpathfactory = XPathFactory.newInstance();
        XPath xpath = xpathfactory.newXPath();
        
        //evaluate xml path
        XPathExpression basExpr = xpath.compile("//mesh/Base_Size/text()");                
        base_Size = Double.parseDouble(((NodeList)basExpr.evaluate(doc, XPathConstants.NODESET)).item(0).getNodeValue());
        
        XPathExpression noPrisExpr = xpath.compile("//mesh/No_Prism/text()");                
        no_Prism = Integer.parseInt(((NodeList)noPrisExpr.evaluate(doc, XPathConstants.NODESET)).item(0).getNodeValue());
        
       XPathExpression prisThickExpr = xpath.compile("//mesh/Prism_Total_Thickness/text()");                
       prism_Total_Thickness = Double.parseDouble(((NodeList)prisThickExpr.evaluate(doc, XPathConstants.NODESET)).item(0).getNodeValue());
         
       XPathExpression maxCellExpr = xpath.compile("//mesh/Max_Cell_Size/text()");                
       max_Cell_Size = Double.parseDouble(((NodeList)maxCellExpr.evaluate(doc, XPathConstants.NODESET)).item(0).getNodeValue());
          
       XPathExpression noBOIExpr = xpath.compile("//mesh/No_BOI/text()");                
       no_BOI = Integer.parseInt(((NodeList)noBOIExpr.evaluate(doc, XPathConstants.NODESET)).item(0).getNodeValue());
       
       //for loop through BOI sizes
       for(Integer i = 0; i < no_BOI; i++){
           XPathExpression boiExpr = xpath.compile("//mesh/BOI_Size_"+i+"/text()");                
           Double boi = Double.parseDouble(((NodeList)boiExpr.evaluate(doc, XPathConstants.NODESET)).item(0).getNodeValue());
           BOI_Size.add(boi);           
       }
       
    } catch (Exception e){
        simulation_0.println("Error importing XML mesh values.");
    }
    
    simulation_0.println(base_Size); 
    
    //---------------------------Set Up STAR------------------------------------

    AutoMeshOperation autoMeshOperation_0 = 
            
      simulation_0.get(MeshOperationManager.class).createAutoMeshOperation(new StringVector(new String[] {"star.resurfacer.ResurfacerAutoMesher", "star.trimmer.TrimmerAutoMesher", "star.prismmesher.PrismAutoMesher", "star.resurfacer.AutomaticSurfaceRepairAutoMesher"}), new NeoObjectVector(new Object[] {meshOperationPart_0}));

    TrimmerAutoMesher trimmerAutoMesher_0 = 
      ((TrimmerAutoMesher) autoMeshOperation_0.getMeshers().getObject("Trimmed Cell Mesher"));

    trimmerAutoMesher_0.setDoMeshAlignment(true);

    Units units_0 = 
      ((Units) simulation_0.getUnitsManager().getObject("mm"));

    autoMeshOperation_0.getDefaultValues().get(BaseSize.class).setUnits(units_0);

    autoMeshOperation_0.getDefaultValues().get(BaseSize.class).setValue(base_Size);
    

    PartsMinimumSurfaceSize partsMinimumSurfaceSize_0 = 
      autoMeshOperation_0.getDefaultValues().get(PartsMinimumSurfaceSize.class);

    partsMinimumSurfaceSize_0.getRelativeSizeScalar().setValue(6.25);

    SurfaceGrowthRate surfaceGrowthRate_0 = 
      autoMeshOperation_0.getDefaultValues().get(SurfaceGrowthRate.class);

    surfaceGrowthRate_0.getGrowthRateScalar().setValue(1.3);

    NumPrismLayers numPrismLayers_0 = 
      autoMeshOperation_0.getDefaultValues().get(NumPrismLayers.class);

    IntegerValue integerValue_0 = 
      numPrismLayers_0.getNumLayersValue();

    integerValue_0.getQuantity().setValue(no_Prism);

    PrismLayerStretching prismLayerStretching_0 = 
      autoMeshOperation_0.getDefaultValues().get(PrismLayerStretching.class);

    prismLayerStretching_0.getStretchingQuantity().setValue(1.5);

    PrismThickness prismThickness_0 = 
      autoMeshOperation_0.getDefaultValues().get(PrismThickness.class);

    prismThickness_0.getRelativeSizeScalar().setValue(prism_Total_Thickness);

    MaximumCellSize maximumCellSize_0 = 
      autoMeshOperation_0.getDefaultValues().get(MaximumCellSize.class);

    maximumCellSize_0.getRelativeSizeScalar().setValue(max_Cell_Size);

    SurfaceCustomMeshControl surfaceCustomMeshControl_0 = 
      autoMeshOperation_0.getCustomMeshControls().createSurfaceControl();

    surfaceCustomMeshControl_0.setApplyOnlyToContact(true);

    surfaceCustomMeshControl_0.getGeometryObjects().setQuery(null);
    //Set meshing to parallel
    autoMeshOperation_0.getMesherParallelModeOption().setSelected(MesherParallelModeOption.Type.PARALLEL);
    
    //Surface Control
    //Get every surface with domain prefix
    for (Iterator<PartSurface> it = mySurfaces.iterator(); it.hasNext();) {
            PartSurface iSurface = it.next();
            String iName = iSurface.getPresentationName();
            String[] body = iName.split("\\.",2);
            if (body[0].matches("domain")){
                PartSurface partSurface_0 =
                    ((PartSurface) meshOperationPart_0.getPartSurfaceManager().getPartSurface(iName));          
                surfaceCustomMeshControl_0.getGeometryObjects().add(partSurface_0);
            }
    }
     
    surfaceCustomMeshControl_0.getCustomConditions().get(PartsTargetSurfaceSizeOption.class).setSelected(PartsTargetSurfaceSizeOption.Type.CUSTOM);

    PartsTargetSurfaceSize partsTargetSurfaceSize_0 = 
      surfaceCustomMeshControl_0.getCustomValues().get(PartsTargetSurfaceSize.class);

    partsTargetSurfaceSize_0.getRelativeSizeScalar().setValue(37500.0);
    //Volume control
    //Create a new volume control for every BOI
    
    for (Integer i = 0; i < no_BOI; i++){
        VolumeCustomMeshControl volumeCustomMeshControl_0 = 
            autoMeshOperation_0.getCustomMeshControls().createVolumeControl();
        
        partImportManager_0.importCadPart(resolvePath((String)"boi_"+String.valueOf(i)+".SLDPRT"), "SharpEdges", 30.0, 2, true, 1.0E-5, true, false, false, false);
        CadPart boi_0 = 
          ((CadPart) simulation_0.get(SimulationPartManager.class).getPart((String)"boi_"+String.valueOf(i)));
        
        volumeCustomMeshControl_0.getGeometryObjects().setObjects(boi_0);
        VolumeControlResurfacerSizeOption volumeControlResurfacerSizeOption_0 = 
        volumeCustomMeshControl_0.getCustomConditions().get(VolumeControlResurfacerSizeOption.class);

        volumeControlResurfacerSizeOption_0.setVolumeControlBaseSizeOption(true);

        VolumeControlSize volumeControlSize_0 = 
        volumeCustomMeshControl_0.getCustomValues().get(VolumeControlSize.class);

        volumeControlSize_0.getRelativeSizeScalar().setValue((Double) BOI_Size.get(i));
        
    }

    autoMeshOperation_0.execute();
    
//-----------------------Initalize and solve------------------------------------
    Solution solution_0 = 
      simulation_0.getSolution();
    
    solution_0.initializeSolution();
    //Set up residual plot
    ResidualPlot residualPlot_0 = 
      ((ResidualPlot) simulation_0.getPlotManager().getPlot("Residuals"));

    residualPlot_0.open();
    
    PlotUpdate plotUpdate_0 = 
      residualPlot_0.getPlotUpdate();
    //Get stopping criteria
 //  MonitorIterationStoppingCriterion monitorIterationStoppingCriterion_0 = 
 //          simulation_0.getSolverStoppingCriterionManager().getObject(xmlFilePath)
    
  //  monitorIterationStoppingCriterion_0.getLogicalOption().setSelected(SolverStoppingCriterionLogicalOption.Type.AND);
  //  monitorIterationStoppingCriterion_1.getLogicalOption().setSelected(SolverStoppingCriterionLogicalOption.Type.AND);
    
    simulation_0.getSimulationIterator().runAutomation();
    
  //save reports to file
  new StarScript(getActiveSimulation(), new java.io.File(resolvePath("Report_to_CSV.java"))).play(); 
    
    
    
}
private void execute1() {

  Simulation simulation_0 = 
    getActiveSimulation();

}
}