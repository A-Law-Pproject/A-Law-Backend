package com.service.alaw.platform.contract.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TermExplanation {
    private String term;
    private String simpleExplanation;
    private String legalDefinition;
    private List<String> examples;
}
