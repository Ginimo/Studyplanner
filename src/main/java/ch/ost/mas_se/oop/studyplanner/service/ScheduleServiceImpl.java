package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.exception.CyclicDependencyException;
import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.model.Schedule;
import ch.ost.mas_se.oop.studyplanner.model.Semester;
import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import ch.ost.mas_se.oop.studyplanner.repository.ScheduleRepository;
import ch.ost.mas_se.oop.studyplanner.repository.StudyModuleRepository;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class ScheduleServiceImpl implements ScheduleService {
  private final StudyModuleRepository studyModuleRepository;
  private final ScheduleRepository scheduleRepository;

  public ScheduleServiceImpl(
      StudyModuleRepository studyModuleRepository, ScheduleRepository scheduleRepository) {
    this.studyModuleRepository = studyModuleRepository;
    this.scheduleRepository = scheduleRepository;
  }

  /**
   * Computes a semester-by-semester schedule using Kahn's layered BFS topological sort. Each
   * semester contains all modules whose prerequisites were fully satisfied by earlier semesters.
   *
   * <p>Complexity: O(V + E) — every module and every in-program prerequisite edge is visited
   * exactly once.
   *
   * @throws CyclicDependencyException if the prerequisite graph contains a cycle
   */
  @Override
  public Schedule calculateSchedule(List<StudyModule> allModules) {

    // build 3 auxiliary data structures
    Map<String, StudyModule> moduleMap = buildModuleMap(allModules);
    Map<String, Integer> inDegree = buildInDegree(allModules, moduleMap.keySet());
    Map<String, List<String>> dependents = buildDependents(allModules, moduleMap.keySet());

    Queue<String> ready = seedReady(inDegree);
    Schedule schedule = new Schedule();
    int placedModuleCount =
        0; // tracks how many modules were placed; less than total at the end means a cycle exists

    for (int semesterNumber = 1; !ready.isEmpty(); semesterNumber++) {
      // snapshot the layer so modules unblocked during this iteration land in the next semester
      // 1 layer = 1 semester
      List<String> layer = List.copyOf(ready);
      ready.clear();
      Semester semester = new Semester(semesterNumber);
      for (String studyModuleKey : layer) {
        semester.addStudyModuleKey(studyModuleKey);
        placedModuleCount++;
        for (String dependentKey : dependents.get(studyModuleKey)) {
          // one more prerequisite of dependentKey is now satisfied; if none remain, it is ready
          int remaining = inDegree.get(dependentKey) - 1;
          inDegree.put(dependentKey, remaining);
          if (remaining == 0) {
            ready.add(dependentKey);
          }
        }
      }
      schedule.addSemester(semester);
    }

    // any module that never reached inDegree 0 is stuck in a cycle and was never placed
    if (placedModuleCount < allModules.size()) {
      throw new CyclicDependencyException(collectCycleKeys(inDegree));
    }
    return schedule;
  }

  /** Returns modules indexed by their key for O(1) lookup during schedule construction. */
  private Map<String, StudyModule> buildModuleMap(List<StudyModule> modules) {
    Map<String, StudyModule> map = new LinkedHashMap<>();
    for (StudyModule module : modules) {
      map.put(module.getStudyModuleKey(), module);
    }
    return map;
  }

  /**
   * Returns each module's in-degree: the count of its prerequisite keys that are also present in
   * the program. Prerequisites referencing unknown modules are ignored so that dangling keys do not
   * cause false cycle detections.
   */
  private Map<String, Integer> buildInDegree(
      List<StudyModule> modules, Set<String> allStudyModuleKeys) {
    Map<String, Integer> inDegree = new LinkedHashMap<>();
    for (StudyModule module : modules) {
      int prerequisiteCount = 0;
      for (String prerequisiteKey : module.getPrerequisiteKeys()) {
        if (allStudyModuleKeys.contains(prerequisiteKey)) {
          prerequisiteCount++;
        }
      }
      inDegree.put(module.getStudyModuleKey(), prerequisiteCount);
    }
    return inDegree;
  }

  /**
   * Returns the reverse adjacency list: for each module key, the list of module keys that declare
   * it as a prerequisite (its "dependents"). Only in-program edges are included.
   */
  private Map<String, List<String>> buildDependents(
      List<StudyModule> modules, Set<String> allStudyModuleKeys) {
    Map<String, List<String>> dependents = new LinkedHashMap<>();
    for (String studyModuleKey : allStudyModuleKeys) {
      dependents.put(
          studyModuleKey, new ArrayList<>()); // pre-populate so every key has a non-null list
    }
    for (StudyModule module : modules) {
      for (String prerequisiteKey : module.getPrerequisiteKeys()) {
        if (allStudyModuleKeys.contains(prerequisiteKey)) {
          dependents.get(prerequisiteKey).add(module.getStudyModuleKey());
        }
      }
    }
    return dependents;
  }

  /** Returns a queue pre-populated with all modules that have no prerequisites. */
  private Queue<String> seedReady(Map<String, Integer> inDegree) {
    Queue<String> ready = new ArrayDeque<>();
    inDegree.forEach(
        (studyModuleKey, inDegreeCount) -> {
          if (inDegreeCount == 0) {
            ready.add(studyModuleKey);
          }
        });
    return ready;
  }

  /** Returns the sorted keys of all modules still blocked after the BFS drains (cycle members). */
  private List<String> collectCycleKeys(Map<String, Integer> inDegree) {
    List<String> cycleKeys = new ArrayList<>();
    for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
      if (entry.getValue() > 0) {
        cycleKeys.add(entry.getKey());
      }
    }
    Collections.sort(cycleKeys);
    return cycleKeys;
  }

  @Override
  public void calculateAndSaveSchedule(DegreeProgram program) {
    List<StudyModule> studyModules =
        studyModuleRepository.findAllStudyModulesByDegreeProgramId(program.getDegreeProgramId());
    Schedule schedule = calculateSchedule(studyModules);
    scheduleRepository.saveSchedule(program.getDegreeProgramId(), schedule);
  }

  @Override
  public Optional<Schedule> findScheduleByDegreeProgramId(String degreeProgramId) {
    return scheduleRepository.findScheduleByDegreeProgramId(degreeProgramId);
  }

  @Override
  public void deleteSchedule(String degreeProgramId) {
    scheduleRepository.deleteSchedule(degreeProgramId);
  }
}
