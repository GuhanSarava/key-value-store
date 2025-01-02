package com.kvstore.controller;

import com.kvstore.dto.KeyValueRequest;
import com.kvstore.dto.KeyValueResponse;
import com.kvstore.service.KeyValueStoreService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
public class KeyValueController {

    @Autowired
    private KeyValueStoreService service;

    @PostMapping("/object/{tenantId}")
    public ResponseEntity<?> create(@PathVariable String tenantId, @Valid @RequestBody KeyValueRequest request){
        service.createKeyValue(request, tenantId);
        return ResponseEntity.ok("Key added successfully");
    }

    @PostMapping("/batch/object/{tenantId}")
    public ResponseEntity<?> createBatch(@PathVariable String tenantId, @Valid @RequestBody List<KeyValueRequest> requests){
        return ResponseEntity.ok(service.createKeyValueBatch(requests, tenantId));
    }

    @GetMapping("/object/{tenantId}/{key}")
    public ResponseEntity<KeyValueResponse> get(@PathVariable String tenantId, @PathVariable String key){
        return ResponseEntity.ok(service.getKeyValue(key, tenantId));
    }

    @DeleteMapping("/object/{tenantId}/{key}")
    public ResponseEntity<?> delete(@PathVariable String tenantId, @PathVariable String key){
        service.deleteKeyValue(key, tenantId);
        return ResponseEntity.ok("Key deleted successfully");
    }
}
