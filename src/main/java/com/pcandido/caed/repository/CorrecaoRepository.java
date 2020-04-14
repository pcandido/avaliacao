package com.pcandido.caed.repository;

import com.pcandido.caed.model.Correcao;
import com.pcandido.caed.model.Situacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CorrecaoRepository extends JpaRepository<Correcao, Long> {

    Optional<Correcao> findFirstBySituacaoOrderByOrdem(Situacao situacao);

    Page<Correcao> findAllBySituacao(Situacao situacao, Pageable pageable);

}
