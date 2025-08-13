package app.pigrest.content.service;

import app.pigrest.auth.domain.Auth;
import app.pigrest.content.domain.Board;
import app.pigrest.content.domain.BoardRepository;
import app.pigrest.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {
    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private BoardService boardService;

    @Test
    @DisplayName("기본 보드가 존재하지 않으면, 기본 보드를 생성한다.")
    void createDefaultBoard() {
        Member member = createMember();
        given(boardRepository.existsByOwnerAndDeletedAtIsNull(member)).willReturn(false);

        boardService.createDefaultBoard(member);

        verify(boardRepository).save(any(Board.class));
    }


    @Test
    @DisplayName("기본 보드가 존재하면, 생성하지 않는다.")
    void createDefaultBoard_alreadyExists() {
        Member member = createMember();
        given(boardRepository.existsByOwnerAndDeletedAtIsNull(member)).willReturn(true);

        boardService.createDefaultBoard(member);

        verify(boardRepository, never()).save(any(Board.class));
    }

    private Member createMember() {
        Auth auth = Auth.of("pig_eun", "encodedPassword");
        return Member.of("piggy", auth);
    }
}