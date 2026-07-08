# Report: Semantische Konsistenz-Analyse (Studyplanner)

Fokus ausschliesslich auf semantische Konsistenz (Bedeutungsebene). Rein syntaktische/mechanische Aspekte (Einrückung, Whitespace, Klammerstil, Import-Reihenfolge) sind bewusst ausgeklammert, da durch Spotless abgedeckt.

## 1. Namens-Konsistenz auf Bedeutungsebene

**Soll-Zustand (im gesamten `src/main` durchgängig eingehalten):**

| Konzept | Verbindliches Verb/Muster | Belegstellen (Auszug) |
|---|---|---|
| Neues Objekt anlegen (Service) | `create*` | `service/DegreeProgramService.java:16` `createDegreeProgram`, `service/StudyModuleService.java:13` `createStudyModule` |
| Persistenz-Upsert (Repository) | `save*` | `repository/DegreeProgramRepository.java:13`, `repository/StudyModuleRepository.java:13`, `repository/ScheduleRepository.java:8` — durchgängig `save*`, nie `insert`/`add`/`persist` |
| Lookup, Einzelobjekt/Liste (Repository **und** Service) | `find*` | `findDegreeProgramById`, `findAllDegreePrograms`, `findStudyModuleByKey`, `findAllStudyModulesByDegreeProgramId`, `findStudyModulesByKeys`, `findScheduleByDegreeProgramId` — nie `get`, `fetch`, `load`, `retrieve` für Lookups |
| Löschen | `delete*` | `deleteDegreeProgram`, `deleteStudyModule`, `deleteSchedule`, `deleteAllStudyModulesByDegreeProgramId` — nie `remove*`/`clear*` auf API-Ebene |
| Ändern | `update*` | `updateDegreeProgram`, `updateStudyModule` |
| Reiner Attribut-Zugriff | `get*`/`set*` (JavaBean) bzw. bare Namen bei Records | Model-/Form-Klassen: `getName()`, `setName(...)`; Views (Records): `name()`, `ects()` |
| Domain→DTO-Mapping | statische Factory `from(...)` | `DegreeProgramView.from`, `StudyModuleView.from`, `StudyModuleListView.from`, `ScheduleView.from`, `SemesterView.from` — ausnahmslos gleiches Muster |
| Klassen-Suffixe | `...Service`/`...ServiceImpl`, `...Repository`/`Json...Repository`, `...Controller`, `...Form`, `...View`, `...Exception`, `Abstract...` für Basisklassen | durchgängig, keine Ausnahme gefunden |

**Abweichungen: keine gefunden.** Diese Konvention ist bereits vollständig durchgesetzt und sollte 1:1 als verbindlicher Standard dokumentiert werden (z. B. in einer `CONTRIBUTING.md` oder im Klassendiagramm-Begleittext), damit sie bei weiteren Features (z. B. Kursblock 8 Filter/Sort) beibehalten wird.

## 2. Package-Struktur

**Soll-Zustand:** Durchgängig Layer-basierter Schnitt: `ui` (+ `ui.forms`, `ui.views`), `service`, `repository`, `model`, `exception`, `serialization`. Es gibt **keine** feature-basierten Pakete (z. B. kein `degreeprogram/`, `studymodule/`), also keine Vermischung der beiden Schnittprinzipien.

**Randbeobachtung (keine Abweichung):** `JacksonConfig.java` und `SampleDataInitializer.java` liegen im Root-Package `ch.ost.mas_se.oop.studyplanner`, neben `StudyplannerApplication`. Das ist idiomatisch für Spring-Boot-Bootstrap-/`@Configuration`-Klassen und passt ins Schema (keine Fach-Logik, daher kein Fach-Package nötig).

**Abweichungen: keine gefunden.**

## 3. null vs. Optional

**Soll-Zustand:** `Optional<T>` ausschliesslich als Rückgabetyp für "Lookup kann leer sein" (`find*ById`/`find*ByKey` in Repository **und** Service). `null` ausschliesslich für dokumentierte, optionale Entity-Attribute (z. B. `StudyModule.description`, `offeredFrom`, `offeredTo` — jeweils per Javadoc als "may be null" ausgewiesen, `model/StudyModule.java:19-26`).

