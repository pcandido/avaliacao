package com.pcandido.caed.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Item {

    private Long id;
    private String item;
    private String referencia;
    private String sequencial;
    private String solicitacao;
    private Situacao situacao;
    private long ordem;
    private List<Chave> chaves;
    private List<Correcao> correcoes;

    @Id
    public Long getId() {
        return id;
    }

    public Item setId(Long id) {
        this.id = id;
        return this;
    }

    public String getItem() {
        return item;
    }

    public Item setItem(String item) {
        this.item = item;
        return this;
    }

    public String getReferencia() {
        return referencia;
    }

    public Item setReferencia(String referencia) {
        this.referencia = referencia;
        return this;
    }

    public String getSequencial() {
        return sequencial;
    }

    public Item setSequencial(String sequencial) {
        this.sequencial = sequencial;
        return this;
    }

    public String getSolicitacao() {
        return solicitacao;
    }

    public Item setSolicitacao(String solicitacao) {
        this.solicitacao = solicitacao;
        return this;
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @JsonIgnore
    public Situacao getSituacao() {
        return situacao;
    }

    public Item setSituacao(Situacao situacao) {
        this.situacao = situacao;
        return this;
    }

    @NotNull
    @JsonIgnore
    public long getOrdem() {
        return ordem;
    }

    public Item setOrdem(long ordem) {
        this.ordem = ordem;
        return this;
    }

    @ManyToMany
    @JoinTable(inverseJoinColumns = @JoinColumn(name = "chave_id"))
    @JsonProperty("chave")
    public List<Chave> getChaves() {
        return chaves;
    }

    public Item setChaves(List<Chave> chave) {
        this.chaves = chave;
        return this;
    }

    public Item addChave(Chave chave) {
        if (this.chaves == null) this.chaves = new ArrayList<>();
        this.chaves.add(chave);
        return this;
    }

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    public List<Correcao> getCorrecoes() {
        return correcoes;
    }

    public Item setCorrecoes(List<Correcao> correcaos) {
        this.correcoes = correcaos;
        return this;
    }

    public Item addCorrecao(Correcao correcao) {
        if (this.correcoes == null) this.correcoes = new ArrayList<>();
        correcao.setItem(this);
        this.correcoes.add(correcao);
        return this;
    }
}
