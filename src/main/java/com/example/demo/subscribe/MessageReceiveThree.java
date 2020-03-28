package com.example.demo.subscribe;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

/**
 * 第三个消费者
 * @author 程就人生
 * @Date
 */
public class MessageReceiveThree implements MessageListener{
	
	@Override
	public void onMessage(Message message, byte[] pattern) {
		System.out.println("消息客户端3号："+ message.toString());
	}
}