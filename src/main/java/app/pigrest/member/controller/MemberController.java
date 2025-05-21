package app.pigrest.member.controller;

import app.pigrest.auth.model.CustomUser;
import app.pigrest.common.ApiResponse;
import app.pigrest.common.ApiStatusCode;
import app.pigrest.exception.ForbiddenException;
import app.pigrest.member.dto.request.UpdateMemberRequest;
import app.pigrest.member.dto.response.GetMemberResponse;
import app.pigrest.member.model.Member;
import app.pigrest.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<GetMemberResponse>> getMember(@PathVariable String username, @AuthenticationPrincipal CustomUser currentUser) {
        Member member = memberService.getMember(username);

        return ResponseEntity.ok(ApiResponse.success(
                ApiStatusCode.OK,
                "User Profile retrieved successfully",
                GetMemberResponse.of(member, currentUser)
        ));
    }

    @PutMapping("/{username}")
    public ResponseEntity<ApiResponse<Void>> updateProfile(@PathVariable String username,
                                                        @AuthenticationPrincipal CustomUser currentUser,
                                                        @Valid @RequestBody UpdateMemberRequest request) {
        if (!currentUser.getUsername().equals(username)) {
            throw new ForbiddenException(ApiStatusCode.FORBIDDEN, "No permission to modify this user.");
        }
        memberService.updateProfile(username, request);

        return ResponseEntity.ok(ApiResponse.success(ApiStatusCode.OK, "User Profile updated successfully", null));
    }
}
