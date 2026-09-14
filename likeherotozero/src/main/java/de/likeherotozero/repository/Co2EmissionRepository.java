package de.likeherotozero.repository;

import de.likeherotozero.entity.Co2EmissionRecord;
import de.likeherotozero.entity.Country;
import de.likeherotozero.entity.ScientistUser;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface Co2EmissionRepository extends JpaRepository<Co2EmissionRecord, Long> {

    List<Co2EmissionRecord> findByCountryOrderByReportingYearDesc(Country country);

    Optional<Co2EmissionRecord> findTopByCountryOrderByReportingYearDesc(Country country);

    Optional<Co2EmissionRecord> findTopByCountryOrderByCreatedAtDesc(Country country);

    List<Co2EmissionRecord> findByCountry_NameIgnoreCaseOrderByReportingYearDesc(String countryName);

    boolean existsByCountryAndReportingYear(Country country, Integer reportingYear);

    boolean existsByCountry_IsoCodeAndReportingYear(String isoCode, Integer reportingYear);

    boolean existsByCountryAndReportingYearAndIdNot(Country country, Integer reportingYear, Long id);

    List<Co2EmissionRecord> findByCreatedByUser(ScientistUser user);

}
