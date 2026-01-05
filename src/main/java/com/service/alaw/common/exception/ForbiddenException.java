package com.service.alaw.common.exception;


import com.service.alaw.common.exception.code.BaseCode;
import lombok.Getter;

@Getter
public class ForbiddenException extends BaseException {

	public ForbiddenException(BaseCode errorCode) {
		super(errorCode);
	}
}
