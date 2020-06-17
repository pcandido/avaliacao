package com.pcandido.avaliacao.exception;

import org.springframework.http.HttpStatus;

public abstract class AppException extends Exception {

    private final HttpStatus httpStatus;
    private final String tipo;
    private final String descricao;

    public AppException(HttpStatus httpStatus, String tipo, String descricao) {
        this.httpStatus = httpStatus;
        this.tipo = tipo;
        this.descricao = descricao;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescricao() {
        return descricao;
    }
}
