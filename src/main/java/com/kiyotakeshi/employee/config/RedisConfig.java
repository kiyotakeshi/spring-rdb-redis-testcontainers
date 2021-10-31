package com.kiyotakeshi.employee.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.time.Duration;

import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

@Configuration
@EnableCaching
public class RedisConfig {

//	@Bean
//	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
//		RedisTemplate<String, Object> template = new RedisTemplate<>();
//		template.setConnectionFactory(connectionFactory);
//
//		template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
//		template.setKeySerializer(new StringRedisSerializer());
//		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//		return template;
//	}

	// @ref https://www.baeldung.com/spring-boot-redis-cache#configuration
	@Bean
	public RedisCacheConfiguration cacheConfiguration() {
		return RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofMinutes(60))
				.disableCachingNullValues()
				// @see https://spring.pleiades.io/spring-data/redis/docs/current/api/org/springframework/data/redis/cache/RedisCacheConfiguration.html#serializeValuesWith-org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair-
				.serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
	}
}
