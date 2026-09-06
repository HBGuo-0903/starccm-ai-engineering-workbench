## Solution Execution

```java
// Initialize
Solution solution = simulation.getSolution();
solution.initializeSolution();
// Optionally clear previous solution:
solution.clearSolution();

// Run
simulation.getSimulationIterator().run();
// Multiple runs can be chained (typically recorded when user clicks "Run" multiple times)
```

---
