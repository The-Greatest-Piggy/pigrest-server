package app.pigrest.member.controller;

import app.pigrest.auth.model.CustomUser;
import app.pigrest.common.ApiResponse;
import app.pigrest.common.ApiStatusCode;
import app.pigrest.member.dto.response.GetMemberResponse;
import app.pigrest.member.model.Member;
import app.pigrest.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
