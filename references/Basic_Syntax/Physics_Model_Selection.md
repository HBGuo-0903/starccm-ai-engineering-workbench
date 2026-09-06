## Physics Model Selection

### Enabling/Disabling Models

Models are enabled via `physicsContinuum_0.enable(ModelClass.class)` and disabled via `physicsContinuum_0.disable(...)`. Order matters — some models depend on others.

### Common Physics Model Combinations

**Steady-state, single-phase, incompressible, turbulent (star1):**
```java
physicsContinuum_0.enable(SteadyModel.class);
physicsContinuum_0.enable(ThreeDimensionalModel.class);
physicsContinuum_0.enable(SingleComponentGasModel.class);
physicsContinuum_0.enable(SegregatedFlowModel.class);
physicsContinuum_0.enable(ConstantDensityModel.class);
physicsContinuum_0.enable(TurbulentModel.class);
physicsContinuum_0.enable(RansTurbulenceModel.class);
physicsContinuum_0.enable(KEpsilonTurbulence.class);
physicsContinuum_0.enable(RkeTwoLayerTurbModel.class);
physicsContinuum_0.enable(KeTwoLayerAllYplusWallTreatment.class);
```

**Transient version (switch from steady):**
```java
physicsContinuum_0.disableModel(steadyModel);
physicsContinuum_0.enable(ImplicitUnsteadyModel.class);
```

**VOF multiphase, transient (star3, star4):**
```java
physicsContinuum_0.enable(ImplicitUnsteadyModel.class);
physicsContinuum_0.enable(EulerianMultiPhaseModel.class);
physicsContinuum_0.enable(SegregatedVofModel.class);
physicsContinuum_0.enable(SegregatedVolumeFluxBasedFlowModel.class);
physicsContinuum_0.enable(VofWaveModel.class);
physicsContinuum_0.enable(GravityModel.class);
// For turbulent VOF (star3):
physicsContinuum_0.enable(TurbulentModel.class);
physicsContinuum_0.enable(RansTurbulenceModel.class);
physicsContinuum_0.enable(RkeTwoLayerTurbModel.class);
// OR for laminar VOF (star4):
physicsContinuum_0.enable(LaminarModel.class);
```

### Physics Model Decision Tree

```
Simulation Type
├── Steady or Transient?
│   ├── Steady → SteadyModel
│   └── Transient → ImplicitUnsteadyModel
├── Flow regime?
│   ├── Incompressible → SegregatedFlow + ConstantDensity
│   └── Compressible → CoupledFlow + IdealGas
├── Turbulence?
│   ├── Yes → Turbulent + RansTurbulence + RkeTwoLayerTurb + KeTwoLayerAllYplusWallTreatment
│   └── No → LaminarModel
├── Multiphase?
│   ├── Single-phase gas → SingleComponentGasModel
│   └── VOF (water+air) → EulerianMultiPhase + SegregatedVof + SegregatedVolumeFluxBasedFlow
└── Special physics?
    ├── Waves → VofWaveModel + GravityModel
    ├── Heat transfer → CoupledEnergy
    └── Cavitation → CavitationModel
```

---
