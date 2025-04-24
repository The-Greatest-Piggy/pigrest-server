package app.pigrest.common;

import app.pigrest.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.s3.dir}")
    private String dir;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file) {
        try {
            String fileName = dir + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

            // S3 업로드 요청
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // 업로드된 파일의 URL 반환
            return String.format("https://%s.s3.%s.amazonaws.com/%s/%s", bucketName, region, dir, fileName);
        } catch (IOException e) {
            throw new CustomException(ApiStatusCode.S3_UPLOAD_ERROR, "Failed to upload file to S3");
        }
    }
}
