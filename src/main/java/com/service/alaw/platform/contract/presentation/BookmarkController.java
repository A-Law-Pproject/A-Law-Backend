package com.service.alaw.platform.contract.presentation;

import com.service.alaw.common.aop.CurrentUserId;
import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.contract.application.service.ContractCommandService;
import com.service.alaw.platform.contract.presentation.swagger.BookmarkSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class BookmarkController implements BookmarkSpec {

  private final ContractCommandService contractCommandService;

  @Override
  @PatchMapping("/{id}/bookmark")
  public ResponseEntity<ApiResponse<Void>> bookmarkContract(
      @PathVariable("id") Long contractId, @CurrentUserId Long userId) {

    contractCommandService.toggleBookmark(contractId, userId, true);
    return ApiResponse.updated();
  }

  @Override
  @DeleteMapping("/{id}/bookmark")
  public ResponseEntity<ApiResponse<Void>> unbookmarkContract(
      @PathVariable("id") Long contractId, @CurrentUserId Long userId) {

    contractCommandService.toggleBookmark(contractId, userId, false);
    return ApiResponse.deleted();
  }
}
