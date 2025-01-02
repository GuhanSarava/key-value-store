package com.kvstore.repository;

import com.kvstore.entity.KeyValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KeyValueRepository extends JpaRepository<KeyValueEntity, String> {
    Optional<KeyValueEntity> findByKeyAndTenantId(String key, String tenantId);

    boolean existsByKeyAndTenantId(String key, String tenantId);
}
