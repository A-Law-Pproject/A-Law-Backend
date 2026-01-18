package com.service.alaw.contractmanagement.controller;

import com.service.alaw.common.exception.BadRequestException;
import com.service.alaw.common.exception.UnauthorizedException;
import com.service.alaw.common.exception.code.CommonErrorCode;
import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.contractmanagement.service.ContractCommandService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contracts")
public class ContractDeleteController {

    private final ContractCommandService commandService;

    public ContractDeleteController(ContractCommandService commandService) {
        this.commandService = commandService;
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @RequestHeader(value = "X-USER-ID", required = false) String userIdHeader,
            @PathVariable Long id
    ) {
        Long userId = parseUserId(userIdHeader);
        commandService.delete(userId, id);
        return ApiResponse.deleted();
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
