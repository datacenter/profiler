package com.cisco.applicationprofiler.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
public class GenericInvalidDataException  extends Exception {

	private static final long serialVersionUID = 100L;

	public GenericInvalidDataException(String message) {
		super(message);
	}
	

}
