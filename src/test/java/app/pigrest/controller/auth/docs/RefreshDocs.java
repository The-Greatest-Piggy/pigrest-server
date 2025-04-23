package app.pigrest.controller.auth.docs;

import app.pigrest.common.ApiResponseDocs;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.List;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class RefreshDocs {
    private static final List<FieldDescriptor> RESPONSE_FIELDS = List.of(
            fieldWithPath("accessToken").description("액세스 토큰").type(SimpleType.STRING),
            fieldWithPath("username").description("사용자 인증 아이디").type(SimpleType.STRING)
    );

    public static ResourceSnippetParameters success() {
        return ResourceSnippetParameters.builder()
                .summary("토큰 재발급")
                .description("Access Token 만료 시, Cookie에 저장되어 있는 Refresh Token을 활용하여 토큰을 재발급할 수 있습니다.<br>" +
                        "Cookie에 저장된 Refresh Token이 유효하다면 Access Token을 재발급해줍니다.<br>" +
                        "유효하지 않은 토큰이거나 Refresh token이 쿠키에 존재하지 않는다면 401 상태 코드가 반환됩니다.")
                .responseFields(ApiResponseDocs.withDataFields(RESPONSE_FIELDS))
                .build();
    }

    public static ResourceSnippetParameters fail() {
        return ResourceSnippetParameters.builder()
                .responseFields(ApiResponseDocs.COMMON_RESPONSE_FIELDS)
                .build();
    }
}
