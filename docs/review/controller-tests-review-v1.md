# Report: Controller-/API-Test-Review (Studyplanner)

## 1. Zuordnung Controller ↔ Testklasse

| Controller | Datei | Testklasse | Anzahl `@Test`-Methoden |
|---|---|---|---|
| `WelcomeController` | `ui/WelcomeController.java` | **keine** | 0 |
| `DegreeProgramController` | `ui/DegreeProgramController.java` | `ui/DegreeProgramControllerTest.java` | 10 |
| `StudyModuleController` | `ui/StudyModuleController.java` | `ui/StudyModuleControllerTest.java` | 10 |
| `ScheduleController` | `ui/ScheduleController.java` | `ui/ScheduleControllerTest.java` | 3 |

`AbstractController` (gemeinsame Basisklasse mit `requireDegreeProgram`) hat keine eigene Testklasse — wird nur implizit über die drei Subklassen-Tests mitgetestet.

## 2. Globaler Befund: Getestete Ebene

**Keine der drei Testklassen ist ein echter Web-Layer-Test.** Es gibt im gesamten `src/test` **keine** Nutzung von `@WebMvcTest`, `MockMvc`, `@SpringBootTest` oder `TestRestTemplate` (verifiziert per Volltextsuche). Stattdessen wird der Controller in allen drei Klassen per `@InjectMocks` als gewöhnliches Java-Objekt instanziiert und die Methoden werden direkt aufgerufen (`testee.showDetails(id, model)` statt eines echten HTTP-Requests).

Konkrete Konsequenzen dieses Testansatzes:

- **Keine echten HTTP-Statuscodes werden geprüft.** Alle Assertions beziehen sich auf den zurückgegebenen View-Namen als `String` (`"redirect:/degree-programs"`, `"degreeprograms/form"`) oder auf den Inhalt von `ConcurrentModel`/`RedirectAttributesModelMap` — nie auf einen tatsächlichen `200`/`302`/`404`/`400`-Statuscode, wie ihn ein echter Request liefern würde.
- **`@Valid`/Bean-Validation wird nie tatsächlich ausgeführt.** `BindingResult` ist in allen Tests ein reiner Mockito-Mock, dessen `hasErrors()` manuell auf `true`/`false` gesetzt wird. Die eigentlichen Constraint-Annotationen auf den Form-Klassen (`@NotBlank`, `@Min(1)`, `@AssertTrue` in `StudyModuleForm.isOfferedToAfterOfferedFrom()`) werden dadurch **von keinem einzigen Test** wirklich ausgewertet. Würde z. B. `@NotBlank` auf `StudyModuleForm.name` versehentlich entfernt, würde das kein Test bemerken.
- **URL-Routing/Path-Mapping wird nicht geprüft.** Ob `@PostMapping("/{degreeProgramId}/deleteDegreeProgram")` tatsächlich unter der erwarteten URL erreichbar ist, wird nirgends verifiziert (reine Methodenaufrufe kennen keine Pfade).
- **Serialisierung/Rendering wird nicht geprüft.** Da nie ein echter Response-Body erzeugt wird, ist unklar, ob die Thymeleaf-Templates mit den im Model gesetzten Attributen tatsächlich fehlerfrei rendern.
- **Positiv:** Die Service-Schicht ist in allen drei Klassen sauber über `@Mock`/`@InjectMocks` mit `MockitoExtension` isoliert — es gibt keine Anbindung an echte Business-Logik, Repository oder Dateisystem. Insofern sind es **saubere, isolierte Controller-Unit-Tests**, nur eben keine Web-Layer-/Integrationstests.

**Empfehlung:** Mindestens für einen repräsentativen Endpunkt pro Controller (idealerweise für die 404- und 400-Pfade) einen echten `@WebMvcTest`+`MockMvc`-Test ergänzen, der reale HTTP-Statuscodes, Content-Type und (bei Validierungsfehlern) das tatsächliche Verhalten der Bean-Validation prüft. Die bestehenden Mockito-Tests können als schnelle Unit-Test-Ebene daneben bestehen bleiben.

