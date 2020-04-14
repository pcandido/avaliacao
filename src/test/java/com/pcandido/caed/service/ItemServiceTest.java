package com.pcandido.caed.service;

import com.pcandido.caed.exception.DataException;
import com.pcandido.caed.exception.IllegalTransactionException;
import com.pcandido.caed.exception.NoAvailableItems;
import com.pcandido.caed.exception.NonNextForbiddenException;
import com.pcandido.caed.model.*;
import com.pcandido.caed.repository.ItemRepository;
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
public class ItemServiceTest {

    @Autowired
    private ItemService service;

    @MockBean
    private ItemRepository repository;

    Item itemDisponivel1;
    Item itemDisponivel2;
    Item itemReservada;
    Item itemCorrigida;
    Item itemComDefeito;
    Correcao correcao1;
    Correcao correcao2;


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

        this.itemReservada = new Item()
                .setId(1L)
                .setItem("item-1")
                .setReferencia("referencia-1")
                .setSequencial("sequencial-1")
                .setSolicitacao("solicitacao-1")
                .setSituacao(Situacao.RESERVADO)
                .setOrdem(1)
                .addChave(chave1)
                .addChave(chave2);

        this.itemCorrigida = new Item()
                .setId(2L)
                .setItem("item-2")
                .setReferencia("referencia-2")
                .setSequencial("sequencial-2")
                .setSolicitacao("solicitacao-2")
                .setSituacao(Situacao.CORRIGIDO)
                .setOrdem(2)
                .addChave(chave1)
                .addChave(chave2);

        this.itemComDefeito = new Item()
                .setId(3L)
                .setItem("item-3")
                .setReferencia("referencia-3")
                .setSequencial("sequencial-3")
                .setSolicitacao("solicitacao-3")
                .setSituacao(Situacao.COM_DEFEITO)
                .setOrdem(3)
                .addChave(chave1)
                .addChave(chave2);

        this.itemDisponivel1 = new Item()
                .setId(4L)
                .setItem("item-4")
                .setReferencia("referencia-4")
                .setSequencial("sequencial-4")
                .setSolicitacao("solicitacao-4")
                .setSituacao(Situacao.DISPONIVEL)
                .setOrdem(4)
                .addChave(chave1)
                .addChave(chave2);

        this.itemDisponivel2 = new Item()
                .setId(5L)
                .setItem("item-5")
                .setReferencia("referencia-5")
                .setSequencial("sequencial-5")
                .setSolicitacao("solicitacao-5")
                .setSituacao(Situacao.DISPONIVEL)
                .setOrdem(5)
                .addChave(chave1)
                .addChave(chave2);

        this.correcao1 = new Correcao()
                .setId(1L)
                .setCorretor("Corretor 1")
                .setChave(chave1)
                .setOpcao(chave1.getOpcoes().get(0));

        this.correcao2 = new Correcao()
                .setId(2L)
                .setCorretor("Corretor 2")
                .setChave(chave2)
                .setOpcao(chave2.getOpcoes().get(0));

