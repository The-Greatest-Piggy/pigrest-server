package app.pigrest.auth.model;

import app.pigrest.member.model.Member;
import com.fasterxml.uuid.Generators;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
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

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt; // 비밀번호 변경 시

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // soft delete

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
