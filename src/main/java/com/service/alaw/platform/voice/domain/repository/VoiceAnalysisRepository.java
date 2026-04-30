package com.service.alaw.platform.voice.domain.repository;

import com.service.alaw.platform.voice.domain.document.VoiceAnalysisDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * voice-only 분석 결과 MongoDB 리포지토리.
 */
public interface VoiceAnalysisRepository extends MongoRepository<VoiceAnalysisDocument, String> {

    Optional<VoiceAnalysisDocument> findByVoiceRecordId(Long voiceRecordId);
}
