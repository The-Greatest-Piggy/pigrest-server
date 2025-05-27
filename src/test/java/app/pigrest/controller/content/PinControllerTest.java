package app.pigrest.controller.content;

import app.pigrest.common.BaseControllerTest;
import app.pigrest.content.controller.PinController;
import app.pigrest.content.model.Pin;
import app.pigrest.content.service.PinService;
import app.pigrest.security.WithMockCustomUser;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockCustomUser
@WebMvcTest(PinController.class)
class PinControllerTest extends BaseControllerTest {

    @MockBean
    private PinService pinService;

    @Test
    public void getPinTest() throws Exception {
        Pin pin = new Pin(1L, "Test Pin"); // 가짜 데이터 생성

        given(pinService.getPinById(1L)).willReturn(pin); // Service 동작 정의

        mockMvc.perform(get("/pins/{id}", 1)
                        .header("Authorization", "Bearer "))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-pin",
                        ResourceDocumentation.resource(
                                ResourceSnippetParameters.builder()
                                        .summary("핀 상세 조회 API")
                                        .description("핀 상세 정보를 조회하는 API입니다.")
                                        .pathParameters(
                                                parameterWithName("id").description("핀 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("id").description("핀 ID"),
                                                fieldWithPath("title").description("핀 제목")
                                        )
                                        .build()
                        )
                ));
    }
}