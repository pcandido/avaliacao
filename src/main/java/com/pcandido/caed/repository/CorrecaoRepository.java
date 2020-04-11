package com.pcandido.caed.repository;

import com.pcandido.caed.model.Correcao;
import com.pcandido.caed.model.Situacao;

import java.util.Optional;

public interface CorrecaoRepository extends BaseRepository<Correcao> {

    Optional<Correcao> findFirstBySituacaoOrderByOrdem(Situacao situacao);

}
