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
        //obtêm a próxima correção disponível do repositório
        Optional<Correcao> proxima = repository.findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL);
        //se não existir nenhuma disponível, obtêm a próxima reservada
        if (proxima.isEmpty()) {
            proxima = repository.findFirstBySituacaoOrderByOrdem(Situacao.RESERVADA);
        }
        //retorna a correção obtida ou lança uma exceção se nenhuma correção foi obtida
        return proxima.orElseThrow(NoAvailableCorrecoes::new);
    }

    public Correcao setComDefeito(long id) throws NoAvailableCorrecoes, NonNextForbiddenException, IllegalTransactionException {
        //recupera a correção do banco de dados
        Correcao toChange = repository.getOne(id);

        if (toChange.getSituacao() == Situacao.DISPONIVEL) {
            //se a situação da correção a alterar for DISPONIVEL, é necessário verificar se ela é de fato a próxima
            Correcao next = repository.findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL).orElseThrow(NoAvailableCorrecoes::new);
            if (!next.getId().equals(id))
                //se não for, uma exceção é lançada
                throw new NonNextForbiddenException();
        } else if (toChange.getSituacao() != Situacao.RESERVADA) {
            //correções reservadas podem ser alteradas em qualquer ordem
            //correções corrigidas ou com defeito não podem ser alteradas para COM_DEFEITO
            throw new IllegalTransactionException();
        }

        //altera a situação
        toChange.setSituacao(Situacao.COM_DEFEITO);
        //persiste
        return repository.save(toChange);
    }

}
