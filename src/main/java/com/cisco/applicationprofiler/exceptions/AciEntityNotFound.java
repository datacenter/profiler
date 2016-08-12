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
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class AciEntityNotFound extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5013931552475032796L;

	public AciEntityNotFound() {
		super("Aci Entity not found");
	}
	
	public AciEntityNotFound(String message) {
		super(message);
	}
	
	public AciEntityNotFound(Throwable cause) {
		super(cause);
	}
	
	public AciEntityNotFound(String message, Throwable cause) {
		super(message,cause);
	}
}
