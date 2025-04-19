package app.pigrest.auth.model;

import app.pigrest.member.model.Member;
import com.fasterxml.uuid.Generators;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "auth")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Auth {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Setter
    @OneToOne(mappedBy = "auth", cascade = CascadeType.ALL)
    private Member member;

    @Column(unique = true, nullable = false, length = 64)
    private String username;

    @Column(nullable = false, length = 64)
    private String password;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    @UpdateTimestamp
    private Instant updatedAt; // 비밀번호 변경 시

    @Column(name = "deleted_at", columnDefinition = "TIMESTAMP")
    private Instant deletedAt; // soft delete

    @Builder(access = AccessLevel.PRIVATE)
    public Auth(String username, String password) {
        this.id = Generators.timeBasedEpochRandomGenerator().generate();
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
