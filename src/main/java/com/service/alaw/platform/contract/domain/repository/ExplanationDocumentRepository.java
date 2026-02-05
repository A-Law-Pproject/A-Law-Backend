package com.service.alaw.platform.contract.domain.repository;

import com.service.alaw.platform.contract.domain.document.ExplanationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExplanationDocumentRepository extends MongoRepository<ExplanationDocument, String> {

    List<ExplanationDocument> findByContractId(Long contractId);

    List<ExplanationDocument> findByContractIdOrderByExplanationIdAsc(Long contractId);
}
