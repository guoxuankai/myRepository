package org.lpl.demo.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.lpl.demo.App;
import org.lpl.demo.bean.User;
import org.lpl.demo.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
public class RedisTest {

	@Autowired
	private RedisUtil redisUtil;
		
	@Test
	public void singleTest(){	
		redisUtil.set("a", "zhangsan");
		System.out.println(redisUtil.get("a"));
		
	}
	/**
	 * 单机版测试
	* @Title: singleTestUser 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param 
	* @return void
	* @autor lpl
	* @date 2018年2月24日
	* @throws
	 */
	@Test
	public void singleTestUser(){

		List<Map<String,Object>> mList = new ArrayList<Map<String,Object>>();
		for(int i = 0;i<10;i++){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("mapId", i);
			mList.add(map);
		}
		
		User user = new User(15231L,"zhangsan","李四",15,1,"15217411234","392926441@qq.com",
				"1520","三年级一班","私立一中","河北省","石家庄","张三的呵呵呵呵呵","内容还是很好的饿哦",
				"120000","/user/hhh/s.jpg","5","java","英语","zhangsan","111",mList);
		redisUtil.set("user1", user);
	}
	
}
