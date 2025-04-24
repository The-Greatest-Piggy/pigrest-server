package app.pigrest.content.controller;

import app.pigrest.common.ApiResponse;
import app.pigrest.common.ApiStatusCode;
import app.pigrest.content.dto.request.PinCreateRequest;
import app.pigrest.content.dto.request.PinUpdateContentRequest;
import app.pigrest.content.dto.response.PinResponse;
import app.pigrest.content.model.Pin;
import app.pigrest.content.service.PinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pins")
@RequiredArgsConstructor
public class PinController {

    private final PinService pinService;

    /**
     * Pin 생성 API (multipart/form-data)
     *
     * 클라이언트 요청 예시:
     * - key "Data": JSON 형식의 PinCreateRequest (memberId, title, content, boards, tags)
     * - key "image": 이미지 파일 (MultipartFile)
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PinResponse> createPin(
            @RequestPart("data") PinCreateRequest request,
            @RequestPart("image") MultipartFile image) {
        System.out.println(image);
        System.out.println(request);
        Pin pin = pinService.createPin(request, image);
        return ApiResponse.success(ApiStatusCode.CREATED, "Pin registered successfully", PinResponse.from(pin));
    }

    /**
     * 단건 Pin 조회 API
     */
    @GetMapping("/{id}")
    public ApiResponse<PinResponse> getPin(@PathVariable Long id) {
        Pin pin = pinService.getPinById(id);
        return ApiResponse.success(ApiStatusCode.OK, "Pin retrieved successfully", PinResponse.from(pin));
    }

    /**
     * 전체 Pin 조회 API
     */
    @GetMapping
    public ApiResponse<List<PinResponse>> getAllPins() {
        List<Pin> pins = pinService.getAllPins();
        List<PinResponse> responses = pins.stream()
                .map(PinResponse::from)
                .collect(Collectors.toList());
        return ApiResponse.success(ApiStatusCode.OK, "Pins retrieved successfully", responses);
    }

    /**
     * Pin 내용 수정 API (제목, 내용)
     */
    @PatchMapping("/{id}/content")
    public ApiResponse<PinResponse> updatePinContent(@PathVariable Long id,
                                                     @RequestBody PinUpdateContentRequest request) {
        Pin updatedPin = pinService.updatePinContent(id, request);
        return ApiResponse.success(ApiStatusCode.OK, "Pin content updated successfully", PinResponse.from(updatedPin));
    }

    /**
     * Pin 이미지 수정 API (multipart/form-data)
     *
     * 클라이언트 요청 시 key "image"로 새 이미지 파일을 전송합니다.
     */
    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PinResponse> updatePinImage(@PathVariable Long id,
                                                   @RequestPart("image") MultipartFile image) {
        Pin updatedPin = pinService.updatePinImage(id, image);
        return ApiResponse.success(ApiStatusCode.OK, "Pin image updated successfully", PinResponse.from(updatedPin));
    }

    /**
     * Pin 삭제 API (소프트 삭제)
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Object> deletePin(@PathVariable Long id) {
        pinService.deletePin(id);
        return ApiResponse.noContent("Pin deleted successfully");
    }
}
