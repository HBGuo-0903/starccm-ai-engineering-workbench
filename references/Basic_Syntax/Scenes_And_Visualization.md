## Scenes and Visualization

### Creating Scenes

```java
// Geometry scene
simulation.getSceneManager().createGeometryScene("几何场景", "轮廓", "表面", 1, null);

// Mesh scene
simulation.getSceneManager().createGeometryScene("网格场景", "轮廓", "网格", 3, null);

// Scalar scene
simulation.getSceneManager().createScalarScene("标量场景", "轮廓", "标量", null);

// Vector scene
simulation.getSceneManager().createVectorScene("矢量场景", "轮廓", "矢量", null);
```

### Configuring Displayers

```java
Scene scene = simulation.getSceneManager().getScene("scene_name");

// Scalar displayer
ScalarDisplayer scalarDisp =
  (ScalarDisplayer) scene.getDisplayerManager().getObject("标量 1");
scalarDisp.getInputParts().setObjects(parts);  // parts to display
scalarDisp.getScalarDisplayQuantity()
    .setFieldFunction(simulation.getFieldFunctionManager().getFunction("Pressure"));
scalarDisp.setFillMode(ScalarFillMode.NODE_FILLED);

// Vector displayer
VectorDisplayer vectorDisp =
  (VectorDisplayer) scene.getDisplayerManager().getObject("矢量 1");
vectorDisp.getInputParts().setObjects(planeSection);

// DFBI body displayer (for 6-DOF bodies)
DfbiDisplayer dfbiDisp =
  (DfbiDisplayer) scene.getDisplayerManager().createDfbiDisplayer("DFBI");
dfbiDisp.getInputParts().setObjects(continuumBody);
dfbiDisp.setOpacity(0.5);
```

### View/Camera Control

```java
scene.setViewOrientation(
    new DoubleVector(new double[] {0.0, -1.0, 0.0}),  // view direction
    new DoubleVector(new double[] {0.0, 0.0, 1.0})    // up vector
);

scene.getCurrentView().setInput(
    position,   // camera position
    lookAt,     // look-at point
    upVector,   // up vector
    viewAngle,  // field of view
    projectionMode,  // 0=perspective, 1=parallel
    nearPlane   // near clipping plane
);
```

### Animation Output

```java
SceneUpdate sceneUpdate = scene.getStarUpdateStageable().getSceneUpdate();
sceneUpdate.getUpdateModeOption().setSelected(StarUpdateModeOption.Type.TIMESTEP);
sceneUpdate.setAnimationFilePath("output_directory");
sceneUpdate.setAnimationFilenameBase("animation_name");
sceneUpdate.setSaveAnimation(true);
sceneUpdate.getHardcopyProperties().setUseAntiAliasing(true);
```

---
