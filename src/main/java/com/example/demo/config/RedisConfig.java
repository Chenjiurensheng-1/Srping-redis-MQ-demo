package com.example.demo.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.example.demo.subscribe.MessageReceiveOne;
import com.example.demo.subscribe.MessageReceiveTwo;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * redis通用配置文件
 * @author 程就人生
 * @date 2020年2月24日
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisConfig{
    
	/**
	 * RedisTemplate模板bean的初始化
	 * @param factory
	 * @return
	 */
    @SuppressWarnings({ "rawtypes", "unchecked"})
    @Bean("redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory factory) {
        
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        
        template.setConnectionFactory(factory);
        
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        
        ObjectMapper om = new ObjectMapper();
        
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        
        jackson2JsonRedisSerializer.setObjectMapper(om);
        
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        
        template.afterPropertiesSet();
         
        return template;
    }
    
    /**
     * redis消息监听器容器
     * 可以添加多个监听不同话题的redis监听器，只需要把消息监听器和相应的消息订阅处理器绑定，该消息监听器
     * 通过反射技术调用消息订阅处理器的相关方法进行一些业务处理
     * @param connectionFactory
     * @param listenerAdapter
     * @return
     */
	//MessageListenerAdapter 表示监听频道的不同订阅者
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    RedisMessageListenerContainer container(LettuceConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter2,MessageListenerAdapter listenerAdapter){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        //订阅一个频道
        container.addMessageListener(listenerAdapter, new PatternTopic("one"));
        //订阅多个频道
        container.addMessageListener(listenerAdapter2, new PatternTopic("one"));
        container.addMessageListener(listenerAdapter2, new PatternTopic("two"));

        //序列化对象（特别注意：发布的时候需要设置序列化；订阅方也需要设置序列化）
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        
        ObjectMapper objectMapper = new ObjectMapper();
        
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        
        container.setTopicSerializer(jackson2JsonRedisSerializer);
        
        return container;
    }
    
    /**
     * 过期时间监听
     * @param factory
     * @return
     */
    @Bean
    public RedisMessageListenerContainer container2(LettuceConnectionFactory factory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(new MessageListener(){
			@Override
			public void onMessage(Message message, byte[] pattern) {
				System.out.println(message.toString());
				System.out.println(new String(pattern));
			}
        	
        }, new PatternTopic("__keyevent@0__:expired"));//__key*__:*
        return container;
    }

    /**
     * 第一个监听频道
     * @param receiver
     * @return
     */
    @Bean
    public MessageListenerAdapter listenerAdapter(MessageReceiveOne receiver){
        //这个地方 是给messageListenerAdapter 传入一个消息接受的处理器，利用反射的方法调用“MessageReceiveOne ”
        return new MessageListenerAdapter(receiver, "getMessage");
    }    
    
    /**
     * 第二个监听频道
     * @param receiver
     * @return
     */
    @Bean
    public MessageListenerAdapter listenerAdapter2(MessageReceiveTwo receiver){
        return new MessageListenerAdapter(receiver, "getMessage");
    }    
}