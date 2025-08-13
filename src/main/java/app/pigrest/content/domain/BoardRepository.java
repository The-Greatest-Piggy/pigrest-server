package app.pigrest.content.domain;

import app.pigrest.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BoardRepository extends JpaRepository<Board, UUID> {
    boolean existsByOwnerAndDeletedAtIsNull(Member owner);
}
