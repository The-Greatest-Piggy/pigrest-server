package app.pigrest.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateMemberRequest {
    @NotBlank(message = "Nickname must not be blank")
    @Size(min = 4, max = 12, message = "Nickname must be between 4 and 12 characters")
    private String nickname;
    private String description;
}
