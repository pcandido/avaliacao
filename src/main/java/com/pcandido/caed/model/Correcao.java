package com.pcandido.caed.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Correcao {

    private Long id;
    private String item;
    private String referencia;
    private String sequencial;
    private String solicitacao;
    private Situacao situacao;
    private long ordem;
    private List<Chave> chave;
    private List<Resposta> respostas;

    @Id
    public Long getId() {
        return id;
    }

    public Correcao setId(Long id) {
        this.id = id;
        return this;
    }

    public String getItem() {
        return item;
    }

    public Correcao setItem(String item) {
        this.item = item;
        return this;
    }

    public String getReferencia() {
        return referencia;
    }

    public Correcao setReferencia(String referencia) {
        this.referencia = referencia;
        return this;
    }

    public String getSequencial() {
        return sequencial;
    }

    public Correcao setSequencial(String sequencial) {
        this.sequencial = sequencial;
        return this;
    }

    public String getSolicitacao() {
        return solicitacao;
    }

    public Correcao setSolicitacao(String solicitacao) {
        this.solicitacao = solicitacao;
        return this;
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    public Situacao getSituacao() {
        return situacao;
    }

    public Correcao setSituacao(Situacao situacao) {
        this.situacao = situacao;
        return this;
    }

    @NotNull
    public long getOrdem() {
        return ordem;
    }

    public Correcao setOrdem(long ordem) {
        this.ordem = ordem;
        return this;
    }

    @ManyToMany
    public List<Chave> getChave() {
        return chave;
    }

    public Correcao setChave(List<Chave> chave) {
        this.chave = chave;
        return this;
    }

    public Correcao addChave(Chave chave) {
        if (this.chave == null) this.chave = new ArrayList<>();
        this.chave.add(chave);
        return this;
    }

    @OneToMany(mappedBy = "correcao", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Resposta> getRespostas() {
        return respostas;
    }

    public Correcao setRespostas(List<Resposta> respostas) {
        this.respostas = respostas;
        return this;
    }

    public Correcao addResposta(Resposta resposta) {
        if (this.respostas == null) this.respostas = new ArrayList<>();
        resposta.setCorrecao(this);
        this.respostas.add(resposta);
        return this;
    }
}
