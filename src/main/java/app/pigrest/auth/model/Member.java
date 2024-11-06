package app.pigrest.auth.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "member")
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToOne
    @JoinColumn(name = "auth_id", nullable = false)
    private Auth auth;

    @Builder(access = AccessLevel.PRIVATE)
    public Member(String nickname, Auth auth) {
        this.nickname = nickname;
        this.auth = auth;
    }

    public static Member of(String nickname, Auth auth) {
        return Member.builder()
                .nickname(nickname)
                .auth(auth)
                .build();
    }
}
