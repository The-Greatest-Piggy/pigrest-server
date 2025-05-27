package app.pigrest.common;

import com.epages.restdocs.apispec.SimpleType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ApiResponseDocs {
    public static final List<FieldDescriptor> COMMON_RESPONSE_FIELDS = List.of(
            fieldWithPath("status").description("상태 코드").type(SimpleType.NUMBER),
            fieldWithPath("message").description("상세 메시지").type(SimpleType.STRING),
            fieldWithPath("timestamp").description("응답 시간").type(SimpleType.STRING),
            fieldWithPath("error").description("에러 메시지").type(SimpleType.STRING).optional(),
            fieldWithPath("data").description("응답 데이터").type(JsonFieldType.OBJECT).optional()
    );

    public static List<FieldDescriptor> withDataFields(List<FieldDescriptor> dataFields) {
        return Stream.concat(
                COMMON_RESPONSE_FIELDS.stream(),
                dataFields.stream().map(dataField -> {
                    FieldDescriptor descriptor = fieldWithPath("data." + dataField.getPath())
                            .description(dataField.getDescription())
                            .type(dataField.getType());
                    return dataField.isOptional() ? descriptor.optional() : descriptor;
                })
        ).toList();
    }
}
