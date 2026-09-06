package optimate;

import java.util.*;
import star.common.*;
import star.common.StarScript;
import star.base.neo.*;
import star.base.report.*;
import star.energy.*;
import star.vis.*;
import star.flow.*;
import java.io.*;
import java.nio.file.*;
import star.meshing.*;
import star.cadmodeler.*;
import star.motion.*;
import star.species.*;
import star.turbulence.*;
import star.starcad2.*;
import star.base.generic.*;
import star.base.neo.NeoProperty;
import star.base.generic.GenericObjectManager;

public class StarDriver extends StarMacro {

    Simulation sim;
    String simname;
    GenericObjectManager gom;
    String directoryCAD = "/u/xeons24/people/aarong/optimate/projects/2dAirfoil/";
    String directoryMACRO = "/u/xeons24/people/aarong/";
    File fileIn;
    File fileOut;
    boolean userMarkedError = false;
    boolean errorDesign = false;
    CameraStateInput[] sceneViews = new CameraStateInput[6];
    ExpressionReport robustnessReport;
    boolean failed = false;

    @Override
    public void execute() {

        try {
            sim = getActiveSimulation();
            simname = sim.getPresentationName();
            gom = getGenericObjectManager();
            fileIn = new File(sim.getSessionDir() + File.separator + "airfoilUS_optimate.in");
            fileOut = new File(sim.getSessionDir() + File.separator + "airfoilUS_optimate.out");
            validateDirs();
            robustnessReport = createCADRobustnessReport();


            //Store current scene views
            Scene scene;
            CameraStateInput cameraStateInput;
            scene = getObject("core.apiFramework.StarGenericObjectWrapper_58");
            cameraStateInput = new CameraStateInput(scene.getCurrentView());
            sceneViews[0] = cameraStateInput;
            scene = getObject("core.apiFramework.StarGenericObjectWrapper_59");
            cameraStateInput = new CameraStateInput(scene.getCurrentView());
            sceneViews[1] = cameraStateInput;
            scene = getObject("core.apiFramework.StarGenericObjectWrapper_60");
            cameraStateInput = new CameraStateInput(scene.getCurrentView());
            sceneViews[2] = cameraStateInput;
            scene = getObject("core.apiFramework.StarGenericObjectWrapper_61");
            cameraStateInput = new CameraStateInput(scene.getCurrentView());
            sceneViews[3] = cameraStateInput;
            scene = getObject("core.apiFramework.StarGenericObjectWrapper_62");
            cameraStateInput = new CameraStateInput(scene.getCurrentView());
            sceneViews[4] = cameraStateInput;
            scene = getObject("core.apiFramework.StarGenericObjectWrapper_57");
            cameraStateInput = new CameraStateInput(scene.getCurrentView());
            sceneViews[5] = cameraStateInput;




            //Read design values from input file.
            try (Scanner scan = new Scanner(fileIn)) {
                String line;
                String split[];
                line = scan.nextLine();
                split = line.split(",");
                setVariable0(split);
                line = scan.nextLine();
                split = line.split(",");
                setVariable1(split);
                line = scan.nextLine();
                split = line.split(",");
                setVariable2(split);
                line = scan.nextLine();
                split = line.split(",");
                setVariable3(split);
                line = scan.nextLine();
                split = line.split(",");
                setVariable4(split);
                line = scan.nextLine();
                split = line.split(",");
                setVariable5(split);
                scan.close();
            } catch (Exception ex) {
                sim.println("Error Reading" + fileIn.getAbsolutePath() + "!");
                print(ex);
                errorDesign = true;
                return;
            }


            try {
                Collection<CadModel> cads = sim.get(SolidModelManager.class).getObjectsOf(CadModel.class);
                for (CadModel c:cads) {
                    c.update();
                }

                List<SolidModelPart> toUpdate = new ArrayList<>();
                for(GeometryPart gpi : sim.get(SimulationPartManager.class).getParts()) {
                    if (gpi instanceof SolidModelPart) {
                        SolidModelPart smp = (SolidModelPart) gpi;
                        if (smp.isDirty()) {
                            toUpdate.add(smp);
                        }
                    }
                }
                sim.get(SimulationPartManager.class).updateParts(toUpdate);
            } catch (Exception ex) {
                robustnessReport.setDefinition("0");
                failed = true;
                print(ex);
            }



            //Play all "Before Meshing" inserted macros.
            File coreapiFrameworkInsertedMacro_0 = new File(directoryMACRO + "test.java");
            new StarScript(sim, coreapiFrameworkInsertedMacro_0).play();





            //Export design results to output file.
            try (BufferedWriter out = new BufferedWriter(new FileWriter(fileOut))) {
                Report r0 = sim.getReportManager().getReport("CAD Success");
                out.append("CAD_Success, " + r0.getReportMonitorValue() + "\n");

                out.close();
            } catch (Exception ex) {
                sim.println("Error Writing Output File!");
                print(ex);
                errorDesign = true;
            }




            //Save plot and scene images.
            for (Scene scene_i : sim.getSceneManager().getScenes()) {
                for(Displayer d : scene_i.getDisplayerManager().getObjects()){
                    d.setRepresentation((PartRepresentation) sim.getRepresentationManager().getObject("Geometry"));
                }
            }
            if (!failed) {
            Scene scene0 = (Scene) sim.getObjectRegistry().getObject(sim.getObjectRegistry().generateObjectKey("star.vis.Scene_0"));
            scene0.printAndWait(sim.getSessionDir() + File.separator + "Mesh-scene" + ".png", 1, 1104, 964);

            Scene scene1 = (Scene) sim.getObjectRegistry().getObject(sim.getObjectRegistry().generateObjectKey("star.vis.Scene_1"));
            scene1.printAndWait(sim.getSessionDir() + File.separator + "Scalar_-_Mach-scene" + ".png", 1, 1104, 964);

            Scene scene2 = (Scene) sim.getObjectRegistry().getObject(sim.getObjectRegistry().generateObjectKey("star.vis.Scene_2"));
            scene2.printAndWait(sim.getSessionDir() + File.separator + "Scalar_-_Pressure-scene" + ".png", 1, 1104, 964);

            Scene scene3 = (Scene) sim.getObjectRegistry().getObject(sim.getObjectRegistry().generateObjectKey("star.vis.Scene_3"));
            scene3.printAndWait(sim.getSessionDir() + File.separator + "Scalar_-_Total_Pressure-scene" + ".png", 1, 1104, 964);

            Scene scene4 = (Scene) sim.getObjectRegistry().getObject(sim.getObjectRegistry().generateObjectKey("star.vis.Scene_4"));
            scene4.printAndWait(sim.getSessionDir() + File.separator + "Streamlines_-_Vorticity-scene" + ".png", 1, 1104, 964);

            Scene scene5 = (Scene) sim.getObjectRegistry().getObject(sim.getObjectRegistry().generateObjectKey("star.vis.Scene_5"));
            scene5.printAndWait(sim.getSessionDir() + File.separator + "Geom-scene" + ".png", 1, 1104, 964);

            }


        } catch (Exception ex) {
            sim.println("Error during analysis!");
            print(ex);
            errorDesign = true;
        } finally {
            if (failed || userMarkedError) {
                sim.println("Design STAR-CCM+ simulation completed with errors");
            } else {
                sim.println("Design STAR-CCM+ simulation completed successfully");
            }
            File f = new File(sim.getSessionDir() + File.separator + "DONE");
            try {
                f.createNewFile();
            } catch (IOException ex) {
            }
        }
    }

