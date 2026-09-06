# Esperados de la remedida del factor post-colapso

Declarados antes de ejecutar. Sustrato: texto que entrega `extractText` @ `4207b23`
(colapso de blanco implementado en `a677897`). Instrumento: `tools/medir-tokens-colapso.mjs`.
Maquina: 2606 Build 21.04.007, Node v20.14.0, js-tiktoken 1.0.21, @anthropic-ai/tokenizer 0.0.4.
Denominador: las mismas 19 paginas del factor pre-colapso (17 clases unicas del banco
mas PartImportManager y Feature), texto completo, PRE-TOPE.

## Exactos (derivados de las columnas colapsadas de tok/paginas.tsv @ 6b6f248)

- chars 19 paginas: 390879
- trozos o200k 19 paginas: 79124
- factor o200k: 4.940   (pre-colapso: 4.827)
- 17 del banco: 250736 chars / 49836 trozos o200k -> factor 5.031   (pre-colapso: 4.854)
- recorrido o200k: 4.765 (PartImportManager) a 5.306 (FieldFunctionManager) = 11.4%
  (pre-colapso: 8.2%; el colapso invierte los extremos)
- chars entregados en el banco, 18 llamadas get_doc, tope 50000: 258714
- G1 control negativo: 17 fallos de 18 contra antes-20000/despues-50000; sobrevive solo PartManager
- G2 control positivo: 18/18 contra colapso-50000
- G3 idempotencia: 19/19 sin blanco colapsable
- G4 concordancia: 19/19 al caracter y al trozo contra las columnas colapsadas de 6b6f248

## Con banda (sin medida previa de ningun tipo)

- factor legacy 19 paginas: punto 4.58, banda 4.40 - 4.80   (pre-colapso: 4.430)
- factor legacy 17 del banco: punto 4.74, banda 4.55 - 4.95   (pre-colapso: 4.534)
- trozos o200k banco entregado: punto -10.1%, banda -6% a -13%   (registrado pre: 57157)
- trozos legacy banco entregado: punto -10.0%, banda -5% a -14%   (registrado pre: 61042)

Hipotesis tras el esperado legacy: la regla legacy cobraba el blanco mas caro, luego
gana mas con el colapso y la separacion entre reglas se estrecha desde el 8.96% actual.
Un legacy por debajo de 4.44 refuta la hipotesis. Los fallos se registran, no se ajustan.