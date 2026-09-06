## Solver Configuration

### Steady-State Solver (star1)

```java
SegregatedFlowSolver flowSolver =
  (SegregatedFlowSolver) simulation.getSolverManager().getSolver(SegregatedFlowSolver.class);

// Velocity solver with linear ramp
VelocitySolver velSolver = flowSolver.getVelocitySolver();
velSolver.getRampCalculatorManager().getRampCalculatorOption()
    .setSelected(RampCalculatorOption.Type.LINEAR_RAMP);
LinearRampCalculator velRamp =
  (LinearRampCalculator) velSolver.getRampCalculatorManager().getCalculator();
velRamp.getEndIterationValue().getQuantity().setValue(200.0);
velRamp.getInitialRampValueQuantity().setValueAndUnits(0.5, dimensionless);

// Pressure solver with linear ramp
PressureSolver pSolver = flowSolver.getPressureSolver();
pSolver.getRampCalculatorManager().getRampCalculatorOption()
    .setSelected(RampCalculatorOption.Type.LINEAR_RAMP);
LinearRampCalculator pRamp =
  (LinearRampCalculator) pSolver.getRampCalculatorManager().getCalculator();
pRamp.getEndIterationValue().getQuantity().setValue(200.0);
pRamp.getInitialRampValueQuantity().setValueAndUnits(0.2, dimensionless);

// AMG solver tolerance
pSolver.getAMGLinearSolver().setConvergeTol(0.05);

// Stopping criterion
StepStoppingCriterion stopCriterion =
  (StepStoppingCriterion) simulation.getSolverStoppingCriterionManager()
      .getSolverStoppingCriterion("Maximum Steps");
stopCriterion.getMaximumNumberStepsObject().getQuantity().setValue(600.0);
```

### Transient Solver (star2/star3/star4)

```java
ImplicitUnsteadySolver unsteadySolver =
  (ImplicitUnsteadySolver) simulation.getSolverManager()
      .getSolver(ImplicitUnsteadySolver.class);

// Time step
unsteadySolver.getTimeStep().setValueAndUnits(0.0025, seconds);

// Time discretization (optional, default is 1st order)
unsteadySolver.getTimeDiscretizationOption()
    .setSelected(TimeDiscretizationOption.Type.SECOND_ORDER);

// Max inner iterations per time step
InnerIterationStoppingCriterion innerStop =
  (InnerIterationStoppingCriterion) simulation.getSolverStoppingCriterionManager()
      .getSolverStoppingCriterion("Maximum Inner Iterations");
innerStop.getMaxIterations().getQuantity().setValue(7.0);

// Max physical time
PhysicalTimeStoppingCriterion physTimeStop =
  (PhysicalTimeStoppingCriterion) simulation.getSolverStoppingCriterionManager()
      .getSolverStoppingCriterion("Maximum Physical Time");
physTimeStop.getMaximumTime().setValueAndUnits(2.0, seconds);

// Disable step-based stopping (use physical time instead)
StepStoppingCriterion stepStop =
  (StepStoppingCriterion) simulation.getSolverStoppingCriterionManager()
      .getSolverStoppingCriterion("Maximum Steps");
stepStop.setIsUsed(false);
```

### Transient Solver Parameter Guidelines

| Application | Typical Δt | Inner Iterations | Physical Reason |
|---|---|---|---|
| Rotating machinery (2000 rpm) | 8.33×10⁻⁵ s | 5-10 | ~1° rotation per step |
| Ship/boat motions | 0.01 s | 5-10 | Wave period resolution |
| Lifeboat water entry | 0.0025 s | 5-10 | High-speed impact |

---
