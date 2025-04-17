package app.pigrest.controller.auth.docs;


import app.pigrest.common.ApiResponseDocs;
import com.epages.restdocs.apispec.HeaderDescriptorWithType;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class LoginDocs {

    private static final List<FieldDescriptor> REQUEST_FIELDS = List.of(
            fieldWithPath("username").description("사용자 인증 아이디").type(SimpleType.STRING),
            fieldWithPath("password").description("사용자 인증 패스워드").type(SimpleType.STRING)
    );

    private static final List<HeaderDescriptorWithType> RESPONSE_HEADERS = List.of(
            headerWithName("Set-Cookie").description("Refresh token cookie. HttpOnly, Secure, Max-Age=7d, Path=/")
    );

    private static final List<FieldDescriptor> RESPONSE_FIELDS = List.of(
            fieldWithPath("accessToken").description("액세스 토큰").type(SimpleType.STRING),
            fieldWithPath("username").description("사용자 인증 아이디").type(SimpleType.STRING)
    );

    public static ResourceSnippetParameters success() {
        return ResourceSnippetParameters.builder()
                .summary("로그인")
                .tag("login")
                .description("로그인 성공 시, access token과 username을 반환합니다.")
                .requestFields(REQUEST_FIELDS)
                .responseHeaders(RESPONSE_HEADERS)
                .responseFields(ApiResponseDocs.withDataFields(RESPONSE_FIELDS))
                .build();
    }

}
