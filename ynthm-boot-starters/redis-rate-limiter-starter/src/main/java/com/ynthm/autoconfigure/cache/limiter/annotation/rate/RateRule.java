package com.ynthm.autoconfigure.cache.limiter.annotation.rate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 限流规则注解
 *
 * @author Ethan Wang
 * @version 1.0
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateRule {
  /**
   * 时间窗口内允许的请求次数
   *
   * @return
   */
  long count();

  /**
   * 时间窗口的长度
   *
   * @return
   */
  long time();

  /**
   * 时间窗口时间单位
   *
   * @return
   */
  TimeUnit timeUnit();
}
