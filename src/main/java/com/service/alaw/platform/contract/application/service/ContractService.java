package com.service.alaw.platform.contract.application.service;

import com.service.alaw.infra.messaging.ContractAnalysisPublisher;
import com.service.alaw.infra.ocr.OCRClient;
import com.service.alaw.infra.s3.S3UploadService;
import com.service.alaw.platform.contract.application.dto.analysis.ContractAnalysisMessage;
import com.service.alaw.platform.contract.application.dto.ocr.FastApiOcrResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractService {

    private final S3UploadService s3Service;
    private final OCRClient ocrClient;
    private final ContractAnalysisPublisher publisher;

    public FastApiOcrResponse uploadAndOCR(MultipartFile file) {
        try {
            // 1. S3 업로드
            String s3Key = s3Service.upload(file);
            String imageUrl = s3Service.getFileUrl(s3Key);
            log.info("S3 업로드 완료 - Key: {}, URL: {}", s3Key, imageUrl);

            // 2. FastAPI OCR 호출 (좌표가 이미 % 단위로 정규화되어 반환됨)
            FastApiOcrResponse ocrResponse = ocrClient.callOCR(s3Key);

            log.info("OCR 완료 - 블록 수: {}, 이미지 크기: {}x{}",
                    ocrResponse.blocks() != null ? ocrResponse.blocks().size() : 0,
                    ocrResponse.imageWidth(), ocrResponse.imageHeight());

            // 3. 분석 작업을 큐에 전송 (비동기 처리)
            ContractAnalysisMessage message = ContractAnalysisMessage.builder()
                    .s3Key(s3Key)
                    .userId(1L) // TODO: 실제 인증된 사용자 ID 사용
                    .build();

            publisher.publish(message);
            log.info("계약서 분석 작업 큐에 전송 완료 - s3Key: {}", s3Key);

            // 4. OCR 결과 그대로 반환 (FastAPI에서 이미 % 좌표로 정규화됨)
            return ocrResponse;

        } catch (Exception e) {
            log.error("OCR 처리 실패: {}", e.getMessage(), e);
            throw new RuntimeException("OCR 처리 중 오류가 발생했습니다.", e);
        }
    }
}
