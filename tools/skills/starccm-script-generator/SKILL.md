---
name: starccm-script-generator
description: Generate, revise, and validate STAR-CCM+ Java macros using the local references index, relevant reference code, and the official STAR-CCM+ Javadoc exposed through starccm-javadoc-mcp. Use for requests to create STAR-CCM+ scripts, macros, geometry/mesh/physics automation, reports, scenes, solver control, or API-grounded code. By default write generated runtime artifacts to a dated folder on the Windows Desktop.
---

# STAR-CCM+ Script Generator

Generate STAR-CCM+ Java code from verified local examples and the installed official API documentation. Treat generated code as a testable draft: API validity, intended behavior, physics correctness, and model-specific object names are separate checks.

## Required Context Order

1. Resolve the cloned repository root from the current project context and read `references/AGENTS.md`.
2. Read `references/STARCCM_References_Index.csv` before scanning reference directories. Resolve every `RelativePath` against `<repo-root>/references`; do not rely on a machine-specific absolute path stored in the CSV.
3. Map the request to the smallest relevant category and select candidate files from the index:
   - `Basic_Syntax`: small native API patterns and syntax.
   - `Standalone_STARCCM_Functions`: macros executed inside STAR-CCM+.
   - `External_STARCCM_Runners`: launchers, batch execution, schedulers, coupling, and complete external workflows.
   - `Shared_STARCCM_Libraries`: `MacroUtils` or `starClasses` dependencies.
4. Read the selected directory `AGENTS.md` and `README.md` when present, then read the entry macro and its direct dependencies. Do not read all repositories by default.
5. If the index is missing, unreadable, or stale, report that the `starccm-knowledge-maintainer` Skill must refresh it first. Do not call `generate-references-index.ps1` from this Skill.

## API Grounding

Use `starccm-javadoc-mcp` for every non-trivial STAR-CCM+ API call:

- Use `search_api` to locate the exact class, interface, method, or field.
- Use `get_doc` to read the official page and method signatures.
- Confirm the installed STAR-CCM+ version and Javadoc root before choosing version-sensitive APIs.
- Preserve exact package names, method signatures, argument types, return types, and deprecation status.
- When the MCP server is unavailable, inspect the local Javadoc or state that API verification is blocked; do not invent a replacement call from memory.
- Use local reference code to understand object names and workflow intent, but use official Javadoc as the authority for API existence and signatures.

The local MCP project is:

`tools/starccm-javadoc-mcp`

The official Javadoc location must be supplied by the user or the `STARCCM_DOC` environment variable. Do not assume a Siemens installation path from another machine. The documentation root must contain `index.html`, `type-search-index.js`, and `member-search-index.js`.

## Generation Rules

- Identify whether the request is an internal macro, an external runner, or a reusable library before writing code.
- Prefer the smallest complete macro that satisfies the requested function.
- Use `StarMacro` and `execute()` for an internal STAR-CCM+ macro unless the requested project structure requires another entry point.
- Reuse `MacroUtils` only when it materially reduces repeated object lookup/configuration; use direct native API for simple demonstrations.
- Reuse `starClasses` only when the selected case actually depends on it; do not present case-specific helpers as universal APIs.
- Preserve the user's model-tree names, input paths, units, and requested execution order. Mark assumptions explicitly.
- Keep external coupling, cluster, Abaqus, FSI, proxy-server, and scheduler logic in an external-runner design.
- Do not silently copy a whole repository into a new case. Copy only the required dependency files unless the user explicitly requests a complete project.
- Include comments that explain the functional intent and the required pre-existing STAR-CCM+ objects, not merely what each line of Java does.
- Never embed licenses, passwords, tokens, private IPs, or other credentials. Use environment variables or placeholders.

## Default Runtime Output

Unless the user specifies another output directory, resolve the Windows Desktop with `[Environment]::GetFolderPath('Desktop')` and create:

`<Desktop>\runtime_yyyyMMdd\`

Use the current date in `yyyyMMdd` format. If that folder already exists, reuse it and avoid overwriting unrelated files. Put all generated scripts in this folder. A normal runtime folder contains:

- the generated `.java` macro or the complete requested launcher source;
- `README.md` when inputs, execution order, dependencies, or validation notes are needed;
- `Input_Files` only when the generated workflow requires concrete input files;
- a validation note only when useful to record API checks or an unresolved runtime limitation.

Do not write generated code into `references`, `backup`, or the source repositories by default.

## Validation Contract

Before reporting completion:

1. Re-read the generated files from the runtime folder.
2. Check public class name versus filename, package declarations, imports, braces, and obvious Java syntax errors.
3. Check every non-trivial STAR-CCM+ call against `search_api` and `get_doc`; record uncertain calls.
4. Check reference dependencies and required model-tree object names.
5. Compile or run a STAR-CCM+ batch test only when the local installation, license, Simulation, and inputs are available. A static or API check is not proof of a successful STAR-CCM+ run.
6. Report the exact runtime folder, files created, API verification status, execution test status, and remaining assumptions.

## Output Style

Give the user the generated runtime path first. Then summarize the function, required inputs, entry point, verified APIs, and validation status. Do not claim that a macro runs successfully unless it was actually executed in STAR-CCM+.
