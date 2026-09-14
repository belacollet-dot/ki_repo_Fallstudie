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

    public ScientistUser createUser(String username, String email, String password, String name, UserRole role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Benutzername existiert bereits: " + username);
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("E-Mail existiert bereits: " + email);
        }

        ScientistUser user = new ScientistUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setName(name);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role != null ? role : UserRole.SCIENTIST);
        user.setEnabled(true);

        return userRepository.save(user);
    }

    @SuppressWarnings("null")
    public ScientistUser updateUser(Long id, String username, String email, String name, UserRole role) {
        ScientistUser user = getUserById(id);

        if (username != null && !username.trim().isEmpty()) {
            user.setUsername(username);
        }
        if (email != null && !email.trim().isEmpty()) {
            user.setEmail(email);
        }
        if (name != null && !name.trim().isEmpty()) {
            user.setName(name);
        }
        if (role != null) {
            user.setRole(role);
        }

        return userRepository.save(user);
    }

    public void updateUserPassword(@NonNull Long id, String newPassword) {
        ScientistUser user = getUserById(id);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void deleteUser(@NonNull Long id) {
        userRepository.deleteById(id);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
