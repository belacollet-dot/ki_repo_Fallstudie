package de.likeherotozero.service;

import de.likeherotozero.entity.ScientistUser;
import de.likeherotozero.repository.ScientistUserRepository;
import org.springframework.security.core.GrantedAuthority;
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
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        ScientistUser user = userRepository.findByUsername(usernameOrEmail)
            .or(() -> userRepository.findByEmail(usernameOrEmail))
            .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden: " + usernameOrEmail));

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        return User.builder()
            .username(user.getUsername())
            .password(user.getPasswordHash())
            .authorities(Collections.singletonList(authority))
            .disabled(!user.isEnabled())
            .build();
    }
}
