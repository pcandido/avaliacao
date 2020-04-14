package com.pcandido.caed.controller;

import com.pcandido.caed.exception.NoAvailableCorrecoes;
import com.pcandido.caed.model.Correcao;
import com.pcandido.caed.service.CorrecaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("correcoes")
public class CorrecaoController {

    @Autowired
    private CorrecaoService service;

    @GetMapping("proxima")
    public ResponseEntity<Correcao> getProxima() throws NoAvailableCorrecoes {
        return ResponseEntity.ok().body(service.getProxima());
    }

}
