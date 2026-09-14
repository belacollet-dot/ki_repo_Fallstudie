package de.likeherotozero.repository;

import de.likeherotozero.entity.ScientistUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScientistUserRepository extends JpaRepository<ScientistUser, Long> {

    Optional<ScientistUser> findByUsername(String username);

    Optional<ScientistUser> findByEmail(String email);

    boolean existsByEmail(String email);
}
