package com.hyd.simplecache.springboot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

@Configuration
@EnableConfigurationProperties(SimpleCacheAutoConfiguration.class)
@Conditional(AutoConfigCondition.class)
public class SimpleCacheAutoConfigurator {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleCacheAutoConfigurator.class);

    @Bean
    public SimpleCacheFactory simpleCacheFactory(
            SimpleCacheAutoConfiguration configuration
    ) {
        System.out.println("*** SimpleCacheAutoConfigurator is working ***");
        return new SimpleCacheFactory(configuration);
    }

    @Bean
    static BeanDefinitionRegistryPostProcessor
    cacheRegister(final ConfigurableEnvironment environment) {
        return new CacheRegister(environment);
    }
}
