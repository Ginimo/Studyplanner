package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.exception.CyclicDependencyException;
import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.model.Schedule;
import ch.ost.mas_se.oop.studyplanner.model.Semester;
import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private final StudyModuleService studyModuleService;

    public ScheduleServiceImpl(StudyModuleService studyModuleService) {
        this.studyModuleService = studyModuleService;
    }

    /**
     * Computes a semester-by-semester schedule using Kahn's layered BFS topological sort.
     * Each semester contains all modules whose prerequisites were fully satisfied by earlier semesters.
     * <p>
     * Complexity: O(V + E) — every module and every in-program prerequisite edge is visited exactly once.
     *
     * @throws CyclicDependencyException if the prerequisite graph contains a cycle
     */
    @Override
    public Schedule calculate(DegreeProgram program) {
        List<StudyModule> allModules = studyModuleService.findAllByDegreeProgramId(program.getId());
        Map<String, StudyModule> moduleMap = buildModuleMap(allModules);
        Map<String, Integer> inDegree = buildInDegree(allModules, moduleMap.keySet());
        Map<String, List<String>> dependents = buildDependents(allModules, moduleMap.keySet());

        Queue<String> ready = seedReady(inDegree);
        Schedule schedule = new Schedule();
        int placed = 0;

        for (int semesterNumber = 1; !ready.isEmpty(); semesterNumber++) {
            // snapshot the layer so modules unblocked during this iteration land in the next semester
            List<String> layer = List.copyOf(ready);
            ready.clear();
            Semester semester = new Semester(semesterNumber);
            for (String key : layer) {
                semester.addStudyModule(moduleMap.get(key));
                placed++;
                for (String dep : dependents.getOrDefault(key, List.of())) {
                    int remaining = inDegree.merge(dep, -1, Integer::sum); // decrement in-degree and return new value
                    if (remaining == 0) {
                        ready.add(dep);
                    }
                }
            }
            schedule.addSemester(semester);
        }

        // any module that never reached inDegree 0 is stuck in a cycle and was never placed
        if (placed < allModules.size()) {
            throw new CyclicDependencyException(collectCycleKeys(inDegree));
        }
        return schedule;
    }

    /** Returns modules indexed by their key for O(1) lookup during schedule construction. */
    private Map<String, StudyModule> buildModuleMap(List<StudyModule> modules) {
        Map<String, StudyModule> map = new LinkedHashMap<>();
        for (StudyModule m : modules) {
            map.put(m.getStudyModuleKey(), m);
        }
        return map;
    }

    /**
     * Returns each module's in-degree: the count of its prerequisite keys that are also
     * present in the program. Prerequisites referencing unknown modules are ignored so that
     * dangling keys do not cause false cycle detections.
     */
    private Map<String, Integer> buildInDegree(List<StudyModule> modules, Set<String> allKeys) {
        Map<String, Integer> inDegree = new LinkedHashMap<>();
        for (StudyModule m : modules) {
            long count = m.getPrerequisiteKeys().stream().filter(allKeys::contains).count();
            inDegree.put(m.getStudyModuleKey(), (int) count);
        }
        return inDegree;
    }

    /**
     * Returns the reverse adjacency list: for each module key, the list of module keys that
     * declare it as a prerequisite (its "dependents"). Only in-program edges are included.
     */
    private Map<String, List<String>> buildDependents(List<StudyModule> modules, Set<String> allKeys) {
        Map<String, List<String>> dependents = new HashMap<>();
        for (StudyModule m : modules) {
            for (String prereq : m.getPrerequisiteKeys()) {
                if (allKeys.contains(prereq)) {
                    dependents.computeIfAbsent(prereq, k -> new ArrayList<>())
                              .add(m.getStudyModuleKey());
                }
            }
        }
        return dependents;
    }

    /** Returns a queue pre-populated with all modules that have no prerequisites. */
    private Queue<String> seedReady(Map<String, Integer> inDegree) {
        Queue<String> ready = new ArrayDeque<>();
        inDegree.forEach((key, degree) -> {
            if (degree == 0) {
                ready.add(key);
            }
        });
        return ready;
    }

    /** Returns the sorted keys of all modules still blocked after the BFS drains (cycle members). */
    private List<String> collectCycleKeys(Map<String, Integer> inDegree) {
        return inDegree.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }
}
