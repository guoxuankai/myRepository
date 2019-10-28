package com.rondaful.cloud.user.utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import jodd.util.URLDecoder;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.rondaful.cloud.user.entity.SysLog;

public class Test {
	//@Autowired
    private MongoTemplate mongoTemplate;


    public static void main(String[] args) throws IOException {
        InputStream fileReader = new FileInputStream(new File("C:\\Users\\Administrator\\Desktop\\new01.txt"));
        InputStreamReader in = new InputStreamReader(fileReader,"UTF8");
        BufferedReader bu = new BufferedReader(in);
        StringBuilder str = new StringBuilder();
        String index = null;
        while (null != (index = bu.readLine())) str.append(index);
        String new01 = URLDecoder.decode(str.toString(),"UTF-8");
        System.out.println(str);
    }

    //@GetMapping("/common/test")
    public void test(SysLog log, Integer page, Integer row){
    	SysLog sysLog = new SysLog();
    	//查询条件
    	
//        cb.setId(1L);
//        cb.setName("nametest");
//        cb.setAge(12);
        //mongoTemplate.save(cb,"test1");
        //Object list = mongoTemplate.findAll(Log.class,"test1");


        // 条件
        Criteria criteria1 = Criteria.where("name").is("nametest");
        Query query = new Query();
        query.addCriteria(criteria1);
        /*if (user.getUserName() != null) {
            query.addCriteria(criteria1);
        }
        if (user.getPassword() != null) {
            query.addCriteria(criteria2);
        }*/

        // 数量
        long total = mongoTemplate.count(null, SysLog.class);
        long size = mongoTemplate.count(query, SysLog.class);

        // 分页
        query.skip((page - 1) * row).limit(row);
//        List<Log> data = mongoTemplate.find(query, Log.class);
        Map<String, Object> map = new HashMap<String, Object>();

//        map.put("data", data);
        map.put("total", total);
        map.put("size", size);




    } 
}
