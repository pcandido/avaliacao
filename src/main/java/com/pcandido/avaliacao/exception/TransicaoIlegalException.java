package com.pcandido.avaliacao.exception;

import com.pcandido.avaliacao.model.Situacao;
import org.springframework.http.HttpStatus;

public class TransicaoIlegalException extends AppException {

    public TransicaoIlegalException(Situacao from, Situacao to) {
        super(HttpStatus.BAD_REQUEST, "NAO_PERMITODO", String.format("Não é permitido alterar um item de %s para %s", from, to));
    }

}
