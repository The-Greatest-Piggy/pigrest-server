package app.pigrest.auth.model;

import app.pigrest.member.model.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "auth")
@NoArgsConstructor
public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "auth", cascade = CascadeType.ALL)
    private Member member;

    @Builder(access = AccessLevel.PRIVATE)
    public Auth(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static Auth of(String username, String password) {
        return Auth.builder()
                .username(username)
                .password(password)
                .build();
    }
}
