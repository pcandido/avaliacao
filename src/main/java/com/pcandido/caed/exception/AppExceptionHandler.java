package com.pcandido.caed.exception;

import com.pcandido.caed.controller.wrapper.ErrorWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({AppException.class})
    public ResponseEntity<ErrorWrapper> handleNoAvailableItems(AppException ex) {
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(new ErrorWrapper(ex.getTipo(), ex.getDescricao()));
    }
}
