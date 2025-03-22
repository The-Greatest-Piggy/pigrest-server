package app.pigrest.content.dto;

import app.pigrest.content.model.Pin;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PinResponse {
    private Long id;
    private String title;

    public static PinResponse of(Pin pin) {
        PinResponse res = new PinResponse();
        res.setId(pin.getId());
        res.setTitle(pin.getTitle());
        return res;
    }
}
