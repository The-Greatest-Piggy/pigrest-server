package app.pigrest.auth.controller;

import app.pigrest.auth.dto.request.LoginRequest;
import app.pigrest.auth.dto.response.LoginResponse;
import app.pigrest.auth.dto.response.RegisterResponse;
import app.pigrest.auth.service.JwtService;
import app.pigrest.common.ApiResponse;
import app.pigrest.common.ApiStatusCode;
import app.pigrest.member.model.Member;
import app.pigrest.auth.dto.request.RegisterRequest;
import app.pigrest.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@RequestBody RegisterRequest request) {
        Member member = authService.create(request);
        // TODO: 실패 케이스 - RegisterRequest에 유효성 검사 관련 처리 추가
        return ResponseEntity.ok(
                ApiResponse.success(
                        ApiStatusCode.CREATED,
                        "Member registered successfully",
                        RegisterResponse.from(member)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        Authentication auth = authService.login(request);
        UserDetails userDetails = userDetailsService.loadUserByUsername(auth.getName());

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        // TODO: Redis에 refresh token 저장

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.success(
                        ApiStatusCode.OK,
                        "Login successful",
                        LoginResponse.of(accessToken, userDetails.getUsername())
                ));
    }
}
