package de.likeherotozero.repository;

import de.likeherotozero.entity.Co2EmissionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface Co2EmissionRepository extends JpaRepository<Co2EmissionRecord, Long> {
    List<Co2EmissionRecord> findByCountryIdOrderByYearDesc(Long countryId);
    List<Co2EmissionRecord> findByScientistIdOrderByYearDesc(Long scientistId);
    List<Co2EmissionRecord> findByCountryIdAndYear(Long countryId, Integer year);
}
