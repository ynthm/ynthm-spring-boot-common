package com.ynthm.autoconfigure.cache.limiter.config;

import com.ynthm.autoconfigure.cache.limiter.util.RedisUtil;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @author Ethan Wang
 */
@Configuration
@EnableCaching
@AutoConfigureBefore(RedisAutoConfiguration.class)
public class RedisConfig extends CachingConfigurerSupport {

  @Bean
  @Override
  public KeyGenerator keyGenerator() {
    return (target, method, params) -> method.getName();
  }

  @Bean
  public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory factory) {
    RedisTemplate<Object, Object> template = new RedisTemplate<>();
    // 如果你要自定义自己的序列化器 必须配置 ObjectMapper.setDefaultTyping 不然会在序列化报错
    // java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to XXX
    // 不要改变全局 ObjectMapper 的 defaultTyping  不然 Controller 层接受参数反序列化会有问题
    template.setConnectionFactory(factory);
    template.setKeySerializer(RedisSerializer.string());
    template.setValueSerializer(RedisSerializer.json());
    template.setHashKeySerializer(RedisSerializer.string());
    template.setHashValueSerializer(RedisSerializer.json());
    // 使上面参数生效
    template.afterPropertiesSet();
    return template;
  }

  /**
   * 配置一个限流脚本的 Bean
   * Bean的名称默认为方法名 "rateLimitScript"
   */
  @Bean
  public DefaultRedisScript<Long> rateLimitScript() {
    DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
    // 设置脚本来源：文件放在resources/lua目录下
    redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/limit_multi_rules.lua")));
    // 设置 Lua 脚本执行后的返回值类型
    redisScript.setResultType(Long.class);
    return redisScript;
  }

  @Bean
  public RedisUtil redisUtil(RedisTemplate<Object, Object> redisTemplate) {
    return new RedisUtil(redisTemplate);
  }
}
