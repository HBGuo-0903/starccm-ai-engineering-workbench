import { readdirSync, readFileSync, writeFileSync } from "node:fs";
import { join } from "node:path";

const ROOT = process.env.STARCCM_DOC;
if (!ROOT) { console.error("ABORTA: STARCCM_DOC no definido"); process.exit(2); }
const TARGETS = ["AutoMeshOperation.html", "FieldFunctionManager.html"];

// Regla de desambiguacion: las paginas de clase viven en su paquete;
// Javadoc genera homonimos en subcarpetas class-use/, que se excluyen.
const hits = [];
function walk(dir) {
  for (const e of readdirSync(dir, { withFileTypes: true })) {
    if (e.isDirectory() && e.name === "class-use") continue;
    const p = join(dir, e.name);
    if (e.isDirectory()) walk(p);
    else if (TARGETS.includes(e.name)) hits.push(p);
  }
}
try { walk(ROOT); } catch (err) {
  console.error("GUARDIAN: no se puede recorrer " + ROOT + ": " + err.message);
  process.exit(2);
}
for (const t of TARGETS) {
  const n = hits.filter(p => p.endsWith("\\" + t)).length;
  if (n !== 1) {
    console.error("GUARDIAN: " + t + " esperado exactamente 1 fichero tras excluir class-use, encontrados " + n);
    process.exit(2);
  }
}

const data = new Map();
for (const file of hits.sort()) {
  const html = readFileSync(file, "utf8");
  const comments = [...html.matchAll(/<!--([\s\S]*?)-->/g)]
    .map(m => m[1].replace(/\s+/g, " ").trim());
  const counts = new Map();
  for (const m of html.matchAll(/<([a-zA-Z][a-zA-Z0-9]*)\b[^>]*?class="([^"]*)"/g)) {
    const k = m[1].toLowerCase() + "." + m[2];
    counts.set(k, (counts.get(k) || 0) + 1);
  }
  data.set(file, { chars: html.length, comments, counts });
}

let out = "";
for (const [file, d] of data) {
  out += "== " + file + " == chars=" + d.chars + "\n";
  out += "-- comentarios en orden de documento --\n";
  d.comments.forEach((c, i) => { out += String(i + 1).padStart(3) + "  " + c.slice(0, 90) + "\n"; });
  out += "-- elementos con class (recuento  tag.class) --\n";
  [...d.counts.entries()].sort((a, b) => b[1] - a[1])
    .forEach(([k, v]) => { out += String(v).padStart(5) + "  " + k + "\n"; });
  out += "\n";
}

const [a, b] = [...data.values()];
const setA = new Set(a.comments), setB = new Set(b.comments);
const soloA = [...setA].filter(c => !setB.has(c));
const soloB = [...setB].filter(c => !setA.has(c));
out += "-- vocabulario de comentarios: solo en pagina 1: " + soloA.length
     + " | solo en pagina 2: " + soloB.length + " --\n";
soloA.forEach(c => { out += "  <1  " + c.slice(0, 90) + "\n"; });
soloB.forEach(c => { out += "  <2  " + c.slice(0, 90) + "\n"; });

writeFileSync("tok\\recon-zonas.txt", out);
console.log(out);
console.log("escrito tok/recon-zonas.txt");