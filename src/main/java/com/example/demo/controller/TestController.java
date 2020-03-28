package com.example.demo.controller;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.subscribe.MessageReceiveThree;
import com.example.demo.subscribe.User;

/**
 * 测试程序
 * @author FengJuan
 * @Date
 */
@RestController
public class TestController {

	@Autowired
	private RedisTemplate<String,Object> redisTemplate;
	
	@Autowired
	private LettuceConnectionFactory factory;
	
	/**
	 * 在配置文件里监听两个事件
	 */
	@GetMapping("/index")
	public void index(){
		String channel1 = "one";
		String channel2 = "two";
	    User user = new User();
	    user.setPhone("18988888888");
	    user.setName("张三");

	    User user2 = new User();
	    user2.setPhone("18988888889");
	    user2.setName("李四");

	    //发布消息
	    redisTemplate.convertAndSend(channel1,user2);	    
	    redisTemplate.convertAndSend(channel2,user);	    
	}
	
	
	/**
	 * 不再配置文件里的监听
	 */
	@GetMapping("/index1")
	public void index1(){
		//随时监听
		factory.getConnection().subscribe(new MessageReceiveThree(), "three".getBytes());
		
		User user2 = new User();
	    user2.setPhone("18988888889");
	    user2.setName("王五");
	    
		factory.getConnection().publish("three".getBytes(), user2.toString().getBytes());
	}
	
	
	/**
	 * 过期事件的监听
	 */
	@GetMapping("/index2")
	public void index2(){
		
		redisTemplate.opsForHash().put("aaakey", "bbbb", "cccc");
		//设置该key五秒后过期
		redisTemplate.expire("aaakey", 5, TimeUnit.SECONDS);
	}
}
