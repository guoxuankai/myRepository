package com.example.demo;

import com.example.demo.core.SqlSession;
import com.example.demo.core.SqlSessionFactory;
import com.example.demo.dao.UserDao;



public class TestClient {

    public static void main(String[] args) {

        SqlSessionFactory factory = new SqlSessionFactory();
        SqlSession sqlSession = factory.openSession();
        //生成代理实现类
        UserDao userDao = sqlSession.getMapper(UserDao.class);

        System.out.println(userDao.getUserInfo(3));
    }
}
