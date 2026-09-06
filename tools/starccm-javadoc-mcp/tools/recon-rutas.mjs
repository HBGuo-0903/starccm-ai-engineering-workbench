import { readdirSync } from "node:fs";
import { join } from "node:path";

const ROOT = process.env.STARCCM_DOC;
if (!ROOT) { console.error("ABORTA: STARCCM_DOC no definido"); process.exit(2); }
const TARGETS = ["AutoMeshOperation.html", "FieldFunctionManager.html"];

const hits = [];
function walk(dir) {
  for (const e of readdirSync(dir, { withFileTypes: true })) {
    const p = join(dir, e.name);
    if (e.isDirectory()) walk(p);
    else if (TARGETS.includes(e.name)) hits.push(p);
  }
}
walk(ROOT);
for (const p of hits.sort()) console.log(p);