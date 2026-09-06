## Mesh Generation

### Importing Mesh

```java
// Import .ccm mesh file
simulation.getImportManager()
    .importMeshFiles(new StringVector(new String[] {
        resolvePath("path/to/mesh.ccm")
    }), NeoProperty.fromString("{'FileOptions': [{'Sequence': 57}]}"));

// Import CAD part
simulation.get(PartImportManager.class)
    .importCadPart2(resolvePath("path/to/geometry.x_b"),
        "SharpEdges", 30.0, 2, true, 1.0E-5, true,
        false, false, false, true, true, false, false, false,
        CadImportUtilities.SimplifyAssemblyNameRetentionOption.IMMEDIATE_ASSEMBLY_NAME);
```

### Creating Regions from Parts

```java
simulation.getRegionManager().newRegionsFromParts(
    new ArrayList<>(Arrays.asList(cadPart1, cadPart2)),
    "OneRegionPerPart", null,
    "OneBoundaryPerPartSurface", null,
    RegionManager.CreateInterfaceMode.NONE,
    "OneEdgeBoundaryPerPart", null);
```

### AutoMesh with Trimmer (star4 pattern)

```java
// Create auto mesh operation
AutoMeshOperation meshOp =
  simulation.get(MeshOperationManager.class)
      .createAutoMeshOperation(
          new StringVector(new String[] {
              "star.resurfacer.ResurfacerAutoMesher",
              "star.trimmer.TrimmerAutoMesher"
          }),
          new ArrayList<>(Arrays.asList(cadPart)));

meshOp.setPresentationName("background");

// Base size
meshOp.getDefaultValues().get(BaseSize.class).setValueAndUnits(5.0, meters);

// Growth rate
PartsSimpleTemplateGrowthRate growth =
  meshOp.getDefaultValues().get(PartsSimpleTemplateGrowthRate.class);
growth.getGrowthRateOption().setSelected(PartsGrowthRateOption.Type.SLOW);

// Volume refinement zone
VolumeCustomMeshControl volCtrl = meshOp.getCustomMeshControls().createVolumeControl();
volCtrl.setPresentationName("refinement_zone");
volCtrl.getGeometryObjects().setObjects(blockPart);
VolumeControlSize sizeCtrl = volCtrl.getCustomValues().get(VolumeControlSize.class);
sizeCtrl.getRelativeSizeScalar().setValueAndUnits(25.0, dimensionless);  // % of base

// Trimmer alignment
TrimmerAutoMesher trimmer =
  (TrimmerAutoMesher) meshOp.getMeshers().getObject("切割体网格单元生成器");
trimmer.setDoMeshAlignment(true);
meshOp.getDefaultValues().get(MeshAlignmentLocation.class)
    .getLocation().setComponentsAndUnits(-125.0, 0.0, 0.0, meters);

// Generate volume mesh
simulation.get(MeshPipelineController.class).generateVolumeMesh();
```

### Mesh Transformation

```java
// Translate mesh
simulation.getRepresentationManager().translateMesh(
    regions, new DoubleVector(new double[] {dx, dy, dz}), unitsList, coordSystem);

// Rotate mesh
simulation.getRepresentationManager().rotateMesh(
    regions, new DoubleVector(new double[] {0.0, 1.0, 0.0}),  // Y-axis
    unitsList, angleInRadians, coordSystem);
```

---
