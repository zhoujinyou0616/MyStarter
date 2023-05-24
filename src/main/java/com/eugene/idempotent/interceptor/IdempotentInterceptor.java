package com.eugene.idempotent.interceptor;

import com.eugene.idempotent.annotation.Idempotent;
import com.eugene.idempotent.util.IdempotentUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class IdempotentInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;

    public IdempotentInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            Idempotent idempotentAnnotation = method.getAnnotation(Idempotent.class);
            if (idempotentAnnotation != null) {
                String key = idempotentAnnotation.key();
                long expireTime = idempotentAnnotation.expireTime();

                if (key.isEmpty()) {
                    key = request.getRequestURI();
                }

                boolean isIdempotent = IdempotentUtils.checkIdempotent(stringRedisTemplate, key, expireTime);
                if (!isIdempotent) {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    return false;
                }
            }
        }
        return true;
    }
}