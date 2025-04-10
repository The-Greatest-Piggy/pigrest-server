package app.pigrest.auth.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.UUID;

@Getter
public class CustomUser extends User {
    private final UUID id;

    @Builder
    public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities, UUID id) {
        super(username, password, authorities);
        this.id = id;
    }
}
