package app.pigrest.controller.auth;

import app.pigrest.common.BasicControllerTest;
import app.pigrest.auth.controller.AuthController;
import app.pigrest.auth.dto.request.RegisterRequest;
import app.pigrest.auth.dto.response.RegisterResponse;
import app.pigrest.auth.model.Auth;
import app.pigrest.auth.service.AuthService;
import app.pigrest.common.ApiResponse;
import app.pigrest.common.ApiStatusCode;
import app.pigrest.controller.auth.docs.RegisterDocs;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends BasicControllerTest {
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
                .andDo(document("register-member-success", resource(RegisterDocs.success())));
    }
}