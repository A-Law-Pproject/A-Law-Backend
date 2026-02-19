package com.service.alaw.platform.contract.domain.repository;

import com.service.alaw.platform.contract.domain.document.ExplanationDocument;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExplanationDocumentRepository
    extends MongoRepository<ExplanationDocument, String> {

  List<ExplanationDocument> findByContractId(Long contractId);

  List<ExplanationDocument> findByContractIdOrderByExplanationIdAsc(Long contractId);
}
