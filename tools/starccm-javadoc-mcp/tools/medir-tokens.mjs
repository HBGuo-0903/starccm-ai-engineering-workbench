import { extractText } from "../lib/extract.mjs";
import { getEncoding } from "js-tiktoken";
import { countTokens } from "@anthropic-ai/tokenizer";
import { readFileSync, writeFileSync, mkdirSync } from "node:fs";
import { fileURLToPath } from "node:url";

const DOC = process.env.STARCCM_DOC;
const VER = process.env.STARCCM_VER || "";
if (!DOC) { console.error("ABORTA: STARCCM_DOC no definido"); process.exit(2); }
if (!VER.startsWith("2606")) { console.error("ABORTA: version inesperada: " + VER); process.exit(2); }

const enc = getEncoding("o200k_base");
const tik = (s) => enc.encode(s).length;
const CAN_P = "Returns the simulation object that owns this report. The value is never null and is created when the simulation starts.";
const CAN_F = "public star.base.neo.NamedObject getPresentationObject(java.lang.String presentationName, boolean createIfMissing)";
if (tik(CAN_P) !== 22 || tik(CAN_F) !== 20) { console.error("ABORTA: canario o200k movido"); process.exit(2); }
if (countTokens(CAN_P) !== 22 || countTokens(CAN_F) !== 26) { console.error("ABORTA: canario anthropic movido"); process.exit(2); }

const R = fileURLToPath(new URL("../", import.meta.url));
function leerTsv(p) {
  const filas = [];
  for (const l of readFileSync(R + p, "utf8").split(/\r?\n/).slice(1)) {
    if (!l.trim()) continue;
    const c = l.split("\t");
    if (c[2] !== "get_doc") continue;
    filas.push({ qid: c[0], clase: c[1], docPath: JSON.parse(c[3]).docPath, chars: +c[4], trunc: c[6] === "true" });
  }
  return filas;
}
const antes = leerTsv("bench\\runs\\antes-20000\\bench-calls.tsv");
const desp = leerTsv("bench\\runs\\despues-50000\\bench-calls.tsv");
if (antes.length !== 18 || desp.length !== 18) { console.error("ABORTA: esperaba 18 y 18"); process.exit(2); }

function leerHtml(docPath) {
  try { return readFileSync(DOC + "\\" + docPath.split("/").join("\\"), "utf8"); }
  catch (e) { console.error("ABORTA: no se puede leer " + docPath + " -- " + e.message); process.exit(2); }
}

let fallos = 0;
const paginas = [];
const vistas = new Set();
for (let i = 0; i < 18; i++) {
  const a = antes[i], d = desp[i];
  if (a.docPath !== d.docPath) { console.error("ABORTA: desalineadas en " + i); process.exit(2); }
  const html = leerHtml(d.docPath);
  const r20 = extractText(html, 20000);
  const r50 = extractText(html, 50000);
  if (!(r20.out.length === a.chars && r20.truncated === a.trunc && r50.out.length === d.chars && r50.truncated === d.trunc)) {
    fallos++; console.error("NO REPRODUCE " + d.clase);
  }
  if (!vistas.has(d.clase)) { vistas.add(d.clase); paginas.push({ clase: d.clase, texto: r20.text, origen: "banco" }); }
}
if (fallos) { console.error("ABORTA: " + fallos + " de 18 no reproducen"); process.exit(2); }
console.log("Reproduccion: 18/18 al caracter.\n");

for (const [clase, docPath] of [["PartImportManager", "star/meshing/PartImportManager.html"], ["Feature", "star/cadmodeler/Feature.html"]]) {
  paginas.push({ clase, texto: extractText(leerHtml(docPath), 10000000).text, origen: "extra" });
}
if (paginas.length !== 19) { console.error("ABORTA: esperaba 19 paginas, hay " + paginas.length); process.exit(2); }

const cache = new Map();
function fracTrozosBlanco(s) {
  const ids = enc.encode(s);
  let n = 0;
  for (const id of ids) {
    let d = cache.get(id);
    if (d === undefined) { d = enc.decode([id]); cache.set(id, d); }
    if (/^\s+$/.test(d)) n++;
  }
  return ids.length ? n / ids.length : 0;
}

const out = ["clase\torigen\ttextChars\tblanco_pct\ttok_o200k\tfac_o200k\ttok_anth\tfac_anth\tchars_colap\ttok_colap\tfac_colap\ttrozos_blanco_pct"];
console.log("clase                        chars  blanco  o200k  anthr  colap  trozBl");
let sc = 0, so = 0, sa = 0, scc = 0, soc = 0;
for (const p of paginas) {
  const s = p.texto, c = s.length;
  const w = (s.match(/\s/g) || []).length / c;
  const t1 = tik(s), t2 = countTokens(s);
  const col = s.replace(/\s+/g, " ");
  const cc = col.length, tc = tik(col);
  const tb = fracTrozosBlanco(s);
  sc += c; so += t1; sa += t2; scc += cc; soc += tc;
  out.push([p.clase, p.origen, c, (w * 100).toFixed(1), t1, (c / t1).toFixed(3), t2, (c / t2).toFixed(3), cc, tc, (cc / tc).toFixed(3), (tb * 100).toFixed(1)].join("\t"));
  console.log(p.clase.padEnd(27) + String(c).padStart(6) + (w * 100).toFixed(1).padStart(7) + "%" + (c / t1).toFixed(3).padStart(7) + (c / t2).toFixed(3).padStart(7) + (cc / tc).toFixed(3).padStart(7) + (tb * 100).toFixed(1).padStart(6) + "%");
}
console.log("\nAgregado sobre " + paginas.length + " paginas:");
console.log("  tal cual   : " + sc + " chars / " + so + " trozos o200k = " + (sc / so).toFixed(3));
console.log("  tal cual   : " + sc + " chars / " + sa + " trozos anthropic = " + (sc / sa).toFixed(3));
console.log("  colapsado  : " + scc + " chars / " + soc + " trozos o200k = " + (scc / soc).toFixed(3));
console.log("  colapsar el blanco quita " + (so - soc) + " trozos de " + so + " = " + (((so - soc) / so) * 100).toFixed(1) + "% menos trozos");
console.log("  y quita " + (sc - scc) + " chars de " + sc + " = " + (((sc - scc) / sc) * 100).toFixed(1) + "% menos caracteres");

mkdirSync(R + "tok", { recursive: true });
writeFileSync(R + "tok\\paginas.tsv", out.join("\n"), "utf8");
console.log("\nEscrito tok\\paginas.tsv");