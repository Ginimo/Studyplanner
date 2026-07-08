# Report: UML-Diagramme vs. Implementierung — Konsistenz-Check

Geprüfte Diagramme (`docs/diagrams/*.puml`): `domain_class_diagram`, `ui_class_diagram`, `domain_sequence_diagram`, `domain_crud_sequence_diagram`, `schedule_service` (Aktivitätsdiagramm), `ui_workflow` (Aktivitätsdiagramm), `use_case_diagram`.

## 1. `domain_class_diagram.puml`

| Diagramm-Element | Status | Fundstelle |
|---|---|---|
| `DegreeProgram.id` / `getId()` | abweichend | Diagramm Z.33,37 vs. `model/DegreeProgram.java:6,23` — Feld/Getter heisst `degreeProgramId`/`getDegreeProgramId()` |
| `StudyModule` Felder/Getter | übereinstimmend | Diagramm Z.43-59 vs. `model/StudyModule.java:9-83` |
| `StudyModule.withoutPrerequisite(String)` | fehlt im Diagramm | `model/StudyModule.java:86-91` |
| `Schedule.addSemester(Semester): void` | fehlt im Code | Diagramm Z.66 — `Schedule` ist immutable, es gibt keine `addSemester`-Methode, nur den Konstruktor `Schedule(LocalDateTime, List<Semester>)` (`model/Schedule.java:13-16`) |
| `Semester.studyModules: List<StudyModule>` | abweichend | Diagramm Z.73 vs. `model/Semester.java:9` — tatsächliches Feld ist `studyModuleKeys: List<String>` (nur Keys, keine Objekt-Referenzen) |
| `Semester.addStudyModule(module): void` | fehlt im Code | Diagramm Z.75 — Methode existiert nicht; Keys werden komplett im Konstruktor übergeben |
| `Semester.getStudyModules(): List<StudyModule>` | fehlt im Code | Diagramm Z.76 — reale Methode ist `getStudyModuleKeys(): List<String>` (`model/Semester.java:26-28`) |
| `Semester.getTotalEcts(): int` | fehlt im Code | Diagramm Z.77 — keine solche Methode in `Semester`; ECTS-Summe wird erst in `ui/views/SemesterView.java:26` berechnet |
| `Semester "1" --> "*" StudyModule : consists of` | abweichend | Diagramm Z.175 — real nur `List<String>` (Keys), keine direkte Objekt-Assoziation |
| `CyclicDependencyException` | übereinstimmend | Diagramm Z.82-86 vs. `exception/CyclicDependencyException.java` |
| `DegreeProgramService.create/update(DegreeProgram): void` | abweichend | Diagramm Z.91,94 vs. `service/DegreeProgramService.java:16-33` — Methoden heissen `createDegreeProgram(name, description, maxDuration): DegreeProgram` / `updateDegreeProgram(id, name, description, maxDuration): void` (Einzelfelder statt Objekt, `create` liefert das erzeugte Objekt zurück) |
| `DegreeProgramServiceImpl` — nur `repository: DegreeProgramRepository` | fehlt im Diagramm | Diagramm Z.99 vs. `service/DegreeProgramServiceImpl.java:16-18` — Klasse hat zusätzlich `studyModuleRepository` und `scheduleRepository` (für kaskadierendes Löschen) |
| `StudyModuleService.create/update(StudyModule): void` | abweichend | Diagramm Z.105,108 vs. `service/StudyModuleService.java:13-51` — Einzelfeld-Parameter statt Objekt |
| `StudyModuleService.findStudyModulesByKeys(...)` | fehlt im Diagramm | `service/StudyModuleService.java:37` |
| `StudyModuleServiceImpl` — nur `repository: StudyModuleRepository` | fehlt im Diagramm | Diagramm Z.113 vs. `service/StudyModuleServiceImpl.java:19-20` — zusätzlich `scheduleRepository` (invalidiert Schedule-Cache bei Modul-Änderung) |
| `ScheduleService.calculate(DegreeProgram): Schedule` (einzige Methode) | abweichend | Diagramm Z.118-120 vs. `service/ScheduleService.java:9-18` — reale Schnittstelle hat **vier** Methoden: `calculateSchedule(List<StudyModule>)`, `calculateAndSaveSchedule(DegreeProgram): void`, `findScheduleByDegreeProgramId(...)`, `deleteSchedule(...)` |
| `ScheduleServiceImpl.studyModuleService: StudyModuleService` | abweichend | Diagramm Z.123 vs. `service/ScheduleServiceImpl.java:16-17` — reale Felder sind `studyModuleRepository` und `scheduleRepository` (direkter Repository-Zugriff, nicht über den Service) |
| `ScheduleController --> ScheduleService/DegreeProgramService` (2 Deps) | fehlt im Diagramm | Diagramm Z.188-189 vs. `ui/ScheduleController.java:28-38` — Controller hat **drei** Service-Abhängigkeiten, `StudyModuleService` fehlt in der Übersicht |
| `InMemoryDegreeProgramRepository` / `InMemoryStudyModuleRepository` | abweichend | Diagramm Z.151,164 vs. Code: reale Klassen sind `JsonDegreeProgramRepository` / `JsonStudyModuleRepository` (Datei-basiert via `AbstractJsonRepository`, kein `Map<String,X> store`-Feld) — Diagramm spiegelt offenbar den Kursblock-5-Stand (Collections) statt Kursblock-7-Stand (IO/JSON) |
| `ScheduleRepository` / `JsonScheduleRepository` | fehlt im Diagramm | `repository/ScheduleRepository.java`, `repository/JsonScheduleRepository.java` — komplett fehlend, obwohl von allen drei Service-Klassen verwendet |
| `AbstractJsonRepository<T>` | fehlt im Diagramm | `repository/AbstractJsonRepository.java` |

