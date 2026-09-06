//@author : Yogesh Nalam

package macro;

import java.util.*;
import star.base.report.ExpressionReport;
import star.base.report.Report;

import star.common.*;
import star.flow.ConstantDensityProperty;
import star.material.ConstantMaterialPropertyMethod;
import star.material.Liquid;
import star.material.SingleComponentLiquidModel;

public class kgsTolpm extends StarMacro
{

       public void execute()
       {
           execute0();
       }
    
    private void execute0()
    {
        Simulation sim = getActiveSimulation();
        
        PhysicsContinuum physicsContinuum_0 = 
      ((PhysicsContinuum) sim.getContinuumManager().getContinuum("Physics 1"));

        SingleComponentLiquidModel singleComponentLiquidModel_0 = 
      physicsContinuum_0.getModelManager().getModel(SingleComponentLiquidModel.class);

        Liquid liquid_0 = 
      ((Liquid) singleComponentLiquidModel_0.getMaterial());

        ConstantMaterialPropertyMethod constantMaterialPropertyMethod_0 = 
      ((ConstantMaterialPropertyMethod) liquid_0.getMaterialProperties().getMaterialProperty(ConstantDensityProperty.class).getMethod());

        double density = constantMaterialPropertyMethod_0.getQuantity().getRawValue();
        int i = 0;
        Units unit_1 = ((Units) sim.getUnitsManager().getObject("kg/s"));
        Collection<Report> report = sim.getReportManager().getObjects();
        for(Report r : report)
        {
            
            if(unit_1 == r.getUnits())
            {
                ++i;
                ExpressionReport er = sim.getReportManager().createReport(ExpressionReport.class);
                er.setPresentationName(r.getPresentationName()+"_lpm");
                String p = "${MassFlow"+i+"Report}"+"*((60.0)/"+constantMaterialPropertyMethod_0.getQuantity().getRawValue()+")";
                er.setDefinition(p);
            }
        }
    }
    
    
    
    
    
    
}