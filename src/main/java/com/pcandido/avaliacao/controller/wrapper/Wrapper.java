package com.pcandido.avaliacao.controller.wrapper;

public abstract class Wrapper {

    private final Status situacao;

    public Wrapper(Status situacao) {
        this.situacao = situacao;
    }

    public Status getSituacao() {
        return situacao;
    }
}
