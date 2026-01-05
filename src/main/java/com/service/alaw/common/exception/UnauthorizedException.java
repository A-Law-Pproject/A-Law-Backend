package com.service.alaw.common.exception;


import com.service.alaw.common.exception.code.BaseCode;
import lombok.Getter;

@Getter
public class UnauthorizedException extends BaseException {

	public UnauthorizedException(BaseCode errorCode) {
		super(errorCode);
	}
}