## 2. `ui_class_diagram.puml`

| Diagramm-Element | Status | Fundstelle |
|---|---|---|
| `DegreeProgramForm` | übereinstimmend | Diagramm Z.25-35 vs. `ui/forms/DegreeProgramForm.java` |
| `StudyModuleForm.isNew` / `getIsNew()` / `setIsNew()` | abweichend | Diagramm Z.45,60-61 vs. `ui/forms/StudyModuleForm.java:32,96-101` — reales Feld heisst `newEntry`, Methoden `isNewEntry()`/`setNewEntry()` |
| `StudyModuleForm.isOfferedToAfterOfferedFrom()` | fehlt im Diagramm | `ui/forms/StudyModuleForm.java:90-94` (Cross-Field-Validierung) |
| `WelcomeController` | übereinstimmend | Diagramm Z.66-70 vs. `ui/WelcomeController.java` |
| `DegreeProgramController.showDetails(id, nameFilter, offeredAt, sortBy, sortDir, model)` | fehlt im Code | Diagramm Z.79 vs. `ui/DegreeProgramController.java:76` — reale Methode ist `showDetails(degreeProgramId, model)`; **kein** Filter/Sortier-Parameter implementiert (Kursblock-8-Feature laut Aufgabenstellung, im Code nicht vorhanden) |
| `DegreeProgramController.toView(...)` | fehlt im Code | Diagramm Z.85 — Mapping erfolgt inline über `DegreeProgramView.from(...)`, keine private `toView`-Methode |
| `AbstractController` (Basisklasse) | fehlt im Diagramm | `ui/AbstractController.java` — komplett fehlend; `DegreeProgramController`, `StudyModuleController`, `ScheduleController` erben davon (`requireDegreeProgram`), im Diagramm aber ohne Vererbungspfeil und ohne die Methode dargestellt |
| `StudyModuleController.requireProgram(id): DegreeProgram` | abweichend | Diagramm Z.101 vs. Code — Methode heisst `requireDegreeProgram` und liegt in `AbstractController.java:16`, nicht lokal in `StudyModuleController` |
| `StudyModuleController.requireStudyModule(...)` | fehlt im Diagramm | `ui/StudyModuleController.java:231-239` |
| `StudyModuleController.toView(...)` (x2) | fehlt im Code | Diagramm Z.102-103 — Mapping erfolgt inline über `StudyModuleView.from(...)`/`DegreeProgramView.from(...)` |
| `ScheduleController` — Felder `degreeProgramService`, `scheduleService` | fehlt im Diagramm | Diagramm Z.107-108 vs. `ui/ScheduleController.java:28-29` — drittes Feld `studyModuleService` fehlt |
| `ScheduleController.resolveSchedule(program, model): Schedule` | fehlt im Code | Diagramm Z.112 — keine solche Methode in `ui/ScheduleController.java` |
| `ScheduleController.addScheduleToModel(schedule, model): void` | abweichend | Diagramm Z.113 vs. `ui/ScheduleController.java:56-57` — reale Signatur hat 3 Parameter: `addScheduleToModel(schedule, degreeProgramId, model)` |
| `DegreeProgramView.id` | abweichend | Diagramm Z.122 vs. `ui/views/DegreeProgramView.java:5-6` — Record-Komponente heisst `degreeProgramId` |
| `ScheduleView` | übereinstimmend | Diagramm Z.132-137 vs. `ui/views/ScheduleView.java` |
| `SemesterView.studyModuleKeys: String` / `getStudyModuleKeys()` | abweichend | Diagramm Z.143,147 vs. `ui/views/SemesterView.java:14` — Record-Komponente heisst `joinedStudyModuleKeys` |
| `StudyModuleKey` (eigene Value-Object-Klasse) | fehlt im Code | Diagramm Z.150-155 — Klasse existiert nicht im Projekt; Modul-Keys sind überall schlichte `String` (`model/StudyModule.java`, `ui/views/StudyModuleView.java`, `ui/views/StudyModuleListView.java`) |
| `StudyModuleListView.studyModuleKey: StudyModuleKey` | abweichend | Diagramm Z.158 vs. `ui/views/StudyModuleListView.java:7` — Typ ist `String` |
| `StudyModuleView.studyModuleKey: StudyModuleKey` | abweichend | Diagramm Z.175 vs. `ui/views/StudyModuleView.java:7` — Typ ist `String` |
| `ScheduleController --> StudyModuleService` | fehlt im Diagramm | Diagramm Z.194-195 vs. `ui/ScheduleController.java:29,37` |