    private void setVariable0(String split[]) {
        Double d = Double.parseDouble(split[1]);
        GenericObject go = gom.getObject("core.apiFramework.StarGenericObjectWrapper_38");
        ClientServerObjectKey starObjKey = go.getGenericProperties().getObjectKey("Optimate tagged object", sim.getObjectRegistry());
        ScalarQuantityDesignParameter dp = sim.getObjectRegistry().getObject(starObjKey);
        ScalarPhysicalQuantityInput spqi_1 = dp.getQuantityInput();
        spqi_1.setDefinition(d + "");
        dp.setQuantityInput(spqi_1);
        sim.println("Presentation Name = " + dp.getPresentationName());
        sim.println("Set Design Parameter named LeadingFlapRotateAngle to " + d);
        return;
    }

    private void setVariable1(String split[]) {
        Double d = Double.parseDouble(split[1]);
        GenericObject go = gom.getObject("core.apiFramework.StarGenericObjectWrapper_39");
        ClientServerObjectKey starObjKey = go.getGenericProperties().getObjectKey("Optimate tagged object", sim.getObjectRegistry());
        ScalarQuantityDesignParameter dp = sim.getObjectRegistry().getObject(starObjKey);
        ScalarPhysicalQuantityInput spqi_1 = dp.getQuantityInput();
        spqi_1.setDefinition(d + "");
        dp.setQuantityInput(spqi_1);
        sim.println("Presentation Name = " + dp.getPresentationName());
        sim.println("Set Design Parameter named TrailingFlapRotateAngle to " + d);
        return;
    }

