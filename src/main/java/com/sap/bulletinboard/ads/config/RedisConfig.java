package com.sap.bulletinboard.ads.config;

import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 60) // to expire the data after 60 seconds, default 30 minutes
@Profile("cloud")
public class RedisConfig extends AbstractCloudConfig {
    @Bean
    public RedisConnectionFactory redisFactory() {
        return connectionFactory().redisConnectionFactory();
    }
}