

package macro;

 

import java.util.*;

 

import star.common.*;

import star.base.neo.*;

import star.vis.*;



public class createLineProbes extends StarMacro {





  public void execute() {


 
    Simulation simulation_0 = 
      getActiveSimulation();
 
    Units units_0 = 
      simulation_0
      .getUnitsManager()
      .getPreferredUnits(
        new IntVector(
          new int[]
            {0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
          )
        );
 
    Scene scene_0 = 
      simulation_0
      .getSceneManager()
      .getScene("Scalar Scene 1");
 
    PartDisplayer partDisplayer_1 = 
      ((PartDisplayer) scene_0
      .getCreatorDisplayer());
 
    FvRepresentation fvRepresentation_0 = 
      ((FvRepresentation) simulation_0
      .getRepresentationManager()
      .getObject("Volume Mesh"));
 
    partDisplayer_1
    .setRepresentation(fvRepresentation_0);
 
    Region region_0 = 
      simulation_0
      .getRegionManager()
      .getRegion("Fluid");
 
    double dXDIST = 0.1;  
 
    double[] dExtents = 
      fvRepresentation_0
      .getRegionExtents(region_0);
 
 
    double dXMIN = dExtents[0];
    double dXMAX = dExtents[1];
 
    double dXLOC = dXMIN + dXDIST;
 

    while(dXLOC < dXMAX) {
      
      LinePart linePart_0 = 
        simulation_0
        .getPartManager()
        .createLinePart(
          new NeoObjectVector(
            new Object[] {region_0}
          ),
          new DoubleVector(
            new double[] {dXLOC, 0.0, 0.0}
          ),
          new DoubleVector(
            new double[] {dXLOC, 0.0762, 0.0}
          ),
          20
        );
 
      LabCoordinateSystem labCoordinateSystem_0 = 
        simulation_0
        .getCoordinateSystemManager()
        .getLabCoordinateSystem();
 
    linePart_0
      .setCoordinateSystem(labCoordinateSystem_0);
 
    Coordinate coordinate_0 = 
      linePart_0.getPoint1Coordinate();
 
    coordinate_0
    .setCoordinate(
       units_0,
       units_0,
       units_0,
       new DoubleVector(
         new double[] {dXLOC, 0.0, 0.0}
       )
    );
 
    Coordinate coordinate_1 = 
      linePart_0
      .getPoint2Coordinate();
 
    coordinate_1
    .setCoordinate(
      units_0,
      units_0,
      units_0,
      new DoubleVector(
        new double[] {dXLOC, 0.0762, 0.0}
      )
    );
 
      String strPartName =
        String.format("X = %6.4f",dXLOC);
 
      linePart_0.setPresentationName(strPartName);
 

      dXLOC = dXLOC + dXDIST;
    } 
 

 
    ScalarDisplayer scalarDisplayer_0 = 
      ((ScalarDisplayer)
        scene_0
        .getDisplayerManager()
        .getDisplayer("Scalar 1")
      );
 
    PartManager partManager_0 =
      simulation_0.getPartManager();
 
    Collection<Part> allDerivedParts =
      partManager_0.getObjects();
 
    scalarDisplayer_0
    .getParts()
    .setObjects(allDerivedParts);
 
  } 
} 
 









