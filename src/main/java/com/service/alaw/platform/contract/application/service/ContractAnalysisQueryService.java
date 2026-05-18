package com.service.alaw.platform.contract.application.service;

import com.service.alaw.common.exception.NotFoundException;
import com.service.alaw.common.exception.code.CommonErrorCode;
import com.service.alaw.platform.contract.application.dto.analysis.ContractAnalysisDetailResponse;
import com.service.alaw.platform.contract.domain.document.ContractAnalysisDocument;
import com.service.alaw.platform.contract.domain.entity.AnalysisJob;
import com.service.alaw.platform.contract.domain.entity.AnalysisJobStatus;
import com.service.alaw.platform.contract.domain.repository.AnalysisJobRepository;
import com.service.alaw.platform.contract.domain.repository.ContractAnalysisDocumentRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContractAnalysisQueryService {

  private final AnalysisJobRepository analysisJobRepository;
  private final ContractAnalysisDocumentRepository analysisDocumentRepository;

  public ContractAnalysisDetailResponse getAnalysisDetail(String jobId, Long userId) {
    AnalysisJob job =
        analysisJobRepository
            .findByJobIdAndUserId(jobId, userId)
            .orElseThrow(() -> new NotFoundException(CommonErrorCode.NOT_FOUND));

    Optional<ContractAnalysisDocument> analysisDocument = analysisDocumentRepository.findByJobId(jobId);
    if (analysisDocument.isPresent()) {
      return ContractAnalysisDetailResponse.ofCompleted(job, analysisDocument.get());
    }

    if (job.getStatus() == AnalysisJobStatus.FAILED) {
      return ContractAnalysisDetailResponse.ofFailed(job);
    }

    return ContractAnalysisDetailResponse.ofPending(job);
  }
}
