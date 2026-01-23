package com.service.alaw.contractmanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.contractmanagement.service.ContractCommandService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractBookmarkController {

    private final ContractCommandService contractCommandService;

    // PATCH /api/contracts/{id}/bookmark - 중요 계약서로 등록 (즐겨찾기)
    @PatchMapping("/{id}/bookmark")
    public ResponseEntity<ApiResponse<Void>> bookmarkContract(
            @PathVariable("id") Long contractId,
            @AuthenticationPrincipal Long userId) {
        contractCommandService.bookmarkContract(contractId, userId);
        return ApiResponse.updated();
    }

    // DELETE /api/contracts/{id}/bookmark - 중요 계약서 해제 (즐겨찾기 해제)
    @DeleteMapping("/{id}/bookmark")
    public ResponseEntity<ApiResponse<Void>> unbookmarkContract(
            @PathVariable("id") Long contractId,
            @AuthenticationPrincipal Long userId) {
        contractCommandService.unbookmarkContract(contractId, userId);
        return ApiResponse.deleted();
    }
}
