package app.pigrest.content.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PinCreateRequest {
    // 실제 서비스에서는 인증된 사용자 정보를 이용합니다.
    private String memberId;
    private String title;
    private String content;

    // 추가 정보: 보드와 태그 (추후 활용)
//    private List<String> boards;
//    private List<String> tags;
}