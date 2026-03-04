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

  public FastApiOcrResponse uploadAndOCR(MultipartFile file, Long userId) {
    try {
      // 1. S3 업로드
      String s3Key = s3Service.upload(file);
      String imageUrl = s3Service.getFileUrl(s3Key);
      log.info("S3 업로드 완료 - Key: {}, URL: {}", s3Key, imageUrl);

      // 2. FastAPI OCR 호출
      FastApiOcrResponse ocrResponse = ocrClient.callOCR(s3Key);
      log.info("OCR 완료 - 단어 수: {}, 이미지 크기: {}x{}, 처리시간: {}s",
              ocrResponse.words() != null ? ocrResponse.words().size() : 0,
              ocrResponse.imageWidth(), ocrResponse.imageHeight(),
              ocrResponse.processingTime());

      // 3. 분석 작업을 큐에 전송 (비동기 처리)
      ContractAnalysisMessage message = ContractAnalysisMessage.builder()
              .s3Key(s3Key)
              .userId(userId)
              .build();
      publisher.publish(message);
      log.info("계약서 분석 작업 큐에 전송 완료 - s3Key: {}", s3Key);

      // 4. imageUrl을 포함한 최종 응답 반환
      return new FastApiOcrResponse(
              ocrResponse.success(),
              ocrResponse.processingTime(),
              imageUrl,
              ocrResponse.imageWidth(),
              ocrResponse.imageHeight(),
              ocrResponse.fullText(),
              ocrResponse.markdown(),
              ocrResponse.contractData(),
              ocrResponse.validation(),
              ocrResponse.words(),
              ocrResponse.warnings(),
              ocrResponse.error()
      );

    } catch (Exception e) {
      log.error("OCR 처리 실패: {}", e.getMessage(), e);
      throw new RuntimeException("OCR 처리 중 오류가 발생했습니다.", e);
    }
  }
}
