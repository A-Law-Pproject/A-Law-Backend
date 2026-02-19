package com.service.alaw.platform.contract.domain.repository;

import com.service.alaw.platform.contract.domain.document.ContractAnalysisDocument;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractAnalysisDocumentRepository
    extends MongoRepository<ContractAnalysisDocument, String> {

  Optional<ContractAnalysisDocument> findByContractId(Long contractId);
}
