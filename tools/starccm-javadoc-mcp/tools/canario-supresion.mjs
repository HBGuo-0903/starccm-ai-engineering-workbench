import { readFileSync } from "node:fs";
import { join } from "node:path";
import { extractText, flattenToText, stripDownwardBlocks } from "../lib/extract.mjs";

const ROOT = process.env.STARCCM_DOC;
if (!ROOT) { console.error("ABORTA: STARCCM_DOC no definido"); process.exit(2); }
const TOPE = 20000;

const CASOS = [
  { fqcn: "star.base.neo.ClientServerObjectManager",
    rel: "star/base/neo/ClientServerObjectManager.html",
    papel: "CANARIO", antes: 26167, despues: 15360, bloques: 1, truncaEnTope: false },
  { fqcn: "star.common.Simulation",
    rel: "star/common/Simulation.html",
    papel: "CONTROL DELTA CERO", antes: 29981, despues: 29981, bloques: 0, truncaEnTope: true },
];

let fallos = 0;
console.log("Javadoc: " + ROOT);
console.log("Tope de comprobacion: " + TOPE + "\n");

for (const c of CASOS) {
  const html = readFileSync(join(ROOT, ...c.rel.split("/")), "utf8");
  const antes = flattenToText(html).length;
  const r = extractText(html, Number.MAX_SAFE_INTEGER);
  const enTope = extractText(html, TOPE);
  const poda = stripDownwardBlocks(html);

  const check = (etiqueta, medido, esperado) => {
    const ok = medido === esperado;
    if (!ok) fallos++;
    console.log("   " + (ok ? "PASA" : "FALLA") + "  " + etiqueta +
                ": medido=" + medido + " esperado=" + esperado);
  };

  console.log(c.fqcn + "   [" + c.papel + "]");
  check("texto antes de suprimir", antes, c.antes);
  check("texto despues de suprimir", r.text.length, c.despues);
  check("bloques suprimidos", r.blocksRemoved, c.bloques);
  check("trunca al tope " + TOPE, enTope.truncated, c.truncaEnTope);
  console.log("   delta de la supresion = " + (antes - r.text.length));
  console.log("   entregado al tope = " + enTope.out.length + " chars");
  console.log("   anomalias de marcado = " + poda.anomalies + "\n");
}

if (fallos > 0) { console.error("CANARIO: " + fallos + " comprobacion(es) FALLAN."); process.exit(2); }
console.log("CANARIO: todo pasa.");
