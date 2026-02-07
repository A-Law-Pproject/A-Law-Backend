package com.service.alaw.platform.contract.domain.controller;

import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.contract.application.dto.ContractCreateRequest;
import com.service.alaw.platform.contract.application.dto.ContractResponse;
import com.service.alaw.platform.contract.domain.service.ContractCommandService;
import com.service.alaw.security.oauth2.domain.PrincipalDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class ContractCreateController {

    private final ContractCommandService contractCommandService;

    // POST /api/v1/contracts - 내 문서에 저장하기 (계약서 생성)
    @PostMapping
    public ResponseEntity<ApiResponse<ContractResponse>> createContract(
            @Valid @RequestBody ContractCreateRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof PrincipalDetails principal) {
            userId = principal.getId();
        }

        ContractResponse response = contractCommandService.createContract(userId, request);
        return ApiResponse.created(response);
    }
}
