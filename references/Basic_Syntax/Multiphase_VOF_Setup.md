## Multiphase (VOF) Setup

### Creating Phases

```java
PhysicsContinuum phys = simulation.getContinuumManager().getContinuum("Physics 1");
EulerianMultiPhaseModel multiphase =
  phys.getModelManager().getModel(EulerianMultiPhaseModel.class);

// Water phase
EulerianPhase water = multiphase.createPhase();
water.setPresentationName("water");
water.enable(SinglePhaseLiquidModel.class);
water.enable(ConstantDensityModel.class);

// Air phase
EulerianPhase air = multiphase.createPhase();
air.setPresentationName("air");
air.enable(SinglePhaseGasModel.class);
air.enable(ConstantDensityModel.class);
```

### Wave Setup

**Flat wave (calm water) — star4:**
```java
VofWaveModel waveModel = phys.getModelManager().getModel(VofWaveModel.class);
FlatVofWave flatWave =
  waveModel.getVofWaveManager().createVofWave(FlatVofWave.class, "静水 VOF 波");
flatWave.getPointOnLevel().setComponentsAndUnits(0.0, 0.0, -30.0, meters);
flatWave.setPresentationName("flat water");
```

**5th-order Stokes wave — star3:**
```java
FifthOrderVofWave wave =
  waveModel.getVofWaveManager().createVofWave(FifthOrderVofWave.class, "五阶 VOF 波");
wave.getWind().setComponentsAndUnits(2.5, 0.0, 0.0, mps);       // wind velocity
wave.getCurrent().setComponentsAndUnits(2.5, 0.0, 0.0, mps);     // current velocity
wave.getWaveHeight().setValueAndUnits(0.2, meters);              // wave height
wave.getWaterDepth().setValueAndUnits(10.0, meters);             // water depth
wave.getSpecificationOption().setSelected(WAVE_LENGTH_SPECIFIED);
wave.getVofWaveSpecification().getWaveLength().setValueAndUnits(6.0, meters);
```

### Wave Initial Conditions

All wave types require setting initial conditions to wave field functions:

```java
// Volume fraction
VolumeFractionProfile vfProfile = phys.getInitialConditions().get(VolumeFractionProfile.class);
vfProfile.setMethod(CompositeArrayProfileMethod.class);
// Profile[0] → VolumeFractionHeavyFluidWave0
// Profile[1] → VolumeFractionLightFluidWave0

// Velocity
VelocityProfile velProfile = phys.getInitialConditions().get(VelocityProfile.class);
velProfile.setMethod(FunctionVectorProfileMethod.class);
velProfile.getMethod(FunctionVectorProfileMethod.class)
    .setFieldFunction(simulation.getFieldFunctionManager().getFunction("VelocityWave0"));

// Pressure
InitialPressureProfile pProfile = phys.getInitialConditions().get(InitialPressureProfile.class);
pProfile.setMethod(FunctionScalarProfileMethod.class);
pProfile.getMethod(FunctionScalarProfileMethod.class)
    .setFieldFunction(simulation.getFieldFunctionManager().getFunction("HydrostaticPressureWave0"));
```

---
