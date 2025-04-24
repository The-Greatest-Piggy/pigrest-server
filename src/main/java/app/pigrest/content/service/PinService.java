package app.pigrest.content.service;

import app.pigrest.common.S3Service;
import app.pigrest.exception.CustomException;
import app.pigrest.common.ApiStatusCode;
import app.pigrest.content.dto.request.PinCreateRequest;
import app.pigrest.content.dto.request.PinUpdateContentRequest;
import app.pigrest.content.model.Pin;
import app.pigrest.content.repository.PinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PinService {

    private final PinRepository pinRepository;
    private final S3Service s3Service;

    /**
     * Pin 생성: JSON 데이터와 이미지 파일(MultipartFile)을 받아 S3에 업로드한 후 URL을 DB에 저장합니다.
     */
    @Transactional
    public Pin createPin(PinCreateRequest request, MultipartFile image) {
        // AWS S3에 이미지 파일 업로드 후 URL 획득
        String imageUrl = s3Service.uploadFile(image);

        // Pin 엔티티 생성 (memberId는 실제 서비스에서는 SecurityContext에서 처리)
        Pin pin = Pin.of(request.getMemberId(), request.getTitle(), imageUrl, request.getContent());
        return pinRepository.save(pin);
    }

    /**
     * 단건 Pin 조회
     */
    public Pin getPinById(Long id) {
        return pinRepository.findById(id)
                .orElseThrow(() -> new CustomException(ApiStatusCode.PIN_NOT_FOUND, "Pin not found"));
    }

    /**
     * 전체 Pin 조회
     */
    public List<Pin> getAllPins() {
        return pinRepository.findAll();
    }

    /**
     * Pin 내용(제목, 내용) 수정
     */
    @Transactional
    public Pin updatePinContent(Long id, PinUpdateContentRequest request) {
        Pin pin = getPinById(id);
        pin.updateContent(request.getTitle(), request.getContent());
        return pinRepository.save(pin);
    }

    /**
     * Pin 이미지 수정: 새 이미지 파일을 받아 S3에 업로드 후 URL을 업데이트합니다.
     */
    @Transactional
    public Pin updatePinImage(Long id, MultipartFile image) {
        String imageUrl = s3Service.uploadFile(image);
        Pin pin = getPinById(id);
        pin.updateImage(imageUrl);
        return pinRepository.save(pin);
    }

    /**
     * Pin 삭제 (소프트 삭제)
     */
    @Transactional
    public void deletePin(Long id) {
        Pin pin = getPinById(id);
        pin.markDeleted();
        pinRepository.save(pin);
    }
}
