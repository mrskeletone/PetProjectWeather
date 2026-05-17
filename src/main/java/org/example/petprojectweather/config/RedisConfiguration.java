package org.example.petprojectweather.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.json.JsonMapper;
import org.example.petprojectweather.dto.WeatherCity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

@Configuration
class RedisConfiguration {
    @Bean
    public RedisTemplate<String, WeatherCity> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, WeatherCity> template = new RedisTemplate<>();
//        JsonMapper jsonMapper = JsonMapper.builder().
//                activateDefaultTyping(
//                        BasicPolymorphicTypeValidator.builder()
//                                .allowIfSubType(Object.class)
//                                .build(),
//                        DefaultTyping.NON_FINAL,
//                        JsonTypeInfo.As.PROPERTY
//                ).build();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
    @Bean
    public ApplicationRunner cleanAllRedis(RedisConnectionFactory connectionFactory) {
        return args -> {
            connectionFactory.getConnection().flushDb();  // очищает текущую БД
            System.out.println("✅ Redis cache flushed");
        };
    }
}
