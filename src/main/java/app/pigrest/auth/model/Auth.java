package app.pigrest.auth.model;

import app.pigrest.member.model.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "auth")
@NoArgsConstructor
public class Auth {
    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username; // 사용자가 정의하는 인증용 아이디

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

    @OneToOne(mappedBy = "auth")
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
