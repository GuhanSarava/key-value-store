package com.kvstore.repository;

import com.kvstore.entity.KeyValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KeyValueRepository extends JpaRepository<KeyValueEntity, String> {
    Optional<KeyValueEntity> findByKey(String key);

    boolean existsByKey(String key);
}
