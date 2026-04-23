package com.senyehui.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.senyehui.common.constant.Constants;

/**
 * JWT Token 工具类
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final StringRedisTemplate redisTemplate;

    public JwtTokenProvider(
            @Value("${syh.jwt.secret}") String secret,
            @Value("${syh.jwt.access-token-expiration:7200000}") long accessTokenExpiration,
            @Value("${syh.jwt.refresh-token-expiration:604800000}") long refreshTokenExpiration,
            StringRedisTemplate redisTemplate) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 生成 Access Token
     */
    public String createAccessToken(Long userId, String username, Long tenantId) {
        return createToken(userId, username, tenantId, accessTokenExpiration, "access");
    }

    /**
     * 生成 Refresh Token
     */
    public String createRefreshToken(Long userId, String username, Long tenantId) {
        return createToken(userId, username, tenantId, refreshTokenExpiration, "refresh");
    }

    private String createToken(Long userId, String username, Long tenantId, long expiration, String type) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("tenantId", tenantId)
                .claim("type", type)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * 从 Token 中获取用户 ID
     */
    public Long getUserId(String token) {
        return Long.parseLong(getClaims(token).getSubject());
    }

    /**
     * 从 Token 中获取用户名
     */
    public String getUsername(String token) {
        return getClaims(token).get("username", String.class);
    }

    /**
     * 从 Token 中获取租户 ID
     */
    public Long getTenantId(String token) {
        return getClaims(token).get("tenantId", Long.class);
    }

    /**
     * 验证 Token 是否有效
     */
    public boolean validateToken(String token) {
        try {
            // 检查是否在黑名单中
            if (isBlacklisted(token)) {
                log.debug("Token 已被加入黑名单");
                return false;
            }
            getClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("Token 已过期");
        } catch (JwtException e) {
            log.debug("Token 无效: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 将 Token 加入黑名单（登出时）
     */
    public void blacklistToken(String token) {
        try {
            Claims claims = getClaims(token);
            long ttl = claims.getExpiration().getTime() - System.currentTimeMillis();
            if (ttl > 0) {
                redisTemplate.opsForValue().set(
                        Constants.CACHE_TOKEN_BLACKLIST + token,
                        "1",
                        ttl,
                        TimeUnit.MILLISECONDS
                );
            }
        } catch (Exception e) {
            log.warn("Token 黑名单处理失败: {}", e.getMessage());
        }
    }

    private boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(Constants.CACHE_TOKEN_BLACKLIST + token));
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
