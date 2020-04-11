package com.pcandido.caed.service;

import com.pcandido.caed.exception.NoAvailableCorrecoes;
import com.pcandido.caed.model.Correcao;
import com.pcandido.caed.model.Situacao;
import com.pcandido.caed.repository.CorrecaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CorrecaoService extends BaseService<Correcao> {

    private final CorrecaoRepository repository;

    @Autowired
    public CorrecaoService(CorrecaoRepository repository) {
        this.repository = repository;
    }

    public Correcao getProxima() throws NoAvailableCorrecoes {
        Optional<Correcao> proxima = repository.findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL);
        if (proxima.isEmpty()) {
            proxima = repository.findFirstBySituacaoOrderByOrdem(Situacao.RESERVADA);
        }
        return proxima.orElseThrow(NoAvailableCorrecoes::new);
    }

    public Correcao setComDefeito(Correcao correcao) {
        correcao.setSituacao(Situacao.COM_DEFEITO);
        return repository.save(correcao);
    }

}
