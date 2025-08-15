package app.pigrest.content.domain;

import app.pigrest.member.domain.Member;
import com.fasterxml.uuid.Generators;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

@Getter
@Entity
@Table(name = "draft")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Draft {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @Builder(access = AccessLevel.PRIVATE)
    public Draft(Member member, Image image, String title, String content) {
        this.id = Generators.timeBasedEpochRandomGenerator().generate();
        this.member = member;
        this.image = image;
        this.title = title;
        this.content = content;
        this.expiresAt = Instant.now().plus(7, ChronoUnit.DAYS);
    }

    public static Draft createFromImage(Member member, Image image) {
        return Draft.builder()
                .member(member)
                .image(image)
                .title("제목 없음")
                .content("")
                .build();
    }

    public static Draft createFromPin(Member member, Pin pin) {
        // TODO: 수정 시, Pin을 복사하여 임시 저장 파일 생성
        return Draft.builder()
                .build();
    }

    public void extendTtl() {
        this.expiresAt = Instant.now().plus(7, ChronoUnit.DAYS);
    }

    public void updateFields(String title, String content) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        extendTtl();
    }

    public static Draft restoreFromRedis(Map<String, String> draftData, Member member) {
        Draft draft = Draft.builder()
                .title(draftData.get("title"))
                .content(draftData.get("content"))
                .member(member)
                .build();
        draft.extendTtl();
        return draft;
    }
}
