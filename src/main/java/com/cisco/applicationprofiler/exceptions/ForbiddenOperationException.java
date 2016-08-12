package com.cisco.applicationprofiler.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class ForbiddenOperationException extends Exception {

	private static final long serialVersionUID = 100L;

	public ForbiddenOperationException() {
		super("Operation not allowed");
	}
	
	public ForbiddenOperationException(String message) {
		super(message);
	}

}
