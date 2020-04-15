package com.pcandido.caed.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
public class Opcao {

    private Long id;
    private String valor;
    private String descricao;
    private Chave chave;

    @Id
    @JsonIgnore
    public Long getId() {
        return id;
    }

    public Opcao setId(Long id) {
        this.id = id;
        return this;
    }

    @NotNull
    @NotEmpty
    public String getValor() {
        return valor;
    }

    public Opcao setValor(String valor) {
        this.valor = valor;
        return this;
    }

    @NotNull
    @NotEmpty
    public String getDescricao() {
        return descricao;
    }

    public Opcao setDescricao(String descricao) {
        this.descricao = descricao;
        return this;
    }

    @ManyToOne
    @NotNull
    @JsonIgnore
    public Chave getChave() {
        return chave;
    }

    public Opcao setChave(Chave chave) {
        this.chave = chave;
        return this;
    }
}
