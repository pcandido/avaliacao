package com.pcandido.avaliacao.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pcandido.avaliacao.model.Chave;
import com.pcandido.avaliacao.model.Correcao;
import com.pcandido.avaliacao.model.Opcao;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

public class CorrecaoRequest {

    @JsonProperty("chave")
    private List<ChaveRequest> chaves;

    @NotNull
    @NotEmpty
    public List<ChaveRequest> getChaves() {
        return chaves;
    }

    public void setChaves(List<ChaveRequest> chaves) {
        this.chaves = chaves;
    }

    public List<Correcao> toCorrecoes() {
        return this.chaves.stream().map(c -> new Correcao()
                .setChave(new Chave().setId(c.id))
                .setOpcao(new Opcao().setValor(c.valor))
        ).collect(Collectors.toList());
    }

    public static class ChaveRequest {

        private long id;
        private String valor;

        @NotNull
        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        @NotNull
        @NotEmpty
        public String getValor() {
            return valor;
        }

        public void setValor(String valor) {
            this.valor = valor;
        }
    }

}
