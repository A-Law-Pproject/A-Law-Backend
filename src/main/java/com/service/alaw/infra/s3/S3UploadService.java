package com.service.alaw.infra.s3;

import com.service.alaw.common.exception.S3Exception;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

import static com.service.alaw.common.exception.code.S3ErrorCode.AWS_S3_UPLOAD_FAIL;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3UploadService {

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile file) {

        String s3Key = "contracts/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            // SDK v2 방식: PutObjectRequest를 먼저 빌드하고 RequestBody로 데이터를 넘김
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        } catch (IOException e) {

            throw new S3Exception(AWS_S3_UPLOAD_FAIL);
        }

        return s3Key;
    }

    public String getFileUrl(String s3Key) {
        return String.format("https://%s.s3.amazonaws.com/%s", bucket, s3Key);
    }

    public void deleteFile(String s3Key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .build());
            log.info("S3 원본 파일 삭제 완료: {}", s3Key);
        } catch (Exception e) {
            log.warn("S3 원본 파일 삭제 실패 (무시): {}", s3Key, e);
        }
    }
}
