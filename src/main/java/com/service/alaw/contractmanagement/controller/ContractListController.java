package com.service.alaw.contractmanagement.controller;

import com.service.alaw.common.exception.BadRequestException;
import com.service.alaw.common.exception.UnauthorizedException;
import com.service.alaw.common.exception.code.CommonErrorCode;
import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.contractmanagement.dto.ContractListItemResponse;
import com.service.alaw.contractmanagement.service.ContractQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contracts")
public class ContractListController {

    private final ContractQueryService queryService;

    public ContractListController(ContractQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    public ApiResponse<List<ContractListItemResponse>> list(
            @RequestHeader(value = "X-USER-ID", required = false) String userIdHeader
    ) {
        Long userId = parseUserId(userIdHeader);
        return ApiResponse.retrieved(queryService.list(userId));
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
