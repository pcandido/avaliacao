package com.pcandido.caed.model;

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
    private List<Chave> chave;
    private List<Correcao> correcaos;

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
    public Situacao getSituacao() {
        return situacao;
    }

    public Item setSituacao(Situacao situacao) {
        this.situacao = situacao;
        return this;
    }

    @NotNull
    public long getOrdem() {
        return ordem;
    }

    public Item setOrdem(long ordem) {
        this.ordem = ordem;
        return this;
    }

    @ManyToMany
    public List<Chave> getChave() {
        return chave;
    }

    public Item setChave(List<Chave> chave) {
        this.chave = chave;
        return this;
    }

    public Item addChave(Chave chave) {
        if (this.chave == null) this.chave = new ArrayList<>();
        this.chave.add(chave);
        return this;
    }

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Correcao> getCorrecaos() {
        return correcaos;
    }

    public Item setCorrecaos(List<Correcao> correcaos) {
        this.correcaos = correcaos;
        return this;
    }

    public Item addCorrecao(Correcao correcao) {
        if (this.correcaos == null) this.correcaos = new ArrayList<>();
        correcao.setItem(this);
        this.correcaos.add(correcao);
        return this;
    }
}
