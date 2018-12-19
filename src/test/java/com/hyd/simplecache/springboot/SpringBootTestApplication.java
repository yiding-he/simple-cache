package com.hyd.simplecache.springboot;

import com.hyd.simplecache.SimpleCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class SpringBootTestApplication {

    @Autowired
    private SimpleCacheFactory simpleCacheFactory;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootTestApplication.class, args);
    }

    @Bean
    CommandLineRunner init() {
        return args -> {
            SimpleCache cache;

            cache = this.simpleCacheFactory.getSimpleCache("local");
            cache.put("mail", "yiding.he@gmail.com");
            System.out.println("mail: " + cache.get("mail"));

            cache = this.simpleCacheFactory.getSimpleCache("caffeine1");
            cache.put("mail", "yiding.he@gmail.com");
            System.out.println("mail: " + cache.get("mail"));

            cache = this.simpleCacheFactory.getSimpleCache("ehcache1");
            cache.put("mail", "yiding.he@gmail.com");
            System.out.println("mail: " + cache.get("mail"));
        };
    }
}