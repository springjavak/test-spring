package com.datafoundry.upload.config;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.datafoundry.upload.model.ExceptionMessage;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = { Exception.class })
	public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
		String exceptionMessageDescription = ex.getLocalizedMessage();
		if (exceptionMessageDescription == null) {
			exceptionMessageDescription = ex.toString();
		}
		ExceptionMessage exceptionMessage = new ExceptionMessage(exceptionMessageDescription);
		return new ResponseEntity<>(exceptionMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		String exceptionMessageDescription = ex.getBindingResult().getFieldErrors().stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage).findFirst().orElse(ex.getMessage());
		ExceptionMessage exceptionMessage = new ExceptionMessage(exceptionMessageDescription);
		return new ResponseEntity<>(exceptionMessage, HttpStatus.BAD_REQUEST);
	}
}
