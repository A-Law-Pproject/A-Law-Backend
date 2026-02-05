package com.service.alaw.platform.contract.domain.repository;

import com.service.alaw.platform.contract.domain.document.ContractAnalysisDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractAnalysisDocumentRepository extends MongoRepository<ContractAnalysisDocument, String> {

    Optional<ContractAnalysisDocument> findByContractId(Long contractId);
}
