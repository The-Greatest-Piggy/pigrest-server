package app.pigrest.content.service;

import app.pigrest.common.TestDataFactory;
import app.pigrest.content.domain.Draft;
import app.pigrest.content.domain.DraftRepository;
import app.pigrest.content.domain.Image;
import app.pigrest.global.exception.ForbiddenException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DraftServiceCacheTest {
    @Mock
    private DraftRepository draftRepository;

    @Mock
    private DraftRedisService draftRedisService;

    @InjectMocks
    private DraftService draftService;

    @Test
    @DisplayName("임시 저장본 조회 시, 캐시에 있는 경우")
    void getDraftWithCache_cacheHit() {
        UUID draftId = UUID.randomUUID();
        Member member = TestDataFactory.createMember();
        Image image = TestDataFactory.createImage();
        Draft draftFromCache = TestDataFactory.createDraftFromCache(draftId, member, image);

        given(draftRedisService.getDraft(draftId)).willReturn(draftFromCache);

        Draft result = draftService.getDraftWithCache(draftId, member);
        assertThat(result.getTitle()).isEqualTo("캐시된 제목");
        assertThat(result.getContent()).isEqualTo("캐시된 내용");
        assertThat(result.getMember().getId()).isEqualTo(member.getId());
        assertThat(result.getImage().getOriginalFilename()).isEqualTo("test");

        verify(draftRepository, never()).findByIdAndMember(any(), any()); // DB 조회하지 않음
        verify(draftRedisService).getDraft(draftId);
    }

    @Test
    @DisplayName("임시 저장본 조회 시, 캐시에 없는 경우")
    void getDraftWithCache_cacheMiss_thenDbRestore() {
        Member member = TestDataFactory.createMember();
        Draft draft = TestDataFactory.createDraft(member);

        given(draftRedisService.getDraft(draft.getId())).willReturn(null);
        given(draftRepository.findByIdAndMember(draft.getId(), member)).willReturn(Optional.of(draft));

        Draft result = draftService.getDraftWithCache(draft.getId(), member);

        assertThat(result).isEqualTo(draft);

        verify(draftRepository).findByIdAndMember(draft.getId(), member);
        verify(draftRedisService).autoSave(draft);
    }

    @Test
    @DisplayName("임시 저장본 조회 시, 캐시에 있지만 권한이 없는 경우")
    void getDraftWithCache_cacheHitButAccessDenied() {
        Member member = TestDataFactory.createMember();
        Member unauthorizedMember = TestDataFactory.createMember("unauthorized_piggy");

        UUID draftId = UUID.randomUUID();
        Image image = TestDataFactory.createImage();
        Draft draftFromCache = TestDataFactory.createDraftFromCache(draftId, member, image);

        given(draftRedisService.getDraft(draftId)).willReturn(draftFromCache);

        assertThatThrownBy(() -> draftService.getDraftWithCache(draftId, unauthorizedMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Access denied to this draft.");
    }

}
