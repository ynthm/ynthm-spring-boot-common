# 分布式缓存限流


```java
    @RateLimiters({
            @RateLimiter(
                    limitType = LimitType.IP,
                    rules = {@RateRule(count = 1, time = 1, timeUnit = TimeUnit.MINUTES)} // 1 分钟内最多 1 次
            ),
            @RateLimiter(
                    limitType = LimitType.IP,
                    rules = {@RateRule(count = 100, time = 1, timeUnit = TimeUnit.DAYS)} // 1 天内最多 100 次
            )
    })
    public void abc(){}
```