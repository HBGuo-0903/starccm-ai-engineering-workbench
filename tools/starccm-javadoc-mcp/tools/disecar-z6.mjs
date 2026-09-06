import { readdirSync, readFileSync, writeFileSync } from "node:fs";
import { join } from "node:path";
import { stripDownwardBlocks, extractText } from "../lib/extract.mjs";
import { flattenWithMap } from "./flatten-map.mjs";
import { getEncoding } from "js-tiktoken";
import { countTokens } from "@anthropic-ai/tokenizer";

const ROOT = process.env.STARCCM_DOC;
const VER = process.env.STARCCM_VER || "";
if (!ROOT) { console.error("ABORTA: STARCCM_DOC no definido"); process.exit(2); }
if (!VER.startsWith("2606")) { console.error("ABORTA: version inesperada: " + VER); process.exit(2); }
const TARGETS = ["AutoMeshOperation.html", "FieldFunctionManager.html"];
// Guardianes POST-COLAPSO (a677897): pagina completa de las columnas
// colapsadas de tok/paginas.tsv @ 6b6f248; Z6 recaracterizado el 10.08.
const ESPERADOS = {
  "FieldFunctionManager.html": { chars: 15187, o200k: 2862, legacy: 3057,
                                 z6: { chars: 6174, o200k: 1139, legacy: 1211 } },
  "AutoMeshOperation.html":    { chars: 13401, o200k: 2745, legacy: 2863,
                                 z6: { chars: 5019, o200k: 1022, legacy: 1052 } },
};

function fail(msg){ console.error("GUARDIAN: " + msg); process.exit(2); }

const hits = [];
function walk(dir){
  for (const e of readdirSync(dir, { withFileTypes: true })) {
    if (e.isDirectory() && e.name === "class-use") continue;
    const p = join(dir, e.name);
    if (e.isDirectory()) walk(p);
    else if (TARGETS.includes(e.name)) hits.push(p);
  }
}
walk(ROOT);
for (const t of TARGETS) {
  const n = hits.filter(p => p.endsWith("\\" + t)).length;
  if (n !== 1) fail(t + ": esperado 1 fichero tras excluir class-use, encontrados " + n);
}

// Cierre equilibrado de un div: cuenta aperturas y cierres desde el tag inicial.
function balancedDivEnd(html, start){
  const re = /<div\b|<\/div>/g; re.lastIndex = start;
  let depth = 0, m;
  while ((m = re.exec(html))) {
    if (m[0] === "<div") depth++; else depth--;
    if (depth === 0) return m.index + m[0].length;
  }
  return -1;
}
function collectRanges(html, marker, cat, from, to){
  const out = []; let i = from;
  for (;;) {
    const s = html.indexOf(marker, i);
    if (s < 0 || s >= to) break;
    const e = balancedDivEnd(html, s);
    if (e < 0) fail("div sin cierre para " + marker + " en " + s);
    if (e > to) fail(cat + " en " + s + " desborda Z6");
    out.push({ start: s, end: e, cat });
    i = e;
  }
  return out;
}

const enc = getEncoding("o200k_base");
const tokO = t => t.length ? enc.encode(t).length : 0;
const tokA = t => t.length ? countTokens(t) : 0;
const pct  = (x, d) => d ? (100 * x / d).toFixed(1) : "-";
const fac  = (c, t) => t ? (c / t).toFixed(3) : "-";

