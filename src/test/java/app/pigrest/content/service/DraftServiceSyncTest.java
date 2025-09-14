package app.pigrest.content.service;

import app.pigrest.common.TestDataFactory;
import app.pigrest.content.domain.Draft;
import app.pigrest.content.domain.DraftRepository;
import app.pigrest.content.domain.Image;
import app.pigrest.global.common.ApiStatusCode;
import app.pigrest.global.exception.ResourceNotFoundException;
import app.pigrest.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DraftServiceSyncTest {
    @Mock
    private DraftRepository draftRepository;

    @Mock
    private DraftRedisService draftRedisService;

    @InjectMocks
    private DraftService draftService;

    @Test
    @DisplayName("최신 업데이트된 Draft가 없는 경우")
    void syncCacheToDatabase_withEmptyActiveDrafts() {
        Set<String> emptySet = Set.of();
        given(draftRedisService.getActiveDraftIds()).willReturn(emptySet);

        draftService.syncCacheToDatabase();

        verify(draftRedisService, never()).getDraft(any(UUID.class));
        verify(draftRepository, never()).findById(any(UUID.class));
        verify(draftRedisService, never()).removeFromActiveDrafts(any());
    }

    @Test
    @DisplayName("최신 업데이트된 Draft가 있고, 스케줄러가 정상적으로 동기화하는 경우")
    void syncCacheToDatabase_withActiveDrafts() {
        UUID draftId1 = UUID.randomUUID();
        UUID draftId2 = UUID.randomUUID();
        Set<String> activeDraftIds = Set.of(draftId1.toString(), draftId2.toString());

        Member member = TestDataFactory.createMember();
        Image image = TestDataFactory.createImage();
        Draft draftFromCache1 = TestDataFactory.createDraftFromCache(draftId1, member, image);
        Draft draftFromCache2 = TestDataFactory.createDraftFromCache(draftId2, member, image);
        Draft draftFromDb1 = TestDataFactory.createDraftFromDb(draftId1, member, image);
        Draft draftFromDb2 = TestDataFactory.createDraftFromDb(draftId2, member, image);

        given(draftRedisService.getActiveDraftIds()).willReturn(activeDraftIds);
        given(draftRedisService.getDraft(draftId1)).willReturn(draftFromCache1);
        given(draftRedisService.getDraft(draftId2)).willReturn(draftFromCache2);
        given(draftRepository.findById(draftId1)).willReturn(Optional.of(draftFromDb1));
        given(draftRepository.findById(draftId2)).willReturn(Optional.of(draftFromDb2));

        draftService.syncCacheToDatabase();

        verify(draftRedisService, times(2)).getDraft(any(UUID.class));
        verify(draftRepository, times(2)).findById(any(UUID.class));
        verify(draftRedisService).removeFromActiveDrafts(draftId1);
        verify(draftRedisService).removeFromActiveDrafts(draftId2);
    }

    @Test
    @DisplayName("특정 Draft 동기화 실패 시에도 나머지 드래프들은 계속해서 처리한다.")
    void syncCacheToDatabase_withSyncException() {
        UUID validDraftId = UUID.randomUUID();
        UUID problemDraftId = UUID.randomUUID();
        Set<String> activeDraftIds = Set.of(validDraftId.toString(), problemDraftId.toString());
        Member member = TestDataFactory.createMember();
        Image image = TestDataFactory.createImage();
        Draft validDraftFromCache = TestDataFactory.createDraftFromCache(validDraftId, member, image);
        Draft validDraftFromDb = TestDataFactory.createDraftFromDb(validDraftId, member, image);

        given(draftRedisService.getActiveDraftIds()).willReturn(activeDraftIds);
        given(draftRedisService.getDraft(validDraftId)).willReturn(validDraftFromCache);
        given(draftRepository.findById(validDraftId)).willReturn(Optional.of(validDraftFromDb));
        given(draftRedisService.getDraft(problemDraftId)).willThrow(new RuntimeException("Unexpected Exception"));

        draftService.syncCacheToDatabase();

        verify(draftRedisService, times(2)).getDraft(any(UUID.class));
        verify(draftRedisService, times(1)).removeFromActiveDrafts(validDraftId);
        verify(draftRedisService, never()).removeFromActiveDrafts(problemDraftId);
    }

    @Test
    @DisplayName("하나의 Draft에 대하여 동기화 성공한 경우")
    void syncSingleDraft_success() {
        UUID draftId = UUID.randomUUID();
        Member member = TestDataFactory.createMember();
        Image image = TestDataFactory.createImage();
        Draft draftFromCache = TestDataFactory.createDraftFromCache(draftId, member, image);
        Draft draftFromDb = TestDataFactory.createDraftFromDb(draftId, member, image);

        given(draftRedisService.getDraft(draftId)).willReturn(draftFromCache);
        given(draftRepository.findById(draftId)).willReturn(Optional.of(draftFromDb));

        draftService.syncSingleDraft(draftId);

        verify(draftRedisService, times(1)).getDraft(draftId);
        verify(draftRepository, times(1)).findById(draftId);
        verify(draftRedisService, times(1)).removeFromActiveDrafts(draftId);
        assertEquals(draftFromCache.getTitle(), draftFromDb.getTitle());
        assertEquals(draftFromCache.getContent(), draftFromDb.getContent());
        assertEquals(draftFromCache.getImage(), draftFromDb.getImage());
        assertEquals(draftFromCache.getExpiresAt(), draftFromDb.getExpiresAt());
    }

    @Test
    @DisplayName("Redis에 draftId에 해당하는 데이터가 없는 경우")
    void syncSingleDraft_noCacheData() {
        UUID draftId = UUID.randomUUID();
        given(draftRedisService.getDraft(draftId)).willReturn(null);

        draftService.syncSingleDraft(draftId);

        verify(draftRedisService, times(1)).getDraft(draftId);
        verify(draftRedisService, times(1)).removeFromActiveDrafts(draftId);
        verify(draftRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Redis에 저장된 데이터가 유효하지 않은 경우")
    void syncSingleDraft_invalidCacheData() {
        UUID draftId = UUID.randomUUID();
        Draft draftFromCache = TestDataFactory.createInvalidDraftFromCache(draftId);

        given(draftRedisService.getDraft(draftId)).willReturn(draftFromCache);

        draftService.syncSingleDraft(draftId);

        verify(draftRedisService, times(1)).getDraft(draftId);
        verify(draftRedisService, times(1)).removeFromActiveDrafts(draftId);
        verify(draftRepository, never()).findById(any());
    }

    @Test
    @DisplayName("DB에 draftId에 해당하는 데이터가 없는 경우")
    void syncSingleDraft_draftNotFoundInDB() {
        UUID draftId = UUID.randomUUID();
        Member member = TestDataFactory.createMember();
        Image image = TestDataFactory.createImage();
        Draft draftFromCache = TestDataFactory.createDraftFromCache(draftId, member, image);

        given(draftRedisService.getDraft(draftId)).willReturn(draftFromCache);
        given(draftRepository.findById(draftId)).willReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> draftService.syncSingleDraft(draftId));
        assertEquals(ApiStatusCode.RESOURCE_NOT_FOUND, exception.getStatusCode());
        assertEquals("Draft Not Found", exception.getMessage());

        verify(draftRedisService, times(1)).getDraft(draftId);
        verify(draftRepository, times(1)).findById(draftId);
        verify(draftRedisService, never()).removeFromActiveDrafts(draftId);

    }
}
