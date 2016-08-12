package com.cisco.applicationprofiler.exceptions;

//import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class VrfNameExistsException extends RuntimeException 
{
	private static final long serialVersionUID = 100L;
	
	
	public VrfNameExistsException(String msg) {
		super(msg);
	}

}