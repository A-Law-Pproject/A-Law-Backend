package com.service.alaw.common.exception;


import com.service.alaw.common.exception.code.BaseCode;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

	private final BaseCode errorCode;

	public BaseException(BaseCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
