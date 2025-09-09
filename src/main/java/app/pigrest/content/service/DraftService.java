package app.pigrest.content.service;

import app.pigrest.content.domain.Draft;
import app.pigrest.content.domain.DraftRepository;
import app.pigrest.global.common.ApiStatusCode;
import app.pigrest.global.exception.ForbiddenException;
import app.pigrest.global.exception.ResourceNotFoundException;
import app.pigrest.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DraftService {
    private final DraftRepository draftRepository;
    private final DraftRedisService draftRedisService;

    @Transactional
    public void autoSave(UUID draftId, Member member, String title, String content) {
        Draft draft = getDraft(draftId, member);

        // TODO: image 변경은 다른 API로 분리
        draft.updateFields(title, content);
        draft.extendTtl();
        draftRedisService.autoSave(draft);
    }

    @Transactional(readOnly = true)
    public Draft getDraft(UUID draftId, Member member) {
        // 권한 exception 세세하게 관리 필요성?
        return draftRepository.findByIdAndMember(draftId, member)
                .orElseThrow(() -> new ResourceNotFoundException(ApiStatusCode.RESOURCE_NOT_FOUND, "Draft Not Found"));
    }

    @Transactional(readOnly = true)
    public Draft getDraftWithCache(UUID draftId, Member member) {
        Draft draftFromCache = draftRedisService.getDraft(draftId);
        if (draftFromCache != null && draftFromCache.isValidFromCache()) {
            if (!member.getId().equals(draftFromCache.getMember().getId())) {
                throw new ForbiddenException(ApiStatusCode.FORBIDDEN, "Access denied to this draft.");
            }
            return draftFromCache;
        }

        // Redis에 없거나, 유효하지 않은 경우
        Draft draft = getDraft(draftId, member);
        draftRedisService.autoSave(draft);
        return draft;
    }

    @Scheduled(fixedDelay = 5000)
    public void syncCacheToDatabase() {
        log.info("Scheduler running - checking active drafts");
        Set<String> activeDraftIds = draftRedisService.getActiveDraftIds();
        log.info("Found {} active drafts: {}", activeDraftIds.size(), activeDraftIds);
        if (!activeDraftIds.isEmpty()) {
            // FIXME: Redis I/O 병목이 발생할 수 있을 것 같음. 추후 확인해볼 것
            activeDraftIds.forEach(draftIdStr -> {
                try {
                    UUID draftId = UUID.fromString(draftIdStr);
                    syncSingleDraft(draftId);
                } catch (IllegalArgumentException e) {
                    log.error("Invalid draft ID: {}", draftIdStr);
                } catch (Exception e) {
                    log.error("Failed to sync draft {}: {}", draftIdStr, e.getMessage());
                }
            });
        }
        // TODO: fixedRate과 fixedDelay는 각각 어떤 상황에 알맞는지 판단할 것
    }

    @Transactional
    public void syncSingleDraft(UUID draftId) {
        Draft draftFromCache = draftRedisService.getDraft(draftId);
        if (draftFromCache == null) {
            log.warn("No redis data for draft: {}", draftId);
            draftRedisService.removeFromActiveDrafts(String.valueOf(draftId));
            return;
        }
        if (!draftFromCache.isValidFromCache()) {
            log.warn("Invalid redis data for draft: {}", draftId);
            draftRedisService.removeFromActiveDrafts(String.valueOf(draftId));
            return;
        }

        // TODO: draft not found exception 변경
        Draft draft = draftRepository.findById(draftId)
                .orElseThrow(() -> {
                    log.error("Draft not found in DB: {}", draftId);
                    return new ResourceNotFoundException(ApiStatusCode.RESOURCE_NOT_FOUND, "Draft Not Found");
                });
        draft.updateFields(draftFromCache.getTitle(), draftFromCache.getContent(), draftFromCache.getImage(), draftFromCache.getExpiresAt());
        draftRedisService.removeFromActiveDrafts(String.valueOf(draftId));
    }
}
