package com.example.demo.subscribe;

import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 第一个消费者
 * @author 程就人生
 * @Date
 */
@Component
public class MessageReceiveOne {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getMessage(String object){
		
		//序列化对象，发布、订阅序列号保持一致
		Jackson2JsonRedisSerializer seria = new Jackson2JsonRedisSerializer(User.class);
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		
		objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		
		seria.setObjectMapper(objectMapper);
		
	    User user = (User)seria.deserialize(object.getBytes());
	    
	    System.out.println("消息客户端1号："+ user.toString());
	}
}