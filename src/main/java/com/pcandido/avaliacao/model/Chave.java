package com.pcandido.avaliacao.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Chave {

    private Long id;
    private String titulo;
    private List<Opcao> opcoes;

    @Id
    public Long getId() {
        return id;
    }

    public Chave setId(Long id) {
        this.id = id;
        return this;
    }

    @NotNull
    @NotEmpty
    public String getTitulo() {
        return titulo;
    }

    public Chave setTitulo(String titulo) {
        this.titulo = titulo;
        return this;
    }

    @OneToMany(mappedBy = "chave")
    @NotNull
    @NotEmpty
    public List<Opcao> getOpcoes() {
        return opcoes;
    }

    public Chave setOpcoes(List<Opcao> opcoes) {
        this.opcoes = opcoes;
        return this;
    }

    public Chave addOpcao(Opcao opcao) {
        if (this.opcoes == null) this.opcoes = new ArrayList<>();
        opcao.setChave(this);
        this.opcoes.add(opcao);
        return this;
    }
}
