package com.ynthm.autoconfigure.cache.limiter.util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.*;

/**
 * @author Ethan Wang
 */
public class RedisUtil {
  /**
   * 默认存活时间2天
   */
  private static final long DEFAULT_EXPIRE_TIME = 60L * 60 * 48;

  private final RedisTemplate<Object, Object> redisTemplate;

  public RedisUtil(RedisTemplate<Object, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  /**
   * 得到key 过期时间
   */
  public long getExpireSeconds(Object key) {
    return redisTemplate.getExpire(key, TimeUnit.SECONDS);
  }

  /**
   * 设置key 的过期时间
   *
   * @param key
   * @param timeout
   */
  public boolean expireSeconds(Object key, long timeout) {
    return redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
  }

  /**
   * 删除key
   *
   * @param key
   */
  public void delete(Object key) {
    redisTemplate.delete(key);
  }

  /**
   * 字段值增加 并返回值
   *
   * @param key
   * @param number
   * @return
   */
  public Long incrBy(String key, long number) {
    return (Long)
            redisTemplate.execute(
                    (RedisCallback) connection -> connection.incrBy(key.getBytes(), number));
  }

  /**
   * 字段值减少 并返回值
   *
   * @param key
   * @param number
   * @return
   */
  public Long decrBy(String key, long number) {
    return (Long)
            redisTemplate.execute(
                    (RedisCallback) connection -> connection.decrBy(key.getBytes(), number));
  }

  /***********      String 类型操作           **************/
  /**
   * 添加String ,设置过期时间
   */
  public void put(Object key, Object value, long timeout, TimeUnit unit) {
    redisTemplate.boundValueOps(key).set(value);
    redisTemplate.expire(key, timeout, unit);
  }

  /**
   * 获取String 中 value
   *
   * @param key
   * @return
   */
  public Object get(String key) {
    return redisTemplate.boundValueOps(key).get();
  }

  /***********      list 类型操作           **************/

  /**
   * 在队列中插入一条数据
   *
   * @param key
   * @param value
   */
  public void lRightPush(String key, Object value) {
    ListOperations<Object, Object> operations = redisTemplate.opsForList();
    // 从队列右插入
    operations.rightPush(key, value);
  }

  public void lLeftPush(String key, Object value) {
    ListOperations<Object, Object> operations = redisTemplate.opsForList();
    // 从队列左插入
    operations.leftPush(key, value);
  }

  /**
   * 在队列中取出一条数据
   *
   * @param key
   * @return
   */
  public Object lLeftPop(String key) {
    // 从队列左边取出
    return redisTemplate.opsForList().leftPop(key);
  }

  public Object lRightPop(String key) {
    // 从队列右边取出
    return redisTemplate.opsForList().rightPop(key);
  }

  /**
   * 在队列中取出所有数据并不删除
   */
  public List<Object> lRange(String key) {
    return redisTemplate.opsForList().range(key, 0, -1);
  }

  /***********      hash 类型操作           **************/
  /**
   * 在hash中插入一条数据
   *
   * @param key
   * @param field
   * @param value
   * @param <K>
   * @param <V>
   */
  public <K, V> void hPut(String key, K field, V value) {
    HashOperations<Object, K, V> hashOperations = redisTemplate.opsForHash();
    hashOperations.put(key, field, value);
  }

  public <K, V> void hPutAll(String key, Map<? extends K, ? extends V> map) {
    HashOperations<Object, K, V> hashOperations = redisTemplate.opsForHash();
    hashOperations.putAll(key, map);
  }

  /**
   * 判断 hk 是否存在
   *
   * @param key
   * @param hKey
   * @param <K>
   * @return
   */
  public <K> boolean hHasKey(String key, K hKey) {
    return redisTemplate.opsForHash().hasKey(key, hKey);
  }

  /**
   * 取出所有 values
   *
   * @param key
   * @return
   */
  public List<Object> hHashValues(String key) {
    return redisTemplate.opsForHash().values(key);
  }


  public <K, V> Map<K, V> hEntries(Object key) {
    return (Map<K, V>) redisTemplate.opsForHash().entries(key);
  }

  /**
   * 取出所有 hks
   *
   * @param key
   * @param <K>
   * @return
   */
  public <K> Set<K> hKeys(String key) {
    return (Set<K>) redisTemplate.opsForHash().keys(key);
  }

  /**
   * 删除相关的 field
   *
   * @param key
   * @param hashKeys
   * @return
   */
  public Long hDel(String key, Object... hashKeys) {
    return redisTemplate.opsForHash().delete(key, hashKeys);
  }

  /***********      set 类型操作           **************/
  /**
   * 在set中插入一条数据
   *
   * @param key
   * @param value
   */
  public void sAdd(String key, Object... value) {
    redisTemplate.opsForSet().add(key, value);
  }

  /**
   * 得到所有 values
   *
   * @param key
   * @return
   */
  public Set<Object> sMembers(String key) {
    return redisTemplate.opsForSet().members(key);
  }

  /**
   * 删除相关value
   *
   * @param key
   * @param value
   * @return
   */
  public Long sDel(String key, Object... value) {
    SetOperations<Object, Object> setOperations = redisTemplate.opsForSet();
    return setOperations.remove(key, value);
  }

  /***********      zset 类型操作           **************/
  /**
   * 在Zset中插入一条数据
   *
   * @param key
   * @param value
   * @param score
   */
  public void zAdd(String key, Object value, long score) {
    ZSetOperations<Object, Object> zSetOperations = redisTemplate.opsForZSet();
    zSetOperations.add(key, value, score);
  }

  /**
   * 得到分数为 score1，score2的值
   *
   * @param key
   * @param score1
   * @param score2
   * @return
   */
  public Set<Object> zRange(String key, long score1, long score2) {
    ZSetOperations<Object, Object> zSetOperations = redisTemplate.opsForZSet();
    return zSetOperations.range(key, score1, score2);
  }

  /**
   * 删除相关value
   *
   * @param key
   * @param value
   * @return
   */
  public Long zDel(String key, Object... value) {
    ZSetOperations<Object, Object> zSetOperations = redisTemplate.opsForZSet();
    return zSetOperations.remove(key, value);
  }

  /**
   * 删除 值排序内的元素
   *
   * @param key
   * @param value1
   * @param value2
   * @return
   */
  public Long zDelRange(String key, long value1, long value2) {
    ZSetOperations<Object, Object> zSetOperations = redisTemplate.opsForZSet();
    return zSetOperations.removeRange(key, value1, value2);
  }

  /**
   * 删除分数内 元素
   *
   * @param key
   * @param value1
   * @param value2
   * @return
   */
  public Long zDelByScore(String key, long value1, long value2) {
    ZSetOperations<Object, Object> zSetOperations = redisTemplate.opsForZSet();
    return zSetOperations.removeRangeByScore(key, value1, value2);
  }
}
