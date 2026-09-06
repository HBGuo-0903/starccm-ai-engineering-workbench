## Turbulence Initial Conditions

```java
// Set turbulence specification type
phys.getInitialConditions().get(KeTurbSpecOption.class)
    .setSelected(KeTurbSpecOption.Type.K_EPSILON);

// Turbulent kinetic energy
TurbulentKineticEnergyProfile kProfile =
  phys.getInitialConditions().get(TurbulentKineticEnergyProfile.class);
kProfile.getMethod(ConstantScalarProfileMethod.class)
    .getQuantity().setValueAndUnits(1.5, J_per_kg);

// Turbulent dissipation rate
TurbulentDissipationRateProfile epsProfile =
  phys.getInitialConditions().get(TurbulentDissipationRateProfile.class);
epsProfile.getMethod(ConstantScalarProfileMethod.class)
    .getQuantity().setValueAndUnits(150.0, m2_per_s3);
```

---
