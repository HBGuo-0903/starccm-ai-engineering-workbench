import { readdirSync, readFileSync } from "node:fs";
import { join } from "node:path";
import { extractText } from "../lib/extract.mjs";
import { getEncoding } from "js-tiktoken";
import { countTokens } from "@anthropic-ai/tokenizer";

const ROOT = process.env.STARCCM_DOC;
const VER = process.env.STARCCM_VER || "";
if (!ROOT) { console.error("ABORTA: STARCCM_DOC no definido"); process.exit(2); }
if (!VER.startsWith("2606")) { console.error("ABORTA: version inesperada: " + VER); process.exit(2); }
// Guardian de reproduccion del colapso: columnas colapsadas de tok/paginas.tsv @ 6b6f248
const ESPERADOS = {
  "FieldFunctionManager.html": { chars: 15187, o200k: 2862 },
  "AutoMeshOperation.html":    { chars: 13401, o200k: 2745 },
};
function fail(msg){ console.error("GUARDIAN: " + msg); process.exit(2); }

const hits = [];
function walk(dir){
  for (const e of readdirSync(dir, { withFileTypes: true })) {
    if (e.isDirectory() && e.name === "class-use") continue;
    const p = join(dir, e.name);
    if (e.isDirectory()) walk(p);
    else if (Object.keys(ESPERADOS).includes(e.name)) hits.push(p);
  }
}
walk(ROOT);
for (const t of Object.keys(ESPERADOS)) {
  const n = hits.filter(p => p.endsWith("\\" + t)).length;
  if (n !== 1) fail(t + ": esperado 1 fichero tras excluir class-use, encontrados " + n);
}

const enc = getEncoding("o200k_base");
for (const file of hits.sort()) {
  const nombre = file.split("\\").pop();
  const esp = ESPERADOS[nombre];
  const res = extractText(readFileSync(file, "utf8"), Number.MAX_SAFE_INTEGER);
  if (/[\n\t]/.test(res.text)) fail(nombre + ": el texto colapsado contiene saltos o tabuladores");
  const o = enc.encode(res.text).length;
  const a = countTokens(res.text);
  if (res.text.length !== esp.chars || o !== esp.o200k)
    fail(nombre + ": reproduccion del colapso rota. chars=" + res.text.length + "/" + esp.chars +
         " o200k=" + o + "/" + esp.o200k);
  console.log(nombre + "  chars=" + res.text.length + "  o200k=" + o +
              "  legacy=" + a + " (legacy: primera medida, sin guardian)  OK");
}
console.log("Colapso verificado: reproduce las columnas colapsadas de tok/paginas.tsv en las dos paginas.");