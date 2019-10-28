package com.rondaful.cloud.user.utils;

import org.springframework.stereotype.Component;

@Component
public class ThreadLocalUtis{
	
	private static  ThreadLocal connThreadLocal = new ThreadLocal();
	
	   //让构造函数为 private，这样该类就不会被实例化
	   private ThreadLocalUtis(){}
	 
	   //获取唯一可用的对象
	   public static ThreadLocal  getThreadLocalInstance(){
		   if(connThreadLocal == null) {
			   return new ThreadLocal();
		   }
	      return connThreadLocal;
	   }
	   
	   public static Object get() {
		   return connThreadLocal.get();
	   }
	   
		public void setConnThreadLocal(ThreadLocal connThreadLocal) {
			this.connThreadLocal = connThreadLocal;
		}



}
