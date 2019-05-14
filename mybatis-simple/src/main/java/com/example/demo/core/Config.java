package com.example.demo.core;


public class Config {

    public static final Config DEFAULT = new Config();

    private Config() {

    }

    private String url = "jdbc:mysql://119.23.31.13:3306/mybatis-simple";
    private String user = "root";
    private String pwd = "123456";

    private String mapperPath = "mapper/";

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPwd() {
        return pwd;
    }

    public String getMapperPath() {
        return mapperPath;
    }
}
