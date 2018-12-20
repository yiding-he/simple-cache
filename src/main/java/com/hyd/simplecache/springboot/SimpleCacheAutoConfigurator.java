package com.hyd.simplecache.springboot;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SimpleCacheAutoConfiguration.class)
public class SimpleCacheAutoConfigurator {

    @Bean
    Caches caches(SimpleCacheAutoConfiguration configuration) {
        return new Caches(configuration);
    }
}
