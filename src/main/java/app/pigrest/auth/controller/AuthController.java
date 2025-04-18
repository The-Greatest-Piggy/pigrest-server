package app.pigrest.auth.controller;

import app.pigrest.auth.dto.request.CheckUsernameRequest;
import app.pigrest.auth.dto.request.LoginRequest;
import app.pigrest.auth.dto.response.CheckUsernameResponse;
import app.pigrest.auth.dto.response.LoginResponse;
import app.pigrest.auth.dto.response.RegisterResponse;
import app.pigrest.auth.model.Auth;
import app.pigrest.auth.service.JwtService;
import app.pigrest.common.ApiResponse;
import app.pigrest.common.ApiStatusCode;
import app.pigrest.auth.dto.request.RegisterRequest;
import app.pigrest.auth.service.AuthService;
import app.pigrest.exception.DuplicateResourceException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        boolean isAvailable = authService.checkUsername(request.getUsername());
        if (!isAvailable) {
            throw new DuplicateResourceException("Username is already in use");
        }

        Auth auth = authService.create(request);
        return ResponseEntity.ok(
                ApiResponse.success(
                        ApiStatusCode.CREATED,
                        "Member registered successfully",
                        RegisterResponse.from(auth)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        Authentication auth = authService.login(request);
        UserDetails userDetails = userDetailsService.loadUserByUsername(auth.getName());

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.success(
                        ApiStatusCode.OK,
                        "Login success",
                        LoginResponse.of(accessToken, userDetails.getUsername())
                ));
    }

    @GetMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                    .filter(c -> c.getName().equals(REFRESH_TOKEN_COOKIE_NAME))
                    .findFirst()
                    .map(Cookie::getValue))
                .ifPresent(jwtService::revokeRefreshToken);

        ResponseCookie deleteCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, null)
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(ApiResponse.success(ApiStatusCode.OK, "Logout success", null));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(HttpServletRequest request) {
        String refreshToken = Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(c -> c.getName().equals(REFRESH_TOKEN_COOKIE_NAME))
                        .findFirst()
                        .map(Cookie::getValue))
                .orElseThrow(() -> new IllegalArgumentException("Refresh token missing"));
        String username = jwtService.validateRefreshToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        String newAccessToken = jwtService.generateAccessToken(userDetails);
        return ResponseEntity.ok(ApiResponse.success(
                ApiStatusCode.OK,
                "Token refreshed",
                LoginResponse.of(newAccessToken, username)
        ));
    }

    @PostMapping("/check-username")
    public ResponseEntity<ApiResponse<CheckUsernameResponse>> checkUsername(@Valid @RequestBody CheckUsernameRequest request) {
        boolean isAvailable = authService.checkUsername(request.getUsername());
        return ResponseEntity.ok(ApiResponse.success(
                ApiStatusCode.OK,
                isAvailable ? "Username is available" : "Username is already in use",
                CheckUsernameResponse.of(isAvailable)
        ));
    }
}
