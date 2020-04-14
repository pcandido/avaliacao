package com.pcandido.caed.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class Resposta {

    private Long id;
    private LocalDateTime dataHora;
    private String corretor;
    private Item item;
    private Chave chave;
    private Opcao opcao;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public Resposta setId(Long id) {
        this.id = id;
        return this;
    }

    @Column(updatable = false)
    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public Resposta setDataHora(LocalDateTime hora) {
        this.dataHora = hora;
        return this;
    }

    public String getCorretor() {
        return corretor;
    }

    public Resposta setCorretor(String corretor) {
        this.corretor = corretor;
        return this;
    }

    @NotNull
    @ManyToOne
    public Item getItem() {
        return item;
    }

    public Resposta setItem(Item item) {
        this.item = item;
        return this;
    }

    @NotNull
    @ManyToOne
    public Chave getChave() {
        return chave;
    }

    public Resposta setChave(Chave chave) {
        this.chave = chave;
        return this;
    }

    @NotNull
    @ManyToOne
    public Opcao getOpcao() {
        return opcao;
    }

    public Resposta setOpcao(Opcao opcao) {
        this.opcao = opcao;
        return this;
    }

    @PrePersist()
    public void onInsert() {
        if (this.dataHora == null) {
            this.dataHora = LocalDateTime.now();
        }
    }
}
