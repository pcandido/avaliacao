package com.pcandido.caed.service;

import com.pcandido.caed.exception.IllegalTransactionException;
import com.pcandido.caed.exception.NoAvailableItems;
import com.pcandido.caed.exception.NonNextForbiddenException;
import com.pcandido.caed.model.Item;
import com.pcandido.caed.model.Resposta;
import com.pcandido.caed.model.Situacao;
import com.pcandido.caed.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemRepository repository;

    @Autowired
    public ItemService(ItemRepository repository) {
        this.repository = repository;
    }

    private Item getProximaDisponivel() throws NoAvailableItems {
        return repository.findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL).orElseThrow(NoAvailableItems::new);
    }

    public Item getProxima() throws NoAvailableItems {
        try {
            //obtêm a próxima correção disponível do repositório
            return getProximaDisponivel();
        } catch (NoAvailableItems e) {
            //se não existir nenhuma disponível, obtêm a próxima reservada
            return repository.findFirstBySituacaoOrderByOrdem(Situacao.RESERVADO).orElseThrow(NoAvailableItems::new);
        }
    }

    public Page<Item> getReservadas(Pageable pageable) {
        return this.repository.findAllBySituacao(Situacao.RESERVADO, pageable);
    }

    public void validateTransaction(Item item, Situacao... allowedFromSituacoes) throws IllegalTransactionException, NonNextForbiddenException {
        //valida se a situação atual está entre as que podem ser manipuladas
        if (!Set.of(allowedFromSituacoes).contains(item.getSituacao())) {
            throw new IllegalTransactionException();
        }

        //se a situação for DISPONIVEL, a única correção que pode ser alterada é a próxima.
        if (item.getSituacao() == Situacao.DISPONIVEL) {
            try {
                if (!getProximaDisponivel().getId().equals(item.getId())) {
                    throw new NonNextForbiddenException();
                }
            } catch (NoAvailableItems e) {
                throw new NonNextForbiddenException();
            }
        }
    }

    public Item setComDefeito(long id) throws NonNextForbiddenException, IllegalTransactionException {
        //recupera a correção do banco de dados
        Item toChange = repository.getOne(id);
        //valida se a correção pode ser alterada
        validateTransaction(toChange, Situacao.DISPONIVEL, Situacao.RESERVADO);
        //altera a situação
        toChange.setSituacao(Situacao.COM_DEFEITO);
        //persiste
        return repository.save(toChange);
    }

    public Item setReservada(long id) throws NonNextForbiddenException, IllegalTransactionException {
        //recupera a correção do banco de dados
        Item toChange = repository.getOne(id);
        //valida se a correção pode ser alterada
        validateTransaction(toChange, Situacao.DISPONIVEL);
        //altera a situação
        toChange.setSituacao(Situacao.RESERVADO);
        //persiste
        return repository.save(toChange);
    }

    public Resposta setResposta(long idItem, Resposta resposta) throws IllegalTransactionException, NonNextForbiddenException {
        //o id da correção será recebido na URL, e não no corpo (dentro da resposta)
        Item item = repository.getOne(idItem);
        //verifica se o usuário pode realizar a operação
        validateTransaction(item, Situacao.DISPONIVEL, Situacao.RESERVADO);

        item.addResposta(resposta);

        Set<Long> respondidas = item.getRespostas().stream().map(a -> a.getChave().getId()).collect(Collectors.toSet());
        if (item.getChave().stream().allMatch(a -> respondidas.contains(a.getId()))) {
            //se todas as chaves já foram corrigidas, o item recebe a situacao CORRIGIDO
            item.setSituacao(Situacao.CORRIGIDO);
        } else {
            //caso contrário, recebe a situação RESERVADO (vite README)
            item.setSituacao(Situacao.RESERVADO);
        }

        repository.save(item);
        return resposta;
    }
}
