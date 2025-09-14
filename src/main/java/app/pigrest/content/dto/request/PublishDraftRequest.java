package app.pigrest.content.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PublishDraftRequest {
    private Long imageId;
    private String title;
    private String content;
}
