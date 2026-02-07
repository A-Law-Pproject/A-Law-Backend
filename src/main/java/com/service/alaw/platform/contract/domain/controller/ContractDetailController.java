package com.service.alaw.platform.contract.domain.controller;

import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.contract.application.dto.ContractResponse;
import com.service.alaw.platform.contract.domain.service.ContractQueryService;
import com.service.alaw.security.oauth2.domain.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
            @PathVariable("id") Long contractId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof PrincipalDetails principal) {
            userId = principal.getId();
        }

        ContractResponse contract = contractQueryService.getContract(contractId, userId);
        return ApiResponse.retrieved(contract);
    }
}
