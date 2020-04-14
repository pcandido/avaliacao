package com.pcandido.caed.service;

import com.pcandido.caed.exception.DataException;
import com.pcandido.caed.exception.IllegalTransactionException;
import com.pcandido.caed.exception.NoAvailableCorrecoes;
import com.pcandido.caed.exception.NonNextForbiddenException;
import com.pcandido.caed.model.*;
import com.pcandido.caed.repository.CorrecaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CorrecaoServiceTest {

    @Autowired
    private CorrecaoService service;

    @MockBean
    private CorrecaoRepository repository;

    Correcao correcaoDisponivel1;
    Correcao correcaoDisponivel2;
    Correcao correcaoReservada;
    Correcao correcaoCorrigida;
    Correcao correcaoComDefeito;
    Resposta resposta1;
    Resposta resposta2;


    @BeforeEach
    private void setup() {
        Chave chave1 = new Chave()
                .setId(1L)
                .setTitulo("Chave 1")
                .addOpcao(new Opcao()
                        .setId(1L)
                        .setValor("C1")
                        .setDescricao("Opção 1"))
                .addOpcao(new Opcao()
                        .setId(2L)
                        .setValor("C2")
                        .setDescricao("Opção 2"));

        Chave chave2 = new Chave()
                .setId(2L)
                .setTitulo("Chave 2")
                .addOpcao(new Opcao()
                        .setId(3L)
                        .setValor("C3")
                        .setDescricao("Opção 3"))
                .addOpcao(new Opcao()
                        .setId(4L)
                        .setValor("C4")
                        .setDescricao("Opção 4"));

        this.correcaoReservada = new Correcao()
                .setId(1L)
                .setItem("item-1")
                .setReferencia("referencia-1")
                .setSequencial("sequencial-1")
                .setSolicitacao("solicitacao-1")
                .setSituacao(Situacao.RESERVADA)
                .setOrdem(1)
                .addChave(chave1)
                .addChave(chave2);

        this.correcaoCorrigida = new Correcao()
                .setId(2L)
                .setItem("item-2")
                .setReferencia("referencia-2")
                .setSequencial("sequencial-2")
                .setSolicitacao("solicitacao-2")
                .setSituacao(Situacao.CORRIGIDA)
                .setOrdem(2)
                .addChave(chave1)
                .addChave(chave2);

        this.correcaoComDefeito = new Correcao()
                .setId(3L)
                .setItem("item-3")
                .setReferencia("referencia-3")
                .setSequencial("sequencial-3")
                .setSolicitacao("solicitacao-3")
                .setSituacao(Situacao.COM_DEFEITO)
                .setOrdem(3)
                .addChave(chave1)
                .addChave(chave2);

        this.correcaoDisponivel1 = new Correcao()
                .setId(4L)
                .setItem("item-4")
                .setReferencia("referencia-4")
                .setSequencial("sequencial-4")
                .setSolicitacao("solicitacao-4")
                .setSituacao(Situacao.DISPONIVEL)
                .setOrdem(4)
                .addChave(chave1)
                .addChave(chave2);

        this.correcaoDisponivel2 = new Correcao()
                .setId(5L)
                .setItem("item-5")
                .setReferencia("referencia-5")
                .setSequencial("sequencial-5")
                .setSolicitacao("solicitacao-5")
                .setSituacao(Situacao.DISPONIVEL)
                .setOrdem(5)
                .addChave(chave1)
                .addChave(chave2);

        this.resposta1 = new Resposta()
                .setId(1L)
                .setCorretor("Corretor 1")
                .setChave(chave1)
                .setOpcao(chave1.getOpcoes().get(0));

        this.resposta2 = new Resposta()
                .setId(2L)
                .setCorretor("Corretor 2")
                .setChave(chave2)
                .setOpcao(chave2.getOpcoes().get(0));

        //mocka o repositório para retornar as amostras quando for solicitada uma correção por id
        when(repository.getOne(1L)).thenAnswer(invocationOnMock -> correcaoReservada);
        when(repository.getOne(2L)).thenAnswer(invocationOnMock -> correcaoCorrigida);
        when(repository.getOne(3L)).thenAnswer(invocationOnMock -> correcaoComDefeito);
        when(repository.getOne(4L)).thenAnswer(invocationOnMock -> correcaoDisponivel1);
        when(repository.getOne(5L)).thenAnswer(invocationOnMock -> correcaoDisponivel2);
    }

    private void mockProximo(Correcao correcao) {
        //mocka o repositório para retornar a amostra ao invés de buscar no banco de dados
        when(repository.findFirstBySituacaoOrderByOrdem(correcao.getSituacao())).thenAnswer(invocationOnMock -> Optional.of(correcao));
    }

    private void mockProximoEmpty(Situacao situacao) {
        //mocka o repositório para retornar a amostra ao invés de buscar no banco de dados
        when(repository.findFirstBySituacaoOrderByOrdem(situacao)).thenAnswer(invocationOnMock -> Optional.empty());
    }

    private void mockSave(Correcao correcao, Situacao situacaoEsperada) {
        //mocka o repositório para retornar o objeto "salvo" sem de fato salvá-lo no banco de dados
        //também testa se a situação do objeto que foi enviado para ser salvo está correta
        when(repository.save(correcao)).thenAnswer(invocationOnMock -> {
            Correcao saving = (Correcao) invocationOnMock.getArguments()[0];
            assertEquals(situacaoEsperada, saving.getSituacao());
            return saving;
        });
    }

    @Test
    public void deve_buscar_proxima_correcao_disponivel_se_existir() throws DataException {
        mockProximo(correcaoDisponivel1);
        //chama o método a ser testado
        Correcao actualNext = service.getProxima();
        //verifica se o objeto retornado é igual à amostra, isso significa que se houver pelo menos uma DISPONIVEL, ela será retornada
        assertEquals(correcaoDisponivel1, actualNext);
        //também verifica se o repository foi invocado
        verify(repository).findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL);
    }

    @Test
    public void deve_buscar_proxima_correcao_reservada_se_nao_existir_disponiveis() throws DataException {
        mockProximoEmpty(Situacao.DISPONIVEL);
        mockProximo(correcaoReservada);

        Correcao actualNext = service.getProxima();
        assertEquals(correcaoReservada, actualNext);

        //verifica se o repositório foi chamado duas vezes (uma para DISPONVEL e outra para RESERVADA)
        verify(repository).findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL);
        verify(repository).findFirstBySituacaoOrderByOrdem(Situacao.RESERVADA);
    }

    @Test
    public void deve_lancar_excecao_se_nao_existir_disponiveis_nem_reservadas() {
        mockProximoEmpty(Situacao.DISPONIVEL);
        mockProximoEmpty(Situacao.RESERVADA);

        //invoca o método e espera uma exceção
        assertThrows(NoAvailableCorrecoes.class, () -> service.getProxima());
    }

    @Test
    public void deve_possibilitar_mudar_status_de_uma_correcao_para_COM_DEFEITO() throws DataException {
        mockProximo(correcaoDisponivel1);
        mockSave(correcaoDisponivel1, Situacao.COM_DEFEITO);

        Correcao saved = service.setComDefeito(correcaoDisponivel1.getId());
        assertEquals(Situacao.COM_DEFEITO, saved.getSituacao());
        verify(repository).save(correcaoDisponivel1);
    }

    @Test
    public void nao_deve_possibilitar_mudar_status_de_uma_correcao_disponivel_que_nao_seja_a_proxima() {
        mockProximo(correcaoDisponivel1);
        assertThrows(NonNextForbiddenException.class, () -> service.setComDefeito(correcaoDisponivel2.getId()));
    }

    @Test
    public void deve_possibilitar_mudar_status_de_uma_correcao_reservada_que_nao_seja_a_proxima() throws DataException {
        mockProximo(correcaoDisponivel1);
        mockSave(correcaoReservada, Situacao.COM_DEFEITO);

        Correcao changed = service.setComDefeito(correcaoReservada.getId());
        assertEquals(Situacao.COM_DEFEITO, changed.getSituacao());
        verify(repository).save(correcaoReservada);
    }

    @Test
    public void nao_deve_possibilitar_mudar_status_de_uma_correcao_CORRIGIDA() {
        mockProximo(correcaoDisponivel1);
        assertThrows(IllegalTransactionException.class, () -> service.setComDefeito(correcaoCorrigida.getId()));
    }

    @Test
    public void nao_deve_possibilitar_mudar_status_de_uma_correcao_COM_DEFEITO() {
        mockProximo(correcaoDisponivel1);
        assertThrows(IllegalTransactionException.class, () -> service.setComDefeito(correcaoComDefeito.getId()));
    }

    @Test
    public void deve_possibilitar_reservar_a_proxima_correcao_disponivel() throws DataException {
        mockProximo(correcaoDisponivel1);
        mockSave(correcaoDisponivel1, Situacao.RESERVADA);

        Correcao saved = service.setReservada(correcaoDisponivel1.getId());
        assertEquals(Situacao.RESERVADA, saved.getSituacao());
        verify(repository).save(correcaoDisponivel1);
    }

    @Test
    public void nao_deve_possibilitar_reservar_uma_correcao_disponivel_que_nao_seja_a_proxima() {
        mockProximo(correcaoDisponivel1);
        assertThrows(NonNextForbiddenException.class, () -> service.setReservada(correcaoDisponivel2.getId()));
    }

    @Test
    public void nao_deve_possibilitar_reservar_uma_correcao_RESERVADA() {
        mockProximo(correcaoDisponivel1);
        assertThrows(IllegalTransactionException.class, () -> service.setReservada(correcaoReservada.getId()));
    }

    @Test
    public void nao_deve_possibilitar_reservar_uma_correcao_CORRIGIDA() {
        mockProximo(correcaoDisponivel1);
        assertThrows(IllegalTransactionException.class, () -> service.setReservada(correcaoCorrigida.getId()));
    }

    @Test
    public void nao_deve_possibilitar_reservar_uma_correcao_COM_DEFEITO() {
        mockProximo(correcaoDisponivel1);
        assertThrows(IllegalTransactionException.class, () -> service.setReservada(correcaoComDefeito.getId()));
    }

    @Test
    public void deve_retornar_todos_as_RESERVADAS_quando_solicitado() {
        Pageable pageable = PageRequest.of(1, 10);
        List<Correcao> samples = List.of(
                correcaoReservada,
                new Correcao().setId(6L).setSituacao(Situacao.RESERVADA),
                new Correcao().setId(7L).setSituacao(Situacao.RESERVADA),
                new Correcao().setId(8L).setSituacao(Situacao.RESERVADA)
        );
        Page<Correcao> page = new PageImpl<>(samples, pageable, 20);

        when(repository.findAllBySituacao(Situacao.RESERVADA, pageable)).thenAnswer(invocationOnMock -> page);
        Page<Correcao> result = service.getReservadas(pageable);

        assertEquals(page, result);
        verify(repository).findAllBySituacao(Situacao.RESERVADA, pageable);
    }

    @Test
    public void nao_deve_possibilitar_responder_uma_correcao_CORRIGIDA() {
        mockProximo(correcaoDisponivel1);
        assertThrows(IllegalTransactionException.class, () -> service.setResposta(correcaoCorrigida.getId(), resposta1));
    }

    @Test
    public void nao_deve_possibilitar_responder_uma_correcao_COM_DEFEITO() {
        mockProximo(correcaoDisponivel1);
        assertThrows(IllegalTransactionException.class, () -> service.setResposta(correcaoComDefeito.getId(), resposta1));
    }

    @Test
    public void nao_deve_aceitar_resposta_se_nao_e_proximo_disponivel() throws DataException {
        mockProximo(correcaoDisponivel1);
        assertThrows(NonNextForbiddenException.class, () -> service.setResposta(correcaoDisponivel2.getId(), resposta1));
    }

    @Test
    public void deve_alterar_situacao_para_corrigido_se_todas_as_chaves_foram_corrigidas() throws DataException {
        mockProximo(correcaoDisponivel1);
        mockSave(correcaoReservada, Situacao.CORRIGIDA);
        correcaoReservada.addResposta(resposta1);

        Resposta saved = service.setResposta(correcaoReservada.getId(), resposta2);
        assertEquals(correcaoReservada, saved.getCorrecao());
        assertEquals(Situacao.CORRIGIDA, saved.getCorrecao().getSituacao());
        verify(repository).save(correcaoReservada);
    }

    @Test
    public void deve_alterar_situacao_para_reservado_se_o_item_esta_parcialmente_corrigido() throws DataException {
        mockProximo(correcaoDisponivel1);
        mockSave(correcaoDisponivel1, Situacao.RESERVADA);

        Resposta saved = service.setResposta(correcaoDisponivel1.getId(), resposta2);
        assertEquals(correcaoDisponivel1, saved.getCorrecao());
        assertEquals(Situacao.RESERVADA, saved.getCorrecao().getSituacao());
        verify(repository).save(correcaoDisponivel1);
    }

    @Test
    public void deve_salvar_a_resposta() throws DataException {
        mockProximo(correcaoDisponivel1);

        when(repository.save(correcaoDisponivel1)).thenAnswer(invocationOnMock -> {
            Correcao saving = (Correcao) invocationOnMock.getArguments()[0];
            assertFalse(correcaoDisponivel1.getRespostas().isEmpty());
            assertEquals(correcaoDisponivel1.getRespostas().get(0), resposta2);
            return saving;
        });

        Resposta saved = service.setResposta(correcaoDisponivel1.getId(), resposta2);
        assertEquals(resposta2, saved);
        assertFalse(saved.getCorrecao().getRespostas().isEmpty());
        assertEquals(resposta2, correcaoDisponivel1.getRespostas().get(0));
        assertEquals(correcaoDisponivel1, saved.getCorrecao());
        verify(repository).save(correcaoDisponivel1);
    }


}
