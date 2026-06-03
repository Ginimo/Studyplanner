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

    // O(V² + E): V = module count, E = total prerequisite edges.
    // Each iteration linearly scans all V modules to find those with no remaining prerequisites,
    // and removes processed keys from all V prerequisite sets. The loop runs at most V times.
    // A queue-based Kahn's algorithm would be O(V + E), but this simpler scan is sufficient
    // for the small module counts expected in a study program.
    @Override
    public Schedule calculate(DegreeProgram program) {
        List<StudyModule> allModules = studyModuleService.findAllByDegreeProgramId(program.getId());
        Map<String, StudyModule> moduleMap = buildModuleMap(allModules);
        Map<String, Set<String>> remainingPrerequisites = buildRemainingPrerequisites(allModules);

        Schedule schedule = new Schedule();
        Set<String> processed = new HashSet<>();
        int semesterNumber = 1;

        while (processed.size() < allModules.size()) {
            List<String> ready = findReady(remainingPrerequisites, processed);
            if (ready.isEmpty()) {
                throw new CyclicDependencyException(getRemainingKeys(remainingPrerequisites, processed));
            }
            Semester semester = new Semester(semesterNumber++);
            ready.forEach(key -> semester.addStudyModule(moduleMap.get(key)));
            processed.addAll(ready);
            schedule.addSemester(semester);
            remainingPrerequisites.values().forEach(prereqs -> ready.forEach(prereqs::remove));
        }

        return schedule;
    }

    private Map<String, StudyModule> buildModuleMap(List<StudyModule> modules) {
        Map<String, StudyModule> map = new LinkedHashMap<>();
        for (StudyModule m : modules) {
            map.put(m.getStudyModuleKey(), m);
        }
        return map;
    }

    private Map<String, Set<String>> buildRemainingPrerequisites(List<StudyModule> modules) {
        Map<String, Set<String>> map = new LinkedHashMap<>();
        for (StudyModule m : modules) {
            map.put(m.getStudyModuleKey(), new HashSet<>(m.getPrerequisiteKeys()));
        }
        return map;
    }

    private List<String> findReady(Map<String, Set<String>> remainingPrerequisites, Set<String> processed) {
        List<String> ready = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : remainingPrerequisites.entrySet()) {
            if (!processed.contains(entry.getKey()) && entry.getValue().isEmpty()) {
                ready.add(entry.getKey());
            }
        }
        return ready;
    }

    private List<String> getRemainingKeys(Map<String, Set<String>> remainingPrerequisites, Set<String> processed) {
        return remainingPrerequisites.keySet().stream()
                .filter(k -> !processed.contains(k))
                .sorted()
                .collect(Collectors.toList());
    }
}
