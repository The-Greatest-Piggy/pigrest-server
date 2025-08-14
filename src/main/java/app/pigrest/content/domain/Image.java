package app.pigrest.content.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Entity
@Table(name = "image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "image")
    private Pin pin;

    @Column(name = "filename", nullable = false, length = 256)
    private String fileName;

    @CreationTimestamp
    private Instant createdAt;

    @Builder(access = AccessLevel.PRIVATE)
    public Image(String fileName) {
        this.fileName = fileName;
    }

    public static Image of(String fileName) {
        return Image.builder()
                .fileName(fileName)
                .build();
    }
}
