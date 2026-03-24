package com.service.alaw;

import com.service.alaw.infra.ai.AIClient;
import com.service.alaw.infra.messaging.ContractAnalysisPublisher;
import com.service.alaw.infra.messaging.VoiceRecordPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.service.alaw.infra.ocr.OCRClient;
import com.service.alaw.infra.s3.S3UploadService;
import com.service.alaw.platform.contract.domain.repository.ContractAnalysisDocumentRepository;
import com.service.alaw.platform.contract.domain.repository.ExplanationDocumentRepository;
import com.service.alaw.platform.contract.domain.repository.OcrResultDocumentRepository;
import com.service.alaw.platform.voice.domain.repository.VoiceFactCheckRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class ALawApplicationTests {

  @MockitoBean private S3UploadService s3UploadService;

  @MockitoBean private OCRClient ocrClient;

  @MockitoBean private AIClient aiClient;

  @MockitoBean private ContractAnalysisPublisher contractAnalysisPublisher;

  @MockitoBean private ContractAnalysisDocumentRepository contractAnalysisDocumentRepository;

  @MockitoBean private ExplanationDocumentRepository explanationDocumentRepository;

  @MockitoBean private RabbitTemplate rabbitTemplate;

  @MockitoBean private OcrResultDocumentRepository ocrResultDocumentRepository;

  @MockitoBean private VoiceRecordPublisher voiceRecordPublisher;

  @MockitoBean private VoiceFactCheckRepository voiceFactCheckRepository;

  @Test
  void contextLoads() {}
}
