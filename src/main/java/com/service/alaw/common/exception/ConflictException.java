package com.service.alaw.common.exception;


import com.service.alaw.common.exception.code.BaseCode;
import lombok.Getter;

@Getter
public class ConflictException extends BaseException {

	public ConflictException(BaseCode errorCode) {
		super(errorCode);
	}
}
