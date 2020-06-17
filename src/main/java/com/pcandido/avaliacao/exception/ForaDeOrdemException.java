package com.pcandido.avaliacao.exception;

import org.springframework.http.HttpStatus;

public class ForaDeOrdemException extends AppException {

    public ForaDeOrdemException() {
        super(HttpStatus.BAD_REQUEST, "NAO_PERMITODO", "Não é permitido alterar um item fora de ordem");
    }
}
