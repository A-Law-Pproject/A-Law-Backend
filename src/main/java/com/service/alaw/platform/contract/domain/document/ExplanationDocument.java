package com.service.alaw.platform.contract.domain.document;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Builder
@Document(collection = "explanation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExplanationDocument {

    @Id
    private String id;

    @Field("explanation_id")
    private Long explanationId;

    @Field("contract_id")
    private Long contractId;

    @Field("original_text")
    private String originalText;

    @Field("explanation")
    private String explanation;

    public static ExplanationDocument of(Long contractId, String originalText, String explanation) {
        return ExplanationDocument.builder()
                .contractId(contractId)
                .originalText(originalText)
                .explanation(explanation)
                .build();
    }
}
