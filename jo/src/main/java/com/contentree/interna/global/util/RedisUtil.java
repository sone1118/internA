package com.contentree.interna.global.util;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisUtil {

	private final StringRedisTemplate stringRedisTemplate;

    public String getData(String key){
    	log.info("RedisUtil > getData - 호출 (가져올 데이터의 키 : {}", key);
        ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    public void setDataWithExpire(String key, String value, Integer duration){
    	log.info("RedisUtil > setDataWithExpire - 호출 (저장 데이터 - key : {}, value : {}, duration :{}", key, value, duration);
        ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
        Duration expireDuration = Duration.ofMillis(duration);
        valueOperations.set(key, value, expireDuration);
    }

    public void deleteData(String key){
    	log.info("RedisUtil > deleteData - 호출 (삭제 데이터의 키 : {}", key);
        stringRedisTemplate.delete(key);
    }

}
