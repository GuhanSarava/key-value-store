package com.kvstore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kvstore.dto.KeyValueRequest;
import com.kvstore.dto.KeyValueResponse;
import com.kvstore.dto.ObjectData;
import com.kvstore.entity.KeyValueEntity;
import com.kvstore.exception.NotFoundException;
import com.kvstore.repository.KeyValueRepository;
import com.kvstore.exception.DuplicateKeyException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;

@Service
public class KeyValueStoreService {

    @Autowired
    private KeyValueRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public void createKeyValue(KeyValueRequest request) throws DuplicateKeyException {
        if (repository.existsByKey(request.getKey())){
            throw new DuplicateKeyException("Key '"+ request.getKey() +"' already exists.");
        }

        KeyValueEntity entity = new KeyValueEntity();
        entity.setKey(request.getKey());

        try {
            entity.setData(objectMapper.writeValueAsString(request.getData()));
        }catch (JsonProcessingException e){
            throw new RuntimeException("Failed to Serialize data");
        }

        if (request.getTtl() != null){
            entity.setExpiresAt(LocalDateTime.now().plusSeconds(request.getTtl()));
        }
        repository.save(entity);
    }

    @Transactional
    public void createKeyValueBatch(List<KeyValueRequest> requests){
        for (KeyValueRequest request : requests){
            if (repository.existsByKey(request.getKey())){
                throw new DuplicateKeyException("Key '" + request.getKey() + "' already exists");
            }

            KeyValueEntity enity = new KeyValueEntity();
            enity.setKey(request.getKey());

            try{
                enity.setData(objectMapper.writeValueAsString(request.getData()));
            }catch (JsonProcessingException e){
                throw new RuntimeException("Failed to Serialize data for key: " + request.getKey());
            }

            if (request.getTtl() != null){
                enity.setExpiresAt(LocalDateTime.now().plusSeconds(request.getTtl()));
            }

            repository.save(enity);
        }
    }

    public KeyValueResponse getKeyValue(String key){
        KeyValueEntity entity = repository.findByKey(key)
                .orElseThrow(() -> new NotFoundException("Key not found"));

        if (entity.getExpiresAt() != null && entity.getExpiresAt().isBefore(LocalDateTime.now())){
            repository.delete(entity);
            throw new NotFoundException("Key has expired");
        }

        try{
            ObjectData data = objectMapper.readValue(entity.getData(), ObjectData.class);
            return new KeyValueResponse(entity.getKey(), data);
        }catch (JsonProcessingException e){
            throw new RuntimeException("Failed to deserialize data");
        }
    }

    public void deleteKeyValue(String key){
        KeyValueEntity entity = repository.findByKey(key)
                .orElseThrow(() -> new NotFoundException("Key not found"));
        repository.delete(entity);
    }
}
