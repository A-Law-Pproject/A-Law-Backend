package com.service.alaw.infra.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketVersioningStatus;
import software.amazon.awssdk.services.s3.model.GetBucketVersioningResponse;
import software.amazon.awssdk.services.s3.model.PutBucketVersioningRequest;
import software.amazon.awssdk.services.s3.model.VersioningConfiguration;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3BucketVersioningInitializer {

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    // 계약서/음성 객체 덮어쓰기·삭제 시 복구 가능하도록 버킷 버저닝을 보장.
    @EventListener(ApplicationReadyEvent.class)
    public void ensureVersioningEnabled() {
        try {
            GetBucketVersioningResponse current = s3Client.getBucketVersioning(b -> b.bucket(bucket));
            if (current.status() == BucketVersioningStatus.ENABLED) {
                log.info("[S3] 버킷 버저닝 활성 상태 확인 - bucket={}", bucket);
                return;
            }

            s3Client.putBucketVersioning(PutBucketVersioningRequest.builder()
                    .bucket(bucket)
                    .versioningConfiguration(VersioningConfiguration.builder()
                            .status(BucketVersioningStatus.ENABLED)
                            .build())
                    .build());
            log.info("[S3] 버킷 버저닝 활성화 완료 - bucket={}", bucket);
        } catch (Exception e) {
            log.warn("[S3] 버킷 버저닝 확인/활성화 실패 - bucket={}, message={}", bucket, e.getMessage());
        }
    }
}
