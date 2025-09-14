package app.pigrest.content.domain;

import app.pigrest.member.domain.Member;
import com.fasterxml.uuid.Generators;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "pin",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_member_image", columnNames = {"member_id", "image_id"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pin {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image image;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    private Instant deletedAt;

    @Builder(access = AccessLevel.PRIVATE)
    public Pin(Member member, Image image, String title, String content) {
        this.id = Generators.timeBasedEpochRandomGenerator().generate();
        this.member = member;
        this.image = image;
        this.title = title;
        this.content = content;
    }

    public static Pin of(Image image, String title) {
        return Pin.builder()
                .image(image)
                .title(title)
                .build();
    }

    public static Pin create(Member member, Image image, String title, String content) {
        return Pin.builder()
                .member(member)
                .image(image)
                .title(title)
                .content(content)
                .build();
    }
}
