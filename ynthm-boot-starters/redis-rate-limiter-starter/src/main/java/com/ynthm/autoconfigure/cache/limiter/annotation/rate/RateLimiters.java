package com.ynthm.autoconfigure.cache.limiter.annotation.rate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 外层容器注解，允许在方法上添加多个@RateLimiter
 * @author Ethan Wang
 * @version 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimiters {
  RateLimiter[] value();
}