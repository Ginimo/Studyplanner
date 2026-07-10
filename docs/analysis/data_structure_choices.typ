#set document(title: "Data Structure Choices — Design Analysis")
#set page(paper: "a4", margin: (x: 2.5cm, y: 3cm))
#set text(font: "New Computer Modern", size: 11pt)
#set heading(numbering: "1.")
#set par(justify: true, leading: 0.75em)

#align(center)[
  #text(size: 16pt, weight: "bold")[Data Structure Choices — Design Analysis] \
]

#v(1em)
#line(length: 100%)
#v(1.2em)


= `ScheduleServiceImpl`

== `moduleMap` — `LinkedHashMap<String, StudyModule>`

`moduleMap` is built before the BFS starts and serves two purposes:

1. *Key set extraction:* `moduleMap.keySet()` is passed to `buildInDegree` and
   `buildDependents` so they can distinguish in-program prerequisites (edges to include)
   from dangling prerequisite keys that reference modules not present in the program
   (edges to ignore). This prevents dangling keys from inflating in-degrees and blocking
   modules that have no real unmet prerequisites.

2. *Deterministic ordering:* `LinkedHashMap` preserves insertion order, so the initial
   ready-queue seed (all keys with in-degree zero) is always produced in the same order
   for the same input. This makes the generated schedule deterministic: identical module
   sets always produce the same layering.

Note: the BFS loop itself works only with module keys (`String`) — it does not look up
`StudyModule` objects from `moduleMap` during the loop. Module details are resolved later
by the controller when building the schedule view, via
`StudyModuleService.findModuleMapByDegreeProgramId`.

#figure(
  table(
    columns: (auto, 1fr),
    stroke: 0.5pt,
    inset: (x: 8pt, y: 4pt),
    fill: (x, _) => if x == 0 { luma(238) } else { white },
    [`"Math"`],   [StudyModule(key: `"Math"`,   ects: 4, …)],
    [`"OO"`],     [StudyModule(key: `"OO"`,     ects: 4, …)],
    [`"DB1"`],    [StudyModule(key: `"DB1"`,    ects: 4, …)],
    text(fill: gray)[$dots.v$ (insertion order)],  text(fill: gray)[$dots.v$],
    [`"Thesis"`], [StudyModule(key: `"Thesis"`, ects: 12, …)],
  ),
  caption: [moduleMap — every key maps to its StudyModule],
)

== `inDegree` — `LinkedHashMap<String, Integer>`

Stores how many prerequisites each module still has left to satisfy.
`LinkedHashMap` is used for the same reason as `moduleMap`: insertion order makes the
initial ready-queue seed deterministic.

#figure(
  table(
    columns: (auto, auto, 1fr),
    stroke: 0.5pt,
    inset: (x: 8pt, y: 4pt),
    fill: (x, _) => if x == 0 { luma(238) } else { white },
    [`"Math"`],   [`0`], [no prerequisites → enters ready queue immediately],
    [`"OO"`],     [`0`], [no prerequisites → enters ready queue immediately],
    [`"DB1"`],    [`1`], [needs OO],
    [`"CPI"`],    [`2`], [needs OO, Math],
    [`"SE1"`],    [`3`], [needs AD1, CPI, DB1],
    text(fill: gray)[$dots.v$], text(fill: gray)[$dots.v$], [],
    [`"Thesis"`], [`3`], [needs DB2, SE2, UI2],
  ),
  caption: [inDegree — count of unsatisfied in-program prerequisites; a module enters the ready queue when its count reaches 0],
)

== `dependents` — `LinkedHashMap<String, List<String>>`

Stores, for each module, the list of modules that depend on it.
This is used to know which in-degrees to decrement when a module is placed.
`LinkedHashMap` is used for the same reason as `moduleMap` and `inDegree`: the map is
pre-populated with one empty list per key in `moduleMap.keySet()` insertion order, keeping
the overall structure deterministic.
The order of decrement operations during BFS does not affect correctness, but
`LinkedHashMap` is used consistently across all three auxiliary maps.

