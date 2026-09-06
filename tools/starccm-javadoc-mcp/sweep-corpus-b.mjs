// sweep-corpus-b.mjs — barrido con desglose de bloques de listas Javadoc
//
// Igual que sweep-corpus.mjs, pero anade tres columnas al TSV:
//   textChars   (sin cambios)
//   listChars   caracteres que ocupan los bloques de listas de clases
//   netChars    textChars - listChars
//
// Criterio de bloque:
//   Etiquetas de inicio:  All Known Implementing Classes, All Known Subinterfaces,
//                         All Superinterfaces, Direct Known Subclasses,
//                         All Implemented Interfaces
//   Etiquetas de fin:     Field Summary, Method Summary, Nested Class Summary,
//                         Constructor Summary, Field Detail, Method Detail,
//                         public interface, public class, public abstract class
//   Si no se encuentra fin -> el bloque no se cuenta (no se inventa)
//
// Uso:
//   node sweep-corpus-b.mjs --out .\salida-b
//   node sweep-corpus-b.mjs --cap 20000 --out .\salida-b

import { readFileSync, readdirSync, statSync, writeFileSync, mkdirSync } from 'node:fs';
import { join, relative, sep, basename } from 'node:path';

import { extractText } from './lib/extract.mjs';

// --- Regresion (mismo valor de referencia) ---------------------------------
const REGRESSION = {
  fqcn: 'star.common.Simulation',
  version: '2606',
  textChars: 29981,
  outCharsAt20000: 20016,
};

// --- Argumentos ------------------------------------------------------------
const argv = process.argv.slice(2);
const argOf = (flag, def) => {
  const i = argv.indexOf(flag);
  return i >= 0 && argv[i + 1] ? argv[i + 1] : def;
};

const DOC = process.env.STARCCM_DOC;
const VER = process.env.STARCCM_VER ?? 'no-declarada';
const CAP = Number(argOf('--cap', '20000'));
const OUT = argOf('--out', './salida-b');

if (!DOC) {
  console.error('ERROR: STARCCM_DOC no esta definida.');
  process.exit(1);
}
if (!Number.isInteger(CAP) || CAP <= 0) {
  console.error(`ERROR: --cap invalido: ${argOf('--cap', '20000')}`);
  process.exit(1);
}
mkdirSync(OUT, { recursive: true });

// --- Clasificacion de paginas (identica a sweep-corpus.mjs) ----------------
const SKIP_EXACT = new Set([
  'index.html', 'index-all.html', 'allclasses-index.html', 'allpackages-index.html',
  'allclasses-frame.html', 'allclasses-noframe.html', 'allclasses.html',
  'overview-summary.html', 'overview-tree.html', 'overview-frame.html',
  'deprecated-list.html', 'help-doc.html', 'serialized-form.html',
  'constant-values.html', 'search.html', 'system-properties.html',
  'element-list.html', 'package-list.html',
]);
const SKIP_SUFFIX = ['package-summary.html', 'package-tree.html', 'package-use.html',
  'package-frame.html', 'module-summary.html'];
const SKIP_DIR = new Set(['class-use', 'doc-files', 'legal', 'resources', 'script-dir',
  'index-files', 'jquery', 'jquery-ui.overrides']);

const skipped = [];

function classify(absPath) {
  const rel = relative(DOC, absPath);
  const parts = rel.split(sep);
  if (parts.some((p) => SKIP_DIR.has(p))) return 'directorio auxiliar';
  const file = basename(absPath);
  if (!file.endsWith('.html')) return 'no html';
  if (SKIP_EXACT.has(file)) return 'pagina de indice/navegacion';
  if (SKIP_SUFFIX.some((s) => file.endsWith(s))) return 'resumen de paquete/modulo';
  if (!/^[A-Z]/.test(file)) return 'inicial no mayuscula';
  if (parts.length < 2) return 'fuera de paquete';
  return null;
}

