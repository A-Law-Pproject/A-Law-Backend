package com.service.alaw.contractmanagement.controller;

import com.service.alaw.common.exception.BadRequestException;
import com.service.alaw.common.exception.UnauthorizedException;
import com.service.alaw.common.exception.code.CommonErrorCode;
import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.contractmanagement.dto.ContractResponse;
import com.service.alaw.contractmanagement.service.ContractCommandService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contracts")
public class ContractBookmarkController {

    private final ContractCommandService commandService;

    public ContractBookmarkController(ContractCommandService commandService) {
        this.commandService = commandService;
    }

    @PatchMapping("/{id}/bookmark")
    public ApiResponse<ContractResponse> bookmarkOn(
            @RequestHeader(value = "X-USER-ID", required = false) String userIdHeader,
            @PathVariable Long id
    ) {
        Long userId = parseUserId(userIdHeader);
        return ApiResponse.updated(commandService.bookmark(userId, id, true));
    }

    @DeleteMapping("/{id}/bookmark")
    public ApiResponse<ContractResponse> bookmarkOff(
            @RequestHeader(value = "X-USER-ID", required = false) String userIdHeader,
            @PathVariable Long id
    ) {
        Long userId = parseUserId(userIdHeader);
        return ApiResponse.updated(commandService.bookmark(userId, id, false));
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