**Positiv-Beispiel für korrekten Grenzübergang:** `repository/JsonScheduleRepository.java:45-49` — `Map.get()` liefert intern `null`, wird aber unmittelbar in `Optional.empty()`/`Optional.of(...)` übersetzt, bevor der Wert die Methode verlässt; das `null` leckt nie nach aussen.

Keine Stelle gefunden, an der `Optional` als Feld- oder Parametertyp verwendet wird (anerkanntes Java-Anti-Pattern) — auch das ist konsistent vermieden.

**Abweichungen: keine gefunden.** Empfehlung: aktuelle Handhabung explizit als Standard benennen (Optional nur an Lookup-Rückgabewerten von Service/Repository, null nur für dokumentierte optionale Felder).

## 4. Logging-Praxis

| Fundstelle | Beschreibung |
|---|---|
| `repository/AbstractJsonRepository.java:14,40,51` | Einzige Stelle im gesamten Produktivcode, die überhaupt loggt: SLF4J (`Logger`/`LoggerFactory`), `log.error("Failed to read/write {}", path, e)`. Log-Level `error` korrekt für echten IO-Fehler, Pattern parametrisiert (kein String-Concat) — für sich genommen vorbildlich. |
| Restlicher Code (`service/*`, `ui/*`, gesamtes UI-Orchestrierungslayer) | Kein einziger Log-Aufruf vorhanden — auch nicht an fachlich naheliegenden Stellen, z. B. `ui/ScheduleController.java:81-83`, wo eine `CyclicDependencyException` gefangen und nur als Flash-Message an den Nutzer weitergegeben wird, ohne serverseitige Protokollierung. |
| gesamtes `src/main` | Keine `System.out.println`/`printStackTrace`-Reste gefunden. |

**Beschreibung der Inkonsistenz:** Es gibt keine "falsche" Fassade oder falsches Log-Level — das Problem ist eine **Abdeckungslücke**: Von >20 Hauptklassen loggt praktisch nur eine. Damit existiert faktisch keine projektweite Logging-Konvention, sondern ein Einzelfall.

**Empfehlung:** SLF4J (`org.slf4j.Logger`) als einzige verwendete Fassade zum verbindlichen Standard erklären (Begründung: Spring-Boot-Standard, bereits vorhanden, keine Alternative im Code, kein zusätzlicher Abhängigkeitsaufwand). Zusätzlich projektweit festlegen, ob Service-/Controller-Schicht bei gefangenen fachlichen Fehlern (z. B. `CyclicDependencyException`) ebenfalls auf `warn`/`info`-Niveau loggen soll — aktuell landen solche Vorfälle ausschliesslich in der Flash-Message der UI und nirgends im Server-Log.

## 5. Weitere Muster-Inkonsistenzen

### 5.1 Records vs. klassische immutable POJOs für Werthalter

**Fundstelle:** `model/DegreeProgram.java`, `model/StudyModule.java`, `model/Schedule.java`, `model/Semester.java` (klassische Klassen mit `final`-Feldern, manuellem Konstruktor, manuellen Gettern, manuellem `equals`/`hashCode`) **vs.** `ui/views/DegreeProgramView.java`, `ui/views/StudyModuleView.java`, `ui/views/StudyModuleListView.java`, `ui/views/ScheduleView.java`, `ui/views/SemesterView.java` (alle als `record` implementiert).

**Beschreibung:** Zwei unterschiedliche Idiome für dasselbe Konzept — unveränderlicher Werthalter mit Validierung, aber ohne Mutationsbedarf. Model-Schicht nutzt das "alte" Muster (Klasse), View-Schicht bereits durchgängig Records.

**Empfehlung:** **Record als verbindlichen Standard für reine, unveränderliche Datenhalter** setzen (Begründung: Records sind im Projekt bereits etabliert und funktionieren erwiesenermassen auch mit Validierung im Konstruktor und Zusatzmethoden; die Aufgabenstellung (Kursblock 9) sieht explizit vor, "geeignete" Bestandsklassen zu Records zu migrieren). `DegreeProgram` und `Semester` sind unmittelbare Kandidaten (reine Daten + Konstruktor-Validierung, keine Mutation). Bei `StudyModule`/`Schedule` mit domänenspezifischen Zusatzmethoden (`withoutPrerequisite`, `getTotalSemesters`) ist die Umstellung ebenfalls möglich, sollte aber bewusst im Team entschieden werden.

### 5.2 Test-Coverage-Inkonsistenz zwischen Schichten

