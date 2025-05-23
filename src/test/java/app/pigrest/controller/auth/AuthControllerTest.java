package app.pigrest.controller.auth;

import app.pigrest.auth.dto.request.CheckUsernameRequest;
import app.pigrest.auth.dto.request.LoginRequest;
import app.pigrest.auth.model.CustomUser;
import app.pigrest.common.BaseControllerTest;
import app.pigrest.auth.controller.AuthController;
import app.pigrest.auth.dto.request.RegisterRequest;
import app.pigrest.auth.dto.response.RegisterResponse;
import app.pigrest.auth.model.Auth;
import app.pigrest.auth.service.AuthService;
import app.pigrest.common.ApiResponse;
import app.pigrest.common.ApiStatusCode;
import app.pigrest.common.TokenType;
import app.pigrest.controller.auth.docs.*;
import app.pigrest.exception.DuplicateResourceException;
import app.pigrest.exception.ExpiredTokenException;
import app.pigrest.exception.InvalidTokenException;
import app.pigrest.security.WithMockCustomUser;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.MethodArgumentNotValidException;


import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends BaseControllerTest {
    @MockBean
    private AuthService authService;

    @Test
    public void registerSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest("lulu_the_piggy", "lulu135!", "lulu135!", "lulu");

        given(authService.checkUsername(anyString())).willReturn(true);

        Auth auth = Auth.of(request.getUsername(), request.getPassword());
        given(authService.create(any(RegisterRequest.class))).willReturn(auth);

        ApiResponse<RegisterResponse> response = ApiResponse.success(
                ApiStatusCode.CREATED, "Member registered successfully", RegisterResponse.from(auth));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("register-success", resource(RegisterDocs.success())));
    }

    @Test
    public void registerFailWhenDuplicateUsername() throws Exception {
        RegisterRequest request = new RegisterRequest("lulu_the_piggy", "lulu135!", "lulu135!", "lulu");

        given(authService.checkUsername(anyString()))
                .willThrow(new DuplicateResourceException("Username is already in use"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(ApiStatusCode.DUPLICATE_RESOURCE.getStatus()))
                .andExpect(jsonPath("$.error").value(ApiStatusCode.DUPLICATE_RESOURCE.getCode()))
                .andDo(document("register-fail-duplicate-username", resource(RegisterDocs.fail())));
    }

    @Test
    public void registerFailWhenPasswordMismatch() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "ddo_nonii", "noni135!!", "noni135@@", "noni");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(ApiStatusCode.VALIDATION_ERROR.getStatus()))
                .andExpect(jsonPath("$.error").value(ApiStatusCode.VALIDATION_ERROR.getCode()))
                .andDo(document("register-fail-password-mismatch", resource(RegisterDocs.fail())));
    }

    @Test
    public void loginSuccess() throws Exception {
        LoginRequest request = new LoginRequest("ddo_nonii", "noni135!");
        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = new CustomUser("ddo_nonii", request.getPassword(), List.of(), null);

        given(authService.login(any(LoginRequest.class))).willReturn(auth);
        given(auth.getName()).willReturn("ddo_nonii");
        given(userDetailsService.loadUserByUsername(request.getUsername())).willReturn(userDetails);
        given(jwtService.generateAccessToken(userDetails)).willReturn("test-access-token");
        given(jwtService.generateRefreshToken(userDetails)).willReturn("test-refresh-token");

        mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("refresh_token"))
                .andDo(document("login-success", resource(LoginDocs.success())));
    }

    @Test
    public void loginFail() throws Exception {
        LoginRequest request = new LoginRequest("do_noni", "noni");
        Authentication auth = mock(Authentication.class);

        given(authService.login(any(LoginRequest.class)))
                .willThrow(new BadCredentialsException("Id or password is incorrect"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andDo(document("login-fail", resource(LoginDocs.fail())));
    }

    @Test
    public void logoutSuccessWhenRefreshTokenExists() throws Exception {
        String refreshToken = "refresh-token-value";
        Cookie cookie = new Cookie("refresh_token", refreshToken);

        mockMvc.perform(get("/auth/logout")
                    .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("refresh_token", 0))
                .andDo(document("logout-success-with-refresh-token",
                        resource(LogoutDocs.success())));

        verify(jwtService).revokeRefreshToken(refreshToken);
    }

    @Test
    public void logoutSuccessWhenRefreshTokenDoesNotExist() throws Exception {
        mockMvc.perform(get("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("refresh_token", 0))
                .andDo(document("logout-success-without-refresh-token",
                        resource(LogoutDocs.success())));

        verify(jwtService, never()).revokeRefreshToken(any());
    }

    @Test
    public void refreshSuccess() throws Exception {
        String username = "ddo_nonii";
        String refreshToken = "refresh-token-value";
        String newAccessToken = "new-access-token-value";

        Cookie cookie = new Cookie("refresh_token", refreshToken);
        UserDetails userDetails = new CustomUser(username, "password", List.of(), null);

        given(jwtService.validateRefreshToken(refreshToken)).willReturn(username);
        given(userDetailsService.loadUserByUsername(username)).willReturn(userDetails);
        given(jwtService.generateAccessToken(userDetails)).willReturn(newAccessToken);

        mockMvc.perform(post("/auth/refresh")
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andDo(document("refresh-success", resource(RefreshDocs.success())));
    }

    @Test
    public void refreshFailWhenMissingToken() throws Exception {
        mockMvc.perform(post("/auth/refresh"))
                .andExpect(status().isUnauthorized())
                .andDo(document("refresh-fail-missing-token", resource(RefreshDocs.fail())));
    }

    @Test
    public void refreshFailWhenInvalidToken() throws Exception {
        String refreshToken = "invalid-refresh-token-value";
        Cookie cookie = new Cookie("refresh_token", refreshToken);

        given(jwtService.validateRefreshToken(refreshToken))
                .willThrow(new InvalidTokenException(TokenType.REFRESH, "Refresh token has an invalid signature"));

        mockMvc.perform(post("/auth/refresh")
                    .cookie(cookie))
                .andExpect(status().isUnauthorized())
                .andDo(document("refresh-fail-invalid-token", resource(RefreshDocs.fail())));
    }

    @Test
    public void refreshFailWhenExpiredToken() throws Exception {
        String refreshToken = "expired-refresh-token-value";
        Cookie cookie = new Cookie("refresh_token", refreshToken);

        given(jwtService.validateRefreshToken(refreshToken))
                .willThrow(new ExpiredTokenException(TokenType.REFRESH));

        mockMvc.perform(post("/auth/refresh")
                        .cookie(cookie))
                .andExpect(status().isUnauthorized())
                .andDo(document("refresh-fail-expired-token", resource(RefreshDocs.fail())));

    }

    @Test
    public void checkUsernameSuccess() throws Exception {
        CheckUsernameRequest request = new CheckUsernameRequest("ddo_nonii");

        given(authService.checkUsername(request.getUsername())).willReturn(true);

        mockMvc.perform(post("/auth/check-username")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("check-username-success", resource(CheckUsernameDocs.success())));
    }
}