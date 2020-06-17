package com.pcandido.avaliacao.service;

import com.pcandido.avaliacao.exception.AppException;
import com.pcandido.avaliacao.exception.ForaDeOrdemException;
import com.pcandido.avaliacao.exception.SemItemException;
import com.pcandido.avaliacao.exception.TransicaoIlegalException;
import com.pcandido.avaliacao.model.Correcao;
import com.pcandido.avaliacao.model.Item;
import com.pcandido.avaliacao.model.Opcao;
import com.pcandido.avaliacao.model.Situacao;
import com.pcandido.avaliacao.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemRepository repository;
    private final OpcaoService opcaoService;

    @Autowired
    public ItemService(ItemRepository repository, OpcaoService opcaoService) {
        this.repository = repository;
        this.opcaoService = opcaoService;
    }

    private Item getProximoDisponivel() throws SemItemException {
        return repository.findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL).orElseThrow(SemItemException::new);
    }

    public Item getProximo() throws SemItemException {
        try {
            //obtêm a próxima correção disponível do repositório
            return getProximoDisponivel();
        } catch (SemItemException e) {
            //se não existir nenhuma disponível, obtêm a próxima reservada
            return repository.findFirstBySituacaoOrderByOrdem(Situacao.RESERVADO).orElseThrow(SemItemException::new);
        }
    }

    public List<Item> getReservadas() {
        return this.repository.findAllBySituacao(Situacao.RESERVADO);
    }

    public void validateTransaction(Item item, Situacao toSituacao, Situacao... allowedFromSituacoes) throws TransicaoIlegalException, ForaDeOrdemException {
        //valida se a situação atual está entre as que podem ser manipuladas
        if (!Set.of(allowedFromSituacoes).contains(item.getSituacao())) {
            throw new TransicaoIlegalException(item.getSituacao(), toSituacao);
        }

        //se a situação for DISPONIVEL, a única correção que pode ser alterada é a próxima.
        if (item.getSituacao() == Situacao.DISPONIVEL) {
            try {
                if (!getProximoDisponivel().getId().equals(item.getId())) {
                    throw new ForaDeOrdemException();
                }
            } catch (SemItemException e) {
                throw new ForaDeOrdemException();
            }
        }
    }

    public Item setComDefeito(long id) throws ForaDeOrdemException, TransicaoIlegalException {
        //recupera a correção do banco de dados
        Item toChange = repository.getOne(id);
        //valida se a correção pode ser alterada
        validateTransaction(toChange, Situacao.COM_DEFEITO, Situacao.DISPONIVEL, Situacao.RESERVADO);
        //altera a situação
        toChange.setSituacao(Situacao.COM_DEFEITO);
        //persiste
        return repository.save(toChange);
    }

    public Item setReservada(long id) throws ForaDeOrdemException, TransicaoIlegalException {
        //recupera a correção do banco de dados
        Item toChange = repository.getOne(id);
        //valida se a correção pode ser alterada
        validateTransaction(toChange, Situacao.RESERVADO, Situacao.DISPONIVEL);
        //altera a situação
        toChange.setSituacao(Situacao.RESERVADO);
        //persiste
        return repository.save(toChange);
    }

    public List<Correcao> addCorrecoes(long idItem, List<Correcao> correcoes) throws AppException {
        //o id da correção será recebido na URL, e não no corpo (dentro da correcao)
        Item item = repository.getOne(idItem);

        //verifica se o usuário pode realizar a operação
        validateTransaction(item, Situacao.CORRIGIDO, Situacao.DISPONIVEL, Situacao.RESERVADO);

        for (Correcao correcao : correcoes) {
            //A opção que é recebida via API não possui a chave primária, necessária para referenciar
            //uma opção no banco de dados no momento de persistir. Como medida provisória, estou usando a chave
            //e o valor da opção para buscar seu id, para então poder salvar usando id como referência
            //TODO: existem opções melhores (mas mais complexas) de se resolver esse problema,
            //ex: Modelar a opção com uma chave primária composta (chave_id e valor)
            Opcao opcao = opcaoService.getOpcao(correcao.getChave(), correcao.getOpcao().getValor());
            correcao.setOpcao(opcao);
            item.addCorrecao(correcao);
        }

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
