package com.baidu.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
	private static Configuration configuration;
	private static SessionFactory sessionFactory;
	
	static {
		//加载核心配置文件
		configuration = new Configuration().configure();
		//通过配置文件建立session工厂（session--connection）
		sessionFactory = configuration.buildSessionFactory();		
	}
	//从工厂拿到session,session是线程不安全的,在方法中使用
	public static Session getSession() {		
		return sessionFactory.openSession();
	}
	//获取当前绑定的session
	public static Session getCurrentSession() {		
		return sessionFactory.getCurrentSession();
	}
	//关闭资源
	public static void closeSession(Session session) {
		if (null!=session) {
			session.close();
		}
	}
}
