package com.pcandido.avaliacao.controller.wrapper;

public class DataWrapper<T> {

    private final Status situacao;
    private final T data;

    public DataWrapper(T data) {
        this.data = data;
        this.situacao = Status.SUCESSO;
    }

    public Status getSituacao() {
        return situacao;
    }

    public T getData() {
        return data;
    }
}
