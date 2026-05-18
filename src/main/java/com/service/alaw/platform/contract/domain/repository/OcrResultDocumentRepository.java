package com.service.alaw.platform.contract.domain.repository;

import com.service.alaw.platform.contract.domain.document.OcrResultDocument;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OcrResultDocumentRepository extends MongoRepository<OcrResultDocument, String> {

  Optional<OcrResultDocument> findByS3Key(String s3Key);

  Optional<OcrResultDocument> findByImageUrl(String imageUrl);
}
