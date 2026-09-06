// sweep-corpus-e.mjs - recuento de clases sobre el tope, POST-COLAPSO (a677897)
// classify y el recorrido del arbol copiados literal de sweep-corpus-c2.mjs.
// Sin measure(): en el barrido d, down_total = 0, luego netChars == textChars.
// Uso: node sweep-corpus-e.mjs --out .\tok
import { readFileSync, readdirSync, statSync, writeFileSync, mkdirSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import { join, relative, sep, basename } from 'node:path';
import { extractText } from './lib/extract.mjs';

const argOf = (flag, def) => {
  const i = process.argv.indexOf(flag);
  return i !== -1 && process.argv[i + 1] ? process.argv[i + 1] : def;
};
const OUT = argOf('--out', './tok');
const CAP = parseInt(argOf('--cap', '20000'), 10);
const DOC = process.env.STARCCM_DOC;
const VER = process.env.STARCCM_VER ?? '(no declarada)';
if (!DOC) { console.error('ABORTA: STARCCM_DOC no esta definida.'); process.exit(2); }
if (!VER.startsWith('2606')) { console.error('ABORTA: version inesperada: ' + VER); process.exit(2); }

// canarios post-colapso, valores MEDIDOS (tok/paginas-colapso.tsv @ faed69d)
const CANARIOS = [
  { fqcn: 'star.common.Simulation', textChars: 26898 },
  { fqcn: 'star.meshing.PartImportManager', textChars: 122083 },
  { fqcn: 'star.cadmodeler.Feature', textChars: 18060 },
];

const SKIP_EXACT = new Set(['index.html','index-all.html','allclasses-index.html','allpackages-index.html','allclasses-frame.html','allclasses-noframe.html','allclasses.html','overview-summary.html','overview-tree.html','overview-frame.html','deprecated-list.html','help-doc.html','serialized-form.html','constant-values.html','search.html','system-properties.html','element-list.html','package-list.html']);
const SKIP_SUFFIX = ['package-summary.html','package-tree.html','package-use.html','package-frame.html','module-summary.html'];
const SKIP_DIR = new Set(['class-use','doc-files','legal','resources','script-dir','index-files','jquery','jquery-ui.overrides']);
function classify(absPath) {
  const parts = relative(DOC, absPath).split(sep);
  if (parts.some((p) => SKIP_DIR.has(p))) return 'directorio auxiliar';
  const file = basename(absPath);
  if (SKIP_EXACT.has(file)) return 'pagina de indice/navegacion';
  if (SKIP_SUFFIX.some((s) => file.endsWith(s))) return 'resumen de paquete/modulo';
  if (!/^[A-Z]/.test(file)) return 'inicial no mayuscula';
  if (parts.length < 2) return 'fuera de paquete';
  return null;
}
const fqcnOf = (p) => relative(DOC, p).replace(/\.html$/, '').split(sep).join('.');

const R = fileURLToPath(new URL('./', import.meta.url));
const pre = new Map();
for (const l of readFileSync(R + 'tok\\corpus-pre-colapso.tsv', 'utf8').split(/\r?\n/).slice(1)) {
  if (!l.trim()) continue;
  const c = l.split('\t');
  pre.set(c[0], +c[1]);
}
if (pre.size !== 23498) { console.error('ABORTA: linea de base con ' + pre.size + ' clases, esperaba 23498'); process.exit(2); }

const files = [];
(function walk(d) {
  for (const n of readdirSync(d)) {
    const p = join(d, n);
    if (statSync(p).isDirectory()) walk(p);
    else if (n.endsWith('.html')) files.push(p);
  }
})(DOC);

const rows = [];
let skipped = 0, errors = 0;
const t0 = Date.now();
for (const f of files) {
  let html;
  try { html = readFileSync(f, 'utf8'); } catch { errors++; continue; }
  if (classify(f)) { skipped++; continue; }
  let res;
  try { res = extractText(html, CAP); } catch { errors++; continue; }
  if (!res || typeof res.text !== 'string') { errors++; continue; }
  rows.push({ fqcn: fqcnOf(f), textChars: res.text.length });
}
const ms = Date.now() - t0;

if (rows.length !== 23498) { console.error('ABORTA: ' + rows.length + ' clases, esperaba 23498 (classify no es literal)'); process.exit(2); }
let faltan = 0;
for (const r of rows) if (!pre.has(r.fqcn)) { faltan++; if (faltan <= 5) console.error('SIN BASE: ' + r.fqcn); }
if (faltan) { console.error('ABORTA: ' + faltan + ' clases sin correspondencia en la linea de base'); process.exit(2); }
console.log('Denominador: 23498 clases, 0 sin correspondencia.');

let fc = 0;
for (const c of CANARIOS) {
  const r = rows.find((x) => x.fqcn === c.fqcn);
  if (!r || r.textChars !== c.textChars) { fc++; console.error('CANARIO FALLA ' + c.fqcn + ': ' + (r ? r.textChars : 'ausente') + ' vs ' + c.textChars); }
}
if (fc) { console.error('ABORTA: ' + fc + ' canarios fallan'); process.exit(2); }
console.log('Canarios: 3/3 al caracter (encoge mucho, encoge poco, y el caso frontera).');

const p19 = new Map();
for (const l of readFileSync(R + 'tok\\paginas-colapso.tsv', 'utf8').split(/\r?\n/).slice(1)) {
  if (!l.trim()) continue;
  const c = l.split('\t');
  p19.set(c[0], +c[2]);
}
let f19 = 0, v19 = 0;
for (const r of rows) {
  const simple = r.fqcn.split('.').pop();
  if (!p19.has(simple)) continue;
  v19++;
  if (p19.get(simple) !== r.textChars) { f19++; console.error('19P DISCREPA ' + r.fqcn + ': ' + r.textChars + ' vs ' + p19.get(simple)); }
}
if (f19) { console.error('ABORTA: ' + f19 + ' de las 19 paginas no concuerdan'); process.exit(2); }
console.log('Concordancia con las 19 paginas: ' + v19 + ' cotejadas, 0 discrepancias.');

const over = (t) => rows.filter((r) => r.textChars > t).length;
const sumPost = rows.reduce((a, r) => a + r.textChars, 0);
let sumPre = 0;
for (const v of pre.values()) sumPre += v;

console.log('\nBARRIDO POST-COLAPSO (a677897), ' + rows.length + ' clases, ' + ms + ' ms, errores ' + errors + ', descartados ' + skipped);
console.log('  sobre  20000 : ' + over(20000) + '   [pre 196, punto 125, banda 95-155]');
console.log('  sobre  30000 : ' + over(30000) + '   [pre 43, punto 30, banda 20-43]');
console.log('  sobre  40000 : ' + over(40000) + '   [pre 21]');
console.log('  sobre  50000 : ' + over(50000) + '   [pre 13, punto 11, banda 10-13]');
console.log('  sobre  60000 : ' + over(60000) + '   [pre 11]');
console.log('  sobre 100000 : ' + over(100000) + '   [pre 5]');
console.log('  caracteres totales: ' + sumPost + ' vs ' + sumPre + ' pre = ' + (((sumPost - sumPre) / sumPre) * 100).toFixed(1) + '%   [banda -5% a -11%]');

const sal = rows.filter((r) => pre.get(r.fqcn) > 20000 && r.textChars <= 20000);
console.log('  clases que CRUZAN el tope de 20000 a la baja: ' + sal.length);
for (const r of sal.slice(0, 10)) console.log('    ' + r.fqcn + ' ' + pre.get(r.fqcn) + ' -> ' + r.textChars);
if (sal.length > 10) console.log('    ... y ' + (sal.length - 10) + ' mas');

mkdirSync(OUT, { recursive: true });
writeFileSync(join(OUT, 'corpus-post-colapso.tsv'), 'fqcn\ttextChars\n' + rows.map((r) => r.fqcn + '\t' + r.textChars).join('\n') + '\n', 'utf8');
writeFileSync(join(OUT, 'corpus-post-colapso-summary.json'), JSON.stringify({
  generado: new Date().toISOString(), starccm_version: VER, node: process.version,
  colapso: 'a677897', barrido_ms: ms, clases: rows.length, descartados: skipped, errores: errors,
  umbrales: [20000,30000,40000,50000,60000,80000,100000].map((t) => ({ tope: t, clases: over(t) })),
  chars_total: sumPost, chars_total_pre: sumPre, cruzan_20000_a_la_baja: sal.length,
}, null, 2), 'utf8');
console.log('\nEscrito ' + OUT + '\\corpus-post-colapso.tsv y su summary');