function* walk(dir) {
  for (const entry of readdirSync(dir)) {
    const abs = join(dir, entry);
    let st;
    try { st = statSync(abs); } catch { continue; }
    if (st.isDirectory()) {
      if (SKIP_DIR.has(entry)) { skipped.push([abs, 'directorio auxiliar']); continue; }
      yield* walk(abs);
    } else if (st.isFile() && entry.endsWith('.html')) {
      yield abs;
    }
  }
}

const fqcnOf = (absPath) =>
  relative(DOC, absPath).replace(/\.html$/, '').split(sep).join('.');

// --- Extraccion de bloques de listas ---------------------------------------
const LIST_LABELS = [
  'All Known Implementing Classes',
  'All Known Subinterfaces',
  'All Superinterfaces',
  'Direct Known Subclasses',
  'All Implemented Interfaces',
];

const LIST_ENDS = [
  'Field Summary',
  'Method Summary',
  'Nested Class Summary',
  'Constructor Summary',
  'Field Detail',
  'Method Detail',
  'public interface',
  'public class',
  'public abstract class',
];

function measureListBlocks(text) {
  // Encuentra todos los bloques con fin determinable y los fusiona si solapan.
  const raw = [];
  for (const label of LIST_LABELS) {
    const startIdx = text.indexOf(label);
    if (startIdx === -1) continue;

    // Primer encabezado de seccion tras la etiqueta
    let endIdx = -1;
    for (const endLabel of LIST_ENDS) {
      const idx = text.indexOf(endLabel, startIdx + label.length);
      if (idx !== -1 && (endIdx === -1 || idx < endIdx)) {
        endIdx = idx;
      }
    }
    if (endIdx === -1) continue; // fin no determinable: no se cuenta
    raw.push({ start: startIdx, end: endIdx });
  }

  if (raw.length === 0) return 0;

  // Fusionar rangos solapados para no contar dos veces
  raw.sort((a, b) => a.start - b.start);
  const merged = [{ ...raw[0] }];
  for (let i = 1; i < raw.length; i++) {
    const last = merged[merged.length - 1];
    if (raw[i].start <= last.end) {
      last.end = Math.max(last.end, raw[i].end);
    } else {
      merged.push({ ...raw[i] });
    }
  }

  return merged.reduce((sum, r) => sum + (r.end - r.start), 0);
}

// --- Barrido ---------------------------------------------------------------
const t0 = Date.now();
const rows = [];
const errors = [];

const overheads = new Map();
const tailSamples = [];
let flagDisagree = 0;

for (const abs of walk(DOC)) {
  const reason = classify(abs);
  if (reason) { skipped.push([abs, reason]); continue; }

  let html;
  try { html = readFileSync(abs, 'utf8'); } catch (e) { errors.push([abs, String(e)]); continue; }

  let res;
  try { res = extractText(html, CAP); } catch (e) { errors.push([abs, String(e)]); continue; }

  if (!res || typeof res.text !== 'string' || typeof res.out !== 'string') {
    errors.push([abs, `extractText no devolvio {text,out,truncated}: ${JSON.stringify(res)?.slice(0, 120)}`]);
    continue;
  }

  const textChars = res.text.length;
  const outChars = res.out.length;
  const truncated = Boolean(res.truncated);

  if (truncated !== textChars > CAP) flagDisagree++;

  if (truncated) {
    const over = outChars - Math.min(textChars, CAP);
    overheads.set(over, (overheads.get(over) ?? 0) + 1);
    if (tailSamples.length < 5) {
      tailSamples.push({ fqcn: fqcnOf(abs), cola: JSON.stringify(res.out.slice(-60)) });
    }
  }

  const listChars = measureListBlocks(res.text);
  const netChars = textChars - listChars;

  rows.push({ fqcn: fqcnOf(abs), htmlChars: html.length, textChars, outChars, truncated, listChars, netChars });
}

const elapsedMs = Date.now() - t0;

