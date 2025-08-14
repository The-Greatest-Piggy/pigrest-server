package app.pigrest.content.service;

import app.pigrest.content.domain.Draft;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DraftRedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, String> hashOperations;

    private static final String DRAFT_KEY_PREFIX = "draft:";
    private static final int DRAFT_TTL_MINUTES = 5;

    public void autoSave(Draft draft) {
        String key = generateDraftKey(draft.getId());

        hashOperations.put(key, "memberId", draft.getMember().getId().toString());
        hashOperations.put(key, "imageId", draft.getImage().getId().toString());
        hashOperations.put(key, "title", draft.getTitle() != null ? draft.getTitle() : "");
        hashOperations.put(key, "content", draft.getContent() != null ? draft.getContent() : "");
        hashOperations.put(key, "expiresAt", draft.getExpiresAt().toString());

        redisTemplate.expire(key, DRAFT_TTL_MINUTES, TimeUnit.MINUTES);

        log.debug("Auto-saved draft to Redis: {}", draft.getId());
    }

    private String generateDraftKey(UUID draftId) {
        return DRAFT_KEY_PREFIX + draftId.toString();
    }
}
