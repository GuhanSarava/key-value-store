package com.kvstore.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kvstore.Utilities.BatchResponse;
import com.kvstore.dto.KeyValueRequest;
import com.kvstore.dto.KeyValueResponse;
import com.kvstore.entity.KeyValueEntity;
import com.kvstore.exception.DuplicateKeyException;
import com.kvstore.exception.NotFoundException;
import com.kvstore.repository.KeyValueRepository;
import com.kvstore.service.KeyValueStoreService;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Unit Tests
class KeyValueStoreTest {

    @Mock
    private KeyValueRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private KeyValueStoreService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //successful creation of a key-value pair
    @Test
    void testCreateKeyValue_Success() throws JsonProcessingException {
        KeyValueRequest request = new KeyValueRequest("key1", Map.of("field", "value"), 3600L);

        String tenantId = "tenant1";

        when(repository.existsByKeyAndTenantId("key1", tenantId)).thenReturn(false);
        when(objectMapper.writeValueAsString(request.getData())).thenReturn("{\"field\":\"value\"}");

        assertDoesNotThrow(() -> service.createKeyValue(request, tenantId));
        verify(repository, times(1)).save(any(KeyValueEntity.class));
    }

    //Verifies that creating a duplicate key throws a DuplicateKeyException
    @Test
    void testCreateKeyValue_DuplicateKey() {
        KeyValueRequest request = new KeyValueRequest("key1", Map.of("field", "value"), 3600L);

        String tenantId = "tenant1";

        when(repository.existsByKeyAndTenantId("key1", tenantId)).thenReturn(true);

        assertThrows(DuplicateKeyException.class, () -> service.createKeyValue(request, tenantId));
        verify(repository, never()).save(any(KeyValueEntity.class));
    }

    //batch creation where some keys succeed, and others fail due to duplication
    @Test
    void testCreateKeyValueBatch_PartialSuccess() throws JsonProcessingException {
        List<KeyValueRequest> requests = List.of(
                new KeyValueRequest("key1", Map.of("field1", "value1"), 3600L),
                new KeyValueRequest("key2", Map.of("field2", "value2"), 3600L),
                new KeyValueRequest("key1", Map.of("field3", "value3"), 3600L)
        );
        String tenantId = "tenant1";

        when(repository.existsByKeyAndTenantId("key1", tenantId)).thenReturn(true);
        when(repository.existsByKeyAndTenantId("key2", tenantId)).thenReturn(false);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        BatchResponse response = service.createKeyValueBatch(requests, tenantId);

        assertEquals(1, response.getSuccesses().size());
        assertEquals(2, response.getFailures().size());
        verify(repository, times(1)).save(any(KeyValueEntity.class));
    }

    //testGetKeyValue_Success
    @Test
    void testGetKeyValue_Success() throws JsonProcessingException {
        String key = "key1";
        String tenantId = "tenant1";
        KeyValueEntity entity = new KeyValueEntity();
        entity.setKey(key);
        entity.setTenantId(tenantId);
        entity.setData("{}");

        when(repository.findByKeyAndTenantId(key, tenantId)).thenReturn(Optional.of(entity));
        when(objectMapper.readValue(entity.getData(), Map.class)).thenReturn(Map.of("field", "value"));

        KeyValueResponse response = service.getKeyValue(key, tenantId);

        assertNotNull(response);
        assertEquals(key, response.getKey());
        assertEquals(Map.of("field", "value"), response.getData());
    }

    //fetching a key that does not exist.
    @Test
    void testGetKeyValue_NotFound() {
        String key = "key1";
        String tenantId = "tenant1";

        when(repository.findByKeyAndTenantId(key, tenantId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getKeyValue(key, tenantId));
    }

    //key-value pair exceeding the size limit (16KB) is rejected
    @Test
    void testCreateKeyValue_Exceeds16KB() throws JsonProcessingException {
        // Creating a large JSON object (17KB)
        String largeValue = "x".repeat(17 * 1024); // String with 17KB of data
        KeyValueRequest request = new KeyValueRequest("key1", Map.of("field", largeValue), 3600L);
        String tenantId = "tenant1";

        when(repository.existsByKeyAndTenantId("key1", tenantId)).thenReturn(false);
        when(objectMapper.writeValueAsString(request.getData())).thenReturn("{\"field\":\"" + largeValue + "\"}");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.createKeyValue(request, tenantId));
        assertEquals("Value size exceeds the maximum allowed limit of 16KB", exception.getMessage());
        verify(repository, never()).save(any(KeyValueEntity.class));
    }

