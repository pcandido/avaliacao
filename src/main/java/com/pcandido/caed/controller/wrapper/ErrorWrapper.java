package com.pcandido.caed.controller.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorWrapper extends Wrapper {

    private final String tipo;
    private final String descricao;

    public ErrorWrapper(String tipo, String descricao) {
        super(Status.ERRO);
        this.tipo = tipo;
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    @JsonProperty("descrição")
    public String getDescricao() {
        return descricao;
    }
}
