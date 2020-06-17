package com.pcandido.avaliacao.controller;

import com.pcandido.avaliacao.controller.model.CorrecaoRequest;
import com.pcandido.avaliacao.controller.wrapper.DataWrapper;
import com.pcandido.avaliacao.controller.wrapper.DescricaoWrapper;
import com.pcandido.avaliacao.exception.AppException;
import com.pcandido.avaliacao.exception.SemItemException;
import com.pcandido.avaliacao.model.Item;
import com.pcandido.avaliacao.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("correcoes")
public class ItemController {

    @Autowired
    private ItemService service;

    /**
     * Carrega próxima correção disponível, e se não exitir nenhuma, a
     * próxima reservada.
     */
    @GetMapping("proxima")
    public ResponseEntity<DataWrapper<Item>> getProximo() throws SemItemException {
        return ResponseEntity.ok().body(new DataWrapper<>(service.getProximo()));
    }

    /**
     * Carrega a lista de todas as correções reservadas.
     */
    @GetMapping("reservadas")
    public ResponseEntity<DataWrapper<List<Item>>> getReservadas() {
        return ResponseEntity.ok().body(new DataWrapper<>(service.getReservadas()));
    }

    /**
     * Altera a situação de uma correção para reservada
     */
    @PostMapping("reservadas/{idItem}")
    public ResponseEntity<DescricaoWrapper> reservar(@PathVariable long idItem) throws AppException {
        service.setReservada(idItem);
        return ResponseEntity.ok().body(new DescricaoWrapper("Correção reservada com sucesso"));
    }

    /**
     * Salvar correção de uma determinada chave de um determinado item
     */
    @PostMapping("{idItem}")
    public ResponseEntity<DescricaoWrapper> corrigir(@PathVariable long idItem, @RequestBody @Valid CorrecaoRequest correcaoRequest) throws AppException {
        service.addCorrecoes(idItem, correcaoRequest.toCorrecoes());
        return ResponseEntity.ok().body(new DescricaoWrapper("Correção salva com sucesso"));
    }

    /**
     * Altera a situação de um item para COM_DEFEITO
     */
    @PostMapping("defeito/{idItem}")
    public ResponseEntity<DescricaoWrapper> setComDefeito(@PathVariable long idItem) throws AppException {
        service.setComDefeito(idItem);
        return ResponseEntity.ok().body(new DescricaoWrapper("Correção marcada como defeito com sucesso"));
    }
}
