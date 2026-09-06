## Macro Structure

Every STAR-CCM+ macro follows this pattern:

```java
package macro;
import java.util.*;
import star.common.*;
// ... other imports

public class MacroName extends StarMacro {
  @Override
  public void execute() {
    execute0();  // typically: import + physics
    execute1();  // typically: regions + motion
    // ...
  }

  private void execute0() {
    Simulation simulation_0 = getActiveSimulation();
    // ... setup code ...
    simulation_0.saveState("C:\\path\\to\\file.sim", false);
  }
}
```

Key conventions:
- Every `executeN()` method gets `Simulation` via `getActiveSimulation()`
- Each sub-step typically ends with `simulation_0.saveState(...)`
- Chinese strings in recorded macros are Unicode-escaped (e.g., `旋转` = "旋转")
- Object names are localized in the UI language used during recording

---
