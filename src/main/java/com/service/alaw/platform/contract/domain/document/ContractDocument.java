package com.service.alaw.platform.contract.domain.document;


import com.service.alaw.platform.BaseTimeDocument;
import com.service.alaw.platform.contract.application.dto.FraudRisk;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Getter
@SuperBuilder
@Document(collection = "contracts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ContractDocument extends BaseTimeDocument {

    @Id
    private String contractDocumentId;

    private Long contractId;
    private String contractType;
    private String ocrText;
    private String summary;

    private Map<String, Object> extractedData;   // 유연성 확보
    private ScamCheckDocument scamCheckReport;    // 고정 구조
    private Map<String, String> easyTermsMap;   // 용어 변환
    private List<FraudRisk> risks;                   // 위험 요소

}
