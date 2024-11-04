package app.pigrest.auth.repository;

import app.pigrest.auth.model.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    Auth findByUsername(String username);
}
