package com.service.alaw.platform.voice.domain.repository;

import com.service.alaw.platform.voice.domain.entity.VoiceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoiceRecordRepository extends JpaRepository<VoiceRecord, Long> {

    Optional<VoiceRecord> findByContract_ContractIdAndUser_UserId(Long contractId, Long userId);

    Optional<VoiceRecord> findByJobId(String jobId);

    List<VoiceRecord> findByUser_UserIdOrderByCreatedDateDesc(Long userId);
}
