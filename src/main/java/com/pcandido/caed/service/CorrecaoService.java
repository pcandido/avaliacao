package com.pcandido.caed.service;

import com.pcandido.caed.exception.IllegalTransactionException;
import com.pcandido.caed.exception.NoAvailableCorrecoes;
import com.pcandido.caed.exception.NonNextForbiddenException;
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
        //get the next DISPONIVEL from the repository
        Optional<Correcao> proxima = repository.findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL);
        //if it does not exists, try to get the next RESERVADA
        if (proxima.isEmpty()) {
            proxima = repository.findFirstBySituacaoOrderByOrdem(Situacao.RESERVADA);
        }
        //return the gotten object or throw an exception if no one was found
        return proxima.orElseThrow(NoAvailableCorrecoes::new);
    }

    public Correcao setComDefeito(long id) throws NoAvailableCorrecoes, NonNextForbiddenException, IllegalTransactionException {
        //retrieve the object from the repository
        Correcao toChange = repository.getOne(id);

        //if the object is DISPONIVEL, we must check if it is the next
        if (toChange.getSituacao() == Situacao.DISPONIVEL) {
            Correcao next = repository.findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL).orElseThrow(NoAvailableCorrecoes::new);
            if (!next.getId().equals(id))
                throw new NonNextForbiddenException();
        } else if (toChange.getSituacao() != Situacao.RESERVADA) {
            throw new IllegalTransactionException();
        }

        //change situacao
        toChange.setSituacao(Situacao.COM_DEFEITO);
        //save to repository
        return repository.save(toChange);
    }

}
