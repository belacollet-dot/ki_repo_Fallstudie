package de.likeherotozero.repository;

import de.likeherotozero.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {

    Optional<Country> findByNameIgnoreCase(String name);

    Optional<Country> findByIsoCodeIgnoreCase(String isoCode);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByIsoCodeIgnoreCase(String isoCode);
}
