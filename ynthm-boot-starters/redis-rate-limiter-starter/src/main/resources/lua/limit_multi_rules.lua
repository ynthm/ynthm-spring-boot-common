local key = KEYS[1] -- 限流键，如 'rate_limit:192.168.1.1:MyService:myMethod'
local uniqueValue = KEYS[2] -- 唯一值，防止并发下时间戳冲突，如UUID或雪花算法ID
local currentTime = tonumber(KEYS[3]) -- 当前时间戳（毫秒）

local maxExpireTime = 0 -- 记录最长的窗口时间，用于设置Key的过期时间

-- 遍历所有规则（参数ARGV以count, time, count, time...交替传入）
for i = 1, #ARGV, 2 do
    local limitCount = tonumber(ARGV[i])
    local windowTime = tonumber(ARGV[i + 1])

    -- 关键修复：检查转换后的值是否为nil
    if limitCount == nil then
        return redis.error_reply("ILLEGAL_ARGV: ARGV[" .. i ..
                                     "] is not a number: " .. tostring(ARGV[i]))
    end
    if windowTime == nil then
        return redis.error_reply("ILLEGAL_ARGV: ARGV[" .. (i + 1) ..
                                     "] is not a number: " ..
                                     tostring(ARGV[i + 1]))
    end

    -- 计算当前规则窗口的开始时间戳
    local windowStart = currentTime - windowTime
    -- 统计该时间窗口内的请求数量
    local count = redis.call('ZCOUNT', key, windowStart, currentTime)

    -- 如果某个规则的请求数已超限，直接返回限流标识（如1）
    if count >= limitCount then return 1 end

    -- 更新最大过期时间
    if windowTime > maxExpireTime then maxExpireTime = windowTime end
end

-- 所有规则均未超限，记录本次请求
redis.call('ZADD', key, currentTime, uniqueValue)
-- 设置Key的过期时间，避免无用数据长期存储
if maxExpireTime > 0 then redis.call('PEXPIRE', key, maxExpireTime) end
-- 返回不限流标识（如0）
return 0