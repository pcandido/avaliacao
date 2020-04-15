package com.pcandido.caed.repository;

import com.pcandido.caed.model.Item;
import com.pcandido.caed.model.Situacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findFirstBySituacaoOrderByOrdem(Situacao situacao);

    List<Item> findAllBySituacao(Situacao situacao);

}
