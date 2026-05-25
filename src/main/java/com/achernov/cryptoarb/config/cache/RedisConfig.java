package com.achernov.cryptoarb.config.cache;

import com.achernov.cryptoarb.constants.CacheNames;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

@Configuration
public class RedisConfig {

  @Bean
  public StringRedisTemplate redisTemplate(RedisConnectionFactory connectionFactory) {
    return new StringRedisTemplate(connectionFactory);
  }

  @Bean
  public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

    RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .disableCachingNullValues()
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()));

    RedisCacheConfiguration metadataConfig = defaultConfig.entryTtl(Duration.ofHours(2));
    RedisCacheConfiguration cryptoRatesConfig = defaultConfig.entryTtl(Duration.ofMinutes(1));

    return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaultConfig)
            .withInitialCacheConfigurations(Map.of(
                    CacheNames.SYSTEM_METADATA, metadataConfig,
                    "crypto-rates", cryptoRatesConfig
            ))
            .build();
  }
}
