/**
 * 
 */
package com.cisco.applicationprofiler.exceptions;

/**
 * @author Mahesh
 *
 */
public class ProjectNameExistsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ProjectNameExistsException() {
		super("Error in creating project");
	}
	
	public ProjectNameExistsException(String message) {
		super(message);
	}
	
	public ProjectNameExistsException(Throwable cause) {
		super(cause);
	}
	
	public ProjectNameExistsException(String message, Throwable cause) {
		super(message,cause);
	}

}
