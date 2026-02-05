package com.service.alaw.platform.contract.domain.controller;

import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.contract.domain.service.ContractCommandService;
import com.service.alaw.security.oauth2.domain.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class ContractDeleteController {

    private final ContractCommandService contractCommandService;

    // DELETE /api/v1/contracts/{id} - 계약서 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteContract(
            @PathVariable("id") Long contractId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof PrincipalDetails principal) {
            userId = principal.getId();
        }

        contractCommandService.deleteContract(contractId, userId);
        return ApiResponse.deleted();
    }
}
