package com.service.alaw.common.exception;

import com.service.alaw.common.exception.code.BaseCode;

public class ContractNotFoundException extends NotFoundException {

    public ContractNotFoundException(BaseCode errorCode) {
            super(errorCode);
    }
}



