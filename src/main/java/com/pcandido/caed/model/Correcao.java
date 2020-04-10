package com.pcandido.caed.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
public class Correcao implements Persistable {

    private Long id;
    private String item;
    private String referencia;
    private String sequencial;
    private String solicitacao;
    private Situacao situacao;
    private long ordem;
    private List<Chave> chave;

    @Id
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getSequencial() {
        return sequencial;
    }

    public void setSequencial(String sequencial) {
        this.sequencial = sequencial;
    }

    public String getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(String solicitacao) {
        this.solicitacao = solicitacao;
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    @NotNull
    public long getOrdem() {
        return ordem;
    }

    public void setOrdem(long ordem) {
        this.ordem = ordem;
    }

    @ManyToMany
    public List<Chave> getChave() {
        return chave;
    }

    public void setChave(List<Chave> chave) {
        this.chave = chave;
    }
}
