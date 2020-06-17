package com.pcandido.avaliacao.repository;

import com.pcandido.avaliacao.model.Item;
import com.pcandido.avaliacao.model.Situacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findFirstBySituacaoOrderByOrdem(Situacao situacao);

    List<Item> findAllBySituacao(Situacao situacao);

}
