package app.pigrest.content.dto.response;

import app.pigrest.content.domain.Draft;

import java.time.Instant;

// TODO: image 관련 데이터 추가 예정
public record GetDraftResponse(
        String title,
        String content,
        Instant expiresAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static GetDraftResponse of(Draft draft) {
        return new GetDraftResponse(
                draft.getTitle(),
                draft.getContent(),
                draft.getExpiresAt(),
                draft.getCreatedAt(),
                draft.getUpdatedAt());
    }
}

