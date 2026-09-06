# Benchmark and provenance

This folder holds the Part 4 benchmark of the Agentic Macros series: the frozen query plan (`plan.json`), one run per server configuration under `runs/`, and this note on where every published figure comes from.

Reference machine for every number in this repository: Simcenter STAR-CCM+ 2606 Build 21.04.007, its own JDK 25.0.1, Node v20.14.0, Windows 11. Measured August 2026.

## Runs

| Run | Server state | What it measures |
|---|---|---|
| `runs/antes-20000/` | default cap 20,000 characters, before whitespace collapse | the "before" of Part 4 |
| `runs/colapso-50000/` | whitespace collapse applied, cap 50,000 | effect of the collapse alone |
| `runs/despues-50000/` | collapse plus cap 50,000 | the "after" |

Each run keeps `bench-calls.tsv` (one row per query: characters returned, milliseconds) and `bench-summary.json`. The per call server log (`mcp-calls.tsv`) is not published: it is a session log, not a result.

## Figures cited in the root README, with their custody

The public repository is derived from a private working repository on the reference machine. Some custody files stay private because they contain the corporate user path in a line that certifies where a physical backup was kept; rewriting them would break the hash under which they were accredited. Their aggregated figures are published here instead, each with the SHA256 of the private file and the commit that produced it.

| Figure | Value | Custody file (private) | Notes |
|---|---|---|---|
| Class pages in the corpus | 23,498 | `salida-c2/corpus-blocks-summary.json` | denominator for every percentage; the tree holds 48,865 HTML files, 23,498 are class pages |
| Median extracted text per page | 5,587 chars | same | `textChars` after extraction, before collapse |
| 99th percentile | 19,854 chars | same | falls just under the old 20,000 cap |
| Pages above 20,000 before collapse | 224 (0.95%) | same | measured 2026-08-05, sweep `c2`, 66,778 ms, canary OK |
| Pages above 20,000 after removing downward class lists only | 196 | same | not 181: that figure came from a criterion that also removed upward lists |
| Pages saved by the list suppression | 28 of 224 | same | |
| Pages above 20,000 after whitespace collapse | 144 | `tok/corpus-post-colapso-summary.json` (public) | collapse commit `a677897` |
| Pages above 50,000 after collapse | 11 | same | 13 before collapse |
| `Simulation`, raw HTML / extracted / `javap` | 201,275 / 29,981 / 7,920 | `salida-b/corpus-summary.json` and the 2026-08-03 session log | `get_doc` 29,981 is pre collapse; 29,779 after |
| Grounding one class, `search_api` + `get_doc` | 33,316 chars | same | 3,335 + 29,981 |
| Collapse effect on the benchmark | 10 to 12% fewer tokens | `bench/runs/colapso-50000/` (public) | 18 `get_doc` calls, cap 50,000; 6.6% fewer characters |
| Characters per token, corpus wide, after collapse | 4.996 `o200k_base`, 4.621 Anthropic legacy | `tok/muestra-corpus.tsv` (public) | stratified sample of 513 classes, census denominator 134,600,475 characters |
| Characters per token by size stratum | monotone decreasing, 9.9% between extremes | same | S1 under 2,365 chars 5.312 / 5.018; S6 over 30,000 chars 4.832 / 4.579 |

Two claims that were withdrawn before publication, kept here so nobody rediscovers them: the truncation is not silent (`get_doc` appends `...[truncated]`, 16 characters, on every truncated page); and "removing the class lists solves half the problem" is true in characters but not in pages (28 of 224).

## About `tok/esperados-*.md`

The four acceptance criteria in `tok/` are the private custody files copied verbatim, with one exception: `esperados-dedup-flatten.md` line 31 has the corporate user path of a physical backup replaced by `<respaldo fisico fuera del repo>`. The private original keeps its accredited hash; the public copy differs from it in that one line only.

## Hashes of the private custody files

Paths are relative to the private repository root. These files are not published; the hashes let anyone check that a copy they are shown is the one measured here.

| Path | SHA256 | Commit |
| --- | --- | --- |
| `salida/corpus-summary.json` | `4002B0ECE5C56BFA9E109A63BC344749B2D35F3B3B00C450B15E630B284BD5EA` | `d1e2f42` |
| `salida-b/corpus-summary.json` | `BFF0E53010940F5026A4D183DF1D70C52A929F11CADD7A3336CD1AB36755C543` | `d1e2f42` |
| `salida-c/corpus-blocks-summary.json` | `A652A97EB86BF11127E5741AD516ADBB770AC9023DF4EDC5AA321FBFD7B0C680` | `d1e2f42` |
| `salida-c2/corpus-blocks-summary.json` | `78458CF1DC2CBBC22FF6D4B74AEDE5FF951E1E48C3D7BEEE64E02D0367E22D62` | `d1e2f42` |
| `salida-c2/confirmacion-canario.txt` | `882873FBBD5F4C623624A88129C435745DD680EEA6197634DFD9472C652EFD4C` | `7ca7743` |
| `tok/corpus-pre-colapso-summary.json` | `648FD705AAA591422289272C225E2C8D6508C87E8061BC95A78C50B556DA220B` | `4100dd0` |
| `tok/recon-zonas.txt` | `BA56F778DCE88CBCD52F424B31CA49BE04635C3F38950E170A73B32EC15A515D` | `74a8888` |
