package com.pcandido.avaliacao.exception;

import org.springframework.http.HttpStatus;

public class OpcaoInvalidaException extends AppException {

    public OpcaoInvalidaException(Long chaveId, String valor) {
        super(HttpStatus.BAD_REQUEST, "CHAVE_INCORRETA", String.format("Chave de correção incorreta. Valor '%s' não é válido para o item %s", valor, chaveId));
    }

}
