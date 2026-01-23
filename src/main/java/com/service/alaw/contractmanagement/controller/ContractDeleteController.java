package com.service.alaw.contractmanagement.controller;

import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.contractmanagement.service.ContractCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractDeleteController {

    private final ContractCommandService contractCommandService;

    // DELETE /api/contracts/{id} - 계약서 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteContract(
            @PathVariable("id") Long contractId,
            @AuthenticationPrincipal Long userId) {
        contractCommandService.deleteContract(contractId, userId);
        return ApiResponse.deleted();
    }
}
