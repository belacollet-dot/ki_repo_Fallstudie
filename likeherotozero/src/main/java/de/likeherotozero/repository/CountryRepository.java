package de.likeherotozero.repository;

import de.likeherotozero.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;


public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByName(String name);
    List<Country> findAllByNameContainingIgnoreCase(String name);
}
