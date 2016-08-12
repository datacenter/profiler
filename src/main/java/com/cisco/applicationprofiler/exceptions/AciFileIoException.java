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
public class AciFileIoException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4231529064258208676L;

	public AciFileIoException() {
		super("Error in writing Input Json File");
	}
	
	public AciFileIoException(String message) {
		super(message);
	}
	
	public AciFileIoException(Throwable cause) {
		super(cause);
	}
	
	public AciFileIoException(String message, Throwable cause) {
		super(message,cause);
	}

}
