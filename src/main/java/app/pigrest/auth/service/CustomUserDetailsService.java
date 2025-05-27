package app.pigrest.auth.service;

import app.pigrest.auth.model.Auth;
import app.pigrest.auth.model.CustomUser;
import app.pigrest.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Auth auth = authRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        return new CustomUser(auth.getUsername(),
                auth.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                auth.getId());
    }
}
