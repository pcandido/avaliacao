package com.pcandido.caed.repository;

import com.pcandido.caed.model.Chave;
import com.pcandido.caed.model.Opcao;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Id;
import java.util.Optional;

public interface OpcaoRepository extends JpaRepository<Opcao, Id> {

    Optional<Opcao> findByChaveAndValor(Chave chave, String valor);

}
