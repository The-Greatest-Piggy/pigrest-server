package app.pigrest.content.dto.response;

import app.pigrest.content.domain.Pin;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PinResponse {
    private UUID id;
    private String title;

    public static PinResponse of(Pin pin) {
        PinResponse res = new PinResponse();
        res.setId(pin.getId());
        res.setTitle(pin.getTitle());
        return res;
    }
}
