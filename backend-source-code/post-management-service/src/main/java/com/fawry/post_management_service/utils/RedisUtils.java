package com.fawry.post_management_service.utils;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RedisUtils {

    private final StringRedisTemplate stringRedisTemplate;
    private static StringRedisTemplate staticTemplate;

    @PostConstruct
    public void init() {
        staticTemplate = this.stringRedisTemplate;
    }

    public static String getJsonValue(String tableName, String key) {
        HashOperations<String, String, String> hashOps = staticTemplate.opsForHash();
        return hashOps.get(tableName, key);
    }
}