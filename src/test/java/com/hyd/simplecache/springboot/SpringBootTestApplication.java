package com.hyd.simplecache.springboot;

import com.hyd.simplecache.SimpleCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBootTestApplication {

    @Autowired
    private Caches caches;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootTestApplication.class, args);
    }

    @Bean
    SimpleCache caffeineCache() {
        return caches.get("caffeine1");
    }

    @Bean
    CommandLineRunner init(SimpleCache caffeineCache) {
        return args -> {
            System.out.println("Bean 'caffeineCache' is " + caffeineCache);

            SimpleCache cache;

            cache = caches.get("local");
            cache.put("mail", "yiding.he@gmail.com");
            System.out.println("mail: " + cache.get("mail"));

            cache = caches.get("caffeine1");
            cache.put("mail", "yiding.he@gmail.com");
            System.out.println("mail: " + cache.get("mail"));

            cache = caches.get("ehcache1");
            cache.put("mail", "yiding.he@gmail.com");
            System.out.println("mail: " + cache.get("mail"));
        };
    }
}
