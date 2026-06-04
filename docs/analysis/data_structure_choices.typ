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

During the BFS loop, each placed module needs to be looked up by its key string.
A `Map` does this in $O(1)$.
A `Set` would require scanning all elements to find a match — $O(V)$ per lookup —
which would degrade the total algorithm from $O(V + E)$ to $O(V^2 + E)$.

`LinkedHashMap` is used instead of plain `HashMap` so modules are processed in the order
they were created, making the output deterministic: the same input always produces the
same schedule.

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

== `dependents` — `HashMap<String, List<String>>`

Stores, for each module, the list of modules that depend on it.
This is used to know which in-degrees to decrement when a module is placed.
The order of decrements does not affect correctness, so a plain `HashMap` is sufficient.

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

Both repositories use a `LinkedHashMap` as their backing store.
This gives $O(1)$ for all four operations (save, find, findAll, delete) and preserves
insertion order, so `findAll()` returns entries in creation order —
producing a stable, predictable list in the UI.

`InMemoryStudyModuleRepository` needs to store modules from multiple degree programs in
a single map.
The alternative would be a nested map: `Map<String, Map<String, StudyModule>>` — an outer
map keyed by degree program ID, each containing an inner map keyed by module key.
Instead, a *flat map with a composite key* is used: the two IDs are joined into a single
string (`"degreeProgramId#moduleKey"`) and used as the map key directly.
For example, a module with key `"OOP1"` in program `"abc-123"` is stored under the key
`"abc-123#OOP1"`.
A flat map is simpler: there is only one map to create, one map to iterate, and no need
to manage the lifecycle of inner maps.

The `#` separator is safe because degree-program IDs are UUIDs and module keys are short
alphanumeric strings — neither can contain `#`, so two different (program, module) pairs
can never produce the same composite key.

#figure(
  align(center,
    grid(
      columns: (auto, auto, auto, auto, auto, auto, auto),
      column-gutter: 5pt,
      align: center + horizon,
      rect(fill: luma(238), stroke: 0.5pt, inset: (x: 8pt, y: 7pt))[
        `"abc-123"` \
        #text(8pt, fill: gray)[degreeProgramId]
      ],
      [+],
      rect(fill: luma(225), stroke: 0.5pt, inset: (x: 8pt, y: 7pt))[`"#"`],
      [+],
      rect(fill: luma(238), stroke: 0.5pt, inset: (x: 8pt, y: 7pt))[
        `"OOP1"` \
        #text(8pt, fill: gray)[moduleKey]
      ],
      $arrow.r$,
      rect(fill: luma(215), stroke: 0.5pt, inset: (x: 8pt, y: 7pt), radius: 2pt)[`"abc-123#OOP1"`],
    )
  ),
  caption: [Composite key — degreeProgramId and moduleKey joined by `"#"` form a single flat-map key that uniquely identifies a (program, module) pair],
)


= Model Layer

== `Schedule` and `Semester` — `unmodifiableList` view

Both classes build their lists incrementally: the BFS loop adds semesters to `Schedule`
and modules to each `Semester` one by one.
At the same time, their getters are public, so external code could potentially call
`getSemesters().add(...)` and corrupt the state mid-calculation.

Wrapping the list in `Collections.unmodifiableList` before returning it blocks that:
any attempt to add, remove, or clear via the returned reference throws an exception.
The BFS itself keeps a direct reference to the underlying `ArrayList` and continues
adding to it freely — only the view handed to callers is read-only.
No copying happens, so this protection costs $O(1)$.

#figure(
  align(center,
    rect(
      stroke: (paint: blue.lighten(40%), thickness: 0.8pt),
      inset: 12pt,
      radius: 3pt,
    )[
      #text(size: 8.5pt, fill: blue.darken(10%))[`Collections.unmodifiableList` — read-only view, O(1) overhead]
      #v(6pt)
      #rect(stroke: 0.5pt, inset: 9pt, fill: luma(248))[
        `ArrayList`: \[Semester₁, Semester₂, …\] \
        #text(8pt, fill: gray)[← BFS holds a direct reference here and writes freely]
      ]
    ]
  ),
  caption: [The unmodifiable wrapper exposes a read-only view of the ArrayList; the BFS keeps a direct reference to the underlying list and continues to add to it],
)

== `StudyModule` prerequisite keys — `List.copyOf`

`StudyModule` is constructed by the controller, which builds the prerequisite list from
form input and passes it in.
If the constructor stored that reference directly, the controller could modify the list
afterwards (accidentally or otherwise) and silently change a module it already saved.

`List.copyOf` makes a new, independent, immutable list at construction time, severing
any connection to the original.
An `unmodifiableList` wrapper would not be enough here: it wraps the caller's list
rather than copying it, so the caller would still hold a mutable reference to the
underlying data.

The key difference between the two cases: `Schedule`/`Semester` own their lists from
the start and just need to hide them from callers, so a wrapper is enough.
`StudyModule` receives a list it does not own, so it must copy it.

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
