// sweep-corpus-c.mjs - desglose de bloques por direccion en la jerarquia
// Igual que sweep-corpus-b.mjs, pero parte listChars en dos:
//   upChars    bloques que miran hacia ARRIBA (se conservan por criterio)
//   downChars  bloques que miran hacia ABAJO  (se suprimen por criterio)
//   netChars   textChars - downChars   <- solo resta lo que el criterio quita
// Uso: node sweep-corpus-c.mjs --out .\salida-c
import { readFileSync, readdirSync, statSync, writeFileSync, mkdirSync } from 'node:fs';
import { join, relative, sep, basename } from 'node:path';
import { extractText } from './lib/extract.mjs';

const argOf = (flag, def) => {
  const i = process.argv.indexOf(flag);
  return i !== -1 && process.argv[i + 1] ? process.argv[i + 1] : def;
};
const OUT = argOf('--out', './salida-c');
const CAP = parseInt(argOf('--cap', '20000'), 10);
const DOC = process.env.STARCCM_DOC;
const VER = process.env.STARCCM_VER ?? '(no declarada)';
if (!DOC) { console.error('ERROR: STARCCM_DOC no esta definida.'); process.exit(1); }

const REGRESSION = { fqcn: 'star.common.Simulation', version: '2606', textChars: 29981 };

const UP_LABELS   = ['All Implemented Interfaces', 'All Superinterfaces'];
const DOWN_LABELS = ['All Known Implementing Classes', 'All Known Subinterfaces', 'Direct Known Subclasses'];
const ENDS = ['Field Summary','Method Summary','Nested Class Summary','Constructor Summary',
              'Field Detail','Method Detail','public interface','public class','public abstract class'];

function measure(text, labels) {
  const raw = [];
  for (const label of labels) {
    const s = text.indexOf(label);
    if (s === -1) continue;
    let e = -1;
    for (const end of ENDS) {
      const i = text.indexOf(end, s + label.length);
      if (i !== -1 && (e === -1 || i < e)) e = i;
    }
    if (e === -1) continue;
    raw.push({ start: s, end: e });
  }
  if (!raw.length) return { chars: 0, undet: 0 };
  raw.sort((a, b) => a.start - b.start);
  const m = [{ ...raw[0] }];
  for (let i = 1; i < raw.length; i++) {
    const last = m[m.length - 1];
    if (raw[i].start <= last.end) last.end = Math.max(last.end, raw[i].end);
    else m.push({ ...raw[i] });
  }
  return { chars: m.reduce((a, r) => a + (r.end - r.start), 0), undet: 0 };
}
function countUndetermined(text, labels) {
  let u = 0;
  for (const label of labels) {
    const s = text.indexOf(label);
    if (s === -1) continue;
    let e = -1;
    for (const end of ENDS) {
      const i = text.indexOf(end, s + label.length);
      if (i !== -1 && (e === -1 || i < e)) e = i;
    }
    if (e === -1) u++;
  }
  return u;
}

const files = [];
(function walk(d) {
  for (const n of readdirSync(d)) {
    const p = join(d, n);
    if (statSync(p).isDirectory()) walk(p);
    else if (n.endsWith('.html')) files.push(p);
  }
})(DOC);

const isClassPage = (p, html) => {
  const b = basename(p);
  if (/^(index|overview|allclasses|package|constant|serialized|deprecated|help|search)/i.test(b)) return false;
  return /class="type-signature"|public (final |abstract |static )*(class|interface|enum|record)/.test(html);
};
const fqcnOf = (p) => relative(DOC, p).replace(/\.html$/, '').split(sep).join('.');

const rows = [];
let skipped = 0, errors = 0, undetTotal = 0;
const t0 = Date.now();
for (const f of files) {
  let html;
  try { html = readFileSync(f, 'utf8'); } catch { errors++; continue; }
  if (!isClassPage(f, html)) { skipped++; continue; }
  let res;
  try { res = extractText(html, CAP); } catch { errors++; continue; }
  if (!res || typeof res.text !== 'string') { errors++; continue; }
  const textChars = res.text.length;
  const up   = measure(res.text, UP_LABELS).chars;
  const down = measure(res.text, DOWN_LABELS).chars;
  undetTotal += countUndetermined(res.text, [...UP_LABELS, ...DOWN_LABELS]);
  rows.push({ fqcn: fqcnOf(f), textChars, upChars: up, downChars: down, netChars: textChars - down });
}
const ms = Date.now() - t0;

const reg = rows.find((r) => r.fqcn === REGRESSION.fqcn);
const regOk = VER.startsWith(REGRESSION.version)
  ? (reg && reg.textChars === REGRESSION.textChars ? 'OK' : 'FALLO')
  : 'no-aplicable';
if (regOk === 'FALLO') {
  console.error(`REGRESION FALLIDA: ${REGRESSION.fqcn} esperado ${REGRESSION.textChars}, medido ${reg?.textChars}`);
  process.exit(2);
}

mkdirSync(OUT, { recursive: true });
writeFileSync(join(OUT, 'corpus-blocks.tsv'),
  'fqcn\ttextChars\tupChars\tdownChars\tnetChars\n' +
  rows.map((r) => `${r.fqcn}\t${r.textChars}\t${r.upChars}\t${r.downChars}\t${r.netChars}`).join('\n') + '\n', 'utf8');

const over = (k, t) => rows.filter((r) => r[k] > t).length;
writeFileSync(join(OUT, 'corpus-blocks-summary.json'), JSON.stringify({
  generado: new Date().toISOString(), starccm_version: VER, javadoc_root: DOC,
  node: process.version, barrido_ms: ms, clases: rows.length, descartados: skipped,
  errores: errors, bloques_sin_fin_determinable: undetTotal, regresion: regOk,
  sobre_tope_textChars: over('textChars', CAP),
  sobre_tope_netChars_solo_abajo: over('netChars', CAP),
  umbrales: [20000,30000,40000,50000,60000,80000,100000].map((t) => ({ tope: t, clases: over('netChars', t) })),
  up_total: rows.reduce((a, r) => a + r.upChars, 0),
  down_total: rows.reduce((a, r) => a + r.downChars, 0),
}, null, 2), 'utf8');

console.log(`clases ${rows.length} | descartados ${skipped} | errores ${errors} | ${ms} ms`);
console.log(`regresion: ${regOk}`);
console.log(`bloques sin fin determinable: ${undetTotal}`);
console.log(`sobre ${CAP} por textChars           ${over('textChars', CAP)}`);
console.log(`sobre ${CAP} suprimiendo solo ABAJO  ${over('netChars', CAP)}   <- el numero que importa`);
console.log(`up_total ${rows.reduce((a,r)=>a+r.upChars,0)} | down_total ${rows.reduce((a,r)=>a+r.downChars,0)}`);
