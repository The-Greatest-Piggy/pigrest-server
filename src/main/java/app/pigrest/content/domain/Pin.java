package app.pigrest.content.domain;

import com.fasterxml.uuid.Generators;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "pin")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pin {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image image;

    @Column(nullable = false)
    private String title;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    private Instant deletedAt;

    @Builder(access = AccessLevel.PRIVATE)
    public Pin(Image image, String title) {
        this.id = Generators.timeBasedEpochRandomGenerator().generate();
        this.image = image;
        this.title = title;
    }

    public static Pin of(Image image, String title) {
        return Pin.builder()
                .image(image)
                .title(title)
                .build();
    }
}
