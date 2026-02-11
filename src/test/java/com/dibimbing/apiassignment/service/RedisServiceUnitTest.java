package com.dibimbing.apiassignment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisServiceUnitTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private RedisService redisService;

    @BeforeEach
    void setUp() {
        // Removed unnecessary stubbing for all tests
    }

    @Test
    void set_ShouldWork() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        String key = "testKey";
        String value = "testValue";

        redisService.set(key, value);

        verify(valueOperations, times(1)).set(key, value);
    }

    @Test
    void set_ShouldHandleException() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        String key = "testKey";
        String value = "testValue";
        doThrow(new RuntimeException("Redis error")).when(valueOperations).set(key, value);

        assertDoesNotThrow(() -> redisService.set(key, value));
        verify(valueOperations, times(1)).set(key, value);
    }

    @Test
    void get_ShouldReturnValue() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        String key = "testKey";
        String expectedValue = "testValue";
        when(valueOperations.get(key)).thenReturn(expectedValue);

        Object result = redisService.get(key);

        assertEquals(expectedValue, result);
        verify(valueOperations, times(1)).get(key);
    }

    @Test
    void get_ShouldReturnNullOnException() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        String key = "testKey";
        when(valueOperations.get(key)).thenThrow(new RuntimeException("Redis error"));

        Object result = redisService.get(key);

        assertNull(result);
    }

    @Test
    void delete_ShouldWork() {
        String key = "testKey";

        redisService.delete(key);

        verify(redisTemplate, times(1)).delete(key);
    }

    @Test
    void deleteMultiple_ShouldWork() {
        String key1 = "key1";
        String key2 = "key2";

        redisService.deleteMultiple(key1, key2);

        verify(redisTemplate, times(1)).delete(key1);
        verify(redisTemplate, times(1)).delete(key2);
    }
}
