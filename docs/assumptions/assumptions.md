# Annahmen

Dieser Abschnitt dokumentiert Annahmen, die während der Entwicklung
getroffen wurden, weil Anforderungen unklar oder nicht spezifiziert waren.

## Übersicht

| ID  | Annahme                                                                                                      | Begründung                                                                                                                     | Auswirkung                                                                          | Datum      | Autor  |
|-----|--------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------|------------|--------|
| A01 | Ein Modul wird immer im Kontext eines Studienprogramms angelegt und gehört dann auch fix an dieses Programm. | Theoretisch könnte ein Modul zu mehreren Degree Programs gehören. Wurde aber vom Dozent als Anforderung im Forum spezifiziert. | Das UML Klassendiagramm enthält eine 1:n Beziehung zwischen Degree Program und Study Module. | 25.05.2026 | Daniel |
| A02 | ...                                                                                                          |                                                                                                                                |                                                                                     |            |        |