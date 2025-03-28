package app.pigrest.auth.controller;

import app.pigrest.auth.dto.request.LoginRequest;
import app.pigrest.auth.dto.response.RegisterResponse;
import app.pigrest.common.ApiResponse;
import app.pigrest.common.ApiStatusCode;
import app.pigrest.member.model.Member;
import app.pigrest.member.service.MemberService;
import app.pigrest.auth.dto.request.RegisterRequest;
import app.pigrest.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@RequestBody RegisterRequest request) {
        Member member = authService.create(request);
        // TODO: 실패 케이스 - RegisterRequest에 유효성 검사 관련 처리 추가
        return ResponseEntity.ok(
                ApiResponse.success(
                        ApiStatusCode.CREATED,
                        "Member id: " + member.getId() + " registered successfully",
                        RegisterResponse.from(member)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(@RequestBody LoginRequest request) {
        authService.login(request);
        return ResponseEntity.ok(ApiResponse.noContent("Login successful"));
    }
}
