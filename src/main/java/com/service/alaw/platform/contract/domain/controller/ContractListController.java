package com.service.alaw.platform.contract.domain.controller;

import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.contract.application.dto.ContractListResponse;
import com.service.alaw.platform.contract.domain.service.ContractQueryService;
import com.service.alaw.security.oauth2.domain.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<ApiResponse<List<ContractListResponse>>> getMyContracts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof PrincipalDetails principal) {
            userId = principal.getId();
        }

        List<ContractListResponse> contracts = contractQueryService.getMyContracts(userId);
        return ApiResponse.retrieved(contracts);
    }
}
