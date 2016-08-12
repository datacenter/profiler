package com.cisco.applicationprofiler.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class EpgNameExistsException extends RuntimeException {
	private static final long serialVersionUID = 100L;
	
	
	public EpgNameExistsException(String msg) {
		super(msg);
	}
}
