package com.cisco.applicationprofiler.exceptions;

//import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class L3OutNameExistsException extends RuntimeException 
{
	private static final long serialVersionUID = 100L;
	
	
	public L3OutNameExistsException(String msg) {
		super(msg);
	}

}