package com.ynthm.autoconfigure.cache.limiter.support.aspect;

import com.ynthm.autoconfigure.cache.limiter.annotation.rate.LimitType;
import com.ynthm.autoconfigure.cache.limiter.annotation.rate.RateLimiter;
import com.ynthm.autoconfigure.cache.limiter.annotation.rate.RateLimiters;
import com.ynthm.autoconfigure.cache.limiter.annotation.rate.RateRule;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

/**
 * 限流注解拦截切面
 *
 * @author Ethan Wang
 * @version 1.0
 */
@Aspect
@Component
public class RateLimitAspect {

  private final StringRedisTemplate stringRedisTemplate;

  /**
   * 加载上述Lua脚本
   */
  private final RedisScript<Long> rateLimitScript;

  public RateLimitAspect(StringRedisTemplate stringRedisTemplate, RedisScript<Long> rateLimitScript) {
    this.stringRedisTemplate = stringRedisTemplate;
    this.rateLimitScript = rateLimitScript;
  }

  @Around("@annotation(rateLimiters)")
  public Object checkRateLimit(ProceedingJoinPoint joinPoint, RateLimiters rateLimiters) throws Throwable {
    // 遍历容器注解中的每一个@RateLimiter规则
    for (RateLimiter rateLimiter : rateLimiters.value()) {
      // 1. 为当前限流规则生成唯一的Redis Key（关键步骤）
      String limitKey = generateLimitKey(joinPoint, rateLimiter);

      // 2. 准备Lua脚本参数
      List<String> keys = Arrays.asList(limitKey, generateUniqueValue(), String.valueOf(System.currentTimeMillis()));
      List<Object> args = new ArrayList<>();
      for (RateRule rule : rateLimiter.rules()) {
        // 将规则参数（次数、时间窗口毫秒数）交替加入args
        args.add(String.valueOf(rule.count()));
        args.add(String.valueOf(rule.timeUnit().toMillis(rule.time())));
      }

      // 3. 执行Lua脚本
      Long result = stringRedisTemplate.execute(rateLimitScript, keys, args.toArray());

      // 4. 判断是否触发限流
      if (result != null && result == 1L) {
        throw new RuntimeException("请求过于频繁，请稍后再试");
      }
    }
    // 所有限流规则均通过，则放行请求
    return joinPoint.proceed();
  }


  /**
   * 生成限流Key的核心方法
   * 根据不同的LimitType（如IP、用户ID）生成不同的Key前缀
   */
  private String generateLimitKey(ProceedingJoinPoint joinPoint, RateLimiter rateLimiter) {
    StringBuilder keyBuilder = new StringBuilder(rateLimiter.keyPrefix());
    LimitType limitType = rateLimiter.limitType();
    keyBuilder.append(limitType.name().toLowerCase() + ":");
    // 4. 根据不同的限流类型，构建键的不同部分
    String dimensionValue = "";
    switch (limitType) {
      case IP:
        // 获取客户端真实IP（考虑了反向代理的情况）
//        dimensionValue = ServletUtils.getClientIpAddress();
        break;
      case USER_ID:
        // 从会话、Token或安全上下文中获取当前用户ID
//        dimensionValue = SecurityContextHolder.optUserId().map(Objects::toString).orElse("anonymous");
        break;
      case DEFAULT:
      default:
        dimensionValue = "global"; // 全局限流，不区分维度
    }

    //  获取方法签名和基本信息
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String className = signature.getDeclaringType().getSimpleName(); // 获取目标类名
    String methodName = signature.getName(); // 获取目标方法名

    // 拼接最终的限流键，格式示例：rate_limit:ip:192.168.1.1:UserController:login
    // 使用冒号分隔不同部分，清晰易读，也符合Redis的键名最佳实践
    return String.format("%s:%s:%s:%s:%s", rateLimiter.keyPrefix(), limitType.name().toLowerCase(), dimensionValue, className, methodName);
  }

  private String generateUniqueValue() {
    return UUID.randomUUID().toString();
  }
}
