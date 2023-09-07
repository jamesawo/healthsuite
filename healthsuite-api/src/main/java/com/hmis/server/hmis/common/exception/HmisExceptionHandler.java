package com.hmis.server.hmis.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

import static com.hmis.server.hmis.common.constant.HmisExceptionMessage.EXCEPTION_VALIDATION_FAILED;

@ControllerAdvice
public class HmisExceptionHandler {

	@ExceptionHandler(value = {MethodArgumentNotValidException.class})
	public ResponseEntity<?> handleMethodArgumentNotValidException( MethodArgumentNotValidException e ){
		HttpStatus badRequest = HttpStatus.BAD_REQUEST;
		ExceptionResponse exceptionResponse = new ExceptionResponse(  );

		exceptionResponse.setMessage( EXCEPTION_VALIDATION_FAILED + badRequest.getReasonPhrase() );
		exceptionResponse.setStatus( badRequest.value() );
		exceptionResponse.setErrors( this.getErrors( e.getBindingResult().getFieldErrors() ) );

		return new ResponseEntity<>( exceptionResponse, badRequest );
	}


	private List<String> getErrors( List< FieldError > fieldErrors ){
		List<String> stringErrors = new ArrayList<>(  );
		for ( FieldError error: fieldErrors ){
			stringErrors.add( error.getDefaultMessage() );
		}
		return stringErrors;
	}
}
