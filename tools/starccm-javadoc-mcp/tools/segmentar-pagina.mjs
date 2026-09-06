import { readdirSync, readFileSync, writeFileSync } from "node:fs";
import { join } from "node:path";
import { stripDownwardBlocks, extractText } from "../lib/extract.mjs";
// --- aplanado con mapa de procedencia (replica flattenToText de lib/extract.mjs) ---
import { flattenWithMap } from "./flatten-map.mjs";
import { getEncoding } from "js-tiktoken";
import { countTokens } from "@anthropic-ai/tokenizer";

const ROOT = process.env.STARCCM_DOC;
const VER = process.env.STARCCM_VER || "";
if (!ROOT) { console.error("ABORTA: STARCCM_DOC no definido"); process.exit(2); }
if (!VER.startsWith("2606")) { console.error("ABORTA: version inesperada: " + VER); process.exit(2); }
const TARGETS = ["AutoMeshOperation.html", "FieldFunctionManager.html"];
// Guardian POST-COLAPSO (a677897): columnas colapsadas de tok/paginas.tsv @ 6b6f248
const ESPERADOS = {
  "FieldFunctionManager.html": { chars: 15187, o200k: 2862, legacy: 3057 },
  "AutoMeshOperation.html":    { chars: 13401, o200k: 2745, legacy: 2863 },
};
const ZONAS = ["Z0-preambulo","Z1-navegacion","Z2-indice-lateral","Z3-cabecera-tipo",
               "Z4-prosa-clase","Z5-tablas-summary","Z6-detail","Z7-cierre"];

function fail(msg){ console.error("GUARDIAN: " + msg); process.exit(2); }

// --- localizar paginas (regla: se excluyen las subcarpetas class-use) ---
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

const enc = getEncoding("o200k_base");
const tokO = t => t.length ? enc.encode(t).length : 0;
const tokA = t => t.length ? countTokens(t) : 0;
const pct  = (x, d) => (100 * x / d).toFixed(1);
const fac  = (c, t) => t ? (c / t).toFixed(3) : "-";

for (const file of hits.sort()) {
  const nombre = file.split("\\").pop();
  const html = readFileSync(file, "utf8");
  const pruned = stripDownwardBlocks(html).html;

  // --- fronteras sobre el HTML podado ---
  const cs = [];
  for (const m of pruned.matchAll(/<!--([\s\S]*?)-->/g)) cs.push({ i: m.index, t: m[1] });
  const one = needle => {
    const f = cs.filter(c => c.t.includes(needle));
    if (f.length !== 1) fail(nombre + ": comentario '" + needle + "' esperado 1, hay " + f.length);
    return f[0].i;
  };
  const b1 = one("START OF TOP NAVBAR"), b2 = one("END OF TOP NAVBAR");
  const b3 = one("START OF CLASS DATA"), b5 = one("NESTED CLASS SUMMARY");
  const b7 = one("END OF CLASS DATA");
  const det = cs.filter(c => c.t.includes("DETAIL"));
  if (det.length < 1 || det.length > 3) fail(nombre + ": comentarios DETAIL esperados 1-3, hay " + det.length);
  const b6 = det[0].i;
  // Frontera Z3/Z4: primer div.block DENTRO de la descripcion de clase.
  // Regla declarada: una clase sin ese bloque produce Z4 vacia (b4 = b5); se anota, no se aborta.
  let b4 = pruned.indexOf('<div class="block">', b3);
  if (b4 < 0 || b4 >= b5) {
    b4 = b5;
    console.log("NOTA: " + nombre + " sin div.block en la descripcion de clase -> Z4 vacia");
  }
  const bounds = [0, b1, b2, b3, b4, b5, b6, b7, pruned.length];
  // Guardian de orden: se admite igualdad (zona vacia), se prohibe el retroceso.
  for (let i = 1; i < bounds.length; i++)
    if (bounds[i] < bounds[i - 1]) fail(nombre + ": fronteras fuera de orden en posicion " + i);

  // --- texto canonico y guardianes ---
  const flat = flattenWithMap(pruned);
  const ref = extractText(html, Number.MAX_SAFE_INTEGER);
  if (flat.text !== ref.text) fail(nombre + ": el texto con mapa NO coincide letra a letra con extractText");
  const esp = ESPERADOS[nombre];
  const totO = tokO(flat.text), totA = tokA(flat.text);
  if (flat.text.length !== esp.chars || totO !== esp.o200k || totA !== esp.legacy)
    fail(nombre + ": reproduccion rota. chars=" + flat.text.length + "/" + esp.chars +
         " o200k=" + totO + "/" + esp.o200k + " legacy=" + totA + "/" + esp.legacy);

  // --- asignacion de zonas (contigua por construccion; se verifica) ---
  const starts = [0]; let z = 0;
  for (let i = 0; i < flat.map.length; i++) {
    while (z < 7 && flat.map[i] >= bounds[z + 1]) { z++; starts.push(i); }
  }
  while (starts.length < 9) starts.push(flat.text.length);
  let sum = 0;
  const filas = [];
  let sumO = 0, sumA = 0;
  for (let k = 0; k < 8; k++) {
    const zt = flat.text.slice(starts[k], starts[k + 1] ?? flat.text.length);
    sum += zt.length;
    const blanco = (zt.match(/\s/g) || []).length;
    const o = tokO(zt), a = tokA(zt);
    sumO += o; sumA += a;
    filas.push([ZONAS[k], zt.length, blanco, zt.length ? pct(blanco, zt.length) : "-",
                o, fac(zt.length, o), a, fac(zt.length, a),
                pct(zt.length, flat.text.length), pct(o, totO)].join("\t"));
  }
  if (sum !== flat.text.length) fail(nombre + ": las zonas no cubren el texto: " + sum + " de " + flat.text.length);

  const cab = "zona\tchars\tblanco\tpct_blanco\ttrozos_o200k\tfactor_o200k\ttrozos_legacy\tfactor_legacy\tpct_chars_pag\tpct_trozos_pag";
  const pie = ["PAGINA-directa", flat.text.length, "", "", totO, fac(flat.text.length, totO), totA,
               fac(flat.text.length, totA), "100.0", "100.0"].join("\t")
    + "\n" + ["SUMA-zonas", sum, "", "", sumO, "", sumA, "", "", ""].join("\t")
    + "\n" + ["MIGAS(suma-directa)", "", "", "", (sumO - totO) + " (" + pct(sumO - totO, totO) + "%)",
              "", (sumA - totA) + " (" + pct(sumA - totA, totA) + "%)", "", "", ""].join("\t");
  const cuerpo = cab + "\n" + filas.join("\n") + "\n" + pie + "\n";
  const salida = "tok\\segmentos-" + nombre.replace(".html", "") + ".tsv";
  writeFileSync(salida, cuerpo);
  console.log("== " + nombre + " ==");
  console.log(cuerpo);
  console.log("escrito " + salida + "\n");
}