package de.likeherotozero.service;

import de.likeherotozero.entity.ScientistUser;
import de.likeherotozero.repository.ScientistUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final ScientistUserRepository userRepository;

    public CustomUserDetailsService(ScientistUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        ScientistUser user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden: " + email));

        return new User(
            user.getEmail(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
