package com.pcandido.caed.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class Correcao {

    private Long id;
    private LocalDateTime dataHora;
    private Item item;
    private Chave chave;
    private Opcao opcao;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public Correcao setId(Long id) {
        this.id = id;
        return this;
    }

    @Column(updatable = false)
    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public Correcao setDataHora(LocalDateTime hora) {
        this.dataHora = hora;
        return this;
    }

    @NotNull
    @ManyToOne
    public Item getItem() {
        return item;
    }

    public Correcao setItem(Item item) {
        this.item = item;
        return this;
    }

    @NotNull
    @ManyToOne
    public Chave getChave() {
        return chave;
    }

    public Correcao setChave(Chave chave) {
        this.chave = chave;
        return this;
    }

    @NotNull
    @ManyToOne
    public Opcao getOpcao() {
        return opcao;
    }

    public Correcao setOpcao(Opcao opcao) {
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