        //mocka o repositório para retornar as amostras quando for solicitada uma correção por id
        when(repository.getOne(1L)).thenAnswer(invocationOnMock -> itemReservada);
        when(repository.getOne(2L)).thenAnswer(invocationOnMock -> itemCorrigida);
        when(repository.getOne(3L)).thenAnswer(invocationOnMock -> itemComDefeito);
        when(repository.getOne(4L)).thenAnswer(invocationOnMock -> itemDisponivel1);
        when(repository.getOne(5L)).thenAnswer(invocationOnMock -> itemDisponivel2);
    }

    private void mockProximo(Item item) {
        //mocka o repositório para retornar a amostra ao invés de buscar no banco de dados
        when(repository.findFirstBySituacaoOrderByOrdem(item.getSituacao())).thenAnswer(invocationOnMock -> Optional.of(item));
    }

    private void mockProximoEmpty(Situacao situacao) {
        //mocka o repositório para retornar a amostra ao invés de buscar no banco de dados
        when(repository.findFirstBySituacaoOrderByOrdem(situacao)).thenAnswer(invocationOnMock -> Optional.empty());
    }

    private void mockSave(Item item, Situacao situacaoEsperada) {
        //mocka o repositório para retornar o objeto "salvo" sem de fato salvá-lo no banco de dados
        //também testa se a situação do objeto que foi enviado para ser salvo está correta
        when(repository.save(item)).thenAnswer(invocationOnMock -> {
            Item saving = (Item) invocationOnMock.getArguments()[0];
            assertEquals(situacaoEsperada, saving.getSituacao());
            return saving;
        });
    }

    @Test
    public void deve_buscar_proximo_item_disponivel_se_existir() throws DataException {
        mockProximo(itemDisponivel1);
        //chama o método a ser testado
        Item actualNext = service.getProxima();
        //verifica se o objeto retornado é igual à amostra, isso significa que se houver pelo menos uma DISPONIVEL, ela será retornada
        assertEquals(itemDisponivel1, actualNext);
        //também verifica se o repository foi invocado
        verify(repository).findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL);
    }

    @Test
    public void deve_buscar_proximo_item_reservado_se_nao_existir_disponiveis() throws DataException {
        mockProximoEmpty(Situacao.DISPONIVEL);
        mockProximo(itemReservada);

        Item actualNext = service.getProxima();
        assertEquals(itemReservada, actualNext);

        //verifica se o repositório foi chamado duas vezes (uma para DISPONVEL e outra para RESERVADA)
        verify(repository).findFirstBySituacaoOrderByOrdem(Situacao.DISPONIVEL);
        verify(repository).findFirstBySituacaoOrderByOrdem(Situacao.RESERVADO);
    }

    @Test
    public void deve_lancar_excecao_se_nao_existir_disponiveis_nem_reservadas() {
        mockProximoEmpty(Situacao.DISPONIVEL);
        mockProximoEmpty(Situacao.RESERVADO);

        //invoca o método e espera uma exceção
        assertThrows(NoAvailableItems.class, () -> service.getProxima());
    }

    @Test
    public void deve_possibilitar_mudar_status_de_um_item_para_COM_DEFEITO() throws DataException {
        mockProximo(itemDisponivel1);
        mockSave(itemDisponivel1, Situacao.COM_DEFEITO);

        Item saved = service.setComDefeito(itemDisponivel1.getId());
        assertEquals(Situacao.COM_DEFEITO, saved.getSituacao());
        verify(repository).save(itemDisponivel1);
    }

    @Test
    public void nao_deve_possibilitar_mudar_status_de_um_item_disponivel_que_nao_seja_o_proximo() {
        mockProximo(itemDisponivel1);
        assertThrows(NonNextForbiddenException.class, () -> service.setComDefeito(itemDisponivel2.getId()));
    }

    @Test
    public void deve_possibilitar_mudar_status_de_um_item_reservado_que_nao_seja_o_proximo() throws DataException {
        mockProximo(itemDisponivel1);
        mockSave(itemReservada, Situacao.COM_DEFEITO);

        Item changed = service.setComDefeito(itemReservada.getId());
        assertEquals(Situacao.COM_DEFEITO, changed.getSituacao());
        verify(repository).save(itemReservada);
    }

    @Test
    public void nao_deve_possibilitar_mudar_status_de_um_item_CORRIGIDO() {
        mockProximo(itemDisponivel1);
        assertThrows(IllegalTransactionException.class, () -> service.setComDefeito(itemCorrigida.getId()));
    }

    @Test
    public void nao_deve_possibilitar_mudar_status_de_um_item_COM_DEFEITO() {
        mockProximo(itemDisponivel1);
        assertThrows(IllegalTransactionException.class, () -> service.setComDefeito(itemComDefeito.getId()));
    }

    @Test
    public void deve_possibilitar_reservar_o_proximo_item_disponivel() throws DataException {
        mockProximo(itemDisponivel1);
        mockSave(itemDisponivel1, Situacao.RESERVADO);

        Item saved = service.setReservada(itemDisponivel1.getId());
        assertEquals(Situacao.RESERVADO, saved.getSituacao());
        verify(repository).save(itemDisponivel1);
    }

    @Test
    public void nao_deve_possibilitar_reservar_um_item_disponivel_que_nao_seja_o_proximo() {
        mockProximo(itemDisponivel1);
        assertThrows(NonNextForbiddenException.class, () -> service.setReservada(itemDisponivel2.getId()));
    }

    @Test
    public void nao_deve_possibilitar_reservar_um_item_RESERVADO() {
        mockProximo(itemDisponivel1);
        assertThrows(IllegalTransactionException.class, () -> service.setReservada(itemReservada.getId()));
    }

    @Test
    public void nao_deve_possibilitar_reservar_um_item_CORRIGIDO() {
        mockProximo(itemDisponivel1);
        assertThrows(IllegalTransactionException.class, () -> service.setReservada(itemCorrigida.getId()));
    }

    @Test
    public void nao_deve_possibilitar_reservar_um_item_COM_DEFEITO() {
        mockProximo(itemDisponivel1);
        assertThrows(IllegalTransactionException.class, () -> service.setReservada(itemComDefeito.getId()));
    }

    @Test
    public void deve_retornar_todos_as_RESERVADAS_quando_solicitado() {
        Pageable pageable = PageRequest.of(1, 10);
        List<Item> samples = List.of(
                itemReservada,
                new Item().setId(6L).setSituacao(Situacao.RESERVADO),
                new Item().setId(7L).setSituacao(Situacao.RESERVADO),
                new Item().setId(8L).setSituacao(Situacao.RESERVADO)
        );
        Page<Item> page = new PageImpl<>(samples, pageable, 20);

        when(repository.findAllBySituacao(Situacao.RESERVADO, pageable)).thenAnswer(invocationOnMock -> page);
        Page<Item> result = service.getReservadas(pageable);

        assertEquals(page, result);
        verify(repository).findAllBySituacao(Situacao.RESERVADO, pageable);
    }

    @Test
    public void nao_deve_possibilitar_corrigir_um_item_CORRIGIDO() {
        mockProximo(itemDisponivel1);
        assertThrows(IllegalTransactionException.class, () -> service.setCorrecao(itemCorrigida.getId(), correcao1));
    }

    @Test
    public void nao_deve_possibilitar_corrigir_um_item_COM_DEFEITO() {
        mockProximo(itemDisponivel1);
        assertThrows(IllegalTransactionException.class, () -> service.setCorrecao(itemComDefeito.getId(), correcao1));
    }

    @Test
    public void nao_deve_aceitar_correcao_se_nao_e_proximo_disponivel() {
        mockProximo(itemDisponivel1);
        assertThrows(NonNextForbiddenException.class, () -> service.setCorrecao(itemDisponivel2.getId(), correcao1));
    }

    @Test
    public void deve_alterar_situacao_para_corrigido_se_todas_as_chaves_foram_corrigidas() throws DataException {
        mockProximo(itemDisponivel1);
        mockSave(itemReservada, Situacao.CORRIGIDO);
        itemReservada.addCorrecao(correcao1);

        Correcao saved = service.setCorrecao(itemReservada.getId(), correcao2);
        assertEquals(itemReservada, saved.getItem());
        assertEquals(Situacao.CORRIGIDO, saved.getItem().getSituacao());
        verify(repository).save(itemReservada);
    }

    @Test
    public void deve_alterar_situacao_para_reservado_se_o_item_esta_parcialmente_corrigido() throws DataException {
        mockProximo(itemDisponivel1);
        mockSave(itemDisponivel1, Situacao.RESERVADO);

        Correcao saved = service.setCorrecao(itemDisponivel1.getId(), correcao2);
        assertEquals(itemDisponivel1, saved.getItem());
        assertEquals(Situacao.RESERVADO, saved.getItem().getSituacao());
        verify(repository).save(itemDisponivel1);
    }

    @Test
    public void deve_salvar_a_correcao() throws DataException {
        mockProximo(itemDisponivel1);

        when(repository.save(itemDisponivel1)).thenAnswer(invocationOnMock -> {
            Item saving = (Item) invocationOnMock.getArguments()[0];
            assertFalse(itemDisponivel1.getCorrecaos().isEmpty());
            assertEquals(itemDisponivel1.getCorrecaos().get(0), correcao2);
            return saving;
        });

        Correcao saved = service.setCorrecao(itemDisponivel1.getId(), correcao2);
        assertEquals(correcao2, saved);
        assertFalse(saved.getItem().getCorrecaos().isEmpty());
        assertEquals(correcao2, itemDisponivel1.getCorrecaos().get(0));
        assertEquals(itemDisponivel1, saved.getItem());
        verify(repository).save(itemDisponivel1);
    }


}
