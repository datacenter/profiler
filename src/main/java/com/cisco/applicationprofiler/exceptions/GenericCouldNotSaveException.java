package com.cisco.applicationprofiler.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INSUFFICIENT_STORAGE)
public class GenericCouldNotSaveException extends RuntimeException {

	private static final long serialVersionUID = 100L;

	public GenericCouldNotSaveException(String msg) {
		super(msg);
	}
}
