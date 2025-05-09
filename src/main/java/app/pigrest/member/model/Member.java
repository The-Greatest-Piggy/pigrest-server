package app.pigrest.member.model;

import app.pigrest.auth.model.Auth;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Auth auth;

    @Column(nullable = false, length = 64)
    private String nickname; // 닉네임

    @Column(name = "profile_image_url", length = 1024)
    private String profileImageUrl;

    @Column(length = 600) // 한글 기준으로 200자
    private String description;

    @Builder(access = AccessLevel.PRIVATE)
    public Member(String nickname, Auth auth) {
        this.id = auth.getId();
        this.auth = auth;
        this.nickname = nickname;
    }

    public static Member of(String nickname, Auth auth) {
        return Member.builder()
                .auth(auth)
                .nickname(nickname)
                .build();
    }
}
