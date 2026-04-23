package com.senyehui.registration.quota;

import com.senyehui.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 名额管理服务
 * 使用 Redis 缓存名额信息，支持原子扣减和回滚
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuotaService {

    private final StringRedisTemplate redisTemplate;

    private static final String QUOTA_KEY_PREFIX = "syh:quota:event:";
    private static final String QUOTA_GROUP_KEY_PREFIX = "syh:quota:group:";
    private static final long QUOTA_CACHE_TTL_HOURS = 72; // 缓存 3 天

    /**
     * 初始化赛事名额到 Redis
     */
    public void initEventQuota(Long eventId, int maxParticipants) {
        if (maxParticipants <= 0) {
            return; // 0 表示不限制名额
        }
        String key = QUOTA_KEY_PREFIX + eventId;
        redisTemplate.opsForValue().set(key, String.valueOf(maxParticipants), QUOTA_CACHE_TTL_HOURS, TimeUnit.HOURS);
        log.info("初始化赛事名额缓存: eventId={}, max={}", eventId, maxParticipants);
    }

    /**
     * 初始化组别名额到 Redis
     */
    public void initGroupQuota(Long groupId, int maxParticipants) {
        if (maxParticipants <= 0) {
            return;
        }
        String key = QUOTA_GROUP_KEY_PREFIX + groupId;
        redisTemplate.opsForValue().set(key, String.valueOf(maxParticipants), QUOTA_CACHE_TTL_HOURS, TimeUnit.HOURS);
        log.info("初始化组别名额缓存: groupId={}, max={}", groupId, maxParticipants);
    }

    /**
     * 原子扣减赛事名额
     *
     * @param eventId         赛事 ID
     * @param maxParticipants 最大名额（DB 兜底值）
     * @return 扣减后的剩余名额
     */
    public long deductEventQuota(Long eventId, int maxParticipants) {
        if (maxParticipants <= 0) {
            return Long.MAX_VALUE; // 不限名额
        }
        String key = QUOTA_KEY_PREFIX + eventId;
        // 如果缓存不存在，先初始化
        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            initEventQuota(eventId, maxParticipants);
        }
        Long remaining = redisTemplate.opsForValue().decrement(key);
        if (remaining == null || remaining < 0) {
            // 名额已满，回滚
            redisTemplate.opsForValue().increment(key);
            throw new BusinessException("报名名额已满");
        }
        log.info("扣减赛事名额: eventId={}, remaining={}", eventId, remaining);
        return remaining;
    }

    /**
     * 原子扣减组别名额
     */
    public long deductGroupQuota(Long groupId, int maxParticipants) {
        if (maxParticipants <= 0) {
            return Long.MAX_VALUE;
        }
        String key = QUOTA_GROUP_KEY_PREFIX + groupId;
        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            initGroupQuota(groupId, maxParticipants);
        }
        Long remaining = redisTemplate.opsForValue().decrement(key);
        if (remaining == null || remaining < 0) {
            redisTemplate.opsForValue().increment(key);
            throw new BusinessException("该组别报名名额已满");
        }
        log.info("扣减组别名额: groupId={}, remaining={}", groupId, remaining);
        return remaining;
    }

    /**
     * 回滚赛事名额（取消报名时使用）
     */
    public void rollbackEventQuota(Long eventId) {
        String key = QUOTA_KEY_PREFIX + eventId;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.opsForValue().increment(key);
            log.info("回滚赛事名额: eventId={}", eventId);
        }
    }

    /**
     * 回滚组别名额
     */
    public void rollbackGroupQuota(Long groupId) {
        String key = QUOTA_GROUP_KEY_PREFIX + groupId;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.opsForValue().increment(key);
            log.info("回滚组别名额: groupId={}", groupId);
        }
    }

    /**
     * 查询赛事剩余名额
     */
    public int getRemainingEventQuota(Long eventId) {
        String key = QUOTA_KEY_PREFIX + eventId;
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Integer.parseInt(value) : -1; // -1 表示未缓存
    }
}
