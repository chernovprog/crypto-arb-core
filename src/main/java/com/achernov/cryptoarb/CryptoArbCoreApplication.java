package com.achernov.cryptoarb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableJpaRepositories(basePackages = "com.achernov.cryptoarb.repository.jpa")
@EnableRedisRepositories(basePackages = "com.achernov.cryptoarb.repository.redis")
public class CryptoArbCoreApplication {
	public static void main(String[] args) {
		SpringApplication.run(CryptoArbCoreApplication.class, args);
	}
}
