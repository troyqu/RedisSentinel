package com.troy.redis.sentinel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootApplication
public class RedisSentinelApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisSentinelApplication.class, args);
    }

}
