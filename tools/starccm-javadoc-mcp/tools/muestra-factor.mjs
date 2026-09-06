// muestra-factor.mjs - factor caracteres->trozos del corpus por muestra estratificada
// Censo: tok/corpus-post-colapso.tsv @ 2980bc2. Semilla fija: la muestra es reproducible.
// Solo se estima el factor por estrato; el denominador de cada estrato es censal.
import { readFileSync, writeFileSync, readdirSync, statSync } from "node:fs";
import { fileURLToPath } from "node:url";
import { join, relative, sep, basename } from "node:path";
import { extractText } from "../lib/extract.mjs";
import { getEncoding } from "js-tiktoken";
import { countTokens } from "@anthropic-ai/tokenizer";

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
const censo = [];
for (const l of readFileSync(R + "tok\\corpus-post-colapso.tsv", "utf8").split(/\r?\n/).slice(1)) {
  if (!l.trim()) continue;
  const c = l.split("\t");
  censo.push({ fqcn: c[0], chars: +c[1] });
}
if (censo.length !== 23498) { console.error("ABORTA: censo con " + censo.length + " clases"); process.exit(2); }

const ESTRATOS = [
  { id: "S1", lo: 0, hi: 2365, n: 80 },
  { id: "S2", lo: 2365, hi: 4116, n: 80 },
  { id: "S3", lo: 4116, hi: 6509, n: 120 },
  { id: "S4", lo: 6509, hi: 11964, n: 100 },
  { id: "S5", lo: 11964, hi: 30000, n: 100 },
  { id: "S6", lo: 30000, hi: Infinity, n: Infinity },
];
function mulberry32(a) { return function () { a |= 0; a = (a + 0x6D2B79F5) | 0; let t = Math.imul(a ^ (a >>> 15), 1 | a); t = (t + Math.imul(t ^ (t >>> 7), 61 | t)) ^ t; return ((t ^ (t >>> 14)) >>> 0) / 4294967296; }; }
const rnd = mulberry32(2606);

const rutas = new Map();
(function walk(d) {
  for (const n of readdirSync(d)) {
    const p = join(d, n);
    if (statSync(p).isDirectory()) { if (basename(p) !== "class-use") walk(p); }
    else if (n.endsWith(".html")) rutas.set(relative(DOC, p).replace(/\.html$/, "").split(sep).join("."), p);
  }
})(DOC);

const sel = [];
for (const e of ESTRATOS) {
  const pool = censo.filter((c) => c.chars >= e.lo && c.chars < e.hi);
  e.clases = pool.length;
  e.charsCenso = pool.reduce((a, c) => a + c.chars, 0);
  if (e.n === Infinity) { for (const c of pool) sel.push({ ...c, est: e.id }); e.n = pool.length; }
  else {
    const idx = pool.map((_, i) => i);
    for (let i = idx.length - 1; i > 0; i--) { const j = Math.floor(rnd() * (i + 1)); [idx[i], idx[j]] = [idx[j], idx[i]]; }
    for (const i of idx.slice(0, e.n)) sel.push({ ...pool[i], est: e.id });
  }
}
console.log("Muestra: " + sel.length + " clases (S6 censo de " + ESTRATOS[5].n + ").");

let sinRuta = 0, fallos = 0;
const med = [];
for (const s of sel) {
  const p = rutas.get(s.fqcn);
  if (!p) { sinRuta++; if (sinRuta <= 5) console.error("SIN RUTA: " + s.fqcn); continue; }
  const t = extractText(readFileSync(p, "utf8"), 10000000).text;
  if (t.length !== s.chars) { fallos++; if (fallos <= 5) console.error("CENSO DISCREPA " + s.fqcn + ": " + t.length + " vs " + s.chars); continue; }
  med.push({ fqcn: s.fqcn, est: s.est, chars: t.length, o200k: tik(t), anth: countTokens(t) });
}
if (sinRuta) { console.error("ABORTA: " + sinRuta + " clases sin ruta"); process.exit(2); }
if (fallos) { console.error("ABORTA: " + fallos + " clases discrepan del censo"); process.exit(2); }
console.log("Guardian de censo: " + med.length + "/" + sel.length + " al caracter.");

const p19 = new Map();
for (const l of readFileSync(R + "tok\\paginas-colapso.tsv", "utf8").split(/\r?\n/).slice(1)) {
  if (!l.trim()) continue;
  const c = l.split("\t");
  p19.set(c[0], { chars: +c[2], o200k: +c[3] });
}
let n19 = 0, f19 = 0;
for (const m of med) {
  const r = p19.get(m.fqcn.split(".").pop());
  if (!r) continue;
  n19++;
  if (r.chars !== m.chars || r.o200k !== m.o200k) { f19++; console.error("19P DISCREPA " + m.fqcn + ": " + m.chars + "/" + m.o200k + " vs " + r.chars + "/" + r.o200k); }
}
if (f19) { console.error("ABORTA: " + f19 + " de las 19 paginas no concuerdan"); process.exit(2); }
console.log("Enlace con las 19 paginas: " + n19 + " caidas en la muestra, 0 discrepancias.\n");

const out = ["fqcn\testrato\tchars\ttok_o200k\ttok_anth"];
for (const m of med) out.push([m.fqcn, m.est, m.chars, m.o200k, m.anth].join("\t"));
writeFileSync(R + "tok\\muestra-corpus.tsv", out.join("\n") + "\n", "utf8");

let numO = 0, numA = 0, denCenso = 0;
const facs = [];
console.log("estrato  clases  charsCenso     n   facO200k  facLegacy");
for (const e of ESTRATOS) {
  const g = med.filter((m) => m.est === e.id);
  const c = g.reduce((a, m) => a + m.chars, 0), o = g.reduce((a, m) => a + m.o200k, 0), t = g.reduce((a, m) => a + m.anth, 0);
  const fo = c / o, fa = c / t;
  facs.push(fo);
  numO += e.charsCenso / fo; numA += e.charsCenso / fa; denCenso += e.charsCenso;
  console.log(e.id.padEnd(9) + String(e.clases).padStart(6) + String(e.charsCenso).padStart(12) + String(g.length).padStart(6) + fo.toFixed(3).padStart(10) + fa.toFixed(3).padStart(11));
}
console.log("\nFACTOR DEL CORPUS (post-colapso a677897, compuesto por estratos, denominador censal " + denCenso + " chars):");
console.log("  o200k_base       : " + (denCenso / numO).toFixed(3) + "   [19 paginas 4.940 | banda 4.65-5.15]");
console.log("  anthropic legacy : " + (denCenso / numA).toFixed(3) + "   [19 paginas 4.755 | banda 4.40-5.00]");
console.log("  recorrido entre estratos: " + (((Math.max(...facs) / Math.min(...facs)) - 1) * 100).toFixed(1) + "%   [banda 2-15%]");
console.log("\nEscrito tok\\muestra-corpus.tsv");