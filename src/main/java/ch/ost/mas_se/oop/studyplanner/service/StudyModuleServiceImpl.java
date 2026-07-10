package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.exception.DuplicateStudyModuleKeyException;
import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import ch.ost.mas_se.oop.studyplanner.repository.StudyModuleRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class StudyModuleServiceImpl implements StudyModuleService {
  private final StudyModuleRepository studyModuleRepository;
  private final ScheduleService scheduleService;

  public StudyModuleServiceImpl(
      StudyModuleRepository studyModuleRepository, ScheduleService scheduleService) {
    this.studyModuleRepository = studyModuleRepository;
    this.scheduleService = scheduleService;
  }

  @Override
  public void createStudyModule(
      String studyModuleKey,
      String degreeProgramId,
      String name,
      String description,
      int ects,
      List<String> prerequisiteKeys,
      LocalDate offeredFrom,
      LocalDate offeredTo) {
    if (studyModuleRepository.findStudyModuleByKey(degreeProgramId, studyModuleKey).isPresent()) {
      throw new DuplicateStudyModuleKeyException(studyModuleKey, degreeProgramId);
    }
    studyModuleRepository.saveStudyModule(
        new StudyModule(
            studyModuleKey,
            degreeProgramId,
            name,
            description,
            ects,
            prerequisiteKeys,
            offeredFrom,
            offeredTo));
    scheduleService.deleteSchedule(degreeProgramId);
  }

  @Override
  public Optional<StudyModule> findStudyModuleByKey(String degreeProgramId, String studyModuleKey) {
    return studyModuleRepository.findStudyModuleByKey(degreeProgramId, studyModuleKey);
  }

  @Override
  public List<StudyModule> findAllStudyModulesByDegreeProgramId(String degreeProgramId) {
    return studyModuleRepository.findAllStudyModulesByDegreeProgramId(degreeProgramId);
  }

  @Override
  public List<StudyModule> findStudyModulesByKeys(
      String degreeProgramId, List<String> studyModuleKeys) {
    Set<String> studyModuleKeySet = new HashSet<>(studyModuleKeys);
    return studyModuleRepository.findAllStudyModulesByDegreeProgramId(degreeProgramId).stream()
        .filter(m -> studyModuleKeySet.contains(m.getStudyModuleKey()))
        .toList();
  }

  @Override
  public List<StudyModule> findFilteredStudyModules(
      String degreeProgramId,
      String nameFilter,
      LocalDate offeredAt,
      String sortBy,
      String sortDir) {
    return studyModuleRepository.findAllStudyModulesByDegreeProgramId(degreeProgramId).stream()
        .filter(
            m ->
                nameFilter == null
                    || nameFilter.isBlank()
                    || m.getName().toLowerCase().contains(nameFilter.toLowerCase()))
        .filter(
            m ->
                offeredAt == null
                    || (m.getOfferedFrom() == null || !offeredAt.isBefore(m.getOfferedFrom()))
                        && (m.getOfferedTo() == null || !offeredAt.isAfter(m.getOfferedTo())))
        .sorted(buildComparator(sortBy, sortDir))
        .toList();
  }

  private static Comparator<StudyModule> buildComparator(String sortBy, String sortDir) {
    Comparator<StudyModule> comparator =
        switch (sortBy) {
          case "OFFERED_FROM" ->
              Comparator.comparing(
                  StudyModule::getOfferedFrom, Comparator.nullsLast(Comparator.naturalOrder()));
          case "OFFERED_TO" ->
              Comparator.comparing(
                  StudyModule::getOfferedTo, Comparator.nullsLast(Comparator.naturalOrder()));
          case "ECTS" -> Comparator.comparingInt(StudyModule::getEcts);
          default -> Comparator.comparing(StudyModule::getName, String.CASE_INSENSITIVE_ORDER);
        };
    return "DESC".equals(sortDir) ? comparator.reversed() : comparator;
  }

  @Override
  public void updateStudyModule(
      String studyModuleKey,
      String degreeProgramId,
      String name,
      String description,
      int ects,
      List<String> prerequisiteKeys,
      LocalDate offeredFrom,
      LocalDate offeredTo) {
    studyModuleRepository.saveStudyModule(
        new StudyModule(
            studyModuleKey,
            degreeProgramId,
            name,
            description,
            ects,
            prerequisiteKeys,
            offeredFrom,
            offeredTo));
    scheduleService.deleteSchedule(degreeProgramId);
  }

  @Override
  public void deleteAllStudyModulesByDegreeProgramId(String degreeProgramId) {
    studyModuleRepository.deleteAllStudyModulesByDegreeProgramId(degreeProgramId);
    scheduleService.deleteSchedule(degreeProgramId);
  }

  @Override
  public Map<String, StudyModule> findModuleMapByDegreeProgramId(String degreeProgramId) {
    return studyModuleRepository.findAllStudyModulesByDegreeProgramId(degreeProgramId).stream()
        .collect(Collectors.toMap(StudyModule::getStudyModuleKey, Function.identity()));
  }

  @Override
  public void deleteStudyModule(String degreeProgramId, String studyModuleKey) {
    List<StudyModule> updated =
        studyModuleRepository.findAllStudyModulesByDegreeProgramId(degreeProgramId).stream()
            .filter(module -> !module.getStudyModuleKey().equals(studyModuleKey))
            .map(
                module ->
                    module.getPrerequisiteKeys().contains(studyModuleKey)
                        ? module.withoutPrerequisite(studyModuleKey)
                        : module)
            .toList();
    studyModuleRepository.replaceAllStudyModulesForProgram(degreeProgramId, updated);
    scheduleService.deleteSchedule(degreeProgramId);
  }
}
