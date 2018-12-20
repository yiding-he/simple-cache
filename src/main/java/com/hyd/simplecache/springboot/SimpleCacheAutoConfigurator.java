package com.hyd.simplecache.springboot;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SimpleCacheAutoConfiguration.class)
@Conditional(AutoConfigCondition.class)
public class SimpleCacheAutoConfigurator {

    @Bean
    Caches simpleCacheFactory(SimpleCacheAutoConfiguration configuration) {
        return new Caches(configuration);
    }
}
