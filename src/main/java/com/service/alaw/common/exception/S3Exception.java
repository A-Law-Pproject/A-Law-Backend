package com.service.alaw.common.exception;


import com.service.alaw.common.exception.code.BaseCode;
import lombok.Getter;

@Getter
public class S3Exception extends BaseException {

	public S3Exception(BaseCode errorCode) {
		super(errorCode);
	}
}
