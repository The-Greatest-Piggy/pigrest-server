package app.pigrest.content.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "pin")
@NoArgsConstructor
public class Pin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 작성자 식별자 (실제 서비스에서는 SecurityContext에서 처리할 수 있습니다)
    @Column(name = "member_id", nullable = false)
    private String memberId;

    @Column(nullable = false)
    private String title;

    // S3 업로드 후 받은 이미지 URL
    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    // 게시글 내용
    @Column(columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder(access = AccessLevel.PRIVATE)
    public Pin(String memberId, String title, String imageUrl, String content) {
        this.memberId = memberId;
        this.title = title;
        this.imageUrl = imageUrl;
        this.content = content;
    }

    public static Pin of(String memberId, String title, String imageUrl, String content) {
        return Pin.builder()
                .memberId(memberId)
                .title(title)
                .imageUrl(imageUrl)
                .content(content)
                .build();
    }

    public void updateContent(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void updateImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void markDeleted() {
        this.deletedAt = LocalDateTime.now();
    }
}