## 3. Lücken pro Controller

### `WelcomeController`
- **Keine Testklasse vorhanden.** Der einzige Endpunkt (`GET /`, `welcome(Model)`, `ui/WelcomeController.java:18-22`) ist zu 0 % getestet — auch nicht der triviale Fall (`programCount` korrekt im Model gesetzt).

### `DegreeProgramController`
- `showEditForm` (`ui/DegreeProgramController.java:91-102`) — **kein Test für unbekannte `degreeProgramId`** (löst intern `requireDegreeProgram` → 404 aus, analog zu `showDetails_withUnknownId`, aber für `showEditForm` fehlt das Pendant).
- `updateDegreeProgram` (`ui/DegreeProgramController.java:108-125`) — **ruft `requireDegreeProgram` gar nicht auf**, bevor `degreeProgramService.updateDegreeProgram(...)` aufgerufen wird. Es existiert kein Test, der ein Update mit unbekannter ID durchführt — dadurch ist unentdeckt geblieben, dass ein Update auf eine nicht existierende ID aktuell **keinen 404 auslöst**, sondern über den Upsert-Mechanismus in `DegreeProgramServiceImpl.updateDegreeProgram`/`JsonDegreeProgramRepository.saveDegreeProgram` still ein neuer Datensatz mit dieser ID angelegt wird. Das ist entweder ein Bug oder eine bewusste Design-Entscheidung — so oder so aktuell nicht durch einen Test abgesichert oder dokumentiert.
- `deleteDegreeProgram` (`ui/DegreeProgramController.java:128-136`) — kein Test für unbekannte `degreeProgramId` (löst `requireDegreeProgram` → 404 aus, ungetestet).
- Keine Tests verifizieren per `verify(...)`, **mit welchen Argumenten** die Services aufgerufen werden (z. B. `createDegreeProgram_withValidForm_...` prüft nie, dass `degreeProgramService.createDegreeProgram("Software Engineering", null, 0)` tatsächlich mit diesen Werten aufgerufen wurde) — nur Rückgabewert/Flash-State werden geprüft, nicht die Interaktion selbst.
- Keine Grenzwert-/Edge-Case-Tests für `maxDurationInSemesters` (z. B. `0`, negative Werte) — da `BindingResult` immer gemockt ist, würde die reale `@Min(1)`-Constraint nie ausgelöst; es gibt auch keinen Test, der zumindest das Verhalten bei einem an sich ungültigen, aber "als gültig simulierten" Grenzwert dokumentiert.

### `StudyModuleController`
- **Duplicate-Key-Ablehnung ist komplett ungetestet** (`ui/StudyModuleController.java:57-65`): Der Zweig, der bei bereits existierendem `studyModuleKey` innerhalb desselben Studiengangs `result.rejectValue("studyModuleKey", "duplicate", ...)` aufruft und die Erstellung verweigert, hat keinen einzigen Test. Das ist die wichtigste im vorherigen Architektur-Review identifizierte Geschäftsregel im Controller — und sie ist zugleich die am wenigsten abgesicherte.
- `showCreateForm` (`ui/StudyModuleController.java:34-39`) — kein Test für unbekannte `degreeProgramId` (indirekt über `showCreateFormView` → `requireDegreeProgram` → 404).
- `showEditForm` (`ui/StudyModuleController.java:108-127`) — kein Test für unbekannte `degreeProgramId` (nur `studyModuleKey`-404 ist über `showDetails_withUnknownStudyModuleKey` sinngemäss, aber nicht für `showEditForm` selbst, abgedeckt).
- `deleteStudyModule` (`ui/StudyModuleController.java:165-177`) — kein Test für unbekannten `studyModuleKey` (löst `requireStudyModule` → 404 aus, ungetestet).
- **`@AssertTrue isOfferedToAfterOfferedFrom()`-Regel** (`ui/forms/StudyModuleForm.java:90-94`) wird von keinem Test auch nur indirekt erwähnt — es gibt keinen Test, der ein Szenario mit `offeredTo` vor `offeredFrom` simuliert.
- Keine Tests verifizieren die tatsächlich an den Service übergebenen Werte (`prerequisiteKeys`, `offeredFrom`, `offeredTo`, `ects`) — z. B. würde ein Copy-&-Paste-Fehler, der `offeredFrom` und `offeredTo` vertauscht, von keinem Test entdeckt.
- `addAvailableStudyModulesForPrerequisiteSelection` (`ui/StudyModuleController.java:217-225`, insbesondere der Ausschluss des aktuell bearbeiteten Moduls aus der Prerequisite-Auswahl) wird nur indirekt über `showEditForm`/`showCreateForm` mitgetestet, nie mit einer Modulliste, die tatsächlich mehrere Kandidaten enthält (aktuelle Tests nutzen durchgängig `List.of()`).