## 3. `domain_sequence_diagram.puml` (Study Plan Calculation)

| Diagramm-Element | Status | Fundstelle |
|---|---|---|
| `SC -> SS : calculate(degreeProgram)` | abweichend | Diagramm Z.21 vs. `ui/ScheduleController.java:79` — realer Aufruf ist `scheduleService.calculateAndSaveSchedule(program)` (Rückgabewert `void`, nicht `Schedule`) |
| `SS -> S : new Schedule()` + spätere Befüllung | abweichend | Diagramm Z.29-32,48-52 — real wird `Schedule` erst am Ende mit fertiger Liste konstruiert (`service/ScheduleServiceImpl.java:72`); `addSemester` existiert nicht |
| `SS -> Sem : new Semester(semesterNumber)` (ohne Module) | abweichend | Diagramm Z.48 vs. `service/ScheduleServiceImpl.java:65` — `Semester` wird mit der fertigen Key-Liste im Konstruktor erzeugt, nicht nachträglich befüllt |
| Aufruf/Beteiligung von `ScheduleRepository` (Speichern des Plans) | fehlt im Diagramm | `service/ScheduleServiceImpl.java:154` (`scheduleRepository.saveSchedule(...)`) — im Diagramm nicht dargestellt |
| Getrennter Request-Flow "Anzeigen des Plans" (`showSchedule`, GET) | fehlt im Diagramm | `ui/ScheduleController.java:44-54` — Diagramm zeigt einen einzigen durchgehenden Fluss bis zur Anzeige, real ist "Berechnen" (POST + Redirect) von "Anzeigen" (separates GET, das den Plan erneut aus dem Repository lädt) getrennt |
| `alt` Cyclic-Dependency-Handling | übereinstimmend | Diagramm Z.55-56,63-64 vs. `ui/ScheduleController.java:81-83` |

## 4. `domain_crud_sequence_diagram.puml` (Create Study Module)

| Diagramm-Element | Status | Fundstelle |
|---|---|---|
| `SMC -> SM : new StudyModule(form.details)` (Controller erzeugt Domain-Objekt) | abweichend | Diagramm Z.14-17 vs. `ui/StudyModuleController.java:67-75` — Controller übergibt nur Einzelfelder; `new StudyModule(...)` wird tatsächlich im **Service** aufgerufen (`service/StudyModuleServiceImpl.java:38-47`) |
| `SMC -> SMS : create(module)` | abweichend | Diagramm Z.19 vs. `ui/StudyModuleController.java:67` — realer Aufruf `studyModuleService.createStudyModule(key, degreeProgramId, name, description, ects, prerequisiteKeys, offeredFrom, offeredTo)` mit Einzelparametern |
| Duplicate-Key-Prüfung (`result.rejectValue`) | fehlt im Diagramm | `ui/StudyModuleController.java:57-65` — im Code vorhandener Zweig fehlt komplett im Sequenzdiagramm |
| Validierungsfehler-Zweig (`result.hasErrors()`) | fehlt im Diagramm | `ui/StudyModuleController.java:53-55` |

## 5. `schedule_service.puml` (Aktivitätsdiagramm, Kahn's-Algorithmus)

