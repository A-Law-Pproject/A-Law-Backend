package com.service.alaw.contractmanagement.controller;

import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.contractmanagement.dto.ContractResponse;
import com.service.alaw.contractmanagement.service.ContractQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class ContractDetailController {

    private final ContractQueryService contractQueryService;

    // GET /api/v1/contracts/{id} - 계약서 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContractResponse>> getContract(
            @PathVariable("id") Long contractId,
            @AuthenticationPrincipal Long userId) {
        ContractResponse contract = contractQueryService.getContract(contractId, userId);
        return ApiResponse.retrieved(contract);
    }
}
