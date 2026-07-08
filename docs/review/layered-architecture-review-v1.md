# Report: Schichtenarchitektur-Analyse (Studyplanner)

## 1. Identifizierte Layer

Anhand der Package-Struktur unter `ch.ost.mas_se.oop.studyplanner`:

| Layer | Package | Typ |
|---|---|---|
| Presentation/Controller | `ui`, `ui.forms`, `ui.views` | Spring MVC `@Controller`, Form-Beans, View-Records (DTOs) |
| Service | `service` | Interfaces + `*Impl` (`@Service`), Geschäftslogik |
| Repository/Persistence | `repository` | Interfaces + `Json*Repository` (`@Repository`), Jackson-basierte Persistenz |
| Domain | `model` | POJOs ohne Framework-Abhängigkeiten |
| Cross-cutting | `exception`, `serialization` | Custom Exception, Jackson-Mixins |

Zielarchitektur: `ui → service → repository → model` (Domain im Zentrum).

## 2. Gesamteinschätzung

Die Architektur ist insgesamt **sauber geschichtet** und deutlich besser als der Durchschnitt vergleichbarer Miniprojekte:

- **Kein Layer-Skipping gefunden**: Kein Controller injiziert ein Repository direkt; alle Controller (`DegreeProgramController`, `StudyModuleController`, `ScheduleController`, `WelcomeController`) hängen ausschliesslich von Service-Interfaces ab.
- **Abhängigkeitsrichtung zur Domain korrekt**: `model`-Klassen (`DegreeProgram`, `StudyModule`, `Schedule`, `Semester`) haben **keine** Jackson-/Spring-/Validation-Annotationen. Die JSON-Serialisierung wird über separate Mixin-Klassen (`serialization`-Package) realisiert, die via `JacksonConfig` registriert werden – das ist ein sehr guter Kunstgriff, um die Domain frei von Persistenz-Details zu halten.
- **DTO/Mapping ist konsistent**: Controller geben nie rohe Domain-Objekte ins Template, sondern mappen immer über `ui.views`-Records (`DegreeProgramView::from`, `StudyModuleView::from`, …). Formulare (`ui.forms`) sind ebenfalls klar von der Domain getrennt.
- **Repository-Abstraktion ist technologieunabhängig**: Die Interfaces (`DegreeProgramRepository` etc.) kennen kein Jackson/`ObjectMapper` – nur die `Json*Repository`-Implementierungen. Ein Wechsel auf JPA würde den Service-Layer nicht berühren.

Trotzdem gibt es zwei konkrete Stellen, an denen Verantwortlichkeiten nicht ganz sauber sitzen:

## 3. Befunde

| # | Datei + Zeile | Schweregrad | Befund |
|---|---|---|---|
| 1 | `src/main/java/.../ui/StudyModuleController.java:57-65` | **mittel** | Geschäftsregel "Modul-Key muss innerhalb eines Studiengangs eindeutig sein" wird im Controller geprüft, nicht im Service/Domain. |
| 2 | `src/main/java/.../ui/ScheduleController.java:56-68` + `src/main/java/.../ui/views/SemesterView.java:16-28` | **gering** | Verknüpfung von Semester-Keys mit `StudyModule`-Objekten sowie die ECTS-Summenbildung (fachliche Berechnung) sind zwischen Controller (holt `moduleMap`) und View-Record (`SemesterView.from`) aufgeteilt statt im `ScheduleService` zu liegen. |
| 3 | `src/main/java/.../repository/*Repository.java` (Interfaces) vs. `Json*Repository.java` | **gering** | Repository-Ports (Interfaces) und Adapter (JSON-Implementierung) liegen im selben Package. Für eine klassische 4-Schichten-Architektur unproblematisch, für eine strengere Clean/Onion-Trennung wäre eine Package-Trennung expliziter. |