#figure(
  table(
    columns: (auto, 1fr),
    stroke: 0.5pt,
    inset: (x: 8pt, y: 4pt),
    fill: (x, _) => if x == 0 { luma(238) } else { white },
    [`"Math"`],   [\[`"CPI"`\]],
    [`"OO"`],     [\[`"DB1"`, `"AD1"`, `"AD2"`, `"CPI"`\]],
    [`"DB1"`],    [\[`"DB2"`, `"SE1"`, `"SE2"`\]],
    [`"AD1"`],    [\[`"AD2"`, `"SE1"`, `"UI1"`\]],
    text(fill: gray)[$dots.v$], text(fill: gray)[$dots.v$],
    [`"Thesis"`], [\[\]],
  ),
  caption: [dependents — reverse adjacency list; when a module is placed, each entry in its list has its inDegree decremented],
)

== `ready` — `ArrayDeque<String>`

Holds module keys that are ready to be scheduled (all prerequisites satisfied).
The variable is declared as `Queue<String>` and backed by `ArrayDeque` — only the
queue-side operations are used: `add` (enqueue at back), `isEmpty`, `clear`, and
iteration via `List.copyOf`.

*Why a queue at all?*
A queue models the BFS layer boundary naturally: new modules are enqueued at the back
as their in-degrees reach zero, and the front-to-back snapshot at the start of each
iteration captures exactly the current ready layer without mixing in modules that become
ready mid-iteration.

*Why `ArrayDeque` and not `LinkedList`?*
Both implement `Queue`, but `LinkedList` allocates a separate `Node` object for every
element.
`ArrayDeque` stores elements in a circular array, so elements are contiguous in memory.

#figure(
  align(center,
    grid(
      columns: (auto, auto, auto, auto),
      column-gutter: 0pt,
      align: center + horizon,
      [front $arrow.r$#h(4pt)],
      rect(stroke: 0.5pt, inset: (x: 12pt, y: 6pt))[`"Math"`],
      rect(stroke: 0.5pt, inset: (x: 12pt, y: 6pt))[`"OO"`],
      [#h(4pt)$arrow.l$ back],
    )
  ),
  caption: [ready — initial state after seeding; contains all modules whose inDegree = 0],
)


= Repository Layer

All three repositories (`JsonDegreeProgramRepository`, `JsonStudyModuleRepository`,
`JsonScheduleRepository`) extend the abstract base class `AbstractJsonRepository<T>` and
persist data to JSON files in the configured data directory (`studyplanner.data.dir`).

`JsonDegreeProgramRepository` and `JsonStudyModuleRepository` both deserialize their file
into a `List<T>` on every read and serialize the complete list back on every write.
`JsonScheduleRepository` uses a `Map<String, Schedule>` keyed by degree-program ID.

*Flat list for study modules:* `JsonStudyModuleRepository` stores all study modules from
all degree programs in a single flat `List<StudyModule>`.
Each `StudyModule` object carries its own `degreeProgramId` field, so filtering by program
is a linear scan.
The alternative — one file per degree program or a nested map structure — would complicate
file management (extra files to create and delete when programs are created or removed)
without meaningfully improving performance at typical class sizes.

*Composite business key:* A study module is uniquely identified by the pair
(`degreeProgramId`, `studyModuleKey`).
`findStudyModuleByKey` and `saveStudyModule` both match on this composite key.
This avoids the need for a synthetic surrogate ID on study modules and keeps module keys
human-readable (e.g., `"OO"`, `"DB1"`), while still supporting the case where the same
short key appears in two different degree programs (see Assumption A03).

#figure(
  align(center,
    grid(
      columns: (auto, auto, auto, auto, auto),
      column-gutter: 5pt,
      align: center + horizon,
      rect(fill: luma(238), stroke: 0.5pt, inset: (x: 8pt, y: 7pt))[
        `"abc-123"` \
        #text(8pt, fill: gray)[degreeProgramId]
      ],
      [+],
      rect(fill: luma(238), stroke: 0.5pt, inset: (x: 8pt, y: 7pt))[
        `"OOP1"` \
        #text(8pt, fill: gray)[studyModuleKey]
      ],
      $arrow.r$,
      rect(fill: luma(215), stroke: 0.5pt, inset: (x: 8pt, y: 7pt), radius: 2pt)[unique StudyModule],
    )
  ),
  caption: [Composite business key — degreeProgramId and studyModuleKey together uniquely identify a study module within the flat JSON list],
)


