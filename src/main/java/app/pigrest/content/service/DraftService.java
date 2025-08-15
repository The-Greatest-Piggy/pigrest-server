package app.pigrest.content.service;

import app.pigrest.content.domain.Draft;
import app.pigrest.content.domain.DraftRepository;
import app.pigrest.global.common.ApiStatusCode;
import app.pigrest.global.exception.ForbiddenException;
import app.pigrest.global.exception.ResourceNotFoundException;
import app.pigrest.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
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
        draft.updateFields(title, content);
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
        Map<String, String> draftData = draftRedisService.getDraftDataFromRedis(draftId);
        if (!draftData.isEmpty()) {
            String memberId = draftData.get("memberId");
            if (!member.getId().toString().equals(memberId)) {
                throw new ForbiddenException(ApiStatusCode.FORBIDDEN, "Access denied to this draft.");
            }
            return Draft.restoreFromRedis(draftData, member);
        }

        // redis에 없는 경우 DB에서 복원
        Draft draft = getDraft(draftId, member);
        draftRedisService.autoSave(draft);
        return draft;
    }
}
