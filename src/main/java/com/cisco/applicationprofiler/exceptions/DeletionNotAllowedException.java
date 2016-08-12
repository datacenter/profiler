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
@ResponseStatus(value = HttpStatus.FAILED_DEPENDENCY)
public class DeletionNotAllowedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 601866155425435676L;

	public DeletionNotAllowedException() {
		super("Delete not allowed");
	}
	
	public DeletionNotAllowedException(String message) {
		super(message);
	}
	
	public DeletionNotAllowedException(Throwable cause) {
		super(cause);
	}
	
	public DeletionNotAllowedException(String message,Throwable cause) {
		super(message, cause);
	}
}
