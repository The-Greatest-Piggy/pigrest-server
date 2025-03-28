package app.pigrest.member.model;

import app.pigrest.auth.model.Auth;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Getter
@Entity
@Table(name = "member")
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id; // TODO: UUID 버전 변경 예정

    @Column(nullable = false)
    private String username;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToOne
    @JoinColumn(name = "auth_id", nullable = false)
    private Auth auth;

    @Builder(access = AccessLevel.PRIVATE)
    public Member(String username, Auth auth) {
        this.username = username;
        this.auth = auth;
    }

    public static Member of(String username, Auth auth) {
        return Member.builder()
                .username(username)
                .auth(auth)
                .build();
    }
}