// --- Estadistica -----------------------------------------------------------
const n = rows.length;
if (n === 0) {
  console.error('ERROR: cero paginas de clase.');
  writeFileSync(join(OUT, 'corpus-skipped.txt'),
    skipped.map(([f, r]) => `${r}\t${f}`).join('\n'), 'utf8');
  process.exit(2);
}

const sortedText = rows.map((r) => r.textChars).sort((a, b) => a - b);
const sortedNet  = rows.map((r) => r.netChars).sort((a, b) => a - b);

const pctOf = (arr) => (p) =>
  arr[Math.min(arr.length - 1, Math.max(0, Math.ceil((p / 100) * arr.length) - 1))];

const pctText = pctOf(sortedText);
const pctNet  = pctOf(sortedNet);

const medianOf = (arr) =>
  arr.length % 2
    ? arr[(arr.length - 1) / 2]
    : Math.round((arr[arr.length / 2 - 1] + arr[arr.length / 2]) / 2);

const medianText = medianOf(sortedText);
const medianNet  = medianOf(sortedNet);
const totalText  = sortedText.reduce((a, b) => a + b, 0);

const overText = rows.filter((r) => r.textChars > CAP);
const overNet  = rows.filter((r) => r.netChars  > CAP);
const noise    = rows.filter((r) => r.textChars > CAP && r.netChars <= CAP);
const noList   = rows.filter((r) => r.listChars === 0);

const lostChars = overText.reduce((a, r) => a + (r.textChars - CAP), 0);

const EDGES = [0, 1000, 2000, 5000, 10000, 20000, 30000, 50000, 100000, Infinity];
const hist = EDGES.slice(0, -1).map((lo, i) => ({
  desde: lo, hasta: EDGES[i + 1] === Infinity ? null : EDGES[i + 1],
  clases_text: sortedText.filter((v) => v >= lo && v < EDGES[i + 1]).length,
  clases_net:  sortedNet.filter((v)  => v >= lo && v < EDGES[i + 1]).length,
}));

// --- Chequeo de regresion (textChars, sin cambios) -------------------------
const ref = rows.find((r) => r.fqcn === REGRESSION.fqcn);
let regression;
if (!ref) {
  regression = { estado: 'ausente', detalle: `${REGRESSION.fqcn} no aparecio en el barrido` };
} else if (VER !== REGRESSION.version) {
  regression = {
    estado: 'no-aplicable', esperado: REGRESSION.textChars, medido: ref.textChars,
    detalle: `referencia tomada en ${REGRESSION.version}; este corpus declara ${VER}`,
  };
} else if (ref.textChars === REGRESSION.textChars) {
  regression = {
    estado: 'coincide', esperado: REGRESSION.textChars, medido: ref.textChars,
    outChars: ref.outChars,
    nota_out: CAP === 20000
      ? (ref.outChars === REGRESSION.outCharsAt20000
        ? `out tambien coincide con el ${REGRESSION.outCharsAt20000} medido el 2026-08-03`
        : `ATENCION: out=${ref.outChars}, el 2026-08-03 fue ${REGRESSION.outCharsAt20000}`)
      : `tope ${CAP} distinto del 20000 de referencia; out no comparable`,
  };
} else {
  regression = {
    estado: 'DISCREPA', esperado: REGRESSION.textChars, medido: ref.textChars,
    detalle: 'la extraccion NO se comporta igual que antes de moverla a lib/extract.mjs',
  };
}

// --- Salidas ---------------------------------------------------------------
const sob = [...overheads.keys()];

