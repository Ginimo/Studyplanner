package ch.ost.mas_se.oop.studyplanner.repository;

import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class InMemoryStudyModuleRepository implements StudyModuleRepository {

    // LinkedHashMap: O(1) get/put/remove; insertion order preserved for stable list display.
    // Composite key "degreeProgramId#moduleKey" lets one flat map serve all degree programs
    // without nested maps, while still guaranteeing uniqueness per (program, module) pair.
    private final Map<String, StudyModule> store = new LinkedHashMap<>();

    private String storeKey(String degreeProgramId, String moduleKey) {
        return degreeProgramId + "#" + moduleKey;
    }

    @Override
    public void save(StudyModule module) {
        store.put(storeKey(module.getDegreeProgramId(), module.getStudyModuleKey()), module);
    }

    @Override
    public Optional<StudyModule> findByKey(String degreeProgramId, String key) {
        return Optional.ofNullable(store.get(storeKey(degreeProgramId, key)));
    }

    // O(n): n = total modules across all degree programs
    @Override
    public List<StudyModule> findAllByDegreeProgramId(String degreeProgramId) {
        return store.values().stream()
                .filter(m -> m.getDegreeProgramId().equals(degreeProgramId))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String degreeProgramId, String key) {
        store.remove(storeKey(degreeProgramId, key));
    }
}
