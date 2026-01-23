package com.service.alaw.contractmanagement.dto;

import com.service.alaw.contractmanagement.entity.ContractType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ContractCreateRequest {

    @NotBlank(message = "계약서 제목은 필수입니다")
    private String title;

    private String fileUrl;

    private ContractType contractType;
}
