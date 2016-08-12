package com.cisco.applicationprofiler.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class ContractInvalidDetailException extends RuntimeException {

	private static final long serialVersionUID = 100L;

	public ContractInvalidDetailException(String msg) {
		super(msg);
	}
}
