package com.pcandido.caed.service;

import com.pcandido.caed.exception.DataException;
import com.pcandido.caed.exception.IllegalTransactionException;
import com.pcandido.caed.exception.NoAvailableCorrecoes;
import com.pcandido.caed.exception.NonNextForbiddenException;
import com.pcandido.caed.model.Correcao;
import com.pcandido.caed.model.Situacao;
import com.pcandido.caed.repository.CorrecaoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CorrecaoServiceTest {

    @Autowired
    private CorrecaoService service;

    @MockBean
    private CorrecaoRepository repository;

    /**
     * Gera amostras de correção para serem usadas durante os testes
     */
    private Correcao createCorrecao(long id, Situacao situacao, long ordem) {
        Correcao correcao = new Correcao();
        correcao.setId(id);
        correcao.setItem("item-" + id);
        correcao.setReferencia("referencia-" + id);
        correcao.setSequencial("sequencial-" + id);
        correcao.setSolicitacao("solicitacao-" + id);
        correcao.setSituacao(situacao);
        correcao.setOrdem(ordem);
        return correcao;
    }

    @Test
    public void deve_buscar_proxima_correcao_disponivel_se_existir() throws DataException {
        //cria uma amostra
        Correcao sample = createCorrecao(1, Situacao.DISPONIVEL, 1);
        //mocka o repositório para retornar a amostra ao invés de buscar no banco de dados
        when(repository.findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL)).thenAnswer(invocationOnMock -> Optional.of(sample));
        //chama o método a ser testado
        Correcao actualNext = service.getProxima();
        //verifica se o objeto retornado é igual à amostra, isso significa que se houver pelo menos uma DISPONIVEL, ela será retornada
        assertEquals(sample, actualNext);
        //também verifica se o repository foi invocado
        verify(repository).findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL);
    }

    @Test
    public void deve_buscar_proxima_correcao_reservada_se_nao_existir_disponiveis() throws DataException {
        Correcao sample = createCorrecao(1, Situacao.RESERVADA, 5);

        //mocka o repositório para retornar nenhuma DISPONIVEL, mas retornar uma RESERVADA
        when(repository.findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL)).thenAnswer(invocationOnMock -> Optional.empty());
        when(repository.findFirstBySituacaoOrderByOrdem(Situacao.RESERVADA)).thenAnswer(invocationOnMock -> Optional.of(sample));

        Correcao actualNext = service.getProxima();
        assertEquals(sample, actualNext);

        //verifica se o repositório foi chamado duas vezes (uma para DISPONVEL e outra para RESERVADA)
        verify(repository).findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL);
        verify(repository).findFirstBySituacaoOrderByOrdem(Situacao.RESERVADA);
    }

    @Test
    public void deve_lancar_excecao_se_nao_existir_disponiveis_nem_reservadas() {
        //mocka o repositório para retornar nenhum DISPONIVEL ou RESERVADA
        when(repository.findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL)).thenAnswer(invocationOnMock -> Optional.empty());
        when(repository.findFirstBySituacaoOrderByOrdem(Situacao.RESERVADA)).thenAnswer(invocationOnMock -> Optional.empty());
        //invoca o método e espera uma exceção
        assertThrows(NoAvailableCorrecoes.class, () -> service.getProxima());
    }

    @Test
    public void deve_possibilitar_mudar_status_de_uma_correcao_para_COM_DEFEITO() throws DataException {
        Correcao sample = createCorrecao(1, Situacao.DISPONIVEL, 1);

        //mocka o repositório para retornar a amostra quando for solicitada uma correção por id
        when(repository.getOne(sample.getId())).thenAnswer(invocationOnMock -> sample);
        //mocka o repositório para retornar a amostra como próximo
        when(repository.findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL)).thenAnswer(invocationOnMock -> Optional.of(sample));
        //mocka o repositório para retornar o objeto "salvo" sem de fato salvá-lo no banco de dados
        //também testa se a situação do objeto que foi enviado para ser salvo está correta
        when(repository.save(sample)).thenAnswer(invocationOnMock -> {
            Correcao correcao = (Correcao) invocationOnMock.getArguments()[0];
            assertEquals(Situacao.COM_DEFEITO, correcao.getSituacao());
            return correcao;
        });

        Correcao saved = service.setComDefeito(sample.getId());
        assertEquals(Situacao.COM_DEFEITO, saved.getSituacao());
        verify(repository).save(sample);
    }

    @Test
    public void nao_deve_possibilitar_mudar_status_de_uma_correcao_disponivel_que_nao_seja_a_proxima() {
        Correcao nextSample = createCorrecao(1, Situacao.DISPONIVEL, 1);
        Correcao otherSample = createCorrecao(2, Situacao.DISPONIVEL, 5);

        when(repository.getOne(otherSample.getId())).thenAnswer(invocationOnMock -> otherSample);
        when(repository.findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL)).thenAnswer(invocationOnMock -> Optional.of(nextSample));

        assertThrows(NonNextForbiddenException.class, () -> service.setComDefeito(otherSample.getId()));
    }

    @Test
    public void deve_possibilitar_mudar_status_de_uma_correcao_reservada_que_nao_seja_a_proxima() throws DataException {
        Correcao otherSample = createCorrecao(1, Situacao.RESERVADA, 1);
        Correcao nextSample = createCorrecao(5, Situacao.DISPONIVEL, 5);

        when(repository.getOne(otherSample.getId())).thenAnswer(invocationOnMock -> otherSample);
        when(repository.findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL)).thenAnswer(invocationOnMock -> Optional.of(nextSample));
        when(repository.save(otherSample)).thenAnswer(invocationOnMock -> otherSample);

        Correcao changed = service.setComDefeito(otherSample.getId());
        assertEquals(Situacao.COM_DEFEITO, changed.getSituacao());
        verify(repository).save(otherSample);
    }

    @Test
    public void nao_deve_possibilitar_mudar_status_de_uma_correcao_CORRIGIDA() {
        Correcao sample = createCorrecao(1, Situacao.CORRIGIDA, 1);
        when(repository.getOne(sample.getId())).thenAnswer(invocationOnMock -> sample);
        assertThrows(IllegalTransactionException.class, () -> service.setComDefeito(sample.getId()));
    }

    @Test
    public void nao_deve_possibilitar_mudar_status_de_uma_correcao_COM_DEFEITO() {
        Correcao sample = createCorrecao(1, Situacao.COM_DEFEITO, 1);
        when(repository.getOne(sample.getId())).thenAnswer(invocationOnMock -> sample);
        assertThrows(IllegalTransactionException.class, () -> service.setComDefeito(sample.getId()));
    }

}
