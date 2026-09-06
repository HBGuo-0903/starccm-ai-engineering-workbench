## Motion Modeling

### Approach Comparison

| Approach | Java Class | When to Use | Macro Example |
|---|---|---|---|
| **MRF** (Moving Reference Frame) | `UserRotatingReferenceFrame` | Steady-state rotating machinery | star1 |
| **Sliding Mesh** | `RotatingMotion` | Transient rotating machinery, blade passing | star2 |
| **DFBI** (6-DOF Rigid Body) | `SixDofMotion` | Free body response to fluid forces | star3, star4 |
| **Overset Mesh** | `OversetMeshInterface` | Large-amplitude motion, complex body movement | star4 |

### MRF Setup (star1 pattern)

```java
// Create rotating reference frame
UserRotatingReferenceFrame rf =
  simulation.get(ReferenceFrameManager.class)
    .createReferenceFrame(UserRotatingReferenceFrame.class, "旋转");

Units rpm = simulation.getUnitsManager().getObject("rpm");
rf.getRotationRate().setValueAndUnits(2000.0, rpm);

// Assign to region
Region region = simulation.getRegionManager().getRegion("Rotating");
MotionSpecification motionSpec = region.getValues().get(MotionSpecification.class);
motionSpec.setReferenceFrame(rf);
```

### Sliding Mesh (star2 pattern)

```java
// Create rotating motion (mesh actually rotates)
RotatingMotion motion =
  simulation.get(MotionManager.class)
    .createMotion(RotatingMotion.class, "旋转");

RotationRate rate = (RotationRate) motion.getRotationSpecification();
rate.getRotationRate().setValueAndUnits(2000.0, rpm);

// Assign motion to region
MotionSpecification motionSpec = region.getValues().get(MotionSpecification.class);
motionSpec.setMotion(motion);
```

### DFBI Setup (star3/star4 pattern)

```java
// Create 6-DOF motion
SixDofMotion sixDof =
  simulation.get(MotionManager.class)
    .createMotion(SixDofMotion.class, "DFBI 旋转和平移");

// Assign to region
MotionSpecification motionSpec = region.getValues().get(MotionSpecification.class);
motionSpec.setMotion(sixDof);

// Create body
ContinuumBody body =
  simulation.get(BodyManager.class).createContinuumBody(true);

body.setPresentationName("boat");
body.getBodySurface().setObjects(boundaryHull);

// Body properties
BodyProperties props = body.getBodyProperties();
props.getMass().setValueAndUnits(10000.0, kg);          // mass
props.getReleaseTime().setValueAndUnits(0.5, seconds);  // hold before release

// Moment of inertia
MomentOfInertia moi = (MomentOfInertia) body.getInitialValueManager().getObject("惯性矩");
moi.getDiagonalComponents().setComponentsAndUnits(Ixx, Iyy, Izz, kgm2);
moi.setUseCenterOfMass(true);

// Center of mass
CenterOfMass com = (CenterOfMass) body.getInitialValueManager().getObject("质心");
com.getPosition().setCoordinate(m, m, m, new DoubleVector(new double[] {x, y, z}));

// Initial velocity
Velocity vel = (Velocity) body.getInitialValueManager().getObject("速度");
vel.getValue().setComponentsAndUnits(vx, vy, vz, mps);

// Release degrees of freedom
BodyFreeMotion freeMotion = (BodyFreeMotion) body.getMotionType();
freeMotion.setFreeTranslationX(true);   // surge
freeMotion.setFreeTranslationZ(true);   // heave
freeMotion.setFreeRotationY(true);      // pitch
```

### Overset Mesh Setup (star4 pattern)

Overset mesh requires TWO regions: a stationary background and a moving overset region.

```java
// Step 1: Set overset boundary type on the overset region
Boundary bnd = oversetRegion.getBoundaryManager().getBoundary("Boundaries");
OversetMeshBoundary omb =
  simulation.get(ConditionTypeManager.class).get(OversetMeshBoundary.class);
bnd.setBoundaryType(omb);

// Step 2: Create overset interface between the two regions
OversetMeshInterface omi =
  simulation.get(ConditionTypeManager.class).get(OversetMeshInterface.class);
simulation.getInterfaceManager()
  .createIndirectRegionInterface(backgroundRegion, oversetRegion, "重叠网格", omi);
```

---
