package app.pigrest.content.domain;

import app.pigrest.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DraftRepository extends JpaRepository<Draft, UUID> {
    Optional<Draft> findByIdAndMember(UUID id, Member member);
}
