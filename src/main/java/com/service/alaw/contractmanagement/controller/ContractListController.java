package com.service.alaw.contractmanagement.controller;

import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.contractmanagement.dto.ContractListResponse;
import com.service.alaw.contractmanagement.service.ContractQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class ContractListController {

    private final ContractQueryService contractQueryService;

    // GET /api/v1/contracts - 내 계약서 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<ContractListResponse>>> getMyContracts(
            @AuthenticationPrincipal Long userId) {
        List<ContractListResponse> contracts = contractQueryService.getMyContracts(userId);
        return ApiResponse.retrieved(contracts);
    }
}
