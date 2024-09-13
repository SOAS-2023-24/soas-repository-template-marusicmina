package util.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<?> handleMissingParameter(MissingServletRequestParameterException ex){
		return ResponseEntity.status(400).body(
				new ExceptionModel(HttpStatus.BAD_REQUEST,
						ex.getMessage())
				);
	}
	
	@ExceptionHandler(NoDataFoundException.class)
	public ResponseEntity<?> handleNoDataFound(NoDataFoundException ex){
		return ResponseEntity.status(404).body(
				new ExceptionModel(HttpStatus.NOT_FOUND, ex.getMessage())
				);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleGlobalException(Exception ex){
		return ResponseEntity.status(500).body(
				new ExceptionModel(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error")
		);
	}
}
