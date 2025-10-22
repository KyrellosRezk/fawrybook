package com.fawry.user_management_service.utils;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
public class RedisUtils {

    private final StringRedisTemplate stringRedisTemplate;
    private static StringRedisTemplate staticTemplate;

    @PostConstruct
    public void init() {
        staticTemplate = this.stringRedisTemplate;
    }

    public static void setJsonValue(String tableName, String key, String value) {
        HashOperations<String, String, String> hashOps = staticTemplate.opsForHash();
        hashOps.put(tableName, key, value);
    }

    public static String getJsonValue(String tableName, String key) {
        HashOperations<String, String, String> hashOps = staticTemplate.opsForHash();
        return hashOps.get(tableName, key);
    }

    public static void setKeyJsonValue(String tableName, String key, String value) {
        String redisKey = tableName + ":" + key;
        staticTemplate.opsForValue().set(redisKey, value);
    }

    public static String getKeyJsonValue(String tableName, String key) {
        String redisKey = tableName + ":" + key;
        Object value = staticTemplate.opsForValue().get(redisKey);
        return value != null ? value.toString() : null;
    }

    public static void deleteJsonValue(String tableName, String key) {
        HashOperations<String, String, String> hashOps = staticTemplate.opsForHash();
        hashOps.delete(tableName, key);
    }

    public static void deleteKeyJsonValue(String tableName, String key) {
        String redisKey = tableName + ":" + key;
        staticTemplate.delete(redisKey);
    }

    public static void setExpire(String tableName, String key, long timeout, TimeUnit unit) {
        String redisKey = tableName + ":" + key;
        staticTemplate.expire(redisKey, timeout, unit);
    }

    public static Long getExpire(String tableName, String key, TimeUnit unit) {
        String redisKey = tableName + ":" + key;
        return staticTemplate.getExpire(redisKey, unit);
    }
}