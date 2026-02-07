package com.service.alaw.platform.contract.domain.controller;

import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.contract.domain.service.ContractCommandService;
import com.service.alaw.security.oauth2.domain.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class ContractBookmarkController {

    private final ContractCommandService contractCommandService;

    // PATCH /api/v1/contracts/{id}/bookmark - 중요 계약서로 등록 (즐겨찾기)
    @PatchMapping("/{id}/bookmark")
    public ResponseEntity<ApiResponse<Void>> bookmarkContract(
            @PathVariable("id") Long contractId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof PrincipalDetails principal) {
            userId = principal.getId();
        }

        contractCommandService.bookmarkContract(contractId, userId);
        return ApiResponse.updated();
    }

    // DELETE /api/v1/contracts/{id}/bookmark - 중요 계약서 해제 (즐겨찾기 해제)
    @DeleteMapping("/{id}/bookmark")
    public ResponseEntity<ApiResponse<Void>> unbookmarkContract(
            @PathVariable("id") Long contractId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof PrincipalDetails principal) {
            userId = principal.getId();
        }

        contractCommandService.unbookmarkContract(contractId, userId);
        return ApiResponse.deleted();
    }
}
