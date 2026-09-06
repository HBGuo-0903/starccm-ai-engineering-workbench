# Esperados del factor por muestra estratificada del corpus (grupo C, redefinido)

Declarados antes de ejecutar. Sustrato: texto de `extractText` @ HEAD (colapso `a677897`).
Censo: `tok/corpus-post-colapso.tsv` @ `2980bc2` (23.498 clases, 134.600.475 chars).
Instrumento: `tools/muestra-factor.mjs`. Semilla fija 2606, generador mulberry32.

## Premisa corregida

El grupo C se redacto como "extender el factor al corpus entero: las 19 paginas no
incluyen las paginas de listas tipo Localizable". Esa premisa ya no es cierta: la
supresion de bloques (`f185c17`, 06.08) elimino esos bloques. `Localizable` mide 965
caracteres post-colapso, no 343.085. **No hay segunda poblacion.** Solo 2 clases de
23.498 tienen listas por encima del 25% de su texto, y suman 2.491 caracteres.

La pregunta viva es otra: las 19 paginas medidas promedian 20.573 caracteres y la
mediana del corpus es 5.207. **El 4,940 se midio sobre paginas grandes de trabajo, no
sobre una muestra del corpus.**

## Estratos (por textChars post-colapso) y muestra

S1 <2365 : 80 | S2 2365-4116 : 80 | S3 4116-6509 : 120 | S4 6509-11964 : 100
S5 11964-30000 : 100 | S6 >30000 : CENSO (33 clases)

El factor se estima por estrato y se agrega con los caracteres EXACTOS de cada estrato,
tomados del censo. Solo se estima el factor; el denominador nunca.

## Exactos (guardianes, abortan)

- cada clase muestreada mide exactamente los textChars del censo: 513/513 al caracter
- clases sin resolver: 0
- S6 es censo completo: 33 de 33
- toda clase de las 19 que caiga en la muestra coincide con tok/paginas-colapso.tsv

## Con banda

- factor o200k del corpus: punto 4.90, banda 4.65 - 5.15   (19 paginas: 4.940)
- factor legacy del corpus: punto 4.70, banda 4.40 - 5.00   (19 paginas: 4.755)
- factor S1 (las pequeñas): punto 4.85, banda 4.50 - 5.25
- factor S6 (censo): punto 4.85, banda 4.70 - 5.05
- recorrido entre estratos: punto 6%, banda 2% - 15%

SIN HIPOTESIS DIRECCIONAL. La evidencia registrada apunta a los dos lados: la zona de
navegacion trocea a 5,034 (por encima de la media de pagina) y pesa mas en paginas
pequeñas, pero WallBoundary, la menor de las 19 con 3.829 chars, da 4,878 (por debajo).
Banda simetrica alrededor del 4,940 conocido. Los fallos se registran, no se ajustan.