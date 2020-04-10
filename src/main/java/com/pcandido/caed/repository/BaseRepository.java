package com.pcandido.caed.repository;

import com.pcandido.caed.model.Persistable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T extends Persistable> extends JpaRepository<T, Long> {
}