    private void setVariable2(String split[]) {
        Double d = Double.parseDouble(split[1]);
        CoordinateDesignParameter design = (CoordinateDesignParameter) sim.getObjectRegistry().getObject(sim.getObjectRegistry().generateObjectKey("star.cadmodeler.CoordinateDesignParameter_0"));
        CadModelCoordinate coord = design.getQuantity();
        double vector[] = coord.getValue().toDoubleArray();
        vector[0] = d;
        Units u0 = coord.getUnits0();
        Units u1 = coord.getUnits1();
        Units u2 = coord.getUnits2();
        coord.setCoordinate(u0, u1, u2, new DoubleVector(new double[] {vector[0], vector[1], vector[2]}));
        sim.println("Set Design Parameter named TranslateLeadingFlap_X to " + d);
        try{
            LabCoordinateSystem lab = sim.getCoordinateSystemManager().getLabCoordinateSystem();
            CartesianCoordinateSystem cs = ((CartesianCoordinateSystem) lab.getLocalCoordinateSystemManager().getObject("TranslateLeadingFlap"));
            Coordinate origin = cs.getOrigin();
            double[] dv = origin.getValue().toDoubleArray();
            dv[0] = d;
            origin.setValue(new DoubleVector(dv));
            sim.println("Moved coordinate system named TranslateLeadingFlap");
        }catch(Exception e){
        }
    }

    private void setVariable3(String split[]) {
        Double d = Double.parseDouble(split[1]);
        CoordinateDesignParameter design = (CoordinateDesignParameter) sim.getObjectRegistry().getObject(sim.getObjectRegistry().generateObjectKey("star.cadmodeler.CoordinateDesignParameter_0"));
        CadModelCoordinate coord = design.getQuantity();
        double vector[] = coord.getValue().toDoubleArray();
        vector[1] = d;
        Units u0 = coord.getUnits0();
        Units u1 = coord.getUnits1();
        Units u2 = coord.getUnits2();
        coord.setCoordinate(u0, u1, u2, new DoubleVector(new double[] {vector[0], vector[1], vector[2]}));
        sim.println("Set Design Parameter named TranslateLeadingFlap_Y to " + d);
        try{
            LabCoordinateSystem lab = sim.getCoordinateSystemManager().getLabCoordinateSystem();
            CartesianCoordinateSystem cs = ((CartesianCoordinateSystem) lab.getLocalCoordinateSystemManager().getObject("TranslateLeadingFlap"));
            Coordinate origin = cs.getOrigin();
            double[] dv = origin.getValue().toDoubleArray();
            dv[1] = d;
            origin.setValue(new DoubleVector(dv));
            sim.println("Moved coordinate system named TranslateLeadingFlap");
        }catch(Exception e){
        }
    }

    private void setVariable4(String split[]) {
        Double d = Double.parseDouble(split[1]);
        CoordinateDesignParameter design = (CoordinateDesignParameter) sim.getObjectRegistry().getObject(sim.getObjectRegistry().generateObjectKey("star.cadmodeler.CoordinateDesignParameter_1"));
        CadModelCoordinate coord = design.getQuantity();
        double vector[] = coord.getValue().toDoubleArray();
        vector[0] = d;
        Units u0 = coord.getUnits0();
        Units u1 = coord.getUnits1();
        Units u2 = coord.getUnits2();
        coord.setCoordinate(u0, u1, u2, new DoubleVector(new double[] {vector[0], vector[1], vector[2]}));
        sim.println("Set Design Parameter named TranslateTrailingFlap_X to " + d);
        try{
            LabCoordinateSystem lab = sim.getCoordinateSystemManager().getLabCoordinateSystem();
            CartesianCoordinateSystem cs = ((CartesianCoordinateSystem) lab.getLocalCoordinateSystemManager().getObject("TranslateTrailingFlap"));
            Coordinate origin = cs.getOrigin();
            double[] dv = origin.getValue().toDoubleArray();
            dv[0] = d;
            origin.setValue(new DoubleVector(dv));
            sim.println("Moved coordinate system named TranslateTrailingFlap");
        }catch(Exception e){
        }
    }

