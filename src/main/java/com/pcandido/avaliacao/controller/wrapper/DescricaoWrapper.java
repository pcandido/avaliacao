package com.pcandido.avaliacao.controller.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DescricaoWrapper {

    private final Status situacao;
    private final String descricao;


    public DescricaoWrapper(String descricao) {
        this.descricao = descricao;
        this.situacao = Status.SUCESSO;
    }

    public Status getSituacao() {
        return situacao;
    }

    @JsonProperty("descrição")
    public String getDescricao() {
        return descricao;
    }
}
