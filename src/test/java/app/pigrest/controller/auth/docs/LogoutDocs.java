package app.pigrest.controller.auth.docs;

import app.pigrest.common.ApiResponseDocs;
import com.epages.restdocs.apispec.ResourceSnippetParameters;

public class LogoutDocs {

    public static ResourceSnippetParameters success() {
        return ResourceSnippetParameters.builder()
                .summary("로그아웃")
                .description("Cookie에 refresh token이 없어도, 로그아웃을 진행합니다.")
                .responseFields(ApiResponseDocs.COMMON_RESPONSE_FIELDS)
                .build();
    }
}
