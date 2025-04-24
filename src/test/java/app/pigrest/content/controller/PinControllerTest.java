package app.pigrest.content.controller;

import app.pigrest.content.model.Pin;
import app.pigrest.content.service.PinService;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PinController.class)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class PinControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PinService pinService;

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    public void getPinTest() throws Exception {
        Pin pin = Pin.of("1234123412341234", "test Title", "https://test.com/image", "test content"); // 가짜 데이터 생성

        when(pinService.getPinById(1L)).thenReturn(pin); // Service 동작 정의

//        mockMvc.perform(get("/pins/{id}", 1))
//                .andExpect(status().isOk())
//                .andDo(document("get-pin",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint()),
//                        ResourceDocumentation.resource(
//                                ResourceSnippetParameters.builder()
//                                        .summary("핀 상세 조회 API")
//                                        .description("핀 상세 정보를 조회하는 API입니다.")
//                                        .pathParameters(
//                                                parameterWithName("id").description("핀 ID")
//                                        )
//                                        .responseFields(
//                                                fieldWithPath("id").description("핀 ID"),
//                                                fieldWithPath("title").description("핀 제목")
//                                        )
//                                        .build()
//                        )
//                ));
    }
}