package app.pigrest.content.controller;

import app.pigrest.content.dto.PinResponse;
import app.pigrest.content.model.Pin;
import app.pigrest.content.service.PinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pins")
@RequiredArgsConstructor
public class PinController {

    private final PinService pinService;

    @GetMapping("/{id}")
    public ResponseEntity<PinResponse> getPin(@PathVariable Long id) {
        Pin pin = pinService.getPinById(id);
        return ResponseEntity.ok(PinResponse.of(pin));
    }
}
