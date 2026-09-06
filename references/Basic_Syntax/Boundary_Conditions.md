## Boundary Conditions

### Setting Boundary Types

```java
Region region = simulation.getRegionManager().getRegion("Background");

Boundary inletBnd = region.getBoundaryManager().getBoundary("Inlet");
inletBnd.setBoundaryType(simulation.get(ConditionTypeManager.class).get(InletBoundary.class));

Boundary outletBnd = region.getBoundaryManager().getBoundary("Outlet");
outletBnd.setBoundaryType(simulation.get(ConditionTypeManager.class).get(PressureBoundary.class));

Boundary symBnd = region.getBoundaryManager().getBoundary("Symmetry");
symBnd.setBoundaryType(simulation.get(ConditionTypeManager.class).get(SymmetryBoundary.class));
```

### Setting Boundary Values

**Velocity inlet (constant):**
```java
VelocityMagnitudeProfile velProfile = boundary.getValues().get(VelocityMagnitudeProfile.class);
velProfile.getMethod(ConstantScalarProfileMethod.class)
    .getQuantity().setValueAndUnits(5.0, mps);
```

**Velocity inlet (wave function):**
```java
boundary.getConditions().get(InletVelocityOption.class)
    .setSelected(InletVelocityOption.Type.COMPONENTS);
VelocityProfile velProfile = boundary.getValues().get(VelocityProfile.class);
velProfile.setMethod(FunctionVectorProfileMethod.class);
velProfile.getMethod(FunctionVectorProfileMethod.class)
    .setFieldFunction(simulation.getFieldFunctionManager().getFunction("VelocityWave0"));
```

**Pressure outlet (hydrostatic wave):**
```java
StaticPressureProfile pProfile = boundary.getValues().get(StaticPressureProfile.class);
pProfile.setMethod(FunctionScalarProfileMethod.class);
pProfile.getMethod(FunctionScalarProfileMethod.class)
    .setFieldFunction(simulation.getFieldFunctionManager().getFunction("HydrostaticPressureWave0"));
```

**Volume fraction at boundary:**
```java
VolumeFractionProfile vfProfile = boundary.getValues().get(VolumeFractionProfile.class);
vfProfile.setMethod(ConstantArrayProfileMethod.class);
vfProfile.getMethod(ConstantArrayProfileMethod.class)
    .getQuantity().setArray(new DoubleVector(new double[] {1.0, 0.0}));  // pure water
// [1.0, 0.0] = pure water, [0.0, 1.0] = pure air
```

**Rotating wall (sliding):**
```java
boundary.getConditions().get(WallSlidingOption.class)
    .setSelected(WallSlidingOption.Type.ROTATION_RATE);
WallRelativeRotationProfile rotProfile = boundary.getValues().get(WallRelativeRotationProfile.class);
rotProfile.getMethod(ConstantScalarProfileMethod.class)
    .getQuantity().setValueAndUnits(2000.0, rpm);
```

---
