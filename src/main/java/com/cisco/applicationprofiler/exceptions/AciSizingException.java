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
public class AciSizingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8602841569243064579L;

	public AciSizingException() {
		super("Error in executing aci sizing tool");
	}

	public AciSizingException(String message) {
		super(message);
	}

	public AciSizingException(Throwable cause) {
		super(cause);
	}

	public AciSizingException(String message, Throwable cause) {
		super(message, cause);
	}

}
