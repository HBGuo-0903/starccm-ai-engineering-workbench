## Macro Writing Best Practices

### Before Recording/Editing

1. **Plan the physics first**: Decide on models before writing code.
2. **Use the GUI to prototype**: Record a macro for the basic structure, then clean it up.
3. **Know your regions and boundaries**: Names are case-sensitive and language-dependent.

### Cleaning Up Recorded Macros

Recording produces noise that should be stripped:
- Repeated `saveState` calls — keep only one at the end of each logical step
- Camera `setInput` calls with near-identical values — remove all, keep one `resetCamera`
- Duplicate `setQuery(null)` / `setObjects()` pairs — keep only the final one
- Toggling settings back and forth (e.g., transparency on/off) — keep only the final state
- Creating-then-deleting objects (like the 10 coordinate systems in star4 execute2) — keep only the final one used

### Macro Organization

Organize `executeN()` methods by logical phase:

```
execute0() → Import geometry
execute1() → Set physics models
execute2() → Create phases / motion
execute3() → Generate mesh
execute4() → Set boundary conditions and initial conditions
execute5() → Configure solvers
execute6() → Set up reports and monitors
execute7() → Create scenes and post-processing
execute8() → Initialize and run
execute9() → Post-process results
```

### Common Pitfalls

- **Wrong model order**: Some models must be enabled before others (e.g., `TurbulentModel` before `RansTurbulenceModel`)
- **Unit mismatch**: Always use the correct unit object; `""` means dimensionless
- **Regional assignment**: MRF uses `setReferenceFrame()`, sliding/DFBI uses `setMotion()`
- **Wave initial conditions**: Must set ALL of VOF, velocity, and pressure profiles to wave functions
- **Overset requires two separate mesh operations**: One for background, one for overset region
- **`execute2()` in star4 is empty**: Some phases may be empty — that's OK
