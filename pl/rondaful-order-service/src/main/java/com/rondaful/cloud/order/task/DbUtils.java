package com.rondaful.cloud.order.task;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DbUtils {
    private static Connection con;
    private static String driver;
    private static String url;
    private static String username;
    private static String password;

    static {
        try {
            readinfo();                 //读取配置文件
            Class.forName(driver);      //反射的方式注册驱动
            con = DriverManager.getConnection(url, username, password);         //获取数据库连接
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("数据库连接失败");          //如果数据库连接异常直接终止程序
        }
    }

    //获取连接方法
    public static Connection getConnection() {
        return con;
    }

    //释放资源
    public static void close(ResultSet rs, Statement ps, Connection con) {
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (con != null) {
                        con.close();
                        con = null;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //没有结果集释放资源
    public static void close(Statement ps, Connection con) {
        try {
            if (ps != null) {
                ps.close();
                ps = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                    con = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //读取配置文件
    private static void readinfo() throws IOException {
        //获取输入流 配置文件dbutils.properties放在工程src文件夹下
        InputStream is = DbUtils.class.getClassLoader().getResourceAsStream("dbutils.properties");
        //建立属性集合
        Properties pro = new Properties();
        //读取属性列表
        pro.load(is);
        //读取键所对应的值,并赋值
        driver = pro.getProperty("driver");
        url = pro.getProperty("url");
        username = pro.getProperty("username");
        password = pro.getProperty("password");
        //关闭流
        is.close();
    }
}