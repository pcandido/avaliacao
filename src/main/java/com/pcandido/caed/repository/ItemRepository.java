package com.pcandido.caed.repository;

import com.pcandido.caed.model.Item;
import com.pcandido.caed.model.Situacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findFirstBySituacaoOrderByOrdem(Situacao situacao);

    Page<Item> findAllBySituacao(Situacao situacao, Pageable pageable);

}
