// STAR-CCM+ macro: CouetteCell.java
// Written by STAR-CCM+ 14.06.013
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
import star.passivescalar.*;

import star.kwturb.*;
import star.lesturb.*;
import star.rsturb.*;
import star.keturb.*;

import star.passivescalar.*;
import star.segregatedenergy.*;
import star.segregatedflow.*;
import star.material.*;
import star.turbulence.*;
import star.flow.*;
import star.metrics.*;
import star.vis.*;

// Simulation file note:
public class SetRPM extends StarMacro {

        public final double RPM = 1140;

        public void execute() {
                setRPM(RPM);
                saveAs("Base_1140RPM.sim");
        }
        
        private void setRPM(double rpm) {
                Simulation simulation = getActiveSimulation();
                
                ScalarGlobalParameter rpmParameter = (ScalarGlobalParameter) simulation.get(GlobalParameterManager.class)
                                .createGlobalParameter(ScalarGlobalParameter.class, "RPM");
                rpmParameter.setDimensions(Dimensions.Builder().angularVelocity(1).build());
                Units rpmUnit = ((Units) simulation.getUnitsManager().getObject("rpm"));
                rpmParameter.getQuantity().setUnits(rpmUnit);
                rpmParameter.getQuantity().setValue(rpm);
        }

        private void saveAs(String simName) {
                Simulation simulation = getActiveSimulation();
                String SessionDirectory = simulation.getSessionDir();
                String fullPath = SessionDirectory + "/" + simName;

                try {
                        simulation.saveState(fullPath);
                } catch (Exception ex) {
                        simulation.println(ex);
                }
        }
}