### Detail zu Fund 1 — kritischste Stelle
**Datei:** `ui/StudyModuleController.java`, Zeilen 57–65
```java
if (studyModuleService
    .findStudyModuleByKey(degreeProgramId, form.getStudyModuleKey())
    .isPresent()) {
  result.rejectValue(
      "studyModuleKey", "duplicate",
      "A study module with this key already exists in this degree program.");
  return showCreateFormView(degreeProgramId, form, model);
}
```
`StudyModuleServiceImpl.createStudyModule` (`service/StudyModuleServiceImpl.java:29-49`) führt selbst **keine** Eindeutigkeitsprüfung durch – `saveStudyModule` im Repository matched per Composite Key (`degreeProgramId` + `studyModuleKey`) und überschreibt ein vorhandenes Modul kommentarlos, falls der Key schon existiert. Die Invariante "Key eindeutig pro Studiengang" ist damit **nicht** im Service/Domain verankert, sondern nur eine Nebenwirkung der aktuellen UI-Flow-Logik. Jeder zukünftige zweite Aufrufer des Service (REST-Endpoint, Batch-Import, Testcode) kann diese Regel umgehen und ein Modul unbemerkt überschreiben.

**Refactoring-Vorschlag:**
1. Neue Exception `DuplicateStudyModuleKeyException` im `exception`-Package.
2. In `StudyModuleServiceImpl.createStudyModule` vor dem Speichern prüfen (`findStudyModuleByKey(...).isPresent()`) und die Exception werfen.
3. `StudyModuleController.createStudyModule` fängt die Exception und ruft `result.rejectValue(...)` auf (analog zum bereits vorhandenen Muster in `ScheduleController` mit `CyclicDependencyException`).

Damit ist die Invariante unabhängig vom Aufrufer garantiert und die Controller-Methode bleibt auf reines Form-Handling reduziert.

### Detail zu Fund 2
**Dateien:** `ui/ScheduleController.java:56-68`, `ui/views/SemesterView.java:16-28`

`addScheduleToModel` im Controller lädt zusätzlich alle Module und baut eine `Map<String, StudyModule>`, die dann an `SemesterView.from(semester, moduleMap)` übergeben wird. Dort werden die Keys zu Objekten aufgelöst **und** `totalEcts` per Stream berechnet – eine fachliche Berechnung (Aggregation), die weder durch einen Service- noch durch einen sichtbaren Unit-Test abgedeckt ist (es existieren keine Tests für `ui.views`).

**Refactoring-Vorschlag:** Verantwortung in den `ScheduleService` (oder ins Domain-Modell `Semester`/`Schedule`) verschieben, z. B. eine Methode `ScheduleService.resolveSemesters(degreeProgramId)`, die bereits aufgelöste, mit ECTS-Summe versehene Objekte liefert. Der Controller ruft dann nur noch diese eine Methode auf und mappt 1:1 auf `SemesterView`. Das macht die Berechnung unit-testbar und wiederverwendbar (z. B. für einen künftigen REST-Endpoint).

### Detail zu Fund 3 (nur zur Vollständigkeit, niedrige Priorität)
Interface und Implementierung liegen zusammen im `repository`-Package. Für die hier verlangte klassische Layered Architecture (Controller→Service→Repository→Model) ist das üblich und unproblematisch – nur bei Anspruch auf strengere Clean/Onion-Architektur würde man den Port ins Service-nahe Package verschieben. **Keine Handlung nötig**, nur der Vollständigkeit halber erwähnt.

## 4. Nicht gefunden (positiv zu vermerken)
- Kein Controller ruft ein Repository direkt auf.
- Kein Persistenz-Code (Jackson, Dateizugriff) ausserhalb von `repository`.
- Keine Geschäftslogik-Duplikate zwischen Service-Implementierungen.
- Domain-Modell ist vollständig frei von Framework-Abhängigkeiten.

## Hinweis am Rande (ausserhalb des Scopes dieser Analyse)
Die in Kursblock 8 verlangten Filter-/Sortierfunktionen (Name, „Offered at“, ECTS, ASC/DESC) sind im aktuellen `DegreeProgramController.showDetails` noch nicht implementiert (keine entsprechenden Query-Parameter/Aufrufe gefunden) – das ist ein Funktionslücken-Befund, keiner zur Schichtenarchitektur, daher nur als Hinweis erwähnt.

---
Es wurden **keine Code-Änderungen vorgenommen** – dies ist ausschliesslich der angeforderte Report.
