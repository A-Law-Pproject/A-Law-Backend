package com.service.alaw.common.exception;


import com.service.alaw.common.exception.code.BaseCode;
import lombok.Getter;

@Getter
public class NotFoundException extends BaseException {

	public NotFoundException(BaseCode errorCode) {
		super(errorCode);
	}
}
