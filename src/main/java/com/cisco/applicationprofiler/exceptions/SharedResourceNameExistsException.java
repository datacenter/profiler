package com.cisco.applicationprofiler.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class SharedResourceNameExistsException extends RuntimeException  {
	private static final long serialVersionUID = 100L;
	
	
	public SharedResourceNameExistsException(String msg) {
		super(msg);
	}


}
