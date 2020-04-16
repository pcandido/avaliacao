package com.pcandido.caed.service;

import com.pcandido.caed.exception.OpcaoInvalidaException;
import com.pcandido.caed.model.Chave;
import com.pcandido.caed.model.Opcao;
import com.pcandido.caed.repository.OpcaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OpcaoService {

    private final OpcaoRepository opcaoRepository;

    @Autowired
    public OpcaoService(OpcaoRepository opcaoRepository) {this.opcaoRepository = opcaoRepository;}

    public Opcao getOpcao(Chave chave, String valor) throws OpcaoInvalidaException {
        return this.opcaoRepository.findByChaveAndValor(chave, valor).orElseThrow(() -> new OpcaoInvalidaException(chave.getId(), valor));
    }

}