| Diagramm-Element | Status | Fundstelle |
|---|---|---|
| Aufbau `moduleMap` / `inDegree` / `dependents`, `ready`-Queue, BFS-Schleife | übereinstimmend | Diagramm Z.32-96 vs. `service/ScheduleServiceImpl.java:76-135` — Algorithmus-Logik und Beispielwerte (Math/OO/DB1/…) stimmen inhaltlich mit `SampleDataInitializer` überein |
| `semester.addStudyModule(moduleMap[key])` | fehlt im Code | Diagramm Z.66 — real: `semesterKeys.add(studyModuleKey)` (nur Keys, kein Objekt-Zugriff auf `moduleMap` an dieser Stelle) |
| `schedule.addSemester(semester)` | fehlt im Code | Diagramm Z.71 — real: `semesters.add(new Semester(...))` in eine lokale Liste, `Schedule` wird erst am Ende konstruiert |
| Cycle-Detection (`placed < total?` → throw) | übereinstimmend | Diagramm Z.97-108 vs. `service/ScheduleServiceImpl.java:69-72` |

## 6. `ui_workflow.puml` (Aktivitätsdiagramm, CRUD-Workflow)

| Diagramm-Element | Status | Fundstelle |
|---|---|---|
| Create/Read/Update-Flows (Pfade, Validierung, Redirects) | übereinstimmend | Diagramm Z.5-44 vs. `ui/DegreeProgramController.java` (GET/POST `/new`, `/{id}/edit`, Listing, Details) |
| `POST /degree-programs/{id}/delete` | abweichend | Diagramm Z.47 vs. `ui/DegreeProgramController.java:128` — reales Mapping ist `POST /degree-programs/{degreeProgramId}/deleteDegreeProgram` |
| Löschen der persistierten `Schedule` beim Programm-Löschen | fehlt im Diagramm | `service/DegreeProgramServiceImpl.java:58` (`scheduleRepository.deleteSchedule(...)`) |
| `POST /degree-programs/{id}/study-modules/{key}/delete` | abweichend | Diagramm Z.79 vs. `ui/StudyModuleController.java:165` — reales Mapping ist `.../{studyModuleKey}/deleteStudyModule` |
| Duplicate-Key-Zweig bei Modul-Erstellung | fehlt im Diagramm | `ui/StudyModuleController.java:57-65` |
| Schedule-Berechnung/-Anzeige (eigener Workflow) | fehlt im Diagramm | `ui/ScheduleController.java` — nicht Teil dieses Diagramms (ggf. bewusst out-of-scope, da Titel "Degree Program CRUD") |

## 7. `use_case_diagram.puml`

| Diagramm-Element | Status | Fundstelle |
|---|---|---|
| UC1 Create and edit study programs | übereinstimmend | `ui/DegreeProgramController.java` (`/new`, `/{id}/edit`) |
| UC2 List all study programs | übereinstimmend | `ui/DegreeProgramController.java:32-39` |
| UC3 Manage modules for study program (CRUD) | übereinstimmend | `ui/StudyModuleController.java` (alle CRUD-Endpunkte) |
| UC4 (Re-)calculate study program | übereinstimmend | `ui/ScheduleController.java:74-85` |
| UC5 View plan (already calculated) | übereinstimmend | `ui/ScheduleController.java:44-54` |
| UC6 Delete whole study program | übereinstimmend | `ui/DegreeProgramController.java:128-136` |

Das Use-Case-Diagramm ist vollständig und deckt sich mit der Implementierung — hier keine Beanstandungen.

---

## Zusammenfassung

**Systematisches Muster über mehrere Diagramme hinweg**: `Schedule.addSemester()` und `Semester.addStudyModule()` (mutable Builder-Stil) erscheinen konsistent im Klassendiagramm, im Sequenzdiagramm *und* im Aktivitätsdiagramm — existieren im Code aber nicht. Das deutet darauf hin, dass die Diagramme einen früheren Zwischenstand (mutable Domain-Objekte, evtl. Kursblock 5) abbilden, der später zugunsten unveränderlicher Objekte (Konstruktor-basiert) refactored wurde, ohne die Diagramme nachzuziehen.

**Weitere auffällige Cluster:**
- **Persistenzschicht veraltet**: Diagramm zeigt `InMemory*Repository`, Code nutzt JSON-Dateipersistenz (`Json*Repository`); `ScheduleRepository` fehlt komplett im Klassendiagramm.
- **`StudyModuleKey`-Wrapper-Klasse** im UI-Klassendiagramm existiert nirgends im Code (überall `String`).
- **Kursblock-8-Feature (Filter/Sort)** ist im UI-Klassendiagramm bereits als Methode dargestellt, im Code aber nicht implementiert.
- **`AbstractController`** (gemeinsame Basisklasse) fehlt in beiden Klassendiagrammen vollständig.
- Mehrere URL-Pfade in `ui_workflow.puml` (`/delete` statt `/deleteDegreeProgram` bzw. `/deleteStudyModule`).

Es wurden **keine Code-Änderungen vorgenommen** — dies ist ausschliesslich der angeforderte Report.
