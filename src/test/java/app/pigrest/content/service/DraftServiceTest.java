package app.pigrest.content.service;

import app.pigrest.common.TestDataFactory;
import app.pigrest.content.domain.Draft;
import app.pigrest.content.domain.DraftRepository;
import app.pigrest.global.exception.ResourceNotFoundException;
import app.pigrest.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DraftServiceTest {
    @Mock
    private DraftRepository draftRepository;

    @Mock
    private DraftRedisService draftRedisService;

    @InjectMocks
    private DraftService draftService;

    @Test
    @DisplayName("임시 저장본을 조회한다.")
    void getDraft() {
        Member member = TestDataFactory.createMember();
        Draft draft = TestDataFactory.createDraft(member);
        given(draftRepository.findByIdAndMember(draft.getId(), member)).willReturn(Optional.of(draft));

        Draft result = draftService.getDraft(draft.getId(), member);

        assertThat(result).isEqualTo(draft);
    }

    @Test
    @DisplayName("존재하지 않는 임시 저장본을 조회 시 예외가 발생한다.")
    void getDraft_notFound() {
        Member member = TestDataFactory.createMember();
        UUID draftId = UUID.randomUUID();
        given(draftRepository.findByIdAndMember(draftId, member)).willReturn(Optional.empty());

        assertThatThrownBy(() -> draftService.getDraft(draftId, member))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Draft Not Found");
    }

    @Test
    @DisplayName("임시 저장본을 자동 저장할 수 있다.")
    void autoSave() {
        Member member = TestDataFactory.createMember();
        Draft draft = TestDataFactory.createDraft(member);
        String newTitle = "새로운 제목";
        String newContent = "새로운 내용";
        given(draftRepository.findByIdAndMember(draft.getId(), member)).willReturn(Optional.of(draft));

        draftService.autoSave(draft.getId(), member, newTitle, newContent);

        assertThat(draft.getTitle()).isEqualTo(newTitle);
        assertThat(draft.getContent()).isEqualTo(newContent);
        verify(draftRedisService).autoSave(draft);
    }

    @Test
    @DisplayName("임시 저장본 자동 저장 중 title만 업데이트할 수 있다.")
    void autoSave_updateTitleOnly() {
        Member member = TestDataFactory.createMember();
        Draft draft = TestDataFactory.createDraft(member);
        String newTitle = "새로운 제목";
        String originalContent = draft.getContent();
        given(draftRepository.findByIdAndMember(draft.getId(), member)).willReturn(Optional.of(draft));

        draftService.autoSave(draft.getId(), member, newTitle, null);

        assertThat(draft.getTitle()).isEqualTo(newTitle);
        assertThat(draft.getContent()).isEqualTo(originalContent);
        verify(draftRedisService).autoSave(draft);
    }
    
    @Test
    @DisplayName("임시 저장본 자동 저장 중 content만 업데이트할 수 있다.")
    void autoSave_updateContentOnly() {
        Member member = TestDataFactory.createMember();
        Draft draft = TestDataFactory.createDraft(member);
        String originalTitle = draft.getTitle();
        String newContent = "새로운 내용";
        given(draftRepository.findByIdAndMember(draft.getId(), member)).willReturn(Optional.of(draft));

        draftService.autoSave(draft.getId(), member, null, newContent);

        assertThat(draft.getTitle()).isEqualTo(originalTitle);
        assertThat(draft.getContent()).isEqualTo(newContent);
        verify(draftRedisService).autoSave(draft);
    }

    @Test
    @DisplayName("존재하지 않는 임시 저장본을 자동 저장하고자 할 때 예외가 발생한다.")
    void autoSave_draftNotFound() {
        Member member = TestDataFactory.createMember();
        UUID draftId = UUID.randomUUID();
        String newTitle = "새로운 제목";
        String newContent = "새로운 내용";

        given(draftRepository.findByIdAndMember(draftId, member)).willReturn(Optional.empty());

        assertThatThrownBy(() -> draftService.autoSave(draftId, member, newTitle, newContent))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Draft Not Found");
    }
}