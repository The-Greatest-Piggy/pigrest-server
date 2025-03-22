package app.pigrest.content.service;

import app.pigrest.content.model.Pin;
import app.pigrest.content.repository.PinRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PinService {

    private final PinRepository pinRepository;

    public Pin getPinById(Long id) {
        return pinRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pin Not Found"));
    }
}
