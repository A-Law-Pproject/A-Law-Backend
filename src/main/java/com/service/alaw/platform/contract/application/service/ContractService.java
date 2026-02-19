package com.service.alaw.platform.contract.application.service;

import com.service.alaw.infra.messaging.ContractAnalysisPublisher;
import com.service.alaw.infra.ocr.OCRClient;
import com.service.alaw.infra.s3.S3UploadService;
import com.service.alaw.platform.contract.application.dto.analysis.ContractAnalysisMessage;
import com.service.alaw.platform.contract.application.dto.ocr.BoundingBox;
import com.service.alaw.platform.contract.application.dto.ocr.FastApiOcrResponse;
import com.service.alaw.platform.contract.application.dto.ocr.TextBlock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
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
      // 1. jobId 생성
      String jobId = UUID.randomUUID().toString();
      log.info("OCR 작업 시작 - jobId: {}, userId: {}", jobId, userId);

      // 2. S3 업로드
      String s3Key = s3Service.upload(file);
      String imageUrl = s3Service.getFileUrl(s3Key);
      log.info("S3 업로드 완료 - Key: {}, URL: {}", s3Key, imageUrl);

      // 3. FastAPI OCR 호출
      FastApiOcrResponse ocrResponse = ocrClient.callOCR(s3Key);

      // 4. 응답 변환
      List<TextBlock> textBlocks = convertToTextBlocks(ocrResponse.textBlocks());
      String fullText = textBlocks.stream().map(TextBlock::text).collect(Collectors.joining("\n"));

      log.info(
          "OCR 완료 - 텍스트 블록 수: {}, 이미지 크기: {}x{}",
          textBlocks.size(),
          ocrResponse.imageWidth(),
          ocrResponse.imageHeight());

      // 5. 분석 작업을 큐에 전송 (비동기 처리)
      ContractAnalysisMessage message =
          ContractAnalysisMessage.builder()
              .jobId(jobId)
              .s3Key(s3Key)
              .userId(userId)
              .build();

      publisher.publish(message);
      log.info("계약서 분석 작업 큐에 전송 완료 - jobId: {}, s3Key: {}", jobId, s3Key);

      // 6. OCR 결과 반환
      return new FastApiOcrResponse(
          jobId,
          "ocr_complete",
          imageUrl,
          ocrResponse.imageWidth(),
          ocrResponse.imageHeight(),
          textBlocks,
          fullText);

    } catch (Exception e) {
      log.error("OCR 처리 실패: {}", e.getMessage(), e);
      throw new RuntimeException("OCR 처리 중 오류가 발생했습니다.", e);
    }
  }

  private List<TextBlock> convertToTextBlocks(List<TextBlock> apiBlocks) {
    if (apiBlocks == null || apiBlocks.isEmpty()) {
      log.warn("OCR 결과에 텍스트 블록이 없습니다.");
      return Collections.emptyList();
    }

    List<TextBlock> textBlocks = new ArrayList<>();

    for (int i = 0; i < apiBlocks.size(); i++) {
      TextBlock block = apiBlocks.get(i);

      if (block.text() == null || block.text().trim().isEmpty()) {
        log.debug("빈 텍스트 블록 건너뜀 - index: {}", i);
        continue;
      }

      BoundingBox box = block.boundingBox();
      textBlocks.add(
          new TextBlock(
              i + 1,
              block.text().trim(),
              block.confidence() != null ? block.confidence() : 0.0,
              new BoundingBox(
                  box != null && box.x() != null ? box.x() : 0,
                  box != null && box.y() != null ? box.y() : 0,
                  box != null && box.width() != null ? box.width() : 0,
                  box != null && box.height() != null ? box.height() : 0)));
    }

    log.info("텍스트 블록 변환 완료 - 원본: {}, 변환: {}", apiBlocks.size(), textBlocks.size());
    return textBlocks;
  }
}
