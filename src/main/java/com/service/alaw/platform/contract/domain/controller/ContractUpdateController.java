package com.service.alaw.platform.contract.domain.controller;

import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.contract.application.dto.ContractResponse;
import com.service.alaw.platform.contract.application.dto.ContractUpdateRequest;
import com.service.alaw.platform.contract.domain.service.ContractCommandService;
import com.service.alaw.security.oauth2.domain.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class ContractUpdateController {

    private final ContractCommandService contractCommandService;

    // PATCH /api/v1/contracts/{id} - 계약서 세부 사항 수정
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ContractResponse>> updateContract(
            @PathVariable("id") Long contractId,
            @RequestBody ContractUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof PrincipalDetails principal) {
            userId = principal.getId();
        }

        ContractResponse response = contractCommandService.updateContract(contractId, userId, request);
        return ApiResponse.updated(response);
    }
}
