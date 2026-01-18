package com.service.alaw.contractmanagement.controller;

import com.service.alaw.common.exception.BadRequestException;
import com.service.alaw.common.exception.UnauthorizedException;
import com.service.alaw.common.exception.code.CommonErrorCode;
import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.contractmanagement.dto.ContractResponse;
import com.service.alaw.contractmanagement.service.ContractQueryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contracts")
public class ContractDetailController {

    private final ContractQueryService queryService;

    public ContractDetailController(ContractQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/{id}")
    public ApiResponse<ContractResponse> get(
            @RequestHeader(value = "X-USER-ID", required = false) String userIdHeader,
            @PathVariable Long id
    ) {
        Long userId = parseUserId(userIdHeader);
        return ApiResponse.retrieved(queryService.get(userId, id));
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
