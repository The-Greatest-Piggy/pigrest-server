package app.pigrest.content.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PinUpdateContentRequest {
    private String title;
    private String content;
}
