package app.pigrest.content.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PublishDraftRequest {
    private UUID imageId;
    private String title;
    private String content;
}
