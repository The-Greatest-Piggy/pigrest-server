package app.pigrest.controller.auth.docs;

import app.pigrest.common.ApiResponseDocs;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.List;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class RegisterDocs {
    private static final List<FieldDescriptor> REQUEST_FIELDS = List.of(
        fieldWithPath("username").description("사용자 인증 아이디").type(SimpleType.STRING),
        fieldWithPath("password").description("사용자 인증 패스워드").type(SimpleType.STRING),
        fieldWithPath("passwordConfirm").description("확인용 비밀번호").type(SimpleType.STRING),
        fieldWithPath("nickname").description("닉네임").type(SimpleType.STRING)
    );

    private static final List<FieldDescriptor> RESPONSE_FIELDS = List.of(
        fieldWithPath("username").description("사용자 인증 아이디").type(SimpleType.STRING)
    );

    public static ResourceSnippetParameters success() {
        return ResourceSnippetParameters.builder()
                .summary("회원 등록")
                .tags("register")
                .description("회원 등록 성공 시, username을 반환합니다.")
                .requestFields(REQUEST_FIELDS)
                .responseFields(ApiResponseDocs.withDataFields(RESPONSE_FIELDS))
                .build();
    }
}
