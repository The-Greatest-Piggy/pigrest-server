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
    public Draft(UUID id, Member member, Image image, String title, String content, Instant expiresAt) {
        this.id = id != null ? id : Generators.timeBasedEpochRandomGenerator().generate();
        this.member = member;
        this.image = image;
        this.title = title;
        this.content = content;
        this.expiresAt = expiresAt != null ? expiresAt : Instant.now().plus(7, ChronoUnit.DAYS);
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

    public static Draft restoreFromCache(UUID id, String title, String content,
                                         Member member, Image image, Instant expiresAt,
                                         Instant createdAt, Instant updatedAt) {
        Draft draft = Draft.builder()
                .id(id)
                .title(title)
                .content(content)
                .member(member)
                .image(image)
                .expiresAt(expiresAt)
                .build();
        draft.setCreatedAtForRestore(createdAt);
        draft.setUpdatedAtForRestore(updatedAt);
        draft.extendTtl();
        return draft;
    }

    public void extendTtl() {
        this.expiresAt = Instant.now().plus(7, ChronoUnit.DAYS);
    }

    public void updateFields(String title, String content) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
    }

    public void updateFields(String title, String content, Image image, Instant expiresAt) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        if (image != null) this.image = image;
        if (expiresAt != null) this.expiresAt = expiresAt;
    }

    public boolean isValidFromCache() {
        return member != null && image != null &&
                expiresAt != null && createdAt != null && updatedAt != null;
    }

    private void setCreatedAtForRestore(Instant createdAt) {
        this.createdAt = createdAt;
    }

    private void setUpdatedAtForRestore(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
