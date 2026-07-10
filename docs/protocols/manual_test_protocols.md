# Test Protokolle

Dieses Dokument dokumentiert Manuelle Tests, die unmittelbar nach dem letzten Commit der Entwicklung
durchgeführt wurden.


## Übersicht


| ID  | Testfall                        | Vorbedingung                                   | Schritte                                                                                                       | Erwartetes Ergebnis                                                            | Tatsächliches Ergebnis | Status | Datum      | Tester |
|-----|---------------------------------|------------------------------------------------|----------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------|------------------------|--------|------------|--------|
| T01 | Degree Program anlegen          | Anwendung gestartet, keine Programme vorhanden | 1. `/degree-programs` öffnen<br>2. „New" klicken<br>3. Name „BSc Data Science", Max. Duration „6"<br>4. „Save" | Programm erscheint in der Liste, Erfolgsmeldung wird angezeigt                 | wie erwartet           | ✅      | 10.07.2026 | Daniel |
| T02 | Degree Program mit leerer Dauer | Formular „New Degree Program" geöffnet         | 1. Name „Test"<br>2. Max. Duration leer lassen<br>3. „Save"                                                    | Formular wird erneut angezeigt, Fehlermeldung beim Feld Max. Duration          | wie erwartet           | ✅      | 10.07.2026 | Daniel |
| T03 | Degree Program mit Dauer 0      | Formular „New Degree Program" geöffnet         | 1. Name „Test"<br>2. Max. Duration „0"<br>3. „Save"                                                            | Browser blockiert Submit, Hinweis „Value must be greater than or equal to 1"   | wie erwartet           | ✅      | 10.07.2026 | Daniel |
| T04 | ...                             | ...                                            | ...                                                                                                            | ...                                                                            | ...                    | ...    | ...        | ...    |
| T05 | ...                             | ...                                            | ...                                                                                                            | ...                                                                            | ...                    | ...    | ...        | ...    |
| T06 | ...                             | ...                                            | ...                                                                                                            | ...                                                                            | ...                    | ...    | ...        | ...    |
| T07 | ...                             | ...                                            | ...                                                                                                            | ...                                                                            | ...                    | ...    | ...        | ...    |
| T08 | ...                             | ...                                            | ...                                                                                                            | ...                                                                            | ...                    | ...    | ...        | ...    |
| T09 | ...                             | ...                                            | ...                                                                                                            | ...                                                                            | ...                    | ...    | ...        | ...    |
| T10 | ...                             | ...                                            | ...                                                                                                            | ...                                                                            | ...                    | ...    | ...        | ...    |


## Details
> **Hintergrund zu T01:**
