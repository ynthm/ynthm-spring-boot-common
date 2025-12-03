package com.ynthm.autoconfigure.cache.limiter.annotation.rate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限流注解
 *
 * @author Ethan Wang
 * @version 1.0
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimiter {
  /**
   * 限流类型，如IP、用户ID等
   *
   * @return
   */
  LimitType limitType() default LimitType.IP;

  /**
   * 定义具体的限流规则数组
   *
   * @return
   */
  RateRule[] rules();

  /**
   * 限流键的前缀，可基于用户、IP或接口
   */
  String keyPrefix() default "rate_limit:";
}