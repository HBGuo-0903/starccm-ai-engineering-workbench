---
title: esperados - deduplicacion de flattenWithMap
tipo: esperados
carril: mcp
creado: 2026-08-20
base: 111c2fe
---

# Criterio de aceptacion - deduplicacion de flattenWithMap

Refactor sobre conducta verde. Se declara y se commitea ANTES de tocar codigo.

## Cambio

Extraer flattenWithMap a tools/flatten-map.mjs y sustituir las dos copias por un import.
Destino tools/, NO lib/extract.mjs: unificar ahi convertiria el guardian
"texto con mapa == extractText" en una comparacion entre dos ramas del mismo codigo.
Alcance del codigo versionado: 23 -> 24 ficheros .mjs.

## Fuente, verificada 2026-08-20 en 111c2fe

Delimitacion: desde "function flattenWithMap" hasta el primer salto-llave-salto posterior,
incluido. 921 chars = 921 bytes (ASCII puro), 26 lineas.
SHA256 F1A51A78FBD744EA5AA479215767775F63E9EFCEC65D74EB6380BF9366FBEB1C
Identico al caracter en tools/disecar-z6.mjs y tools/segmentar-pagina.mjs.
El modulo es ese extracto con el prefijo "export ". Sin dependencias: apply esta
anidada dentro y captura s y map por cierre.

## Custodia

Copia fisica en <respaldo fisico fuera del repo>, fuera del repo.
Bajo core.autocrlf true ningun hash sobrevive a git checkout: la custodia es la copia.

## Linea base verde en 111c2fe, medida 2026-08-20 y replicada dos veces

36EDB48C0D1A71F6DC86B55938A859CAEC7F7B57D12E40FC1AFD93F77AECE9BC  tok/z6-FieldFunctionManager.tsv  383 B
688739A0F7143C341DEAD7FF900F5CBFE01581C4279B87AC5DC267922BA78E1E  tok/z6-AutoMeshOperation.tsv  379 B
1A3EB2EB66C4FDEEEB19C90BD6D456DB54A50900E533BD8FF525552B8EF7B699  tok/segmentos-FieldFunctionManager.tsv  664 B
9FDB6B8FEEAAD14E2E487A681495D1F8F62B914A4EE1023DBDFD812996EF8786  tok/segmentos-AutoMeshOperation.tsv  652 B
3C19F0E8EEC1D16F6485F70D3B337E9A0790777FF772940D739AF3BE7706F1C6  disecar-z6 stdout  946 B
E3CAABE5CC170971E122C9E3BF76FB529890A454D1DEF04CBF308B0330041F17  segmentar-pagina stdout  1554 B

exit 0 y stderr 0 B en los dos. Primer fichero procesado: FieldFunctionManager.
Metodo de captura de stdout: redireccion de bytes con cmd /c. NO la captura de
PowerShell: ver la anomalia al final.

## Criterio de verde

1. Las cuatro .tsv regeneradas con el mismo SHA256 de arriba.
2. LastWriteTime de las cuatro movido. Comparar hashes sin escritura da verde en falso.
3. Los dos stdout con el mismo SHA256, capturados por redireccion de bytes.
4. exit 0 y stderr 0 B en los dos.
5. git status --porcelain vacio tras ejecutar.
6. Control negativo de procedencia: STARCCM_VER=2602 da exit 2, stdout 0 B, stderr 33 B.
7. Control negativo de alineamiento: perturbar una regla de blanco en el modulo da
   exit 2, stderr 67 B en disecar-z6 y 97 B en segmentar-pagina, y ninguna .tsv
   reescrita. Sin este control el verde solo acredita el camino feliz.

La prueba es SHA256 sobre disco. Nunca git diff.

## Orden de commits, uno por fichero, nunca git commit -a

1. tok/esperados-dedup-flatten.md, este fichero, antes de tocar codigo
2. tools/flatten-map.mjs
3. tools/disecar-z6.mjs, y ejecutar: segmentar-pagina sin tocar debe seguir
   reproduciendo sus dos custodias y su stdout
4. tools/segmentar-pagina.mjs

## Decisiones declaradas

- El modulo no lleva guardianes. No lee el corpus, luego no le toca STARCCM_DOC; no
  publica cifras, luego no le toca STARCCM_VER. Ponerselos seria rellenar una celda
  por analogia.
- Se escribe LF puro, sin BOM, como los dos instrumentos que lo importan.
- El caracter U+00B7 de disecar-z6 L175 no se toca. Cambiarlo alteraria el stdout y su
  hash: es cambio de conducta y va en su propia unidad de tratamiento.

## Anomalia registrada 2026-08-20

La captura de stdout por la consola de PowerShell 5.1 con chcp 437 corrompe el unico
caracter no-ASCII de disecar-z6, U+00B7, dos bytes en UTF-8: mas 4 B por aparicion, dos
apariciones, 954 B en vez de 946. segmentar-pagina es ASCII puro, sale identico por los
dos canales y sirve de control. Regla: un stdout cuyo hash se vaya a comparar se captura
por redireccion de bytes, nunca por la consola de PS.
