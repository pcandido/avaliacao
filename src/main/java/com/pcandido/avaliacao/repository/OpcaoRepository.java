package com.pcandido.avaliacao.repository;

import com.pcandido.avaliacao.model.Chave;
import com.pcandido.avaliacao.model.Opcao;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Id;
import java.util.Optional;

public interface OpcaoRepository extends JpaRepository<Opcao, Id> {

    Optional<Opcao> findByChaveAndValor(Chave chave, String valor);

}
