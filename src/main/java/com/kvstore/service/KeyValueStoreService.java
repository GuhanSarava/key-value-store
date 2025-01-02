package com.kvstore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kvstore.dto.KeyValueRequest;
import com.kvstore.dto.KeyValueResponse;
import com.kvstore.entity.KeyValueEntity;
import com.kvstore.exception.NotFoundException;
import com.kvstore.repository.KeyValueRepository;
import com.kvstore.exception.DuplicateKeyException;

import com.kvstore.util.BatchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class KeyValueStoreService {

    @Autowired
    private KeyValueRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public void createKeyValue(KeyValueRequest request, String tenantId) throws DuplicateKeyException {
        if (repository.existsByKeyAndTenantId(request.getKey(), tenantId)){
            throw new DuplicateKeyException("Key '"+ request.getKey() +"' already exists for tenant: " + tenantId);
        }

        try {
            String serializedDate = objectMapper.writeValueAsString(request.getData());
            if (serializedDate.getBytes().length > 16 * 1024){
                throw new IllegalArgumentException("Value size exceeds the maximum allowed limit of 16KB");
            }

            KeyValueEntity entity = new KeyValueEntity();
            entity.setKey(request.getKey());
            entity.setTenantId(tenantId);
            entity.setData(objectMapper.writeValueAsString(request.getData()));

            if (request.getTtl() != null){
                entity.setExpiresAt(LocalDateTime.now().plusSeconds(request.getTtl()));
            }
            repository.save(entity);

        }catch (JsonProcessingException e){
            throw new RuntimeException("Failed to Serialize data");
        }
    }

    @Transactional
    public BatchResponse createKeyValueBatch(List<KeyValueRequest> requests, String tenantId){
        BatchResponse response = new BatchResponse();

        for (KeyValueRequest request : requests){

            try{

                if (repository.existsByKeyAndTenantId(request.getKey(),tenantId)){
                    response.addFailure(request.getKey(), "Key '" + request.getKey() + "' already exists for tenant: "+ tenantId);
                    continue;
                }

                String serializedDate = objectMapper.writeValueAsString(request.getData());
                if (serializedDate.getBytes().length > 16 * 1024){
                    response.addFailure(request.getKey(), "Value size exceeds 16KB limit");
                    continue;
                }

                if (request.getKey().length() > 32){
                    response.addFailure(request.getKey(), "Key length exceeds 32 characters");
                    continue;
                }

                KeyValueEntity enity = new KeyValueEntity();
                enity.setKey(request.getKey());
                enity.setTenantId(tenantId);
                enity.setData(objectMapper.writeValueAsString(request.getData()));

                if (request.getTtl() != null){
                    enity.setExpiresAt(LocalDateTime.now().plusSeconds(request.getTtl()));
                }

                repository.save(enity);

                response.addSuccess(request.getKey());
            }catch (JsonProcessingException e){
               response.addFailure(request.getKey(), "Unexpected error: " + e.getMessage());
            }
        }

        return response;
    }

    public KeyValueResponse getKeyValue(String key, String tenantId){
        KeyValueEntity entity = repository.findByKeyAndTenantId(key, tenantId)
                .orElseThrow(() -> new NotFoundException("Key not found"));

        if (entity.getExpiresAt() != null && entity.getExpiresAt().isBefore(LocalDateTime.now())){
            repository.delete(entity);
            throw new NotFoundException("Key has expired for tenant: " + tenantId);
        }

        try{

            Map<String, Object> data = objectMapper.readValue(entity.getData(), Map.class);
            return new KeyValueResponse(entity.getKey(), data);
        }catch (JsonProcessingException e){
            throw new RuntimeException("Failed to deserialize data");
        }
    }

    public void deleteKeyValue(String key, String tenantId){
        KeyValueEntity entity = repository.findByKeyAndTenantId(key, tenantId)
                .orElseThrow(() -> new NotFoundException("Key not found"));
        repository.delete(entity);
    }
}
