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

    @PostMapping("/object")
    public ResponseEntity<?> create(@Valid @RequestBody KeyValueRequest request){
        service.createKeyValue(request);
        return ResponseEntity.ok("Key added successfully");
    }

    @PostMapping("/batch/object")
    public ResponseEntity<?> createBatch(@Valid @RequestBody List<KeyValueRequest> requests){
        service.createKeyValueBatch(requests);
        return ResponseEntity.ok("Keys added successfully");
    }

    @GetMapping("/object/{key}")
    public ResponseEntity<KeyValueResponse> get(@PathVariable String key){
        return ResponseEntity.ok(service.getKeyValue(key));
    }

    @DeleteMapping("/object/{key}")
    public ResponseEntity<?> delete(@PathVariable String key){
        service.deleteKeyValue(key);
        return ResponseEntity.ok("Key deleted successfully");
    }
}
