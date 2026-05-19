package com.service.alaw.platform.contract.application.service;

import com.service.alaw.common.exception.FastApiException;
import com.service.alaw.infra.messaging.ContractAnalysisPublisher;
import com.service.alaw.infra.ocr.OCRClient;
import com.service.alaw.infra.s3.S3UploadService;
import com.service.alaw.platform.contract.application.dto.analysis.ContractAnalysisMessage;
import com.service.alaw.platform.contract.application.dto.crud.ContractResponse;
import com.service.alaw.platform.contract.application.dto.ocr.FastApiOcrResponse;
import com.service.alaw.platform.contract.domain.entity.AnalysisJob;
import com.service.alaw.platform.contract.domain.entity.Contract;
import com.service.alaw.platform.contract.domain.entity.ContractType;
import com.service.alaw.platform.contract.domain.repository.AnalysisJobRepository;
import com.service.alaw.platform.contract.domain.repository.ContractRepository;
import com.service.alaw.platform.user.domain.entity.User;
import com.service.alaw.platform.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ContractService {

  private final S3UploadService s3Service;
  private final OCRClient ocrClient;
  private final ContractAnalysisPublisher publisher;
  private final ContractRepository contractRepository;
  private final UserRepository userRepository;
  private final AnalysisJobRepository analysisJobRepository;

  @CacheEvict(cacheNames = "contracts-list", key = "#userId")
  public ContractResponse uploadAndSave(MultipartFile file, String title, ContractType contractType, Long userId) {
    try {
      String s3Key = s3Service.upload(file);
      String imageUrl = s3Service.getFileUrl(s3Key);
      log.info("S3 업로드 완료 - Key: {}", s3Key);

      FastApiOcrResponse ocrResponse = ocrClient.callOCR(s3Key, imageUrl);
      log.info("OCR 완료 - 단어 수: {}", ocrResponse.words() != null ? ocrResponse.words().size() : 0);

      User user = userRepository.findById(userId)
              .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. userId=" + userId));

      Contract contract = Contract.of(user, title, imageUrl, contractType);
      contract.confirmSave(title, contractType);
      contract.updateRawText(ocrResponse.fullText());

      String jobId = UUID.randomUUID().toString();
      contract.updateAnalysisId(jobId);
      Contract savedContract = contractRepository.save(contract);

      AnalysisJob analysisJob = AnalysisJob.of(jobId, savedContract.getContractId(), userId);
      analysisJobRepository.save(analysisJob);

      ContractAnalysisMessage message = ContractAnalysisMessage.builder()
              .jobId(jobId).s3Key(s3Key).userId(userId).contractId(savedContract.getContractId())
              .build();
      publisher.publish(message);
      log.info("계약서 저장 및 분석 큐 전송 완료 - contractId: {}, jobId: {}", savedContract.getContractId(), jobId);

      return ContractResponse.from(savedContract);
    } catch (FastApiException e) {
      log.error("OCR 처리 실패: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("계약서 저장 중 오류 발생", e);
      throw new FastApiException("계약서 저장 중 오류가 발생했습니다.", e);
    }
  }

  public FastApiOcrResponse uploadAndOCR(MultipartFile file, Long userId) {
    try {
      // 1. S3 업로드
      String s3Key = s3Service.upload(file);
      String imageUrl = s3Service.getFileUrl(s3Key);
      log.info("S3 업로드 완료 - Key: {}, URL: {}", s3Key, imageUrl);

      // 2. FastAPI OCR 호출 (MongoDB 저장은 FastAPI에서 처리)
      FastApiOcrResponse ocrResponse = ocrClient.callOCR(s3Key, imageUrl);
      log.info("OCR 완료 - 단어 수: {}, 이미지 크기: {}x{}, 처리시간: {}s",
              ocrResponse.words() != null ? ocrResponse.words().size() : 0,
              ocrResponse.imageWidth(), ocrResponse.imageHeight(),
              ocrResponse.processingTime());

      // 4. Contract PostgreSQL 저장 (title은 파일명으로 임시 저장, contractType은 나중에 수동 입력)
      User user = userRepository.findById(userId)
              .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. userId=" + userId));
      String tempTitle = file.getOriginalFilename() != null ? file.getOriginalFilename() : "미제목";
      Contract contract = Contract.of(user, tempTitle, imageUrl, null);
      Contract savedContract = contractRepository.save(contract);
      log.info("Contract PostgreSQL 저장 완료 - contractId={}", savedContract.getContractId());

      // 5. 분석 작업을 큐에 전송 (비동기 처리)
      String jobId = UUID.randomUUID().toString();
      savedContract.updateRawText(ocrResponse.fullText());
      savedContract.updateAnalysisId(jobId);

      AnalysisJob analysisJob = AnalysisJob.of(jobId, savedContract.getContractId(),userId);
      analysisJobRepository.save(analysisJob);
      log.info("AnalysisJob 저장 완료 - jobId: {}, contractId: {}", jobId, savedContract.getContractId());

      ContractAnalysisMessage message = ContractAnalysisMessage.builder()
              .jobId(jobId)
              .s3Key(s3Key)
              .userId(userId)
              .contractId(savedContract.getContractId())
              .build();
      log.info("발행 직전 message={}", message);
      publisher.publish(message);
      log.info("계약서 분석 작업 큐에 전송 완료 - jobId: {}, s3Key: {}, contractId: {}", jobId, s3Key, savedContract.getContractId());

      // 5. imageUrl을 포함한 최종 응답 반환
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
              ocrResponse.error(),
              savedContract.getContractId(),
              jobId
      );

    } catch (FastApiException e) {
      log.error("OCR 처리 실패: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("OCR 처리 중 예상치 못한 오류 발생", e);
      throw new FastApiException("OCR 처리 중 오류가 발생했습니다.", e);
    }
  }
}
