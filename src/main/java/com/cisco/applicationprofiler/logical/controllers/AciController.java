/**
 * 
 */
package com.cisco.applicationprofiler.logical.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cisco.applicationprofiler.exceptions.WebServiceError;

/**
 * @author Mahesh
 *
 */
public abstract class AciController {
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseBody
	public ResponseEntity<WebServiceError> handleValidationException(MethodArgumentNotValidException e) {
		List<ObjectError> errors = e.getBindingResult().getAllErrors();
		WebServiceError webServiceError = WebServiceError.build(WebServiceError.Type.VALIDATION_ERROR,
				"Field error in Object " + errors.get(0).getObjectName() + ";in Field "
						+ e.getBindingResult().getFieldError().getField() + ":" + errors.get(0).getDefaultMessage());
		return new ResponseEntity<>(webServiceError, HttpStatus.BAD_REQUEST);
	}
}
