package br.com.alura.AluraFake.exceptionHandler;

import br.com.alura.AluraFake.exception.CustomException;
import br.com.alura.AluraFake.exception.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        Problem problem = new Problem(LocalDateTime.now());
        problem.setStatus(status.value());
        problem.setTitle(ex.getMessage());


        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> customException(CustomException ex, WebRequest request) {
        HttpStatusCode status = HttpStatus.BAD_REQUEST;

        Problem problem = new Problem(LocalDateTime.now());

        problem.setTitle(ex.getMessage());
        problem.setStatus(status.value());

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

}
