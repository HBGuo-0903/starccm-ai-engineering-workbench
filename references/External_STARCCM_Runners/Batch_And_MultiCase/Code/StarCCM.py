__all__ = ['StarCCM_wrapper']

import os
from openmdao.main.api import Component
from openmdao.lib.datatypes.api import Float, Str, Int, Enum, File, Bool
from openmdao.lib.components.api import ExternalCode


# Make sure that your class has some kind of docstring. Otherwise
# the descriptions for your variables won't show up in the
# source ducumentation.
#
# http://openmdao.org/dev_docs/plugin-guide/filewrapper_plugin.html
# http://openmdao.org/dev_docs/plugin-guide/index.html
#
class StarCCM_wrapper(ExternalCode):
    """ This is a wrapper of Star-CCM+. It can generate both volume grid and CFD solutions.
User is expected to have access to Star-CCM+ executable and power-on-demand license.
It is also expected that the code will be run in PBS.

If the user wants to generate a volume grid (runVolGrid = 1), a surface grid file must
have been provided:

- STLFile (a .stl file)

By default, two more input files are automatically generated: 

- javaBatch1File: a Star-CCM+ macro (a .java file),
- cshBatch1File:  a C-shell script (a .csh file).

However, user can choose to provide his/her own C-shell script (cshUserBatch1 = 1).
If everything goes smoothly, it will generate a volume grid:

- simMeshFile: the volume grid (a .sim file).

Volume grid can be used to generate CFD solutions.

If the user wants to generate a CFD solution (runCFD = 1), a mesh file (the simMeshFile
file) must have been either generated in the previous step or provided by the user.

By default, two more input files are automatically generated:

- javaBatch2File: a Star-CCM+ macro (a .java file),
- cshBatch2File:  a C-shell script (a .csh file).

However, user can choose to provide his/her own C-shell script (cshUserBatch2 = 1).
If everything goes smoothly, it will generate a CFD solution:

- simFlowFile: the CFD solution (a .sim file).

several CSV and PNG files can be generated too.

The inputs are in imperial system.
    """

    # declare inputs and outputs:
    numNodes       = Int(5,iotype='in',desc='number of nodes to request')
    numCPUs        = Int(24,iotype='in',desc='number of CPUs per node to request')
    mpiProcs       = Int(24,iotype='in',desc='number of mpi processes')
    WallTime       = Str('2:00:00',iotype='in',desc='wait time, format: 00:00:00')
    GroupID        = Str('my_group_id',iotype='in',desc='my group ID')
    jobName        = Str('starccm',iotype='in',desc='job name')
    queue          = Enum('devel',('long','normal','debug','devel'),iotype='in',desc='queue level')

    runVolGrid     = Enum(0,(0,1),iotype='in',desc='generate volume grid with default to no(0)')
    javaBatch1File = Str('my_mesh_java_script',iotype='in',desc='name of batch file for a meshing run without file extension .java')
    STLFile        = Str('stl_file',iotype='in',desc='name of the STL file')
    simMeshFile    = Str('g1_ATR_mesh.sim',iotype='in',desc='name of mesh file (a .sim file).')

    mySphereRadius = Float(1500.0,iotype='in',units='ft', desc='radius of freestream outer boundary in feet')
    mySphereX      = Float(0.0,iotype='in',units='ft', desc='Center of freestream sphere in feet')
    mySphereY      = Float(0.0,iotype='in',units='ft', desc='Center of freestream sphere in feet')
    mySphereZ      = Float(0.0,iotype='in',units='ft', desc='Center of freestream sphere in feet')
    mySphereTriangles = Float(200.0,iotype='in',units='ft', desc='size of sphere outer boundary facets')
    myPrismFieldRatio = Float(10.0,iotype='in',units='ft', desc='thickness ratio of near field to outermost prism')
    myBLcells      = Int(9,iotype='in', desc='number of cells in boundary layer normal direction')
    myBLthickness  = Float(10.0,iotype='in',units='inch', desc='thickness of boundary layer in inches')
    myBaseSize     = Float(8.0,iotype='in',units='ft', desc='mesh base size in feet')
    myCurvature    = Int(90,iotype='in', desc='number of points to divide a circle')
    mySurfaceGrowthRate = Float(1.25,iotype='in', desc='growth rate (max size ratio) of surface triangles')
    myFeatureAngle = Float(17.0,iotype='in',units='deg', desc='maximum angle for defining sharp edges on ATR models in degrees')
    myMinMesh      = Float(0.2,iotype='in', desc=' smallest cell size in percent')
    myEdgeTarget   = Float(0.4,iotype='in', desc=' target size for feature curve edges in percent')

    makeSurfaceMesh= Bool(True,iotype='in', desc='use true to make surface mesh, false to skip')
    makeVolumeMesh = Bool(True,iotype='in', desc='use true to make volume mesh, false to skip')
    saveMeshFile   = Bool(True,iotype='in', desc='use true to save final mesh file, false to skip')

    runCFD         = Enum(1,(0,1),iotype='in',desc='run CFD with default to yes(1)')
    javaBatch2File = Str('my_flow_java_script',iotype='in',desc='name of batch file for a CFD run without file extension .java')
    simFlowFile    = Str('g1_ATR_flow.sim',iotype='in',desc='name of simulation file (a .sim file).')

    myMach         = Float(0.5,iotype='in',desc='Mach number')
    myAoA          = Float(3.0,iotype='in',desc='angle of attack, degree')
    myBeta         = Float(3.0,iotype='in',desc='beta, degree')
    myPressure     = Float(6.75886,iotype='in',desc='pressure, psi')
    myTemperature  = Float(447.415,iotype='in',desc='temperature, Rankine')
    myMoment_X     = Float(0.0,iotype='in',desc='moment reference location, X, feet')
    myMoment_Y     = Float(0.0,iotype='in',desc='moment reference location, Y, feet')
    myMoment_Z     = Float(0.0,iotype='in',desc='moment reference location, Z, feet')

    myCFL          = Float(5.0,iotype='in',desc='final CFL number')
    myIteration    = Int(5,iotype='in',desc='number of flow solver iterations')
    myCFLRampStart = Float(0.1,iotype='in',desc='starting CFL number')
    myCFLRampIters = Int(20,iotype='in',desc='number of iterations over which to ramp CFL up to final value')
    useExpert      = Bool(True,iotype='in', desc='flag whether use expert driver for solver control')
    myColorLevels  = Int(64,iotype='in',desc='number of colorbar levels in key')

    saveSimFile    = Bool(True,iotype='in', desc='use true to save final sim file, false to skip')
    saveResidualImage= Bool(True,iotype='in', desc='use true to save residuals image, false to skip')
    saveFandMImages= Bool(True,iotype='in', desc='use true to save final F&M images, false to skip')
    saveCpImages   = Bool(True,iotype='in', desc='use true to save final Cp images, false to skip')
    saveResidualCSV= Bool(True,iotype='in', desc='use true to save residuals history, false to skip')
    saveFandMCSV   = Bool(True,iotype='in', desc='use true to save final F&M histories, false to skip')

    cshUserBatch1  = Enum(0,(0,1),iotype='in',desc='User will provide a shell script for mesh sim with default to no(0)')
    cshBatch1File  = Str('my_csh_script',iotype='in',desc='name of batch file for mesh sim without file extension .csh')
    cshUserBatch2  = Enum(0,(0,1),iotype='in',desc='User will provide a shell script for CFD sim with default to no(0)')
    cshBatch2File  = Str('my_csh_script',iotype='in',desc='name of batch file for CFD sim without file extension .csh')
    LicAccessName  = Str('my_lic_host_name',iotype='in',desc='starccm license is accessed from this machine')
    LicLocalPort   = Str('2999',iotype='in',desc='the port number on local machine to access license')
    starccmExec    = Str('starccm+',iotype='in',desc='absolute path of starccm execatable')

    CDLMD_LicFile  = Str('1999@localhost',desc='CDLMD license file')
    starccmLic     = Str('my_star_ccm_lic',desc='Power on Demand license string')

    def __init__(self):
        """Constructor for the StarCCM wrapper"""
        super(StarCCM_wrapper, self).__init__()
        self.command = ['qsub', 'runStarCCM.pbs']
        self.starccmLic = os.environ['STAR_POWER_ON_DEMAND_LIC']
        self.CDLMD_LicFile = os.environ['LM_LICENSE_FILE']




    def write_pbs(self):
        """
        Create a .pbs file in working directory for qsub command. PBS is expected to be available.
        On command line:
        % qsub 'runStarCCM.pbs
        """
        fout = open("runStarCCM.pbs", "w")
        fout.write("#PBS -S /bin/csh\n")
        fout.write("#PBS -l select=" + str(self.numNodes) + ":ncpus=" + str(self.numCPUs) + ":mpiprocs=" + str(self.mpiProcs) + ":model=has,walltime=" + self.WallTime  + "\n\n")
        fout.write("#PBS -W group_list=" + self.GroupID + "\n")
        fout.write("#PBS -j oe\n")
        fout.write("#PBS -q " + self.queue + "\n")
        fout.write("#PBS -N " + self.jobName + "\n")
        fout.write("#PBS -m e\n")
        fout.write("#PBS -W block=true\n\n")
        fout.write("cd $PBS_O_WORKDIR\n")

        if self.runVolGrid == 1:
            #fout.write("/bin/rm -f " + self.simMeshFile + ".sim\n")
            fout.write("/bin/rm -f starccmMeshRun.out\n")
            fout.write("chmod u+x " + self.cshBatch1File + ".csh\n")
            # do not use >>& because it will fail in some environment
            fout.write("./" + self.cshBatch1File + ".csh -powerOnDemand " + self.javaBatch1File + ".java >& starccmMeshRun.out\n\n")
        else:
            fout.write("echo 'User chooses not to make a mesh run.'\n")

        if self.runCFD == 1:
            fout.write("chmod u+x " + self.cshBatch2File + ".csh\n")
            fout.write("/bin/rm -f *.csv *.png starccmFlowRun.out\n")
            # do not use >>& because it will fail in some environment
            fout.write("./" + self.cshBatch2File + ".csh -powerOnDemand " + self.javaBatch2File + ".java " + self.simMeshFile + " >& starccmFlowRun.out\n\n")
            fout.write("# rename the strange file names\n")
            fout.write("/bin/mv \$PWDForceX.csv    ForceX.csv\n")
            fout.write("/bin/mv \$PWDForceY.csv    ForceY.csv\n")
            fout.write("/bin/mv \$PWDForceZ.csv    ForceZ.csv\n")
            fout.write("/bin/mv \$PWDMomentX.csv   MomentX.csv\n")
            fout.write("/bin/mv \$PWDMomentY.csv   MomentY.csv\n")
            fout.write("/bin/mv \$PWDMomentZ.csv   MomentZ.csv\n")
            fout.write("/bin/mv \$PWDResiduals.csv Residuals.csv\n\n")
            fout.write("/bin/mv \$PWDForceX.png    ForceX.png\n")
            fout.write("/bin/mv \$PWDForceY.png    ForceY.png\n")
            fout.write("/bin/mv \$PWDForceZ.png    ForceZ.png\n")
            fout.write("/bin/mv \$PWDMomentX.png   MomentX.png\n")
            fout.write("/bin/mv \$PWDMomentY.png   MomentY.png\n")
            fout.write("/bin/mv \$PWDMomentZ.png   MomentZ.png\n")
            fout.write("/bin/mv \$PWDResiduals.png Residuals.png\n")
            fout.write("/bin/mv \$PWDUpperCp.png   UpperCp.png\n")
            fout.write("/bin/mv \$PWDLowerCp.png   LowerCp.png\n")
            fout.write("/bin/rm -rf null\n")
        else:
            fout.write("echo 'User chooses not to make a CFD run.'\n")

        fout.close()





    def write_mesh_java(self):
        """
        Create STAR macro javaBatch1File.java file for volume grid run.
        On command line:
        starccm+ -power -licpath 2999@localhost -podkey <LICENSE KEY> -batch javaBatch1File.java
        """
        fout = open(self.javaBatch1File+".java","w")
        fout.write("""\
// STAR-CCM+ macro
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.resurfacer.*;
import star.trimmer.*;
import star.prismmesher.*;
import star.meshing.*;

public class %s extends StarMacro {

  public void execute() {
    execute0();
  }
  """ % (self.javaBatch1File))

        fout.write("""\

  private void execute0() {

    // Directory for output files and final sim file (if saved)
    String myPath = "$PWD";

    String myInputSTLFilename = "%s";  // contains aircraft geometry

    String myOutputMeshFilename = "%s";  // output sim name with volume mesh

    double mySphereRadius_ft = %f;  // radius of freestream outer boundary in feet
    double mySphereX_ft = %f;          // Center of freestream sphere in feet
    double mySphereY_ft = %f;
    double mySphereZ_ft = %f;
    double mySphereTriangles_ft = %f;  // size of sphere outer boundary facets

    double myPrismFieldRatio = %f;   // thickness ratio of near field to outermost prism

    int myBLcells = %d;             // number of cells in boundary layer normal direction
    double myBLthickness_in = %f;    // thickness of boundary layer in inches

    double myBaseSize_ft = %f;        // mesh base size in feet
    int myCurvature = %d;           // number of points to divide a circle
    double mySurfaceGrowthRate = %f; // growth rate (max size ratio) of surface triangles
    double myFeatureAngle_deg = %f;  // maximum angle for defining sharp edges on ATR model

    double myMinMesh_pct = %f;       // smallest cell size in percent
    double myEdgeTarget_pct = %f;    // target size for feature curve edges in percent

    boolean bln_makeSurfaceMesh = %s; // use true to make surface mesh, false to skip
    boolean bln_makeVolumeMesh = %s;  // use true to make volume mesh, false to skip
    boolean bln_saveMeshFile = %s;    // use true to save final mesh file, false to skip
    """ % (self.STLFile,self.simMeshFile,self.mySphereRadius,self.mySphereX,self.mySphereY,
           self.mySphereZ,self.mySphereTriangles,self.myPrismFieldRatio,self.myBLcells,
           self.myBLthickness,self.myBaseSize,self.myCurvature,self.mySurfaceGrowthRate,
           self.myFeatureAngle,self.myMinMesh,self.myEdgeTarget,
           str(self.makeSurfaceMesh).lower(),str(self.makeVolumeMesh).lower(),str(self.saveMeshFile).lower()))

        fout.write("""\

    if (!bln_makeSurfaceMesh) bln_makeVolumeMesh = false;

    // Start of STAR macro
    Simulation simulation_0 = getActiveSimulation();

    Units units_0 = simulation_0.getUnitsManager().getPreferredUnits(new IntVector(new int[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));
    Units units_1 = ((Units) simulation_0.getUnitsManager().getObject("ft"));
    units_1.setPreferred(true);

    PartImportManager partImportManager_0 = simulation_0.get(PartImportManager.class);

    // Read concatenated STL parts
    //partImportManager_0.importStlPart(resolvePath(myPath+myInputSTLFilename), "OneSurfacePerPatch", units_1, true, 1.0E-5);
    partImportManager_0.importStlPart(resolvePath(myInputSTLFilename), "OneSurfacePerPatch", units_1, true, 1.0E-5);

    MeshPartFactory meshPartFactory_0 = simulation_0.get(MeshPartFactory.class);

    SimpleSpherePart simpleSpherePart_0 = meshPartFactory_0.createNewSpherePart(simulation_0.get(SimulationPartManager.class));

    simpleSpherePart_0.setDoNotRetessellate(true);

    LabCoordinateSystem labCoordinateSystem_0 = simulation_0.getCoordinateSystemManager().getLabCoordinateSystem();

    simpleSpherePart_0.setCoordinateSystem(labCoordinateSystem_0);

    Coordinate coordinate_0 = simpleSpherePart_0.getOrigin();

    coordinate_0.setCoordinateSystem(labCoordinateSystem_0);
    coordinate_0.setCoordinate(units_1, units_1, units_1, new DoubleVector(new double[] {0.0, 0.0, 0.0}));

    // Set location of freestream sphere center (x, y, z) in feet
    coordinate_0.setValue(new DoubleVector(new double[] {mySphereX_ft, mySphereY_ft, mySphereZ_ft}));

    simpleSpherePart_0.getRadius().setUnits(units_1);

    // Set freestream sphere radius in feet
    simpleSpherePart_0.getRadius().setValue(mySphereRadius_ft);
    simpleSpherePart_0.getTessellationDensityOption().setSelected(TessellationDensityOption.MEDIUM);
    simpleSpherePart_0.rebuildSimpleShapePart();
    simpleSpherePart_0.setDoNotRetessellate(false);

    Region region_0 = simulation_0.getRegionManager().createEmptyRegion();

    region_0.setPresentationName("Region");
    Boundary boundary_0 = region_0.getBoundaryManager().getBoundary("Default");

    region_0.getBoundaryManager().removeBoundaries(new NeoObjectVector(new Object[] {boundary_0}));
    FeatureCurve featureCurve_0 = ((FeatureCurve) region_0.getFeatureCurveManager().getObject("Default"));

    region_0.getFeatureCurveManager().removeObjects(featureCurve_0);
    FeatureCurve featureCurve_1 = region_0.getFeatureCurveManager().createEmptyFeatureCurveWithName("Feature Curve");

    MeshPart meshPart_0 = ((MeshPart) simulation_0.get(SimulationPartManager.class).getPart("combined"));

    simulation_0.getRegionManager().newRegionsFromParts(new NeoObjectVector(new Object[] {meshPart_0, simpleSpherePart_0}), "OneRegion", region_0, "OneBoundaryPerPartSurface", null, "OneFeatureCurve", featureCurve_1, false);

    MeshContinuum meshContinuum_0 = simulation_0.getContinuumManager().createContinuum(MeshContinuum.class);

    PhysicsContinuum physicsContinuum_0 = simulation_0.getContinuumManager().createContinuum(PhysicsContinuum.class);

    meshContinuum_0.enable(ResurfacerMeshingModel.class);

    // Use trimmer (Cartesian hex) mesh
    meshContinuum_0.enable(TrimmerMeshingModel.class);

    meshContinuum_0.enable(PrismMesherModel.class);

    // Base size in feet - larger values makes coarser grids, smaller values makes finer grids
    meshContinuum_0.getReferenceValues().get(BaseSize.class).setValue(myBaseSize_ft);

    ResurfacerMeshingModel resurfacerMeshingModel_0 = meshContinuum_0.getModelManager().getModel(ResurfacerMeshingModel.class);
    resurfacerMeshingModel_0.setDoCompatibilityRefinement(true);
    resurfacerMeshingModel_0.setDoAutomaticSurfaceRepair(false);

    MaxTrimmerSizeToPrismThicknessRatio maxTrimmerSizeToPrismThicknessRatio_0 = meshContinuum_0.getReferenceValues().get(MaxTrimmerSizeToPrismThicknessRatio.class);
    maxTrimmerSizeToPrismThicknessRatio_0.setLimitCellSizeByPrismThickness(true);
    SizeThicknessRatio sizeThicknessRatio_0 = maxTrimmerSizeToPrismThicknessRatio_0.getSizeThicknessRatio();

    // Prism to field thickness ratio
    sizeThicknessRatio_0.setNeighboringThicknessMultiplier(myPrismFieldRatio);

    NumPrismLayers numPrismLayers_0 = meshContinuum_0.getReferenceValues().get(NumPrismLayers.class);

    // Number of boundary layer cells
    numPrismLayers_0.setNumLayers(myBLcells);

    PrismThickness prismThickness_0 = meshContinuum_0.getReferenceValues().get(PrismThickness.class);

    prismThickness_0.getRelativeOrAbsoluteOption().setSelected(RelativeOrAbsoluteOption.ABSOLUTE);

    GenericAbsoluteSize genericAbsoluteSize_0 = ((GenericAbsoluteSize) prismThickness_0.getAbsoluteSize());

    Units units_2 = ((Units) simulation_0.getUnitsManager().getObject("in"));

    genericAbsoluteSize_0.getValue().setUnits(units_2);

    // Boundary layer thickness in inches
    genericAbsoluteSize_0.getValue().setValue(myBLthickness_in);

    SurfaceCurvature surfaceCurvature_0 = meshContinuum_0.getReferenceValues().get(SurfaceCurvature.class);

    SurfaceCurvatureNumPts surfaceCurvatureNumPts_0 = surfaceCurvature_0.getSurfaceCurvatureNumPts();

    // Curvature refinement specified as number of points around a circle
    surfaceCurvatureNumPts_0.setNumPointsAroundCircle(myCurvature);

    SurfaceGrowthRate surfaceGrowthRate_0 = meshContinuum_0.getReferenceValues().get(SurfaceGrowthRate.class);

    // Surface growth rate (ratio of triangle sizes)
    surfaceGrowthRate_0.setGrowthRate(mySurfaceGrowthRate);

    SurfaceSize surfaceSize_0 = meshContinuum_0.getReferenceValues().get(SurfaceSize.class);

    RelativeMinimumSize relativeMinimumSize_0 = surfaceSize_0.getRelativeMinimumSize();

    // Set triangle minimum size percentage
    relativeMinimumSize_0.setPercentage(myMinMesh_pct);

    SimpleTemplateGrowthRate simpleTemplateGrowthRate_0 = meshContinuum_0.getReferenceValues().get(SimpleTemplateGrowthRate.class);

    // Set volume mesh growth rate for field (FAST, MEDIUM, SLOW, VERYSLOW)
    simpleTemplateGrowthRate_0.getGrowthRateOption().setSelected(GrowthRateOption.SLOW);

    // Set nearfield mesh growth rate for field (FAST, MEDIUM, SLOW, VERYSLOW)
    simpleTemplateGrowthRate_0.getSurfaceGrowthRateOption().setSelected(SurfaceGrowthRateOption.VERYSLOW);

    // Remove existing feature curves (will remark feature curves below)
    region_0.getFeatureCurveManager().removeObjects(featureCurve_1);

    MeshPipelineController meshPipelineController_0 =  simulation_0.get(MeshPipelineController.class);

    meshPipelineController_0.initializeMeshPipeline();

    SurfaceRep surfaceRep_0 = ((SurfaceRep) simulation_0.getRepresentationManager().getObject("Initial Surface"));

    Boundary boundary_1 = region_0.getBoundaryManager().getBoundary("combined.fuselage");
    Boundary boundary_2 = region_0.getBoundaryManager().getBoundary("combined.tail");
    Boundary boundary_3 = region_0.getBoundaryManager().getBoundary("combined.wing");
    Boundary boundary_4 = region_0.getBoundaryManager().getBoundary("Sphere.Sphere Surface");
    boundary_4.setBoundaryType(FreeStreamBoundary.class);

    // Identify feature curves using angle criteria (currently set at 17 degrees for the ATR model)
    FeatureCurve featureCurve_2 = surfaceRep_0.createFeatureEdgesOnBoundaries(new NeoObjectVector(new Object[] {boundary_1, boundary_2, boundary_3, boundary_4}), true, true, true, true, true, true, myFeatureAngle_deg, false);

    SurfaceSizeOption surfaceSizeOption_0 = featureCurve_2.get(MeshConditionManager.class).get(SurfaceSizeOption.class);

    surfaceSizeOption_0.setSurfaceSizeOption(true);

    SurfaceSize surfaceSize_1 = featureCurve_2.get(MeshValueManager.class).get(SurfaceSize.class);

    RelativeMinimumSize relativeMinimumSize_1 = surfaceSize_1.getRelativeMinimumSize();

    // Set feature curve minimum size (usually the same as surface triangle minimum size)
    relativeMinimumSize_1.setPercentage(myMinMesh_pct);

    RelativeTargetSize relativeTargetSize_0 = surfaceSize_1.getRelativeTargetSize();

    // Set feature curve target size as a percentage
    relativeTargetSize_0.setPercentage(myEdgeTarget_pct);

    SurfaceSizeOption surfaceSizeOption_1 = boundary_4.get(MeshConditionManager.class).get(SurfaceSizeOption.class);

    surfaceSizeOption_1.setSurfaceSizeOption(true);

    SurfaceSize surfaceSize_2 = boundary_4.get(MeshValueManager.class).get(SurfaceSize.class);

    surfaceSize_2.getRelativeOrAbsoluteOption().setSelected(RelativeOrAbsoluteOption.ABSOLUTE);

    AbsoluteMinimumSize absoluteMinimumSize_0 = surfaceSize_2.getAbsoluteMinimumSize();

    // Set minimum triangle size for freestream boundary (in feet)
    absoluteMinimumSize_0.getValue().setValue(mySphereTriangles_ft);

    AbsoluteTargetSize absoluteTargetSize_0 = surfaceSize_2.getAbsoluteTargetSize();

    // Set target triangle size for freestream boundary in feet
    absoluteTargetSize_0.getValue().setValue(mySphereTriangles_ft);

    // Make surface mesh
    if ( bln_makeSurfaceMesh ) meshPipelineController_0.generateSurfaceMesh();

    // Make volume mesh
    if ( bln_makeVolumeMesh ) meshPipelineController_0.generateVolumeMesh();

    // Save .sim file
    if ( bln_saveMeshFile ) simulation_0.saveState(resolvePath(myOutputMeshFilename));


  }
}
""")
        fout.close()





    def write_flow_java(self):
        """
        Create STAR macro javaBatch2File.java file for CFD run.
        On command line:
        starccm+ -power -licpath 2999@localhost -podkey <LICENSE KEY> -rsh "ssh -o stricthostkeychecking=no" -on ... -server javaBatch2File.java
        """
        fout = open(self.javaBatch2File+".java","w")
        fout.write("""\
// STAR-CCM+ macro: ATR_CoupledFlow.java
// Written by STAR-CCM+ 9.06.011
package macro;

import java.util.*;
import star.turbulence.*;
import star.kwturb.*;
import star.material.*;
import star.common.*;
import star.base.neo.*;
import star.coupledflow.*;
import star.vis.*;
import star.base.report.*;
import star.flow.*;
import star.energy.*;

public class %s extends StarMacro {

  public void execute() {
    execute0();
  }
  """ % self.javaBatch2File)

        fout.write("""\

  private void execute0() {
    String myPath = "$PWD";  // Directory for output files and final sim file (if saved)
    String myOutputSimFilename = "%s";
    // Flowfield inputs (freestream static pressure in psi, Temperature in Rankine)

    double myMach          = %f;
    double myAoA_deg       = %f;
    double myBeta_deg      = %f;
    double myPressure_psi  = %f;
    double myTemperature_R = %f;
    """ % (self.simFlowFile, self.myMach, self.myAoA, self.myBeta, self.myPressure, self.myTemperature))

        fout.write("""\

    double myMRCx_ft = %f;       // Moment Reference Location in feet
    double myMRCy_ft = %f;
    double myMRCz_ft = %f;
    """ % (self.myMoment_X, self.myMoment_Y, self.myMoment_Z))

        fout.write("""\

    double myCFL = %f;           // final CFL number
    int myIterations = %d;        // number of flow solver iterations
    double myCFLRampStart = %f;  // starting CFL number
    int myCFLRampIters = %d;      // number of iterations over which to ramp CFL up to final value
    boolean bln_useExpert = %s; // use expert driver for solver control
    int myColorLevels = %d;       // number of colorbar levels in key
    """ % (self.myCFL, self.myIteration, self.myCFLRampStart, self.myCFLRampIters, str(self.useExpert).lower(), self.myColorLevels))

        fout.write("""\

    // boolean bln_cellRemedy = True;     // use cell quality remediation model - NOT USED
    boolean bln_saveSimFile = %s;         // use true to save final sim file, false to skip
    boolean bln_saveResidualsImage = %s;          // use true to save residuals image, false to skip
    boolean bln_saveFandMImages = %s;     // use true to save final F&M images, false to skip
    boolean bln_saveCpImages = %s;        // use true to save final Cp images, false to skip
    boolean bln_saveResidualCSV = %s;    // use true to save residuals history, false to skip
    boolean bln_saveFandMCSV = %s;        // use true to save final F&M histories, false to skip
    """ % (str(self.saveSimFile).lower(), str(self.saveResidualImage).lower(), str(self.saveFandMImages).lower(), str(self.saveCpImages).lower(), str(self.saveResidualCSV).lower(), str(self.saveFandMCSV).lower()))

        fout.write("""\

    // Gas constants
    double myRgas = 1716.5;  // Universal gas constant in English units
    double myGamma = 1.4;    // Ratio of specific heats

    double myDensity_lbft3;
    double myVelocity_fps;
    double myUvel_fps;
    double myVvel_fps;
    double myWvel_fps;
    double myAoA_rad;
    double myBeta_rad;
    """)

        fout.write("""\

    myVelocity_fps = myMach * Math.sqrt(myGamma * myRgas * myTemperature_R); // Compute freestream velocity in fps
    myDensity_lbft3 = 144 * myPressure_psi / ( myRgas * myTemperature_R ) * 32.1740486; // Compute freestream density in lb/ft3
    myAoA_rad  = Math.toRadians(myAoA_deg);  // Convert to radians
    myBeta_rad = Math.toRadians(myBeta_deg); // Convert to radians

    // Compute freestream velocity vector (assumes body x out tail, body y out right wing, body z up)
    myUvel_fps =  myVelocity_fps * Math.cos(myAoA_rad) * Math.cos(myBeta_rad);
    myVvel_fps = -myVelocity_fps * Math.sin(myBeta_rad);
    myWvel_fps =  myVelocity_fps * Math.sin(myAoA_rad) * Math.cos(myBeta_rad);

    // STAR-CCM+ commands start here
    Simulation simulation_0 = getActiveSimulation();
    PhysicsContinuum physicsContinuum_0 = ((PhysicsContinuum) simulation_0.getContinuumManager().getContinuum("Physics 1"));
    physicsContinuum_0.enable(SteadyModel.class);
    physicsContinuum_0.enable(SingleComponentGasModel.class);
    physicsContinuum_0.enable(CoupledFlowModel.class);
    physicsContinuum_0.enable(IdealGasModel.class);
    physicsContinuum_0.enable(CoupledEnergyModel.class);
    physicsContinuum_0.enable(TurbulentModel.class);
    physicsContinuum_0.enable(RansTurbulenceModel.class);
    physicsContinuum_0.enable(KOmegaTurbulence.class);
    physicsContinuum_0.enable(SstKwTurbModel.class);
    physicsContinuum_0.enable(KwAllYplusWallTreatment.class);

    // if ( bln_cellRemedy ) physicsContinuum_0.enable(CellQualityRemediationModel.class);
    CoupledFlowModel coupledFlowModel_0 = physicsContinuum_0.getModelManager().getModel(CoupledFlowModel.class);
    // coupledFlowModel_0.getCoupledInviscidFluxOption().setSelected(CoupledInviscidFluxOption.AUSM_SCHEME);
    coupledFlowModel_0.getCoupledInviscidFluxOption().setSelected(CoupledInviscidFluxOption.ROE_SCHEME);

    SingleComponentGasModel singleComponentGasModel_0 = physicsContinuum_0.getModelManager().getModel(SingleComponentGasModel.class);
    Gas gas_0 = ((Gas) singleComponentGasModel_0.getMaterial());
    gas_0.getMaterialProperties().getMaterialProperty(DynamicViscosityProperty.class).setMethod(SutherlandLaw.class);

    simulation_0.getUnitsManager().getSystemOption().setSelected(UnitsManagerSystemOption.SYSTEM_USCS);
    ForceReport forceReport_0 = simulation_0.getReportManager().createReport(ForceReport.class);
    forceReport_0.setPresentationName("Force X");
    forceReport_0.getDirection().setComponents(1.0, 0.0, 0.0);

    Region region_0 = simulation_0.getRegionManager().getRegion("Region");
    Boundary boundary_0 = region_0.getBoundaryManager().getBoundary("combined.fuselage");
    Boundary boundary_1 = region_0.getBoundaryManager().getBoundary("combined.tail");
    Boundary boundary_2 = region_0.getBoundaryManager().getBoundary("combined.wing");

    forceReport_0.getParts().setObjects(boundary_0, boundary_1, boundary_2);

    ForceReport forceReport_1 = simulation_0.getReportManager().createReport(ForceReport.class);
    forceReport_1.setPresentationName("Force Y");
    forceReport_1.getDirection().setComponents(0.0, 1.0, 0.0);
    forceReport_1.getParts().setObjects(boundary_0, boundary_1, boundary_2);

    ForceReport forceReport_2 = simulation_0.getReportManager().createReport(ForceReport.class);
    forceReport_2.setPresentationName("Force Z");
    forceReport_2.getParts().setObjects(boundary_0, boundary_1, boundary_2);
    forceReport_2.getDirection().setComponents(0.0, 0.0, 1.0);

    MomentReport momentReport_0 = simulation_0.getReportManager().createReport(MomentReport.class);
    momentReport_0.setPresentationName("Moment X");
    momentReport_0.getDirection().setComponents(1.0, 0.0, 0.0);
    momentReport_0.getOrigin().setComponents(myMRCx_ft, myMRCy_ft, myMRCz_ft);
    momentReport_0.getParts().setObjects(boundary_0, boundary_1, boundary_2);

    MomentReport momentReport_1 = simulation_0.getReportManager().createReport(MomentReport.class);
    momentReport_1.setPresentationName("Moment Y");
    momentReport_1.getDirection().setComponents(0.0, 1.0, 0.0);
    momentReport_1.getOrigin().setComponents(myMRCx_ft, myMRCy_ft, myMRCz_ft);
    momentReport_1.getParts().setObjects(boundary_0, boundary_1, boundary_2);

    MomentReport momentReport_2 = simulation_0.getReportManager().createReport(MomentReport.class);
    momentReport_2.setPresentationName("Moment Z");
    momentReport_2.getDirection().setComponents(0.0, 0.0, 1.0);
    momentReport_2.getOrigin().setComponents(myMRCx_ft, myMRCy_ft, myMRCz_ft);
    momentReport_2.getParts().setObjects(boundary_0, boundary_1, boundary_2);

    simulation_0.getMonitorManager().createMonitorAndPlot(new NeoObjectVector(new Object[] {forceReport_0}), true, "%1$s Plot");
    ReportMonitor reportMonitor_0 = ((ReportMonitor) simulation_0.getMonitorManager().getMonitor("Force X Monitor"));
    MonitorPlot monitorPlot_0 = simulation_0.getPlotManager().createMonitorPlot(new NeoObjectVector(new Object[] {reportMonitor_0}), "Force X Monitor Plot");

    simulation_0.getMonitorManager().createMonitorAndPlot(new NeoObjectVector(new Object[] {forceReport_1}), true, "%1$s Plot");
    ReportMonitor reportMonitor_1 = ((ReportMonitor) simulation_0.getMonitorManager().getMonitor("Force Y Monitor"));
    MonitorPlot monitorPlot_1 = simulation_0.getPlotManager().createMonitorPlot(new NeoObjectVector(new Object[] {reportMonitor_1}), "Force Y Monitor Plot");

    simulation_0.getMonitorManager().createMonitorAndPlot(new NeoObjectVector(new Object[] {forceReport_2}), true, "%1$s Plot");
    ReportMonitor reportMonitor_2 = ((ReportMonitor) simulation_0.getMonitorManager().getMonitor("Force Z Monitor"));
    MonitorPlot monitorPlot_2 = simulation_0.getPlotManager().createMonitorPlot(new NeoObjectVector(new Object[] {reportMonitor_2}), "Force Z Monitor Plot");

    simulation_0.getMonitorManager().createMonitorAndPlot(new NeoObjectVector(new Object[] {momentReport_0}), true, "%1$s Plot");
    ReportMonitor reportMonitor_3 = ((ReportMonitor) simulation_0.getMonitorManager().getMonitor("Moment X Monitor"));
    MonitorPlot monitorPlot_3 = simulation_0.getPlotManager().createMonitorPlot(new NeoObjectVector(new Object[] {reportMonitor_3}), "Moment X Monitor Plot");

    simulation_0.getMonitorManager().createMonitorAndPlot(new NeoObjectVector(new Object[] {momentReport_1}), true, "%1$s Plot");
    ReportMonitor reportMonitor_4 = ((ReportMonitor) simulation_0.getMonitorManager().getMonitor("Moment Y Monitor"));
    MonitorPlot monitorPlot_4 = simulation_0.getPlotManager().createMonitorPlot(new NeoObjectVector(new Object[] {reportMonitor_4}), "Moment Y Monitor Plot");

    simulation_0.getMonitorManager().createMonitorAndPlot(new NeoObjectVector(new Object[] {momentReport_2}), true, "%1$s Plot");
    ReportMonitor reportMonitor_5 = ((ReportMonitor) simulation_0.getMonitorManager().getMonitor("Moment Z Monitor"));
    MonitorPlot monitorPlot_5 = simulation_0.getPlotManager().createMonitorPlot(new NeoObjectVector(new Object[] {reportMonitor_5}), "Moment Z Monitor Plot");

    UserFieldFunction userFieldFunction_0 = simulation_0.getFieldFunctionManager().createFieldFunction();

    userFieldFunction_0.getTypeOption().setSelected(FieldFunctionTypeOption.SCALAR);
    userFieldFunction_0.setPresentationName("Dynamic Pressure");
    userFieldFunction_0.setFunctionName("Dynamic Pressure");

    Units units_0 = simulation_0.getUnitsManager().getPreferredUnits(new IntVector(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));
    Units units_1 = simulation_0.getUnitsManager().getPreferredUnits(new IntVector(new int[] {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0}));
    Units units_2 = simulation_0.getUnitsManager().getPreferredUnits(new IntVector(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

    userFieldFunction_0.setDefinition("0.5*$Density*mag2($$Velocity)");
    userFieldFunction_0.setDimensionsVector(new IntVector(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

    PressureCoefficientFunction pressureCoefficientFunction_0 = ((PressureCoefficientFunction) simulation_0.getFieldFunctionManager().getFunction("PressureCoefficient"));
    pressureCoefficientFunction_0.getReferenceDensity().setUnits(units_1);
    pressureCoefficientFunction_0.getReferenceDensity().setValue(myDensity_lbft3);
    pressureCoefficientFunction_0.getReferenceVelocity().setValue(myVelocity_fps);

    Units units_4 = ((Units) simulation_0.getUnitsManager().getObject("psi"));
    physicsContinuum_0.getReferenceValues().get(ReferencePressure.class).setUnits(units_4);
    physicsContinuum_0.getReferenceValues().get(ReferencePressure.class).setValue(myPressure_psi);

    InitialPressureProfile initialPressureProfile_0 = physicsContinuum_0.getInitialConditions().get(InitialPressureProfile.class);
    initialPressureProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setUnits(units_4);
    initialPressureProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(0.0);

    StaticTemperatureProfile staticTemperatureProfile_0 = physicsContinuum_0.getInitialConditions().get(StaticTemperatureProfile.class);
    staticTemperatureProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(myTemperature_R);

    VelocityProfile velocityProfile_0 = physicsContinuum_0.getInitialConditions().get(VelocityProfile.class);
    velocityProfile_0.getMethod(ConstantVectorProfileMethod.class).getQuantity().setComponents(myUvel_fps, myVvel_fps, myWvel_fps);

    Boundary boundary_3 = region_0.getBoundaryManager().getBoundary("Sphere.Sphere Surface");
    MachNumberProfile machNumberProfile_0 = boundary_3.getValues().get(MachNumberProfile.class);
    machNumberProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(myMach);
    FlowDirectionProfile flowDirectionProfile_0 = boundary_3.getValues().get(FlowDirectionProfile.class);
    flowDirectionProfile_0.getMethod(ConstantVectorProfileMethod.class).getQuantity().setComponents(myUvel_fps, myVvel_fps, myWvel_fps);
    StaticTemperatureProfile staticTemperatureProfile_1 = boundary_3.getValues().get(StaticTemperatureProfile.class);
    staticTemperatureProfile_1.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(myTemperature_R);

    CoupledImplicitSolver coupledImplicitSolver_0 = ((CoupledImplicitSolver) simulation_0.getSolverManager().getSolver(CoupledImplicitSolver.class));
    coupledImplicitSolver_0.getExpertInitManager().getExpertInitOption().setSelected(ExpertInitOption.GRID_SEQ_METHOD);
    coupledImplicitSolver_0.getRampCalculatorManager().getRampCalculatorOption().setSelected(RampCalculatorOption.LINEAR_RAMP);

    LinearRampCalculator linearRampCalculator_0 = ((LinearRampCalculator) coupledImplicitSolver_0.getRampCalculatorManager().getCalculator());
    linearRampCalculator_0.setEndIteration(myCFLRampIters);
    linearRampCalculator_0.setInitialRampValue(myCFLRampStart);

    if ( bln_useExpert ) coupledImplicitSolver_0.getSolutionDriverManager().getExpertDriverOption().setSelected(ExpertDriverOption.EXPERT_DRIVER);
    coupledImplicitSolver_0.setCFL(myCFL);

    StepStoppingCriterion stepStoppingCriterion_0 = ((StepStoppingCriterion) simulation_0.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps"));
    stepStoppingCriterion_0.setMaximumNumberSteps(myIterations);

    ResidualPlot residualPlot_0 = ((ResidualPlot) simulation_0.getPlotManager().getPlot("Residuals"));
    residualPlot_0.open();

    simulation_0.getSimulationIterator().run();

    if ( bln_saveFandMCSV ) {
       monitorPlot_0.export(resolvePath(myPath+"ForceX.csv"), ",");
       monitorPlot_1.export(resolvePath(myPath+"ForceY.csv"), ",");
       monitorPlot_2.export(resolvePath(myPath+"ForceZ.csv"), ",");
       monitorPlot_3.export(resolvePath(myPath+"MomentX.csv"), ",");
       monitorPlot_4.export(resolvePath(myPath+"MomentY.csv"), ",");
       monitorPlot_5.export(resolvePath(myPath+"MomentZ.csv"), ",");  }

    if ( bln_saveResidualCSV ) residualPlot_0.export(resolvePath(myPath+"Residuals.csv"), ",");

    if ( bln_saveFandMImages ) {
       monitorPlot_0.encode(resolvePath(myPath+"ForceX.png"), "png", 1280, 1024);
       monitorPlot_1.encode(resolvePath(myPath+"ForceY.png"), "png", 1280, 1024);
       monitorPlot_2.encode(resolvePath(myPath+"ForceZ.png"), "png", 1280, 1024);
       monitorPlot_3.encode(resolvePath(myPath+"MomentX.png"), "png", 1280, 1024);
       monitorPlot_4.encode(resolvePath(myPath+"MomentY.png"), "png", 1280, 1024);
       monitorPlot_5.encode(resolvePath(myPath+"MomentZ.png"), "png", 1280, 1024);  }

    if ( bln_saveResidualsImage ) residualPlot_0.encode(resolvePath(myPath+"Residuals.png"), "png", 1280, 1024);

    simulation_0.getSceneManager().createEmptyScene("Scene");
    Scene scene_0 = simulation_0.getSceneManager().getScene("Scene 1");
    scene_0.initializeAndWait();
    scene_0.open(true);
    ScalarDisplayer scalarDisplayer_0 = scene_0.getDisplayerManager().createScalarDisplayer("Scalar");
    scalarDisplayer_0.initialize();
    scalarDisplayer_0.setFillMode(1);
    scalarDisplayer_0.getParts().setObjects(boundary_0, boundary_1, boundary_2);
    CurrentView currentView_0 = scene_0.getCurrentView();
    currentView_0.setInput(new DoubleVector(new double[] {9.472914132185263, -1.9526280616434708, -0.4363019644952004}), new DoubleVector(new double[] {9.472914132185263, -1.9526280616434708, 60.19461844566105}), new DoubleVector(new double[] {0.0, 1.0, 0.0}), 15.827846343869405, 1);
    scalarDisplayer_0.getScalarDisplayQuantity().setFieldFunction(pressureCoefficientFunction_0);
    scene_0.setBackgroundColorMode(0);
    FixedAspectAnnotationProp fixedAspectAnnotationProp_0 = ((FixedAspectAnnotationProp) scene_0.getAnnotationPropManager().getAnnotationProp("Logo"));
    scene_0.getAnnotationPropManager().remove(fixedAspectAnnotationProp_0);
    Legend legend_0 = scalarDisplayer_0.getLegend();
    legend_0.setLevels(myColorLevels);  // number of colorbar levels

    if ( bln_saveCpImages ) {
       scene_0.printAndWait(resolvePath(myPath+"UpperCp.png"), 1, 1280, 1024);
       currentView_0.setInput(new DoubleVector(new double[] {8.765561494829376, 1.816196184733111, -1.0850064933037444}), new DoubleVector(new double[] {8.765561494829374, 1.8161961847331114, -62.914352196428744}), new DoubleVector(new double[] {0.0, -1.0, 0.0}), 16.14069812417223, 1);
       scene_0.printAndWait(resolvePath(myPath+"LowerCp.png"), 1, 1280, 1024);  }

    if ( bln_saveSimFile ) simulation_0.saveState(resolvePath(myOutputSimFilename)); // Save the simulation file
  }
}
""")
        fout.close()




    def write_mesh_csh(self):
        """
        Create the C-shell file to generate volume mesh.
        On command line:
        cshBatch1File.csh -powerOnDemand javaBatch1File.java
        """
        str = self.LicLocalPort
        fout = open(self.cshBatch1File+".csh","w")
        fout.write("""\
#!/bin/csh

if ( $#argv == 0 ) then
     echo ""
     echo "USAGE: $0 [-powerOnDemand] javaBatchFile.java"
     echo ""
     exit
endif

set powerOnDemand=0
set javaBatchFile=$1
set powerOnDemandLicense=""
if ( "$1" == "-powerOnDemand" ) then
  set powerOnDemand=1
  set javaBatchFile=$2
  set powerOnDemandLicense="-licpath %s@localhost -podkey %s"
endif
""" % (str,self.starccmLic))

        fout.write("""\

alias echo "/bin/echo -e"
echo "\\n#=============================================="
echo "# Begin Star Meshing"
echo "# Java Batch File = $javaBatchFile"
if ( $powerOnDemand == 1 ) echo "# Using Power on Demand license."
set starttime = `date`
echo "# Start Time = ${starttime}\\n"

if ( $powerOnDemand == 1 ) then
  echo "\\n# Running 'killall ssh' to clear out all prior tunnels."
  killall ssh
  echo "\\n# Making a tunnel for the Power on Demand License."
  ssh -f -L %s:flex.cd-adapco.com:1999 -L 2099:flex.cd-adapco.com:2099 -N %s
  echo "\\n# Checking to see if there is a valid port tunnel in place for the Power on Demand License."
  ps -ef | grep '%s:flex.cd-adapco.com:1999'
endif
""" % (str,self.LicAccessName,str))

        fout.write("""\

setenv CDLMD_LICENSE_FILE %s
unsetenv LM_LICENSE_FILE

set EXEC = "%s"

$EXEC -power ${powerOnDemandLicense} \\
     -batch $javaBatchFile
set endtime = `date`
echo "#   End Time = ${endtime}"
echo "# Start Time = ${starttime}\\n"
echo "# End Star Simulation\\n"
""" % (self.CDLMD_LicFile, self.starccmExec))

        fout.close()



    def write_flow_csh(self):
        """
        Create the C-shell file to run CFD.
        On command line:
        cshBatch1File.csh -powerOnDemand javaBatch2File.java simMeshFile
        """
        str = self.LicLocalPort
        fout = open(self.cshBatch2File+".csh","w")
        fout.write("""\
#!/bin/csh

if ( $#argv == 0 ) then
     echo ""
     echo "USAGE: $0 [-powerOnDemand] javaBatchFile.java Simulation.sim"
     echo ""
     exit
endif

set powerOnDemand=0
set javaBatchFile=$1
set simFile=$2
set powerOnDemandLicense=""
if ( "$1" == "-powerOnDemand" ) then
  set powerOnDemand=1
  set javaBatchFile=$2
  set simFile=$3
  set powerOnDemandLicense="-licpath %s@localhost -podkey %s"
endif
""" % (str,self.starccmLic))

        fout.write("""\

alias echo "/bin/echo -e"
echo "\\n#=============================================="
echo "# Begin Star Simulation"
echo "# Java Batch File = $javaBatchFile"
echo "# sim File = $simFile"
if ( $powerOnDemand == 1 ) echo "# Using Power on Demand license."
set starttime = `date`
echo "# Start Time = ${starttime}\\n"

if ( $powerOnDemand == 1 ) then
  echo "\\n# Running 'killall ssh' to clear out all prior tunnels."
  killall ssh
  echo "\\n# Making a tunnel for the Power on Demand License."
  ssh -f -L %s:flex.cd-adapco.com:1999 -L 2099:flex.cd-adapco.com:2099 -N %s
  echo "\\n# Checking to see if there is a valid port tunnel in place for the Power on Demand License."
  ps -ef | grep '%s:flex.cd-adapco.com:1999'
endif
""" % (str,self.LicAccessName,str))

        fout.write("""\

setenv CDLMD_LICENSE_FILE %s
unsetenv LM_LICENSE_FILE

set lnodes=`cat $PBS_NODEFILE`
set llnodes = `echo $lnodes | sed 's/ /,/g'`
#echo "llnodes = $llnodes"
set numCores = `echo $llnodes | sed 's/,/ /g' | wc -w`

set EXEC = "%s"

$EXEC -power ${powerOnDemandLicense} \\
     -on $llnodes \\
     -rsh 'ssh -o stricthostkeychecking=no' \\
     -classpath ~/bin \\
     -load \\
     -batch $javaBatchFile \\
      $simFile
set endtime = `date`
echo "#   End Time = ${endtime}"
echo "# Start Time = ${starttime}\\n"
echo "# End Star Simulation\\n"
""" % (self.CDLMD_LicFile, self.starccmExec))

        fout.close()



    def execute(self):
        """ do your calculations here """
        if self.runVolGrid == 1:
            if self.cshUserBatch1 == 0:
                self.write_mesh_csh()
        if self.runVolGrid == 1:
            self.write_mesh_java()
        if self.runCFD == 1:
            if self.cshUserBatch2 == 0:
                self.write_flow_csh()
        self.write_pbs()
        if self.runCFD == 1:
            self.write_flow_java()
        super(StarCCM_wrapper, self).execute()

def main():
    sim = StarCCM_wrapper()
    sim.run()


if __name__ == '__main__':
    main()







