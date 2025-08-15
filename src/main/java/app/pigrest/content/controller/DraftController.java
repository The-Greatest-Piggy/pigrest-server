package app.pigrest.content.controller;

import app.pigrest.content.domain.Draft;
import app.pigrest.content.dto.request.DraftAutoSaveRequest;
import app.pigrest.content.dto.response.GetDraftResponse;
import app.pigrest.content.service.DraftService;
import app.pigrest.global.common.ApiResponse;
import app.pigrest.global.common.ApiStatusCode;
import app.pigrest.global.security.CustomUser;
import app.pigrest.member.domain.Member;
import app.pigrest.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/drafts")
@RequiredArgsConstructor
public class DraftController {
    private final DraftService draftService;
    private final MemberService memberService;

    @PutMapping("/{draftId}")
    public ResponseEntity<ApiResponse<Object>> autoSave(
            @PathVariable UUID draftId,
            @RequestBody DraftAutoSaveRequest request,
            @AuthenticationPrincipal CustomUser user) {
        Member member = memberService.getMember(user.getUsername());
        draftService.autoSave(draftId, member, request.getTitle(), request.getContent());

        return ResponseEntity.ok(ApiResponse.noContent(""));
    }

    @GetMapping("/{draftId}")
    public ResponseEntity<ApiResponse<GetDraftResponse>> getDraft(
            @PathVariable UUID draftId,
            @AuthenticationPrincipal CustomUser user) {
        Member member = memberService.getMember(user.getUsername());
        Draft draft = draftService.getDraftWithCache(draftId, member);
        System.out.println("왜 때문일까");

        return ResponseEntity.ok(ApiResponse.success(
                ApiStatusCode.OK,
                "Draft retrieved successfully",
                GetDraftResponse.of(draft)));
    }
}
