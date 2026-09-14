package de.likeherotozero.service;

import de.likeherotozero.entity.ScientistUser;
import de.likeherotozero.repository.ScientistUserRepository;

import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScientistUserService {

    private final ScientistUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public ScientistUserService(ScientistUserRepository repository,
                                PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<ScientistUser> findAll() {
        return repository.findAll();
    }

    public Optional<ScientistUser> findById(@NonNull Long id) {
        return repository.findById(id);
    }

    /**
     * Speichert einen User. Wenn das Passwort-Feld leer ist,
     * wird das bestehende Passwort (bereits gehashed) direkt gesetzt.
     * rawPasswordOrEmpty: entweder Klartext-PW (wird gehasht) oder leer (bleibt unverändert).
     */
    public ScientistUser saveWithRawPassword(@NonNull ScientistUser user, String rawPasswordOrEmpty) {
        if (rawPasswordOrEmpty != null && !rawPasswordOrEmpty.isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(rawPasswordOrEmpty));
        }
        return repository.save(user);
    }

    public void deleteById(@NonNull Long id) {
        repository.deleteById(id);
    }

    public Optional<ScientistUser> findByUsername(String username) {
        return repository.findByUsername(username);
    }
}
