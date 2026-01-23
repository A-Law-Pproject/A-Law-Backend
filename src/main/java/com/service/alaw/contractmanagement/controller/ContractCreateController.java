package com.service.alaw.contractmanagement.controller;

import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.contractmanagement.dto.ContractCreateRequest;
import com.service.alaw.contractmanagement.dto.ContractResponse;
import com.service.alaw.contractmanagement.service.ContractCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ContractCreateRequest request) {
        ContractResponse response = contractCommandService.createContract(userId, request);
        return ApiResponse.created(response);
    }
}
