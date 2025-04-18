package app.pigrest.security;

import app.pigrest.auth.model.CustomUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        String username = annotation.username();
        String password = annotation.password();
        List<SimpleGrantedAuthority> authorities = Stream.of(annotation.roles())
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
        UUID id = UUID.fromString(annotation.id());

        CustomUser principal = new CustomUser(username, password, authorities, id);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, password, authorities);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);

        return context;
    }
}
