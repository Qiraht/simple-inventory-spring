package com.dibimbing.apiassignment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            log.info("Redis set successfully for key: {}", key);
        } catch (Exception e) {
            log.error("Redis set failed for key: {}", key, e);
        }
    }

    public Object get(String key) {
        try {
            log.info("Redis get successfully for key: {}", key);
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Error getting value for key: {}", key, e);
            return null;
        }
    }

    public void delete(String key) {
        try {
            redisTemplate.delete(key);
            log.info("Redis delete successfully for key: {}", key);
        } catch (Exception e) {
            log.error("Error deleting key: {}", key, e);
        }
    }

    public void deleteMultiple(String... keys) {
        try {
            for (String key : keys) {
                redisTemplate.delete(key);
            }
        } catch (Exception e) {
            log.error("Error deleting keys: {}", keys, e);
        }
    }
}