    private void setVariable5(String split[]) {
        Double d = Double.parseDouble(split[1]);
        CoordinateDesignParameter design = (CoordinateDesignParameter) sim.getObjectRegistry().getObject(sim.getObjectRegistry().generateObjectKey("star.cadmodeler.CoordinateDesignParameter_1"));
        CadModelCoordinate coord = design.getQuantity();
        double vector[] = coord.getValue().toDoubleArray();
        vector[1] = d;
        Units u0 = coord.getUnits0();
        Units u1 = coord.getUnits1();
        Units u2 = coord.getUnits2();
        coord.setCoordinate(u0, u1, u2, new DoubleVector(new double[] {vector[0], vector[1], vector[2]}));
        sim.println("Set Design Parameter named TranslateTrailingFlap_Y to " + d);
        try{
            LabCoordinateSystem lab = sim.getCoordinateSystemManager().getLabCoordinateSystem();
            CartesianCoordinateSystem cs = ((CartesianCoordinateSystem) lab.getLocalCoordinateSystemManager().getObject("TranslateTrailingFlap"));
            Coordinate origin = cs.getOrigin();
            double[] dv = origin.getValue().toDoubleArray();
            dv[1] = d;
            origin.setValue(new DoubleVector(dv));
            sim.println("Moved coordinate system named TranslateTrailingFlap");
        }catch(Exception e){
        }
    }

    private ExpressionReport createCADRobustnessReport() {
        ExpressionReport r;
        try {
            r = (ExpressionReport) sim.get(ReportManager.class).getReport("CAD Success");
        } catch (Exception e) {
            r = sim.get(ReportManager.class).createReport(ExpressionReport.class);
            r.setPresentationName("CAD Success");
        }
        r.setDefinition("1");
        return r;
    }
    private GenericObjectManager getGenericObjectManager() {
        NeoProperty args = new NeoProperty();
        args.put("BaseName", "StarGeneric");
        sim.execute("LoadLibrary", args);
        return GenericObjectManager.get(sim);
    }

    private void createInteractionFunction() {
        UserFieldFunction uf = sim.getFieldFunctionManager().createFieldFunction();
        uf.setPresentationName("userinteractionfunction");
        uf.setFunctionName("userinteractionfunction");
        uf.setDefinition("0");
    }

    private void validateDirs() {
        File f = new File(directoryMACRO);
        if (!f.isAbsolute()) {
            directoryMACRO = sim.getSessionDir() + File.separator + directoryMACRO;
        }

        f = new File(directoryCAD);
        if (!f.isAbsolute()) {
            directoryCAD = sim.getSessionDir() + File.separator + directoryCAD;
        }
    }

    private void print(Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        sim.println(sw.toString());
    }

    private <T extends ClientServerObject> T getObject(String genericObject) throws Exception {
        GenericObject obj = (GenericObject) gom.getObject(genericObject);
        NeoProperty genericProperties = obj.getGenericProperties();
        ClientServerObjectKey csoKey = genericProperties.getObjectKey("Optimate tagged object", sim.getObjectRegistry());
        if (csoKey == null) {
            throw new Exception("Unable to locate client server object!");
        }
        return genericProperties.getObject("Optimate tagged object", sim.getObjectRegistry());
    }


    private void createLink() {
        String fs = File.separator;
        String design = sim.getSessionDirFile().getParentFile().getName();
        String study = sim.getSessionDirFile().getParentFile().getParentFile().getParent() + fs;
        String output = study + "POST_0" + fs + design + fs + "star" + fs + "Tool_star_output.msg";
        if (new File(output).exists()) {
            try {
                Path source = Paths.get(output);
                Path dest = Paths.get(sim.getSessionDir() + fs + "Tool_star_output.msg");
                Files.createSymbolicLink(dest, source);
            } catch (UnsupportedOperationException ex) {
                print(ex);
            } catch (FileAlreadyExistsException ex) {
                print(ex);
            } catch (SecurityException ex) {
                print(ex);
            } catch (IOException ex) {
                print(ex);
            }
        }
    }

    private String getDesignNumber() {
        String path = sim.getSessionDir();
        String split[];
        int design;
        if(System.getProperty("os.name").toLowerCase().contains("win")){
            split = path.split("\\\\");
        } else {
            split = path.split("/");
        }
        int lastIndex = 0;
        int index = 0;
        for (String s : split) {
            if (s.contains("Design")) {
                lastIndex = index;
            }
            index++;
        }
        String s = split[lastIndex].replace("Design", "").replace("-ERROR", "");
        design = Integer.parseInt(s);
        return String.format("-%03d",design);
    }

}
