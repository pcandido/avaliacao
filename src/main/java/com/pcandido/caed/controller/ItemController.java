package com.pcandido.caed.controller;

import com.pcandido.caed.controller.model.CorrecaoRequest;
import com.pcandido.caed.controller.wrapper.DataWrapper;
import com.pcandido.caed.controller.wrapper.DescricaoWrapper;
import com.pcandido.caed.exception.AppException;
import com.pcandido.caed.exception.SemItemException;
import com.pcandido.caed.model.Item;
import com.pcandido.caed.service.ItemService;
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
    @GetMapping("proximo")
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
