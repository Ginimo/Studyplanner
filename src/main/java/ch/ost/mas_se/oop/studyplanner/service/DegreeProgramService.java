package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;

import java.util.List;
import java.util.Optional;

public interface DegreeProgramService {

    void create(DegreeProgram program);

    Optional<DegreeProgram> findById(String id);

    List<DegreeProgram> findAll();

    void update(DegreeProgram program);

    void delete(String id);
}
