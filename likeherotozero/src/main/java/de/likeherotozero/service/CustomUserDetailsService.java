package de.likeherotozero.service;

import de.likeherotozero.entity.ScientistUser;
import de.likeherotozero.repository.ScientistUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final ScientistUserRepository scientistUserRepository;

    public CustomUserDetailsService(ScientistUserRepository scientistUserRepository) {
        this.scientistUserRepository = scientistUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ScientistUser scientistUser = scientistUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden: " + username));

        return User.builder()
                .username(scientistUser.getUsername())
                .password(scientistUser.getPasswordHash())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + scientistUser.getRole().name())))
                .disabled(!scientistUser.getEnabled())
                .build();
    }
}
