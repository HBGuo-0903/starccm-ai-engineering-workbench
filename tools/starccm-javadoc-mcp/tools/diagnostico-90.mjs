import { readFileSync } from "node:fs";
import { join } from "node:path";
import { flattenToText, stripDownwardBlocks } from "../lib/extract.mjs";

const ROOT = process.env.STARCCM_DOC;
if (!ROOT) { console.error("ABORTA: STARCCM_DOC no definido"); process.exit(2); }
const REL = "star/base/neo/ClientServerObjectManager.html";

const DOWN_LABELS = ["All Known Implementing Classes", "All Known Subinterfaces", "Direct Known Subclasses"];
const ENDS = ["Field Summary","Method Summary","Nested Class Summary","Constructor Summary",
              "Field Detail","Method Detail","public interface","public class","public abstract class"];

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
  if (!raw.length) return { chars: 0, tramos: [] };
  raw.sort((a, b) => a.start - b.start);
  const m = [{ ...raw[0] }];
  for (let i = 1; i < raw.length; i++) {
    const last = m[m.length - 1];
    if (raw[i].start <= last.end) last.end = Math.max(last.end, raw[i].end);
    else m.push({ ...raw[i] });
  }
  return { chars: m.reduce((a, r) => a + (r.end - r.start), 0), tramos: m };
}

const html = readFileSync(join(ROOT, ...REL.split("/")), "utf8");
const text0 = flattenToText(html);
const text1 = flattenToText(stripDownwardBlocks(html).html);

const med = measure(text0, DOWN_LABELS);
const delta = text0.length - text1.length;

console.log("Clase: " + REL);
console.log("textChars antes   = " + text0.length);
console.log("textChars despues = " + text1.length);
console.log("delta real del parche       = " + delta);
console.log("down segun measure()        = " + med.chars);
console.log("discrepancia (down - delta) = " + (med.chars - delta));
console.log("tramos de measure(): " + JSON.stringify(med.tramos));
console.log("");

let p = 0;
while (p < text1.length && text0[p] === text1[p]) p++;
let k = 0;
while (k < text1.length - p && text0[text0.length - 1 - k] === text1[text1.length - 1 - k]) k++;
const q = text0.length - k;
console.log("region eliminada por el parche: [" + p + ", " + q + ")  longitud " + (q - p));
console.log("contigua: " + ((q - p) === delta ? "SI" : "NO -> el parche quita en varios trozos"));

const s = med.tramos[0].start;
const e = med.tramos[0].end;
console.log("tramo de measure()            : [" + s + ", " + e + ")  longitud " + (e - s));
console.log("");
console.log("--- contado por measure() ANTES del inicio del parche = " + (p - s) + " chars ---");
console.log(JSON.stringify(text0.slice(Math.min(s, p), Math.max(s, p))));
console.log("");
console.log("--- contado por measure() DESPUES del fin del parche = " + (e - q) + " chars ---");
console.log(JSON.stringify(text0.slice(Math.min(q, e), Math.max(q, e))));