**Fundstelle:** `src/test/java/.../service/DegreeProgramServiceImplTest.java:1-5`, `ScheduleServiceImplTest.java:1-5`, `StudyModuleServiceImplTest.java:1-5`, `src/test/java/.../repository/JsonDegreeProgramRepositoryTest.java:1-5`, `JsonStudyModuleRepositoryTest.java:1-5`, `JsonScheduleRepositoryTest.java:1-5` (bewusst angelegt via Commits `d59046c "add empty service test classes"` und `07fa983 "add empty test classes for repository"`).

**Beschreibung:** Alle sechs Test-Klassen für Service- und Repository-Schicht sind leere Platzhalter (`class XyzTest {}`) mit einem ungenutzten Wildcard-Import `import static org.junit.jupiter.api.Assertions.*;` — keine einzige `@Test`-Methode vorhanden. Nur die drei Controller-Tests (`ui`-Paket: `DegreeProgramControllerTest`, `StudyModuleControllerTest`, `ScheduleControllerTest`) sind tatsächlich implementiert — durchgängig mit **AssertJ** (`assertThat`, `assertThatThrownBy`) + **Mockito** (`@Mock`, `@InjectMocks`, `MockitoExtension`) und einer konsistenten Namenskonvention `methodeUnterTest_szenario_erwartetesVerhalten` (z. B. `createStudyModule_withValidForm_redirectsToProgram`, `StudyModuleControllerTest.java`).

**Empfehlung:** **AssertJ + Mockito + das Namensschema `methode_szenario_ergebnis`** (wie bereits in den drei UI-Tests etabliert) als verbindlichen Standard für alle Schichten übernehmen; die JUnit-Jupiter-`Assertions`-Wildcard-Importe in den leeren Stubs sind Reste der IDE-Vorlage und sollten beim Befüllen ersetzt werden. Da dieselbe fachlich kritische Logik (Schedule-Berechnung/Topologische Sortierung, CRUD-Kaskaden) in Service und Repository aktuell ungetestet ist, ist dies der grösste Coverage-Gap im Projekt und direkt relevant für Bewertungskriterium 6 ("Testen: Manuell + Unit Tests") des Bewertungsrasters.

### 5.3 Exception-Handling

**Soll-Zustand:** Durchgängig *unchecked* (`RuntimeException`-Hierarchie). `CyclicDependencyException` (`exception/CyclicDependencyException.java:5`) folgt demselben Muster wie die generischen `RuntimeException`-Wraps in `AbstractJsonRepository.java:41,52`. Keine checked Exceptions, keine `throws`-Deklarationen für Custom-Typen gefunden.

**Abweichungen: keine gefunden.**

### 5.4 Konstruktions-Patterns

**Soll-Zustand:** Kein Builder-Pattern im Projekt. Klare, durchgehend eingehaltene Aufteilung nach Zweck: Konstruktor mit Validierung (Model), statische Factory `from(...)` (Views), JavaBean No-Arg-Konstruktor + Setter (Forms, für Spring-MVC-Data-Binding erforderlich).

**Abweichungen: keine gefunden.**

---

## Zusammenfassung

Das Projekt ist bei **Namensgebung, Package-Struktur, null/Optional-Handhabung, Exception-Stil und Konstruktions-Patterns bereits sehr konsistent** — hier sind keine Korrekturen notwendig, nur eine explizite Dokumentation der bestehenden Konventionen als verbindlicher Standard empfehlenswert.

Zwei echte Inkonsistenzen mit Handlungsbedarf:
1. **Logging** ist auf eine einzige Klasse beschränkt (`AbstractJsonRepository`) — keine falsche Praxis, aber keine projektweite Konvention.
2. **Records vs. klassische Klassen** für gleichartige Werthalter (Model vs. View) — sollte im Rahmen von Kursblock 9 vereinheitlicht werden.

Ein Coverage-Fund ausserhalb der reinen "Stil"-Frage, aber mit direktem Bezug zu Konsistenz der Testpraxis:
3. **Service- und Repository-Tests sind leere Stubs**, während UI-Tests bereits einem klaren, konsistenten Muster (AssertJ + Mockito + Namensschema) folgen, das auf die restlichen Schichten übertragen werden sollte.

---
Es wurden **keine Code-Änderungen vorgenommen** — dies ist ausschliesslich der angeforderte Report.
