package de.likeherotozero.service;

import de.likeherotozero.entity.ScientistUser;
import de.likeherotozero.entity.UserRole;
import de.likeherotozero.repository.ScientistUserRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ScientistUserService {

    private final ScientistUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ScientistUserService(ScientistUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<ScientistUser> getAllUsers() {
        return userRepository.findAll();
    }

    public ScientistUser getUserById(@NonNull Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Benutzer nicht gefunden mit ID: " + id));
    }

    public ScientistUser getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Benutzer nicht gefunden mit E-Mail: " + email));
    }

    public ScientistUser createUser(String name, String email, String password, UserRole role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("E-Mail existiert bereits: " + email);
        }

        ScientistUser user = new ScientistUser();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role != null ? role : UserRole.SCIENTIST);

        return userRepository.save(user);
    }

    @SuppressWarnings("null")
    public ScientistUser updateUser(Long id, String name, String email, UserRole role) {
        ScientistUser user = getUserById(id);

        if (name != null && !name.trim().isEmpty()) {
            user.setName(name);
        }
        if (email != null && !email.trim().isEmpty()) {
            user.setEmail(email);
        }
        if (role != null) {
            user.setRole(role);
        }

        return userRepository.save(user);
    }

    public void updateUserPassword(@NonNull Long id, String newPassword) {
        ScientistUser user = getUserById(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void deleteUser(@NonNull Long id) {
        userRepository.deleteById(id);
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
