---
name: starccm-knowledge-maintainer
description: Maintain, audit, reorganize, and synchronize the STAR-CCM+ Java development knowledge base under the local references directory. Use when documentation is stale, projects need classification, duplicate or test files need review, README or AGENTS files need updating, indexes need regeneration, or a clean handoff is requested. Applies neat-freak principles of inventory, deduplication, anti-bloat, audience separation, and evidence-based validation.
---

# STAR-CCM+ Knowledge Maintainer

Keep the STAR-CCM+ reference library accurate, navigable, compact, and safe for a new developer or another agent to use. Act as an editor: reconcile actual code, project structure, documentation, and the generated index instead of merely appending notes.

## Mandatory Starting Point

Before inspecting or changing reference content:

1. Resolve the cloned repository root and read `references/AGENTS.md`.
2. Read `references/STARCCM_References_Index.csv`; resolve its `RelativePath` values against `<repo-root>/references`.
3. Use the CSV to select the relevant category, project, and files for the user's goal.
4. Read the selected directory's `AGENTS.md`, `README.md`, and actual code before making a classification or cleanup decision.

Do not recursively scan all active references or `backup` at the beginning. Expand scope only when the index, imports, file paths, version evidence, or duplicate checks show that it is necessary.

索引文件是导航入口，不是静态附件。开始维护时先读取现有 CSV；如果 CSV 缺失、无法解析或早于相关目录的修改时间，先刷新一次。任何维护动作完成后，都必须调用以下脚本刷新索引：

`tools/skills/starccm-knowledge-maintainer/scripts/generate-references-index.ps1`

PowerShell 调用方式：

```powershell
& "<repo-root>\tools\skills\starccm-knowledge-maintainer\scripts\generate-references-index.ps1"
```

刷新完成后重新读取生成的 `STARCCM_References_Index.csv`，以它作为最终的路径、分类和文件数量校验依据。文件移动、删除、改名、代码修改或文档修改都必须执行刷新，不能只手工修改 CSV，也不能以旧索引作为最终结果。

## Knowledge Boundaries

- `Basic_Syntax`: small syntax and native API learning examples; do not add unnecessary README or AGENTS files to trivial single-file examples.
- `Standalone_STARCCM_Functions`: complete functions executed inside STAR-CCM+; each case directory should represent one meaningful function or workflow.
- `External_STARCCM_Runners`: complete projects that call STAR-CCM+ externally, including launchers, schedulers, coupling, FSI, Abaqus, proxy-server, and cluster content. Keep a complete external project together.
- `Shared_STARCCM_Libraries`: reusable libraries only. `MacroUtils` remains the complete original project; `StarClasses_Helper_Library` remains a case-level helper collection, not official Siemens documentation.
- `backup`: historical and original material. Use it for provenance, comparison, or recovery; do not index it as active content.

## Audit Procedure

### Inventory

Use the index to create a focused inventory. For each selected item, record internally:

- actual path and item type;
- entry file and direct dependencies;
- Java, script, input, documentation, and total file counts;
- README/AGENTS presence;
- package names, imports, external paths, and version markers;
- tests, examples, generated files, and historical variants.

### Understand Before Reorganizing

Read the actual files needed to answer the task. A directory containing many Java files may be one project with an entry point, ordered steps, helper classes, data models, and compatibility code. Decide based on imports, package declarations, build files, launch scripts, input references, and execution order—not on file count alone.

When a directory mixes multiple functions, split only when each resulting function is independently understandable and runnable. If the files form one external project, preserve the entire project and document its internal roles instead.

### Detect Redundancy

Check for:

- exact duplicate files by content hash;
- same-name files with version suffixes;
- historical copies superseded by a newer version;
- test, smoke-test, negative-test, generated, or temporary files;
- duplicated helper libraries copied into cases;
- README/AGENTS statements that disagree with code;
- broken paths and links;
- Mermaid workflows that do not parse because labels contain unquoted punctuation.

Do not delete based on filename alone. Read candidates and confirm whether they are referenced. For a historical version, retain the newest verified version and archive the older one when the user has authorized cleanup. Keep tests when they are the only executable evidence or are required by the project; otherwise archive them with the reason recorded.

## Documentation Rules

### AGENTS.md

Use `AGENTS.md` for rules that a future agent must follow:

- scope and project boundary;
- input and output contract;
- required reading and execution constraints;
- file roles and dependency boundaries;
- maintenance and validation rules.

For `Standalone_STARCCM_Functions` case directories, the summary must explain the functional problem solved, the method used, and the resulting STAR-CCM+ effect. The workflow must be Mermaid-only, sufficiently detailed, and derived from the real Java execution path. Quote Mermaid node labels containing parentheses, commas, slashes, colons, or other punctuation, for example `A["ClassName.execute(): 获取 Simulation"]`.

For external projects, explain the external launcher, STAR-CCM+ invocation, Java macro handoff, inputs, outputs, and cross-process boundaries. Do not replace the real workflow with a generic “input → execute → output” sentence.

### README.md

Use README for ordinary users and future maintainers. Explain what the project does, when to use it, how to locate the entry point, required inputs, dependencies, expected outputs, and known limitations. Keep source attribution concise: state that the project was referenced from the relevant original repository without duplicating the repository's entire history.

Avoid creating decorative documentation files such as `Workflow_Description.md`. Put agent constraints in `AGENTS.md` and user guidance in `README.md`.

### Anti-Bloat

Apply neat-freak principles:

- edit or merge existing rules instead of appending repeated statements;
- remove obsolete instructions and completed temporary plans;
- keep root AGENTS files focused on durable boundaries and procedures;
- move detailed technical explanations to the appropriate README or source comments;
- do not turn AGENTS into a chronological change log;
- do not create documentation for a trivial one-file syntax example unless it needs non-obvious setup.

## Safe Change Procedure

1. Confirm target paths from the index and selected AGENTS files.
2. Preserve unrelated user changes.
3. Prefer a reversible archive under the existing `backup` structure for material cleanup.
4. Move a complete project as a unit; never split an external project's launcher, Java sources, inputs, or documentation across categories.
5. Update the nearest AGENTS/README files affected by the change.
6. Call `<repo-root>\tools\skills\starccm-knowledge-maintainer\scripts\generate-references-index.ps1` to refresh `references\STARCCM_References_Index.csv`.
7. Re-read the refreshed CSV and verify changed paths, categories, and file counts.

## Validation Checklist

Before reporting completion, verify:

- every changed path exists and every documented path resolves;
- the index contains the current categories and file counts;
- no unintended backup, build, cache, or dependency directory entered active references;
- no exact duplicates remain where cleanup was requested;
- no obsolete version was retained as the active default without explanation;
- each changed case has an accurate summary and code-derived Mermaid workflow;
- external projects remain whole;
- README instructions agree with actual entry files, inputs, outputs, and dependencies;
- no secrets, license keys, passwords, or private connection details were introduced;
- STAR-CCM+ execution claims are separated from static inspection and API verification.

## Handoff Report

Report the result in this order:

1. focused scope read from the index;
2. actual files inspected and changed;
3. moves, archives, merges, or removals and their reasons;
4. regenerated index row count and validation results;
5. unresolved ambiguity, unavailable runtime, or user decision still required.
