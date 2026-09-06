#!/usr/bin/env node
// STAR-CCM+ Javadoc MCP server
// Tools:
//   search_api  - find classes/interfaces and methods/fields by name
//   get_doc     - return the readable text of a Javadoc page
//
// Usage (registered via claude mcp add):
//   node server.mjs <path-to-javadoc-root>
//
// NOTE: an stdio MCP server must keep stdout clean for JSON-RPC.
// Never console.log to stdout here; use console.error (stderr) if needed.

import { McpServer } from "@modelcontextprotocol/sdk/server/mcp.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import { z } from "zod";
import { promises as fs } from "node:fs";
import path from "node:path";
import { extractText } from "./lib/extract.mjs";

// Javadoc root: first CLI arg or STARCCM_DOC env var; exits with a clear error if neither.
const _javadocArg = process.argv[2] || process.env.STARCCM_DOC;
if (!_javadocArg) {
  process.stderr.write(
    "Error: Javadoc root not specified. Pass it as the first argument or set STARCCM_DOC.\n"
  );
  process.exit(1);
}
const JAVADOC_ROOT = path.resolve(_javadocArg);

const STARCCM_VER = process.env.STARCCM_VER || "";
const LOG_PATH = process.env.STARCCM_MCP_LOG || "";

async function logCall(tool, args, chars, ms, meta) {
  if (!LOG_PATH) return;
  try {
    const ts = new Date().toISOString();
    const line =
      [ts, tool, JSON.stringify(args), chars, ms, JSON.stringify(meta), STARCCM_VER].join("\t") +
      "\n";
    await fs.appendFile(LOG_PATH, line, "utf8");
  } catch {}
}

let indexCache = null;

async function fileExists(p) {
  try { await fs.access(p); return true; } catch { return false; }
}

// Javadoc *-search-index.js files look like:  someVar = [ {...}, {...} ];
// Slice from the first '[' to the last ']' and JSON.parse that.
async function parseSearchIndex(file) {
  const text = await fs.readFile(file, "utf8");
  const start = text.indexOf("[");
  const end = text.lastIndexOf("]");
  if (start === -1 || end === -1) return [];
  try { return JSON.parse(text.slice(start, end + 1)); } catch { return []; }
}

async function walkHtml(dir, root, out) {
  let entries;
  try { entries = await fs.readdir(dir, { withFileTypes: true }); } catch { return; }
  for (const e of entries) {
    const full = path.join(dir, e.name);
    if (e.isDirectory()) {
      await walkHtml(full, root, out);
    } else if (e.isFile() && e.name.toLowerCase().endsWith(".html")) {
      out.push(path.relative(root, full).split(path.sep).join("/"));
    }
  }
}

// Non-class Javadoc pages to ignore in the filename fallback.
const SKIP_HTML = /^(index|allclasses|allpackages|overview|package-|help-doc|constant-values|deprecated-list|serialized-form|search|element-list)/i;

async function buildIndex() {
  if (indexCache) return indexCache;

  const types = [];
  const members = [];

  const typeIdxFile = path.join(JAVADOC_ROOT, "type-search-index.js");
  const memberIdxFile = path.join(JAVADOC_ROOT, "member-search-index.js");

  if (await fileExists(typeIdxFile)) {
    for (const t of await parseSearchIndex(typeIdxFile)) {
      if (!t || !t.l) continue;
      const pkg = t.p || "";
      const url = t.u || (pkg ? pkg.replaceAll(".", "/") + "/" + t.l + ".html" : t.l + ".html");
      types.push({ name: t.l, pkg, url });
    }
  }

  if (await fileExists(memberIdxFile)) {
    for (const m of await parseSearchIndex(memberIdxFile)) {
      if (!m || !m.l) continue;
      const pkg = m.p || "";
      const classUrl = (pkg ? pkg.replaceAll(".", "/") + "/" : "") + (m.c || "") + ".html";
      members.push({ name: m.l, cls: m.c || "", pkg, url: classUrl + (m.u || "") });
    }
  }

  // Fallback: derive class names from .html filenames if no type index exists.
  if (types.length === 0) {
    const htmls = [];
    await walkHtml(JAVADOC_ROOT, JAVADOC_ROOT, htmls);
    for (const rel of htmls) {
      const base = path.basename(rel, ".html");
      if (SKIP_HTML.test(base)) continue;
      const dir = path.dirname(rel);
      const pkg = dir === "." ? "" : dir.split("/").join(".");
      types.push({ name: base, pkg, url: rel });
    }
  }

  indexCache = { types, members };
  return indexCache;
}

