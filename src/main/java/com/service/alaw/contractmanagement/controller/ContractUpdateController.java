package com.service.alaw.contractmanagement.controller;

import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.contractmanagement.dto.ContractResponse;
import com.service.alaw.contractmanagement.dto.ContractUpdateRequest;
import com.service.alaw.contractmanagement.service.ContractCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractUpdateController {

    private final ContractCommandService contractCommandService;

    // PATCH /api/contracts/{id} - 계약서 세부 사항 수정
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ContractResponse>> updateContract(
            @PathVariable("id") Long contractId,
            @AuthenticationPrincipal Long userId,
            @RequestBody ContractUpdateRequest request) {
        ContractResponse response = contractCommandService.updateContract(contractId, userId, request);
        return ApiResponse.updated(response);
    }
}
