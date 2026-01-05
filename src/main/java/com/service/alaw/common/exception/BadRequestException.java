package com.service.alaw.common.exception;


import com.service.alaw.common.exception.code.BaseCode;
import lombok.Getter;

@Getter
public class BadRequestException extends BaseException {

	public BadRequestException(BaseCode errorCode) {
		super(errorCode);
	}
}
