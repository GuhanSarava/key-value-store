package com.kvstore.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "key_value_store")
public class KeyValueEntity {

    @Id
    @Column(name = "key", unique = true, length = 32, nullable = false)
    private String key;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "data", nullable = false, columnDefinition = "TEXT")
    private String data;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
