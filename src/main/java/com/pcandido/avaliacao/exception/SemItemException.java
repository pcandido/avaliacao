package com.pcandido.avaliacao.exception;

import org.springframework.http.HttpStatus;

public class SemItemException extends AppException {

    public SemItemException() {
        super(HttpStatus.NOT_FOUND, "SEM_CORRECAO", "Não existem mais correções disponíveis");
    }

}
