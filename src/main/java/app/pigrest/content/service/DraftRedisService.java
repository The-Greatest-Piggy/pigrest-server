package app.pigrest.content.service;

import app.pigrest.content.domain.Draft;
import app.pigrest.content.domain.Image;
import app.pigrest.member.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DraftRedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, Object> hashOperations;

    public DraftRedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    private static final String DRAFT_KEY_PREFIX = "draft:";
    private static final int DRAFT_TTL_MINUTES = 30;

    public void autoSave(Draft draft) {
        String key = generateDraftKey(draft.getId());

        hashOperations.put(key, "title", draft.getTitle());
        hashOperations.put(key, "content", draft.getContent());
        hashOperations.put(key, "member", draft.getMember());
        hashOperations.put(key, "image", draft.getImage());
        hashOperations.put(key, "expiresAt", draft.getExpiresAt());
        hashOperations.put(key, "createdAt", draft.getCreatedAt());
        hashOperations.put(key, "updatedAt", draft.getUpdatedAt());
        redisTemplate.expire(key, DRAFT_TTL_MINUTES, TimeUnit.MINUTES);

        log.debug("Auto-saved draft to Redis: {}", draft.getId());

        redisTemplate.opsForSet().add("active_drafts", draft.getId().toString());
    }

    public Draft getDraft(UUID draftId) {
        String key = generateDraftKey(draftId);
        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            return null;
        }

        String title = (String) hashOperations.get(key, "title");
        String content = (String) hashOperations.get(key, "content");

        Member member = (Member) hashOperations.get(key, "member");
        Image image = (Image) hashOperations.get(key, "image");
        Instant expiresAt = (Instant) hashOperations.get(key, "expiresAt");
        Instant createdAt = (Instant) hashOperations.get(key, "createdAt");
        Instant updatedAt = (Instant) hashOperations.get(key, "updatedAt");

        return Draft.restoreFromCache(draftId, title, content, member, image, expiresAt, createdAt, updatedAt);
    }

    public Set<String> getActiveDraftIds() {
        Set<Object> activeDraftIds = redisTemplate.opsForSet().members("active_drafts");
        if (activeDraftIds == null) {
            return Collections.emptySet();
        }
        return activeDraftIds.stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    public void removeFromActiveDrafts(String draftId) {
        redisTemplate.opsForSet().remove("active_drafts", draftId);
    }

    private String generateDraftKey(UUID draftId) {
        return DRAFT_KEY_PREFIX + draftId.toString();
    }
}