= Model Layer

== `Schedule` and `Semester` — Java records with `List.copyOf`

`Schedule` and `Semester` are Java records — inherently immutable value types.
Their lists are fully constructed before the record is created, and the compact
constructor seals them at construction time.

- `Semester(int semesterNumber, List<String> studyModuleKeys)` — the BFS builds a
  `semesterKeys` list for each layer and passes it to the compact constructor.
  The compact constructor immediately replaces the parameter with
  `List.copyOf(studyModuleKeys)` (or `List.of()` when `null`), producing an independent,
  unmodifiable snapshot. The caller retains a reference to the original mutable list but
  cannot affect the record's internal state through it.
- `Schedule(LocalDateTime calculatedAt, List<Semester> semesters)` — likewise, the BFS
  accumulates semesters in a local list and passes the finished list to the compact
  constructor, which replaces it with `List.copyOf(semesters)`.

`List.copyOf` both copies the data and makes the result unmodifiable in a single step —
unlike `Collections.unmodifiableList`, which creates only a read-only _view_ around an
existing (potentially mutable) list without copying it.
The record accessor methods `semesters()` and `studyModuleKeys()` return these immutable
lists directly; no further wrapping is needed.

#figure(
  align(center,
    grid(
      columns: (1fr, auto, 1fr),
      column-gutter: 16pt,
      align: center + horizon,
      stack(
        rect(fill: luma(238), stroke: 0.5pt, inset: (x: 10pt, y: 8pt))[
          caller's list \
          #text(9pt)[\[`"OO"`, `"DB1"`\]]
        ],
        v(4pt),
        text(8pt, fill: gray)[mutable — caller retains reference],
      ),
      $arrow.r^"List.copyOf"$,
      stack(
        rect(fill: luma(215), stroke: 0.5pt, inset: (x: 10pt, y: 8pt), radius: 2pt)[
          record field \
          #text(9pt)[\[`"OO"`, `"DB1"`\]]
        ],
        v(4pt),
        text(8pt, fill: gray)[immutable copy — returned by accessor],
      ),
    )
  ),
  caption: [The compact constructor calls `List.copyOf` to sever the link to the caller's mutable list; the record accessor returns the immutable result directly],
)

== `StudyModule` prerequisite keys — `List.copyOf`

`StudyModule` is constructed inside the service layer (`StudyModuleServiceImpl`).
The prerequisite keys originate from form input (collected by the controller) and are
passed as a `List<String>` through the service call into the constructor.
If the constructor stored that reference directly, the caller could modify the list
afterwards (accidentally or otherwise) and silently change a module it already saved.

`List.copyOf` makes a new, independent, immutable list at construction time, severing
any connection to the original.
An `unmodifiableList` wrapper would not be enough here: it wraps the caller's list
rather than copying it, so the caller would still hold a mutable reference to the
underlying data.

All three types (`Schedule`, `Semester`, and `StudyModule`) use `List.copyOf` for the
same reason: callers must not be able to affect an object's internal list after it is
constructed. `StudyModule` is especially susceptible because its prerequisite list
travels through several layers (form → controller → service → constructor) before
arriving, and the caller at each hop still holds a reference to the original collection.

#figure(
  align(center,
    grid(
      columns: (1fr, auto, 1fr),
      column-gutter: 16pt,
      align: center + horizon,
      stack(
        rect(fill: luma(238), stroke: 0.5pt, inset: (x: 10pt, y: 8pt))[
          caller's list \
          #text(9pt)[\[`"DB1"`, `"SE1"`\]]
        ],
        v(4pt),
        text(8pt, fill: gray)[mutable — caller retains reference],
      ),
      $arrow.r^"List.copyOf"$,
      stack(
        rect(fill: luma(215), stroke: 0.5pt, inset: (x: 10pt, y: 8pt), radius: 2pt)[
          `prerequisiteKeys` \
          #text(9pt)[\[`"DB1"`, `"SE1"`\]]
        ],
        v(4pt),
        text(8pt, fill: gray)[immutable copy — independent],
      ),
    )
  ),
  caption: [`List.copyOf` creates an independent immutable copy at construction time, severing any link to the caller's original list],
)
