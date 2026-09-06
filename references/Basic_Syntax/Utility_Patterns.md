## Utility Patterns

### Unit Handling

```java
// Get by name
Units rpm = simulation.getUnitsManager().getObject("rpm");
Units mps = simulation.getUnitsManager().getObject("m/s");
Units meters = simulation.getUnitsManager().getObject("m");
Units seconds = simulation.getUnitsManager().getObject("s");
Units kg = simulation.getUnitsManager().getObject("kg");
Units kgm2 = simulation.getUnitsManager().getObject("kg-m^2");
Units dimensionless = simulation.getUnitsManager().getObject("");
Units m2ps3 = simulation.getUnitsManager().getObject("m^2/s^3");
Units Jpkg = simulation.getUnitsManager().getObject("J/kg");

// Get preferred units by dimension
Units prefLength = simulation.getUnitsManager()
    .getPreferredUnits(Dimensions.Builder().length(1).build());
Units prefAngle = simulation.getUnitsManager()
    .getPreferredUnits(Dimensions.Builder().angle(1).build());
```

### Coordinate Systems

```java
// Lab coordinate system
LabCoordinateSystem labCS = simulation.getCoordinateSystemManager().getLabCoordinateSystem();

// Create local coordinate system
CartesianCoordinateSystem localCS =
  labCS.getLocalCoordinateSystemManager()
      .createLocalCoordinateSystem(CartesianCoordinateSystem.class, "笛卡尔");
localCS.getOrigin().setValue(new DoubleVector(new double[] {-8.22, 0.0, -24.329}));
localCS.getXVector().setComponents(1.0, 0.0, 0.0);
localCS.getXyPlane().setComponents(0.0, 1.0, 0.0);
localCS.setPresentationName("my_coord_system");
```

### Plane Section (for vector plots)

```java
PlaneSection plane =
  (PlaneSection) simulation.getPartManager().createImplicitPart(
      new ArrayList<>(),                          // input parts
      new DoubleVector(new double[] {0.0, 0.0, 1.0}),  // normal
      new DoubleVector(new double[] {0.0, 0.0, 0.0}),  // origin
      0, 1,                                        // display mode, representation
      new DoubleVector(new double[] {0.0}),        // offset
      null);
plane.getInputParts().setObjects(region1, region2);
```

### Iso-Surface (for free surface)

```java
PrimitiveFieldFunction vofWater =
  simulation.getFieldFunctionManager().getFunction("VolumeFractionwater");

IsoPart isoPart =
  simulation.getPartManager().createIsoPart(
      new ArrayList<>(Arrays.asList(region)), vofWater, null);
isoPart.setMode(IsoMode.ISOVALUE_SINGLE);
isoPart.getSingleIsoValue().getValueQuantity().setValue(0.05);  // VOF=0.05
isoPart.setPresentationName("water surface");
```

---
