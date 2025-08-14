package app.pigrest.content.service;

import app.pigrest.content.domain.Draft;
import app.pigrest.content.domain.DraftRepository;
import app.pigrest.global.common.ApiStatusCode;
import app.pigrest.global.exception.ResourceNotFoundException;
import app.pigrest.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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
}
