package app.pigrest.content.dto.response;

import app.pigrest.content.domain.Pin;

import java.time.Instant;
import java.util.UUID;

public record PublishDraftResponse(
        UUID id, // Pin ID
        String title,
        String content,
        String imageUrl,
        Instant createdAt,
        Instant updatedAt
) {
    public static PublishDraftResponse from(Pin pin) {
        return new PublishDraftResponse(
                pin.getId(),
                pin.getTitle(),
                pin.getContent(),
                null, // TODO: Image 로직 작성 후 변경
                pin.getCreatedAt(),
                pin.getUpdatedAt()
        );
    }
}
