import { getEncoding } from "js-tiktoken";
import { countTokens } from "@anthropic-ai/tokenizer";
import { readFileSync } from "node:fs";
import { fileURLToPath } from "node:url";

const RAIZ = fileURLToPath(new URL("../node_modules/", import.meta.url));
const ver = (p) => { try { return JSON.parse(readFileSync(RAIZ + p + "\\package.json", "utf8")).version; } catch (e) { return "?"; } };

const PROSA = "Returns the simulation object that owns this report. The value is never null and is created when the simulation starts.";
const FIRMA = "public star.base.neo.NamedObject getPresentationObject(java.lang.String presentationName, boolean createIfMissing)";

let enc;
try { enc = getEncoding("o200k_base"); }
catch (e) { console.error("ABORTA: js-tiktoken no operativo: " + e.message); process.exit(2); }
const tik = (s) => enc.encode(s).length;

let anthOk = true;
try { countTokens(PROSA); } catch (e) { anthOk = false; console.error("AVISO: tokenizador Anthropic no operativo: " + e.message); }

if (tik(PROSA) !== tik(PROSA)) { console.error("ABORTA: js-tiktoken no determinista"); process.exit(2); }
if (anthOk && countTokens(PROSA) !== countTokens(PROSA)) { console.error("ABORTA: tokenizador Anthropic no determinista"); process.exit(2); }

console.log("js-tiktoken " + ver("js-tiktoken") + " / encoding o200k_base");
console.log("@anthropic-ai/tokenizer " + ver("@anthropic-ai\\tokenizer") + (anthOk ? "" : "  [NO OPERATIVO]"));
console.log("");
for (const [nombre, s] of [["PROSA", PROSA], ["FIRMA", FIRMA]]) {
  const c = s.length;
  const t1 = tik(s);
  console.log(nombre + ": " + c + " chars");
  console.log("   o200k_base : " + t1 + " trozos -> " + (c / t1).toFixed(3) + " chars/trozo");
  if (anthOk) { const t2 = countTokens(s); console.log("   anthropic  : " + t2 + " trozos -> " + (c / t2).toFixed(3) + " chars/trozo"); }
}