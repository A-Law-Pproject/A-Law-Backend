package com.service.alaw.platform.voice.domain.repository;

import com.service.alaw.platform.voice.domain.document.VoiceFactCheckDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface VoiceFactCheckRepository extends MongoRepository<VoiceFactCheckDocument, String> {

    Optional<VoiceFactCheckDocument> findByVoiceRecordId(Long voiceRecordId);
}
