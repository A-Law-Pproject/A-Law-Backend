package com.service.alaw.platform.contract.application.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record TermExplanation(
        String term,
        String simpleExplanation,
        String legalDefinition,
        List<String> examples
) {}
