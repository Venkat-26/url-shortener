package com.personal.url_shortener.cache;


import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
    	return new StringRedisTemplate(factory);
    }
    
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
    	RedisCacheConfiguration defaultConfig = RedisCacheConfiguration
    			.defaultCacheConfig()
    			.entryTtl(Duration.ofHours(24))
    			.disableCachingNullValues()
    			.serializeKeysWith(
    					RedisSerializationContext.SerializationPair
    					.fromSerializer(new StringRedisSerializer()))
    			.serializeValuesWith(
    					RedisSerializationContext.SerializationPair
    					.fromSerializer(RedisSerializer.java()));
    	
    	Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
    	cacheConfigs.put("urls", defaultConfig.entryTtl(Duration.ofHours(24)));
    	cacheConfigs.put("users", defaultConfig.entryTtl(Duration.ofHours(24)));
    	
    	return RedisCacheManager.builder(factory)
    			.cacheDefaults(defaultConfig)
    			.withInitialCacheConfigurations(cacheConfigs)
    			.build();
    	}
}