const summary = {
  generado: new Date().toISOString(),
  starccm_version: VER,
  javadoc_root: DOC,
  node: process.version,
  tope_evaluado: CAP,
  barrido_ms: elapsedMs,
  clases: n,
  descartados: skipped.length,
  errores: errors.length,
  sin_bloques_de_listas: noList.length,
  textChars: {
    min: sortedText[0], p25: pctText(25), mediana: medianText, p75: pctText(75),
    p90: pctText(90), p95: pctText(95), p99: pctText(99), max: sortedText[n - 1],
    media: Math.round(totalText / n), total: totalText,
  },
  netChars: {
    min: sortedNet[0], p25: pctNet(25), mediana: medianNet, p75: pctNet(75),
    p90: pctNet(90), p95: pctNet(95), p99: pctNet(99), max: sortedNet[n - 1],
  },
  sobre_tope: {
    por_textChars: overText.length,
    por_netChars: overNet.length,
    solo_ruido: noise.length,
    porcentaje_ruido_sobre_overText: overText.length
      ? Number(((noise.length / overText.length) * 100).toFixed(1))
      : 0,
    caracteres_perdidos_textChars: lostChars,
    top20_por_netChars: [...overNet].sort((a, b) => b.netChars - a.netChars).slice(0, 20)
      .map((r) => ({ fqcn: r.fqcn, textChars: r.textChars, listChars: r.listChars, netChars: r.netChars })),
  },
  recorte: {
    sobrante_por_clase: Object.fromEntries([...overheads.entries()].sort((a, b) => a[0] - b[0])),
    colas: tailSamples,
    banderas_incoherentes: flagDisagree,
  },
  histograma: hist,
  regresion: regression,
};

writeFileSync(
  join(OUT, 'corpus-textchars.tsv'),
  'fqcn\thtmlChars\ttextChars\toutChars\ttruncated\tlistChars\tnetChars\n' +
  rows.map((r) =>
    `${r.fqcn}\t${r.htmlChars}\t${r.textChars}\t${r.outChars}\t${r.truncated}\t${r.listChars}\t${r.netChars}`
  ).join('\n') + '\n',
  'utf8',
);
writeFileSync(join(OUT, 'corpus-summary.json'), JSON.stringify(summary, null, 2) + '\n', 'utf8');
writeFileSync(join(OUT, 'corpus-skipped.txt'),
  skipped.map(([f, r]) => `${r}\t${f}`).join('\n') + '\n', 'utf8');
if (errors.length) {
  writeFileSync(join(OUT, 'corpus-errors.txt'),
    errors.map(([f, e]) => `${f}\t${e}`).join('\n') + '\n', 'utf8');
}

// --- Informe por consola ---------------------------------------------------
console.log(`corpus            ${VER}   (${DOC})`);
console.log(`clases medidas    ${n}   (descartados ${skipped.length}, errores ${errors.length})`);
console.log('');
console.log(`textChars  min ${sortedText[0]} | mediana ${medianText} | p99 ${pctText(99)} | max ${sortedText[n - 1]}`);
console.log(`netChars   min ${sortedNet[0]}  | mediana ${medianNet}  | p99 ${pctNet(99)}  | max ${sortedNet[n - 1]}`);
console.log('');
console.log(`sobre ${CAP} (textChars)   ${overText.length} clases`);
console.log(`sobre ${CAP} (netChars)    ${overNet.length} clases  <- genuinamente largas`);
console.log(`solo ruido (textChars>${CAP}, netChars<=${CAP})   ${noise.length} clases (${summary.sobre_tope.porcentaje_ruido_sobre_overText}% de las que superaban el tope)`);
console.log(`sin bloques de listas       ${noList.length} clases (${((noList.length / n) * 100).toFixed(1)}% del corpus)`);
console.log('');
console.log(`texto perdido     ${lostChars} caracteres`);
console.log(`recorte           sobrante ${sob.length ? sob.join('/') : 'n/a'} caracteres; banderas incoherentes ${flagDisagree}`);
if (tailSamples.length) console.log(`                  cola ejemplo: ${tailSamples[0].cola}`);
console.log(`regresion         ${regression.estado}${regression.nota_out ? ' — ' + regression.nota_out : ''}`);
console.log('');
console.log(`top 20 por netChars:`);
summary.sobre_tope.top20_por_netChars.forEach((r, i) => {
  console.log(`  ${String(i + 1).padStart(2)}. ${r.fqcn.padEnd(60)} text=${r.textChars} list=${r.listChars} net=${r.netChars}`);
});
console.log('');
console.log(`barrido en ${elapsedMs} ms -> ${OUT}`);
