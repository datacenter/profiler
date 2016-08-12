package com.cisco.applicationprofiler.exceptions;

//import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class BdNotFoundException extends RuntimeException 
{
	private static final long serialVersionUID = 100L;
	
	
	public BdNotFoundException(String msg) {
		super(msg);
	}

}