package com.ynthm.autoconfigure.cache.limiter.annotation.rate;

/**
 * 定义限流规则依据的不同维度
 *
 * @author Ethan Wang
 * @version 1.0
 */
public enum LimitType {
  /**
   * 自定义 Key
   */
  DEFAULT,
  /**
   * 用户 ID
   */
  USER_ID,
  /**
   * 客户端 IP 地址
   */
  IP,
  /**
   * 方法名称
   */
  METHOD_NAME
}
