package com.pcandido.caed.service;

import com.pcandido.caed.exception.IllegalTransactionException;
import com.pcandido.caed.exception.NoAvailableItems;
import com.pcandido.caed.exception.NonNextForbiddenException;
import com.pcandido.caed.model.Correcao;
import com.pcandido.caed.model.Item;
import com.pcandido.caed.model.Situacao;
import com.pcandido.caed.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemRepository repository;

    @Autowired
    public ItemService(ItemRepository repository) {
        this.repository = repository;
    }

    private Item getProximoDisponivel() throws NoAvailableItems {
        return repository.findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL).orElseThrow(NoAvailableItems::new);
    }

    public Item getProximo() throws NoAvailableItems {
        try {
            //obtêm a próxima correção disponível do repositório
            return getProximoDisponivel();
        } catch (NoAvailableItems e) {
            //se não existir nenhuma disponível, obtêm a próxima reservada
            return repository.findFirstBySituacaoOrderByOrdem(Situacao.RESERVADO).orElseThrow(NoAvailableItems::new);
        }
    }

    public List<Item> getReservadas() {
        return this.repository.findAllBySituacao(Situacao.RESERVADO);
    }

    public void validateTransaction(Item item, Situacao... allowedFromSituacoes) throws IllegalTransactionException, NonNextForbiddenException {
        //valida se a situação atual está entre as que podem ser manipuladas
        if (!Set.of(allowedFromSituacoes).contains(item.getSituacao())) {
            throw new IllegalTransactionException();
        }

        //se a situação for DISPONIVEL, a única correção que pode ser alterada é a próxima.
        if (item.getSituacao() == Situacao.DISPONIVEL) {
            try {
                if (!getProximoDisponivel().getId().equals(item.getId())) {
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

    public List<Correcao> addCorrecoes(long idItem, List<Correcao> correcoes) throws IllegalTransactionException, NonNextForbiddenException {
        //o id da correção será recebido na URL, e não no corpo (dentro da correcao)
        Item item = repository.getOne(idItem);
        //verifica se o usuário pode realizar a operação
        validateTransaction(item, Situacao.DISPONIVEL, Situacao.RESERVADO);

        correcoes.forEach(item::addCorrecao);

        Set<Long> corrigidos = item.getCorrecoes().stream().map(a -> a.getChave().getId()).collect(Collectors.toSet());
        if (item.getChaves().stream().allMatch(a -> corrigidos.contains(a.getId()))) {
            //se todas as chaves já foram corrigidas, o item recebe a situacao CORRIGIDO
            item.setSituacao(Situacao.CORRIGIDO);
        } else {
            //caso contrário, recebe a situação RESERVADO (vite README)
            item.setSituacao(Situacao.RESERVADO);
        }

        repository.save(item);
        return correcoes;
    }
}
