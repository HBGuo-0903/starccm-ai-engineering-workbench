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
function leerHtml(docPath) {
  try { return readFileSync(DOC + "\\" + docPath.split("/").join("\\"), "utf8"); }
  catch (e) { console.error("ABORTA: no se puede leer " + docPath + " -- " + e.message); process.exit(2); }
}

const colap = leerTsv("bench\\runs\\colapso-50000\\bench-calls.tsv");
if (colap.length !== 18) { console.error("ABORTA: esperaba 18 get_doc en colapso-50000, hay " + colap.length); process.exit(2); }

let f2 = 0;
const paginas = [], vistas = new Set(), entregados = [];
for (const c of colap) {
  const r50 = extractText(leerHtml(c.docPath), 50000);
  if (!(r50.out.length === c.chars && r50.truncated === c.trunc)) { f2++; console.error("G2 NO REPRODUCE " + c.clase + ": " + r50.out.length + "/" + r50.truncated + " vs " + c.chars + "/" + c.trunc); }
  entregados.push(r50.out);
  if (!vistas.has(c.clase)) { vistas.add(c.clase); paginas.push({ clase: c.clase, texto: r50.text, origen: "banco" }); }
}
if (f2) { console.error("ABORTA G2: " + f2 + " de 18 no reproducen colapso-50000"); process.exit(2); }
console.log("G2 control positivo: 18/18 al caracter contra colapso-50000.");

const antes = leerTsv("bench\\runs\\antes-20000\\bench-calls.tsv");
const desp = leerTsv("bench\\runs\\despues-50000\\bench-calls.tsv");
if (antes.length !== 18 || desp.length !== 18) { console.error("ABORTA: esperaba 18 y 18 pre-colapso"); process.exit(2); }
let f1 = 0; const sobreviven = [];
for (let i = 0; i < 18; i++) {
  const a = antes[i], d = desp[i];
  if (a.docPath !== d.docPath) { console.error("ABORTA: pre-colapso desalineadas en " + i); process.exit(2); }
  const html = leerHtml(d.docPath);
  const r20 = extractText(html, 20000), r50 = extractText(html, 50000);
  const ok = r20.out.length === a.chars && r20.truncated === a.trunc && r50.out.length === d.chars && r50.truncated === d.trunc;
  if (ok) sobreviven.push(d.clase); else f1++;
}
console.log("G1 control negativo: " + f1 + " de 18 no reproducen; sobreviven [" + sobreviven.join(", ") + "]");
if (f1 !== 17 || sobreviven.length !== 1 || sobreviven[0] !== "PartManager") { console.error("ABORTA G1: la forma del fallo no es la esperada (17 fallos, sobrevive PartManager)"); process.exit(2); }

for (const [clase, docPath] of [["PartImportManager", "star/meshing/PartImportManager.html"], ["Feature", "star/cadmodeler/Feature.html"]]) {
  paginas.push({ clase, texto: extractText(leerHtml(docPath), 10000000).text, origen: "extra" });
}
if (paginas.length !== 19) { console.error("ABORTA: esperaba 19 paginas, hay " + paginas.length); process.exit(2); }

let f3 = 0;
for (const p of paginas) { if (p.texto.replace(/\s+/g, " ") !== p.texto) { f3++; console.error("G3 NO IDEMPOTENTE " + p.clase); } }
if (f3) { console.error("ABORTA G3: " + f3 + " de 19 aun tienen blanco colapsable"); process.exit(2); }
console.log("G3 idempotencia: 19/19 sin blanco colapsable.");

const prev = new Map();
for (const l of readFileSync(R + "tok\\paginas.tsv", "utf8").split(/\r?\n/).slice(1)) {
  if (!l.trim()) continue;
  const c = l.split("\t");
  prev.set(c[0], { chars: +c[8], tok: +c[9] });
}
let f4 = 0;
for (const p of paginas) {
  const r = prev.get(p.clase);
  if (!r) { console.error("G4 FALTA en paginas.tsv: " + p.clase); f4++; continue; }
  const t = tik(p.texto);
  if (p.texto.length !== r.chars || t !== r.tok) { f4++; console.error("G4 DISCREPA " + p.clase + ": " + p.texto.length + "/" + t + " vs " + r.chars + "/" + r.tok); }
}
if (f4) { console.error("ABORTA G4: " + f4 + " de 19 no concuerdan con las columnas colapsadas de tok\\paginas.tsv"); process.exit(2); }
console.log("G4 concordancia: 19/19 al caracter y al trozo contra 6b6f248.\n");

const out = ["clase\torigen\tchars_post\ttok_o200k\tfac_o200k\ttok_anth\tfac_anth"];
console.log("clase                        chars  o200k  anthr");
let sc = 0, so = 0, sa = 0, bc = 0, bo = 0, ba = 0;
const facs = [];
for (const p of paginas) {
  const s = p.texto, c = s.length, t1 = tik(s), t2 = countTokens(s);
  sc += c; so += t1; sa += t2;
  if (p.origen === "banco") { bc += c; bo += t1; ba += t2; }
  facs.push({ clase: p.clase, f: c / t1 });
  out.push([p.clase, p.origen, c, t1, (c / t1).toFixed(3), t2, (c / t2).toFixed(3)].join("\t"));
  console.log(p.clase.padEnd(27) + String(c).padStart(6) + (c / t1).toFixed(3).padStart(7) + (c / t2).toFixed(3).padStart(7));
}
facs.sort((x, y) => x.f - y.f);
console.log("\nFACTOR POST-COLAPSO (a677897), 19 paginas, texto completo pre-tope:");
console.log("  o200k_base       : " + sc + " chars / " + so + " trozos = " + (sc / so).toFixed(3) + "   [esperado exacto 4.940]");
console.log("  anthropic legacy : " + sc + " chars / " + sa + " trozos = " + (sc / sa).toFixed(3) + "   [banda 4.40 - 4.80]");
console.log("  17 del banco     : " + bc + " chars / " + bo + " o200k = " + (bc / bo).toFixed(3) + " / " + ba + " legacy = " + (bc / ba).toFixed(3));
console.log("  recorrido o200k  : " + facs[0].f.toFixed(3) + " (" + facs[0].clase + ") a " + facs[18].f.toFixed(3) + " (" + facs[18].clase + ") = " + (((facs[18].f / facs[0].f) - 1) * 100).toFixed(1) + "%");
console.log("  separacion o200k/legacy: " + ((((sc / so) / (sc / sa)) - 1) * 100).toFixed(2) + "%   [pre-colapso 8.96%]");

let ech = 0, eo = 0, ea = 0;
for (const s of entregados) { ech += s.length; eo += tik(s); ea += countTokens(s); }
console.log("\nBANCO, 18 llamadas get_doc entregadas, tope 50000, POST-COLAPSO:");
console.log("  chars         : " + ech + "  vs 277057 pre-colapso = " + (((ech - 277057) / 277057) * 100).toFixed(1) + "%   [esperado exacto 258714]");
console.log("  trozos o200k  : " + eo + "  vs 57157 pre-colapso = " + (((eo - 57157) / 57157) * 100).toFixed(1) + "%   [banda -6% a -13%]");
console.log("  trozos legacy : " + ea + "  vs 61042 pre-colapso = " + (((ea - 61042) / 61042) * 100).toFixed(1) + "%   [banda -5% a -14%]");

mkdirSync(R + "tok", { recursive: true });
writeFileSync(R + "tok\\paginas-colapso.tsv", out.join("\n"), "utf8");
console.log("\nEscrito tok\\paginas-colapso.tsv");