const server = new McpServer({ name: "starccm", version: "0.1.0" });

server.registerTool(
  "search_api",
  {
    title: "Search STAR-CCM+ API",
    description:
      "Search the STAR-CCM+ Java API (Javadoc) for classes/interfaces and methods/fields by name. " +
      "Use this to find the correct class or method when writing STAR-CCM+ Java macros. " +
      "Returns each match with its package, declaring class (for members) and the relative path to its HTML doc page " +
      "(pass that path to get_doc to read the full signatures and descriptions).",
    inputSchema: {
      query: z.string().describe("Text to look for in class or member names, e.g. 'Simulation' or 'getRegionManager'"),
      kind: z.enum(["type", "member", "all"]).optional().describe("Restrict to types, members, or both (default: all)"),
      limit: z.number().int().positive().optional().describe("Maximum number of results (default 25)")
    }
  },
  async ({ query, kind = "all", limit = 25 }) => {
    const t0 = Date.now();
    const { types, members } = await buildIndex();
    const q = query.toLowerCase();
    const out = [];

    if (kind === "type" || kind === "all") {
      for (const t of types) {
        if (t.name.toLowerCase().includes(q)) {
          out.push(`[type]   ${t.pkg ? t.pkg + "." : ""}${t.name}  ->  ${t.url}`);
        }
      }
    }
    if (kind === "member" || kind === "all") {
      for (const m of members) {
        if (m.name.toLowerCase().includes(q)) {
          out.push(`[member] ${m.pkg ? m.pkg + "." : ""}${m.cls}.${m.name}  ->  ${m.url}`);
        }
      }
    }

    const total = out.length;
    const shown = out.slice(0, limit);
    const text = total
      ? `${total} match(es) for "${query}"${total > limit ? ` (showing first ${limit})` : ""}:\n` + shown.join("\n")
      : `No matches for "${query}".`;

    const meta = { total, shown: shown.length };
    await logCall("search_api", { query, kind, limit }, text.length, Date.now() - t0, meta);

    return {
      content: [{ type: "text", text }],
      _meta: { log: meta }
    };
  }
);

server.registerTool(
  "get_doc",
  {
    title: "Read STAR-CCM+ API doc page",
    description:
      "Return the readable text of a STAR-CCM+ Javadoc page (typically a class page) so you can see its method " +
      "signatures and descriptions. Pass the relative path returned by search_api, e.g. 'star/common/Simulation.html'.",
    inputSchema: {
      docPath: z.string().describe("Relative HTML path under the Javadoc root, e.g. 'star/common/Simulation.html'"),
      maxChars: z.number().int().positive().optional().describe("Maximum characters to return (default 50000)")
    }
  },
  async ({ docPath, maxChars = 50000 }) => {
    const t0 = Date.now();
    const clean = docPath.split("#")[0];
    const rootResolved = path.resolve(JAVADOC_ROOT);
    const full = path.resolve(rootResolved, clean);
    if (full !== rootResolved && !full.startsWith(rootResolved + path.sep)) {
      await logCall("get_doc", { docPath, maxChars }, 0, Date.now() - t0, { error: "path_refused" });
      return { content: [{ type: "text", text: "Refused: path is outside the Javadoc root." }], isError: true };
    }

    let html;
    try { html = await fs.readFile(full, "utf8"); }
    catch {
      await logCall("get_doc", { docPath, maxChars }, 0, Date.now() - t0, { error: "read_failed" });
      return { content: [{ type: "text", text: `Could not read '${clean}'.` }], isError: true };
    }

    const { text, out, truncated } = extractText(html, maxChars);

    const meta = { htmlChars: html.length, textChars: text.length, maxChars, truncated };
    await logCall("get_doc", { docPath, maxChars }, out.length, Date.now() - t0, meta);

    return {
      content: [{ type: "text", text: out }],
      _meta: { log: meta }
    };
  }
);

const transport = new StdioServerTransport();
await server.connect(transport);
