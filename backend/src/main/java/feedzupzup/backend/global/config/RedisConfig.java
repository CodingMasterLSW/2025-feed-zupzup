package feedzupzup.backend.global.config;

import feedzupzup.backend.sse.infrastructure.RedisSseSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(final RedisConnectionFactory connectionFactory) {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public ChannelTopic feedbackCountTopic() {
        return new ChannelTopic("sse:feedback-count");
    }

    @Bean
    public MessageListenerAdapter feedbackCountListenerAdapter(final RedisSseSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "handleMessage");
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            final RedisConnectionFactory connectionFactory,
            final MessageListenerAdapter feedbackCountListenerAdapter,
            final ChannelTopic feedbackCountTopic
    ) {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(feedbackCountListenerAdapter, feedbackCountTopic);
        return container;
    }
}
