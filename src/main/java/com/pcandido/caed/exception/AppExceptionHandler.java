package com.pcandido.caed.exception;

import com.pcandido.caed.controller.wrapper.ErrorWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({NoAvailableItems.class})
    public ResponseEntity<ErrorWrapper> handleNoAvailableItems() {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorWrapper("SEM_CORRECAO", "Não existem mais correções disponíveis"));
    }

    @ExceptionHandler({NonNextForbiddenException.class})
    public ResponseEntity<ErrorWrapper> handleNonNextForbiddenException() {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorWrapper("NAO_PERMITODO", "Não é permitido alterar um item fora de ordem"));
    }


}