### `ScheduleController`
- **`CyclicDependencyException`-Pfad ist komplett ungetestet** (`ui/ScheduleController.java:74-85`): Es gibt keinen Test, der `scheduleService.calculateAndSaveSchedule(...)` eine `CyclicDependencyException` werfen lässt und prüft, dass `errorMessage` (statt `successMessage`) als Flash-Attribut gesetzt wird. Das ist exakt das in der Aufgabenstellung (Kursblock 6, inkl. UI-Screenshot mit Fehlerbanner) explizit geforderte Verhalten — und der am stärksten sicherheitsrelevante/fachlich wichtigste Zweig im gesamten Controller-Set ist zu 0 % abgedeckt.
- `showSchedule` (`ui/ScheduleController.java:44-54`) — kein Test für unbekannte `degreeProgramId` (löst `requireDegreeProgram` → 404 aus, ungetestet).
- `calculateSchedule` (`ui/ScheduleController.java:74-85`) — kein Test für unbekannte `degreeProgramId`.
- **"Happy Path mit echten Daten" ist ungetestet**: Beide bestehenden `showSchedule`-Tests verwenden ausschliesslich leere Listen (`List.of()`) für Module/Semester. Die eigentliche Kernlogik von `SemesterView.from(...)` — Auflösung der Modul-Keys zu echten `StudyModule`-Objekten über die `moduleMap` und Berechnung von `totalEcts` — wird dadurch nie mit realen, mehrelementigen Daten durchlaufen. Ein Bug in der ECTS-Summierung oder im Key-Lookup würde von keinem Test entdeckt.
- Keine Verifikation, dass `scheduleService.calculateAndSaveSchedule(program)` mit dem korrekten `DegreeProgram`-Objekt aufgerufen wird (nur Rückgabewert/Flash-State geprüft).

## 4. Übergreifende Beobachtungen (Response-Contract, Validierung, Test-Qualität)

- **Response-Contract:** In keinem der drei Test-Files wird ein echter HTTP-Statuscode geprüft (kein `MockMvc.andExpect(status().is...())`). 404-Fälle werden nur als Java-Exception-Typ (`ResponseStatusException`) via `assertThatThrownBy` verifiziert, nie als tatsächliche `404`-Response. 400/Validierungsfehler werden nie über echte HTTP-Requests ausgelöst, sondern nur simuliert, indem `bindingResult.hasErrors()` manuell auf `true` gesetzt wird.
- **Input-Validierung & Edge Cases:** Durchgängig schwach. Es gibt keinen Test, der tatsächlich leere/malformte Werte durch die reale Bean-Validation schickt; alle "invalid form"-Tests simulieren nur das Ergebnis (`hasErrors() == true`), nie die Ursache.
- **Test-Qualität:**
  - Namenskonvention (`methode_szenario_ergebnis`) ist über alle drei Klassen konsistent und aussagekräftig (bestätigt den Befund aus dem Semantic-Consistency-Review).
  - Assertions sind grösstenteils aussagekräftig für Struktur (`isEqualTo(view)`, `isInstanceOf(...)`), aber häufig oberflächlich für Inhalt (`isNotNull()` statt konkreter Werte, z. B. `showDetails_returnsDetailsViewWithProgramAndStudyModules` prüft nur, dass `studyModules` nicht `null` ist, nicht was darin enthalten ist).
  - **Keine einzige `verify(mock).method(...)`-Interaktionsprüfung** in allen drei Testklassen — es wird nie geprüft, dass der Controller die Services mit den korrekten Argumenten aufruft, nur dass ein plausibles Ergebnis zurückkommt.
  - Keine Test-Smells wie geteilter Zustand zwischen Tests oder Kontrollfluss-Logik (`if`/Schleifen) im Testcode gefunden — Tests sind durchgängig linear (Arrange/Act/Assert), jede Testmethode erzeugt frisches `ConcurrentModel`/`RedirectAttributesModelMap`.
  - Isolation der Service-Schicht ist sauber (Mockito, kein Zugriff auf echtes Repository/Dateisystem).

