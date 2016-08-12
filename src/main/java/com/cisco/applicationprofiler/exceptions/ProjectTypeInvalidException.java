/**
 * 
 */
package com.cisco.applicationprofiler.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Mahesh
 *
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ProjectTypeInvalidException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7781096685445398677L;
	
	public ProjectTypeInvalidException() {
		super();
	}
	
	public ProjectTypeInvalidException(String message) {
		super(message);
	}

	public ProjectTypeInvalidException(Throwable cause) {
		super(cause);
	}

	public ProjectTypeInvalidException(String message,Throwable cause) {
		super(message,cause);
	}


}
