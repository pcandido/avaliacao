package com.pcandido.caed.repository;

import com.pcandido.caed.model.Correcao;
import com.pcandido.caed.model.Situacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CorrecaoRepository extends BaseRepository<Correcao> {

    Optional<Correcao> findFirstBySituacaoOrderByOrdem(Situacao situacao);

    Page<Correcao> findAllBySituacao(Situacao situacao, Pageable pageable);

}
