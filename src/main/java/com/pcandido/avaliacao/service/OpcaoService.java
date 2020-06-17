package com.pcandido.avaliacao.service;

import com.pcandido.avaliacao.exception.OpcaoInvalidaException;
import com.pcandido.avaliacao.model.Chave;
import com.pcandido.avaliacao.model.Opcao;
import com.pcandido.avaliacao.repository.OpcaoRepository;
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
