package com.service.alaw.platform.contract.application.service;

import com.service.alaw.common.exception.NotFoundException;
import com.service.alaw.common.exception.code.CommonErrorCode;
import com.service.alaw.infra.s3.S3UploadService;
import com.service.alaw.platform.contract.application.dto.crud.ContractCreateRequest;
import com.service.alaw.platform.contract.application.dto.crud.ContractResponse;
import com.service.alaw.platform.contract.application.dto.crud.ContractUpdateRequest;
import com.service.alaw.platform.contract.domain.entity.Contract;
import com.service.alaw.platform.contract.domain.repository.ContractRepository;
import com.service.alaw.platform.user.domain.entity.User;
import com.service.alaw.platform.user.domain.repository.UserRepository;
import com.service.alaw.platform.voice.domain.entity.VoiceRecord;
import com.service.alaw.platform.voice.domain.repository.VoiceRecordRepository;
import lombok.RequiredArgsConstructor;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ContractCommandService {

  private final ContractRepository contractRepository;
  private final UserRepository userRepository;
  private final ContractValidator contractValidator;
  private final VoiceRecordRepository voiceRecordRepository;
  private final S3UploadService s3UploadService;

  public ContractResponse createContract(Long userId, ContractCreateRequest request) {
    log.info("계약서 생성 시작 - userId: {}, title: {}", userId, request.title());
    User user = findUserById(userId);
    Contract contract = buildContract(user, request);
    Contract savedContract = contractRepository.save(contract);
    log.info("계약서 생성 완료 - contractId: {}", savedContract.getContractId());
    return ContractResponse.from(savedContract);
  }

  public ContractResponse updateContract(
      Long contractId, Long userId, ContractUpdateRequest request) {
    log.info("계약서 수정 시작 - contractId: {}, userId: {}", contractId, userId);
    Contract contract = contractValidator.validateContractOwnership(contractId, userId);
    contract.updateTitle(request.title());
    log.info("계약서 수정 완료 - contractId: {}", contractId);
    return ContractResponse.from(contract);
  }

  public void deleteContract(Long contractId, Long userId) {
    log.info("계약서 삭제 시작 - contractId: {}, userId: {}", contractId, userId);
    Contract contract = contractValidator.validateContractOwnership(contractId, userId);

    // 연결된 음성 녹음 S3 파일 및 DB 레코드 먼저 삭제
    List<VoiceRecord> voiceRecords = voiceRecordRepository.findAllByContract_ContractIdAndUser_UserId(contractId, userId);
    for (VoiceRecord voiceRecord : voiceRecords) {
      s3UploadService.delete(voiceRecord.getS3Key());
    }
    voiceRecordRepository.deleteAll(voiceRecords);

    contractRepository.delete(contract);
    log.info("계약서 삭제 완료 - contractId: {}", contractId);
  }

  public void toggleBookmark(Long contractId, Long userId, boolean bookmark) {
    log.info("북마크 변경 시작 - contractId: {}, userId: {}, bookmark: {}", contractId, userId, bookmark);
    Contract contract = contractValidator.validateContractOwnership(contractId, userId);

    if (bookmark) {
      contract.bookmark();
    } else {
      contract.unbookmark();
    }
    log.info("북마크 변경 완료 - contractId: {}", contractId);
  }

  private User findUserById(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new NotFoundException(CommonErrorCode.NOT_FOUND));
  }

  private Contract buildContract(User user, ContractCreateRequest request) {
    return Contract.builder()
        .user(user)
        .title(request.title())
        .fileUrl(request.fileUrl())
        .contractType(request.contractType())
        .build();
  }
}