for (const file of hits.sort()) {
  const nombre = file.split("\\").pop();
  const html = readFileSync(file, "utf8");
  const pruned = stripDownwardBlocks(html).html;

  const cs = [];
  for (const m of pruned.matchAll(/<!--([\s\S]*?)-->/g)) cs.push({ i: m.index, t: m[1] });
  const det = cs.filter(c => c.t.includes("DETAIL"));
  if (det.length < 1) fail(nombre + ": sin comentarios DETAIL");
  const b6 = det[0].i;
  const fin = cs.filter(c => c.t.includes("END OF CLASS DATA"));
  if (fin.length !== 1) fail(nombre + ": END OF CLASS DATA esperado 1, hay " + fin.length);
  const b7 = fin[0].i;

  const ranges = [
    ...collectRanges(pruned, '<div class="member-signature">', "firma", b6, b7),
    ...collectRanges(pruned, '<div class="block">', "prosa", b6, b7),
  ].sort((a, b) => a.start - b.start);
  for (let i = 1; i < ranges.length; i++)
    if (ranges[i].start < ranges[i - 1].end) fail(nombre + ": solape de elementos en " + ranges[i].start);

  const flat = flattenWithMap(pruned);
  const ref = extractText(html, Number.MAX_SAFE_INTEGER);
  if (flat.text !== ref.text) fail(nombre + ": texto con mapa != extractText");
  const esp = ESPERADOS[nombre];
  const totO = tokO(flat.text), totA = tokA(flat.text);
  if (flat.text.length !== esp.chars || totO !== esp.o200k || totA !== esp.legacy)
    fail(nombre + ": reproduccion de pagina rota");

  let z6s = -1, z6e = flat.text.length;
  for (let i = 0; i < flat.map.length; i++) {
    if (z6s < 0 && flat.map[i] >= b6) z6s = i;
    if (flat.map[i] >= b7) { z6e = i; break; }
  }
  if (z6s < 0) fail(nombre + ": Z6 vacia");
  const z6t = flat.text.slice(z6s, z6e);
  const z6O = tokO(z6t), z6A = tokA(z6t);
  if (z6t.length !== esp.z6.chars || z6O !== esp.z6.o200k || z6A !== esp.z6.legacy)
    fail(nombre + ": reproduccion de Z6 rota. chars=" + z6t.length + "/" + esp.z6.chars +
         " o200k=" + z6O + "/" + esp.z6.o200k + " legacy=" + z6A + "/" + esp.z6.legacy);

  // Clasificacion letra a letra y segmentos contiguos por categoria
  const segs = []; let p = 0, curCat = null, curStart = z6s;
  for (let i = z6s; i < z6e; i++) {
    while (p < ranges.length && flat.map[i] >= ranges[p].end) p++;
    const cat = (p < ranges.length && flat.map[i] >= ranges[p].start) ? ranges[p].cat : "resto";
    if (cat !== curCat) {
      if (curCat !== null) segs.push({ cat: curCat, start: curStart, end: i });
      curCat = cat; curStart = i;
    }
  }
  segs.push({ cat: curCat, start: curStart, end: z6e });

  const acc = { firma: { chars:0, blanco:0, o:0, a:0, n:0 },
                prosa: { chars:0, blanco:0, o:0, a:0, n:0 },
                resto: { chars:0, blanco:0, o:0, a:0, n:0 } };
  for (const s of segs) {
    const t = flat.text.slice(s.start, s.end);
    const A = acc[s.cat];
    A.chars += t.length;
    A.blanco += (t.match(/\s/g) || []).length;
    A.o += tokO(t); A.a += tokA(t); A.n++;
  }
  const sumChars = acc.firma.chars + acc.prosa.chars + acc.resto.chars;
  if (sumChars !== z6t.length) fail(nombre + ": categorias no cubren Z6: " + sumChars + " de " + z6t.length);
  const sumO = acc.firma.o + acc.prosa.o + acc.resto.o;
  const sumA = acc.firma.a + acc.prosa.a + acc.resto.a;

  const cab = "categoria\tsegmentos\tchars\tblanco\tpct_blanco\ttrozos_o200k\tfactor_o200k\ttrozos_legacy\tfactor_legacy\tpct_chars_z6";
  const filas = ["firma","prosa","resto"].map(c => {
    const A = acc[c];
    return [c, A.n, A.chars, A.blanco, pct(A.blanco, A.chars), A.o, fac(A.chars, A.o),
            A.a, fac(A.chars, A.a), pct(A.chars, z6t.length)].join("\t");
  });
  const pie = ["Z6-directa","",z6t.length,"","",z6O,fac(z6t.length,z6O),z6A,fac(z6t.length,z6A),"100.0"].join("\t")
    + "\n" + ["SUMA-categorias","",sumChars,"","",sumO,"",sumA,"",""].join("\t")
    + "\n" + ["MIGAS(suma-directa)","","","","",(sumO - z6O) + " (" + pct(sumO - z6O, z6O) + "%)","",
              (sumA - z6A) + " (" + pct(sumA - z6A, z6A) + "%)","",""].join("\t");
  const cuerpo = cab + "\n" + filas.join("\n") + "\n" + pie + "\n";
  const salida = "tok\\z6-" + nombre.replace(".html", "") + ".tsv";
  writeFileSync(salida, cuerpo);
  console.log("== " + nombre + " · Z6 por categorias ==");
  console.log(cuerpo);
  console.log("escrito " + salida + "\n");
}