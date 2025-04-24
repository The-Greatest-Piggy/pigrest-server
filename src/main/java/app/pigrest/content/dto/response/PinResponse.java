package app.pigrest.content.dto.response;

import app.pigrest.content.model.Pin;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PinResponse {
    private Long id;
    private String memberId;
    private String title;
    private String imageUrl;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PinResponse from(Pin pin) {
        PinResponse response = new PinResponse();
        response.setId(pin.getId());
        response.setMemberId(pin.getMemberId());
        response.setTitle(pin.getTitle());
        response.setImageUrl(pin.getImageUrl());
        response.setContent(pin.getContent());
        response.setCreatedAt(pin.getCreatedAt());
        response.setUpdatedAt(pin.getUpdatedAt());
        return response;
    }
}