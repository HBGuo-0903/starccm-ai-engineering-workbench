# starccm-javadoc-mcp

A small MCP server that lets an AI assistant search and read the Simcenter STAR-CCM+ Java API documentation (Javadoc) that ships with your own installation, so the macros it writes use real, current API calls instead of invented ones. Two tools, `search_api` and `get_doc`, plus the instruments used to measure and reduce what the server costs in tokens.

This repository accompanies two tutorial series published on the Siemens Simcenter community forum. The forum renders client side and is hard to index; this README is the indexable table of contents.

## What it does

`search_api` finds a class, interface, method or field by name in the Javadoc search index and returns the path to its page. `get_doc` returns that page as plain text: the class description and the Method Summary with exact signatures and any Deprecated tag. The AI asks, reads, then writes. Nothing is recited from memory.

The Javadoc itself is not in this repository and never will be. It is Siemens documentation that ships with STAR-CCM+; the server reads it from a folder on your machine.

## Requirements

- Simcenter STAR-CCM+ with its Javadoc (typically under `<STAR-CCM+ install>\doc\...\client\html`). Measured against 2606 Build 21.04.007; the tool reads any modern Javadoc that ships `type-search-index.js` and `member-search-index.js`.
- Node.js 18 or newer.
- Claude Code CLI (the server is registered with `claude mcp add`). Any MCP client that speaks stdio should work; only Claude Code has been tested.

## Setup in five steps

1. Copy the Javadoc folder to a path without spaces, for example `C:\Users\<your-user>\ccmp\html`. Keep the whole folder, including the `*-search-index.js` files.
2. Clone this repository and install its two dependencies: `npm install`.
3. Register the server with Claude Code:

   ```
   claude mcp add --scope user starccm -- cmd /c node <path-to-repo>\server.mjs <path-to-javadoc>
   ```

   On macOS or Linux drop the `cmd /c` wrapper.
4. Check with `claude mcp list` that `starccm` shows as connected.
5. In a Claude Code session, ask: `use search_api to find RegionManager`, then `use get_doc on star/common/RegionManager.html`.

Step by step, with the Windows pitfalls (the MSIX build of Claude Desktop silently ignores local MCP servers; PowerShell blocks `npx.ps1`; a cold `npx` start times out), see Part 2 of the first series below.

## Measured cost

All figures were measured on the reference machine (STAR-CCM+ 2606 Build 21.04.007, Node v20.14.0) in August 2026 with the instruments in `tools/`. The raw custody files live in `tok/` and `bench/`; the aggregated numbers and their provenance are in `bench/README.md`.

| What | Value |
|---|---|
| Class pages in the Javadoc | 23,498 |
| Extracted text per page, median | 5,587 characters |
| Extracted text per page, 99th percentile | 19,854 characters |
| Pages above 20,000 characters | 224 (0.95%) before whitespace collapse, 144 after |
| Pages above 50,000 characters (the current default cap) | 11 after collapse |
| `star.common.Simulation`: raw HTML / extracted text / `javap -public` | 201,275 / 29,981 / 7,920 characters |
| Cost of grounding one class through `search_api` + `get_doc` | 33,316 characters, 3.8 times `javap` |
| Effect of whitespace collapse on the benchmark, in tokens | 10 to 12% fewer |
| Characters per token, corpus wide, after collapse | 4.996 (`o200k_base`), 4.621 (Anthropic legacy tokenizer) |

The comparison against raw HTML (25 times) that circulated early on measured the server's markup stripping, not its cost, and was withdrawn before publication. The `javap` ratio is the honest one.

## Layout

```
server.mjs              the MCP server: search_api and get_doc
lib/extract.mjs         HTML to text extraction used by get_doc and by every instrument
tools/                  measurement instruments (token factor, corpus sweep, collapse guards)
sweep-corpus-*.mjs      corpus characterisation sweeps, one per criterion revision
tok/                    custody files: measured outputs and the acceptance criteria that certify them
bench/                  the Part 4 benchmark: plan, per query results, summaries
tools/publish-list.txt  the list of files derived from the private working repository
```

Every instrument that publishes a figure checks the STAR-CCM+ version and the Javadoc root before writing, and reproduces its own custody file byte for byte. The acceptance criteria are in `tok/esperados-*.md`, in Spanish: they are working documents, kept as they were written.

## The two tutorial series

**Simcenter STAR-CCM+ Macros with AI** (6 parts, published). Builds this server and uses it.

1. [Why AI Invents the API (and the Fix)](https://community.sw.siemens.com/s/question/0D5Vb00001LZ5ReKAL/simcenter-starccm-macros-with-ai-16-why-ai-invents-the-api-and-the-fix)
2. [Build a Javadoc Search Tool (MCP Server)](https://community.sw.siemens.com/s/question/0D5Vb00001LiDTCKA3/simcenter-starccm-macros-with-ai-26-build-a-javadoc-search-tool-mcp-server)
3. [Find a Class, Read Its Real Page](https://community.sw.siemens.com/s/question/0D5Vb00001ME3gzKAD/simcenter-starccm-macros-with-ai-36-find-a-class-read-its-real-page)
4. [Trace a Macro from getActiveSimulation()](https://community.sw.siemens.com/s/question/0D5Vb00001McZcIKAV/simcenter-starccm-macros-with-ai-46-trace-a-macro-from-getactivesimulation)
5. [Audit a Macro for Invented and Deprecated Calls](https://community.sw.siemens.com/s/question/0D5Vb00001MkZ4UKAV/simcenter-starccm-macros-with-ai-56-audit-a-macro-for-invented-deprecated-calls)
6. [Stress-Test a Grounded Macro Against a Recording](https://community.sw.siemens.com/s/question/0D5Vb00001NKFrkKAH/simcenter-starccm-macros-with-ai-66-stresstest-a-grounded-macro-against-a-recording)

**Simcenter STAR-CCM+ Agentic Macros** (7 parts, in preparation). Adds a compile loop, a batch runner and the token economy work whose "before" numbers are the table above. Each part will land here as a tag.

Tags: `series1` marks the tree that corresponds to Part 2 of the first series.

## Related

[simulation-capsule](https://github.com/pacoEzq/simulation-capsule) (same author) is the other half of the idea: this repository teaches a model to write a simulation, that one teaches it to read the results.

## A note on AI generated macros

Grounding confirms that every API call is real and current for your version. It does not confirm that a macro does what you meant, that its arguments are right, or that the physics is right. Treat AI output as a draft: review and test it before you rely on it. Use of Siemens documentation and AI tools remains subject to your applicable Siemens terms.

## License

BSD-3-Clause. See `LICENSE`.
