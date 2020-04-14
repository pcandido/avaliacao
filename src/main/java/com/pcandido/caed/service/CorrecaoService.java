package com.pcandido.caed.service;

import com.pcandido.caed.exception.IllegalTransactionException;
import com.pcandido.caed.exception.NoAvailableCorrecoes;
import com.pcandido.caed.exception.NonNextForbiddenException;
import com.pcandido.caed.model.Correcao;
import com.pcandido.caed.model.Resposta;
import com.pcandido.caed.model.Situacao;
import com.pcandido.caed.repository.CorrecaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CorrecaoService {

    private final CorrecaoRepository repository;

    @Autowired
    public CorrecaoService(CorrecaoRepository repository) {
        this.repository = repository;
    }

    private Correcao getProximaDisponivel() throws NoAvailableCorrecoes {
        return repository.findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL).orElseThrow(NoAvailableCorrecoes::new);
    }

    public Correcao getProxima() throws NoAvailableCorrecoes {
        try {
            //obtêm a próxima correção disponível do repositório
            return getProximaDisponivel();
        } catch (NoAvailableCorrecoes e) {
            //se não existir nenhuma disponível, obtêm a próxima reservada
            return repository.findFirstBySituacaoOrderByOrdem(Situacao.RESERVADA).orElseThrow(NoAvailableCorrecoes::new);
        }
    }

    public Page<Correcao> getReservadas(Pageable pageable) {
        return this.repository.findAllBySituacao(Situacao.RESERVADA, pageable);
    }

    public void validateTransaction(Correcao correcao, Situacao... allowedFromSituacoes) throws IllegalTransactionException, NonNextForbiddenException {
        //valida se a situação atual está entre as que podem ser manipuladas
        if (!Set.of(allowedFromSituacoes).contains(correcao.getSituacao())) {
            throw new IllegalTransactionException();
        }

        //se a situação for DISPONIVEL, a única correção que pode ser alterada é a próxima.
        if (correcao.getSituacao() == Situacao.DISPONIVEL) {
            try {
                if (!getProximaDisponivel().getId().equals(correcao.getId())) {
                    throw new NonNextForbiddenException();
                }
            } catch (NoAvailableCorrecoes noAvailableCorrecoes) {
                throw new NonNextForbiddenException();
            }
        }
    }

    public Correcao setComDefeito(long id) throws NonNextForbiddenException, IllegalTransactionException {
        //recupera a correção do banco de dados
        Correcao toChange = repository.getOne(id);
        //valida se a correção pode ser alterada
        validateTransaction(toChange, Situacao.DISPONIVEL, Situacao.RESERVADA);
        //altera a situação
        toChange.setSituacao(Situacao.COM_DEFEITO);
        //persiste
        return repository.save(toChange);
    }

    public Correcao setReservada(long id) throws NonNextForbiddenException, IllegalTransactionException {
        //recupera a correção do banco de dados
        Correcao toChange = repository.getOne(id);
        //valida se a correção pode ser alterada
        validateTransaction(toChange, Situacao.DISPONIVEL);
        //altera a situação
        toChange.setSituacao(Situacao.RESERVADA);
        //persiste
        return repository.save(toChange);
    }

    public Resposta setResposta(long idCorrecao, Resposta resposta) throws IllegalTransactionException, NonNextForbiddenException {
        //o id da correção será recebido na URL, e não no corpo (dentro da resposta)
        Correcao correcao = repository.getOne(idCorrecao);
        //verifica se o usuário pode realizar a operação
        validateTransaction(correcao, Situacao.DISPONIVEL, Situacao.RESERVADA);

        correcao.addResposta(resposta);

        Set<Long> respondidas = correcao.getRespostas().stream().map(a -> a.getChave().getId()).collect(Collectors.toSet());
        if (correcao.getChave().stream().allMatch(a -> respondidas.contains(a.getId()))) {
            //se todas as chaves já foram corrigidas, o item recebe a situacao CORRIGIDO
            correcao.setSituacao(Situacao.CORRIGIDA);
        } else {
            //caso contrário, recebe a situação RESERVADO (vite README)
            correcao.setSituacao(Situacao.RESERVADA);
        }

        repository.save(correcao);
        return resposta;
    }
}
