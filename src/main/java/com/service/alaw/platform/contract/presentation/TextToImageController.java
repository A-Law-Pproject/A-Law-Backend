package com.service.alaw.platform.contract.presentation;

import com.service.alaw.common.aop.CurrentUserId;
import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.contract.application.dto.image.ContractImageResponse;
import com.service.alaw.platform.contract.application.service.ContractImageService;
import com.service.alaw.platform.contract.presentation.swagger.TextToImageSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class TextToImageController implements TextToImageSpec {

    private final ContractImageService contractImageService;

    @Override
    @GetMapping("/{contractId}/image")
    public ResponseEntity<ApiResponse<ContractImageResponse>> getContractImage(
            @PathVariable Long contractId,
            @CurrentUserId Long userId) {

        return ApiResponse.retrieved(contractImageService.getContractImage(contractId, userId));
    }
}
