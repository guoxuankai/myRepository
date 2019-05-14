package com.example.demo.core;



public class SqlSessionFactory {

    static {
        SqlMappersHolder inst = SqlMappersHolder.INSTANCE;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public SqlSession openSession() {
        return new SqlSession();
    }
}
