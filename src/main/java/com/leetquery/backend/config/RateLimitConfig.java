package com.leetquery.backend.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate Limiting Configuration using Bucket4j
 * Implements token bucket algorithm for rate limiting
 */
@Configuration
public class RateLimitConfig {
    
    /**
     * In-memory bucket cache for rate limiting
     * In production, consider using distributed cache (Redis)
     */
    private static final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    
    /**
     * Get or create a bucket for a given key (typically IP address or user ID)
     * Default: 100 requests per minute
     */
    public static Bucket resolveBucket(String key) {
        return cache.computeIfAbsent(key, k -> createNewBucket());
    }
    
    /**
     * Create a new bucket with default limits
     * 100 tokens per minute (refill at rate of 100 tokens per 60 seconds)
     */
    private static Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }
    
    /**
     * Create a bucket with custom limits
     */
    public static Bucket createBucket(int tokens, Duration refillPeriod) {
        Bandwidth limit = Bandwidth.classic(tokens, Refill.intervally(tokens, refillPeriod));
        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }
    
    /**
     * Default rate limit: 1000 requests per hour
     */
    public static final int DEFAULT_REQUESTS_PER_HOUR = 1000;
    
    /**
     * Strict rate limit for sensitive endpoints: 10 requests per minute
     */
    public static final int STRICT_REQUESTS_PER_MINUTE = 10;
    
    /**
     * Query execution rate limit: 50 requests per minute
     */
    public static final int QUERY_REQUESTS_PER_MINUTE = 50;
}