## 5. Top 3 wichtigste fehlende Testfälle

| # | Testklasse | Vorgeschlagener Methodenname | Zu prüfende Bedingung |
|---|---|---|---|
| 1 | `ScheduleControllerTest` | `calculateSchedule_withCyclicDependency_setsErrorFlashMessageInsteadOfSuccess` | **Input:** `scheduleService.calculateAndSaveSchedule(program)` wirft `CyclicDependencyException(List.of("M1", "M2"))`. **Erwartet:** Rückgabewert bleibt `"redirect:/degree-programs/" + PROGRAM_ID + "/schedule"` (kein Rethrow aus dem Controller); `redirectAttributes.getFlashAttributes()` enthält `errorMessage` mit dem Exception-Text und **nicht** `successMessage`. Bei einem echten `MockMvc`-Test zusätzlich: HTTP `302/303` mit Flash-Scope statt `200`. |
| 2 | `StudyModuleControllerTest` | `createStudyModule_withDuplicateKeyInSameProgram_rejectsAndReturnsFormViewWithoutCreating` | **Input:** `studyModuleService.findStudyModuleByKey(PROGRAM_ID, "OOP1")` liefert `Optional.of(existingModule)`, Formular übermittelt denselben Key `"OOP1"`. **Erwartet:** Rückgabewert ist `"studymodules/form"` (kein Redirect); `bindingResult`/`result` erhält `rejectValue("studyModuleKey", "duplicate", ...)` (per `verify`); `studyModuleService.createStudyModule(...)` wird **nie** aufgerufen (`verify(studyModuleService, never()).createStudyModule(any(), any(), any(), any(), anyInt(), any(), any(), any())`). Bei `MockMvc`: HTTP `200` mit Formular + Fehlermeldung statt `302`. |
| 3 | `DegreeProgramControllerTest` | `updateDegreeProgram_withUnknownDegreeProgramId_throwsNotFoundException` | **Input:** `degreeProgramService.findDegreeProgramById("unknown-id")` liefert `Optional.empty()`, `bindingResult.hasErrors()` = `false`. **Erwartet:** Aufruf von `testee.updateDegreeProgram("unknown-id", form, bindingResult, model, redirectAttributes)` wirft `ResponseStatusException` mit Status `404`, **und** `degreeProgramService.updateDegreeProgram(...)` wird nie aufgerufen. *Hinweis:* Dieser Test wird mit dem aktuellen Code voraussichtlich **fehlschlagen**, da `updateDegreeProgram` derzeit keinen `requireDegreeProgram`-Aufruf enthält (siehe Lücke oben) — das Schreiben dieses Tests deckt vermutlich einen echten, bisher unbemerkten Bug auf, der vor bzw. zusammen mit dem Test behoben werden sollte. |

Als weitere hochprioritäre Kandidaten (nicht in den Top 3, aber empfehlenswert kurz danach): `showSchedule_withRealSemesterData_resolvesStudyModulesAndComputesTotalEcts` (schliesst die "nur leere Listen getestet"-Lücke in `ScheduleControllerTest`) sowie mindestens ein `WelcomeControllerTest`.

---
Es wurden **keine Code-/Test-Änderungen vorgenommen** — dies ist ausschliesslich der angeforderte Plan/Report.
