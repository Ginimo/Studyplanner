# Plan: Service-Test-Ausbau (Studyplanner)

Status quo: `DegreeProgramServiceImplTest`, `StudyModuleServiceImplTest`, `ScheduleServiceImplTest` sind aktuell leere Stub-Klassen (`class XyzTest {}`, kein `@Test`) — die gesamte Service-Schicht ist zu **0 %** getestet. Dieser Plan schlägt konkrete Testfälle vor. **Es wurde noch kein Testcode geschrieben** — dies ist ausschliesslich der angeforderte Plan zur Freigabe.

## 1. Bestandsaufnahme: Öffentliche Methoden je Service

### `DegreeProgramService` / `DegreeProgramServiceImpl`
*(Dependencies: `DegreeProgramRepository`, `StudyModuleRepository`, `ScheduleRepository`)*

| Methode | Getestet? |
|---|---|
| `createDegreeProgram(name, description, maxDurationInSemesters): DegreeProgram` | ❌ nicht getestet |
| `findDegreeProgramById(id): Optional<DegreeProgram>` | ❌ nicht getestet |
| `findAllDegreePrograms(): List<DegreeProgram>` | ❌ nicht getestet |
| `updateDegreeProgram(id, name, description, maxDurationInSemesters): void` | ❌ nicht getestet |
| `deleteDegreeProgram(id): void` | ❌ nicht getestet |

### `StudyModuleService` / `StudyModuleServiceImpl`
*(Dependencies: `StudyModuleRepository`, `ScheduleRepository`)*

| Methode | Getestet? |
|---|---|
| `createStudyModule(key, degreeProgramId, name, description, ects, prerequisiteKeys, offeredFrom, offeredTo): void` | ❌ nicht getestet |
| `findStudyModuleByKey(degreeProgramId, key): Optional<StudyModule>` | ❌ nicht getestet |
| `findAllStudyModulesByDegreeProgramId(degreeProgramId): List<StudyModule>` | ❌ nicht getestet |
| `findStudyModulesByKeys(degreeProgramId, keys): List<StudyModule>` | ❌ nicht getestet |
| `updateStudyModule(key, degreeProgramId, name, description, ects, prerequisiteKeys, offeredFrom, offeredTo): void` | ❌ nicht getestet |
| `deleteStudyModule(degreeProgramId, key): void` | ❌ nicht getestet |

### `ScheduleService` / `ScheduleServiceImpl`
*(Dependencies: `StudyModuleRepository`, `ScheduleRepository`; `calculateSchedule` selbst ist **pure**, benötigt keine Mocks)*

