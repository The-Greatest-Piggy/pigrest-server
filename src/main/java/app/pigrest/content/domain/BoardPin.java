package app.pigrest.content.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

// TODO: soft delete 관련 작업 추가 예정
@Entity
public class BoardPin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pin_id")
    private Pin pin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
}
