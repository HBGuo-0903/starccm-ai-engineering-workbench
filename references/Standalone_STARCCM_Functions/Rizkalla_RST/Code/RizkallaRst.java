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
// Reactive Flow Demo
public class RizkallaRst extends StarMacro {

        // RPM Units [1/min]
        static final double RPM = __RPM__;

        // Fluid Properties
        static final double density = 997.561; // kg/m3
        static final double viscosity = 8.8871E-4; // Pa s

        // Time-Stepping
        boolean IsSteady = true;

        // New FileName
        String SimName = "";
        String SessionDirectory = "";

        // Steps to run
        int StepsToRun = __STEPS__;


        public void execute() {
                Simulation simulation = getActiveSimulation();
                SessionDirectory = simulation.getSessionDir();
                StartRstSimulation();
        }

        public String getFileName() {
                return String.format("Rizkalla_%s_RST.sim",  getRpmString());
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

        private String getRpmString() {
                Simulation simulation = getActiveSimulation();
                ScalarGlobalParameter sParameter = (ScalarGlobalParameter) simulation.get(GlobalParameterManager.class)
                                .getObject("RPM");
                return sParameter.getQuantity().getDefinition().replace(" ", "").replace(".0", "") + "RPM";
        }

        private void setRpm(double RPM) {
                Simulation simulation = getActiveSimulation();

                ScalarGlobalParameter rpmParameter = (ScalarGlobalParameter) simulation.get(GlobalParameterManager.class)
                                .getObject("RPM");

                rpmParameter.setDimensions(Dimensions.Builder().angularVelocity(1).build());
                Units rpmUnit = ((Units) simulation.getUnitsManager().getObject("rpm"));

                rpmParameter.getQuantity().setUnits(rpmUnit);
                rpmParameter.getQuantity().setValue(RPM);
        }

        private void StartRstSimulation() {
                
                setRpm(RPM);
                
                // Save and prepare
                SetAutoSave();
                SimName = getFileName();
                saveAs(SimName);

                // Run
                RunSteps(10000);
        }


        private void RunSteps(int steps) {
                Simulation simulation = getActiveSimulation();
                simulation.getSimulationIterator().run(steps);
        }

        private void createDimensionlessFieldFuncs(String name, String definition) {

                Simulation simulation = getActiveSimulation();

                UserFieldFunction userFieldFunction = simulation.getFieldFunctionManager().createFieldFunction();

                userFieldFunction.getTypeOption().setSelected(FieldFunctionTypeOption.Type.SCALAR);

                userFieldFunction.setPresentationName(name);
                userFieldFunction.setFunctionName(name);
                userFieldFunction.setDefinition(definition);

        }

        private void SetAutoSave() {

                Simulation simulation = getActiveSimulation();

                AutoSave autoSave = simulation.getSimulationIterator().getAutoSave();

                autoSave.setSeparator("_At_");
                autoSave.setFormatWidth(6);

                StarUpdate starUpdate = autoSave.getStarUpdate();

                starUpdate.setEnabled(true);
                if (IsSteady) {
                        starUpdate.getUpdateModeOption().setSelected(StarUpdateModeOption.Type.ITERATION);
                        IterationUpdateFrequency iterationUpdateFrequency = starUpdate.getIterationUpdateFrequency();
                        iterationUpdateFrequency.setIterations(5000);
                } else {
                        starUpdate.getUpdateModeOption().setSelected(StarUpdateModeOption.Type.TIMESTEP);
                        TimeStepUpdateFrequency timeStepUpdateFrequency = starUpdate.getTimeStepUpdateFrequency();
                        timeStepUpdateFrequency.setTimeSteps(1000);
                }
                autoSave.setMaxAutosavedFiles(2);
        }

        private void CreateGlobalDimensionlessParameter(String name, String definition) {
                Simulation simulation = getActiveSimulation();
                ScalarGlobalParameter sParameter = (ScalarGlobalParameter) simulation.get(GlobalParameterManager.class)
                                .createGlobalParameter(ScalarGlobalParameter.class, name);

                sParameter.getQuantity().setDefinition(definition);
        }
}
