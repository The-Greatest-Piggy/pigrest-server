package app.pigrest.content.domain;

import app.pigrest.member.domain.Member;
import com.fasterxml.uuid.Generators;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "board")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Member owner;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate = true;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "deleted_at", columnDefinition = "TIMESTAMP")
    private Instant deletedAt;

    @Builder(access = AccessLevel.PRIVATE)
    public Board(Member owner, String title, String description, boolean isPrivate) {
        this.id = Generators.timeBasedEpochRandomGenerator().generate();
        this.owner = owner;
        this.title = title;
        this.description = description;
        this.isPrivate = isPrivate;
    }

    public static Board createDefault(Member owner) {
        return Board.builder()
                .owner(owner)
                .title("내 보드")
                .description("기본 보드 입니다.")
                .isPrivate(true)
                .build();
    }

    public void softDelete() {
        this.deletedAt = Instant.now();
    }
}
