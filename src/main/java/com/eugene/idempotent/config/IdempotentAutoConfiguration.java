package com.eugene.idempotent.config;

import com.eugene.idempotent.interceptor.IdempotentInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnWebApplication
public class IdempotentAutoConfiguration implements WebMvcConfigurer {

    private final StringRedisTemplate stringRedisTemplate;

    public IdempotentAutoConfiguration(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Bean
    public IdempotentInterceptor idempotentInterceptor() {
        return new IdempotentInterceptor(stringRedisTemplate);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(idempotentInterceptor());
    }
}