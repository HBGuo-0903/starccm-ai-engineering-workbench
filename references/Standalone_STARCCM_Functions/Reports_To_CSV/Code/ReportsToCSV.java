package macro;

import java.util.*;
import java.io.*;
import java.nio.*;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import star.common.*;
import star.base.neo.*;
import star.base.report.*;
import star.flow.*;
import star.material.ConstantMaterialPropertyMethod;
import star.material.Liquid;
import star.material.SingleComponentLiquidModel;

public class ReportsToCSV extends StarMacro
{
    BufferedWriter bwout = null;

    public void execute() 
    {
        try 
	{
        Simulation simulation_0 = getActiveSimulation();
        
        JFileChooser f = new JFileChooser();
        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
        f.showSaveDialog(null);
	
        File afilePath = f.getSelectedFile();
	String fileName =  promptUserForInput("File Name", simulation_0.getPresentationName());
	String filePath = afilePath.getPath();
        String sep = System.getProperty("file.separator");
        String CSVFilePath = filePath+sep+fileName+".csv";
        
        String simulationName = simulation_0.getPresentationName();
 	simulation_0.println("Simulation Name:" + simulationName);

        bwout = new BufferedWriter(new FileWriter(resolvePath(CSVFilePath)));
        bwout.write("Report Name,     Value,        Unit,   \n");
        
        Collection<Report> reportCollection =  simulation_0.getReportManager().getObjects();
        Collection<NamedObject> no;
        Units unit_1 = ((Units) simulation_0.getUnitsManager().getObject("kg/s"));
        Units unit_2 = ((Units) simulation_0.getUnitsManager().getObject("bar"));
        
        PhysicsContinuum physicsContinuum_0 = ((PhysicsContinuum) simulation_0.getContinuumManager().getContinuum("Physics 1"));

        SingleComponentLiquidModel singleComponentLiquidModel_0 = physicsContinuum_0.getModelManager().getModel(SingleComponentLiquidModel.class);

        Liquid liquid_0 = ((Liquid) singleComponentLiquidModel_0.getMaterial());

        ConstantMaterialPropertyMethod constantMaterialPropertyMethod_0 = ((ConstantMaterialPropertyMethod) liquid_0.getMaterialProperties().getMaterialProperty(ConstantDensityProperty.class).getMethod());

        String pname;
       
            for (Report thisReport : reportCollection)
        {
            try
               {
                   
            if(thisReport.getUnits() == unit_1)
            {
               
               MassFlowReport massFlowReport_1 = (MassFlowReport) simulation_0.getReportManager().getReport(thisReport.getPresentationName());
               no =  massFlowReport_1.getParts().getObjects();
               for(NamedObject n : no)
               {
                 
                   MassFlowReport massFlowReport = simulation_0.getReportManager().createReport(MassFlowReport.class);
                    massFlowReport.setPresentationName("MassFlow_"+n);
                    massFlowReport.getParts().setObjects(n);
                    
                    String  fieldLocationName = massFlowReport.getPresentationName();
                    Double fieldValue = massFlowReport.getReportMonitorValue();
                    String fieldUnits = massFlowReport.getUnits().toString();
                bwout.write( fieldLocationName + ",    " +fieldValue + ",    " + fieldUnits +"\n"); 
                
                pname = massFlowReport.getPresentationName();
                    pname = pname.replaceAll("\\s+","");
            
                    ExpressionReport er = simulation_0.getReportManager().createReport(ExpressionReport.class);
                    er.setPresentationName(pname+"_lpm");
                    String p = "${"+pname+"Report}"+"*((60.0)/"+constantMaterialPropertyMethod_0.getQuantity().getRawValue()+")";
                    er.setDefinition(p);
                
                    fieldLocationName = er.getPresentationName();
                    fieldValue = er.getReportMonitorValue();
                    
                bwout.write( fieldLocationName + ",    " +fieldValue + ",    " + "lpm" +"\n"); 
                
                
                 
            //    simulation_0.getReportManager().removeObjects(massFlowReport);
                
                
                    
               }
            }
            
            else if(thisReport.getUnits() == unit_2)
            {
              
                MassFlowAverageReport m = (MassFlowAverageReport) simulation_0.getReportManager().getReport(thisReport.getPresentationName());
                no =  m.getParts().getObjects();
               
                for(NamedObject n : no)
               {
                    MassFlowAverageReport massFlowAverageReport_1 = simulation_0.getReportManager().createReport(MassFlowAverageReport.class);
                    PrimitiveFieldFunction primitiveFieldFunction_1 = ((PrimitiveFieldFunction) simulation_0.getFieldFunctionManager().getFunction("TotalPressure"));
                    massFlowAverageReport_1.setFieldFunction(primitiveFieldFunction_1);
                    
                    massFlowAverageReport_1.setPresentationName("TotalPressure_"+n);
                    massFlowAverageReport_1.getParts().setObjects(n);
                    massFlowAverageReport_1.setUnits(unit_2);
                    String  fieldLocationName = massFlowAverageReport_1.getPresentationName();
                    Double fieldValue = massFlowAverageReport_1.getReportMonitorValue();
                    String fieldUnits = massFlowAverageReport_1.getUnits().toString();
                bwout.write( fieldLocationName + ",    " +fieldValue + ",    " + fieldUnits +"\n"); 
                
                    simulation_0.getReportManager().removeObjects(massFlowAverageReport_1);
               
               
               }
            }
            
            
            else
            {
                    String  fieldLocationName = thisReport.getPresentationName();
                    Double fieldValue = thisReport.getReportMonitorValue();
                    String fieldUnits = thisReport.getUnits().toString();
                bwout.write( fieldLocationName + ",    " +fieldValue + ",    " + fieldUnits +"\n");  
            }
          }catch(Exception e){}  
            
        }
            
           
        bwout.close();
        
        JOptionPane.showMessageDialog(null, "All the reports have been exported ! ");
        
        } 

	catch (IOException iOException) {
        }

    
	}

}
