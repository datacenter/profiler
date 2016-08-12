/**
 * 
 */
package com.cisco.applicationprofiler.exceptions;

import java.io.Serializable;

/**
 * @author Mahesh
 *
 */
public class WebServiceError implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4110878550730505896L;
	private int code;
	private String description;
	private String message;

	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public static WebServiceError build(Type errorType, String description) {
		WebServiceError error = new WebServiceError();
		error.setCode(errorType.getCode());
		error.setDescription(errorType.getMessage());
		error.setMessage(description);
		return error;
	}

	public enum Type {
		BAD_REQUEST_ERROR(4002, "Bad request error"),
		INTERNAL_SERVER_ERROR(5001, "Unexpected server error"),
		VALIDATION_ERROR(4001, "Found validation issues");
		private int code;
		private String message;
		Type(int code, String message) {
			this.code = code;
			this.message = message;
		}
		public int getCode() {
			return code;
		}
		public String getMessage() {
			return message;
		}
	}
}