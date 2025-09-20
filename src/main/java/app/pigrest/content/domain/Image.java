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

    @Column(name = "original_filename", nullable = false, length = 256)
    private String originalFilename;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @CreationTimestamp
    private Instant createdAt;

    @Builder(access = AccessLevel.PRIVATE)
    public Image(String originalFilename, String filePath) {
        this.originalFilename = originalFilename;
        this.filePath = filePath;
    }

    public static Image of(String originalFilename, String filePath) {
        return Image.builder()
                .originalFilename(originalFilename)
                .filePath(filePath)
                .build();
    }
}
