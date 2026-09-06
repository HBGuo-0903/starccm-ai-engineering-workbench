package macro;

import java.util.*;
import java.io.*;
import java.nio.file.*;


import star.common.*;
import star.base.neo.*;
import star.base.report.*;

import star.meshing.*;
import star.prismmesher.*;
import star.solidmesher.*;
import star.dualmesher.*;
import star.resurfacer.*;

import star.kwturb.*;
import star.lesturb.*;

import star.segregatedflow.*;
import star.material.*;
import star.turbulence.*;
import star.flow.*;
import star.metrics.*;
import star.vis.*;




public class CreateNewPhysicsLes extends StarMacro {


    // TurbulenceModelling
    String TurbulenceModel = "LES"; // "RANS-kOmega" / "LES" / "Laminar"
    String kOmegaConsituitive = "Cubic"; // "Cubic" / "Linear" / "QCR"
    boolean IsSteady = false;

 public void execute() {
     CreatePhysics(IsSteady, TurbulenceModel);
 }
 
 
 
 
 private void CreatePhysics(boolean isSteady, String turbulenceModel) {

    Simulation simulation = 
      getActiveSimulation();

    PhysicsContinuum physicsContinuum = 
      simulation.getContinuumManager().createContinuum(PhysicsContinuum.class);

    physicsContinuum.enable(ThreeDimensionalModel.class);

    if(isSteady){
        physicsContinuum.enable(SteadyModel.class);
    } else {
        physicsContinuum.enable(ImplicitUnsteadyModel.class);
    }
    
    physicsContinuum.enable(SingleComponentLiquidModel.class);

    physicsContinuum.enable(SegregatedFlowModel.class);

    physicsContinuum.enable(ConstantDensityModel.class);


    if(turbulenceModel == "RANS-kOmega") {
        physicsContinuum.enable(TurbulentModel.class);
        
        physicsContinuum.enable(RansTurbulenceModel.class);
        
        physicsContinuum.enable(KOmegaTurbulence.class);

        physicsContinuum.enable(SstKwTurbModel.class);

        physicsContinuum.enable(KwAllYplusWallTreatment.class);

        physicsContinuum.enable(GammaTransitionModel.class);
        
        
        SstKwTurbModel sstKwTurbModel = physicsContinuum.getModelManager().getModel(SstKwTurbModel.class);
        
        if(kOmegaConsituitive == "Cubic"){
            sstKwTurbModel.getKwTurbConstitutiveOption().setSelected(KwTurbConstitutiveOption.Type.CUBIC);
        }else if(kOmegaConsituitive == "QCR"){
            sstKwTurbModel.getKwTurbConstitutiveOption().setSelected(KwTurbConstitutiveOption.Type.QCR);
        } else if(kOmegaConsituitive == "Linear"){
            sstKwTurbModel.getKwTurbConstitutiveOption().setSelected(KwTurbConstitutiveOption.Type.LINEAR);
        }            
        
        
    } else if(turbulenceModel == "LES"){
        physicsContinuum.enable(TurbulentModel.class);

        physicsContinuum.enable(LesTurbulenceModel.class);

        physicsContinuum.enable(WaleSgsModel.class);

        physicsContinuum.enable(LesAllYplusWallTreatment.class);
    } else {
        physicsContinuum.enable(LaminarModel.class);
        
    }
  }
}