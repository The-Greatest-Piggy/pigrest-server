package app.pigrest.controller.auth.docs;

import app.pigrest.common.ApiResponseDocs;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.List;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CheckUsernameDocs {
    private static final List<FieldDescriptor> REQUEST_FIELDS = List.of(
            fieldWithPath("username").description("사용자 인증 아이디").type(SimpleType.STRING)
    );

    private static final List<FieldDescriptor> RESPONSE_FIELDS = List.of(
            fieldWithPath("isAvailable").description("사용 가능 여부").type(SimpleType.BOOLEAN)
    );

    public static ResourceSnippetParameters success() {
        return ResourceSnippetParameters.builder()
                .summary("아이디 중복 체크")
                .description("아이디(username) 사용 가능 여부를 반환합니다.<br>" +
                        "유효성 검사에서 실패 시, 400 상태 코드(VALIDATION_ERROR)를 반환합니다.<br>" +
                        "아이디는 4 글자와 16 글자 사이여야 하고, 소문자/숫자/언더스코어(_)만 사용 가능합니다.")
                .requestFields(REQUEST_FIELDS)
                .responseFields(ApiResponseDocs.withDataFields(RESPONSE_FIELDS))
                .build();
    }

    public static ResourceSnippetParameters fail() {
        return ResourceSnippetParameters.builder()
                .requestFields(REQUEST_FIELDS)
                .responseFields(ApiResponseDocs.COMMON_RESPONSE_FIELDS)
                .build();
    }
}