| Methode | Getestet? |
|---|---|
| `calculateSchedule(allModules): Schedule` (Kahn's-Algorithmus, Kernlogik) | ❌ nicht getestet |
| `calculateAndSaveSchedule(program): void` | ❌ nicht getestet |
| `findScheduleByDegreeProgramId(id): Optional<Schedule>` | ❌ nicht getestet |
| `deleteSchedule(id): void` | ❌ nicht getestet |

**Hinweis zu `@NotBlank`/`@Min`-Constraints auf den Service-Interfaces:** Diese Bean-Validation-Annotationen greifen nur, wenn die `@Validated`-Klasse hinter einem Spring-AOP-Proxy aufgerufen wird (z. B. in einem `@SpringBootTest`/Slice-Test). Reine Mockito-Unit-Tests (`new StudyModuleServiceImpl(mockRepo, ...)`) lösen diese Validierung **nicht** aus. Der Plan unten testet deshalb stattdessen konsequent die **Domain-Invarianten in den Konstruktoren** von `StudyModule`/`DegreeProgram` (die auch ohne Spring-Kontext feuern, z. B. `ects < 1` → `IllegalArgumentException`), da dies der tatsächlich mit reinen Unit-Tests erreichbare Schutzwall ist.

## 2. Testfall-Vorschläge

### 2.1 `ScheduleServiceImpl.calculateSchedule(List<StudyModule>)` — höchste Priorität

| Testmethode | Given | When | Then | Mocks |
|---|---|---|---|---|
| `calculateSchedule_withAssignmentExampleData_producesFiveSemesterPlanMatchingSpec` | Die 11 Module aus dem Aufgabenblatt (Math, OO, DB1, DB2, AD1, CPI, Thesis, SE1, SE2, UI1, UI2) mit exakt den dort angegebenen Prerequisites | `calculateSchedule(allModules)` wird aufgerufen | Ergebnis hat genau 5 Semester: S1={Math,OO}, S2={DB1,AD1,CPI}, S3={DB2,SE1,UI1}, S4={SE2,UI2}, S5={Thesis} (Reihenfolge innerhalb eines Semesters unabhängig, Mengenvergleich) | keine (pure Methode) |
| `calculateSchedule_withNoPrerequisites_placesAllModulesInFirstSemester` | 3 Module ohne jegliche Prerequisites | `calculateSchedule(...)` | Ergebnis hat genau 1 Semester mit allen 3 Modulen | keine |
| `calculateSchedule_withLinearChain_placesEachModuleInOwnSemester` | A→B→C (B braucht A, C braucht B) | `calculateSchedule(...)` | 3 Semester, je 1 Modul, in Reihenfolge A, B, C | keine |
| `calculateSchedule_withDiamondDependency_placesJoinModuleAfterBothBranches` | A und B benötigen beide D; C benötigt sowohl A als auch B (Diamant-Form) | `calculateSchedule(...)` | Semester 1={D}, Semester 2={A,B}, Semester 3={C} — C erscheint erst, wenn **beide** Zweige erfüllt sind | keine |
| `calculateSchedule_withDirectCycle_throwsCyclicDependencyException` | Modul A benötigt B, Modul B benötigt A | `calculateSchedule(...)` | wirft `CyclicDependencyException`; Exception-Message enthält beide Modul-Keys (`A`, `B`) | keine |
| `calculateSchedule_withIndirectCycle_throwsCyclicDependencyExceptionWithAllCycleMembers` | A→B→C→A (3-elementiger Zyklus), plus ein unabhängiges Modul D ohne Prerequisites | `calculateSchedule(...)` | wirft `CyclicDependencyException`; Message enthält A, B, C, **nicht** D (D wäre korrekt platzierbar gewesen) | keine |
| `calculateSchedule_withDanglingPrerequisiteReference_ignoresUnknownKeyAndPlacesModuleImmediately` | Modul A hat `prerequisiteKeys = ["NICHT_EXISTENT"]`, sonst keine Module im Programm | `calculateSchedule(List.of(A))` | A landet trotzdem in Semester 1 (unbekannte Prerequisite-Keys werden laut Javadoc ignoriert, kein Fehler) | keine |
| `calculateSchedule_withEmptyModuleList_returnsScheduleWithZeroSemesters` | leere Liste `List.of()` | `calculateSchedule(List.of())` | Rückgabe ist ein `Schedule` mit `getTotalSemesters() == 0` und leerer `getSemesters()`-Liste | keine |
| `calculateSchedule_withModuleRequiringMultiplePrerequisitesAtDifferentDepths_waitsForLatestPrerequisite` | C benötigt A (Tiefe 1) und B (Tiefe 2, B benötigt A) | `calculateSchedule(...)` | C erscheint erst in Semester 3 (nach A **und** B), nicht bereits in Semester 2 nur weil A schon erfüllt war | keine |

### 2.2 `ScheduleServiceImpl.calculateAndSaveSchedule(DegreeProgram)` — hohe Priorität

| Testmethode | Given | When | Then | Mocks |
|---|---|---|---|---|
| `calculateAndSaveSchedule_withValidModules_savesComputedScheduleForProgram` | `studyModuleRepository.findAllStudyModulesByDegreeProgramId(id)` liefert 2 unabhängige Module | `calculateAndSaveSchedule(program)` | `scheduleRepository.saveSchedule(eq(id), scheduleCaptor.capture())` wird genau einmal aufgerufen; das erfasste `Schedule` hat 1 Semester mit beiden Modulen | `StudyModuleRepository`, `ScheduleRepository` |
| `calculateAndSaveSchedule_withCyclicDependency_propagatesExceptionAndNeverSaves` | `findAllStudyModulesByDegreeProgramId` liefert 2 Module mit zirkulärer Abhängigkeit (A↔B) | `calculateAndSaveSchedule(program)` | wirft `CyclicDependencyException`; `scheduleRepository.saveSchedule(...)` wird **nie** aufgerufen (`verify(scheduleRepository, never())...`) | `StudyModuleRepository`, `ScheduleRepository` |
| `calculateAndSaveSchedule_withNoModulesInProgram_savesEmptySchedule` | `findAllStudyModulesByDegreeProgramId` liefert `List.of()` | `calculateAndSaveSchedule(program)` | `saveSchedule` wird mit einem `Schedule` aufgerufen, dessen `getTotalSemesters() == 0` ist | `StudyModuleRepository`, `ScheduleRepository` |

### 2.3 `StudyModuleServiceImpl.deleteStudyModule` — hohe Priorität (Datenintegrität)

| Testmethode | Given | When | Then | Mocks |
|---|---|---|---|---|
| `deleteStudyModule_moduleIsPrerequisiteOfOthers_removesDanglingPrerequisiteReferences` | Programm hat Modul B (keine Prereqs) und Modul A (Prereq: `["B"]`); `findAllStudyModulesByDegreeProgramId` liefert `[A, B]` | `deleteStudyModule(programId, "B")` | `replaceAllStudyModulesForProgram(programId, captor.capture())` wird mit einer Liste aufgerufen, die nur noch A enthält, **und** A's `prerequisiteKeys` ist jetzt leer (nicht mehr `["B"]`) | `StudyModuleRepository`, `ScheduleRepository` |
| `deleteStudyModule_afterDeletion_invalidatesExistingSchedule` | beliebiges Modul vorhanden | `deleteStudyModule(programId, key)` | `scheduleRepository.deleteSchedule(programId)` wird genau einmal aufgerufen | `StudyModuleRepository`, `ScheduleRepository` |
| `deleteStudyModule_withUnknownKey_leavesModuleListUnchangedAndDoesNotThrow` | Programm hat Module `[A, B]`, keines davon hat den Key `"UNKNOWN"` | `deleteStudyModule(programId, "UNKNOWN")` | Methode wirft **keine** Exception (kein Existenz-Check auf Service-Ebene); `replaceAllStudyModulesForProgram` wird mit einer Liste aufgerufen, die weiterhin A und B unverändert enthält (dokumentiert bewusstes No-Op-Verhalten) | `StudyModuleRepository`, `ScheduleRepository` |

### 2.4 `StudyModuleServiceImpl.findStudyModulesByKeys` — mittlere Priorität

| Testmethode | Given | When | Then | Mocks |
|---|---|---|---|---|
| `findStudyModulesByKeys_withMatchingAndUnknownKeys_returnsOnlyMatchesAndSkipsUnknown` | Programm hat Module `[A, B, C]`; Aufruf mit `["A", "C", "NICHT_VORHANDEN"]` | `findStudyModulesByKeys(programId, keys)` | Ergebnis enthält genau A und C (2 Elemente), kein Fehler wegen des unbekannten Keys | `StudyModuleRepository` |
| `findStudyModulesByKeys_withEmptyKeyList_returnsEmptyList` | Programm hat Module `[A, B]`; Aufruf mit `List.of()` | `findStudyModulesByKeys(programId, List.of())` | Ergebnis ist leer | `StudyModuleRepository` |
| `findStudyModulesByKeys_withDuplicateKeysInInput_returnsEachMatchOnlyOnce` | Programm hat Modul A; Aufruf mit `["A", "A"]` (Duplikat) | `findStudyModulesByKeys(programId, List.of("A","A"))` | Ergebnis enthält A genau einmal (interne `Set`-Nutzung dedupliziert) | `StudyModuleRepository` |

### 2.5 `StudyModuleServiceImpl.createStudyModule` / `updateStudyModule` — mittlere Priorität

| Testmethode | Given | When | Then | Mocks |
|---|---|---|---|---|
| `createStudyModule_withValidData_savesModuleAndInvalidatesExistingSchedule` | gültige Parameter (Key, Name, ECTS=4, keine Prereqs) | `createStudyModule(...)` | `studyModuleRepository.saveStudyModule(captor.capture())` erfasst ein `StudyModule` mit den übergebenen Werten; `scheduleRepository.deleteSchedule(degreeProgramId)` wird aufgerufen (Business-Regel: neues Modul macht bestehenden Plan ungültig) | `StudyModuleRepository`, `ScheduleRepository` |
| `createStudyModule_withEctsBelowOne_throwsIllegalArgumentException` | `ects = 0` | `createStudyModule(..., 0, ...)` | wirft `IllegalArgumentException` (Domain-Invariante aus `StudyModule`-Konstruktor, nicht die `@Min`-Annotation) | `StudyModuleRepository`, `ScheduleRepository` (werden nicht aufgerufen, da Exception vor dem Save-Aufruf entsteht) |
| `createStudyModule_withOfferedToBeforeOfferedFrom_throwsIllegalArgumentException` | `offeredFrom = 2027-01-01`, `offeredTo = 2026-01-01` | `createStudyModule(...)` | wirft `IllegalArgumentException` (Domain-Invariante) | `StudyModuleRepository`, `ScheduleRepository` |
| `createStudyModule_withNullPrerequisiteKeys_storesModuleWithEmptyPrerequisiteList` | `prerequisiteKeys = null` | `createStudyModule(..., null, ...)` | gespeichertes `StudyModule.getPrerequisiteKeys()` ist eine leere Liste, kein `NullPointerException` | `StudyModuleRepository`, `ScheduleRepository` |
| `updateStudyModule_withValidData_replacesModuleAndInvalidatesSchedule` | bestehendes Modul, neue Werte (z. B. anderer Name) | `updateStudyModule(...)` | `saveStudyModule` erfasst ein `StudyModule` mit den **neuen** Werten (vollständiger Ersatz, kein Merge mit altem Zustand); `deleteSchedule` wird aufgerufen | `StudyModuleRepository`, `ScheduleRepository` |

### 2.6 `DegreeProgramServiceImpl.deleteDegreeProgram` — mittlere Priorität (Kaskade)

| Testmethode | Given | When | Then | Mocks |
|---|---|---|---|---|
| `deleteDegreeProgram_cascadesToStudyModulesAndSchedule` | beliebige `degreeProgramId` | `deleteDegreeProgram(id)` | alle drei Aufrufe erfolgen genau einmal mit der korrekten `id`: `studyModuleRepository.deleteAllStudyModulesByDegreeProgramId(id)`, `scheduleRepository.deleteSchedule(id)`, `degreeProgramRepository.deleteDegreeProgram(id)` | `DegreeProgramRepository`, `StudyModuleRepository`, `ScheduleRepository` |

### 2.7 `DegreeProgramServiceImpl` — Basis-CRUD (Happy Path + Grenzfälle) — niedrigere Priorität

| Testmethode | Given | When | Then | Mocks |
|---|---|---|---|---|
| `createDegreeProgram_withValidData_generatesIdAndSavesProgram` | Name "BSc Informatik", Beschreibung, `maxDurationInSemesters=6` | `createDegreeProgram(...)` | Rückgabewert hat nicht-leere, generierte `degreeProgramId`, passende Name/Description/Duration; `degreeProgramRepository.saveDegreeProgram(...)` wurde mit demselben Objekt aufgerufen | `DegreeProgramRepository` |
| `createDegreeProgram_withMaxDurationZero_throwsIllegalArgumentException` | `maxDurationInSemesters = 0` | `createDegreeProgram(...)` | wirft `IllegalArgumentException` (Domain-Invariante aus `DegreeProgram`-Konstruktor) | `DegreeProgramRepository` |
| `findDegreeProgramById_withExistingId_returnsProgram` | Repository liefert `Optional.of(program)` | `findDegreeProgramById(id)` | Rückgabe ist genau dieses `Optional` | `DegreeProgramRepository` |
| `findDegreeProgramById_withUnknownId_returnsEmptyOptional` | Repository liefert `Optional.empty()` | `findDegreeProgramById("unknown")` | Rückgabe ist `Optional.empty()` | `DegreeProgramRepository` |
| `findAllDegreePrograms_delegatesToRepositoryAndReturnsResult` | Repository liefert Liste mit 2 Programmen | `findAllDegreePrograms()` | Rückgabe ist exakt diese Liste (Passthrough) | `DegreeProgramRepository` |
| `updateDegreeProgram_replacesExistingProgramFieldsCompletely` | bestehendes Programm, neue Werte | `updateDegreeProgram(id, newName, newDesc, newDuration)` | `saveDegreeProgram` erfasst ein `DegreeProgram` mit `id` unverändert, aber Name/Description/Duration auf die neuen Werte gesetzt (vollständiger Ersatz) | `DegreeProgramRepository` |

### 2.8 Einfache Delegationsmethoden (`findByKey`, `findAll*`, `findScheduleByDegreeProgramId`, `deleteSchedule`) — niedrigste Priorität

| Testmethode | Given | When | Then | Mocks |
|---|---|---|---|---|
| `findStudyModuleByKey_delegatesToRepository` | Repository liefert `Optional.of(module)` bzw. `Optional.empty()` (2 Testfälle) | `findStudyModuleByKey(programId, key)` | Rückgabe entspricht 1:1 dem Repository-Ergebnis | `StudyModuleRepository` |
| `findAllStudyModulesByDegreeProgramId_delegatesToRepository` | Repository liefert Liste mit 3 Modulen | `findAllStudyModulesByDegreeProgramId(programId)` | Rückgabe ist exakt diese Liste | `StudyModuleRepository` |
| `findScheduleByDegreeProgramId_delegatesToRepository` | Repository liefert `Optional.of(schedule)` bzw. `Optional.empty()` | `findScheduleByDegreeProgramId(programId)` | Rückgabe entspricht 1:1 dem Repository-Ergebnis | `ScheduleRepository` |
| `deleteSchedule_delegatesToRepository` | — | `deleteSchedule(programId)` | `scheduleRepository.deleteSchedule(programId)` wird genau einmal aufgerufen | `ScheduleRepository` |

## 3. Priorisierung (Gesamtübersicht, kritischste Geschäftslogik zuerst)

| Prio | Bereich | Begründung |
|---|---|---|
| 1 | `ScheduleServiceImpl.calculateSchedule` (Abschnitt 2.1) | Kernalgorithmus des gesamten Projekts (topologische Sortierung), aktuell 0 % getestet, pure Funktion → am günstigsten zu testen, höchstes Risiko bei Regression |
| 2 | `ScheduleServiceImpl.calculateAndSaveSchedule` (2.2) | Verbindet Kernalgorithmus mit Persistenz; insbesondere die Nicht-Speicherung bei Zyklus ist sicherheitsrelevant für Datenkonsistenz |
| 3 | `StudyModuleServiceImpl.deleteStudyModule` (2.3) | Einzige Stelle mit komplexer Datenintegritäts-Logik (Bereinigung dangling Prerequisites); Fehler hier führen zu inkonsistenten Datenständen |
| 4 | `StudyModuleServiceImpl.createStudyModule` / `updateStudyModule` (2.5) | Schedule-Invalidierung ist eine leicht zu übersehende Nebenwirkung (Business-Regel "Datenänderung macht alten Plan ungültig") |
| 5 | `DegreeProgramServiceImpl.deleteDegreeProgram` (2.6) | Kaskadierendes Löschen über 3 Repositories — Fehler würden verwaiste Daten hinterlassen |
| 6 | `StudyModuleServiceImpl.findStudyModulesByKeys` (2.4) | Nicht-triviale Filter-/Dedup-Logik, wird von `StudyModuleController` für die Prerequisite-Anzeige genutzt |
| 7 | `DegreeProgramServiceImpl` Basis-CRUD (2.7) | Wichtig für Grundabsicherung, aber überwiegend einfache Delegation + bereits im Domain-Modell abgesicherte Invarianten |
| 8 | Einfache Delegationsmethoden (2.8) | Geringstes Risiko (reine Pass-through-Aufrufe), aber schnell zu ergänzen für 100 %-Methodenabdeckung |

---
**Nächster Schritt (nach Freigabe):** Umsetzung der Testfälle in der vorgeschlagenen Prioritätsreihenfolge, beginnend mit Abschnitt 2.1. Es wurde noch **kein Testcode geschrieben**.
