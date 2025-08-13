package app.pigrest.content.service;

import app.pigrest.content.domain.Board;
import app.pigrest.content.domain.BoardRepository;
import app.pigrest.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    @Transactional
    public void createDefaultBoard(Member member) {
        if (boardRepository.existsByOwnerAndDeletedAtIsNull(member)) {
            log.warn("Default board already exists for member: {}", member.getId());
            return;
        }
        Board defaultBoard = Board.createDefault(member);
        boardRepository.save(defaultBoard);
    }
}
