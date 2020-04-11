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
     * Aims to generate sample "correções" to be used in tests
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
        //create sample correção
        Correcao sample = createCorrecao(1, Situacao.DISPONIVEL, 1);
        //mock the repository to return my sample instead of fetch from the database
        when(repository.findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL)).thenAnswer(invocationOnMock -> Optional.of(sample));
        //call the method I want to test
        Correcao actualNext = service.getProxima();
        //assert that the return object is equal to my sample, it means if there is at least one "DISPONIVEL" it will be returned
        assertEquals(sample, actualNext);
        //also, assert my repository was called
        verify(repository).findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL);
    }

    @Test
    public void deve_buscar_proxima_correcao_reservada_se_nao_existir_disponiveis() throws DataException {
        //create a sample object
        Correcao sample = createCorrecao(1, Situacao.RESERVADA, 5);
        //mock repository to return no DISPONIVEL, but a RESERVADA
        when(repository.findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL)).thenAnswer(invocationOnMock -> Optional.empty());
        when(repository.findFirstBySituacaoOrderByOrdem(Situacao.RESERVADA)).thenAnswer(invocationOnMock -> Optional.of(sample));
        //call the method
        Correcao actualNext = service.getProxima();
        //assert the returned object is equal to the sample
        assertEquals(sample, actualNext);
        //assert the repository was called twice (one to DISPONVEL and other to RESERVADA)
        verify(repository).findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL);
        verify(repository).findFirstBySituacaoOrderByOrdem(Situacao.RESERVADA);
    }

    @Test
    public void deve_lancar_excecao_se_nao_existir_disponiveis_nem_reservadas() {
        //mock the repository to do not return DISPONIVEL either RESERVADA
        when(repository.findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL)).thenAnswer(invocationOnMock -> Optional.empty());
        when(repository.findFirstBySituacaoOrderByOrdem(Situacao.RESERVADA)).thenAnswer(invocationOnMock -> Optional.empty());
        //call the method and expect an exception
        assertThrows(NoAvailableCorrecoes.class, () -> service.getProxima());
    }

    @Test
    public void deve_possibilitar_mudar_status_de_uma_correcao_para_COM_DEFEITO() throws DataException {
        //this use case starts with a "Correção DISPONÍVEL"
        Correcao sample = createCorrecao(1, Situacao.DISPONIVEL, 1);
        //mock the repository to return the sample when it is asked to find by id
        when(repository.getOne(sample.getId())).thenAnswer(invocationOnMock -> sample);
        //mock the repository to return sample as next correcao
        when(repository.findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL)).thenAnswer(invocationOnMock -> Optional.of(sample));
        //mocking the repository to return the "saved" object without actually save it
        //also, test if the "situacao" of the object asked to be saved is correct
        when(repository.save(sample)).thenAnswer(invocationOnMock -> {
            Correcao correcao = (Correcao) invocationOnMock.getArguments()[0];
            assertEquals(Situacao.COM_DEFEITO, correcao.getSituacao());
            return correcao;
        });
        //call the method to change the situação to COM_DEFEITO
        Correcao saved = service.setComDefeito(sample.getId());
        //assert the returned correção has the situação = COM_DEFEITO
        assertEquals(Situacao.COM_DEFEITO, saved.getSituacao());
        //assert the repository was really called to save the object and the saved object had already changed to COM_DEFEITO
        verify(repository).save(sample);
    }

    @Test
    public void nao_deve_possibilitar_mudar_status_de_uma_correcao_disponivel_que_nao_seja_a_proxima() {
        Correcao nextSample = createCorrecao(1, Situacao.DISPONIVEL, 1);
        Correcao otherSample = createCorrecao(2, Situacao.DISPONIVEL, 5);
        //mock the repository to return the otherSample when it is asked to find by id
        when(repository.getOne(otherSample.getId())).thenAnswer(invocationOnMock -> otherSample);
        //mock the repository to return nextSample as next correcao
        when(repository.findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL)).thenAnswer(invocationOnMock -> Optional.of(nextSample));
        //try to change the "situacao" of non-next correção and expect an exception
        assertThrows(NonNextForbiddenException.class, () -> service.setComDefeito(otherSample.getId()));
    }

    @Test
    public void deve_possibilitar_mudar_status_de_uma_correcao_reservada_que_nao_seja_a_proxima() throws DataException {
        Correcao otherSample = createCorrecao(1, Situacao.RESERVADA, 1);
        Correcao nextSample = createCorrecao(5, Situacao.DISPONIVEL, 5);
        //mock the repository to return the otherSample when it is asked to find by id
        when(repository.getOne(otherSample.getId())).thenAnswer(invocationOnMock -> otherSample);
        //mock the repository to return nextSample as next correcao
        when(repository.findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL)).thenAnswer(invocationOnMock -> Optional.of(nextSample));
        //mock the repository to do not save in the database
        when(repository.save(otherSample)).thenAnswer(invocationOnMock -> otherSample);
        //call the method
        Correcao changed = service.setComDefeito(otherSample.getId());
        //assert the situacao was changed
        assertEquals(Situacao.COM_DEFEITO, changed.getSituacao());
        //assert it was saved
        verify(repository).save(otherSample);
    }

    @Test
    public void nao_deve_possibilitar_mudar_status_de_uma_correcao_CORRIGIDA() {
        //create samples
        Correcao sample = createCorrecao(1, Situacao.CORRIGIDA, 1);
        //mock the repository to return the sample when it is asked to find by id
        when(repository.getOne(sample.getId())).thenAnswer(invocationOnMock -> sample);
        //try to change the "situacao" of non-next correção and expect an exception
        assertThrows(IllegalTransactionException.class, () -> service.setComDefeito(sample.getId()));
    }

    @Test
    public void nao_deve_possibilitar_mudar_status_de_uma_correcao_COM_DEFEITO() {
        //create samples
        Correcao sample = createCorrecao(1, Situacao.COM_DEFEITO, 1);
        //mock the repository to return the sample when it is asked to find by id
        when(repository.getOne(sample.getId())).thenAnswer(invocationOnMock -> sample);
        //try to change the "situacao" of non-next correção and expect an exception
        assertThrows(IllegalTransactionException.class, () -> service.setComDefeito(sample.getId()));
    }

}
