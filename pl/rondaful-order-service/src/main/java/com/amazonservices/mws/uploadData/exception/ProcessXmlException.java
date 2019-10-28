package com.amazonservices.mws.uploadData.exception;

import com.rondaful.cloud.common.enums.ResponseCodeEnumSupper;
import com.rondaful.cloud.common.exception.GlobalException;

public class ProcessXmlException extends GlobalException {


	/**
	 * 
	 */
	private static final long serialVersionUID = -7775846806358312952L;

	public ProcessXmlException(String errorCode, String message) {
		super(errorCode, message, true);
	}

	public ProcessXmlException(ResponseCodeEnumSupper responseCodeEnum) {
		super(responseCodeEnum.getCode(), responseCodeEnum.getMsg(), true);
	}

	public ProcessXmlException(ResponseCodeEnumSupper responseCodeEnum, String msg) {
		super(responseCodeEnum.getCode(), msg, true);
	}

}
