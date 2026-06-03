package ch.ost.mas_se.oop.studyplanner.repository;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;

import java.util.List;
import java.util.Optional;

public interface DegreeProgramRepository {

    void save(DegreeProgram program);

    Optional<DegreeProgram> findById(String id);

    List<DegreeProgram> findAll();

    void delete(String id);
}
