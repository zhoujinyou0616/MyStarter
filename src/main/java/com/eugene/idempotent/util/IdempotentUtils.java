package com.eugene.idempotent.util;

import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

public class IdempotentUtils {

    private static final String LUA_SCRIPT = "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then" +
            " redis.call('expire', KEYS[1], ARGV[2]);" +
            " return 1;" +
            "else" +
            " return 0;" +
            "end";

    public static boolean checkIdempotent(StringRedisTemplate stringRedisTemplate, String key, long expireTime) {
        Long result = stringRedisTemplate.execute((RedisCallback<Long>) (connection) -> {
            return connection.eval(LUA_SCRIPT.getBytes(), ReturnType.INTEGER, 1, key.getBytes(), "1".getBytes(), String.valueOf(expireTime).getBytes());
        });

        return result != null && result == 1L;
    }

}