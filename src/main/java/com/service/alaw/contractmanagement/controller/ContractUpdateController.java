package com.service.alaw.contractmanagement.controller;

import com.service.alaw.common.exception.BadRequestException;
import com.service.alaw.common.exception.UnauthorizedException;
import com.service.alaw.common.exception.code.CommonErrorCode;
import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.contractmanagement.dto.ContractResponse;
import com.service.alaw.contractmanagement.dto.ContractUpdateRequest;
import com.service.alaw.contractmanagement.service.ContractCommandService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contracts")
public class ContractUpdateController {

    private final ContractCommandService commandService;

    public ContractUpdateController(ContractCommandService commandService) {
        this.commandService = commandService;
    }

    @PatchMapping("/{id}")
    public ApiResponse<ContractResponse> updateTitle(
            @RequestHeader(value = "X-USER-ID", required = false) String userIdHeader,
            @PathVariable Long id,
            @Valid @RequestBody ContractUpdateRequest request
    ) {
        Long userId = parseUserId(userIdHeader);
        return ApiResponse.updated(commandService.updateTitle(userId, id, request));
    }

    private Long parseUserId(String header) {
        if (header == null) {
            throw new UnauthorizedException(CommonErrorCode.UNAUTHORIZED);
        }
        try {
            return Long.parseLong(header);
        } catch (NumberFormatException e) {
            throw new BadRequestException(CommonErrorCode.BAD_REQUEST);
        }
    }
}
