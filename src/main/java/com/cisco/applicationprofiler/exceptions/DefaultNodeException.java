package com.cisco.applicationprofiler.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class DefaultNodeException extends RuntimeException {

	private static final long serialVersionUID = 100L;

	public DefaultNodeException(String message)
	{
		super(message);
	}
}
