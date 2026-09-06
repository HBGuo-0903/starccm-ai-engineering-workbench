# Esperados del barrido de corpus post-colapso

Declarados antes de ejecutar. Sustrato: texto de `extractText` @ HEAD (colapso `a677897`).
Instrumento: `sweep-corpus-e.mjs`. Linea de base: `tok/corpus-pre-colapso.tsv` @ `4100dd0`
(barrido `d`, 23.498 clases, pre-colapso). `classify` copiado literal de `sweep-corpus-c2.mjs`.
Sin `measure()`: con `down_total = 0` en el barrido `d`, `netChars` es `textChars`.

## Exactos (guardianes, abortan)

- clases clasificadas: 23498 (mismo denominador que el barrido d, por construccion)
- clases sin resolver contra la linea de base: 0
- Simulation: 26898   (canario que encoge mucho, -10.3%)
- PartImportManager: 122083   (canario que encoge poco, -5.0%)
- Feature: 18060
- concordancia con tok/paginas-colapso.tsv: 19/19 al caracter

## Con banda

- clases sobre 20000: punto 125, banda 95 - 155   (pre-colapso: 196)
- clases sobre 30000: punto 30, banda 20 - 43   (pre-colapso: 43)
- clases sobre 50000: punto 11, banda 10 - 13   (pre-colapso: 13)
- reduccion total de caracteres del corpus: punto -7.5%, banda -5% a -11%

Derivacion de la banda de 20000: contando sobre la distribucion pre-colapso cuantas
clases quedan por encima de 20000/(1-x) para x entre 5% y 11% -> de 151 a 100.
La banda declarada la ensancha por los dos lados. Los fallos se registran, no se ajustan.

## Discrepancias que este barrido toca de paso

- 30000: STATUS dice 42, el resumen del barrido d dice 43. El 42 salia de `netChars`
  con `measure()` sesgado; el 43 es medida directa. Errata pendiente.
- Feature: STATUS dice "el minimo real es 20.031 (star.cadmodeler.Feature)". El fichero
  rescatado da 20.251 para Feature. Errata pendiente.