    //batch creation where one key fails due to exceeding the size limit
    @Test
    void testCreateKeyValueBatch_LargeJSONFailure() throws JsonProcessingException {
        String largeValue = "x".repeat(17 * 1024); // String with 17KB of data

        List<KeyValueRequest> requests = List.of(
                new KeyValueRequest("key1", Map.of("field1", "value1"), 3600L),
                new KeyValueRequest("key2", Map.of("field2", largeValue), 3600L), // Exceeds 16KB
                new KeyValueRequest("key3", Map.of("field3", "value3"), 3600L)
        );
        String tenantId = "tenant1";

        when(repository.existsByKeyAndTenantId(anyString(), eq(tenantId))).thenReturn(false);
        when(objectMapper.writeValueAsString(any())).thenAnswer(invocation -> {
            Map<String, Object> data = invocation.getArgument(0);
            String key = data.keySet().iterator().next();
            return key.equals("field2") ? "{\"field2\":\"" + largeValue + "\"}" : "{}";
        });

        BatchResponse response = service.createKeyValueBatch(requests, tenantId);

        assertEquals(2, response.getSuccesses().size());
        assertEquals(1, response.getFailures().size());
        verify(repository, times(2)).save(any(KeyValueEntity.class));
    }

    //Verifies that fetching an expired key results in a NotFoundException
    @Test
    void testGetKeyValue_KeyExpired() {
        String key = "key1";
        String tenantId = "tenant1";

        KeyValueEntity expiredEntity = new KeyValueEntity();
        expiredEntity.setKey(key);
        expiredEntity.setTenantId(tenantId);
        expiredEntity.setData("{}");
        expiredEntity.setExpiresAt(LocalDateTime.now().minusSeconds(1));

        when(repository.findByKeyAndTenantId(key, tenantId)).thenReturn(Optional.of(expiredEntity));

        assertThrows(NotFoundException.class, () -> service.getKeyValue(key, tenantId));
        verify(repository, times(1)).delete(expiredEntity);
    }

    // fetching a key that is still valid within its TTL.
    @Test
    void testGetKeyValue_KeyNotExpired() throws JsonProcessingException {
        String key = "key1";
        String tenantId = "tenant1";

        // Simulating a key with TTL in the future
        KeyValueEntity validEntity = new KeyValueEntity();
        validEntity.setKey(key);
        validEntity.setTenantId(tenantId);
        validEntity.setData("{}");
        validEntity.setExpiresAt(LocalDateTime.now().plusSeconds(3600));

        when(repository.findByKeyAndTenantId(key, tenantId)).thenReturn(Optional.of(validEntity));
        when(objectMapper.readValue(validEntity.getData(), Map.class)).thenReturn(Map.of("field", "value"));

        KeyValueResponse response = service.getKeyValue(key, tenantId);

        assertNotNull(response);
        assertEquals(key, response.getKey());
        assertEquals(Map.of("field", "value"), response.getData());
        verify(repository, never()).delete(any(KeyValueEntity.class));
    }

    //Tests fetching a key with no TTL (does not expire).
    @Test
    void testGetKeyValue_NoTTL() throws JsonProcessingException {
        String key = "key1";
        String tenantId = "tenant1";

        // Simulating a key without TTL
        KeyValueEntity entity = new KeyValueEntity();
        entity.setKey(key);
        entity.setTenantId(tenantId);
        entity.setData("{}");

        when(repository.findByKeyAndTenantId(key, tenantId)).thenReturn(Optional.of(entity));
        when(objectMapper.readValue(entity.getData(), Map.class)).thenReturn(Map.of("field", "value"));

        KeyValueResponse response = service.getKeyValue(key, tenantId);

        assertNotNull(response);
        assertEquals(key, response.getKey());
        assertEquals(Map.of("field", "value"), response.getData());
        verify(repository, never()).delete(any(KeyValueEntity.class));
    }

    //Verifies the creation of a key-value pair with a TTL
    @Test
    void testCreateKeyValue_WithTTL() throws JsonProcessingException {
        KeyValueRequest request = new KeyValueRequest("key1", Map.of("field", "value"), 3600L);
        request.setKey("key1");
        request.setData(Map.of("field", "value"));
        request.setTtl(3600L);
        String tenantId = "tenant1";

        when(repository.existsByKeyAndTenantId("key1", tenantId)).thenReturn(false);
        when(objectMapper.writeValueAsString(request.getData())).thenReturn("{\"field\":\"value\"}");

        service.createKeyValue(request, tenantId);

        verify(repository, times(1)).save(argThat(entity ->
                entity.getExpiresAt() != null &&
                        entity.getExpiresAt().isAfter(LocalDateTime.now()) &&
                        entity.getExpiresAt().isBefore(LocalDateTime.now().plusSeconds(3601))
        ));
    }
}